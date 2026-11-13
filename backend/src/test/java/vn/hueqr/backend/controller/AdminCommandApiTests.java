package vn.hueqr.backend.controller;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.CreatePlaceRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.AdminCommandWindowRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminCommandApiTests {
	private static final String PASSWORD = "Mat-khau-command-fixture";
	private record Browser(MockHttpSession session, AdminCsrfResponse csrf) {
	}

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper json;

	@Autowired
	private MutableClock clock;

	@Autowired
	private PasswordEncoder passwords;

	@Autowired
	private AdminAccountRepository accounts;

	@Autowired
	private AdminCommandWindowRepository windows;

	@Autowired
	private OperationReceiptRepository receipts;

	@Autowired
	private PlaceRepository places;

	@Autowired
	private JdbcTemplate jdbc;

	@BeforeEach
	void reset() {
		clearData();
		clock.set(GuestSessionTestClock.BASE);
		String passwordHash = passwords.encode(PASSWORD);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "owner", passwordHash, true));
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "other", passwordHash, true));
	}

	@AfterEach
	void clearData() {
		// Tháo dữ liệu theo khóa ngoại để các lớp test dùng cùng context không phụ thuộc thứ tự chạy
		receipts.deleteAll();
		windows.deleteAll();
		places.deleteAll();
		accounts.deleteAll();
	}

	@Test
	void issuesExactlyTwentyFourHoursAtDatabasePrecisionWithoutExtendingQ3() throws Exception {
		Browser browser = login("owner");
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(29)).plusNanos(123456789));
		AdminCommandWindowResponse window = issue(browser);

		assertThat(window.issuedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertThat(window.expiresAt()).isEqualTo(window.issuedAt().plus(Duration.ofHours(24)));
		assertThat(windows.findById(window.windowId()).orElseThrow().getExpiresAt()).isEqualTo(window.expiresAt());
		assertThat(receipts.count()).isZero();
		assertThat(places.count()).isZero();
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
	}

	@Test
	void requiresAuthenticatedAdminCsrfAndAnEmptyWindowBody() throws Exception {
		mvc.perform(post("/api/v1/admin/command-windows")).andExpect(status().isUnauthorized());
		Browser browser = login("owner");
		mvc.perform(post("/api/v1/admin/command-windows").session(browser.session())).andExpect(status().isForbidden());
		mvc.perform(post("/api/v1/admin/command-windows").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().is(422)).andExpect(jsonPath("$.fieldErrors.body").isNotEmpty());
		assertThat(windows.count()).isZero();
	}

	@Test
	void createsAHiddenNormalizedPlaceAndCommitsOneMinimalReceipt() throws Exception {
		Browser browser = login("owner");
		String key = key(issue(browser));
		AdminOperationResponse result = read(create(browser, key, "  Điện\u00a0Thái   Hòa  ").andExpect(status().isCreated())
				.andExpect(header().string("Cache-Control", "no-store")));
		var place = places.findById(UUID.fromString((String) result.result().get("placeId"))).orElseThrow();

		assertThat(result.replayed()).isFalse();
		assertThat(result.resultCode()).isEqualTo("PLACE_CREATED");
		assertThat(place.getDisplayName()).isEqualTo("Điện Thái Hòa");
		assertThat(place.isVisible()).isFalse();
		assertThat(place.getActiveRegionRevisionId()).isNull();
		assertThat(place.getVersion()).isZero();
		assertThat(receipts.count()).isEqualTo(1);
		assertThat(receipts.findById(result.operationId()).orElseThrow().getResultSummary())
				.doesNotContain("Điện", "displayName", "password", "credential");
	}

	@Test
	void replayAndLookupKeepTheOriginalCommittedResultWithoutCreatingAnotherPlace() throws Exception {
		Browser browser = login("owner");
		String key = key(issue(browser));
		AdminOperationResponse first = read(create(browser, key, "Ngọ Môn").andExpect(status().isCreated()));
		clock.set(clock.instant().plus(Duration.ofMinutes(1)));
		AdminOperationResponse replay = read(create(browser, key, "Ngọ Môn").andExpect(status().isCreated()));

		assertThat(replay.operationId()).isEqualTo(first.operationId());
		assertThat(replay.result()).isEqualTo(first.result());
		assertThat(replay.committedAt()).isEqualTo(first.committedAt());
		assertThat(replay.replayed()).isTrue();
		lookup(browser, key).andExpect(status().isOk()).andExpect(jsonPath("$.state").value("COMMITTED"))
				.andExpect(jsonPath("$.operation.operationId").value(first.operationId().toString()));
		assertThat(places.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void reusingACommittedKeyWithAnotherPayloadReturnsConflict() throws Exception {
		Browser browser = login("owner");
		String key = key(issue(browser));
		create(browser, key, "Ngọ Môn").andExpect(status().isCreated());

		create(browser, key, "Điện Thái Hòa").andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("KEY_REUSED"));
		assertThat(places.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void anotherAccountCannotReadOrExecuteAWindowAndUnknownWindowsAreRejected() throws Exception {
		Browser owner = login("owner");
		String key = key(issue(owner));
		create(owner, key, "Ngọ Môn").andExpect(status().isCreated());
		Browser other = login("other");

		lookup(other, key).andExpect(status().isForbidden()).andExpect(jsonPath("$.operation").doesNotExist());
		create(other, key, "Ngọ Môn").andExpect(status().isForbidden());
		create(owner, UUID.randomUUID() + ":" + UUID.randomUUID(), "Điểm khác").andExpect(status().isForbidden());
		assertThat(places.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void aNewLoginCanReadTheSameWindowWithoutChangingItsDeadline() throws Exception {
		Browser browser = login("owner");
		AdminCommandWindowResponse window = issue(browser);
		String key = key(window);
		create(browser, key, "Ngọ Môn").andExpect(status().isCreated());
		clock.set(clock.instant().plus(Duration.ofHours(2)));
		Browser fresh = login("owner");

		lookup(fresh, key).andExpect(status().isOk()).andExpect(jsonPath("$.state").value("COMMITTED"));
		create(fresh, key, "Ngọ Môn").andExpect(status().isCreated()).andExpect(jsonPath("$.replayed").value(true));
		assertThat(windows.findById(window.windowId()).orElseThrow().getExpiresAt()).isEqualTo(window.expiresAt());
		assertThat(places.count()).isEqualTo(1);
	}

	@Test
	void exactWindowExpiryRejectsBothReplayAndNewExecutionAfterRelogin() throws Exception {
		Browser browser = login("owner");
		AdminCommandWindowResponse window = issue(browser);
		String originalKey = key(window);
		create(browser, originalKey, "Ngọ Môn").andExpect(status().isCreated());
		clock.set(window.expiresAt());
		Browser fresh = login("owner");

		lookup(fresh, originalKey).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("COMMAND_WINDOW_EXPIRED"));
		create(fresh, originalKey, "Ngọ Môn").andExpect(status().isConflict());
		create(fresh, key(window), "Điểm mới").andExpect(status().isConflict());
		assertThat(places.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void epochIsCheckedBeforeAnExistingReceiptIsReplayed() throws Exception {
		Browser browser = login("owner");
		AdminCommandWindowResponse window = issue(browser);
		String key = key(window);
		create(browser, key, "Ngọ Môn").andExpect(status().isCreated());
		jdbc.update("UPDATE admin_command_window SET service_epoch = ? WHERE id = ?",
				UUID.randomUUID().toString(), uuidBytes(window.windowId()));

		lookup(browser, key).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("SERVICE_EPOCH_MISMATCH"));
		create(browser, key, "Ngọ Môn").andExpect(status().isForbidden());
		assertThat(places.count()).isEqualTo(1);
	}

	@Test
	void unobservedResultDoesNotClaimFailureOrRenewQ3() throws Exception {
		Browser browser = login("owner");
		String key = key(issue(browser));
		clock.set(clock.instant().plus(Duration.ofMinutes(29)));

		lookup(browser, key).andExpect(status().isOk()).andExpect(jsonPath("$.state").value("NOT_OBSERVED"))
				.andExpect(jsonPath("$.operation").doesNotExist());
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		assertThat(receipts.count()).isZero();
	}

	@Test
	void duplicateNamesWarnWithoutBlockingAndAccentsRemainSignificant() throws Exception {
		Browser browser = login("owner");
		AdminCommandWindowResponse window = issue(browser);

		create(browser, key(window), "Ngọ Môn").andExpect(status().isCreated())
				.andExpect(jsonPath("$.result.duplicateNameWarning").value(false));
		create(browser, key(window), "ngọ môn").andExpect(status().isCreated())
				.andExpect(jsonPath("$.result.duplicateNameWarning").value(true));
		create(browser, key(window), "Ngo Mon").andExpect(status().isCreated())
				.andExpect(jsonPath("$.result.duplicateNameWarning").value(false));
		assertThat(places.count()).isEqualTo(3);
	}

	@Test
	void countsOneHundredDisplayedCharactersAndRejectsInvalidNamesWithoutReceipts() throws Exception {
		Browser browser = login("owner");
		AdminCommandWindowResponse window = issue(browser);
		String grapheme = "👨‍👩‍👧‍👦";

		create(browser, key(window), grapheme.repeat(100)).andExpect(status().isCreated());
		for (String invalid : new String[]{null, "  ", "a\nb", "a\u0000b", "a\u2028b", grapheme.repeat(101)}) {
			create(browser, key(window), invalid).andExpect(status().is(422))
					.andExpect(jsonPath("$.fieldErrors.displayName").isNotEmpty());
		}
		// Escape JSON giữ surrogate lẻ qua UTF-8 của request để kiểm đúng dữ liệu server nhận
		mvc.perform(post("/api/v1/admin/places").session(browser.session()).header("Idempotency-Key", key(window))
				.header(browser.csrf().headerName(), browser.csrf().token()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"displayName\":\"\\ud800\"}")).andExpect(status().is(422));
		assertThat(places.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void rejectsMissingMalformedAndNonCanonicalKeysWithoutWriting() throws Exception {
		Browser browser = login("owner");
		String valid = key(issue(browser));

		for (String invalid : new String[]{null, "invalid", UUID.randomUUID().toString(), valid.toUpperCase()}) {
			create(browser, invalid, "Ngọ Môn").andExpect(status().is(422))
					.andExpect(jsonPath("$.fieldErrors['Idempotency-Key']").isNotEmpty());
		}
		assertThat(places.count()).isZero();
		assertThat(receipts.count()).isZero();
	}

	@Test
	void committedResultRemainsReadableAfterItsPlaceIsDeleted() throws Exception {
		Browser browser = login("owner");
		String key = key(issue(browser));
		AdminOperationResponse first = read(create(browser, key, "Ngọ Môn").andExpect(status().isCreated()));
		places.deleteById(UUID.fromString((String) first.result().get("placeId")));

		lookup(browser, key).andExpect(status().isOk()).andExpect(jsonPath("$.state").value("COMMITTED"));
		AdminOperationResponse replay = read(create(browser, key, "Ngọ Môn").andExpect(status().isCreated()));
		assertThat(replay.result()).isEqualTo(first.result());
		assertThat(replay.replayed()).isTrue();
		assertThat(places.count()).isZero();
	}

	@Test
	void concurrentRequestsWithTheSameKeyCreateOnePlaceAndOneReceipt() throws Exception {
		Browser browser = login("owner");
		String key = key(issue(browser));
		CyclicBarrier ready = new CyclicBarrier(2);

		try (var pool = Executors.newFixedThreadPool(2)) {
			var first = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(create(browser, key, "Ngọ Môn").andExpect(status().isCreated()));
			});
			var second = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(create(browser, key, "Ngọ Môn").andExpect(status().isCreated()));
			});
			AdminOperationResponse a = first.get(30, TimeUnit.SECONDS);
			AdminOperationResponse b = second.get(30, TimeUnit.SECONDS);
			assertThat(a.operationId()).isEqualTo(b.operationId());
			assertThat(a.result()).isEqualTo(b.result());
			assertThat(a.replayed()).isNotEqualTo(b.replayed());
		}
		assertThat(places.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void invalidCsrfCannotCreateAPlace() throws Exception {
		Browser browser = login("owner");
		String key = key(issue(browser));

		mvc.perform(post("/api/v1/admin/places").session(browser.session()).header("Idempotency-Key", key)
				.contentType(MediaType.APPLICATION_JSON).content("{\"displayName\":\"Ngọ Môn\"}"))
				.andExpect(status().isForbidden());
		assertThat(places.count()).isZero();
	}

	private Browser login(String name) throws Exception {
		var initial = mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk()).andReturn();
		MockHttpSession session = (MockHttpSession) initial.getRequest().getSession(false);
		AdminCsrfResponse csrf = json.readValue(initial.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(post("/api/v1/auth/login").session(session).header(csrf.headerName(), csrf.token())
				.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(new AdminLoginRequest(name, PASSWORD))))
				.andExpect(status().isOk());
		var current = mvc.perform(get("/api/v1/auth/csrf").session(session)).andExpect(status().isOk()).andReturn();
		return new Browser(session, json.readValue(current.getResponse().getContentAsString(), AdminCsrfResponse.class));
	}

	private AdminCommandWindowResponse issue(Browser browser) throws Exception {
		var result = mvc.perform(post("/api/v1/admin/command-windows").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token())).andExpect(status().isCreated()).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminCommandWindowResponse.class);
	}

	private ResultActions create(Browser browser, String key, String name) throws Exception {
		var request = post("/api/v1/admin/places").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new CreatePlaceRequest(name)));
		if (key != null)
			request.header("Idempotency-Key", key);
		return mvc.perform(request);
	}

	private ResultActions lookup(Browser browser, String key) throws Exception {
		return mvc.perform(get("/api/v1/admin/operations/" + key).session(browser.session()));
	}

	private AdminOperationResponse read(ResultActions result) throws Exception {
		return json.readValue(result.andReturn().getResponse().getContentAsString(), AdminOperationResponse.class);
	}

	private static String key(AdminCommandWindowResponse window) {
		return window.windowId() + ":" + UUID.randomUUID();
	}

	private static byte[] uuidBytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
