package vn.hueqr.backend.dto;

public record AdminCsrfResponse(String headerName, String token) {
	@Override
	public String toString() {
		return "AdminCsrfResponse[redacted]";
	}
}
