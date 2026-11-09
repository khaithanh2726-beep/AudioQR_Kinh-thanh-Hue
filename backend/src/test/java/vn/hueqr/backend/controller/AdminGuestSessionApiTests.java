package vn.hueqr.backend.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.PlaceView;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.PlaceViewRepository;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.GuestSessionRepository;
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
class AdminGuestSessionApiTests {
	private static final Instant NOW = Instant.parse("2026-11-09T05:00:00Z");
	private static final Instant START = Instant.parse("2026-11-08T17:00:00Z");
	private static final String PASSWORD = "Mat-khau-read-fixture";

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
	private GuestSessionRepository guests;

	@Autowired
	private PlaceRepository places;

	@Autowired
	private PlaceViewRepository views;

	@BeforeEach
	void setUp() {
		views.deleteAll();
		guests.deleteAll();
		clock.set(NOW);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "guest-read-owner", passwords.encode(PASSWORD), true));
	}

	@AfterEach
	void clear() {
		views.deleteAll();
		guests.deleteAll();
		places.deleteAll();
		accounts.findByLoginName("guest-read-owner").ifPresent(accounts::delete);
	}

	@Test
	void listIncludesBothLocalDateEdgesAndExcludesTheNextMidnightAndPreviousMicrosecond() throws Exception {
		var session = login();
		seed("Đầu ngày", START, null);
		seed("Cuối ngày", START.plusSeconds(86400).minusNanos(1000), null);
		seed("Trước ngày", START.minusNanos(1000), null);
		seed("Sau ngày", START.plusSeconds(86400), null);
		mvc.perform(get(path()).session(session)).andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
				.andExpect(jsonPath("$.fromDate").value("2026-11-09")).andExpect(jsonPath("$.timezone").value("Asia/Saigon"))
				.andExpect(jsonPath("$.sessions.length()").value(2)).andExpect(jsonPath("$.sessions[0].displayName").value("Đầu ngày"))
				.andExpect(jsonPath("$.sessions[1].displayName").value("Cuối ngày"));
	}

	@Test
	void stateFiltersUseEndedTimestampAndExactExpiryRatherThanAssumingOnlinePresence() throws Exception {
		var session = login();
		seed("Đã kết thúc", NOW.minusSeconds(60), NOW.minusSeconds(10));
		seed("Còn hạn", NOW.minusSeconds(30), null);
		seed("Hết hạn đúng mốc", NOW.minus(Duration.ofHours(8)), null);
		for (String state : new String[]{"ACTIVE", "ENDED", "EXPIRED"})
			mvc.perform(get(path()).session(session).param("status", state)).andExpect(status().isOk())
					.andExpect(jsonPath("$.sessions.length()").value(1)).andExpect(jsonPath("$.sessions[0].state").value(state));
		mvc.perform(get(path()).session(session).param("status", "ALL")).andExpect(jsonPath("$.sessions.length()").value(3));
	}

	@Test
	void duplicateNamesRemainSeparateAndSearchTreatsWildcardsAsLiteralText() throws Exception {
		var session = login();
		GuestSession first = seed("Khách 100%_Huế", NOW.minusSeconds(10), null);
		seed("Khách 100%_Huế", NOW.minusSeconds(9), null);
		seed("Khác", NOW.minusSeconds(8), null);
		mvc.perform(get(path()).session(session).param("searchNameOrId", "%_")).andExpect(status().isOk())
				.andExpect(jsonPath("$.sessions.length()").value(2));
		mvc.perform(get(path()).session(session).param("searchNameOrId", first.getId().toString())).andExpect(status().isOk())
				.andExpect(jsonPath("$.sessions.length()").value(1)).andExpect(jsonPath("$.sessions[0].sessionId").value(first.getId().toString()));
		assertThat(guests.count()).isEqualTo(3);
	}

	@Test
	void overdueD08IsHiddenEvenBeforePhysicalCleanupAndNoCredentialHashesLeak() throws Exception {
		var session = login();
		GuestSession overdue = seed("Hết lưu", NOW.minus(Duration.ofDays(30)).minusSeconds(3600), NOW.minus(Duration.ofDays(30)));
		GuestSession retained = seed("Còn lưu", NOW.minus(Duration.ofDays(30)).minusSeconds(3600), NOW.minus(Duration.ofDays(30)).plusNanos(1000));
		mvc.perform(get(path()).session(session).param("fromDate", "2026-10-01").param("toDate", "2026-11-09"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.sessions.length()").value(1));
		mvc.perform(get(path() + "/" + overdue.getId()).session(session)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("DATA_NOT_RETAINED"));
		var result = mvc.perform(get(path() + "/" + retained.getId()).session(session)).andExpect(status().isOk())
				.andExpect(jsonPath("$.displayName").value("Còn lưu")).andReturn();
		assertThat(result.getResponse().getContentAsString()).doesNotContain("credentialHash", "fingerprint", "startRequestKey", "serviceEpoch", "startTicket");
		assertThat(guests.count()).isEqualTo(2);
	}

	@Test
	void invalidFiltersAndMissingDetailsAreReportedWithoutChangingSessionData() throws Exception {
		var session = login();
		seed("Giữ nguyên", NOW, null);
		mvc.perform(get(path()).session(session).param("fromDate", "2026-02-29")).andExpect(status().isUnprocessableContent());
		mvc.perform(get(path()).session(session).param("fromDate", "2026-11-10").param("toDate", "2026-11-09")).andExpect(status().isUnprocessableContent());
		mvc.perform(get(path()).session(session).param("status", "ONLINE")).andExpect(status().isUnprocessableContent());
		mvc.perform(get(path() + "/bad").session(session)).andExpect(status().isUnprocessableContent());
		mvc.perform(get(path() + "/" + UUID.randomUUID()).session(session)).andExpect(status().isNotFound());
		assertThat(guests.count()).isEqualTo(1);
	}

	@Test
	void readsRequireAdminAndDoNotExtendQ3OrOfferAnEndSessionAction() throws Exception {
		var session = login();
		GuestSession guest = seed("Chỉ đọc", NOW, null);
		mvc.perform(get(path())).andExpect(status().isUnauthorized());
		clock.set(NOW.plus(Duration.ofMinutes(29)));
		mvc.perform(get(path()).session(session)).andExpect(status().isOk());
		mvc.perform(get(path() + "/" + guest.getId()).session(session)).andExpect(status().isOk());
		mvc.perform(get("/api/v1/admin/session").session(session)).andExpect(jsonPath("$.lastInteractiveAt").value(NOW.toString()));
		clock.set(NOW.plus(Duration.ofMinutes(30)));
		mvc.perform(get(path()).session(session)).andExpect(status().isUnauthorized());
		assertThat(guests.findById(guest.getId()).orElseThrow().getEndedAt()).isNull();
	}

	@Test
	void detailShowsOnlyThisSessionsCountedViewsAndRetainsDeletedPlaceIdentity() throws Exception {
		var session = login();
		var guest = seed("Chi tiết", NOW.minusSeconds(60), null);
		var other = seed("Phiên khác", NOW.minusSeconds(60), null);
		var place = places.saveAndFlush(new Place(UUID.randomUUID(), "Tên cũ", NOW));
		view(guest, place, "Tên cũ", "vi", NOW.minusSeconds(5));
		view(other, place, "Tên cũ", "fr", NOW.minusSeconds(4));
		places.deleteById(place.getId());
		mvc.perform(get(path() + "/" + guest.getId()).session(session)).andExpect(status().isOk())
				.andExpect(jsonPath("$.views.length()").value(1)).andExpect(jsonPath("$.views[0].placeDeleted").value(true))
				.andExpect(jsonPath("$.views[0].placeId").value(place.getId().toString()))
				.andExpect(jsonPath("$.views[0].placeName").value("Tên cũ"));
	}

	@Test
	void statisticsUseSessionStartDateAndViewDateIndependentlyWithTwoTotalsAndTwoGroups() throws Exception {
		var session = login();
		var previousDay = seed("Bắt đầu hôm trước", START.minusSeconds(3600), null);
		var today = seed("Bắt đầu hôm nay", NOW.minusSeconds(60), null);
		var place = places.saveAndFlush(new Place(UUID.randomUUID(), "Ngọ Môn fixture", NOW));
		view(previousDay, place, "Ngọ Môn fixture", "vi", START.plusSeconds(1));
		view(today, place, "Ngọ Môn fixture", "vi", NOW.minusSeconds(30));
		view(today, place, "Ngọ Môn fixture", "en", NOW.minusSeconds(20));
		mvc.perform(get("/api/v1/admin/statistics").session(session)).andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store"))
				.andExpect(jsonPath("$.sessionTotal").value(1)).andExpect(jsonPath("$.viewTotal").value(3))
				.andExpect(jsonPath("$.sessionsByDay.length()").value(1)).andExpect(jsonPath("$.sessionsByDay[0].sessions").value(1))
				.andExpect(jsonPath("$.viewsByPlaceLanguageDay.length()").value(2));
	}

	@Test
	void statisticsGroupByStablePlaceIdNotNameAndRenameDoesNotSplitOnePlacesGroup() throws Exception {
		var session = login();
		var guest = seed("Khách", NOW.minusSeconds(60), null);
		var first = places.saveAndFlush(new Place(UUID.randomUUID(), "Cùng tên", NOW));
		var second = places.saveAndFlush(new Place(UUID.randomUUID(), "Cùng tên", NOW));
		view(guest, first, "Tên trước", "vi", NOW.minusSeconds(30));
		view(guest, first, "Cùng tên", "vi", NOW.minusSeconds(20));
		view(guest, second, "Cùng tên", "vi", NOW.minusSeconds(10));
		places.deleteById(first.getId());
		var result = mvc.perform(get("/api/v1/admin/statistics").session(session)).andExpect(status().isOk())
				.andExpect(jsonPath("$.viewTotal").value(3)).andExpect(jsonPath("$.viewsByPlaceLanguageDay.length()").value(2)).andReturn();
		var response = json.readValue(result.getResponse().getContentAsString(), vn.hueqr.backend.dto.AdminStatisticsResponse.class);
		assertThat(response.viewsByPlaceLanguageDay()).filteredOn(v -> v.placeId().equals(first.getId())).singleElement()
				.satisfies(v -> { assertThat(v.views()).isEqualTo(2); assertThat(v.placeDeleted()).isTrue(); assertThat(v.placeName()).isEqualTo("Cùng tên"); });
	}

	@Test
	void statisticsDoNotIncludeOverdueD08SessionsOrTheirViewsBeforePhysicalPurge() throws Exception {
		var session = login();
		var overdue = seed("Quá hạn", NOW.minus(Duration.ofDays(30)).minusSeconds(3600), NOW.minus(Duration.ofDays(30)));
		var retained = seed("Chưa quá hạn", NOW.minus(Duration.ofDays(30)).minusSeconds(3600), NOW.minus(Duration.ofDays(30)).plusNanos(1000));
		var place = places.saveAndFlush(new Place(UUID.randomUUID(), "Điểm", NOW));
		view(overdue, place, "Điểm", "vi", overdue.getStartedAt().plusSeconds(1));
		view(retained, place, "Điểm", "vi", retained.getStartedAt().plusSeconds(1));
		mvc.perform(get("/api/v1/admin/statistics").session(session).param("fromDate", "2026-10-01").param("toDate", "2026-11-09"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.sessionTotal").value(1)).andExpect(jsonPath("$.viewTotal").value(1));
		assertThat(guests.count()).isEqualTo(2);
		assertThat(views.count()).isEqualTo(2);
	}

	@Test
	void statisticsRequireAdminValidateDatesAndDoNotExtendQ3() throws Exception {
		mvc.perform(get("/api/v1/admin/statistics")).andExpect(status().isUnauthorized());
		var session = login();
		mvc.perform(get("/api/v1/admin/statistics").session(session).param("toDate", "2026-02-29")).andExpect(status().isUnprocessableContent());
		clock.set(NOW.plus(Duration.ofMinutes(29)));
		mvc.perform(get("/api/v1/admin/statistics").session(session)).andExpect(status().isOk())
				.andExpect(jsonPath("$.sessionTotal").value(0)).andExpect(jsonPath("$.viewTotal").value(0));
		mvc.perform(get("/api/v1/admin/session").session(session)).andExpect(jsonPath("$.lastInteractiveAt").value(NOW.toString()));
	}

	private void view(GuestSession guest, Place place, String name, String language, Instant time) {
		views.saveAndFlush(new PlaceView(UUID.randomUUID(), guest, UUID.randomUUID(), place.getId(), place.getId(), name, language, time));
	}

	private GuestSession seed(String name, Instant start, Instant end) {
		byte[] hash = java.nio.ByteBuffer.allocate(32).putLong(UUID.randomUUID().getMostSignificantBits()).array();
		var guest = new GuestSession(UUID.randomUUID(), UUID.randomUUID(), hash, "00000000-0000-0000-0000-000000000001",
				new byte[32], start.minusSeconds(1), start.plusSeconds(86399), new byte[32], name, "vi", start, start.plusSeconds(28800));
		if (end != null)
			guest.endAt(end);
		return guests.saveAndFlush(guest);
	}

	private MockHttpSession login() throws Exception {
		var initial = mvc.perform(get("/api/v1/auth/csrf")).andReturn();
		var session = (MockHttpSession) initial.getRequest().getSession(false);
		var csrf = json.readValue(initial.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(post("/api/v1/auth/login").session(session).header(csrf.headerName(), csrf.token()).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new AdminLoginRequest("guest-read-owner", PASSWORD)))).andExpect(status().isOk());
		return session;
	}

	private static String path() {
		return "/api/v1/admin/guest-sessions";
	}
}
