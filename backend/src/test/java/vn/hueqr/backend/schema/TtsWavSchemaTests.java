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
class TtsWavSchemaTests {
	private static final Timestamp NOW = Timestamp.from(Instant.parse("2026-11-09T00:00:00.123456Z"));

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void stagedJobRequiresReadyFileAndTimestampAndKeepsWavReferenced() {
		UUID job = insertJob(jdbc, "DISPATCHING");
		assertRejected(() -> jdbc.update("UPDATE tts_job SET phase = 'RESULT_STAGED' WHERE id = ?", bytes(job)), 3819);
		assertRejected(() -> jdbc.update("UPDATE tts_job SET phase = 'RESULT_STAGED', source_wav_file_id = ?, result_staged_at = ? WHERE id = ?",
				bytes(UUID.randomUUID()), NOW, bytes(job)), 1452);
		UUID file = file();
		jdbc.update("UPDATE tts_job SET phase = 'RESULT_STAGED', source_wav_file_id = ?, result_staged_at = ? WHERE id = ?", bytes(file), NOW, bytes(job));
		assertRejected(() -> jdbc.update("DELETE FROM file_asset WHERE id = ?", bytes(file)), 1451);
		assertThat(jdbc.queryForObject("SELECT state FROM file_asset WHERE id = ?", String.class, bytes(file))).isEqualTo("READY");
	}

	@Test
	void failedAndDiscardedHaveTerminalTimesButUncertainNeverGetsAnInventedTerminalTime() {
		UUID job = insertJob(jdbc, "DISPATCHING");
		assertRejected(() -> jdbc.update("UPDATE tts_job SET phase = 'FAILED' WHERE id = ?", bytes(job)), 3819);
		assertRejected(() -> jdbc.update("UPDATE tts_job SET phase = 'UNCERTAIN', terminal_at = ? WHERE id = ?", NOW, bytes(job)), 3819);
		jdbc.update("UPDATE tts_job SET phase = 'FAILED', failure_code = 'PROVIDER_QUOTA', terminal_at = ? WHERE id = ?", NOW, bytes(job));
		assertThat(jdbc.queryForObject("SELECT terminal_at FROM tts_job WHERE id = ?", Timestamp.class, bytes(job))).isEqualTo(NOW);
	}

	@Test
	void readyWavRequiresPositiveMetadataAndImmutableStorageKeyIsUnique() {
		UUID file = file();
		assertRejected(() -> jdbc.update("UPDATE file_asset SET byte_size = 0 WHERE id = ?", bytes(file)), 3819);
		assertRejected(() -> jdbc.update("UPDATE file_asset SET duration = 0 WHERE id = ?", bytes(file)), 3819);
		assertRejected(() -> jdbc.update("UPDATE file_asset SET format = 'FLAC' WHERE id = ?", bytes(file)), 3819);
		assertRejected(() -> jdbc.update("""
				INSERT INTO file_asset (id, storage_key, format, checksum, byte_size, duration, state, ready_at)
				SELECT ?, storage_key, format, checksum, byte_size, duration, state, ready_at FROM file_asset WHERE id = ?
				""", bytes(UUID.randomUUID()), bytes(file)), 1062);
	}

	@Test
	void upgradingV6KeepsQueuedDispatchingAndUnknownJobsOwnersVersionsAndChecksums() {
		try (var mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var source = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
			var legacy = new JdbcTemplate(source);
			Flyway.configure().dataSource(source).target("6").load().migrate();
			var checksums = legacy.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank");
			for (String phase : new String[]{"QUEUED", "DISPATCHING", "UNCERTAIN"})
				insertJob(legacy, phase);
			String columns = "id, phase, phase_version, worker_instance, worker_token, dispatched_at, accepted_at, updated_at";
			var oldRows = legacy.queryForList("SELECT " + columns + " FROM tts_job ORDER BY phase");
			var upgrade = Flyway.configure().dataSource(source).target("7").load();
			upgrade.migrate();
			upgrade.validate();
			assertThat(upgrade.info().current().getVersion().toString()).isEqualTo("7");
			assertThat(legacy.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IN ('1','2','3','4','5','6') ORDER BY installed_rank"))
					.isEqualTo(checksums);
			var current = legacy.queryForList("SELECT " + columns + " FROM tts_job ORDER BY phase");
			for (int i = 0; i < oldRows.size(); i++) {
				var before = oldRows.get(i);
				var after = current.get(i);
				for (String binary : new String[]{"id", "worker_instance", "worker_token"})
					assertThat((byte[]) after.remove(binary)).isEqualTo((byte[]) before.remove(binary));
				assertThat(after).isEqualTo(before);
			}
			assertThat(legacy.queryForObject("SELECT COUNT(*) FROM file_asset", Long.class)).isZero();
			assertThat(legacy.queryForObject("SELECT COUNT(*) FROM tts_job WHERE source_wav_file_id IS NOT NULL OR terminal_at IS NOT NULL", Long.class)).isZero();
		}
	}

	private UUID file() {
		UUID id = UUID.randomUUID();
		jdbc.update("INSERT INTO file_asset (id, storage_key, format, checksum, byte_size, duration, state, ready_at) VALUES (?, ?, 'WAV', ?, 48044, 1, 'READY', ?)",
				bytes(id), id + ".wav", new byte[32], NOW);
		return id;
	}

	private static UUID insertJob(JdbcTemplate sql, String phase) {
		UUID place = UUID.randomUUID();
		UUID slot = UUID.randomUUID();
		UUID text = UUID.randomUUID();
		UUID job = UUID.randomUUID();
		sql.update("INSERT INTO place (id, display_name, created_at, updated_at) VALUES (?, 'Fixture', ?, ?)", bytes(place), NOW, NOW);
		sql.update("INSERT INTO content_slot (id, place_id, language) VALUES (?, ?, 'vi')", bytes(slot), bytes(place));
		sql.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, 'Text', ?, ?)", bytes(text), bytes(slot), new byte[32], NOW);
		sql.update("""
				INSERT INTO tts_job (id, target_text_revision_id, place_key_snapshot, text_revision_key_snapshot,
				language_snapshot, text_hash_snapshot, model, voice, phase, admin_account_key, request_window_key,
				request_operation_key, accepted_at, updated_at) VALUES (?, ?, ?, ?, 'vi', ?, ?, 'Charon', 'QUEUED', ?, ?, ?, ?, ?)
				""", bytes(job), bytes(text), bytes(place), bytes(text), new byte[32], "gemini-3.8-flash-tts",
				bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), NOW, NOW);
		if (!phase.equals("QUEUED"))
			sql.update("UPDATE tts_job SET phase = ?, worker_instance = ?, worker_token = ?, dispatched_at = ?, phase_version = 3 WHERE id = ?",
					phase, bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), NOW, bytes(job));
		sql.update("UPDATE text_revision SET active_tts_job_id = ?, latest_tts_job_key = ? WHERE id = ?", bytes(job), bytes(job), bytes(text));
		return job;
	}

	private static void assertRejected(Runnable command, int code) {
		DataAccessException error = assertThrows(DataAccessException.class, command::run);
		assertThat(error.getMostSpecificCause()).isInstanceOf(SQLException.class);
		assertThat(((SQLException) error.getMostSpecificCause()).getErrorCode()).isEqualTo(code);
	}

	private static byte[] bytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
