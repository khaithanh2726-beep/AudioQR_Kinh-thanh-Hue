package vn.hueqr.backend.schema;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.PlaceView;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.PlaceViewRepository;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class PlaceViewSchemaTests {
	private static final Instant NOW = Instant.parse("2026-11-09T01:00:00.123456Z");

	@Autowired
	private GuestSessionRepository sessions;

	@Autowired
	private PlaceRepository places;

	@Autowired
	private PlaceViewRepository views;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void oneOpeningCanOnlyBeCountedOncePerSessionButAnotherOpeningCanCountAgain() {
		var guest = guest();
		var place = places.saveAndFlush(new Place(UUID.randomUUID(), "Điểm", NOW));
		UUID opening = UUID.randomUUID();
		views.saveAndFlush(new PlaceView(UUID.randomUUID(), guest, opening, place.getId(), place.getId(), "Điểm", "vi", NOW));
		DataAccessException error = assertThrows(DataAccessException.class, () -> jdbc.update("""
				INSERT INTO place_view (id, session_id, opening_key, live_place_id, place_key_snapshot, place_name_snapshot, language, viewed_at)
				SELECT ?, session_id, opening_key, live_place_id, place_key_snapshot, place_name_snapshot, 'en', viewed_at FROM place_view WHERE session_id = ?
				""", bytes(UUID.randomUUID()), bytes(guest.getId())));
		assertThat(((SQLException) error.getMostSpecificCause()).getErrorCode()).isEqualTo(1062);
		assertThat(error.getMostSpecificCause().getMessage()).contains("uk_place_view_session_opening");
		views.saveAndFlush(new PlaceView(UUID.randomUUID(), guest, UUID.randomUUID(), place.getId(), place.getId(), "Điểm", "vi", NOW));
		assertThat(views.findBySession_IdOrderByViewedAtAscIdAsc(guest.getId())).hasSize(2);
	}

	@Test
	void deletingPlaceClearsOnlyTheLiveReferenceAndKeepsSnapshotWithoutHoldingOpeningMetadata() {
		var guest = guest();
		var place = places.saveAndFlush(new Place(UUID.randomUUID(), "Tên cũ", NOW));
		var view = views.saveAndFlush(new PlaceView(UUID.randomUUID(), guest, UUID.randomUUID(), place.getId(), place.getId(), "Tên cũ", "ru", NOW));
		places.delete(place);
		places.flush();
		assertThat(jdbc.queryForObject("SELECT live_place_id FROM place_view WHERE id = ?", byte[].class, bytes(view.getId()))).isNull();
		assertThat(jdbc.queryForObject("SELECT place_key_snapshot FROM place_view WHERE id = ?", byte[].class, bytes(view.getId()))).isEqualTo(bytes(place.getId()));
		assertThat(jdbc.queryForObject("SELECT place_name_snapshot FROM place_view WHERE id = ?", String.class, bytes(view.getId()))).isEqualTo("Tên cũ");
		assertThat(jdbc.queryForObject("""
				SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE()
				AND TABLE_NAME = 'place_view' AND REFERENCED_TABLE_NAME IS NOT NULL
				AND COLUMN_NAME IN ('opening_key', 'place_key_snapshot')
				""", Long.class)).isZero();
	}

	@Test
	void sessionDeletionRequiresViewsToBeRemovedFirstAndLanguageIsRestricted() {
		var guest = guest();
		UUID place = UUID.randomUUID();
		var view = views.saveAndFlush(new PlaceView(UUID.randomUUID(), guest, UUID.randomUUID(), null, place, "Đã xóa", "vi", NOW));
		var error = assertThrows(DataAccessException.class, () -> jdbc.update("DELETE FROM guest_session WHERE id = ?", bytes(guest.getId())));
		assertThat(((SQLException) error.getMostSpecificCause()).getErrorCode()).isEqualTo(1451);
		assertThat(((SQLException) assertThrows(DataAccessException.class,
				() -> jdbc.update("UPDATE place_view SET language = 'de' WHERE id = ?", bytes(view.getId()))).getMostSpecificCause()).getErrorCode()).isEqualTo(3819);
		views.deleteAll();
		views.flush();
		assertThat(jdbc.update("DELETE FROM guest_session WHERE id = ?", bytes(guest.getId()))).isEqualTo(1);
	}

	private GuestSession guest() {
		return sessions.saveAndFlush(new GuestSession(UUID.randomUUID(), UUID.randomUUID(), bytes32(), "00000000-0000-0000-0000-000000000001",
				new byte[32], NOW, NOW.plusSeconds(86400), new byte[32], "Khách", "vi", NOW, NOW.plusSeconds(28800)));
	}

	private static byte[] bytes(UUID id) {
		return java.nio.ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}

	private static byte[] bytes32() {
		return java.nio.ByteBuffer.allocate(32).putLong(UUID.randomUUID().getMostSignificantBits()).array();
	}
}
