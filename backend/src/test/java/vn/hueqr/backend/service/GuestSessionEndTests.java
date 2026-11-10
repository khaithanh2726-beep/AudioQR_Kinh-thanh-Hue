package vn.hueqr.backend.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GuestSessionEndTests {
	private static final String EPOCH = "00000000-0000-0000-0000-000000000001";

	private final GuestInputPolicy input = new GuestInputPolicy();
	private GuestSessionRepository sessions;
	private StartTicketService tickets;
	private MutableClock clock;
	private GuestSessionService service;
	private GuestSession session;
	private String credential;
	private String key;

	@BeforeEach
	void prepareSession() {
		sessions = mock(GuestSessionRepository.class);
		tickets = mock(StartTicketService.class);
		PlatformTransactionManager manager = mock(PlatformTransactionManager.class);
		when(manager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
		when(tickets.currentEpoch(EPOCH)).thenReturn(true);

		clock = new MutableClock();
		Instant startedAt = GuestSessionTestClock.BASE;
		clock.set(startedAt.plusSeconds(60).plusNanos(789));
		credential = credential(1);
		key = UUID.randomUUID().toString();
		session = new GuestSession(UUID.randomUUID(), UUID.randomUUID(), input.credentialHash(credential),
				EPOCH, new byte[32], startedAt, startedAt.plus(Duration.ofHours(24)),
				new byte[32], "An Bình", "vi", startedAt, startedAt.plus(Duration.ofHours(8)));
		when(sessions.findByIdForUpdate(session.getId())).thenReturn(Optional.of(session));
		service = new GuestSessionService(sessions, tickets, new StartTicketTimePolicy(clock), input, clock, manager);
	}

	@Test
	void endsActiveSessionAtServerMicrosecondsAndKeepsOriginalTimes() {
		var ended = service.end(session.getId(), key, credential);

		assertThat(ended.state()).isEqualTo("ENDED");
		assertThat(ended.endedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertThat(ended.startedAt()).isEqualTo(session.getStartedAt());
		assertThat(ended.expiresAt()).isEqualTo(session.getExpiresAt());
		assertThat(ended.currentLanguage()).isNull();
		assertThat(ended.created()).isFalse();
		assertThat(ended.replayed()).isFalse();
		verify(sessions).findByIdForUpdate(session.getId());
		verify(sessions).saveAndFlush(session);
	}

	@Test
	void replayAfterOriginalExpiryKeepsTheFirstEndTime() {
		var first = service.end(session.getId(), key, credential);
		clock.set(session.getExpiresAt().plusSeconds(1));

		var again = service.end(session.getId(), key, credential);

		assertThat(again.endedAt()).isEqualTo(first.endedAt());
		assertThat(again.expiresAt()).isEqualTo(first.expiresAt());
		assertThat(again.state()).isEqualTo("ENDED");
		assertThat(again.replayed()).isTrue();
		verify(sessions, times(1)).saveAndFlush(session);
	}

	@Test
	void anotherValidKeyForAnEndedSessionKeepsTheCommittedEndTime() {
		var first = service.end(session.getId(), key, credential);
		clock.set(clock.instant().plusSeconds(10));

		var again = service.end(session.getId(), UUID.randomUUID().toString(), credential);

		assertThat(again.endedAt()).isEqualTo(first.endedAt());
		assertThat(again.replayed()).isTrue();
		verify(sessions, times(1)).saveAndFlush(session);
	}

	@Test
	void acceptsOneNanosecondBeforeTheDeadline() {
		clock.set(session.getExpiresAt().minusNanos(1));

		var ended = service.end(session.getId(), key, credential);

		assertThat(ended.endedAt()).isEqualTo(session.getExpiresAt().minusNanos(1_000));
		assertThat(ended.state()).isEqualTo("ENDED");
	}

	@Test
	void rejectsAtAndAfterTheDeadlineWithoutWritingAnEndTime() {
		for (Instant now : new Instant[]{session.getExpiresAt(), session.getExpiresAt().plusNanos(1)}) {
			clock.set(now);

			assertThatThrownBy(() -> service.end(session.getId(), key, credential))
					.isInstanceOf(GuestSessionException.class).hasMessage("SESSION_EXPIRED");
			assertThat(session.getEndedAt()).isNull();
		}
		verify(sessions, never()).saveAndFlush(any());
	}

	@Test
	void checksTheCurrentTimeAfterAcquiringTheSessionLock() {
		// Cho thời gian vượt hạn ngay khi Repository trả phiên đã khóa
		when(sessions.findByIdForUpdate(session.getId())).thenAnswer(invocation -> {
			clock.set(session.getExpiresAt());
			return Optional.of(session);
		});

		assertThatThrownBy(() -> service.end(session.getId(), key, credential))
				.isInstanceOf(GuestSessionException.class).hasMessage("SESSION_EXPIRED");
		assertThat(session.getEndedAt()).isNull();
		verify(sessions, never()).saveAndFlush(any());
	}

	@Test
	void rejectsAnotherTabsCredentialWithoutChangingTheSession() {
		assertThatThrownBy(() -> service.end(session.getId(), key, credential(2)))
				.isInstanceOf(GuestSessionException.class).hasMessage("GUEST_CREDENTIAL_INVALID");

		assertThat(session.getEndedAt()).isNull();
		verify(sessions, never()).saveAndFlush(any());
	}

	@Test
	void rejectsMissingCredentialBeforeAccessingSessionData() {
		assertThatThrownBy(() -> service.end(session.getId(), key, null))
				.isInstanceOf(GuestSessionException.class).hasMessage("GUEST_CREDENTIAL_INVALID");

		verifyNoInteractions(sessions);
	}

	@Test
	void rejectsMissingOrMalformedKeysBeforeAccessingSessionData() {
		for (String invalid : new String[]{null, "", "invalid", "1-1-1-1-1"}) {
			assertThatThrownBy(() -> service.end(session.getId(), invalid, credential))
					.isInstanceOf(GuestSessionException.class).hasMessage("VALIDATION_FAILED");
		}
		verifyNoInteractions(sessions);
	}

	@Test
	void rejectsMissingSessionWithTheCredentialError() {
		UUID missingId = UUID.randomUUID();

		assertThatThrownBy(() -> service.end(missingId, key, credential))
				.isInstanceOf(GuestSessionException.class).hasMessage("GUEST_CREDENTIAL_INVALID");

		verify(sessions, never()).saveAndFlush(any());
	}

	@Test
	void rejectsOldEpochBeforeChangingAnActiveSession() {
		when(tickets.currentEpoch(EPOCH)).thenReturn(false);

		assertThatThrownBy(() -> service.end(session.getId(), key, credential))
				.isInstanceOf(GuestSessionException.class).hasMessage("GUEST_CREDENTIAL_INVALID");
		assertThat(session.getEndedAt()).isNull();
		verify(sessions, never()).saveAndFlush(any());
	}

	@Test
	void checksCredentialAndEpochBeforeReplayingAnEndedSession() {
		service.end(session.getId(), key, credential);

		assertThatThrownBy(() -> service.end(session.getId(), key, credential(2)))
				.isInstanceOf(GuestSessionException.class).hasMessage("GUEST_CREDENTIAL_INVALID");

		when(tickets.currentEpoch(EPOCH)).thenReturn(false);
		assertThatThrownBy(() -> service.end(session.getId(), key, credential))
				.isInstanceOf(GuestSessionException.class).hasMessage("GUEST_CREDENTIAL_INVALID");
		verify(sessions, times(1)).saveAndFlush(session);
	}

	private static String credential(int marker) {
		byte[] bytes = new byte[32];
		Arrays.fill(bytes, (byte) marker);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
