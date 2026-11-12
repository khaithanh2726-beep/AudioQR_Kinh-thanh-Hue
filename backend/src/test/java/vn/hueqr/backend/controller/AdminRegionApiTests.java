package vn.hueqr.backend.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminRegionRevisionResponse;
import vn.hueqr.backend.dto.ApplyRegionRequest;
import vn.hueqr.backend.dto.SaveRegionRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.entity.OperationReceipt;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.AdminCommandWindowRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.RegionRevisionRepository;
import vn.hueqr.backend.service.RegionGeometryPolicy;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminRegionApiTests {
	private static final String PASSWORD = "Mat-khau-region-fixture";
	private static final String SQUARE = "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[2,0],[2,2],[0,2],[0,0]]]}";
	private static final String OTHER = "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[3,0],[3,3],[0,3],[0,0]]]}";

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

	@MockitoSpyBean
	private OperationReceiptRepository receipts;

	@Autowired
	private PlaceRepository places;

	@Autowired
	private RegionRevisionRepository regions;

	@Autowired
	private JdbcTemplate jdbc;

	@BeforeEach
	void setUp() {
		clearData();
		clock.set(GuestSessionTestClock.BASE);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "region-owner", passwords.encode(PASSWORD), true));
	}

	@AfterEach
	void clearData() {
		reset(receipts);
		receipts.deleteAll();
		windows.deleteAll();
		// Tháo con trỏ trước khi xóa vùng và địa điểm của fixture
		jdbc.update("UPDATE place SET active_region_revision_id = NULL");
		regions.deleteAll();
		places.deleteAll();
		accounts.deleteAll();
	}

	@Test
	void preciseCoordinatesSurviveMySqlAndRemainValidAfterApplyingTheRegion() throws Exception {
		String polygon = "{\"type\":\"Polygon\",\"coordinates\":[[[107.57891379087943,16.467354402659],"
				+ "[107.57956620912057,16.467645002659],[107.57922589087943,16.468010497340998],"
				+ "[107.57891379087943,16.467354402659]]]}";
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(polygon))
				.andExpect(status().isCreated())));
		var response = mvc.perform(get(regionPath(place.getId(), revision)).session(browser.session()))
				.andExpect(status().isOk()).andReturn();
		var saved = json.readValue(response.getResponse().getContentAsString(), AdminRegionRevisionResponse.class);
		var checked = new RegionGeometryPolicy(json).validate(saved.polygon(), saved.mapSpaceKey(), saved.geometryHash());
		assertThat(checked.polygon()).isEqualTo(polygon);
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), hash(polygon))
				.andExpect(status().isOk());
		var rejected = mvc.perform(put("/api/v1/admin/places/" + place.getId() + "/visibility").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).header("Idempotency-Key", key(window))
				.header("If-Match", "\"1\"").contentType(MediaType.APPLICATION_JSON).content("{\"visible\":true}"))
				.andExpect(status().isConflict()).andReturn();
		assertThat(json.readTree(rejected.getResponse().getContentAsString()).get("missingConditions"))
				.isEqualTo(json.readTree("[\"VI_PAIR_NOT_APPLIED\"]"));
	}

	@Test
	void savingAndReadingARevisionDoesNotApplyItOrChangePlaceVersion() throws Exception {
		Browser browser = login();
		Place original = seed();
		clock.set(clock.instant().plusNanos(123456789));
		var result = read(save(browser, original.getId(), key(issue(browser)), "\"0\"", request(SQUARE))
				.andExpect(status().isCreated()).andExpect(header().string("ETag", "\"0\""))
				.andExpect(header().string("Cache-Control", "no-store")));
		UUID revisionId = revisionId(result);
		var response = mvc.perform(get(regionPath(original.getId(), revisionId)).session(browser.session()))
				.andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store")).andReturn();
		var region = json.readValue(response.getResponse().getContentAsString(), AdminRegionRevisionResponse.class);
		assertThat(region.polygon()).isEqualTo(json.readTree(SQUARE));
		assertThat(region.geometryHash()).isEqualTo(hash(SQUARE));
		assertThat(region.placeId()).isEqualTo(original.getId());
		assertThat(region.savedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertPlaceUnchanged(original);
		assertThat(regions.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void overlappingRegionsOfDifferentPlacesCanBeSavedWithoutPublishingEitherPlace() throws Exception {
		Browser browser = login();
		String window = issue(browser);
		for (int i = 0; i < 2; i++) {
			Place place = seed();
			save(browser, place.getId(), key(window), "\"0\"", request(SQUARE)).andExpect(status().isCreated());
			assertPlaceUnchanged(place);
		}
		assertThat(regions.count()).isEqualTo(2);
	}

	@Test
	void readingARevisionRequiresItsOwnPlaceAndDoesNotExposeAnotherPlacesRegion() throws Exception {
		Browser browser = login();
		Place first = seed();
		Place another = seed();
		UUID revision = revisionId(read(save(browser, first.getId(), key(issue(browser)), "\"0\"", request(SQUARE))));
		mvc.perform(get(regionPath(another.getId(), revision)).session(browser.session()))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("REGION_REVISION_NOT_FOUND"));
		mvc.perform(get(regionPath(UUID.randomUUID(), revision)).session(browser.session()))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("LOCATION_NOT_FOUND"));
		mvc.perform(get(regionPath(first.getId(), UUID.randomUUID())).session(browser.session()))
				.andExpect(status().isNotFound());
	}

	@Test
	void malformedGeometryAndAHashMismatchLeaveNoRevisionOrReceipt() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		for (SaveRegionRequest input : List.of(new SaveRegionRequest(json.readTree(SQUARE), "wrong-space", hash(SQUARE)),
				new SaveRegionRequest(json.readTree(SQUARE.replace("[2,0]", "[0,0]")), RegionGeometryPolicy.MAP_SPACE_KEY, hash(SQUARE)))) {
			save(browser, place.getId(), key(window), "\"0\"", input).andExpect(status().isUnprocessableContent());
		}
		save(browser, place.getId(), key(window), "\"0\"",
				new SaveRegionRequest(json.readTree(SQUARE), RegionGeometryPolicy.MAP_SPACE_KEY, "0".repeat(64)))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("GEOMETRY_HASH_MISMATCH"));
		assertPlaceUnchanged(place);
		assertThat(regions.count()).isZero();
		assertThat(receipts.count()).isZero();
	}

	@Test
	void savingRequiresAStrongCurrentPlaceVersion() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		save(browser, place.getId(), key(window), null, request(SQUARE)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("PRECONDITION_REQUIRED"));
		save(browser, place.getId(), key(window), "W/\"0\"", request(SQUARE)).andExpect(status().isBadRequest());
		save(browser, place.getId(), key(window), "\"1\"", request(SQUARE)).andExpect(status().isPreconditionFailed());
		assertThat(regions.count()).isZero();
		assertThat(receipts.count()).isZero();
	}

	@Test
	void applyingTheSavedPreviewNeedsNoExtraReadAndDoesNotPublishThePlace() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), hash(SQUARE))
				.andExpect(status().isOk()).andExpect(header().string("ETag", "\"1\""))
				.andExpect(jsonPath("$.result.visible").value(false));
		Place current = places.findById(place.getId()).orElseThrow();
		assertThat(current.getActiveRegionRevisionId()).isEqualTo(revision);
		assertThat(current.getVersion()).isEqualTo(1);
		assertThat(current.isVisible()).isFalse();
		assertThat(current.getDisplayName()).isEqualTo(place.getDisplayName());
		assertThat(receipts.count()).isEqualTo(2);
	}

	@Test
	void applyingPreservesAnAlreadyVisiblePlaceAndDoesNotModifyTheSavedGeometry() throws Exception {
		Browser browser = login();
		Place place = seed();
		jdbc.update("UPDATE place SET visible = TRUE");
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), hash(SQUARE)).andExpect(status().isOk());
		assertThat(places.findById(place.getId()).orElseThrow().isVisible()).isTrue();
		assertThat(json.readTree(regions.findById(revision).orElseThrow().getPolygon())).isEqualTo(json.readTree(SQUARE));
	}

	@Test
	void savingAnotherDraftKeepsTheCurrentRegionAndItsVersion() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID first = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		apply(browser, place.getId(), key(window), "\"0\"", first.toString(), hash(SQUARE)).andExpect(status().isOk());
		save(browser, place.getId(), key(window), "\"1\"", request(OTHER)).andExpect(status().isCreated())
				.andExpect(header().string("ETag", "\"1\""));
		Place current = places.findById(place.getId()).orElseThrow();
		assertThat(current.getActiveRegionRevisionId()).isEqualTo(first);
		assertThat(current.getVersion()).isEqualTo(1);
		assertThat(regions.count()).isEqualTo(2);
	}

	@Test
	void applyingRejectsAnUnsavedForeignOrUnpreviewedRevision() throws Exception {
		Browser browser = login();
		Place place = seed();
		Place another = seed();
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		apply(browser, place.getId(), key(window), "\"0\"", UUID.randomUUID().toString(), hash(SQUARE))
				.andExpect(status().isNotFound());
		apply(browser, another.getId(), key(window), "\"0\"", revision.toString(), hash(SQUARE))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("REGION_REVISION_NOT_FOUND"));
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), hash(OTHER))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("GEOMETRY_HASH_MISMATCH"));
		apply(browser, place.getId(), key(window), "\"0\"", null, hash(SQUARE))
				.andExpect(status().isUnprocessableContent());
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), null)
				.andExpect(status().isUnprocessableContent());
		assertPlaceUnchanged(place);
		assertPlaceUnchanged(another);
		assertThat(regions.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void applyingRequiresTheCurrentStrongPlaceVersion() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		apply(browser, place.getId(), key(window), null, revision.toString(), hash(SQUARE)).andExpect(status().isBadRequest());
		apply(browser, place.getId(), key(window), "*", revision.toString(), hash(SQUARE)).andExpect(status().isBadRequest());
		apply(browser, place.getId(), key(window), "\"1\"", revision.toString(), hash(SQUARE)).andExpect(status().isPreconditionFailed());
		assertPlaceUnchanged(place);
	}

	@Test
	void replayingASaveAfterItsPlaceWasDeletedReturnsTheCommittedResultWithoutRecreatingData() throws Exception {
		Browser browser = login();
		Place place = seed();
		String key = key(issue(browser));
		AdminOperationResponse first = read(save(browser, place.getId(), key, "\"0\"", request(SQUARE)));
		regions.deleteAll();
		places.deleteAll();
		AdminOperationResponse replay = read(save(browser, place.getId(), key, "\"0\"", request(SQUARE))
				.andExpect(status().isCreated()));
		assertThat(replay.operationId()).isEqualTo(first.operationId());
		assertThat(replay.replayed()).isTrue();
		assertThat(regions.count()).isZero();
		assertThat(places.count()).isZero();
	}

	@Test
	void jsonFormattingAndNumericSpellingDoNotExecuteTheSameSaveTwice() throws Exception {
		Browser browser = login();
		Place place = seed();
		String key = key(issue(browser));
		AdminOperationResponse first = read(save(browser, place.getId(), key, "\"0\"", request(SQUARE)));
		String equivalent = "{\"coordinates\":[[[0.00,0],[2e0,0],[2,2],[0,2],[0,0]]],\"type\":\"Polygon\"}";
		AdminOperationResponse replay = read(save(browser, place.getId(), key, "\"0\"",
				new SaveRegionRequest(json.readTree(equivalent), RegionGeometryPolicy.MAP_SPACE_KEY, hash(SQUARE)))
				.andExpect(status().isCreated()));
		assertThat(replay.operationId()).isEqualTo(first.operationId());
		assertThat(replay.replayed()).isTrue();
		assertThat(regions.count()).isEqualTo(1);
	}

	@Test
	void changingGeometryUnderTheSameOperationKeyIsRejectedBeforeSavingAnotherRevision() throws Exception {
		Browser browser = login();
		Place place = seed();
		String key = key(issue(browser));
		save(browser, place.getId(), key, "\"0\"", request(SQUARE)).andExpect(status().isCreated());
		save(browser, place.getId(), key, "\"0\"", request(OTHER)).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("KEY_REUSED"));
		assertThat(regions.count()).isEqualTo(1);
	}

	@Test
	void anApplyReplayReturnsItsOriginalVersionAfterAnotherRegionHasBeenApplied() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID first = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		String applyKey = key(window);
		AdminOperationResponse applied = read(apply(browser, place.getId(), applyKey, "\"0\"", first.toString(), hash(SQUARE)));
		UUID next = revisionId(read(save(browser, place.getId(), key(window), "\"1\"", request(OTHER))));
		apply(browser, place.getId(), key(window), "\"1\"", next.toString(), hash(OTHER)).andExpect(status().isOk());
		AdminOperationResponse replay = read(apply(browser, place.getId(), applyKey, "\"0\"", first.toString(), hash(SQUARE))
				.andExpect(status().isOk()).andExpect(header().string("ETag", "\"1\"")));
		assertThat(replay.operationId()).isEqualTo(applied.operationId());
		assertThat(replay.replayed()).isTrue();
		assertThat(places.findById(place.getId()).orElseThrow().getActiveRegionRevisionId()).isEqualTo(next);
		assertThat(places.findById(place.getId()).orElseThrow().getVersion()).isEqualTo(2);
	}

	@Test
	void aNewKeyApplyingTheCurrentRegionDoesNotIncrementItsVersionAgain() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), hash(SQUARE)).andExpect(status().isOk());
		apply(browser, place.getId(), key(window), "\"1\"", revision.toString(), hash(SQUARE)).andExpect(status().isOk())
				.andExpect(header().string("ETag", "\"1\""));
		assertThat(places.findById(place.getId()).orElseThrow().getVersion()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(3);
	}

	@Test
	void concurrentRequestsWithTheSameSaveKeyCreateOneRevisionAndReceipt() throws Exception {
		Browser browser = login();
		Place place = seed();
		String key = key(issue(browser));
		CyclicBarrier ready = new CyclicBarrier(2);
		try (var pool = Executors.newFixedThreadPool(2)) {
			var a = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(save(browser, place.getId(), key, "\"0\"", request(SQUARE)).andExpect(status().isCreated()));
			});
			var b = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return read(save(browser, place.getId(), key, "\"0\"", request(SQUARE)).andExpect(status().isCreated()));
			});
			assertThat(a.get(30, TimeUnit.SECONDS).operationId()).isEqualTo(b.get(30, TimeUnit.SECONDS).operationId());
		}
		assertThat(regions.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void twoWindowsApplyingDifferentRegionsAtTheSameVersionCannotOverwriteEachOther() throws Exception {
		Browser browser = login();
		Place place = seed();
		String firstWindow = issue(browser);
		String secondWindow = issue(browser);
		UUID first = revisionId(read(save(browser, place.getId(), key(firstWindow), "\"0\"", request(SQUARE))));
		UUID second = revisionId(read(save(browser, place.getId(), key(secondWindow), "\"0\"", request(OTHER))));
		// Hai cửa sổ độc lập tranh cùng version, không chỉ tranh khóa chống gửi lặp của một cửa sổ
		CyclicBarrier ready = new CyclicBarrier(2);
		try (var pool = Executors.newFixedThreadPool(2)) {
			var a = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return apply(browser, place.getId(), key(firstWindow), "\"0\"", first.toString(), hash(SQUARE))
						.andReturn().getResponse().getStatus();
			});
			var b = pool.submit(() -> {
				ready.await(15, TimeUnit.SECONDS);
				return apply(browser, place.getId(), key(secondWindow), "\"0\"", second.toString(), hash(OTHER))
						.andReturn().getResponse().getStatus();
			});
			assertThat(List.of(a.get(30, TimeUnit.SECONDS), b.get(30, TimeUnit.SECONDS)))
					.containsExactlyInAnyOrder(200, 412);
		}
		assertThat(places.findById(place.getId()).orElseThrow().getVersion()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(3);
	}

	@Test
	void failureToStoreTheSaveReceiptRollsBackTheAlreadyFlushedRevision() throws Exception {
		Browser browser = login();
		Place place = seed();
		String key = key(issue(browser));
		doThrow(new DataIntegrityViolationException("receipt fixture failure"))
				.when(receipts).saveAndFlush(any(OperationReceipt.class));
		save(browser, place.getId(), key, "\"0\"", request(SQUARE)).andExpect(status().isInternalServerError());
		assertThat(regions.count()).isZero();
		assertThat(receipts.count()).isZero();
		assertPlaceUnchanged(place);
	}

	@Test
	void failureToStoreTheApplyReceiptRollsBackTheRegionPointerAndPlaceVersion() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		doThrow(new DataIntegrityViolationException("receipt fixture failure"))
				.when(receipts).saveAndFlush(any(OperationReceipt.class));
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), hash(SQUARE))
				.andExpect(status().isInternalServerError());
		assertPlaceUnchanged(place);
		assertThat(regions.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void readingDoesNotExtendQ3AndExpiredSessionsCannotReadOrWriteRegions() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		UUID revision = revisionId(read(save(browser, place.getId(), key(window), "\"0\"", request(SQUARE))));
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(29)));
		mvc.perform(get(regionPath(place.getId(), revision)).session(browser.session())).andExpect(status().isOk());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)));
		apply(browser, place.getId(), key(window), "\"0\"", revision.toString(), hash(SQUARE)).andExpect(status().isUnauthorized());
		mvc.perform(get(regionPath(place.getId(), revision))).andExpect(status().isUnauthorized());
		assertPlaceUnchanged(place);
	}

	@Test
	void invalidCsrfAndMalformedJsonCannotChangeRegionsOrExtendQ3() throws Exception {
		Browser browser = login();
		Place place = seed();
		String key = key(issue(browser));
		clock.set(clock.instant().plus(Duration.ofMinutes(20)));
		mvc.perform(post(savePath(place.getId())).session(browser.session()).header("Idempotency-Key", key)
				.header("If-Match", "\"0\"").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request(SQUARE))))
				.andExpect(status().isForbidden());
		mvc.perform(put(applyPath(place.getId())).session(browser.session()).header("Idempotency-Key", key)
				.header("If-Match", "\"0\"").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
		mvc.perform(post(savePath(place.getId())).session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token())
				.contentType(MediaType.APPLICATION_JSON).content("{")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		assertThat(regions.count()).isZero();
	}

	@Test
	void anExpiredCommandWindowCannotReplayASavedRevisionAfterLoginAgain() throws Exception {
		Browser browser = login();
		Place place = seed();
		String key = key(issue(browser));
		save(browser, place.getId(), key, "\"0\"", request(SQUARE)).andExpect(status().isCreated());
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofHours(24)));
		Browser again = login();
		save(again, place.getId(), key, "\"0\"", request(SQUARE)).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("COMMAND_WINDOW_EXPIRED"));
		assertThat(regions.count()).isEqualTo(1);
	}

	private Place seed() {
		return places.saveAndFlush(new Place(UUID.randomUUID(), "Điểm fixture", clock.instant()));
	}

	private void assertPlaceUnchanged(Place original) {
		Place current = places.findById(original.getId()).orElseThrow();
		assertThat(current.getVersion()).isEqualTo(original.getVersion());
		assertThat(current.getActiveRegionRevisionId()).isEqualTo(original.getActiveRegionRevisionId());
		assertThat(current.isVisible()).isEqualTo(original.isVisible());
		assertThat(current.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
	}

	private Browser login() throws Exception {
		var initial = mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk()).andReturn();
		MockHttpSession session = (MockHttpSession) initial.getRequest().getSession(false);
		AdminCsrfResponse csrf = json.readValue(initial.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(post("/api/v1/auth/login").session(session).header(csrf.headerName(), csrf.token())
				.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(new AdminLoginRequest("region-owner", PASSWORD))))
				.andExpect(status().isOk());
		var current = mvc.perform(get("/api/v1/auth/csrf").session(session)).andExpect(status().isOk()).andReturn();
		return new Browser(session, json.readValue(current.getResponse().getContentAsString(), AdminCsrfResponse.class));
	}

	private String issue(Browser browser) throws Exception {
		var result = mvc.perform(post("/api/v1/admin/command-windows").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token())).andExpect(status().isCreated()).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminCommandWindowResponse.class).windowId().toString();
	}

	private ResultActions save(Browser browser, UUID place, String key, String ifMatch, SaveRegionRequest input) throws Exception {
		var request = post(savePath(place)).session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token())
				.header("Idempotency-Key", key).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(input));
		if (ifMatch != null)
			request.header("If-Match", ifMatch);
		return mvc.perform(request);
	}

	private ResultActions apply(Browser browser, UUID place, String key, String ifMatch, String revision, String hash) throws Exception {
		var request = put(applyPath(place)).session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token())
				.header("Idempotency-Key", key).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new ApplyRegionRequest(revision, hash)));
		if (ifMatch != null)
			request.header("If-Match", ifMatch);
		return mvc.perform(request);
	}

	private SaveRegionRequest request(String polygon) throws Exception {
		return new SaveRegionRequest(json.readTree(polygon), RegionGeometryPolicy.MAP_SPACE_KEY, hash(polygon));
	}

	private String hash(String polygon) throws Exception {
		return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
				.digest((RegionGeometryPolicy.MAP_SPACE_KEY + "\n" + polygon).getBytes(StandardCharsets.UTF_8)));
	}

	private AdminOperationResponse read(ResultActions result) throws Exception {
		return json.readValue(result.andReturn().getResponse().getContentAsString(), AdminOperationResponse.class);
	}

	private UUID revisionId(AdminOperationResponse result) {
		return UUID.fromString(result.result().get("regionRevisionId").toString());
	}

	private static String key(String window) {
		return window + ":" + UUID.randomUUID();
	}

	private static String savePath(UUID place) {
		return "/api/v1/admin/places/" + place + "/region-revisions";
	}

	private static String regionPath(UUID place, UUID revision) {
		return savePath(place) + "/" + revision;
	}

	private static String applyPath(UUID place) {
		return "/api/v1/admin/places/" + place + "/active-region";
	}
}
