package vn.hueqr.backend.dto;

public record AdminLoginRequest(String loginName, String password) {
	@Override
	public String toString() {
		return "AdminLoginRequest[redacted]";
	}
}
