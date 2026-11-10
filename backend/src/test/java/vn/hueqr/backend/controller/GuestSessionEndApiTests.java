package vn.hueqr.backend.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.SessionResponse;
import vn.hueqr.backend.dto.StartRequest;
import vn.hueqr.backend.dto.StartTicketResponse;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class GuestSessionEndApiTests {
	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper json;
	@Autowired
	GuestSessionRepository repository;
	@Autowired
	MutableClock clock;

	@BeforeEach
	void resetFixture() {
		repository.deleteAll();
		clock.set(GuestSessionTestClock.BASE);
	}

	@Test
	void endsOnlyTheRequestedSessionAtServerTimeAndStatusReportsEnded() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);
		clock.set(active.startedAt().plusSeconds(30).plusNanos(789));

		SessionResponse ended = read(end(active.sessionId(), credential, UUID.randomUUID().toString())
				.andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store")));

		assertThat(ended.state()).isEqualTo("ENDED");
		assertThat(ended.endedAt()).isEqualTo(active.startedAt().plusSeconds(30));
		assertThat(ended.startedAt()).isEqualTo(active.startedAt());
		assertThat(ended.expiresAt()).isEqualTo(active.expiresAt());
		assertThat(ended.currentLanguage()).isNull();
		assertThat(ended.created()).isFalse();
		assertThat(ended.replayed()).isFalse();
		assertThat(repository.findById(active.sessionId()).orElseThrow().getEndedAt()).isEqualTo(ended.endedAt());

		mvc.perform(get("/api/v1/guest/sessions/" + active.sessionId()).header("X-Guest-Credential", credential))
				.andExpect(status().isOk()).andExpect(jsonPath("$.state").value("ENDED"))
				.andExpect(jsonPath("$.endedAt").value(ended.endedAt().toString()))
				.andExpect(jsonPath("$.currentLanguage").doesNotExist());
	}

	@Test
	void repeatedEndAndStartReplayAfterExpiryKeepTheCommittedEndTime() throws Exception {
		String credential = credential(1);
		StartTicketResponse ticket = issue(credential);
		SessionResponse active = start(ticket, credential, 201);
		String key = UUID.randomUUID().toString();
		SessionResponse first = read(end(active.sessionId(), credential, key).andExpect(status().isOk()));
		clock.set(active.expiresAt().plusSeconds(1));

		SessionResponse again = read(end(active.sessionId(), credential, key).andExpect(status().isOk()));
		SessionResponse startReplay = start(ticket, credential, 200);

		assertThat(again.endedAt()).isEqualTo(first.endedAt());
		assertThat(again.replayed()).isTrue();
		assertThat(startReplay.state()).isEqualTo("ENDED");
		assertThat(startReplay.sessionId()).isEqualTo(first.sessionId());
		assertThat(startReplay.endedAt()).isEqualTo(first.endedAt());
		assertThat(startReplay.expiresAt()).isEqualTo(active.expiresAt());
		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void anotherValidEndKeyReturnsTheSameEndTime() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);
		SessionResponse first = read(end(active.sessionId(), credential, UUID.randomUUID().toString())
				.andExpect(status().isOk()));
		clock.set(clock.instant().plusSeconds(20));

		SessionResponse again = read(end(active.sessionId(), credential, UUID.randomUUID().toString())
				.andExpect(status().isOk()));

		assertThat(again.endedAt()).isEqualTo(first.endedAt());
		assertThat(again.replayed()).isTrue();
	}

	@Test
	void oneNanosecondBeforeExpiryEndsWithDatabaseMicrosecondPrecision() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);
		clock.set(active.expiresAt().minusNanos(1));

		SessionResponse ended = read(end(active.sessionId(), credential, UUID.randomUUID().toString())
				.andExpect(status().isOk()));

		assertThat(ended.endedAt()).isEqualTo(active.expiresAt().minusNanos(1_000));
		assertThat(repository.findById(active.sessionId()).orElseThrow().getEndedAt()).isEqualTo(ended.endedAt());
		assertThat(ended.expiresAt()).isEqualTo(active.expiresAt());
	}

	@Test
	void atAndAfterExpiryReturnConflictAndLeaveEndedAtNull() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);

		for (Instant now : new Instant[]{active.expiresAt(), active.expiresAt().plusNanos(1)}) {
			clock.set(now);
			end(active.sessionId(), credential, UUID.randomUUID().toString())
					.andExpect(status().isConflict()).andExpect(header().string("Cache-Control", "no-store"))
					.andExpect(jsonPath("$.code").value("SESSION_EXPIRED"))
					.andExpect(jsonPath("$.retryable").value(false)).andExpect(jsonPath("$.correlationId").isNotEmpty());
			assertThat(repository.findById(active.sessionId()).orElseThrow().getEndedAt()).isNull();
		}

		mvc.perform(get("/api/v1/guest/sessions/" + active.sessionId()).header("X-Guest-Credential", credential))
				.andExpect(status().isOk()).andExpect(jsonPath("$.state").value("EXPIRED"))
				.andExpect(jsonPath("$.endedAt").doesNotExist());
	}

	@Test
	void missingWrongAndUnknownCredentialsDoNotExposeOrChangeSessionData() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);
		String key = UUID.randomUUID().toString();

		for (String invalid : new String[]{null, "invalid", credential(2)}) {
			end(active.sessionId(), invalid, key)
					.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("GUEST_CREDENTIAL_INVALID"))
					.andExpect(jsonPath("$.displayName").doesNotExist()).andExpect(jsonPath("$.endedAt").doesNotExist());
		}
		end(UUID.randomUUID(), credential, key).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("GUEST_CREDENTIAL_INVALID"));

		assertThat(repository.findById(active.sessionId()).orElseThrow().getEndedAt()).isNull();
	}

	@Test
	void wrongCredentialCannotReadTheResultOfAnEndedSession() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);
		String key = UUID.randomUUID().toString();
		end(active.sessionId(), credential, key).andExpect(status().isOk());

		end(active.sessionId(), credential(2), key).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.displayName").doesNotExist()).andExpect(jsonPath("$.endedAt").doesNotExist());
	}

	@Test
	void missingOrMalformedEndKeysReturnValidationErrorsWithoutEnding() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);

		for (String invalid : new String[]{null, "invalid", "1-1-1-1-1", "ABCDEF01-2345-6789-ABCD-0123456789AB"}) {
			end(active.sessionId(), credential, invalid).andExpect(status().is(422))
					.andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
					.andExpect(jsonPath("$.fieldErrors['Idempotency-Key']").isNotEmpty());
		}
		assertThat(repository.findById(active.sessionId()).orElseThrow().getEndedAt()).isNull();
	}

	@Test
	void nonEmptyBodyCannotSupplyTheEndTime() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);

		mvc.perform(post("/api/v1/guest/sessions/" + active.sessionId() + "/end")
				.header("X-Guest-Credential", credential).header("Idempotency-Key", UUID.randomUUID())
				.contentType(MediaType.APPLICATION_JSON).content("{\"endedAt\":\"2000-01-01T00:00:00Z\"}"))
				.andExpect(status().is(422)).andExpect(jsonPath("$.fieldErrors.body").isNotEmpty());

		assertThat(repository.findById(active.sessionId()).orElseThrow().getEndedAt()).isNull();
	}

	@Test
	void malformedSessionIdReturnsBadRequest() throws Exception {
		mvc.perform(post("/api/v1/guest/sessions/not-a-uuid/end")
				.header("X-Guest-Credential", credential(1)).header("Idempotency-Key", UUID.randomUUID()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));

		assertThat(repository.count()).isZero();
	}

	@Test
	void endingOneOfTwoSameNameSessionsLeavesTheOtherActive() throws Exception {
		String firstCredential = credential(1);
		String secondCredential = credential(2);
		SessionResponse first = start(issue(firstCredential), firstCredential, 201);
		SessionResponse second = start(issue(secondCredential), secondCredential, 201);

		end(first.sessionId(), firstCredential, UUID.randomUUID().toString()).andExpect(status().isOk());

		mvc.perform(get("/api/v1/guest/sessions/" + second.sessionId()).header("X-Guest-Credential", secondCredential))
				.andExpect(status().isOk()).andExpect(jsonPath("$.state").value("ACTIVE"))
				.andExpect(jsonPath("$.endedAt").doesNotExist())
				.andExpect(jsonPath("$.expiresAt").value(second.expiresAt().toString()));
		assertThat(repository.count()).isEqualTo(2);
	}

	@Test
	void concurrentEndRequestsHaveOneFirstResultAndOneReplay() throws Exception {
		String credential = credential(1);
		SessionResponse active = start(issue(credential), credential, 201);
		clock.set(active.startedAt().plus(Duration.ofMinutes(1)));
		String key = UUID.randomUUID().toString();
		CyclicBarrier ready = new CyclicBarrier(2);

		try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
			Future<SessionResponse> first = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(end(active.sessionId(), credential, key).andExpect(status().isOk()));
			});
			Future<SessionResponse> second = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(end(active.sessionId(), credential, key).andExpect(status().isOk()));
			});

			SessionResponse a = first.get(30, TimeUnit.SECONDS);
			SessionResponse b = second.get(30, TimeUnit.SECONDS);

			assertThat(a.endedAt()).isEqualTo(b.endedAt());
			assertThat(a.replayed()).isNotEqualTo(b.replayed());
			assertThat(repository.findById(active.sessionId()).orElseThrow().getEndedAt()).isEqualTo(a.endedAt());
		}
		assertThat(repository.count()).isEqualTo(1);
	}

	private StartTicketResponse issue(String credential) throws Exception {
		var result = mvc.perform(post("/api/v1/guest/start-ticket").header("X-Guest-Credential", credential))
				.andExpect(status().isCreated());
		return json.readValue(result.andReturn().getResponse().getContentAsString(), StartTicketResponse.class);
	}

	private SessionResponse start(StartTicketResponse ticket, String credential, int expectedStatus) throws Exception {
		return read(mvc.perform(post("/api/v1/guest/sessions").header("X-Guest-Credential", credential)
				.header("Idempotency-Key", ticket.requestId()).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new StartRequest(ticket.startTicket(), "An Bình", "vi"))))
				.andExpect(status().is(expectedStatus)));
	}

	private ResultActions end(UUID id, String credential, String key) throws Exception {
		var request = post("/api/v1/guest/sessions/" + id + "/end");
		if (credential != null)
			request.header("X-Guest-Credential", credential);
		if (key != null)
			request.header("Idempotency-Key", key);
		return mvc.perform(request);
	}

	private SessionResponse read(ResultActions result) throws Exception {
		return json.readValue(result.andReturn().getResponse().getContentAsString(), SessionResponse.class);
	}

	private static String credential(int marker) {
		byte[] bytes = new byte[32];
		Arrays.fill(bytes, (byte) marker);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
