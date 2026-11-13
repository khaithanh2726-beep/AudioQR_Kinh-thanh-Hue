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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static vn.hueqr.backend.support.AudioDataFixture.bytes;
import static vn.hueqr.backend.support.AudioDataFixture.time;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class AudioPairSchemaTests {
	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void matchingFilesAndRevisionsCreateOnePairWithoutApplyingOrApprovingIt() {
		var data = new AudioDataFixture(jdbc);
		var source = data.insertSource("vi");
		UUID audio = data.insertAudio(source);
		data.insertPair(source.slot(), source.text(), audio);
		assertRejected(() -> data.insertPair(source.slot(), source.text(), audio), 1062, "uk_content_pair_members");
		assertThat(jdbc.queryForObject("SELECT active_pair_id FROM content_slot WHERE id = ?", byte[].class, bytes(source.slot()))).isNull();
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?", Boolean.class, bytes(source.place()))).isFalse();
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM audio_review_ref", Long.class)).isZero();
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM text_review_ref", Long.class)).isZero();
		assertThat(jdbc.queryForObject("SELECT phase FROM tts_job WHERE id = ?", String.class, bytes(source.job()))).isEqualTo("RESULT_STAGED");
	}

	@Test
	void audioCannotUseAnotherTextsJobOrAnotherWavAndEachJobHasOnlyOneAudio() {
		var data = new AudioDataFixture(jdbc);
		var first = data.insertSource("vi");
		var second = data.insertSource("en");
		assertRejected(() -> data.insertAudio(UUID.randomUUID(), first.text(), second.job(), second.wav(), first.mp3()), 1452, "fk_audio_revision_job_text_wav");
		assertRejected(() -> data.insertAudio(UUID.randomUUID(), first.text(), first.job(), second.wav(), first.mp3()), 1452, "fk_audio_revision_job_text_wav");
		data.insertAudio(first);
		assertRejected(() -> data.insertAudio(first), 1062, "uk_audio_revision_job");
	}

	@Test
	void formatsCannotBeSwappedAndWavPointerStillRejectsMp3AfterTheFormatExpansion() {
		var data = new AudioDataFixture(jdbc);
		var source = data.insertSource("vi");
		assertRejected(() -> data.insertAudio(UUID.randomUUID(), source.text(), source.job(), source.wav(), source.wav()), 3819, "ck_audio_revision_distinct_files");
		UUID otherWav = UUID.randomUUID();
		data.insertFile(otherWav, "WAV");
		assertRejected(() -> data.insertAudio(UUID.randomUUID(), source.text(), source.job(), source.wav(), otherWav), 1452, "fk_audio_revision_mp3");
		assertRejected(() -> jdbc.update("UPDATE tts_job SET source_wav_file_id = ? WHERE id = ?", bytes(source.mp3()), bytes(source.job())), 1452, "fk_tts_job_source_wav");
		assertRejected(() -> jdbc.update("UPDATE tts_job SET source_wav_format = 'MP3' WHERE id = ?", bytes(source.job())), 3819, "ck_tts_job_source_format");
	}

	@Test
	void pairCannotMixLanguagesPlacesOrAudioFromAnotherText() {
		var data = new AudioDataFixture(jdbc);
		var first = data.insertSource("vi");
		var second = data.insertSource("en");
		var third = data.insertSource("vi");
		UUID audio = data.insertAudio(first);
		UUID otherAudio = data.insertAudio(second);
		assertRejected(() -> data.insertPair(second.slot(), first.text(), audio), 1452, "fk_content_pair_text_slot");
		assertRejected(() -> data.insertPair(third.slot(), first.text(), audio), 1452, "fk_content_pair_text_slot");
		assertRejected(() -> data.insertPair(first.slot(), first.text(), otherAudio), 1452, "fk_content_pair_audio_text");
	}

	@Test
	void activePointerCanOnlySelectAnExistingPairOfItsOwnSlot() {
		var data = new AudioDataFixture(jdbc);
		var first = data.insertSource("vi");
		var second = data.insertSource("fr");
		UUID pair = data.insertPair(first.slot(), first.text(), data.insertAudio(first));
		assertRejected(() -> jdbc.update("UPDATE content_slot SET active_pair_id = ? WHERE id = ?", bytes(pair), bytes(second.slot())), 1452, "fk_content_slot_active_pair");
		assertRejected(() -> jdbc.update("UPDATE content_slot SET active_pair_id = ? WHERE id = ?", bytes(UUID.randomUUID()), bytes(first.slot())), 1452, "fk_content_slot_active_pair");
		jdbc.update("UPDATE content_slot SET active_pair_id = ? WHERE id = ?", bytes(pair), bytes(first.slot()));
		assertThat(jdbc.queryForObject("SELECT draft_text_id FROM content_slot WHERE id = ?", byte[].class, bytes(first.slot()))).isEqualTo(bytes(first.text()));
		assertRejected(() -> jdbc.update("DELETE FROM content_pair WHERE id = ?", bytes(pair)), 1451, "fk_content_slot_active_pair");
	}

	@Test
	void liveRelationsRejectDirectDeletionOfAudioFilesTextAndJob() {
		var data = new AudioDataFixture(jdbc);
		var source = data.insertSource("ru");
		UUID audio = data.insertAudio(source);
		data.insertPair(source.slot(), source.text(), audio);
		assertRejected(() -> jdbc.update("DELETE FROM audio_revision WHERE id = ?", bytes(audio)), 1451, "fk_content_pair_audio_text");
		assertRejected(() -> jdbc.update("DELETE FROM file_asset WHERE id = ?", bytes(source.wav())), 1451, null);
		assertRejected(() -> jdbc.update("DELETE FROM file_asset WHERE id = ?", bytes(source.mp3())), 1451, "fk_audio_revision_mp3");
		assertRejected(() -> jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(source.text())), 1451, null);
		assertRejected(() -> jdbc.update("DELETE FROM tts_job WHERE id = ?", bytes(source.job())), 1451, null);
		assertThat(jdbc.queryForList("""
				SELECT DELETE_RULE FROM information_schema.REFERENTIAL_CONSTRAINTS
				WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME IN ('audio_revision', 'audio_review_ref', 'content_pair')
				""", String.class)).hasSize(9).containsOnly("RESTRICT");
	}

	@Test
	void audioReviewRequiresExistingAudioAndRecorderAndIsNotCopiedToAnotherAudio() {
		var data = new AudioDataFixture(jdbc);
		var source = data.insertSource("vi");
		UUID audio = data.insertAudio(source);
		UUID account = UUID.randomUUID();
		jdbc.update("INSERT INTO admin_account (id, login_name, password_hash, enabled) VALUES (?, ?, 'Fixture', TRUE)", bytes(account), account.toString());
		assertRejected(() -> insertReview(UUID.randomUUID(), account), 1452, "fk_audio_review_ref_revision");
		assertRejected(() -> insertReview(audio, UUID.randomUUID()), 1452, "fk_audio_review_ref_account");
		insertReview(audio, account);
		assertRejected(() -> insertReview(audio, account), 1062, "PRIMARY");
		assertRejected(() -> jdbc.update("DELETE FROM audio_revision WHERE id = ?", bytes(audio)), 1451, "fk_audio_review_ref_revision");
		UUID next = data.insertAudio(data.insertSource("vi"));
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM audio_review_ref WHERE audio_revision_id = ?", Long.class, bytes(next))).isZero();
	}

	@Test
	void explicitDeletionOrderRemovesPrivateDataWhileFilesRemainForLaterCleanup() {
		var data = new AudioDataFixture(jdbc);
		var source = data.insertSource("vi");
		UUID audio = data.insertAudio(source);
		UUID pair = data.insertPair(source.slot(), source.text(), audio);
		jdbc.update("UPDATE content_slot SET active_pair_id = ?, draft_text_id = NULL WHERE id = ?", bytes(pair), bytes(source.slot()));
		jdbc.update("UPDATE content_slot SET active_pair_id = NULL WHERE id = ?", bytes(source.slot()));
		jdbc.update("DELETE FROM content_pair WHERE id = ?", bytes(pair));
		jdbc.update("DELETE FROM audio_revision WHERE id = ?", bytes(audio));
		jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL WHERE id = ?", bytes(source.text()));
		jdbc.update("DELETE FROM tts_job WHERE id = ?", bytes(source.job()));
		jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(source.text()));
		jdbc.update("DELETE FROM content_slot WHERE id = ?", bytes(source.slot()));
		jdbc.update("DELETE FROM place WHERE id = ?", bytes(source.place()));
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM file_asset WHERE id IN (?, ?)", Long.class, bytes(source.wav()), bytes(source.mp3()))).isEqualTo(2);
	}

	@Test
	void twoAudioRevisionsCanShareFilesAndRemovingOneStillCannotDeleteReferencedFiles() {
		var data = new AudioDataFixture(jdbc);
		var first = data.insertSource("vi");
		var second = data.insertSource("vi");
		UUID firstAudio = data.insertAudio(first);
		jdbc.update("UPDATE tts_job SET source_wav_file_id = ? WHERE id = ?", bytes(first.wav()), bytes(second.job()));
		data.insertAudio(UUID.randomUUID(), second.text(), second.job(), first.wav(), first.mp3());
		jdbc.update("DELETE FROM audio_revision WHERE id = ?", bytes(firstAudio));
		assertRejected(() -> jdbc.update("DELETE FROM file_asset WHERE id = ?", bytes(first.mp3())), 1451, "fk_audio_revision_mp3");
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM audio_revision WHERE wav_file_id = ? AND mp3_file_id = ?", Long.class, bytes(first.wav()), bytes(first.mp3()))).isEqualTo(1);
	}

	@Test
	void upgradingV8PreservesExistingSourcesDraftsReviewsAndChecksumsWithoutInventingAudio() {
		try (var mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var ds = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
			var sql = new JdbcTemplate(ds);
			Flyway.configure().dataSource(ds).target("8").load().migrate();
			// V8 chưa hỗ trợ MP3, dựng WAV/job và nháp ở schema cũ rồi mới nâng V9
			var old = new AudioDataFixture(sql);
			var source = old.insertStagedWav("vi");
			UUID account = UUID.randomUUID();
			sql.update("INSERT INTO admin_account (id, login_name, password_hash, enabled) VALUES (?, ?, 'Fixture', TRUE)", bytes(account), account.toString());
			sql.update("INSERT INTO text_review_ref (text_revision_id, locator, recorded_by, recorded_at) VALUES (?, 'Hồ sơ trước V9', ?, ?)", bytes(source.text()), bytes(account), time());
			var checksums = sql.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank");
			var files = sql.queryForList("SELECT HEX(id) AS id, storage_key, HEX(checksum) AS checksum, format, ready_at, byte_size, duration FROM file_asset");
			String jobColumns = "HEX(id) AS id, HEX(target_text_revision_id) AS target, HEX(source_wav_file_id) AS wav, phase, phase_version, HEX(worker_token) AS token, accepted_at, dispatched_at, result_staged_at";
			var jobs = sql.queryForList("SELECT " + jobColumns + " FROM tts_job");
			String textColumns = "HEX(id) AS id, body, HEX(content_hash) AS hash, saved_at, HEX(active_tts_job_id) AS active, HEX(latest_tts_job_key) AS latest";
			var texts = sql.queryForList("SELECT " + textColumns + " FROM text_revision");
			Flyway upgrade = Flyway.configure().dataSource(ds).target("9").load();
			upgrade.migrate();
			upgrade.validate();
			assertThat(upgrade.info().current().getVersion().toString()).isEqualTo("9");
			assertThat(sql.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version IN ('1','2','3','4','5','6','7','8') ORDER BY installed_rank")).isEqualTo(checksums);
			assertThat(sql.queryForList("SELECT HEX(id) AS id, storage_key, HEX(checksum) AS checksum, format, ready_at, byte_size, duration FROM file_asset")).isEqualTo(files);
			assertThat(sql.queryForList("SELECT " + jobColumns + " FROM tts_job")).isEqualTo(jobs);
			assertThat(sql.queryForList("SELECT " + textColumns + " FROM text_revision")).isEqualTo(texts);
			assertThat(sql.queryForObject("SELECT draft_text_id FROM content_slot WHERE id = ?", byte[].class, bytes(source.slot()))).isEqualTo(bytes(source.text()));
			assertThat(sql.queryForObject("SELECT active_pair_id FROM content_slot WHERE id = ?", byte[].class, bytes(source.slot()))).isNull();
			assertThat(sql.queryForObject("SELECT locator FROM text_review_ref WHERE text_revision_id = ?", String.class, bytes(source.text()))).isEqualTo("Hồ sơ trước V9");
			assertThat(sql.queryForObject("SELECT COUNT(*) FROM audio_revision", Long.class)).isZero();
			assertThat(sql.queryForObject("SELECT COUNT(*) FROM audio_review_ref", Long.class)).isZero();
			assertThat(sql.queryForObject("SELECT COUNT(*) FROM content_pair", Long.class)).isZero();
		}
	}

	private void insertReview(UUID audio, UUID account) {
		jdbc.update("INSERT INTO audio_review_ref (audio_revision_id, locator, recorded_by, recorded_at) VALUES (?, 'Hồ sơ fixture', ?, ?)", bytes(audio), bytes(account), time());
	}

	private static void assertRejected(Runnable command, int code, String constraint) {
		var error = assertThrows(DataAccessException.class, command::run);
		assertThat(error.getMostSpecificCause()).isInstanceOf(SQLException.class);
		assertThat(((SQLException) error.getMostSpecificCause()).getErrorCode()).isEqualTo(code);
		if (constraint != null)
			assertThat(error.getMostSpecificCause().getMessage()).contains(constraint);
	}
}
