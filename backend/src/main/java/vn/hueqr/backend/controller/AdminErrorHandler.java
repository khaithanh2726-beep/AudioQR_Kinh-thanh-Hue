package vn.hueqr.backend.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.hueqr.backend.dto.AdminErrorResponse;
import vn.hueqr.backend.service.AdminSessionException;
import vn.hueqr.backend.service.AdminCommandException;

@RestControllerAdvice(assignableTypes = {AdminAuthController.class, AdminCommandController.class, AdminPlaceController.class})
public class AdminErrorHandler {
	@ExceptionHandler(AuthenticationException.class)
	ResponseEntity<AdminErrorResponse> authentication() {
		return response(401, "ADMIN_LOGIN_FAILED", Map.of());
	}

	@ExceptionHandler(AdminSessionException.class)
	ResponseEntity<AdminErrorResponse> session(AdminSessionException error) {
		boolean invalidActivity = "VALIDATION_FAILED".equals(error.code());
		return response(invalidActivity ? 422 : 401, error.code(), invalidActivity
				? Map.of("activityType", "Loại hoạt động không hợp lệ.") : Map.of());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<AdminErrorResponse> malformed() {
		return response(400, "MALFORMED_REQUEST", Map.of());
	}

	@ExceptionHandler(AdminCommandException.class)
	ResponseEntity<AdminErrorResponse> command(AdminCommandException error) {
		return response(error.status(), error.code(), error.fields());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<AdminErrorResponse> unexpected() {
		return response(500, "INTERNAL_ERROR", Map.of());
	}

	private ResponseEntity<AdminErrorResponse> response(int status, String code, Map<String, String> fields) {
		return ResponseEntity.status(status).cacheControl(CacheControl.noStore())
				.body(new AdminErrorResponse(code, "Yêu cầu không thể thực hiện.", fields, false, UUID.randomUUID().toString()));
	}
}
