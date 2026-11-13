package vn.hueqr.backend.schema;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.mysql.MySQLContainer;
import vn.hueqr.backend.support.AudioDataFixture;
import static org.assertj.core.api.Assertions.*;
import static vn.hueqr.backend.repository.LifecycleRepository.bytes;

class LocalAudioMigrationTests {
	@Test
	void upgradingV11PreservesTheStagedJobPointersMetadataAndOldChecksums() {
		try (var mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var data = new DriverManagerDataSource(mysql.getJdbcUrl(),mysql.getUsername(),mysql.getPassword());
			var sql = new JdbcTemplate(data);
			Flyway.configure().dataSource(data).target("11").load().migrate();
			var source = new AudioDataFixture(sql).insertStagedWav("fr");
			var checksums = sql.queryForList("SELECT version, checksum FROM flyway_schema_history ORDER BY installed_rank");
			var before = sql.queryForMap("SELECT phase, phase_version, language_snapshot, model, voice, accepted_at, updated_at FROM tts_job WHERE id=?",bytes(source.job()));
			var upgrade = Flyway.configure().dataSource(data).target("12").load();
			upgrade.migrate();
			upgrade.validate();
			assertThat(sql.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version <> '12' ORDER BY installed_rank")).isEqualTo(checksums);
			assertThat(sql.queryForMap("SELECT phase, phase_version, language_snapshot, model, voice, accepted_at, updated_at FROM tts_job WHERE id=?",bytes(source.job()))).isEqualTo(before);
			assertThat(sql.queryForObject("SELECT source_wav_file_id FROM tts_job WHERE id=?",byte[].class,bytes(source.job()))).isEqualTo(bytes(source.wav()));
			assertThat(sql.queryForObject("SELECT active_tts_job_id FROM text_revision WHERE id=?",byte[].class,bytes(source.text()))).isEqualTo(bytes(source.job()));
			assertThat(sql.queryForObject("SELECT conversion_task FROM tts_job WHERE id=?",byte[].class,bytes(source.job()))).isNull();
			assertThat(sql.queryForObject("SELECT COUNT(*) FROM audio_revision",Long.class)).isZero();
		}
	}
}
