package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record AdminGuestSessionResponse(UUID sessionId, String displayName, String initialLanguage,
		Instant startedAt, Instant expiresAt, Instant endedAt, String state, List<AdminPlaceViewResponse> views) {
	@Override
	public String toString() {
		return "AdminGuestSessionResponse[redacted]";
	}
}
