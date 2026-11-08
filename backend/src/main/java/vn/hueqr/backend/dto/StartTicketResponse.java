package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record StartTicketResponse(String startTicket, UUID requestId, Instant issuedAt,
		Instant ticketExpiresAt, UUID serviceEpoch, Instant serverNow) {
	@Override
	public String toString() {
		return "StartTicketResponse[redacted]";
	}
}

