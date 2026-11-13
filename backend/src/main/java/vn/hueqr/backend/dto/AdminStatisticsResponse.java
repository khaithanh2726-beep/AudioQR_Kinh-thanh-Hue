package vn.hueqr.backend.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AdminStatisticsResponse(LocalDate fromDate, LocalDate toDate, String timezone, long sessionTotal,
		long viewTotal, List<SessionsByDay> sessionsByDay, List<ViewsByPlaceLanguageDay> viewsByPlaceLanguageDay) {
	public record SessionsByDay(LocalDate date, long sessions) {
	}

	public record ViewsByPlaceLanguageDay(LocalDate date, UUID placeId, String placeName, boolean placeDeleted,
			String language, long views) {
	}
}
