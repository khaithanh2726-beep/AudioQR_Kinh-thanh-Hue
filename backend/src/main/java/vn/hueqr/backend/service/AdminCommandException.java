package vn.hueqr.backend.service;

import java.util.Map;
import java.util.List;

public class AdminCommandException extends RuntimeException {
	private final int status;
	private final String code;
	private final Map<String, String> fields;
	private final List<String> missingConditions;

	public AdminCommandException(int status, String code) {
		this(status, code, Map.of());
	}

	public AdminCommandException(int status, String code, Map<String, String> fields) {
		this(status, code, fields, List.of());
	}

	public AdminCommandException(int status, String code, Map<String, String> fields, List<String> missingConditions) {
		super(code);
		this.status = status;
		this.code = code;
		this.fields = Map.copyOf(fields);
		this.missingConditions = List.copyOf(missingConditions);
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

	public List<String> missingConditions() {
		return missingConditions;
	}
}
