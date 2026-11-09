package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record AdminPlaceResponse(UUID placeId, String displayName, boolean visible, UUID activeRegionRevisionId,
		long version, Instant createdAt, Instant updatedAt, boolean canShow, List<String> missingConditions) {
}
