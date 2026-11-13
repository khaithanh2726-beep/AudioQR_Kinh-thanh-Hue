package vn.hueqr.backend.service;

import java.util.Map;
import java.util.UUID;

final class AdminPlacePreconditions {
	private AdminPlacePreconditions() {
	}

	static UUID placeId(String value) {
		return id(value, "placeId", "Cần mã địa điểm UUID chuẩn chữ thường.");
	}

	static UUID revisionId(String value, String field) {
		return id(value, field, "Cần mã bản vùng UUID chuẩn chữ thường.");
	}

	static long version(String ifMatch) {
		if (ifMatch == null || ifMatch.isBlank())
			throw new AdminCommandException(400, "PRECONDITION_REQUIRED", Map.of("If-Match", "Cần phiên bản địa điểm đang chỉnh."));
		try {
			// Chỉ nhận một ETag mạnh để đối chiếu đúng phiên bản đang chỉnh
			if (!ifMatch.matches("\"(0|[1-9][0-9]*)\""))
				throw new IllegalArgumentException();
			return Long.parseLong(ifMatch.substring(1, ifMatch.length() - 1));
		}
		catch (IllegalArgumentException error) {
			throw new AdminCommandException(400, "VALIDATION_FAILED", Map.of("If-Match", "Cần một ETag mạnh dạng số trong dấu ngoặc kép."));
		}
	}

	private static UUID id(String value, String field, String message) {
		try {
			UUID id = UUID.fromString(value);
			if (!id.toString().equals(value))
				throw new IllegalArgumentException();
			return id;
		}
		catch (IllegalArgumentException | NullPointerException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of(field, message));
		}
	}
}
