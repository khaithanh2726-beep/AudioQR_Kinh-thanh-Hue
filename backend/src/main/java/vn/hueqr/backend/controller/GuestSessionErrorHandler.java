package vn.hueqr.backend.controller;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import vn.hueqr.backend.dto.GuestErrorResponse;
import vn.hueqr.backend.service.GuestSessionException;

@RestControllerAdvice(assignableTypes = GuestSessionController.class)
public class GuestSessionErrorHandler {
	@ExceptionHandler(GuestSessionException.class)
	ResponseEntity<GuestErrorResponse> business(GuestSessionException error) {
		int status = switch (error.code()) {
			case "VALIDATION_FAILED" -> 422;
			case "SERVICE_EPOCH_MISMATCH" -> 403;
			case "KEY_REUSED", "START_TICKET_EXPIRED", "SESSION_EXPIRED" -> 409;
			default -> 401;
		};
		return ResponseEntity.status(status).header("Cache-Control", "no-store")
				.body(response(error.code(), "Yêu cầu không thể thực hiện.", error.fieldErrors()));
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
	ResponseEntity<GuestErrorResponse> malformed() {
		return ResponseEntity.badRequest().header("Cache-Control", "no-store")
				.body(response("MALFORMED_REQUEST", "Cấu trúc yêu cầu không hợp lệ.", Map.of()));
	}

	private GuestErrorResponse response(String code, String message, Map<String, String> fields) {
		return new GuestErrorResponse(code, message, fields, false, UUID.randomUUID().toString());
	}
}

