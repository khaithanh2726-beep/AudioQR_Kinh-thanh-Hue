package vn.hueqr.backend.service;

import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.dto.SessionResponse;
import vn.hueqr.backend.dto.StartRequest;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.service.StartTicketCodec.Payload;

@Service
public class GuestSessionService {
	private final GuestSessionRepository sessions;
	private final StartTicketService tickets;
	private final StartTicketTimePolicy time;
	private final GuestInputPolicy input;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public GuestSessionService(GuestSessionRepository sessions, StartTicketService tickets,
			StartTicketTimePolicy time, GuestInputPolicy input, Clock clock, PlatformTransactionManager manager) {
		this.sessions = sessions;
		this.tickets = tickets;
		this.time = time;
		this.input = input;
		this.clock = clock;

		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}

	public SessionResponse start(StartRequest request, String keyHeader, String credential) {
		byte[] credentialHash = input.credentialHash(credential);
		UUID key = input.requestKey(keyHeader);
		Payload payload = tickets.verify(request.startTicket(), key, credentialHash);
		String name = input.displayName(request.displayName());
		String language = input.language(request.initialLanguage());
		byte[] digest = input.ticketDigest(request.startTicket());
		byte[] fingerprint = input.fingerprint(name, language, digest);

		try {
			return transaction.execute(status -> {
				// Tra kết quả đã ghi trước khi xét hạn tạo mới để yêu cầu gửi lại vẫn nhận đúng phiên
				var existing = sessions.findByStartRequestKey(key);
				if (existing.isPresent())
					return replay(existing.get(), payload, fingerprint, digest);

				if (time.isExpiredForFirstCreation(payload.times()))
					throw new GuestSessionException("START_TICKET_EXPIRED");

				Instant startedAt = clock.instant().truncatedTo(ChronoUnit.MICROS);
				// Kiểm lại hạn vé vì thời gian có thể đã trôi qua sau lần kiểm trước
				if (!startedAt.isBefore(payload.times().ticketExpiresAt()))
					throw new GuestSessionException("START_TICKET_EXPIRED");

				GuestSession session = new GuestSession(UUID.randomUUID(), key, credentialHash,
						payload.serviceEpoch().toString(), fingerprint, payload.times().issuedAt(),
						payload.times().ticketExpiresAt(), digest, name, language, startedAt,
						startedAt.plus(Duration.ofHours(8)));
				sessions.saveAndFlush(session);
				return response(session, true, false);
			});
		}
		catch (DataIntegrityViolationException collision) {
			if (!isStartUniqueCollision(collision))
				throw collision;

			// Đọc lại phiên trong giao dịch mới sau khi giao dịch tranh chấp khóa duy nhất đã hoàn tác
			return transaction.execute(status -> sessions.findByStartRequestKey(key)
					.map(session -> replay(session, payload, fingerprint, digest))
					.orElseThrow(() -> new GuestSessionException("KEY_REUSED")));
		}
	}

	private static boolean isStartUniqueCollision(Throwable error) {
		for (Throwable cause = error; cause != null; cause = cause.getCause()) {
			if (cause instanceof java.sql.SQLException sql && sql.getErrorCode() == 1062
					&& sql.getMessage() != null && (sql.getMessage().contains("uk_guest_session_start_request_key")
					|| sql.getMessage().contains("uk_guest_session_credential_hash")))
				return true;
		}

		return false;
	}

	private SessionResponse replay(GuestSession session, Payload payload, byte[] fingerprint, byte[] digest) {
		if (!MessageDigest.isEqual(session.getCredentialHash(), payload.credentialHash())
				|| !MessageDigest.isEqual(session.getStartFingerprint(), fingerprint)
				|| !MessageDigest.isEqual(session.getStartTicketDigest(), digest)
				|| !session.getServiceEpoch().equals(payload.serviceEpoch().toString())
				|| !session.getStartTicketIssuedAt().equals(payload.times().issuedAt())
				|| !session.getStartTicketExpiresAt().equals(payload.times().ticketExpiresAt()))
			throw new GuestSessionException("KEY_REUSED");

		return response(session, false, true);
	}

	public SessionResponse status(UUID id, String credential) {
		byte[] hash = input.credentialHash(credential);

		return transaction.execute(status -> {
			GuestSession session = sessions.findById(id)
					.orElseThrow(() -> new GuestSessionException("GUEST_CREDENTIAL_INVALID"));

			if (!MessageDigest.isEqual(hash, session.getCredentialHash())
					|| !tickets.currentEpoch(session.getServiceEpoch()))
				throw new GuestSessionException("GUEST_CREDENTIAL_INVALID");

			return response(session, false, false);
		});
	}

	public SessionResponse end(UUID id, String keyHeader, String credential) {
		byte[] credentialHash = input.credentialHash(credential);
		// Mỗi phiên chỉ có một lần kết thúc, endedAt đã ghi là kết quả dùng lại cho K04
		input.requestKey(keyHeader);

		return transaction.execute(status -> {
			// Khóa phiên để các yêu cầu kết thúc đồng thời cùng dùng một mốc đã ghi
			GuestSession session = sessions.findByIdForUpdate(id)
					.orElseThrow(() -> new GuestSessionException("GUEST_CREDENTIAL_INVALID"));
			if (!MessageDigest.isEqual(credentialHash, session.getCredentialHash())
					|| !tickets.currentEpoch(session.getServiceEpoch()))
				throw new GuestSessionException("GUEST_CREDENTIAL_INVALID");

			// Phiên đã kết thúc trả lại mốc cũ, kể cả lần gửi lại diễn ra sau hạn phiên
			if (session.getEndedAt() != null)
				return response(session, false, true);

			Instant now = clock.instant();
			if (!now.isBefore(session.getExpiresAt()))
				throw new GuestSessionException("SESSION_EXPIRED");

			// Cắt đến micro giây để endedAt trả về khớp DATETIME(6) khi đọc lại
			session.endAt(now.truncatedTo(ChronoUnit.MICROS));
			sessions.saveAndFlush(session);
			return response(session, false, false);
		});
	}

	private SessionResponse response(GuestSession session, boolean created, boolean replayed) {
		Instant now = clock.instant();
		String state = session.getEndedAt() != null ? "ENDED"
				: !now.isBefore(session.getExpiresAt()) ? "EXPIRED" : "ACTIVE";
		return new SessionResponse(session.getId(), state, session.getDisplayName(), session.getInitialLanguage(),
				"ACTIVE".equals(state) ? session.getInitialLanguage() : null, session.getStartedAt(),
				session.getExpiresAt(), session.getEndedAt(), now, created, replayed);
	}
}

