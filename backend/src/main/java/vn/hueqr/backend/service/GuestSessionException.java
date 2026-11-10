package vn.hueqr.backend.service;

import java.util.Map;

public final class GuestSessionException extends RuntimeException {
	private final String code;
	private final Map<String, String> fieldErrors;

	public GuestSessionException(String code) {
		this(code, Map.of());
	}

	public GuestSessionException(String code, Map<String, String> fieldErrors) {
		super(code);
		this.code = code;
		this.fieldErrors = Map.copyOf(fieldErrors);
	}

	public String code() {
		return code;
	}

	public Map<String, String> fieldErrors() {
		return fieldErrors;
	}
}

