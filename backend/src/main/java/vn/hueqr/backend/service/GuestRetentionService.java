package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.GuestRetentionRepository;

@Service
public class GuestRetentionService {
	public enum State { NOT_FOUND, ACTIVE, READ_IN_PROGRESS, UNCHANGED, CONTEXT_CLEARED, PURGED }

	public record Result(State state, int deletedViews) {
	}

	public record Batch(int examined, int clearedContexts, int purgedSessions, int deletedViews, int blockedReads) {
	}

	private final GuestRetentionRepository sessions;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public GuestRetentionService(GuestRetentionRepository sessions, Clock clock, PlatformTransactionManager manager) {
		this.sessions = sessions;
		this.clock = clock;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public Batch sweep(int limit) {
		if (limit < 1)
			throw new IllegalArgumentException("RETENTION_BATCH_MUST_BE_POSITIVE");
		Instant now = clock.instant().truncatedTo(ChronoUnit.MICROS);
		int examined = 0;
		int cleared = 0;
		int purged = 0;
		int views = 0;
		int blocked = 0;
		for (UUID id : sessions.candidates(now, now.minus(GuestRetentionPolicy.RETENTION), limit)) {
			Result result = clean(id);
			examined++;
			cleared += result.state() == State.CONTEXT_CLEARED ? 1 : 0;
			purged += result.state() == State.PURGED ? 1 : 0;
			views += result.deletedViews();
			blocked += result.state() == State.READ_IN_PROGRESS ? 1 : 0;
		}
		return new Batch(examined, cleared, purged, views, blocked);
	}

	public Result clean(UUID id) {
		return transaction.execute(tx -> {
			// Cùng thứ tự khóa phiên với tải audio, rồi kiểm lại đồng hồ và claim trong giao dịch mới
			var found = sessions.lock(id);
			if (found.isEmpty())
				return new Result(State.NOT_FOUND, 0);
			var session = found.get();
			// Cắt đến micro giây để so mốc dọn đúng độ chính xác DATETIME(6)
			Instant now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			Instant anchor = GuestRetentionPolicy.anchor(session.startedAt(), session.expiresAt(), session.endedAt(), now);
			if (now.isBefore(anchor))
				return new Result(State.ACTIVE, 0);
			// Claim không có TTL, chỉ stream đóng hoặc đối chiếu tiến trình mới cho phép nhả
			if (sessions.hasReadClaim(id))
				return new Result(State.READ_IN_PROGRESS, 0);
			boolean purge = !now.isBefore(anchor.plus(GuestRetentionPolicy.RETENTION));
			if (!purge && !session.hasContext())
				return new Result(State.UNCHANGED, 0);

			sessions.clearContext(id);
			if (!purge)
				return new Result(State.CONTEXT_CLEARED, 0);
			int views = sessions.deleteViews(id);
			sessions.deleteSession(id);
			return new Result(State.PURGED, views);
		});
	}
}
