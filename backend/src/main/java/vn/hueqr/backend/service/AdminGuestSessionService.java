package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hueqr.backend.dto.AdminGuestSessionListResponse;
import vn.hueqr.backend.dto.AdminGuestSessionResponse;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.repository.PlaceViewRepository;
import vn.hueqr.backend.dto.AdminPlaceViewResponse;

@Service
@Transactional(readOnly = true)
public class AdminGuestSessionService {
	private final GuestSessionRepository sessions;
	private final AdminDateRange dates;
	private final Clock clock;
	private final PlaceViewRepository views;

	public AdminGuestSessionService(GuestSessionRepository sessions, AdminDateRange dates, Clock clock, PlaceViewRepository views) {
		this.sessions = sessions;
		this.dates = dates;
		this.clock = clock;
		this.views = views;
	}

	public AdminGuestSessionListResponse list(String from, String to, String status, String search) {
		var range = dates.parse(from, to);
		if (status != null && !Set.of("ALL", "ACTIVE", "ENDED", "EXPIRED").contains(status))
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("status", "Cần ALL, ACTIVE, ENDED hoặc EXPIRED."));
		Instant now = clock.instant();
		Instant cutoff = now.minus(Duration.ofDays(30));
		Specification<GuestSession> filter = (root, query, cb) -> {
			var ended = root.<Instant>get("endedAt");
			var expires = root.<Instant>get("expiresAt");
			var retained = cb.or(cb.and(cb.isNotNull(ended), cb.greaterThan(ended, cutoff)),
					cb.and(cb.isNull(ended), cb.greaterThan(expires, cutoff)));
			var selected = cb.and(retained, cb.greaterThanOrEqualTo(root.get("startedAt"), range.start()),
					cb.lessThan(root.get("startedAt"), range.endExclusive()));
			if ("ENDED".equals(status))
				selected = cb.and(selected, cb.isNotNull(ended));
			else if ("ACTIVE".equals(status))
				selected = cb.and(selected, cb.isNull(ended), cb.greaterThan(expires, now));
			else if ("EXPIRED".equals(status))
				selected = cb.and(selected, cb.isNull(ended), cb.lessThanOrEqualTo(expires, now));
			if (search != null && !search.isBlank()) {
				String term = search.strip().toLowerCase(Locale.ROOT);
				String literal = term.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
				var byName = cb.like(cb.lower(root.get("displayName")), "%" + literal + "%", '\\');
				String hex = term.replace("-", "");
				var byId = hex.matches("[0-9a-f]+")
						? cb.like(cb.lower(cb.function("hex", String.class, root.get("id"))), "%" + hex + "%") : cb.disjunction();
				selected = cb.and(selected, cb.or(byName, byId));
			}
			return selected;
		};
		var data = sessions.findAll(filter, Sort.by("startedAt", "id")).stream().map(s -> response(s, now)).toList();
		return new AdminGuestSessionListResponse(range.fromDate(), range.toDate(), "Asia/Saigon", data);
	}

	public AdminGuestSessionResponse detail(String id) {
		UUID sid;
		try {
			sid = UUID.fromString(id);
			if (!sid.toString().equals(id))
				throw new IllegalArgumentException();
		}
		catch (IllegalArgumentException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("sessionId", "Cần mã phiên UUID chuẩn chữ thường."));
		}
		var session = sessions.findById(sid).orElseThrow(() -> new AdminCommandException(404, "DATA_NOT_RETAINED"));
		Instant now = clock.instant();
		Instant anchor = session.getEndedAt() == null ? session.getExpiresAt() : session.getEndedAt();
		// Không trả dữ liệu quá hạn D08 dù tác vụ dọn vật lý chưa chạy
		if (!now.isBefore(anchor.plus(Duration.ofDays(30))))
			throw new AdminCommandException(404, "DATA_NOT_RETAINED");
		var base = response(session, now);
		var rows = views.findBySession_IdOrderByViewedAtAscIdAsc(sid).stream().map(v -> new AdminPlaceViewResponse(
				v.getId(), v.getPlaceKeySnapshot(), v.getPlaceNameSnapshot(), v.getLivePlaceId() == null, v.getLanguage(), v.getViewedAt())).toList();
		return new AdminGuestSessionResponse(base.sessionId(), base.displayName(), base.initialLanguage(), base.startedAt(),
				base.expiresAt(), base.endedAt(), base.state(), rows);
	}

	private AdminGuestSessionResponse response(GuestSession session, Instant now) {
		String state = session.getEndedAt() != null ? "ENDED" : now.isBefore(session.getExpiresAt()) ? "ACTIVE" : "EXPIRED";
		return new AdminGuestSessionResponse(session.getId(), session.getDisplayName(), session.getInitialLanguage(),
				session.getStartedAt(), session.getExpiresAt(), session.getEndedAt(), state, null);
	}
}
