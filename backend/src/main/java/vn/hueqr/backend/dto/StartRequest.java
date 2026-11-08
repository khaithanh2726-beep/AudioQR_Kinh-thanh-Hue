package vn.hueqr.backend.dto;

public record StartRequest(String startTicket, String displayName, String initialLanguage) {
	@Override
	public String toString() {
		return "StartRequest[redacted]";
	}
}

