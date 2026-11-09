package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AdminOperationResponse(UUID operationId, String operationType, int httpStatus, String resultCode,
		Map<String, Object> result, Instant committedAt, boolean replayed) {
	public AdminOperationResponse {
		// Giữ previousPairId=null cho lần áp dụng đầu tiên, vẫn sao chép và khóa Map kết quả
		result = java.util.Collections.unmodifiableMap(new java.util.LinkedHashMap<>(result));
	}
}
