package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record GuestViewAckResponse(UUID viewId, boolean created, Instant viewedAt, String language, UUID openingId) {
}
