package vn.hueqr.backend.schema;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
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

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class PlaceRegionSchemaTests {
	private static final Instant SAVED_AT = Instant.parse("2026-11-08T01:02:03.123456Z");
	private static final String POLYGON = "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[0,1],[0,0]]]}";

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private Flyway flyway;

	@Test
	void permitsHiddenPlacesWithTheSameNameWithoutARegionOrContent() {
		UUID first = insertPlace();
		UUID second = insertPlace();

		assertThat(first).isNotEqualTo(second);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place WHERE display_name = ?",
				Long.class, "Cổng Ngọ Môn")).isEqualTo(2L);
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?",
				Boolean.class, uuidBytes(first))).isFalse();
		assertThat(jdbc.queryForObject("SELECT active_region_revision_id FROM place WHERE id = ?",
				byte[].class, uuidBytes(first))).isNull();
		assertThat(jdbc.queryForObject("SELECT version FROM place WHERE id = ?",
				Long.class, uuidBytes(first))).isZero();
	}

	@Test
	void rejectsARegionWhosePlaceDoesNotExist() {
		assertRejected(() -> insertRegion(UUID.randomUUID(), POLYGON), 1452, "fk_region_revision_place");
	}

	@Test
	void rejectsAnActiveRegionThatDoesNotExist() {
		UUID place = insertPlace();

		assertRejected(() -> selectRegion(place, UUID.randomUUID()), 1452, "fk_place_active_region");
	}

	@Test
	void rejectsSelectingAnotherPlacesRegion() {
		UUID first = insertPlace();
		UUID second = insertPlace();
		UUID region = insertRegion(first, POLYGON);

		assertRejected(() -> selectRegion(second, region), 1452, "fk_place_active_region");
	}

	@Test
	void rejectsDeletingAPlaceWhileItsRegionsRemain() {
		UUID place = insertPlace();
		insertRegion(place, POLYGON);

		assertRejected(() -> jdbc.update("DELETE FROM place WHERE id = ?", uuidBytes(place)),
				1451, "fk_region_revision_place");
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM region_revision WHERE place_id = ?",
				Long.class, uuidBytes(place))).isEqualTo(1L);
	}

	@Test
	void rejectsDeletingAnActiveRegion() {
		UUID place = insertPlace();
		UUID region = insertRegion(place, POLYGON);
		selectRegion(place, region);

		assertRejected(() -> jdbc.update("DELETE FROM region_revision WHERE id = ?", uuidBytes(region)),
				1451, "fk_place_active_region");
	}

	@Test
	void permitsExplicitDeletionAfterClearingThePointerAndRemovingRegions() {
		UUID retainedPlace = insertPlace();
		UUID place = insertPlace();
		UUID region = insertRegion(place, POLYGON);
		selectRegion(place, region);

		jdbc.update("UPDATE place SET active_region_revision_id = NULL WHERE id = ?", uuidBytes(place));
		jdbc.update("DELETE FROM region_revision WHERE id = ?", uuidBytes(region));
		jdbc.update("DELETE FROM place WHERE id = ?", uuidBytes(place));

		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place WHERE id = ?",
				Long.class, uuidBytes(place))).isZero();
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM place WHERE id = ?",
				Long.class, uuidBytes(retainedPlace))).isEqualTo(1L);
	}

	@Test
	void rejectsMalformedJsonInThePolygonColumn() {
		UUID place = insertPlace();

		assertRejected(() -> insertRegion(place, "{invalid"), 3140, "polygon");
	}

	@Test
	void permitsDistinctSavedRevisionsWithTheSameGeometryWithoutApplyingEither() {
		UUID place = insertPlace();
		UUID first = insertRegion(place, POLYGON);
		UUID second = insertRegion(place, POLYGON);

		assertThat(first).isNotEqualTo(second);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM region_revision WHERE place_id = ?",
				Long.class, uuidBytes(place))).isEqualTo(2L);
		assertThat(jdbc.queryForObject("SELECT active_region_revision_id FROM place WHERE id = ?",
				byte[].class, uuidBytes(place))).isNull();
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?",
				Boolean.class, uuidBytes(place))).isFalse();
	}

	@Test
	void validatesAppliedMigrationsAndRestrictiveForeignKeys() {
		flyway.validate();

		assertThat(Arrays.stream(flyway.info().applied()).filter(info -> info.getVersion() != null)
				.map(info -> info.getVersion().toString())).containsExactly("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
		assertThat(jdbc.queryForList("""
				SELECT DELETE_RULE FROM information_schema.REFERENTIAL_CONSTRAINTS
				WHERE CONSTRAINT_SCHEMA = DATABASE()
				  AND CONSTRAINT_NAME IN ('fk_region_revision_place', 'fk_place_active_region')
				""", String.class)).containsExactlyInAnyOrder("RESTRICT", "RESTRICT");
	}

	@Test
	void upgradingFromV1PreservesAnExistingGuestSessionAndTheV1Checksum() {
		// Database riêng chỉ áp V1 trước, tránh dùng schema đã được Spring nâng lên V2
		try (MySQLContainer mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var source = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
			JdbcTemplate legacy = new JdbcTemplate(source);
			Flyway.configure().dataSource(source).target("1").load().migrate();
			Integer originalChecksum = legacy.queryForObject(
					"SELECT checksum FROM flyway_schema_history WHERE version = '1'", Integer.class);
			UUID sessionId = UUID.randomUUID();
			byte[] hash = new byte[32];
			Arrays.fill(hash, (byte) 3);
			legacy.update("""
					INSERT INTO guest_session (id, start_request_key, credential_hash, service_epoch,
					    start_fingerprint, start_ticket_issued_at, start_ticket_expires_at, start_ticket_digest,
					    display_name, initial_language, started_at, expires_at)
					VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
					""", uuidBytes(sessionId), uuidBytes(UUID.randomUUID()), hash,
					"00000000-0000-0000-0000-000000000001", hash, Timestamp.from(SAVED_AT),
					Timestamp.from(SAVED_AT.plusSeconds(86400)), hash, "Khách trước V2", "vi",
					Timestamp.from(SAVED_AT), Timestamp.from(SAVED_AT.plusSeconds(28800)));

			Flyway upgrade = Flyway.configure().dataSource(source).target("2").load();
			upgrade.migrate();
			upgrade.validate();

			assertThat(upgrade.info().current().getVersion().toString()).isEqualTo("2");
			assertThat(legacy.queryForObject("SELECT checksum FROM flyway_schema_history WHERE version = '1'",
					Integer.class)).isEqualTo(originalChecksum);
			assertThat(legacy.queryForObject("SELECT display_name FROM guest_session WHERE id = ?",
					String.class, uuidBytes(sessionId))).isEqualTo("Khách trước V2");
			assertThat(legacy.queryForObject("SELECT started_at FROM guest_session WHERE id = ?",
					Timestamp.class, uuidBytes(sessionId))).isEqualTo(Timestamp.from(SAVED_AT));
			assertThat(legacy.queryForObject("SELECT COUNT(*) FROM guest_session", Long.class)).isEqualTo(1L);
			assertThat(legacy.queryForObject("SELECT COUNT(*) FROM place", Long.class)).isZero();
			assertThat(legacy.queryForObject("SELECT COUNT(*) FROM region_revision", Long.class)).isZero();
		}
	}

	private UUID insertPlace() {
		UUID id = UUID.randomUUID();
		jdbc.update("INSERT INTO place (id, display_name, created_at, updated_at) VALUES (?, ?, ?, ?)",
				uuidBytes(id), "Cổng Ngọ Môn", Timestamp.from(SAVED_AT), Timestamp.from(SAVED_AT));
		return id;
	}

	private UUID insertRegion(UUID place, String polygon) {
		UUID id = UUID.randomUUID();
		byte[] hash = new byte[32];
		Arrays.fill(hash, (byte) 1);
		jdbc.update("""
				INSERT INTO region_revision (id, place_id, polygon, map_space_key, geometry_hash, saved_at)
				VALUES (?, ?, ?, ?, ?, ?)
				""", uuidBytes(id), uuidBytes(place), polygon, "test-map", hash, Timestamp.from(SAVED_AT));
		return id;
	}

	private void selectRegion(UUID place, UUID region) {
		jdbc.update("UPDATE place SET active_region_revision_id = ? WHERE id = ?",
				uuidBytes(region), uuidBytes(place));
	}

	private static void assertRejected(Runnable command, int errorCode, String constraintOrColumn) {
		DataAccessException error = assertThrows(DataAccessException.class, command::run);
		assertThat(error.getMostSpecificCause()).isInstanceOf(SQLException.class);
		SQLException cause = (SQLException) error.getMostSpecificCause();
		assertThat(cause.getErrorCode()).isEqualTo(errorCode);
		assertThat(cause.getMessage()).contains(constraintOrColumn);
	}

	private static byte[] uuidBytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
