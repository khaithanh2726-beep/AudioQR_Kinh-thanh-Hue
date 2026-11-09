package vn.hueqr.backend.service;

import java.io.Serializable;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.hueqr.backend.dto.AdminSessionResponse;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.repository.AdminAccountRepository;

@Service
public class AdminSessionService {
	private static final String STATE = AdminSessionService.class.getName() + ".state";
	private static final Duration IDLE_LIMIT = Duration.ofMinutes(30);
	private static final Set<String> ACTIVITIES = Set.of("EDIT", "DRAW", "SELECT", "VIEW_SCROLL",
			"SAVE", "APPLY", "VISIBILITY", "DELETE", "CONFIRM", "TTS_CREATE", "TTS_CHECK", "TTS_RETRY", "AUDIO_PREVIEW");
	private final AdminAccountRepository accounts;
	private final Clock clock;

	private record State(UUID accountId, Instant lastInteractiveAt) implements Serializable {
	}

	public AdminSessionService(AdminAccountRepository accounts, Clock clock) {
		this.accounts = accounts;
		this.clock = clock;
	}

	public AdminSessionResponse start(HttpServletRequest request, UUID accountId) {
		AdminAccount account = account(accountId);
		Instant now = clock.instant();
		State state = new State(accountId, now);
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval((int) IDLE_LIMIT.toSeconds());
		session.setAttribute(STATE, state);
		return response(state, account, now);
	}

	public AdminSessionResponse current(HttpServletRequest request) {
		HttpSession session = session(request);
		synchronized (session) {
			Instant now = clock.instant();
			State state = activeState(session, now);
			return response(state, account(state.accountId()), now);
		}
	}

	public AdminSessionResponse activity(HttpServletRequest request, String activityType) {
		HttpSession session = session(request);
		// Kiểm hạn và cập nhật cùng khóa, tránh hoạt động đồng thời ghi đè mốc mới bằng mốc cũ
		synchronized (session) {
			Instant now = clock.instant();
			State previous = activeState(session, now);
			AdminAccount account = account(previous.accountId());
			if (!ACTIVITIES.contains(activityType == null ? "" : activityType))
				throw new AdminSessionException("VALIDATION_FAILED");

			State updated = new State(previous.accountId(), now);
			session.setAttribute(STATE, updated);
			return response(updated, account, now);
		}
	}

	private State activeState(HttpSession session, Instant now) {
		Object value = session.getAttribute(STATE);
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(value instanceof State state) || authentication == null
				|| !state.accountId().toString().equals(authentication.getName())
				|| !now.isBefore(state.lastInteractiveAt().plus(IDLE_LIMIT)))
			throw new AdminSessionException("ADMIN_SESSION_EXPIRED");
		return state;
	}

	private HttpSession session(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			throw new AdminSessionException("ADMIN_SESSION_EXPIRED");
		return session;
	}

	private AdminAccount account(UUID id) {
		return accounts.findById(id).filter(AdminAccount::isEnabled)
				.orElseThrow(() -> new AdminSessionException("ADMIN_SESSION_EXPIRED"));
	}

	private AdminSessionResponse response(State state, AdminAccount account, Instant now) {
		return new AdminSessionResponse(account.getId(), account.getLoginName(), "ADMIN",
				state.lastInteractiveAt(), state.lastInteractiveAt().plus(IDLE_LIMIT), now);
	}
}
