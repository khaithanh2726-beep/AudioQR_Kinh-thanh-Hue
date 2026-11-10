package vn.hueqr.backend.repository;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.hueqr.backend.dto.GuestViewAckResponse;

@Repository
public class GuestContentRepository {
	public record Opening(UUID id, UUID sessionId, UUID placeId, String name, String language, long version, String state) {
	}

	public record Grant(UUID id, UUID openingId, UUID placeId, String name, String language,
			UUID textId, byte[] textHash, UUID pairId, UUID audioId, UUID mp3Id, String source,
			boolean viewEligible, String availableLanguages, Instant expiresAt, Instant revokedAt) {
	}

	public record Result(UUID openingId, long openingVersion, UUID grantId, String action,
			String selectionKind, String language, String availableLanguages, byte[] fingerprint) {
	}

	private final JdbcTemplate sql;

	public GuestContentRepository(JdbcTemplate sql) {
		this.sql = sql;
	}

	public Optional<Opening> opening(UUID id, UUID session) {
		return sql.query("SELECT * FROM guest_open_intent WHERE id = ? AND session_id = ?",
				(r, n) -> new Opening(uuid(r, "id"), uuid(r, "session_id"), uuid(r, "place_key_snapshot"),
						r.getString("place_name_snapshot"), r.getString("current_language"), r.getLong("version"), r.getString("state")),
				bytes(id), bytes(session)).stream().findFirst();
	}

	public Opening createOpening(UUID session, UUID place, String name, String language, Instant now) {
		UUID id = UUID.randomUUID();
		sql.update("INSERT INTO guest_open_intent (id, session_id, live_place_id, place_key_snapshot, place_name_snapshot, current_language, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
				bytes(id), bytes(session), bytes(place), bytes(place), name, language, Timestamp.from(now));
		return new Opening(id, session, place, name, language, 0, "OPEN");
	}

	public Opening language(Opening opening, String language) {
		sql.update("UPDATE guest_open_intent SET current_language = ?, version = version + 1 WHERE id = ?", language, bytes(opening.id()));
		return new Opening(opening.id(), opening.sessionId(), opening.placeId(), opening.name(), language, opening.version() + 1, "OPEN");
	}

	public void close(UUID opening, Instant now) {
		sql.update("UPDATE guest_open_intent SET state = 'CLOSED', closed_at = ?, version = version + 1 WHERE id = ? AND state = 'OPEN'",
				Timestamp.from(now), bytes(opening));
	}

	public void insertGrant(UUID session, Grant grant, Instant now) {
		sql.update("""
				INSERT INTO guest_content_grant (id, session_id, opening_id, place_key_snapshot, place_name_snapshot,
				language, text_revision_key, text_hash, pair_key, audio_revision_key, mp3_file_id, source,
				view_eligible, available_languages, issued_at, expires_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""", bytes(grant.id()), bytes(session), bytes(grant.openingId()), bytes(grant.placeId()), grant.name(), grant.language(),
				bytes(grant.textId()), grant.textHash(), bytes(grant.pairId()), bytes(grant.audioId()), bytes(grant.mp3Id()), grant.source(),
				grant.viewEligible(), grant.availableLanguages(), Timestamp.from(now), Timestamp.from(grant.expiresAt()));
	}

	public Optional<Grant> grant(UUID id, UUID session) {
		return sql.query("SELECT * FROM guest_content_grant WHERE id = ? AND session_id = ?", (r, n) -> new Grant(
				uuid(r, "id"), uuid(r, "opening_id"), uuid(r, "place_key_snapshot"), r.getString("place_name_snapshot"),
				r.getString("language"), uuid(r, "text_revision_key"), r.getBytes("text_hash"), uuid(r, "pair_key"),
				uuid(r, "audio_revision_key"), uuid(r, "mp3_file_id"), r.getString("source"), r.getBoolean("view_eligible"),
				r.getString("available_languages"), r.getTimestamp("expires_at").toInstant(), instant(r, "revoked_at")), bytes(id), bytes(session))
				.stream().findFirst();
	}

	public Optional<Result> result(UUID session, UUID key) {
		return sql.query("SELECT * FROM guest_request_result WHERE session_id = ? AND request_key = ?", (r, n) -> new Result(
				uuid(r, "opening_key"), r.getLong("opening_version"), uuid(r, "grant_key"), r.getString("action"),
				r.getString("selection_kind"), r.getString("language"), r.getString("available_languages"), r.getBytes("fingerprint")),
				bytes(session), bytes(key)).stream().findFirst();
	}

	public void insertResult(UUID session, UUID key, UUID nonce, long seq, Result result) {
		sql.update("""
				INSERT INTO guest_request_result (session_id, request_key, document_nonce, selection_seq, fingerprint,
				action, opening_key, opening_version, grant_key, selection_kind, language, available_languages)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""", bytes(session), bytes(key), bytes(nonce), seq, result.fingerprint(), result.action(), bytes(result.openingId()),
				result.openingVersion(), bytes(result.grantId()), result.selectionKind(), result.language(), result.availableLanguages());
	}

	public boolean selectionUsed(UUID session, UUID nonce, long seq) {
		return sql.queryForObject("SELECT COUNT(*) FROM guest_request_result WHERE session_id = ? AND document_nonce = ? AND selection_seq = ?",
				Long.class, bytes(session), bytes(nonce), seq) > 0;
	}

	public Optional<GuestViewAckResponse> view(UUID session, UUID opening) {
		return sql.query("SELECT * FROM place_view WHERE session_id = ? AND opening_key = ?", (r, n) -> new GuestViewAckResponse(
				uuid(r, "id"), false, r.getTimestamp("viewed_at").toInstant(), r.getString("language"), opening), bytes(session), bytes(opening))
				.stream().findFirst();
	}

	public GuestViewAckResponse insertView(UUID session, Grant grant, boolean placeExists, Instant now) {
		UUID id = UUID.randomUUID();
		sql.update("""
				INSERT INTO place_view (id, session_id, opening_key, live_place_id, place_key_snapshot, place_name_snapshot, language, viewed_at)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""", bytes(id), bytes(session), bytes(grant.openingId()), placeExists ? bytes(grant.placeId()) : null,
				bytes(grant.placeId()), grant.name(), grant.language(), Timestamp.from(now));
		return new GuestViewAckResponse(id, true, now, grant.language(), grant.openingId());
	}

	public void claim(UUID id, UUID file, UUID grant, Instant now) {
		claim(id, file, grant, now, null);
	}

	public void claim(UUID id, UUID file, UUID grant, Instant now, UUID instance) {
		sql.update("INSERT INTO file_claim (id, file_id, owner_key, kind, created_at, owner_instance) VALUES (?, ?, ?, 'READ', ?, ?)",
				bytes(id), bytes(file), bytes(grant), LifecycleRepository.time(now), bytes(instance));
	}

	public void release(UUID id) {
		sql.update("DELETE FROM file_claim WHERE id = ?", bytes(id));
	}

	public static List<String> languages(String value) {
		return value.isEmpty() ? List.of() : List.of(value.split(","));
	}

	private static Instant instant(ResultSet row, String column) throws SQLException {
		Timestamp timestamp = row.getTimestamp(column);
		return timestamp == null ? null : timestamp.toInstant();
	}

	private static UUID uuid(ResultSet row, String column) throws SQLException {
		byte[] raw = row.getBytes(column);
		if (raw == null)
			return null;
		var buffer = ByteBuffer.wrap(raw);
		return new UUID(buffer.getLong(), buffer.getLong());
	}

	private static byte[] bytes(UUID id) {
		return id == null ? null : ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
