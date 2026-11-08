package vn.hueqr.backend.repository;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.RegionRevision;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class PlaceRegionRepositoryTests {
	private static final Instant SAVED_AT = Instant.parse("2026-11-08T01:02:03.123456Z");
	private static final String POLYGON = "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[0,1],[0,0]]]}";

	@Autowired
	private PlaceRepository places;

	@Autowired
	private RegionRevisionRepository regions;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void reloadsUnicodePlaceAndRegionJsonWithUtcMicrosecondsAndTheirRelationship() {
		Place place = places.saveAndFlush(new Place(UUID.randomUUID(), "Điện Thái Hòa", SAVED_AT));
		byte[] hash = hashFixture();
		RegionRevision region = regions.saveAndFlush(new RegionRevision(UUID.randomUUID(), place,
				POLYGON, "test-map", hash, SAVED_AT));
		hash[0] = 9;
		entityManager.clear();

		RegionRevision loaded = regions.findById(region.getId()).orElseThrow();

		assertThat(loaded.getPlace().getId()).isEqualTo(place.getId());
		assertThat(loaded.getPlace().getDisplayName()).isEqualTo("Điện Thái Hòa");
		assertThat(loaded.getMapSpaceKey()).isEqualTo("test-map");
		assertThat(loaded.getGeometryHash()).containsOnly((byte) 1);
		loaded.getGeometryHash()[0] = 7;
		assertThat(loaded.getGeometryHash()).containsOnly((byte) 1);
		assertThat(loaded.getSavedAt()).isEqualTo(SAVED_AT);
		assertThat(loaded.getPlace().getCreatedAt()).isEqualTo(SAVED_AT);
		assertThat(jdbc.queryForObject("SELECT JSON_TYPE(polygon) FROM region_revision WHERE id = ?",
				String.class, uuidBytes(region.getId()))).isEqualTo("OBJECT");
		assertThat(jdbc.queryForObject("SELECT JSON_UNQUOTE(JSON_EXTRACT(polygon, '$.type')) FROM region_revision WHERE id = ?",
				String.class, uuidBytes(region.getId()))).isEqualTo("Polygon");
		assertThat(jdbc.queryForObject("SELECT saved_at FROM region_revision WHERE id = ?",
				LocalDateTime.class, uuidBytes(region.getId()))).isEqualTo(LocalDateTime.ofInstant(SAVED_AT, ZoneOffset.UTC));
	}

	@Test
	void savingARegionLeavesThePlaceHiddenWithoutAnActivePointer() {
		Place place = places.saveAndFlush(new Place(UUID.randomUUID(), "Cửu Đỉnh", SAVED_AT));
		regions.saveAndFlush(new RegionRevision(UUID.randomUUID(), place, POLYGON, "test-map", hashFixture(), SAVED_AT));
		entityManager.clear();

		Place loaded = places.findById(place.getId()).orElseThrow();

		assertThat(loaded.isVisible()).isFalse();
		assertThat(loaded.getActiveRegionRevisionId()).isNull();
		assertThat(loaded.getVersion()).isZero();
	}

	@Test
	void changingTheActivePointerKeepsBothSavedRevisionsAndThePlaceHidden() {
		Place place = places.saveAndFlush(new Place(UUID.randomUUID(), "Cung Diên Thọ", SAVED_AT));
		RegionRevision first = regions.saveAndFlush(new RegionRevision(UUID.randomUUID(), place,
				POLYGON, "test-map", hashFixture(), SAVED_AT));
		RegionRevision second = regions.saveAndFlush(new RegionRevision(UUID.randomUUID(), place,
				POLYGON, "test-map", hashFixture(), SAVED_AT.plusSeconds(1)));
		place.setActiveRegionRevisionId(first.getId(), SAVED_AT.plusSeconds(2));
		places.flush();
		place.setActiveRegionRevisionId(second.getId(), SAVED_AT.plusSeconds(3));
		places.flush();
		entityManager.clear();

		Place loaded = places.findById(place.getId()).orElseThrow();

		assertThat(loaded.getActiveRegionRevisionId()).isEqualTo(second.getId());
		assertThat(loaded.getVersion()).isEqualTo(2L);
		assertThat(loaded.getUpdatedAt()).isEqualTo(SAVED_AT.plusSeconds(3));
		assertThat(loaded.isVisible()).isFalse();
		assertThat(regions.findById(first.getId()).orElseThrow().getSavedAt()).isEqualTo(SAVED_AT);
		assertThat(regions.findById(second.getId()).orElseThrow().getSavedAt()).isEqualTo(SAVED_AT.plusSeconds(1));
	}

	@Test
	void renamingOnePlaceKeepsItsIdAndDoesNotChangeAnotherPlaceWithTheSameName() {
		Place first = places.saveAndFlush(new Place(UUID.randomUUID(), "Cổng Ngọ Môn", SAVED_AT));
		Place second = places.saveAndFlush(new Place(UUID.randomUUID(), "Cổng Ngọ Môn", SAVED_AT));
		first.rename("Ngọ Môn", SAVED_AT.plusSeconds(1));
		places.flush();
		entityManager.clear();

		Place reloaded = places.findById(first.getId()).orElseThrow();

		assertThat(reloaded.getId()).isEqualTo(first.getId());
		assertThat(reloaded.getDisplayName()).isEqualTo("Ngọ Môn");
		assertThat(reloaded.getCreatedAt()).isEqualTo(SAVED_AT);
		assertThat(reloaded.getUpdatedAt()).isEqualTo(SAVED_AT.plusSeconds(1));
		assertThat(reloaded.getVersion()).isEqualTo(1L);
		assertThat(places.findById(second.getId()).orElseThrow().getDisplayName()).isEqualTo("Cổng Ngọ Môn");
	}

	@Test
	void rejectsAnUpdateFromAStaleDetachedPlaceVersion() {
		Place stale = places.saveAndFlush(new Place(UUID.randomUUID(), "Cửu Đỉnh", SAVED_AT));
		// Giữ bản cũ ngoài persistence context để mô phỏng người sửa trên phiên bản đã lỗi thời
		entityManager.clear();
		Place current = places.findById(stale.getId()).orElseThrow();
		current.rename("Cửu Đỉnh hiện hành", SAVED_AT.plusSeconds(1));
		places.flush();
		entityManager.clear();
		stale.rename("Cửu Đỉnh bản cũ", SAVED_AT.plusSeconds(2));

		assertThrows(ObjectOptimisticLockingFailureException.class, () -> places.saveAndFlush(stale));
	}

	private static byte[] hashFixture() {
		byte[] hash = new byte[32];
		Arrays.fill(hash, (byte) 1);
		return hash;
	}

	private static byte[] uuidBytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
