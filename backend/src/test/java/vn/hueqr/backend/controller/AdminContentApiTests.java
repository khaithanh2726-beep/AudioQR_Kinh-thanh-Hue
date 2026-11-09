package vn.hueqr.backend.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminContentResponse;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminTextReviewReferenceResponse;
import vn.hueqr.backend.dto.SaveTextRevisionRequest;
import vn.hueqr.backend.dto.SaveTextReviewReferenceRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.entity.OperationReceipt;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.AdminCommandWindowRepository;
import vn.hueqr.backend.repository.ContentSlotRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.RegionRevisionRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TextReviewReferenceRepository;
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
class AdminContentApiTests {
	private static final String PASSWORD = "Mat-khau-content-fixture";

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
	private ContentSlotRepository slots;

	@Autowired
	private TextRevisionRepository texts;

	@Autowired
	private TextReviewReferenceRepository references;

	@Autowired
	private JdbcTemplate jdbc;

	@BeforeEach
	void setUp() {
		clearData();
		clock.set(GuestSessionTestClock.BASE);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "content-owner", passwords.encode(PASSWORD), true));
	}

	@AfterEach
	void clearData() {
		reset(receipts);
		receipts.deleteAll();
		windows.deleteAll();
		references.deleteAll();
		// Tháo con trỏ để dọn vòng khóa ngoại giữa ngăn và văn bản của fixture
		jdbc.update("UPDATE content_slot SET draft_text_id = NULL");
		texts.deleteAll();
		slots.deleteAll();
		jdbc.update("UPDATE place SET active_region_revision_id = NULL");
		regions.deleteAll();
		places.deleteAll();
		accounts.deleteAll();
	}

	@Test
	void readingEmptyLanguagesDoesNotCreateSlotsDraftsOrVietnameseFallback() throws Exception {
		Browser browser = login();
		Place place = seed();
		for (String language : new String[]{"vi", "en", "fr", "ru"}) {
			var state = detail(browser, place.getId(), language);
			assertThat(state.slotId()).isNull();
			assertThat(state.draftText()).isNull();
			assertThat(state.etag()).isEqualTo(placeTag(place));
		}
		assertThat(slots.count()).isZero();
		assertThat(texts.count()).isZero();
		assertThat(receipts.count()).isZero();
	}

	@Test
	void savingPreservesExactTextHashMicrosecondsAndHiddenPlaceWithoutPublishing() throws Exception {
		Browser browser = login();
		Place place = seed();
		String body = "  Huế 😀\nVăn bản\r\n";
		clock.set(clock.instant().plusNanos(123456789));
		var result = read(save(browser, place.getId(), "vi", key(issue(browser)), placeTag(place), body, null)
				.andExpect(status().isCreated()).andExpect(header().string("Cache-Control", "no-store")));
		var state = detail(browser, place.getId(), "vi");
		assertThat(state.draftText().body()).isEqualTo(body);
		assertThat(state.draftText().contentHash()).isEqualTo(hash(body));
		assertThat(state.draftText().savedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertThat(state.etag()).isEqualTo(result.result().get("etag"));
		assertThat(state.version()).isEqualTo(1L);
		assertThat(state.draftText().reviewReference().locator()).isNull();
		assertThat(result.result()).doesNotContainKeys("body", "locator", "limitations");
		assertPlaceUnchanged(place);
	}

	@Test
	void fourLanguagesHaveIndependentSlotsAndSavingAnotherLanguageDoesNotReuseText() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		for (String language : new String[]{"vi", "en", "fr", "ru"}) {
			save(browser, place.getId(), language, key(window), placeTag(place), language, null).andExpect(status().isCreated());
			assertThat(detail(browser, place.getId(), language).draftText().body()).isEqualTo(language);
		}
		assertThat(slots.count()).isEqualTo(4);
		assertThat(texts.count()).isEqualTo(4);
		assertPlaceUnchanged(place);
	}

	@Test
	void savingANewRevisionKeepsOldTextAndReviewReferenceWithoutCopyingApproval() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var first = initial(browser, place, window, "T1");
		UUID tid = first.draftText().textRevisionId();
		writeReview(browser, tid, key(window), first.draftText().reviewReference().etag(), "Hồ sơ, mục T1", "Chưa rà bản dịch")
				.andExpect(status().isOk()).andExpect(jsonPath("$.resultCode").value("TEXT_REVIEW_REFERENCE_RECORDED"));
		save(browser, place.getId(), "vi", key(window), first.etag(), "T2", tid.toString()).andExpect(status().isCreated());
		var second = detail(browser, place.getId(), "vi");
		assertThat(second.draftText().textRevisionId()).isNotEqualTo(tid);
		assertThat(second.draftText().reviewReference().locator()).isNull();
		assertThat(texts.findById(tid).orElseThrow().getBody()).isEqualTo("T1");
		assertThat(review(browser, tid).locator()).isEqualTo("Hồ sơ, mục T1");
		assertThat(references.count()).isEqualTo(1);
		assertThat(texts.count()).isEqualTo(2);
		assertPlaceUnchanged(place);
	}

	@Test
	void explicitSavesOfIdenticalTextStillCreateDistinctImmutableRevisions() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var first = initial(browser, place, window, "Cùng văn bản");
		save(browser, place.getId(), "vi", key(window), first.etag(), "Cùng văn bản", first.draftText().textRevisionId().toString())
				.andExpect(status().isCreated());
		var second = detail(browser, place.getId(), "vi");
		assertThat(second.draftText().textRevisionId()).isNotEqualTo(first.draftText().textRevisionId());
		assertThat(second.draftText().contentHash()).isEqualTo(first.draftText().contentHash());
		assertThat(texts.count()).isEqualTo(2);
	}

	@Test
	void invalidBodiesDoNotCreateAnEmptySlotAndTheSameKeyCanRetryAfterValidationFailure() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		String retry = key(window);
		for (String body : new String[]{null, "", " \r\n\t\u00a0", "bad\ud800"})
			save(browser, place.getId(), "vi", retry, placeTag(place), body, null).andExpect(status().isUnprocessableContent())
					.andExpect(jsonPath("$.fieldErrors.body").exists());
		assertThat(slots.count()).isZero();
		assertThat(receipts.count()).isZero();
		save(browser, place.getId(), "vi", retry, placeTag(place), "Hợp lệ", null).andExpect(status().isCreated());
	}

	@Test
	void validatesPlaceLanguageAndTextIdentifiersWithoutCreatingMissingTargets() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		for (String language : new String[]{"de", "VI"}) {
			mvc.perform(get(path(place.getId(), language)).session(browser.session())).andExpect(status().isUnprocessableContent());
			save(browser, place.getId(), language, key(window), placeTag(place), "Text", null).andExpect(status().isUnprocessableContent());
		}
		mvc.perform(get(path(UUID.randomUUID(), "vi")).session(browser.session())).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("LOCATION_NOT_FOUND"));
		mvc.perform(get("/api/v1/admin/places/bad/content/vi").session(browser.session())).andExpect(status().isUnprocessableContent());
		save(browser, place.getId(), "vi", key(window), placeTag(place), "Text", "bad").andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.fieldErrors.baseTextId").exists());
		UUID missing = UUID.randomUUID();
		writeReview(browser, missing, key(window), reviewAbsent(missing), "Hồ sơ", null).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("TEXT_REVISION_NOT_FOUND"));
		assertThat(slots.count()).isZero();
		assertThat(receipts.count()).isZero();
	}

	@Test
	void requiresCorrectBaseTextAndRejectsForeignOrStaleBasesEvenWithCurrentEtag() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var first = initial(browser, place, window, "T1");
		for (String base : new String[]{null, UUID.randomUUID().toString()})
			save(browser, place.getId(), "vi", key(window), first.etag(), "T2", base).andExpect(status().isConflict())
					.andExpect(jsonPath("$.code").value("BASE_TEXT_CONFLICT"));
		assertThat(texts.count()).isEqualTo(1);
	}

	@Test
	void enforcesTypedPreconditionsAndInitialPlaceVersionWithoutTreatingSlotAsAbsent() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		save(browser, place.getId(), "vi", key(window), null, "T1", null).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("PRECONDITION_REQUIRED"));
		save(browser, place.getId(), "vi", key(window), "\"0\"", "T1", null).andExpect(status().isBadRequest());
		place.rename("Đổi tên", clock.instant());
		place = places.saveAndFlush(place);
		String stale = "\"place:" + place.getId() + ":0\"";
		save(browser, place.getId(), "vi", key(window), stale, "T1", null).andExpect(status().isPreconditionFailed());
		var first = initial(browser, place, window, "T1");
		save(browser, place.getId(), "vi", key(window), placeTag(place), "T2", first.draftText().textRevisionId().toString())
				.andExpect(status().isPreconditionFailed());
		assertThat(texts.count()).isEqualTo(1);
	}

	@Test
	void reviewVersionIsIndependentOfDraftVersionAndRecordsTheAccountNotAnInventedReviewer() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var state = initial(browser, place, window, "Văn bản");
		UUID tid = state.draftText().textRevisionId();
		var absent = review(browser, tid);
		assertThat(absent.version()).isNull();
		clock.set(clock.instant().plusNanos(123456789));
		read(writeReview(browser, tid, key(window), absent.etag(), "Tài liệu A, mục 2", null).andExpect(status().isOk()));
		var recorded = review(browser, tid);
		assertThat(recorded.version()).isZero();
		assertThat(recorded.contentHash()).isEqualTo(hash("Văn bản"));
		assertThat(recorded.recordedBy()).isEqualTo(accounts.findAll().getFirst().getId());
		assertThat(recorded.recordedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertThat(recorded.limitations()).isNull();
		writeReview(browser, tid, key(window), absent.etag(), "Ghi đè", null).andExpect(status().isPreconditionFailed());
		clock.set(clock.instant().plusSeconds(1));
		writeReview(browser, tid, key(window), recorded.etag(), "Tài liệu B", "Giới hạn bản dịch").andExpect(status().isOk());
		assertThat(review(browser, tid).version()).isEqualTo(1L);
		assertThat(detail(browser, place.getId(), "vi").etag()).isEqualTo(state.etag());
		assertPlaceUnchanged(place);
	}

	@Test
	void referencesRequireSavedTextsAndNonblankLocatorsButDoNotFetchExternalDocuments() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var state = initial(browser, place, window, "Text");
		UUID tid = state.draftText().textRevisionId();
		String etag = state.draftText().reviewReference().etag();
		for (String locator : new String[]{null, "", "\u00a0\n", "bad\udc00"})
			writeReview(browser, tid, key(window), etag, locator, null).andExpect(status().isUnprocessableContent());
		writeReview(browser, tid, key(window), etag, "Hồ sơ", "bad\ud800").andExpect(status().isUnprocessableContent());
		assertThat(references.count()).isZero();
		// Miền .invalid chỉ là vị trí hồ sơ được ghi lại, server không kiểm tài liệu qua mạng
		writeReview(browser, tid, key(window), etag, "https://review.invalid/record#text", null).andExpect(status().isOk());
		assertThat(review(browser, tid).locator()).isEqualTo("https://review.invalid/record#text");
		assertThat(texts.count()).isEqualTo(1);
	}

	@Test
	void replayReturnsTheOriginalTextResultAfterANewerDraftWasSaved() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		String key = key(window);
		var first = read(save(browser, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isCreated()));
		var state = detail(browser, place.getId(), "vi");
		save(browser, place.getId(), "vi", key(window), state.etag(), "T2", state.draftText().textRevisionId().toString())
				.andExpect(status().isCreated());
		var replay = read(save(browser, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isCreated()));
		assertThat(replay.replayed()).isTrue();
		assertThat(replay.result()).isEqualTo(first.result());
		assertThat(replay.operationId()).isEqualTo(first.operationId());
		assertThat(detail(browser, place.getId(), "vi").draftText().body()).isEqualTo("T2");
		assertThat(texts.count()).isEqualTo(2);
	}

	@Test
	void replayReviewDoesNotOverwriteANewerReferenceAndReceiptsContainNoDocumentOrText() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var state = initial(browser, place, window, "Văn bản kín fixture");
		UUID tid = state.draftText().textRevisionId();
		String key = key(window);
		String etag = reviewAbsent(tid);
		var first = read(writeReview(browser, tid, key, etag, "Hồ sơ kín fixture", "Giới hạn kín fixture").andExpect(status().isOk()));
		clock.set(clock.instant().plusSeconds(1));
		writeReview(browser, tid, key(window), review(browser, tid).etag(), "Hồ sơ mới", null).andExpect(status().isOk());
		var replay = read(writeReview(browser, tid, key, etag, "Hồ sơ kín fixture", "Giới hạn kín fixture").andExpect(status().isOk()));
		assertThat(replay.replayed()).isTrue();
		assertThat(replay.result()).isEqualTo(first.result());
		assertThat(review(browser, tid).locator()).isEqualTo("Hồ sơ mới");
		assertThat(receipts.findAll()).allSatisfy(receipt -> assertThat(receipt.getResultSummary())
				.doesNotContain("Văn bản kín fixture", "Hồ sơ kín fixture", "Giới hạn kín fixture", "Hồ sơ mới"));
	}

	@Test
	void keyReuseRejectsChangedBodyTargetPreconditionAndOperationType() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		String key = key(window);
		save(browser, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isCreated());
		var state = detail(browser, place.getId(), "vi");
		List<ResultActions> changed = List.of(
				save(browser, place.getId(), "vi", key, placeTag(place), "Changed", null),
				save(browser, place.getId(), "en", key, placeTag(place), "T1", null),
				save(browser, place.getId(), "vi", key, state.etag(), "T1", null),
				writeReview(browser, state.draftText().textRevisionId(), key, reviewAbsent(state.draftText().textRevisionId()), "Hồ sơ", null));
		for (var response : changed)
			response.andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("KEY_REUSED"));
		assertThat(texts.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void aReceiptFailureRollsBackNewSlotTextPointerAndReferenceAndAllowsRetry() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		String key = key(window);
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		save(browser, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isInternalServerError());
		assertThat(slots.count()).isZero();
		assertThat(texts.count()).isZero();
		assertThat(receipts.count()).isZero();
		reset(receipts);
		save(browser, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isCreated());
		var state = detail(browser, place.getId(), "vi");
		UUID tid = state.draftText().textRevisionId();
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		writeReview(browser, tid, key(window), reviewAbsent(tid), "Hồ sơ", null).andExpect(status().isInternalServerError());
		assertThat(references.count()).isZero();
		assertThat(detail(browser, place.getId(), "vi").etag()).isEqualTo(state.etag());
		reset(receipts);
		writeReview(browser, tid, key(window), reviewAbsent(tid), "Hồ sơ", null).andExpect(status().isOk());
	}

	@Test
	void aReceiptFailureDuringLaterSaveOrReviewUpdatePreservesThePreviousState() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var state = initial(browser, place, window, "T1");
		UUID tid = state.draftText().textRevisionId();
		writeReview(browser, tid, key(window), reviewAbsent(tid), "Hồ sơ cũ", null).andExpect(status().isOk());
		var ref = review(browser, tid);
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		save(browser, place.getId(), "vi", key(window), state.etag(), "T2", tid.toString()).andExpect(status().isInternalServerError());
		writeReview(browser, tid, key(window), ref.etag(), "Hồ sơ mới", null).andExpect(status().isInternalServerError());
		assertThat(texts.count()).isEqualTo(1);
		assertThat(detail(browser, place.getId(), "vi").etag()).isEqualTo(state.etag());
		assertThat(review(browser, tid)).isEqualTo(ref);
	}

	@Test
	void simultaneousFirstSavesFromDifferentWindowsCreateOnlyOneSlotAndOneDraft() throws Exception {
		Browser first = login();
		Browser second = login();
		Place place = seed();
		String firstKey = key(issue(first));
		String secondKey = key(issue(second));
		assertThat(concurrently(
				() -> save(first, place.getId(), "vi", firstKey, placeTag(place), "A", null),
				() -> save(second, place.getId(), "vi", secondKey, placeTag(place), "B", null)))
				.containsExactlyInAnyOrder(201, 412);
		assertThat(slots.count()).isEqualTo(1);
		assertThat(texts.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void simultaneousUpdatesFromDifferentWindowsCannotOverwriteTheSameBaseDraft() throws Exception {
		Browser first = login();
		Browser second = login();
		Place place = seed();
		String firstWindow = issue(first);
		var state = initial(first, place, firstWindow, "T1");
		String firstKey = key(firstWindow);
		String secondKey = key(issue(second));
		String base = state.draftText().textRevisionId().toString();
		assertThat(concurrently(
				() -> save(first, place.getId(), "vi", firstKey, state.etag(), "A", base),
				() -> save(second, place.getId(), "vi", secondKey, state.etag(), "B", base)))
				.containsExactlyInAnyOrder(201, 412);
		assertThat(texts.count()).isEqualTo(2);
		assertThat(receipts.count()).isEqualTo(2);
	}

	@Test
	void simultaneousReferenceCreationCannotOverwriteAnotherUsersReference() throws Exception {
		Browser first = login();
		Browser second = login();
		Place place = seed();
		String firstWindow = issue(first);
		var state = initial(first, place, firstWindow, "T1");
		UUID tid = state.draftText().textRevisionId();
		String firstKey = key(firstWindow);
		String secondKey = key(issue(second));
		assertThat(concurrently(
				() -> writeReview(first, tid, firstKey, reviewAbsent(tid), "A", null),
				() -> writeReview(second, tid, secondKey, reviewAbsent(tid), "B", null)))
				.containsExactlyInAnyOrder(200, 412);
		assertThat(references.count()).isEqualTo(1);
	}

	@Test
	void simultaneousSameKeySavesReturnOneCommittedResultAndOneReplay() throws Exception {
		Browser first = login();
		Browser second = login();
		Place place = seed();
		String key = key(issue(first));
		assertThat(concurrently(
				() -> save(first, place.getId(), "vi", key, placeTag(place), "T1", null),
				() -> save(second, place.getId(), "vi", key, placeTag(place), "T1", null)))
				.containsExactly(201, 201);
		assertThat(slots.count()).isEqualTo(1);
		assertThat(texts.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void readRequestsDoNotExtendQ3ButAnExplicitInvalidSaveCountsAsInteraction() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		var state = initial(browser, place, window, "T1");
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(20)));
		detail(browser, place.getId(), "vi");
		review(browser, state.draftText().textRevisionId());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		save(browser, place.getId(), "vi", key(window), state.etag(), "", state.draftText().textRevisionId().toString())
				.andExpect(status().isUnprocessableContent());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(clock.instant().toString()));
		clock.set(clock.instant().plus(Duration.ofMinutes(30)));
		mvc.perform(get(path(place.getId(), "vi")).session(browser.session())).andExpect(status().isUnauthorized());
		assertThat(texts.count()).isEqualTo(1);
	}

	@Test
	void csrfMalformedJsonAndUnauthenticatedRequestsCannotWriteOrExtendQ3() throws Exception {
		Browser browser = login();
		Place place = seed();
		clock.set(clock.instant().plus(Duration.ofMinutes(20)));
		mvc.perform(post(path(place.getId(), "vi") + "/text-revisions").session(browser.session())
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
		mvc.perform(put(reviewPath(UUID.randomUUID())).session(browser.session()).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
		mvc.perform(post(path(place.getId(), "vi") + "/text-revisions").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).contentType(MediaType.APPLICATION_JSON).content("{"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
		mvc.perform(get(path(place.getId(), "vi"))).andExpect(status().isUnauthorized());
		mvc.perform(get(reviewPath(UUID.randomUUID()))).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		assertThat(slots.count()).isZero();
		assertThat(receipts.count()).isZero();
	}

	@Test
	void commandWindowOwnershipAndExpiryAreCheckedBeforeTextReplay() throws Exception {
		Browser browser = login();
		Place place = seed();
		String window = issue(browser);
		String key = key(window);
		save(browser, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isCreated());
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "other-content-owner", passwords.encode(PASSWORD), true));
		Browser other = login("other-content-owner");
		save(other, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isForbidden());
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofHours(24)));
		Browser again = login();
		save(again, place.getId(), "vi", key, placeTag(place), "T1", null).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("COMMAND_WINDOW_EXPIRED"));
		assertThat(texts.count()).isEqualTo(1);
	}

	private List<Integer> concurrently(Callable<ResultActions> first, Callable<ResultActions> second) throws Exception {
		// Hai phiên trình duyệt cùng vượt rào để giao dịch thực sự tranh chấp, không chỉ gọi lần lượt
		var barrier = new CyclicBarrier(2);
		try (var pool = Executors.newFixedThreadPool(2)) {
			var a = pool.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return first.call().andReturn().getResponse().getStatus();
			});
			var b = pool.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return second.call().andReturn().getResponse().getStatus();
			});
			return List.of(a.get(20, TimeUnit.SECONDS), b.get(20, TimeUnit.SECONDS));
		}
	}

	private AdminContentResponse initial(Browser browser, Place place, String window, String body) throws Exception {
		save(browser, place.getId(), "vi", key(window), placeTag(place), body, null).andExpect(status().isCreated());
		return detail(browser, place.getId(), "vi");
	}

	private AdminContentResponse detail(Browser browser, UUID place, String language) throws Exception {
		var result = mvc.perform(get(path(place, language)).session(browser.session())).andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store")).andReturn();
		var state = json.readValue(result.getResponse().getContentAsString(), AdminContentResponse.class);
		assertThat(result.getResponse().getHeader("ETag")).isEqualTo(state.etag());
		return state;
	}

	private AdminTextReviewReferenceResponse review(Browser browser, UUID text) throws Exception {
		var result = mvc.perform(get(reviewPath(text)).session(browser.session())).andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store")).andReturn();
		var state = json.readValue(result.getResponse().getContentAsString(), AdminTextReviewReferenceResponse.class);
		assertThat(result.getResponse().getHeader("ETag")).isEqualTo(state.etag());
		return state;
	}

	private Place seed() {
		return places.saveAndFlush(new Place(UUID.randomUUID(), "Địa điểm nội dung fixture", clock.instant()));
	}

	private void assertPlaceUnchanged(Place original) {
		Place current = places.findById(original.getId()).orElseThrow();
		assertThat(current.getVersion()).isEqualTo(original.getVersion());
		assertThat(current.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
		assertThat(current.getActiveRegionRevisionId()).isEqualTo(original.getActiveRegionRevisionId());
		assertThat(current.isVisible()).isFalse();
	}

	private Browser login() throws Exception {
		return login("content-owner");
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

	private String issue(Browser browser) throws Exception {
		var result = mvc.perform(post("/api/v1/admin/command-windows").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token())).andExpect(status().isCreated()).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminCommandWindowResponse.class).windowId().toString();
	}

	private ResultActions save(Browser browser, UUID place, String language, String key, String etag, String body, String base) throws Exception {
		return perform(browser, post(path(place, language) + "/text-revisions"), key, etag, new SaveTextRevisionRequest(body, base));
	}

	private ResultActions writeReview(Browser browser, UUID text, String key, String etag, String locator, String limitations) throws Exception {
		return perform(browser, put(reviewPath(text)), key, etag, new SaveTextReviewReferenceRequest(locator, limitations));
	}

	private ResultActions perform(Browser browser, MockHttpServletRequestBuilder request, String key, String etag, Object body) throws Exception {
		// Escape surrogate lẻ để fixture truyền đúng ký tự lỗi, tránh bộ mã hóa HTTP thay bằng dấu hỏi
		String payload = json.writeValueAsString(body).replace("\ud800", "\\ud800").replace("\udc00", "\\udc00");
		request.session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token())
				.header("Idempotency-Key", key).contentType(MediaType.APPLICATION_JSON).content(payload);
		if (etag != null)
			request.header("If-Match", etag);
		return mvc.perform(request);
	}

	private AdminOperationResponse read(ResultActions response) throws Exception {
		return json.readValue(response.andReturn().getResponse().getContentAsString(), AdminOperationResponse.class);
	}

	private String hash(String body) throws Exception {
		return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8)));
	}

	private static String key(String window) {
		return window + ":" + UUID.randomUUID();
	}

	private static String placeTag(Place place) {
		return "\"place:" + place.getId() + ":" + place.getVersion() + "\"";
	}

	private static String reviewAbsent(UUID text) {
		return "\"review:" + text + ":none\"";
	}

	private static String path(UUID place, String language) {
		return "/api/v1/admin/places/" + place + "/content/" + language;
	}

	private static String reviewPath(UUID text) {
		return "/api/v1/admin/text-revisions/" + text + "/review-reference";
	}
}
