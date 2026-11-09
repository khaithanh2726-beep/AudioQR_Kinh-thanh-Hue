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
class TtsJobSchemaTests {
	private static final Timestamp NOW = Timestamp.from(Instant.parse("2026-11-09T00:00:00.123456Z"));

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void activePointerRequiresAJobForExactlyTheSameText() {
		UUID first = text();
		UUID second = text();
		UUID job = job(first, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
		jdbc.update("UPDATE text_revision SET active_tts_job_id = ?, latest_tts_job_key = ? WHERE id = ?", bytes(job), bytes(job), bytes(first));
		assertRejected(() -> jdbc.update("UPDATE text_revision SET active_tts_job_id = ? WHERE id = ?", bytes(job), bytes(second)),
				1452, "fk_text_active_tts_job");
		assertRejected(() -> jdbc.update("UPDATE text_revision SET active_tts_job_id = ? WHERE id = ?", bytes(UUID.randomUUID()), bytes(second)),
				1452, "fk_text_active_tts_job");
	}

	@Test
	void jobNeedsALiveTargetAtAcceptanceAndRestrictsDeletingEitherSideOfTheActiveReference() {
		assertRejected(() -> job(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()), 1452, "fk_tts_job_text");
		UUID text = text();
		UUID job = job(text, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
		jdbc.update("UPDATE text_revision SET active_tts_job_id = ? WHERE id = ?", bytes(job), bytes(text));
		assertRejected(() -> jdbc.update("DELETE FROM tts_job WHERE id = ?", bytes(job)), 1451, "fk_text_active_tts_job");
		assertRejected(() -> jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(text)), 1451, "fk_tts_job_text");
		jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL WHERE id = ?", bytes(text));
		jdbc.update("UPDATE tts_job SET target_valid = FALSE, target_text_revision_id = NULL WHERE id = ?", bytes(job));
		assertThat(jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(text))).isEqualTo(1);
		assertThat(jdbc.queryForObject("SELECT text_revision_key_snapshot FROM tts_job WHERE id = ?", byte[].class, bytes(job))).isEqualTo(bytes(text));
	}

	@Test
	void firstAcceptanceKeyIsUniqueWhileAccountAndWindowKeysRemainLogicalSnapshots() {
		UUID text = text();
		UUID account = UUID.randomUUID();
		UUID window = UUID.randomUUID();
		UUID operation = UUID.randomUUID();
		job(text, account, window, operation);
		assertRejected(() -> job(text, account, window, operation), 1062, "uk_tts_job_request");
		assertThat(jdbc.queryForObject("""
				SELECT COUNT(*) FROM information_schema.REFERENTIAL_CONSTRAINTS
				WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'tts_job'
				AND REFERENCED_TABLE_NAME IN ('admin_account', 'admin_command_window')
				""", Long.class)).isZero();
	}

	@Test
	void dispatchMarkerRequiresOwnershipAndCannotPretendANewQueuedJobWasAlreadySent() {
		UUID job = job(text(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
		assertRejected(() -> jdbc.update("UPDATE tts_job SET phase = 'DISPATCHING' WHERE id = ?", bytes(job)), 3819, "ck_tts_job_dispatch");
		assertRejected(() -> jdbc.update("UPDATE tts_job SET phase = 'UNCERTAIN' WHERE id = ?", bytes(job)), 3819, "ck_tts_job_dispatch");
		assertRejected(() -> jdbc.update("UPDATE tts_job SET worker_token = ? WHERE id = ?", bytes(UUID.randomUUID()), bytes(job)), 3819, "ck_tts_job_dispatch");
		jdbc.update("UPDATE tts_job SET phase = 'DISPATCHING', worker_instance = ?, worker_token = ?, dispatched_at = ? WHERE id = ?",
				bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), NOW, bytes(job));
		jdbc.update("UPDATE tts_job SET phase = 'UNCERTAIN' WHERE id = ?", bytes(job));
		assertThat(jdbc.queryForObject("SELECT phase FROM tts_job WHERE id = ?", String.class, bytes(job))).isEqualTo("UNCERTAIN");
	}

	@Test
	void queueProfileRejectsInvalidLanguageUnsupportedPhasesAndValidFlagWithoutTarget() {
		UUID job = job(text(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
		assertRejected(() -> jdbc.update("UPDATE tts_job SET language_snapshot = 'VI' WHERE id = ?", bytes(job)), 3819, "ck_tts_job_language");
		// Phase ngoài ba chặng vi phạm cả hai CHECK, không phụ thuộc thứ tự MySQL chọn báo lỗi
		assertRejected(() -> jdbc.update("UPDATE tts_job SET phase = 'SUCCEEDED' WHERE id = ?", bytes(job)), 3819,
				"ck_tts_job_phase", "ck_tts_job_dispatch");
		assertRejected(() -> jdbc.update("UPDATE tts_job SET target_text_revision_id = NULL WHERE id = ?", bytes(job)), 3819, "ck_tts_job_target");
	}

	@Test
	void upgradingV5PreservesSavedTextReviewDraftPointerAndAllExistingChecksums() {
		try (var mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var source = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
			var legacy = new JdbcTemplate(source);
			Flyway.configure().dataSource(source).target("5").load().migrate();
			var checksums = legacy.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank");
			UUID place = UUID.randomUUID();
			UUID slot = UUID.randomUUID();
			UUID text = UUID.randomUUID();
			UUID account = UUID.randomUUID();
			legacy.update("INSERT INTO place (id, display_name, created_at, updated_at) VALUES (?, ?, ?, ?)", bytes(place), "Điểm V5", NOW, NOW);
			legacy.update("INSERT INTO content_slot (id, place_id, language, version) VALUES (?, ?, 'vi', 9)", bytes(slot), bytes(place));
			legacy.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, ?, ?, ?)",
					bytes(text), bytes(slot), "Văn bản V5 😀", new byte[32], NOW);
			legacy.update("UPDATE content_slot SET draft_text_id = ? WHERE id = ?", bytes(text), bytes(slot));
			legacy.update("INSERT INTO admin_account (id, login_name, password_hash) VALUES (?, ?, ?)", bytes(account), "legacy-v5", "{noop}fixture");
			legacy.update("INSERT INTO text_review_ref (text_revision_id, locator, limitations, recorded_by, recorded_at, version) VALUES (?, ?, ?, ?, ?, 3)",
					bytes(text), "Hồ sơ V5", "Giới hạn V5", bytes(account), NOW);
			var upgrade = Flyway.configure().dataSource(source).target("6").load();
			upgrade.migrate();
			upgrade.validate();
			assertThat(upgrade.info().current().getVersion().toString()).isEqualTo("6");
			assertThat(legacy.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IN ('1','2','3','4','5') ORDER BY installed_rank"))
					.isEqualTo(checksums);
			assertThat(legacy.queryForObject("SELECT body FROM text_revision WHERE id = ?", String.class, bytes(text))).isEqualTo("Văn bản V5 😀");
			assertThat(legacy.queryForObject("SELECT content_hash FROM text_revision WHERE id = ?", byte[].class, bytes(text))).isEqualTo(new byte[32]);
			assertThat(legacy.queryForObject("SELECT saved_at FROM text_revision WHERE id = ?", Timestamp.class, bytes(text))).isEqualTo(NOW);
			assertThat(legacy.queryForObject("SELECT draft_text_id FROM content_slot WHERE id = ?", byte[].class, bytes(slot))).isEqualTo(bytes(text));
			assertThat(legacy.queryForObject("SELECT version FROM content_slot WHERE id = ?", Long.class, bytes(slot))).isEqualTo(9L);
			assertThat(legacy.queryForObject("SELECT locator FROM text_review_ref WHERE text_revision_id = ?", String.class, bytes(text))).isEqualTo("Hồ sơ V5");
			assertThat(legacy.queryForObject("SELECT version FROM text_review_ref WHERE text_revision_id = ?", Long.class, bytes(text))).isEqualTo(3L);
			assertThat(legacy.queryForObject("SELECT active_tts_job_id FROM text_revision WHERE id = ?", byte[].class, bytes(text))).isNull();
			assertThat(legacy.queryForObject("SELECT latest_tts_job_key FROM text_revision WHERE id = ?", byte[].class, bytes(text))).isNull();
			assertThat(legacy.queryForObject("SELECT COUNT(*) FROM tts_job", Long.class)).isZero();
		}
	}

	private UUID text() {
		UUID place = UUID.randomUUID();
		UUID slot = UUID.randomUUID();
		UUID text = UUID.randomUUID();
		jdbc.update("INSERT INTO place (id, display_name, created_at, updated_at) VALUES (?, ?, ?, ?)", bytes(place), "Fixture", NOW, NOW);
		jdbc.update("INSERT INTO content_slot (id, place_id, language) VALUES (?, ?, 'vi')", bytes(slot), bytes(place));
		jdbc.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, ?, ?, ?)", bytes(text), bytes(slot), "Text", new byte[32], NOW);
		return text;
	}

	private UUID job(UUID text, UUID account, UUID window, UUID operation) {
		UUID id = UUID.randomUUID();
		jdbc.update("""
				INSERT INTO tts_job (id, target_text_revision_id, place_key_snapshot, text_revision_key_snapshot,
				language_snapshot, text_hash_snapshot, model, voice, phase, admin_account_key, request_window_key,
				request_operation_key, accepted_at, updated_at) VALUES (?, ?, ?, ?, 'vi', ?, ?, ?, 'QUEUED', ?, ?, ?, ?, ?)
				""", bytes(id), bytes(text), bytes(UUID.randomUUID()), bytes(text), new byte[32], "gemini-3.8-flash-tts", "Charon",
				bytes(account), bytes(window), bytes(operation), NOW, NOW);
		return id;
	}

	private static void assertRejected(Runnable command, int code, String... constraints) {
		DataAccessException error = assertThrows(DataAccessException.class, command::run);
		SQLException cause = (SQLException) error.getMostSpecificCause();
		assertThat(cause.getErrorCode()).isEqualTo(code);
		assertThat(cause.getMessage()).containsAnyOf(constraints);
	}

	private static byte[] bytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
