package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

import tools.jackson.databind.JsonNode;

public record AdminRegionRevisionResponse(UUID revisionId, UUID placeId, JsonNode polygon,
		String mapSpaceKey, String geometryHash, Instant savedAt) {
}
