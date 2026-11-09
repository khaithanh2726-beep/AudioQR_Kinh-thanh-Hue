package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hueqr.backend.dto.AdminStatisticsResponse;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.entity.PlaceView;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.repository.PlaceViewRepository;

@Service
public class AdminStatisticsService {
	private record Group(LocalDate date, UUID placeId, String language) {
	}

	private static final ZoneId ZONE = ZoneId.of("Asia/Saigon");
	private final GuestSessionRepository sessions;
	private final PlaceViewRepository views;
	private final AdminDateRange dates;
	private final Clock clock;

	public AdminStatisticsService(GuestSessionRepository sessions, PlaceViewRepository views, AdminDateRange dates, Clock clock) {
		this.sessions = sessions;
		this.views = views;
		this.dates = dates;
		this.clock = clock;
	}

	// Cùng snapshot đọc để hai tổng và hai nhóm không lệch nhau nếu lượt mới commit giữa các truy vấn
	@Transactional(readOnly = true, isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
	public AdminStatisticsResponse read(String from, String to) {
		var range = dates.parse(from, to);
		Instant cutoff = clock.instant().minus(Duration.ofDays(30));
		Specification<GuestSession> sessionFilter = (root, query, cb) -> cb.and(retained(root, cb, cutoff),
				cb.greaterThanOrEqualTo(root.get("startedAt"), range.start()), cb.lessThan(root.get("startedAt"), range.endExclusive()));
		var sessionRows = sessions.findAll(sessionFilter);
		Map<LocalDate, Long> byDay = new TreeMap<>();
		for (var row : sessionRows)
			byDay.merge(row.getStartedAt().atZone(ZONE).toLocalDate(), 1L, Long::sum);

		Specification<PlaceView> viewFilter = (root, query, cb) -> cb.and(retained(root.join("session"), cb, cutoff),
				cb.greaterThanOrEqualTo(root.get("viewedAt"), range.start()), cb.lessThan(root.get("viewedAt"), range.endExclusive()));
		var viewRows = views.findAll(viewFilter, Sort.by("viewedAt", "id"));
		Map<Group, AdminStatisticsResponse.ViewsByPlaceLanguageDay> groups = new LinkedHashMap<>();
		for (var row : viewRows) {
			var group = new Group(row.getViewedAt().atZone(ZONE).toLocalDate(), row.getPlaceKeySnapshot(), row.getLanguage());
			var previous = groups.get(group);
			// Tên ở lượt mới nhất trong nhóm dùng làm nhãn, khóa gộp luôn là Id snapshot ổn định
			groups.put(group, new AdminStatisticsResponse.ViewsByPlaceLanguageDay(group.date(), group.placeId(), row.getPlaceNameSnapshot(),
					row.getLivePlaceId() == null, group.language(), previous == null ? 1 : previous.views() + 1));
		}
		var ordered = groups.values().stream().sorted(Comparator.comparing(AdminStatisticsResponse.ViewsByPlaceLanguageDay::date)
				.thenComparing(v -> v.placeId().toString()).thenComparing(AdminStatisticsResponse.ViewsByPlaceLanguageDay::language)).toList();
		return new AdminStatisticsResponse(range.fromDate(), range.toDate(), "Asia/Saigon", sessionRows.size(), viewRows.size(),
				byDay.entrySet().stream().map(e -> new AdminStatisticsResponse.SessionsByDay(e.getKey(), e.getValue())).toList(), ordered);
	}

	private Predicate retained(From<?, ?> root, CriteriaBuilder cb, Instant cutoff) {
		return cb.or(cb.and(cb.isNotNull(root.get("endedAt")), cb.greaterThan(root.get("endedAt"), cutoff)),
				cb.and(cb.isNull(root.get("endedAt")), cb.greaterThan(root.get("expiresAt"), cutoff)));
	}
}
