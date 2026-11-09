package vn.hueqr.backend.repository;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GuestRetentionRepository {
	public record Session(UUID id, Instant startedAt, Instant expiresAt, Instant endedAt, boolean hasContext) {
	}

	private final JdbcTemplate sql;

	public GuestRetentionRepository(JdbcTemplate sql) {
		this.sql = sql;
	}

	public List<UUID> candidates(Instant now, Instant cutoff, int limit) {
		return sql.query("""
				SELECT s.id FROM guest_session s
				WHERE (s.expires_at <= ? OR (s.ended_at >= s.started_at AND s.ended_at <= s.expires_at AND s.ended_at <= ?))
				AND (
				    CASE WHEN s.ended_at >= s.started_at AND s.ended_at <= s.expires_at AND s.ended_at <= ?
				         THEN s.ended_at ELSE s.expires_at END <= ?
				    OR s.current_opening_id IS NOT NULL OR s.document_nonce IS NOT NULL
				    OR s.current_language IS NOT NULL OR s.selection_seq <> 0
				    OR EXISTS (SELECT 1 FROM guest_content_grant g WHERE g.session_id = s.id)
				    OR EXISTS (SELECT 1 FROM guest_open_intent o WHERE o.session_id = s.id)
				    OR EXISTS (SELECT 1 FROM guest_request_result r WHERE r.session_id = s.id)
				)
				AND NOT EXISTS (
				    SELECT 1 FROM file_claim c JOIN guest_content_grant g ON g.id = c.owner_key
				    WHERE g.session_id = s.id AND c.kind = 'READ'
				)
				ORDER BY s.id LIMIT ?
				""", (row, index) -> uuid(row.getBytes("id")), time(now), time(now), time(now), time(cutoff), limit);
	}

	public Optional<Session> lock(UUID id) {
		return sql.query("""
				SELECT s.*, (s.current_opening_id IS NOT NULL OR s.document_nonce IS NOT NULL
				    OR s.current_language IS NOT NULL OR s.selection_seq <> 0
				    OR EXISTS (SELECT 1 FROM guest_content_grant g WHERE g.session_id = s.id)
				    OR EXISTS (SELECT 1 FROM guest_open_intent o WHERE o.session_id = s.id)
				    OR EXISTS (SELECT 1 FROM guest_request_result r WHERE r.session_id = s.id)) AS has_context
				FROM guest_session s WHERE s.id = ? FOR UPDATE
				""", (row, index) -> new Session(uuid(row.getBytes("id")), instant(row, "started_at"),
				instant(row, "expires_at"), instant(row, "ended_at"), row.getBoolean("has_context")), bytes(id))
				.stream().findFirst();
	}

	public boolean hasReadClaim(UUID session) {
		return sql.queryForObject("""
				SELECT COUNT(*) FROM file_claim c JOIN guest_content_grant g ON g.id = c.owner_key
				WHERE g.session_id = ? AND c.kind = 'READ'
				""", Long.class, bytes(session)) > 0;
	}

	public void clearContext(UUID session) {
		// Tháo khóa trỏ hiện tại trước khi xóa lần mở vì khóa ngoại không xóa dây chuyền
		sql.update("UPDATE guest_session SET current_opening_id = NULL, current_language = NULL, document_nonce = NULL, selection_seq = 0 WHERE id = ?", bytes(session));
		sql.update("DELETE FROM guest_request_result WHERE session_id = ?", bytes(session));
		sql.update("DELETE FROM guest_content_grant WHERE session_id = ?", bytes(session));
		sql.update("DELETE FROM guest_open_intent WHERE session_id = ?", bytes(session));
	}

	public int deleteViews(UUID session) {
		return sql.update("DELETE FROM place_view WHERE session_id = ?", bytes(session));
	}

	public void deleteSession(UUID session) {
		sql.update("DELETE FROM guest_session WHERE id = ?", bytes(session));
	}

	private static Instant instant(ResultSet row, String name) throws SQLException {
		LocalDateTime value = row.getObject(name, LocalDateTime.class);
		return value == null ? null : value.toInstant(ZoneOffset.UTC);
	}

	// DATETIME lưu UTC như cấu hình JPA, không phụ thuộc múi giờ mặc định của JVM
	private static LocalDateTime time(Instant value) {
		return LocalDateTime.ofInstant(value, ZoneOffset.UTC);
	}

	private static UUID uuid(byte[] value) {
		ByteBuffer buffer = ByteBuffer.wrap(value);
		return new UUID(buffer.getLong(), buffer.getLong());
	}

	private static byte[] bytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
