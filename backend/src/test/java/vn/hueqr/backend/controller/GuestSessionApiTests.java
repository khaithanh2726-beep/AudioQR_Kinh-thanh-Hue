package vn.hueqr.backend.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.*;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.service.*;
import vn.hueqr.backend.service.StartTicketCodec.Payload;
import vn.hueqr.backend.service.StartTicketTimePolicy.TicketTimes;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class GuestSessionApiTests {
	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper json;
	@Autowired
	GuestSessionRepository repository;
	@Autowired
	MutableClock clock;
	@Autowired
	StartTicketCodec codec;
	@Autowired
	StartTicketService tickets;
	@Autowired
	StartTicketTimePolicy time;
	@Autowired
	GuestInputPolicy input;
	@Autowired
	PlatformTransactionManager manager;
	@Autowired
	JdbcTemplate jdbc;

	@BeforeEach
	void resetFixture() {
		repository.deleteAll();
		clock.set(GuestSessionTestClock.BASE);
	}

	@Test
	void ticketDoesNotCreateSessionAndStartNormalizesNameWithFixedEightHours() throws Exception {
		String credential = credential(1);
		StartTicketResponse ticket = issue(credential);

		assertThat(repository.count()).isZero();
		assertThat(ticket.ticketExpiresAt()).isEqualTo(ticket.issuedAt().plus(Duration.ofHours(24)));

		SessionResponse session = start(ticket, credential, "  An   Bình  ", "vi", 201);

		assertThat(session.displayName()).isEqualTo("An Bình");
		assertThat(session.expiresAt()).isEqualTo(session.startedAt().plus(Duration.ofHours(8)));
		assertThat(session.created()).isTrue();
		assertThat(session.state()).isEqualTo("ACTIVE");
		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void lostResponseReplayKeepsIdAndOriginalDeadlines() throws Exception {
		String credential = credential(1);
		StartTicketResponse ticket = issue(credential);
		SessionResponse first = start(ticket, credential, " An  Bình ", "vi", 201);

		clock.set(GuestSessionTestClock.BASE.plusSeconds(60));
		SessionResponse again = start(ticket, credential, "An Bình", "vi", 200);

		assertThat(again.sessionId()).isEqualTo(first.sessionId());
		assertThat(again.startedAt()).isEqualTo(first.startedAt());
		assertThat(again.expiresAt()).isEqualTo(first.expiresAt());
		assertThat(again.replayed()).isTrue();
		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void sameNameInTwoTabsHasIndependentSessionAndCredential() throws Exception {
		String a = credential(1), b = credential(2);
		SessionResponse first = start(issue(a), a, "Trùng tên", "vi", 201);
		SessionResponse second = start(issue(b), b, "Trùng tên", "fr", 201);

		assertThat(first.sessionId()).isNotEqualTo(second.sessionId());
		mvc.perform(get("/api/v1/guest/sessions/" + first.sessionId()).header("X-Guest-Credential", b))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.displayName").doesNotExist());
		assertThat(repository.count()).isEqualTo(2);
	}

	@Test
	void changedNameOrInitialLanguageReturnsConflictWithoutEditingSession() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);
		SessionResponse first = start(t, c, "An", "vi", 201);

		start(t, c, "Bình", "vi", 409);
		start(t, c, "An", "en", 409);

		assertThat(repository.findById(first.sessionId()).orElseThrow().getDisplayName()).isEqualTo("An");
		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void invalidNameLanguageJsonOrRequestKeyDoesNotInsert() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);

		for (String name : new String[]{" ", "An\nBình", "a".repeat(51)})
			start(t, c, name, "vi", 422);
		start(t, c, "An", "de", 422);

		mvc.perform(post("/api/v1/guest/sessions").contentType(MediaType.APPLICATION_JSON).content("{"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"))
				.andExpect(jsonPath("$.retryable").value(false)).andExpect(jsonPath("$.correlationId").isNotEmpty());

		mvc.perform(post("/api/v1/guest/sessions").header("X-Guest-Credential", c)
				.header("Idempotency-Key", "invalid").contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new StartRequest(t.startTicket(), "An", "vi"))))
				.andExpect(status().is(422));

		mvc.perform(post("/api/v1/guest/sessions").header("X-Guest-Credential", c)
				.header("Idempotency-Key", UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new StartRequest(t.startTicket(), "An", "vi"))))
				.andExpect(status().isUnauthorized());

		assertThat(repository.count()).isZero();
	}

	@Test
	void ticketEndpointRejectsNonEmptyBody() throws Exception {
		mvc.perform(post("/api/v1/guest/start-ticket").header("X-Guest-Credential", credential(1))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().is(422)).andExpect(jsonPath("$.fieldErrors.body").isNotEmpty());

		assertThat(repository.count()).isZero();
	}

	@Test
	void oldTicketCannotRecreateSessionAfterLedgerRemoval() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);
		start(t, c, "An", "vi", 201);

		clock.set(t.issuedAt().plus(Duration.ofDays(31)));

		// Xóa bản ghi để mô phỏng kết quả đã bị dọn, kiểm khả năng tạo lại bằng vé cũ
		repository.deleteAll();

		start(t, c, "An", "vi", 409);

		assertThat(repository.count()).isZero();
	}

	@Test
	void badSignatureMissingCredentialAndWrongBindingDoNotInsert() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);

		mvc.perform(post("/api/v1/guest/start-ticket")).andExpect(status().isUnauthorized());
		start(t, credential(2), "An", "vi", 401);

		StartTicketResponse modified = new StartTicketResponse("invalid", t.requestId(), t.issuedAt(),
				t.ticketExpiresAt(), t.serviceEpoch(), t.serverNow());
		start(modified, c, "An", "vi", 401);

		assertThat(repository.count()).isZero();
	}

	@Test
	void expiredTicketWithoutLedgerFailsAtExactDeadline() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);

		clock.set(t.ticketExpiresAt());
		start(t, c, "An", "vi", 409);

		assertThat(repository.count()).isZero();
	}

	@Test
	void expiredTicketWithLedgerCanReplayAnActiveThenExpiredSession() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);

		// Tạo phiên sát hạn vé để kiểm gửi lại khi vé hết hạn nhưng phiên còn hiệu lực
		clock.set(t.ticketExpiresAt().minusSeconds(60));
		SessionResponse first = start(t, c, "An", "vi", 201);

		clock.set(t.ticketExpiresAt().plusSeconds(1));
		assertThat(start(t, c, "An", "vi", 200).state()).isEqualTo("ACTIVE");

		clock.set(first.expiresAt());
		SessionResponse expired = start(t, c, "An", "vi", 200);
		assertThat(expired.state()).isEqualTo("EXPIRED");
		assertThat(expired.expiresAt()).isEqualTo(first.expiresAt());

		mvc.perform(get("/api/v1/guest/sessions/" + first.sessionId()).header("X-Guest-Credential", c))
				.andExpect(status().isOk()).andExpect(jsonPath("$.state").value("EXPIRED"))
				.andExpect(jsonPath("$.currentLanguage").doesNotExist());
		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void statusDoesNotExtendExpiryAndReturnsEndedWithoutRevivingIt() throws Exception {
		String c = credential(1);
		SessionResponse first = start(issue(c), c, "An", "vi", 201);

		clock.set(first.startedAt().plusSeconds(20));
		var result = mvc.perform(get("/api/v1/guest/sessions/" + first.sessionId())
				.header("X-Guest-Credential", c)).andExpect(status().isOk()).andReturn();
		SessionResponse read = json.readValue(result.getResponse().getContentAsString(), SessionResponse.class);
		assertThat(read.expiresAt()).isEqualTo(first.expiresAt());

		// Ghi ended_at trực tiếp để dựng trạng thái ENDED cho ca kiểm K02
		jdbc.update("UPDATE guest_session SET ended_at = started_at WHERE id = ?",
				java.nio.ByteBuffer.allocate(16).putLong(first.sessionId().getMostSignificantBits())
						.putLong(first.sessionId().getLeastSignificantBits()).array());
		mvc.perform(get("/api/v1/guest/sessions/" + first.sessionId()).header("X-Guest-Credential", c))
				.andExpect(status().isOk()).andExpect(jsonPath("$.state").value("ENDED"));
	}

	@Test
	void wrongEpochFutureTimeAndInvalidLifetimeAreRejected() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);

		Payload original = codec.decodeAndVerify(t.startTicket());
		for (Payload invalid : new Payload[]{
				new Payload(t.requestId(), original.credentialHash(), UUID.randomUUID(), original.times()),
				new Payload(t.requestId(), original.credentialHash(), t.serviceEpoch(),
						new TicketTimes(t.issuedAt().plusSeconds(1), t.ticketExpiresAt().plusSeconds(1))),
				new Payload(t.requestId(), original.credentialHash(), t.serviceEpoch(),
						new TicketTimes(t.issuedAt(), t.ticketExpiresAt().plusSeconds(1)))}) {
			String encoded = codec.encode(invalid);
			mvc.perform(post("/api/v1/guest/sessions").header("X-Guest-Credential", c)
					.header("Idempotency-Key", t.requestId()).contentType(MediaType.APPLICATION_JSON)
					.content(json.writeValueAsString(new StartRequest(encoded, "An", "vi"))))
					.andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(401, 403));
		}

		assertThat(repository.count()).isZero();
	}

	@Test
	void aNewTicketCannotCreateAnotherSessionWithTheSameCredential() throws Exception {
		String c = credential(1);
		start(issue(c), c, "An", "vi", 201);
		start(issue(c), c, "An", "vi", 409);

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void concurrentMissesRollbackTheLoserAndReadTheCommittedSessionInANewTransaction() throws Exception {
		String c = credential(1);
		StartTicketResponse t = issue(c);

		CyclicBarrier bothMissed = new CyclicBarrier(2);
		AtomicInteger reads = new AtomicInteger();

		// Chờ hai lần SELECT cùng đọc rỗng để buộc hai yêu cầu tranh chấp UNIQUE
		GuestSessionRepository observed = (GuestSessionRepository) Proxy.newProxyInstance(
				GuestSessionRepository.class.getClassLoader(), new Class<?>[]{GuestSessionRepository.class},
				(proxy, method, args) -> {
					Object result;
					try {
						result = method.invoke(repository, args);
					}
					catch (InvocationTargetException error) {
						throw error.getCause();
					}

					if (method.getName().equals("findByStartRequestKey") && reads.incrementAndGet() <= 2)
						bothMissed.await(15, TimeUnit.SECONDS);

					return result;
				});

		GuestSessionService racing = new GuestSessionService(observed, tickets, time, input, clock, manager);
		StartRequest request = new StartRequest(t.startTicket(), "An", "vi");

		try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
			Future<SessionResponse> a = pool.submit(() -> racing.start(request, t.requestId().toString(), c));
			Future<SessionResponse> b = pool.submit(() -> racing.start(request, t.requestId().toString(), c));

			SessionResponse first = a.get(30, TimeUnit.SECONDS), second = b.get(30, TimeUnit.SECONDS);

			assertThat(first.sessionId()).isEqualTo(second.sessionId());
			assertThat(first.created()).isNotEqualTo(second.created());
		}

		assertThat(reads.get()).isGreaterThanOrEqualTo(3);
		assertThat(repository.count()).isEqualTo(1);
	}

	private StartTicketResponse issue(String credential) throws Exception {
		var result = mvc.perform(post("/api/v1/guest/start-ticket").header("X-Guest-Credential", credential))
				.andExpect(status().isCreated()).andExpect(header().string("Cache-Control", "no-store")).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), StartTicketResponse.class);
	}

	private SessionResponse start(StartTicketResponse ticket, String credential, String name,
			String language, int expectedStatus) throws Exception {
		var result = mvc.perform(post("/api/v1/guest/sessions").header("X-Guest-Credential", credential)
				.header("Idempotency-Key", ticket.requestId()).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new StartRequest(ticket.startTicket(), name, language))))
				.andExpect(status().is(expectedStatus)).andReturn();
		return expectedStatus == 200 || expectedStatus == 201
				? json.readValue(result.getResponse().getContentAsString(), SessionResponse.class) : null;
	}

	private static String credential(int marker) {
		byte[] bytes = new byte[32];
		Arrays.fill(bytes, (byte) marker);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}

