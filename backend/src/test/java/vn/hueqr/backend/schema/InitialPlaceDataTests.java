package vn.hueqr.backend.schema;

import java.sql.Connection;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.mysql.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static vn.hueqr.backend.support.AudioDataFixture.bytes;
import static vn.hueqr.backend.support.AudioDataFixture.time;

class InitialPlaceDataTests {
	private static MySQLContainer mysql;

	private static DriverManagerDataSource dataSource;

	private JdbcTemplate jdbc;

	@BeforeAll
	static void startTemporaryDatabase() {
		mysql = new MySQLContainer("mysql:8.4.13");
		mysql.start();
		dataSource = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
	}

	@AfterAll
	static void stopTemporaryDatabase() {
		if (mysql != null)
			mysql.stop();
	}

	@BeforeEach
	void recreateSchema() {
		var flyway = Flyway.configure().dataSource(dataSource).cleanDisabled(false).load();
		flyway.clean();
		flyway.migrate();
		jdbc = new JdbcTemplate(dataSource);
	}

	@Test
	void schemaMigrationAloneNeverSeedsContentAndExplicitSeedCreatesFiveHiddenPlacesAndTwentyEmptySlots() throws Exception {
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place", Long.class)).isZero();
		seed();
		assertThat(jdbc.queryForList("SELECT display_name FROM place", String.class)).containsExactlyInAnyOrder(
				"Cổng Ngọ Môn", "Điện Thái Hòa", "Tử Cấm Thành", "Cửu Đỉnh", "Cung Diên Thọ");
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place WHERE visible = FALSE AND active_region_revision_id IS NULL", Long.class)).isEqualTo(5);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM content_slot WHERE draft_text_id IS NULL AND active_pair_id IS NULL", Long.class)).isEqualTo(20);
		for (String lang : new String[]{"vi", "en", "fr", "ru"})
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM content_slot WHERE language = ?", Long.class, lang)).isEqualTo(5);
		for (String table : new String[]{"region_revision", "text_revision", "audio_revision", "content_pair", "text_review_ref", "audio_review_ref", "file_asset", "tts_job", "admin_account"})
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class)).isZero();
	}

	@Test
	void repeatingSeedKeepsStableIdsNamesVersionsDraftsAndOriginalCreationTimes() throws Exception {
		seed();
		var ids = jdbc.queryForList("SELECT HEX(id) AS id, created_at FROM place ORDER BY id");
		byte[] place = jdbc.queryForObject("SELECT id FROM place WHERE display_name = 'Cổng Ngọ Môn'", byte[].class);
		byte[] slot = jdbc.queryForObject("SELECT id FROM content_slot WHERE place_id = ? AND language = 'vi'", byte[].class, place);
		UUID text = UUID.randomUUID();
		jdbc.update("UPDATE place SET display_name = 'Tên đã sửa', version = 7 WHERE id = ?", place);
		jdbc.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, 'Nháp đang giữ', ?, ?)", bytes(text), slot, new byte[32], time());
		jdbc.update("UPDATE content_slot SET draft_text_id = ?, version = 4 WHERE id = ?", bytes(text), slot);
		seed();
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place", Long.class)).isEqualTo(5);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM content_slot", Long.class)).isEqualTo(20);
		assertThat(jdbc.queryForList("SELECT HEX(id) AS id, created_at FROM place ORDER BY id")).isEqualTo(ids);
		assertThat(jdbc.queryForObject("SELECT display_name FROM place WHERE id = ?", String.class, place)).isEqualTo("Tên đã sửa");
		assertThat(jdbc.queryForObject("SELECT version FROM place WHERE id = ?", Long.class, place)).isEqualTo(7);
		assertThat(jdbc.queryForObject("SELECT draft_text_id FROM content_slot WHERE id = ?", byte[].class, slot)).isEqualTo(bytes(text));
		assertThat(jdbc.queryForObject("SELECT version FROM content_slot WHERE id = ?", Long.class, slot)).isEqualTo(4);
	}

	@Test
	void existingPlaceWithTheSameNameIsNotMergedOrRenamed() throws Exception {
		UUID existing = UUID.randomUUID();
		jdbc.update("INSERT INTO place (id, display_name, created_at, updated_at) VALUES (?, 'Cổng Ngọ Môn', ?, ?)", bytes(existing), time(), time());
		seed();
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place", Long.class)).isEqualTo(6);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place WHERE display_name = 'Cổng Ngọ Môn'", Long.class)).isEqualTo(2);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM content_slot WHERE place_id = ?", Long.class, bytes(existing))).isZero();
	}

	@Test
	void seedReusesAnExistingLanguageSlotInsteadOfChangingItsIdentity() throws Exception {
		seed();
		byte[] place = jdbc.queryForObject("SELECT id FROM place WHERE display_name = 'Cổng Ngọ Môn'", byte[].class);
		jdbc.update("DELETE FROM content_slot WHERE place_id = ? AND language = 'vi'", place);
		UUID replacement = UUID.randomUUID();
		jdbc.update("INSERT INTO content_slot (id, place_id, language, version) VALUES (?, ?, 'vi', 3)", bytes(replacement), place);
		seed();
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM content_slot", Long.class)).isEqualTo(20);
		assertThat(jdbc.queryForObject("SELECT id FROM content_slot WHERE place_id = ? AND language = 'vi'", byte[].class, place)).isEqualTo(bytes(replacement));
		assertThat(jdbc.queryForObject("SELECT version FROM content_slot WHERE id = ?", Long.class, bytes(replacement))).isEqualTo(3);
	}

	private void seed() throws Exception {
		try (Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new org.springframework.core.io.support.EncodedResource(new ClassPathResource("db/seed/five-places.sql"), java.nio.charset.StandardCharsets.UTF_8));
		}
	}
}
