package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import tools.jackson.databind.JsonNode;

public record GuestMapResponse(List<MapPlace> places, Instant serverNow) {
	public record MapPlace(UUID id, String name, UUID regionRevisionId, String mapSpaceKey, JsonNode polygon) {
	}
}
