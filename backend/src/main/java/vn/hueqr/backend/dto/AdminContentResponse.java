package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminContentResponse(UUID placeId, String language, UUID slotId, Long version, String etag,
		SavedText draftText, AdminAppliedPairResponse appliedPair) {
	public record SavedText(UUID textRevisionId, String body, String contentHash, Instant savedAt,
			AdminTextReviewReferenceResponse reviewReference, TtsJobResponse latestTtsJob) {
	}
}
