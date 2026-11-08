package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminPlaceResponse(UUID placeId, String displayName, boolean visible, UUID activeRegionRevisionId,
		long version, Instant createdAt, Instant updatedAt) {
}
