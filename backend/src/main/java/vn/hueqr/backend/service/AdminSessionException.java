package vn.hueqr.backend.service;

public class AdminSessionException extends RuntimeException {
	private final String code;

	public AdminSessionException(String code) {
		super(code);
		this.code = code;
	}

	public String code() {
		return code;
	}
}
