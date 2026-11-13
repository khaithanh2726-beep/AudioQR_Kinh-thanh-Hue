package vn.hueqr.backend.dto;

import java.util.Map;
import java.util.List;

public record AdminErrorResponse(String code, String message, Map<String, String> fieldErrors,
		boolean retryable, String correlationId, List<String> missingConditions) {
	public AdminErrorResponse(String code, String message, Map<String, String> fieldErrors, boolean retryable, String correlationId) {
		this(code, message, fieldErrors, retryable, correlationId, List.of());
	}
}
