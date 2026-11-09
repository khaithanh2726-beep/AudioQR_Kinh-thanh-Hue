package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminPlaceViewResponse(UUID viewId, UUID placeId, String placeName, boolean placeDeleted,
		String language, Instant viewedAt) {
}
