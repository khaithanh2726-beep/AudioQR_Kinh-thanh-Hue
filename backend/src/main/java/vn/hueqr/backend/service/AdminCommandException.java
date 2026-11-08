package vn.hueqr.backend.service;

import java.util.Map;

public class AdminCommandException extends RuntimeException {
	private final int status;
	private final String code;
	private final Map<String, String> fields;

	public AdminCommandException(int status, String code) {
		this(status, code, Map.of());
	}

	public AdminCommandException(int status, String code, Map<String, String> fields) {
		super(code);
		this.status = status;
		this.code = code;
		this.fields = Map.copyOf(fields);
	}

	public int status() {
		return status;
	}

	public String code() {
		return code;
	}

	public Map<String, String> fields() {
		return fields;
	}
}
