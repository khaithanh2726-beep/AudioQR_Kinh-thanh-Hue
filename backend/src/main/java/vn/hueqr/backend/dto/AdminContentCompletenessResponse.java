package vn.hueqr.backend.dto;

import java.util.List;
import java.util.UUID;

public record AdminContentCompletenessResponse(UUID placeId, String displayName, String language,
		AdminContentResponse content, List<AudioCandidate> draftAudios, List<String> draftMissingConditions) {
	public record AudioCandidate(UUID audioRevisionId, UUID textRevisionId, String textHash, String mp3Checksum,
			AdminAudioReviewReferenceResponse reviewReference, List<String> missingConditions) {
	}
}
