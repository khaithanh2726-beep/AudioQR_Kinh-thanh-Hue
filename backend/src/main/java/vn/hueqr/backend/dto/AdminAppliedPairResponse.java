package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AdminAppliedPairResponse(UUID pairId, UUID textRevisionId, String body, String textHash,
		UUID audioRevisionId, String mp3Checksum, Instant pairCreatedAt,
		AdminTextReviewReferenceResponse textReviewReference, AdminAudioReviewReferenceResponse audioReviewReference,
		List<String> missingConditions) {
}
