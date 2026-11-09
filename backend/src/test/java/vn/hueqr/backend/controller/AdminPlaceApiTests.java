package vn.hueqr.backend.controller;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
import vn.hueqr.backend.dto.AdminPlaceResponse;
import vn.hueqr.backend.dto.UpdatePlaceRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.RegionRevision;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.AdminCommandWindowRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.RegionRevisionRepository;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminPlaceApiTests {
	private static final String PASSWORD = "Mat-khau-place-fixture";
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
	private RegionRevisionRepository regions;

	@Autowired
	private JdbcTemplate jdbc;

	@BeforeEach
	void reset() {
		clearData();
		clock.set(GuestSessionTestClock.BASE);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "place-owner", passwords.encode(PASSWORD), true));
	}

	@AfterEach
	void clearData() {
		receipts.deleteAll();
		windows.deleteAll();
		// Tháo con trỏ trước khi xóa vùng để giữ đúng thứ tự khóa ngoại của fixture
		jdbc.update("UPDATE place SET active_region_revision_id = NULL");
		regions.deleteAll();
		places.deleteAll();
		accounts.deleteAll();
	}

	@Test
	void anEmptyListIsSuccessfulAndDoesNotCreateData() throws Exception {
		Browser browser = login();
		mvc.perform(get("/api/v1/admin/places").session(browser.session())).andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store")).andExpect(jsonPath("$.length()").value(0));
		assertThat(receipts.count()).isZero();
		assertThat(windows.count()).isZero();
	}

	@Test
	void listIncludesMoreThanFivePlacesAndKeepsDuplicateNamesWithStableIdentifiers() throws Exception {
		Browser browser = login();
		UUID first = UUID.fromString("00000000-0000-0000-0000-000000000001");
		UUID second = UUID.fromString("00000000-0000-0000-0000-000000000002");
		places.saveAllAndFlush(List.of(new Place(second, "Trùng tên", clock.instant()), new Place(first, "Trùng tên", clock.instant())));
		for (int index = 0; index < 5; index++) {
			places.saveAndFlush(new Place(UUID.randomUUID(), "Điểm " + index, clock.instant().plusSeconds(index + 1)));
		}

		mvc.perform(get("/api/v1/admin/places").session(browser.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(7)).andExpect(jsonPath("$[0].placeId").value(first.toString()))
				.andExpect(jsonPath("$[1].placeId").value(second.toString())).andExpect(jsonPath("$[0].displayName").value("Trùng tên"))
				.andExpect(jsonPath("$[1].displayName").value("Trùng tên"));
	}

	@Test
	void detailReturnsTheStoredFieldsAndAQuotedVersionEtag() throws Exception {
		Browser browser = login();
		Place place = seed("Ngọ Môn");
		var result = detail(browser, place.getId().toString()).andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"0\"")).andExpect(header().string("Cache-Control", "no-store"))
				.andExpect(jsonPath("$.activeRegionRevisionId").isEmpty()).andReturn();
		AdminPlaceResponse body = json.readValue(result.getResponse().getContentAsString(), AdminPlaceResponse.class);
		assertThat(body.placeId()).isEqualTo(place.getId());
		assertThat(body.displayName()).isEqualTo("Ngọ Môn");
		assertThat(body.visible()).isFalse();
		assertThat(body.createdAt()).isEqualTo(place.getCreatedAt());
		assertThat(body.updatedAt()).isEqualTo(place.getUpdatedAt());
	}

	@Test
	void allPlaceEndpointsRequireAnAuthenticatedAdmin() throws Exception {
		String id = UUID.randomUUID().toString();
		mvc.perform(get("/api/v1/admin/places")).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/v1/admin/places/" + id)).andExpect(status().isUnauthorized());
		mvc.perform(patch("/api/v1/admin/places/" + id).contentType(MediaType.APPLICATION_JSON)
				.content("{\"displayName\":\"Ngọ Môn\"}")).andExpect(status().isUnauthorized());
	}

	@Test
	void listAndDetailDoNotExtendTheThirtyMinuteSession() throws Exception {
		Browser browser = login();
		Place place = seed("Ngọ Môn");
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(29)));
		mvc.perform(get("/api/v1/admin/places").session(browser.session())).andExpect(status().isOk());
		detail(browser, place.getId().toString()).andExpect(status().isOk());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)));
		detail(browser, place.getId().toString()).andExpect(status().isUnauthorized());
	}

	@Test
	void renameNormalizesQ1AndReturnsTheFlushedVersionWithMicrosecondPrecision() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		String key = key(issue(browser));
		clock.set(GuestSessionTestClock.BASE.plusNanos(987));
		AdminOperationResponse result = read(rename(browser, place.getId().toString(), key, "\"0\"", "  Điện\u00a0Thái   Hòa  ")
				.andExpect(status().isOk()).andExpect(header().string("ETag", "\"1\""))
				.andExpect(header().string("Cache-Control", "no-store")));

		Place stored = places.findById(place.getId()).orElseThrow();
		assertThat(stored.getDisplayName()).isEqualTo("Điện Thái Hòa");
		assertThat(stored.getVersion()).isEqualTo(1L);
		assertThat(stored.getUpdatedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertThat(result.resultCode()).isEqualTo("PLACE_UPDATED");
		assertThat(result.result()).doesNotContainKey("displayName");
		assertThat(result.replayed()).isFalse();
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void renamePreservesVisibilityActiveRegionAndCreationTime() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		RegionRevision region = regions.saveAndFlush(new RegionRevision(UUID.randomUUID(), place,
				"{\"type\":\"Polygon\",\"coordinates\":[]}", "fixture-space", new byte[32], clock.instant()));
		// Chỉ fixture dữ liệu để kiểm không bị đổi ngoài ý muốn, không bỏ qua gate bật hiện trong API
		jdbc.update("UPDATE place SET visible = TRUE, active_region_revision_id = ? WHERE id = ?", uuidBytes(region.getId()), uuidBytes(place.getId()));
		clock.set(clock.instant().plusSeconds(1));

		rename(browser, place.getId().toString(), key(issue(browser)), "\"0\"", "Tên mới").andExpect(status().isOk());

		Place stored = places.findById(place.getId()).orElseThrow();
		assertThat(stored.isVisible()).isTrue();
		assertThat(stored.getActiveRegionRevisionId()).isEqualTo(region.getId());
		assertThat(stored.getCreatedAt()).isEqualTo(place.getCreatedAt());
		assertThat(regions.count()).isEqualTo(1);
	}

	@Test
	void duplicateWarningExcludesTheCurrentPlaceAndKeepsVietnameseAccents() throws Exception {
		Browser browser = login();
		Place place = seed("Ngọ Môn");
		String window = issue(browser);
		AdminOperationResponse same = read(rename(browser, place.getId().toString(), key(window), "\"0\"", "NGỌ MÔN").andExpect(status().isOk()));
		assertThat(same.result().get("duplicateNameWarning")).isEqualTo(false);
		seed("Điện Thái Hòa");
		AdminOperationResponse duplicate = read(rename(browser, place.getId().toString(), key(window), "\"1\"", "điện thái hòa").andExpect(status().isOk()));
		assertThat(duplicate.result().get("duplicateNameWarning")).isEqualTo(true);
		AdminOperationResponse differentAccents = read(rename(browser, place.getId().toString(), key(window), "\"2\"", "Dien Thai Hoa").andExpect(status().isOk()));
		assertThat(differentAccents.result().get("duplicateNameWarning")).isEqualTo(false);
		assertThat(places.count()).isEqualTo(2);
	}

	@Test
	void missingIfMatchIsRejectedWithoutAReceiptOrMutation() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		rename(browser, place.getId().toString(), key(issue(browser)), null, "Tên mới").andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("PRECONDITION_REQUIRED"));
		assertUnchanged(place);
	}

	@Test
	void malformedWeakWildcardAndMultipleEtagsCannotBypassTheVersionCheck() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		String window = issue(browser);
		for (String value : new String[]{"0", "W/\"0\"", "*", "\"0\", \"1\"", "\"-1\"", "\"00\"", "\"9223372036854775808\""}) {
			rename(browser, place.getId().toString(), key(window), value, "Tên mới").andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.fieldErrors['If-Match']").isNotEmpty());
		}
		assertUnchanged(place);
	}

	@Test
	void staleVersionReturns412AndKeepsThePlaceAndReceiptCountUnchanged() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		rename(browser, place.getId().toString(), key(issue(browser)), "\"5\"", "Tên mới").andExpect(status().isPreconditionFailed())
				.andExpect(jsonPath("$.code").value("VERSION_CONFLICT"));
		assertUnchanged(place);
	}

	@Test
	void unknownPlaceReturns404ForDetailAndRename() throws Exception {
		Browser browser = login();
		String id = UUID.randomUUID().toString();
		detail(browser, id).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("LOCATION_NOT_FOUND"));
		rename(browser, id, key(issue(browser)), "\"0\"", "Tên mới").andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("LOCATION_NOT_FOUND"));
		assertThat(receipts.count()).isZero();
	}

	@Test
	void nonCanonicalAndInvalidPlaceIdsReturnFieldErrors() throws Exception {
		Browser browser = login();
		String window = issue(browser);
		for (String id : new String[]{"invalid", "1-1-1-1-1", "ABCDEFAB-1234-1234-1234-123456789ABC"}) {
			detail(browser, id).andExpect(status().is(422)).andExpect(jsonPath("$.fieldErrors.placeId").isNotEmpty());
			rename(browser, id, key(window), "\"0\"", "Tên mới").andExpect(status().is(422));
		}
		assertThat(receipts.count()).isZero();
	}

	@Test
	void renameEnforcesTheSameGraphemeAndControlRulesAsCreate() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		String window = issue(browser);
		for (String invalid : new String[]{null, "   ", "Tên\nkhác", "Tên\u0001", "a".repeat(101)}) {
			rename(browser, place.getId().toString(), key(window), "\"0\"", invalid).andExpect(status().is(422))
					.andExpect(jsonPath("$.fieldErrors.displayName").isNotEmpty());
		}
		String family = "👨‍👩‍👧‍👦";
		rename(browser, place.getId().toString(), key(window), "\"0\"", family.repeat(100)).andExpect(status().isOk());
		assertThat(places.findById(place.getId()).orElseThrow().getDisplayName()).isEqualTo(family.repeat(100));
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void replayReturnsItsOriginalResultAndEtagEvenAfterAnotherRename() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		String window = issue(browser);
		String originalKey = key(window);
		AdminOperationResponse original = read(rename(browser, place.getId().toString(), originalKey, "\"0\"", "Tên thứ hai")
				.andExpect(status().isOk()));
		rename(browser, place.getId().toString(), key(window), "\"1\"", "Tên thứ ba").andExpect(status().isOk());
		AdminOperationResponse replay = read(rename(browser, place.getId().toString(), originalKey, "\"0\"", "Tên thứ hai")
				.andExpect(status().isOk()).andExpect(header().string("ETag", "\"1\"")));

		assertThat(replay.operationId()).isEqualTo(original.operationId());
		assertThat(replay.result()).isEqualTo(original.result());
		assertThat(replay.replayed()).isTrue();
		detail(browser, place.getId().toString()).andExpect(header().string("ETag", "\"2\""))
				.andExpect(jsonPath("$.displayName").value("Tên thứ ba"));
		assertThat(receipts.count()).isEqualTo(2);
	}

	@Test
	void keyCannotBeReusedWithAnotherNameVersionOrPlace() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		Place other = seed("Điểm khác");
		String key = key(issue(browser));
		rename(browser, place.getId().toString(), key, "\"0\"", "Tên mới").andExpect(status().isOk());
		rename(browser, place.getId().toString(), key, "\"0\"", "Tên khác").andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("KEY_REUSED"));
		rename(browser, place.getId().toString(), key, "\"1\"", "Tên mới").andExpect(status().isConflict());
		rename(browser, other.getId().toString(), key, "\"0\"", "Tên mới").andExpect(status().isConflict());
		assertThat(receipts.count()).isEqualTo(1);
		assertThat(places.findById(other.getId()).orElseThrow().getDisplayName()).isEqualTo("Điểm khác");
	}

	@Test
	void concurrentRenamesAcrossWindowsAndSessionsAllowOnlyOneWriterForTheSameVersion() throws Exception {
		Browser firstBrowser = login();
		Browser secondBrowser = login();
		Place place = seed("Tên cũ");
		String firstKey = key(issue(firstBrowser));
		String secondKey = key(issue(secondBrowser));
		CyclicBarrier ready = new CyclicBarrier(2);

		try (var pool = Executors.newFixedThreadPool(2)) {
			var first = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return rename(firstBrowser, place.getId().toString(), firstKey, "\"0\"", "Tên A").andReturn();
			});
			var second = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return rename(secondBrowser, place.getId().toString(), secondKey, "\"0\"", "Tên B").andReturn();
			});
			var a = first.get(30, TimeUnit.SECONDS).getResponse();
			var b = second.get(30, TimeUnit.SECONDS).getResponse();
			assertThat(List.of(a.getStatus(), b.getStatus())).containsExactlyInAnyOrder(200, 412);
			String winner = a.getStatus() == 200 ? "Tên A" : "Tên B";
			assertThat(places.findById(place.getId()).orElseThrow().getDisplayName()).isEqualTo(winner);
		}
		assertThat(places.findById(place.getId()).orElseThrow().getVersion()).isEqualTo(1L);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void concurrentExactRetriesRenameOnlyOnce() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		String key = key(issue(browser));
		CyclicBarrier ready = new CyclicBarrier(2);
		try (var pool = Executors.newFixedThreadPool(2)) {
			var first = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(rename(browser, place.getId().toString(), key, "\"0\"", "Tên mới").andExpect(status().isOk()));
			});
			var second = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(rename(browser, place.getId().toString(), key, "\"0\"", "Tên mới").andExpect(status().isOk()));
			});
			var a = first.get(30, TimeUnit.SECONDS);
			var b = second.get(30, TimeUnit.SECONDS);
			assertThat(a.operationId()).isEqualTo(b.operationId());
			assertThat(a.replayed()).isNotEqualTo(b.replayed());
		}
		assertThat(places.findById(place.getId()).orElseThrow().getVersion()).isEqualTo(1L);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void invalidCsrfOrMalformedJsonCannotRenameOrExtendQ3() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		String key = key(issue(browser));
		clock.set(clock.instant().plus(Duration.ofMinutes(20)));
		mvc.perform(patch("/api/v1/admin/places/" + place.getId()).session(browser.session())
				.header("Idempotency-Key", key).header("If-Match", "\"0\"").contentType(MediaType.APPLICATION_JSON)
				.content("{\"displayName\":\"Tên mới\"}")).andExpect(status().isForbidden());
		mvc.perform(patch("/api/v1/admin/places/" + place.getId()).session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).header("Idempotency-Key", key)
				.header("If-Match", "\"0\"").contentType(MediaType.APPLICATION_JSON).content("{"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		assertUnchanged(place);
	}

	@Test
	void saveIntentExtendsQ3EvenWhenVersionValidationFails() throws Exception {
		Browser browser = login();
		Place place = seed("Tên cũ");
		String key = key(issue(browser));
		clock.set(clock.instant().plus(Duration.ofMinutes(29)));
		rename(browser, place.getId().toString(), key, "\"1\"", "Tên mới").andExpect(status().isPreconditionFailed());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(clock.instant().toString()));
		assertUnchanged(place);
	}

	private Place seed(String name) {
		return places.saveAndFlush(new Place(UUID.randomUUID(), name, clock.instant()));
	}

	private void assertUnchanged(Place original) {
		Place stored = places.findById(original.getId()).orElseThrow();
		assertThat(stored.getDisplayName()).isEqualTo(original.getDisplayName());
		assertThat(stored.getVersion()).isEqualTo(original.getVersion());
		assertThat(stored.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
		assertThat(receipts.count()).isZero();
	}

	private Browser login() throws Exception {
		var initial = mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk()).andReturn();
		MockHttpSession session = (MockHttpSession) initial.getRequest().getSession(false);
		AdminCsrfResponse csrf = json.readValue(initial.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(post("/api/v1/auth/login").session(session).header(csrf.headerName(), csrf.token())
				.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(new AdminLoginRequest("place-owner", PASSWORD))))
				.andExpect(status().isOk());
		var current = mvc.perform(get("/api/v1/auth/csrf").session(session)).andExpect(status().isOk()).andReturn();
		return new Browser(session, json.readValue(current.getResponse().getContentAsString(), AdminCsrfResponse.class));
	}

	private String issue(Browser browser) throws Exception {
		var result = mvc.perform(post("/api/v1/admin/command-windows").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token())).andExpect(status().isCreated()).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminCommandWindowResponse.class).windowId().toString();
	}

	private ResultActions detail(Browser browser, String id) throws Exception {
		return mvc.perform(get("/api/v1/admin/places/" + id).session(browser.session()));
	}

	private ResultActions rename(Browser browser, String id, String key, String ifMatch, String name) throws Exception {
		var request = patch("/api/v1/admin/places/" + id).session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).header("Idempotency-Key", key)
				.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(new UpdatePlaceRequest(name)));
		if (ifMatch != null)
			request.header("If-Match", ifMatch);
		return mvc.perform(request);
	}

	private AdminOperationResponse read(ResultActions result) throws Exception {
		return json.readValue(result.andReturn().getResponse().getContentAsString(), AdminOperationResponse.class);
	}

	private static String key(String window) {
		return window + ":" + UUID.randomUUID();
	}

	private static byte[] uuidBytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
