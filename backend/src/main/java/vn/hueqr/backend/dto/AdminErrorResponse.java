package vn.hueqr.backend.dto;

import java.util.Map;

public record AdminErrorResponse(String code, String message, Map<String, String> fieldErrors,
		boolean retryable, String correlationId) {
}
