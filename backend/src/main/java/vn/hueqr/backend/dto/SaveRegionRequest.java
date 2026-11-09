package vn.hueqr.backend.dto;

import tools.jackson.databind.JsonNode;

public record SaveRegionRequest(JsonNode polygon, String mapSpaceKey, String geometryHash) {
}
