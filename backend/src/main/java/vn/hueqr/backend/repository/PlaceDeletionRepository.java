package vn.hueqr.backend.repository;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceDeletionRepository {
	private final JdbcTemplate sql;

	public PlaceDeletionRepository(JdbcTemplate sql) {
		this.sql = sql;
	}

	public List<UUID> fileIds(UUID place) {
		return sql.query("""
				SELECT a.wav_file_id AS id FROM audio_revision a JOIN text_revision t ON t.id = a.text_revision_id
				JOIN content_slot s ON s.id = t.slot_id WHERE s.place_id = ?
				UNION SELECT a.mp3_file_id FROM audio_revision a JOIN text_revision t ON t.id = a.text_revision_id
				JOIN content_slot s ON s.id = t.slot_id WHERE s.place_id = ?
				""", (row, index) -> {
			var buffer = ByteBuffer.wrap(row.getBytes(1));
			return new UUID(buffer.getLong(), buffer.getLong());
		}, bytes(place), bytes(place));
	}

	public List<UUID> appliedFileIds(UUID place) {
		return sql.query("""
				SELECT a.wav_file_id FROM content_slot s JOIN content_pair p ON p.id = s.active_pair_id
				JOIN audio_revision a ON a.id = p.audio_revision_id WHERE s.place_id = ?
				UNION SELECT a.mp3_file_id FROM content_slot s JOIN content_pair p ON p.id = s.active_pair_id
				JOIN audio_revision a ON a.id = p.audio_revision_id WHERE s.place_id = ?
				""", (row, index) -> {
			var buffer = ByteBuffer.wrap(row.getBytes(1));
			return new UUID(buffer.getLong(), buffer.getLong());
		}, bytes(place), bytes(place));
	}

	public void deletePrivateData(UUID place, Instant now) {
		var at = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
		// Tháo các con trỏ vòng trước khi xóa bản riêng, giữ file và snapshot lịch sử độc lập
		sql.update("UPDATE content_slot SET active_pair_id = NULL, draft_text_id = NULL WHERE place_id = ?", bytes(place));
		sql.update("UPDATE text_revision t JOIN content_slot s ON s.id = t.slot_id SET t.active_tts_job_id = NULL WHERE s.place_id = ?", bytes(place));
		sql.update("DELETE p FROM content_pair p JOIN content_slot s ON s.id = p.slot_id WHERE s.place_id = ?", bytes(place));
		sql.update("DELETE r FROM audio_review_ref r JOIN audio_revision a ON a.id = r.audio_revision_id JOIN text_revision t ON t.id = a.text_revision_id JOIN content_slot s ON s.id = t.slot_id WHERE s.place_id = ?", bytes(place));
		sql.update("DELETE a FROM audio_revision a JOIN text_revision t ON t.id = a.text_revision_id JOIN content_slot s ON s.id = t.slot_id WHERE s.place_id = ?", bytes(place));
		// Chưa dispatch thì loại bỏ ngay, đang xử lý giữ marker tới khi worker kết thúc hoặc có chứng cứ dừng
		sql.update("""
				UPDATE tts_job SET target_valid = FALSE, target_text_revision_id = NULL,
				terminal_at = CASE WHEN phase = 'QUEUED' THEN ? ELSE terminal_at END,
				phase = CASE WHEN phase = 'QUEUED' THEN 'DISCARDED' ELSE phase END,
				phase_version = phase_version + 1, updated_at = ? WHERE place_key_snapshot = ?
				""", at, at, bytes(place));
		sql.update("DELETE r FROM text_review_ref r JOIN text_revision t ON t.id = r.text_revision_id JOIN content_slot s ON s.id = t.slot_id WHERE s.place_id = ?", bytes(place));
		sql.update("DELETE t FROM text_revision t JOIN content_slot s ON s.id = t.slot_id WHERE s.place_id = ?", bytes(place));
		sql.update("DELETE FROM content_slot WHERE place_id = ?", bytes(place));
		sql.update("UPDATE place SET active_region_revision_id = NULL WHERE id = ?", bytes(place));
		sql.update("DELETE FROM region_revision WHERE place_id = ?", bytes(place));
	}

	public void deletePlace(UUID place) {
		sql.update("DELETE FROM place WHERE id = ?", bytes(place));
	}

	private static byte[] bytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
