package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminAudioReviewReferenceResponse(UUID audioRevisionId, UUID textRevisionId, String language,
		String textHash, String mp3Checksum, String etag, String locator, String limitations,
		UUID recordedBy, Instant recordedAt, Long version) {
}
