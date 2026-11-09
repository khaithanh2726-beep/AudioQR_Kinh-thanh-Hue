package vn.hueqr.backend.schema;

import java.sql.SQLException;
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
import vn.hueqr.backend.support.AudioDataFixture;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.*;
import static vn.hueqr.backend.support.AudioDataFixture.bytes;
import static vn.hueqr.backend.support.AudioDataFixture.time;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class GuestContentSchemaTests {
	@Autowired
	JdbcTemplate sql;


	@Test
	void upgradingV9KeepsOldSessionsAndChecksumsWithoutInventingOpeningsOrViews() {
		try (var mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var ds = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
			var db = new JdbcTemplate(ds);
			Flyway.configure().dataSource(ds).target("9").load().migrate();
			UUID id = session(db);
			var before = db.queryForList("SELECT HEX(id), display_name, initial_language, started_at, expires_at, HEX(credential_hash) FROM guest_session");
			var checksums = db.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank");
			Flyway upgrade = Flyway.configure().dataSource(ds).target("10").load();
			upgrade.migrate();
			upgrade.validate();
			assertThat(db.queryForList("SELECT HEX(id), display_name, initial_language, started_at, expires_at, HEX(credential_hash) FROM guest_session")).isEqualTo(before);
			assertThat(db.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE CAST(version AS UNSIGNED) <= 9 ORDER BY installed_rank")).isEqualTo(checksums);
			assertThat(db.queryForObject("SELECT current_language FROM guest_session WHERE id = ?", String.class, bytes(id))).isEqualTo("vi");
			for (String table : new String[]{"guest_open_intent", "guest_content_grant", "guest_request_result", "file_claim", "place_view"})
				assertThat(db.queryForObject("SELECT COUNT(*) FROM " + table, Long.class)).isZero();
		}
	}

	@Test
	void currentOpeningCannotBelongToAnotherSession() {
		UUID first = session(sql);
		UUID second = session(sql);
		UUID opening = opening(first);
		assertRejected(() -> sql.update("UPDATE guest_session SET current_opening_id = ? WHERE id = ?", bytes(opening), bytes(second)), 1452, "fk_guest_current_opening");
		sql.update("UPDATE guest_session SET current_opening_id = ? WHERE id = ?", bytes(opening), bytes(first));
		assertRejected(() -> sql.update("DELETE FROM guest_open_intent WHERE id = ?", bytes(opening)), 1451, "fk_guest_current_opening");
	}

	@Test
	void grantCannotMixOpeningAndSessionOrMakeRestoreViewEligible() {
		UUID first = session(sql);
		UUID second = session(sql);
		UUID opening = opening(first);
		assertRejected(() -> grant(second, opening, "SELECT", true), 1452, "fk_guest_grant_opening");
		assertRejected(() -> grant(first, opening, "RESTORE", true), 3819, "ck_guest_grant_view");
		grant(first, opening, "RESTORE", false);
		assertThat(sql.queryForObject("SELECT COUNT(*) FROM guest_content_grant WHERE opening_id = ?", Long.class, bytes(opening))).isEqualTo(1);
	}

	@Test
	void requestKeyAndDocumentSequenceAreBothUniqueWithinTheSession() {
		UUID sid = session(sql);
		UUID nonce = UUID.randomUUID();
		UUID key = UUID.randomUUID();
		Runnable insert = () -> request(sid, nonce, key, 1);
		insert.run();
		assertRejected(insert, 1062, "PRIMARY");
		assertRejected(() -> request(sid, nonce, UUID.randomUUID(), 1), 1062, "uk_guest_request_selection");
		assertRejected(() -> request(sid, nonce, UUID.randomUUID(), 0), 3819, "ck_guest_request_seq");
		request(sid, UUID.randomUUID(), UUID.randomUUID(), 1);
	}

	@Test
	void readClaimPreventsFileDeletionUntilItIsExplicitlyReleased() {
		UUID id = UUID.randomUUID();
		new AudioDataFixture(sql).insertFile(id, "MP3");
		UUID claim = UUID.randomUUID();
		sql.update("INSERT INTO file_claim (id, file_id, owner_key, kind, created_at) VALUES (?, ?, ?, 'READ', ?)", bytes(claim), bytes(id), bytes(UUID.randomUUID()), time());
		assertRejected(() -> sql.update("DELETE FROM file_asset WHERE id = ?", bytes(id)), 1451, "fk_file_claim_file");
		sql.update("DELETE FROM file_claim WHERE id = ?", bytes(claim));
		assertThat(sql.update("DELETE FROM file_asset WHERE id = ?", bytes(id))).isEqualTo(1);
	}

	private static UUID session(JdbcTemplate db) {
		UUID id = UUID.randomUUID();
		db.update("""
				INSERT INTO guest_session (id, start_request_key, credential_hash, service_epoch, start_fingerprint,
				start_ticket_issued_at, start_ticket_expires_at, start_ticket_digest, display_name, initial_language, started_at, expires_at)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Schema fixture', 'vi', ?, ?)
				""", bytes(id), bytes(UUID.randomUUID()), java.util.HexFormat.of().parseHex(id.toString().replace("-", "").repeat(2)),
				UUID.randomUUID().toString(), new byte[32], time(), java.sql.Timestamp.from(AudioDataFixture.NOW.plusSeconds(86400)), new byte[32],
				time(), java.sql.Timestamp.from(AudioDataFixture.NOW.plusSeconds(28800)));
		return id;
	}

	private UUID opening(UUID sid) {
		UUID id = UUID.randomUUID();
		sql.update("INSERT INTO guest_open_intent (id, session_id, place_key_snapshot, place_name_snapshot, current_language, created_at) VALUES (?, ?, ?, 'Schema fixture', 'vi', ?)",
				bytes(id), bytes(sid), bytes(UUID.randomUUID()), time());
		return id;
	}

	private void grant(UUID sid, UUID opening, String source, boolean eligible) {
		sql.update("""
				INSERT INTO guest_content_grant (id, session_id, opening_id, place_key_snapshot, place_name_snapshot, language,
				text_revision_key, text_hash, pair_key, audio_revision_key, source, view_eligible, available_languages, issued_at, expires_at)
				VALUES (?, ?, ?, ?, 'Schema fixture', 'vi', ?, ?, ?, ?, ?, ?, 'vi', ?, ?)
				""", bytes(UUID.randomUUID()), bytes(sid), bytes(opening), bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), new byte[32],
				bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), source, eligible, time(), java.sql.Timestamp.from(AudioDataFixture.NOW.plusSeconds(28800)));
	}

	private void request(UUID sid, UUID nonce, UUID key, long seq) {
		sql.update("""
				INSERT INTO guest_request_result (session_id, request_key, document_nonce, selection_seq, fingerprint, action,
				opening_key, opening_version, selection_kind, language, available_languages)
				VALUES (?, ?, ?, ?, ?, 'SELECT', ?, 0, 'NEW_OPENING', 'vi', 'vi')
				""", bytes(sid), bytes(key), bytes(nonce), seq, new byte[32], bytes(UUID.randomUUID()));
	}

	private static void assertRejected(Runnable action, int code, String constraint) {
		var error = catchThrowableOfType(DataAccessException.class, action::run);
		assertThat(error.getMostSpecificCause()).isInstanceOf(SQLException.class);
		assertThat(((SQLException) error.getMostSpecificCause()).getErrorCode()).isEqualTo(code);
		assertThat(error.getMostSpecificCause().getMessage()).contains(constraint);
	}
}
