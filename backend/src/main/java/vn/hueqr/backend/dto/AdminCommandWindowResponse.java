package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminCommandWindowResponse(UUID windowId, String serviceEpoch, Instant issuedAt, Instant expiresAt) {
}
