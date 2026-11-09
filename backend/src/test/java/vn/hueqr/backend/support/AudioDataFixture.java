package vn.hueqr.backend.support;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;

// Chỉ dựng metadata trong database tạm, không tạo bytes MP3 hoặc bằng chứng đã nghe duyệt
public class AudioDataFixture {
	public static final Instant NOW = Instant.parse("2026-11-09T01:02:03.123456Z");

	public record Source(UUID place, UUID slot, UUID text, UUID job, UUID wav, UUID mp3) {
	}

	private final JdbcTemplate sql;

	public AudioDataFixture(JdbcTemplate sql) {
		this.sql = sql;
	}

	public Source insertSource(String language) {
		Source source = insertStagedWav(language);
		insertFile(source.mp3(), "MP3");
		return source;
	}

	public Source insertStagedWav(String language) {
		var source = new Source(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
		sql.update("INSERT INTO place (id, display_name, created_at, updated_at) VALUES (?, 'Fixture', ?, ?)", bytes(source.place()), time(), time());
		sql.update("INSERT INTO content_slot (id, place_id, language) VALUES (?, ?, ?)", bytes(source.slot()), bytes(source.place()), language);
		sql.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, 'Bản đã lưu', ?, ?)",
				bytes(source.text()), bytes(source.slot()), new byte[32], time());
		insertFile(source.wav(), "WAV");
		sql.update("""
				INSERT INTO tts_job (id, target_text_revision_id, place_key_snapshot, text_revision_key_snapshot,
				language_snapshot, text_hash_snapshot, model, voice, phase, worker_instance, worker_token,
				admin_account_key, request_window_key, request_operation_key, accepted_at, updated_at,
				dispatched_at, source_wav_file_id, result_staged_at)
				VALUES (?, ?, ?, ?, ?, ?, 'gemini-3.8-flash-tts', 'Charon', 'RESULT_STAGED', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""", bytes(source.job()), bytes(source.text()), bytes(source.place()), bytes(source.text()), language,
				new byte[32], bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), bytes(UUID.randomUUID()),
				bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), time(), time(), time(), bytes(source.wav()), time());
		sql.update("UPDATE text_revision SET active_tts_job_id = ?, latest_tts_job_key = ? WHERE id = ?",
				bytes(source.job()), bytes(source.job()), bytes(source.text()));
		sql.update("UPDATE content_slot SET draft_text_id = ? WHERE id = ?", bytes(source.text()), bytes(source.slot()));
		return source;
	}

	public void insertFile(UUID id, String format) {
		sql.update("INSERT INTO file_asset (id, storage_key, format, checksum, byte_size, duration, state, ready_at) VALUES (?, ?, ?, ?, 48044, 1, 'READY', ?)",
				bytes(id), id + "." + format.toLowerCase(java.util.Locale.ROOT), format, new byte[32], time());
	}

	public UUID insertAudio(Source source) {
		UUID id = UUID.randomUUID();
		insertAudio(id, source.text(), source.job(), source.wav(), source.mp3());
		return id;
	}

	public void insertAudio(UUID id, UUID text, UUID job, UUID wav, UUID mp3) {
		sql.update("INSERT INTO audio_revision (id, text_revision_id, job_id, wav_file_id, mp3_file_id, created_at) VALUES (?, ?, ?, ?, ?, ?)",
				bytes(id), bytes(text), bytes(job), bytes(wav), bytes(mp3), time());
	}

	public UUID insertPair(UUID slot, UUID text, UUID audio) {
		UUID id = UUID.randomUUID();
		sql.update("INSERT INTO content_pair (id, slot_id, text_revision_id, audio_revision_id, created_at) VALUES (?, ?, ?, ?, ?)",
				bytes(id), bytes(slot), bytes(text), bytes(audio), time());
		return id;
	}

	public static byte[] bytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}

	public static Timestamp time() {
		return Timestamp.from(NOW);
	}
}
