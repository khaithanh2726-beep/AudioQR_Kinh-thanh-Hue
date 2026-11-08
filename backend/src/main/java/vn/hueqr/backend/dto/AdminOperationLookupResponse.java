package vn.hueqr.backend.dto;

import java.time.Instant;

public record AdminOperationLookupResponse(String state, AdminOperationResponse operation, Instant serverNow) {
}
