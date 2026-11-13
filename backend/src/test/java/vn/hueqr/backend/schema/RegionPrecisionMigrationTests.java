package vn.hueqr.backend.schema;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.mysql.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

class RegionPrecisionMigrationTests {
	@Test
	void upgradingV12PreservesExistingRegionsAndStoresNewDecimalsWithoutRounding() {
		try (var mysql = new MySQLContainer("mysql:8.4.13")) {
			mysql.start();
			var data = new DriverManagerDataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
			var sql = new JdbcTemplate(data);
			Flyway.configure().dataSource(data).target("12").load().migrate();
			var checksums = sql.queryForList("SELECT version, checksum FROM flyway_schema_history ORDER BY installed_rank");
			sql.update("INSERT INTO place(id,display_name,created_at,updated_at) VALUES(UNHEX(REPEAT('01',16)), 'Region migration', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6))");
			String polygon = "{\"type\":\"Polygon\",\"coordinates\":[[[107.57891379087943,16.467354402659],"
					+ "[107.57956620912057,16.467645002659],[107.57922589087943,16.468010497340998],"
					+ "[107.57891379087943,16.467354402659]]]}";
			String insert = "INSERT INTO region_revision(id,place_id,polygon,map_space_key,geometry_hash,saved_at) "
					+ "VALUES(UNHEX(REPEAT(?,16)),UNHEX(REPEAT('01',16)),?,'WGS84_LON_LAT',UNHEX(REPEAT('ab',32)),UTC_TIMESTAMP(6))";
			sql.update(insert, "02", polygon);
			sql.update("UPDATE place SET active_region_revision_id=UNHEX(REPEAT('02',16))");
			var before = sql.queryForMap("SELECT CAST(polygon AS CHAR) AS polygon,HEX(geometry_hash) AS digest,saved_at FROM region_revision");
			// V13 không thể suy ngược chữ số đã mất ở JSON cũ, phải giữ bản cũ và Hash để kiểm lại qua API.
			assertThat(before.get("polygon")).isNotEqualTo(polygon);
			var upgrade = Flyway.configure().dataSource(data).target("13").load();
			upgrade.migrate();
			upgrade.validate();
			assertThat(sql.queryForMap("SELECT polygon,HEX(geometry_hash) AS digest,saved_at FROM region_revision")).isEqualTo(before);
			assertThat(sql.queryForObject("SELECT HEX(active_region_revision_id) FROM place", String.class)).isEqualTo("02".repeat(16));
			assertThat(sql.queryForList("SELECT version, checksum FROM flyway_schema_history WHERE version <> '13' ORDER BY installed_rank")).isEqualTo(checksums);
			sql.update(insert, "03", polygon);
			assertThat(sql.queryForObject("SELECT polygon FROM region_revision WHERE id=UNHEX(REPEAT('03',16))", String.class)).isEqualTo(polygon);
			assertThat(sql.queryForObject("SELECT DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='region_revision' AND COLUMN_NAME='polygon'", String.class)).isEqualTo("longtext");
		}
	}
}
