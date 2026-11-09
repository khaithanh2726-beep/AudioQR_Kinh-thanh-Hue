package vn.hueqr.backend.schema;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.mysql.MySQLContainer;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class ContentSchemaTests {
	private static final Timestamp NOW = Timestamp.from(Instant.parse("2026-11-09T00:00:00.123456Z"));

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void eachPlaceHasAtMostOneSlotForEachOfTheFourLanguages() {
		UUID place = place();
		slot(place, "vi");
		slot(place, "en");
		assertRejected(() -> slot(place, "vi"), 1062, "uk_content_slot_place_language");
		assertRejected(() -> slot(place, "VI"), 3819, "ck_content_slot_language");
		assertRejected(() -> slot(place, "de"), 3819, "ck_content_slot_language");
		assertRejected(() -> slot(UUID.randomUUID(), "fr"), 1452, "fk_content_slot_place");
	}

	@Test
	void draftPointerCannotSelectAnotherLanguageOrAnotherPlacesText() {
		UUID firstPlace = place();
		UUID vi = slot(firstPlace, "vi");
		UUID en = slot(firstPlace, "en");
		UUID other = slot(place(), "vi");
		UUID text = text(vi);
		select(vi, text);
		assertRejected(() -> select(en, text), 1452, "fk_content_slot_draft_text");
		assertRejected(() -> select(other, text), 1452, "fk_content_slot_draft_text");
		assertRejected(() -> select(vi, UUID.randomUUID()), 1452, "fk_content_slot_draft_text");
	}

	@Test
	void textRequiresASlotButDistinctVersionsMayHaveTheSameContentHash() {
		assertRejected(() -> text(UUID.randomUUID()), 1452, "fk_text_revision_slot");
		UUID slot = slot(place(), "fr");
		UUID first = text(slot);
		UUID second = text(slot);
		assertThat(first).isNotEqualTo(second);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM text_revision WHERE slot_id = ?", Long.class, bytes(slot))).isEqualTo(2L);
	}

	@Test
	void referencesRequireExactlyOneSavedTextAndAnExistingRecordingAccount() {
		UUID tid = text(slot(place(), "ru"));
		UUID account = account();
		assertRejected(() -> review(UUID.randomUUID(), account), 1452, "fk_text_review_ref_revision");
		assertRejected(() -> review(tid, UUID.randomUUID()), 1452, "fk_text_review_ref_account");
		review(tid, account);
		assertRejected(() -> review(tid, account), 1062, "PRIMARY");
		assertRejected(() -> jdbc.update("DELETE FROM admin_account WHERE id = ?", bytes(account)), 1451, "fk_text_review_ref_account");
	}

	@Test
	void restrictiveForeignKeysRequireExplicitOrderedCleanup() {
		UUID pid = place();
		UUID sid = slot(pid, "vi");
		UUID tid = text(sid);
		select(sid, tid);
		assertRejected(() -> jdbc.update("DELETE FROM place WHERE id = ?", bytes(pid)), 1451, "fk_content_slot_place");
		assertRejected(() -> jdbc.update("DELETE FROM content_slot WHERE id = ?", bytes(sid)), 1451, "fk_text_revision_slot");
		assertRejected(() -> jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(tid)), 1451, "fk_content_slot_draft_text");
		review(tid, account());
		jdbc.update("UPDATE content_slot SET draft_text_id = NULL WHERE id = ?", bytes(sid));
		assertRejected(() -> jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(tid)), 1451, "fk_text_review_ref_revision");
		jdbc.update("DELETE FROM text_review_ref WHERE text_revision_id = ?", bytes(tid));
		jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(tid));
		jdbc.update("DELETE FROM content_slot WHERE id = ?", bytes(sid));
		assertThat(jdbc.update("DELETE FROM place WHERE id = ?", bytes(pid))).isEqualTo(1);
	}

	@Test
	void upgradingV4PreservesExistingPlacesRegionsAccountsWindowsReceiptsAndChecksums() {
		// Database riêng ở V4 trước khi nâng, không dùng schema do Spring đã áp V5
		try (var mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var source = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
			var legacy = new JdbcTemplate(source);
			Flyway.configure().dataSource(source).target("4").load().migrate();
			var checksums = legacy.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank");
			UUID pid = UUID.randomUUID();
			UUID rid = UUID.randomUUID();
			UUID aid = UUID.randomUUID();
			UUID wid = UUID.randomUUID();
			UUID receipt = UUID.randomUUID();
			legacy.update("INSERT INTO place (id, display_name, version, created_at, updated_at) VALUES (?, ?, 7, ?, ?)",
					bytes(pid), "Địa điểm trước V5", NOW, NOW);
			legacy.update("INSERT INTO region_revision (id, place_id, polygon, map_space_key, geometry_hash, saved_at) VALUES (?, ?, ?, ?, ?, ?)",
					bytes(rid), bytes(pid), "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[0,1],[0,0]]]}", "WGS84_LON_LAT", new byte[32], NOW);
			legacy.update("UPDATE place SET active_region_revision_id = ? WHERE id = ?", bytes(rid), bytes(pid));
			legacy.update("INSERT INTO admin_account (id, login_name, password_hash) VALUES (?, ?, ?)", bytes(aid), "legacy-v4", "{noop}fixture");
			legacy.update("INSERT INTO admin_command_window (id, admin_account_id, service_epoch, issued_at, expires_at) VALUES (?, ?, ?, ?, ?)",
					bytes(wid), bytes(aid), "00000000-0000-0000-0000-000000000001", NOW, Timestamp.from(NOW.toInstant().plusSeconds(86400)));
			legacy.update("""
					INSERT INTO operation_receipt (id, window_id, admin_account_id, operation_key, fingerprint,
					operation_type, http_status, result_code, result_summary, committed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
					""", bytes(receipt), bytes(wid), bytes(aid), bytes(UUID.randomUUID()), new byte[32], "CREATE_PLACE", 201,
					"PLACE_CREATED", "{\"fixture\":true}", NOW);
			var original = legacy.queryForMap("SELECT display_name, visible, version, created_at, updated_at, active_region_revision_id FROM place WHERE id = ?", bytes(pid));
			var upgrade = Flyway.configure().dataSource(source).target("5").load();
			upgrade.migrate();
			upgrade.validate();
			assertThat(upgrade.info().current().getVersion().toString()).isEqualTo("5");
			assertThat(legacy.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IN ('1','2','3','4') ORDER BY installed_rank"))
					.isEqualTo(checksums);
			var retained = legacy.queryForMap("SELECT display_name, visible, version, created_at, updated_at, active_region_revision_id FROM place WHERE id = ?", bytes(pid));
			assertThat(retained.remove("active_region_revision_id")).isEqualTo(original.remove("active_region_revision_id"));
			assertThat(retained).isEqualTo(original);
			assertThat(legacy.queryForObject("SELECT result_summary FROM operation_receipt WHERE id = ?", String.class, bytes(receipt))).contains("fixture");
			for (String table : new String[]{"region_revision", "admin_account", "admin_command_window", "operation_receipt"})
				assertThat(legacy.queryForObject("SELECT COUNT(*) FROM " + table, Long.class)).isEqualTo(1L);
			for (String table : new String[]{"content_slot", "text_revision", "text_review_ref"})
				assertThat(legacy.queryForObject("SELECT COUNT(*) FROM " + table, Long.class)).isZero();
		}
	}

	private UUID place() {
		UUID id = UUID.randomUUID();
		jdbc.update("INSERT INTO place (id, display_name, created_at, updated_at) VALUES (?, ?, ?, ?)", bytes(id), "Fixture", NOW, NOW);
		return id;
	}

	private UUID account() {
		UUID id = UUID.randomUUID();
		jdbc.update("INSERT INTO admin_account (id, login_name, password_hash) VALUES (?, ?, ?)", bytes(id), id.toString(), "{noop}fixture");
		return id;
	}

	private UUID slot(UUID place, String language) {
		UUID id = UUID.randomUUID();
		jdbc.update("INSERT INTO content_slot (id, place_id, language) VALUES (?, ?, ?)", bytes(id), bytes(place), language);
		return id;
	}

	private UUID text(UUID slot) {
		UUID id = UUID.randomUUID();
		jdbc.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, ?, ?, ?)", bytes(id), bytes(slot), "Văn bản", new byte[32], NOW);
		return id;
	}

	private void select(UUID slot, UUID text) {
		jdbc.update("UPDATE content_slot SET draft_text_id = ? WHERE id = ?", bytes(text), bytes(slot));
	}

	private void review(UUID text, UUID account) {
		jdbc.update("INSERT INTO text_review_ref (text_revision_id, locator, recorded_by, recorded_at) VALUES (?, ?, ?, ?)", bytes(text), "Hồ sơ fixture", bytes(account), NOW);
	}

	private static void assertRejected(Runnable command, int code, String constraint) {
		DataAccessException error = assertThrows(DataAccessException.class, command::run);
		SQLException cause = (SQLException) error.getMostSpecificCause();
		assertThat(cause.getErrorCode()).isEqualTo(code);
		assertThat(cause.getMessage()).contains(constraint);
	}

	private static byte[] bytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
