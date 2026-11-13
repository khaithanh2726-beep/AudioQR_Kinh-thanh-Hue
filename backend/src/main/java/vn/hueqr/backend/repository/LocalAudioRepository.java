package vn.hueqr.backend.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static vn.hueqr.backend.repository.LifecycleRepository.*;

@Repository
public class LocalAudioRepository {
	public record Job(UUID id, UUID place, UUID text, UUID wav, String phase, boolean valid, UUID task, UUID token, UUID file) {
	}

	public record Source(UUID id, String key, String checksum, long size, BigDecimal duration) {
	}

	private final JdbcTemplate sql;

	public LocalAudioRepository(JdbcTemplate sql) {
		this.sql = sql;
	}

	public List<UUID> pending(int limit) {
		return sql.query("SELECT id FROM tts_job WHERE target_valid = TRUE AND phase IN ('RESULT_STAGED', 'CONVERTING') ORDER BY updated_at, id LIMIT ?",
				(r, n) -> uuid(r, "id"), limit);
	}

	public Optional<Job> job(UUID id, boolean lock) {
		return sql.query("SELECT * FROM tts_job WHERE id = ?" + (lock ? " FOR UPDATE" : ""), (r, n) -> new Job(id,
				uuid(r, "place_key_snapshot"), uuid(r, "target_text_revision_id"), uuid(r, "source_wav_file_id"), r.getString("phase"),
				r.getBoolean("target_valid"), uuid(r, "conversion_task"), uuid(r, "conversion_token"), uuid(r, "conversion_file_id")), bytes(id)).stream().findFirst();
	}

	public boolean lockTarget(Job job) {
		if (sql.query("SELECT id FROM place WHERE id = ? FOR UPDATE", (r, n) -> r.getBytes(1), bytes(job.place())).isEmpty() || job.text() == null)
			return false;
		return !sql.query("SELECT id FROM text_revision WHERE id = ? FOR UPDATE", (r, n) -> r.getBytes(1), bytes(job.text())).isEmpty();
	}

	public boolean matchesTarget(UUID id) {
		return sql.queryForObject("SELECT EXISTS(SELECT 1 FROM tts_job j JOIN text_revision t ON t.id = j.target_text_revision_id JOIN content_slot s ON s.id = t.slot_id WHERE j.id = ? AND j.target_valid = TRUE AND t.active_tts_job_id = j.id AND t.id = j.text_revision_key_snapshot AND t.content_hash = j.text_hash_snapshot AND s.language = j.language_snapshot AND s.place_id = j.place_key_snapshot)", Boolean.class, bytes(id));
	}

	public Optional<Source> wav(UUID file) {
		return sql.query("SELECT * FROM file_asset WHERE id = ? AND format = 'WAV' AND state = 'READY' FOR UPDATE", (r,n) ->
				new Source(file, r.getString("storage_key"), java.util.HexFormat.of().formatHex(r.getBytes("checksum")), r.getLong("byte_size"), r.getBigDecimal("duration")), bytes(file)).stream().findFirst();
	}

	public String textHash(UUID job) {
		return java.util.HexFormat.of().formatHex(sql.queryForObject("SELECT text_hash_snapshot FROM tts_job WHERE id = ?", byte[].class, bytes(job)));
	}

	public void claim(UUID job, UUID owner, UUID task, UUID token, UUID file, Instant now) {
		sql.update("UPDATE tts_job SET phase = 'CONVERTING', conversion_instance = ?, conversion_task = ?, conversion_token = ?, conversion_file_id = ?, local_finished_at = NULL, updated_at = ?, phase_version = phase_version + 1 WHERE id = ?",
				bytes(owner), bytes(task), bytes(token), bytes(file), time(now), bytes(job));
	}

	public void succeed(UUID job, UUID audio, UUID text, UUID wav, UUID mp3, Instant now) {
		sql.update("INSERT INTO audio_revision (id, text_revision_id, job_id, wav_file_id, mp3_file_id, created_at) VALUES (?, ?, ?, ?, ?, ?)",
				bytes(audio), bytes(text), bytes(job), bytes(wav), bytes(mp3), time(now));
		sql.update("UPDATE tts_job SET phase = 'SUCCEEDED', terminal_at = ?, updated_at = ?, failure_code = NULL, phase_version = phase_version + 1 WHERE id = ?", time(now), time(now), bytes(job));
		sql.update("UPDATE text_revision SET active_tts_job_id = NULL WHERE id = ? AND active_tts_job_id = ?", bytes(text), bytes(job));
	}

	public void fail(UUID job, UUID text, String code, Instant now) {
		sql.update("UPDATE tts_job SET phase = 'FAILED', failure_code = ?, terminal_at = ?, updated_at = ?, phase_version = phase_version + 1 WHERE id = ?", code, time(now), time(now), bytes(job));
		if (text != null)
			sql.update("UPDATE text_revision SET active_tts_job_id = NULL WHERE id = ? AND active_tts_job_id = ?", bytes(text), bytes(job));
	}

	public void discard(UUID job, Instant now) {
		sql.update("UPDATE text_revision SET active_tts_job_id = NULL WHERE active_tts_job_id = ?", bytes(job));
		sql.update("UPDATE tts_job SET phase = 'DISCARDED', source_wav_file_id = NULL, result_staged_at = NULL, terminal_at = ?, updated_at = ?, phase_version = phase_version + 1 WHERE id = ?", time(now), time(now), bytes(job));
	}

	public void reset(UUID job) {
		sql.update("UPDATE tts_job SET conversion_instance = NULL, conversion_task = NULL, conversion_token = NULL, conversion_file_id = NULL WHERE id = ?", bytes(job));
	}
}
