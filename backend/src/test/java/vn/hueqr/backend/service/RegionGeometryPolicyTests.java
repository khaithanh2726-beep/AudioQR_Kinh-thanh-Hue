package vn.hueqr.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegionGeometryPolicyTests {
	private static final String SQUARE = "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[2,0],[2,2],[0,2],[0,0]]]}";
	private static final String SQUARE_HASH = "6c56d973f986ec5a9721110a9eca5448a0e1787fc7f4dbccd7eda891d882a501";
	private final ObjectMapper json = new ObjectMapper();
	private final RegionGeometryPolicy policy = new RegionGeometryPolicy(json);

	@Test
	void aClosedSquareKeepsItsVerticesAndMatchesThePublishedHashVector() {
		var result = policy.validate(json.readTree(SQUARE), RegionGeometryPolicy.MAP_SPACE_KEY, SQUARE_HASH);
		assertThat(result.polygon()).isEqualTo(SQUARE);
		assertThat(result.geometryHash()).isEqualTo(SQUARE_HASH);
	}

	@Test
	void objectKeyOrderAndEquivalentNumericSpellingDoNotChangeTheGeometryHash() {
		String equivalent = "{\"coordinates\":[[[-0.0,0.0],[2.00,0],[2,2e0],[0,2],[0,0]]],\"type\":\"Polygon\"}";
		assertThat(policy.validate(json.readTree(equivalent), RegionGeometryPolicy.MAP_SPACE_KEY, SQUARE_HASH).polygon())
				.isEqualTo(SQUARE);
	}

	@Test
	void anotherMapSpaceIsRejectedInsteadOfReinterpretingCoordinates() {
		AdminCommandException error = assertThrows(AdminCommandException.class,
				() -> policy.validate(json.readTree(SQUARE), "EPSG:3857", SQUARE_HASH));
		assertThat(error.status()).isEqualTo(422);
		assertThat(error.fields()).containsKey("mapSpaceKey");
	}

	@Test
	void theSuppliedPreviewHashMustMatchTheServersGeometryHash() {
		AdminCommandException error = assertThrows(AdminCommandException.class,
				() -> policy.validate(json.readTree(SQUARE), RegionGeometryPolicy.MAP_SPACE_KEY, "0".repeat(64)));
		assertThat(error.status()).isEqualTo(409);
		assertThat(error.code()).isEqualTo("GEOMETRY_HASH_MISMATCH");
	}

	@Test
	void aHashMustUseExactlySixtyFourLowercaseHexDigits() {
		for (String value : new String[]{null, "", "0".repeat(63), "g".repeat(64), SQUARE_HASH.toUpperCase()}) {
			AdminCommandException error = assertThrows(AdminCommandException.class,
					() -> policy.matches(SQUARE_HASH, value, "previewedGeometryHash"));
			assertThat(error.status()).isEqualTo(422);
			assertThat(error.fields()).containsKey("previewedGeometryHash");
		}
		assertThat(policy.matches(SQUARE_HASH, SQUARE_HASH, "previewedGeometryHash")).isTrue();
	}

	@Test
	void incompleteOrUnclosedRingsAreRejectedWithoutAddingAClosingVertex() {
		for (String coordinates : List.of("[]", "[[0,0],[2,0],[0,0]]", "[[0,0],[2,0],[2,2],[0,2]]")) {
			assertInvalid("{\"type\":\"Polygon\",\"coordinates\":[" + coordinates + "]}");
		}
	}

	@Test
	void holesMultipartShapesAndForeignGeometryMembersAreOutsideTheCurrentProfile() {
		for (String value : List.of("null", "[]", "{}", SQUARE.replace("Polygon", "MultiPolygon"),
				SQUARE.replace("\"type\"", "\"other\":0,\"type\""),
				SQUARE.replace("]]]}", "]],[[0,0],[1,0],[0,1],[0,0]]]}") )) {
			assertInvalid(value);
		}
	}

	@Test
	void coordinatesMustBeTwoFiniteNumbersWithinLongitudeAndLatitudeBounds() {
		for (String point : List.of("[181,0]", "[0,91]", "[-181,0]", "[0,-91]", "[0,0,1]",
				"[\"0\",0]", "[null,0]", "[true,0]", "{}")) {
			assertInvalid(SQUARE.replace("[2,0]", point));
		}
	}

	@Test
	void repeatedVerticesAndZeroLengthEdgesAreRejected() {
		assertInvalid(polygon("[0,0],[2,0],[2,0],[0,2],[0,0]"));
		assertInvalid(polygon("[0,0],[2,0],[2,2],[2,0],[0,2],[0,0]"));
	}

	@Test
	void selfCrossingAndNonAdjacentEdgeTouchingAreRejected() {
		assertInvalid(polygon("[0,0],[2,2],[0,2],[2,0],[0,0]"));
		assertInvalid(polygon("[0,0],[2,0],[2,2],[1,0],[0,2],[0,0]"));
	}

	@Test
	void collinearRingsAndAdjacentEdgesThatBacktrackAreRejected() {
		assertInvalid(polygon("[0,0],[1,0],[2,0],[0,0]"));
		assertInvalid(polygon("[0,0],[2,0],[1,0],[2,2],[0,2],[0,0]"));
	}

	@Test
	void validConcaveAndClockwisePolygonsKeepTheirSubmittedVertexOrder() throws Exception {
		for (String value : List.of(polygon("[0,0],[2,0],[1,1],[2,2],[0,2],[0,0]"),
				polygon("[0,0],[0,2],[2,2],[2,0],[0,0]"),
				polygon("[0,0],[1,0],[2,0],[2,2],[0,2],[0,0]"))) {
			assertThat(policy.validate(json.readTree(value), RegionGeometryPolicy.MAP_SPACE_KEY, hash(value)).polygon())
					.isEqualTo(value);
		}
	}

	@Test
	void aVerySmallNonzeroAreaIsNotRejectedByAnInventedMinimumArea() throws Exception {
		String value = polygon("[0,0],[0.000000000001,0],[0,0.000000000001],[0,0]");
		assertThat(policy.validate(json.readTree(value), RegionGeometryPolicy.MAP_SPACE_KEY, hash(value)).polygon())
				.isEqualTo(value);
	}

	@Test
	void requestCanonicalizationPreservesArrayOrderAndStringValues() {
		assertThat(policy.canonicalJson(json.readTree("{\"z\":1.00,\"a\":[true,null,\"1.00\",2]}")))
				.isEqualTo("{\"a\":[true,null,\"1.00\",2],\"z\":1}");
	}

	private void assertInvalid(String value) {
		AdminCommandException error = assertThrows(AdminCommandException.class,
				() -> policy.validate(json.readTree(value), RegionGeometryPolicy.MAP_SPACE_KEY, SQUARE_HASH));
		assertThat(error.status()).isEqualTo(422);
		assertThat(error.fields()).containsKey("polygon");
	}

	private String polygon(String points) {
		return "{\"type\":\"Polygon\",\"coordinates\":[[" + points + "]]}";
	}

	private String hash(String polygon) throws Exception {
		return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
				.digest((RegionGeometryPolicy.MAP_SPACE_KEY + "\n" + polygon).getBytes(StandardCharsets.UTF_8)));
	}
}
