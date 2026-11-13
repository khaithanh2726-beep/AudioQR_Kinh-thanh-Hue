package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(UUID sessionId, String state, String displayName, String initialLanguage,
		String currentLanguage, Instant startedAt, Instant expiresAt, Instant endedAt,
		Instant serverNow, boolean created, boolean replayed) {
	@Override
	public String toString() {
		return "SessionResponse[redacted]";
	}
}

