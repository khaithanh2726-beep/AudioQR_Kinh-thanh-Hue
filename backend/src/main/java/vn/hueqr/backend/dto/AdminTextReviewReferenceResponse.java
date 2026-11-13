package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminTextReviewReferenceResponse(UUID textRevisionId, String contentHash, String etag,
		String locator, String limitations, UUID recordedBy, Instant recordedAt, Long version) {
}
