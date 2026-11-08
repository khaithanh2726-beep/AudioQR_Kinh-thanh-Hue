package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminSessionResponse(UUID accountId, String loginName, String role,
		Instant lastInteractiveAt, Instant expiresAt, Instant serverNow) {
}
