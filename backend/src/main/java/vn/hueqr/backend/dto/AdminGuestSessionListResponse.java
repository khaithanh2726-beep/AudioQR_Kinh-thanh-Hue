package vn.hueqr.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record AdminGuestSessionListResponse(LocalDate fromDate, LocalDate toDate, String timezone,
		List<AdminGuestSessionResponse> sessions) {
	@Override
	public String toString() {
		return "AdminGuestSessionListResponse[redacted]";
	}
}
