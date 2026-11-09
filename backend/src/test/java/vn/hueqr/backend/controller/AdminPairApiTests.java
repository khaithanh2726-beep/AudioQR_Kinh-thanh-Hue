package vn.hueqr.backend.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.Timestamp;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminAudioReviewReferenceResponse;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminContentResponse;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.ApplyPairRequest;
import vn.hueqr.backend.dto.SaveAudioReviewReferenceRequest;
import vn.hueqr.backend.dto.SaveTextRevisionRequest;
import vn.hueqr.backend.dto.SetPlaceVisibilityRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.entity.OperationReceipt;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;
import vn.hueqr.backend.support.AudioDataFixture;
import vn.hueqr.backend.support.AudioDataFixture.Source;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;
import vn.hueqr.backend.support.WavFixture;

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
import static vn.hueqr.backend.support.AudioDataFixture.bytes;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminPairApiTests {
	private static final String PASSWORD = "Mat-khau-pair-fixture";
	private static final Path ROOT = Path.of("target/pair-files").toAbsolutePath();
	private static final String POLYGON = "{\"type\":\"Polygon\",\"coordinates\":[[[107.5,16.4],[107.6,16.4],[107.5,16.5],[107.5,16.4]]]}";

	private record Browser(MockHttpSession session, AdminCsrfResponse csrf) {
	}

	private record Fixture(Source source, UUID audio, String language) {
	}

	@DynamicPropertySource
	static void configureRoot(DynamicPropertyRegistry registry) {
		registry.add("HUEQR_AUDIO_ROOT", () -> ROOT.toString());
	}

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper json;

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private MutableClock clock;

	@Autowired
	private PasswordEncoder passwords;

	@Autowired
	private AdminAccountRepository accounts;

	@MockitoSpyBean
	private OperationReceiptRepository receipts;

	private UUID accountId;

	@BeforeEach
	void setUp() throws Exception {
		clearData();
		clock.set(AudioDataFixture.NOW);
		Files.createDirectories(ROOT);
		accountId = UUID.randomUUID();
		accounts.saveAndFlush(new AdminAccount(accountId, "pair-owner", passwords.encode(PASSWORD), true));
	}

	@AfterEach
	void clearData() {
		reset(receipts);
		for (String table : new String[]{"operation_receipt", "admin_command_window"})
			jdbc.update("DELETE FROM " + table);
		jdbc.update("UPDATE content_slot SET active_pair_id = NULL, draft_text_id = NULL");
		for (String table : new String[]{"content_pair", "audio_review_ref", "audio_revision", "text_review_ref"})
			jdbc.update("DELETE FROM " + table);
		jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL");
		for (String table : new String[]{"tts_job", "text_revision", "content_slot"})
			jdbc.update("DELETE FROM " + table);
		jdbc.update("UPDATE place SET active_region_revision_id = NULL");
		for (String table : new String[]{"region_revision", "place", "file_asset", "admin_account"})
			jdbc.update("DELETE FROM " + table);
	}

	@Test
	void readingAbsentAudioReviewDoesNotCreateOrApplyAnything() throws Exception {
		var browser = login();
		var fixture = seed("vi", false);
		var ref = review(browser, fixture.audio());
		assertThat(ref.etag()).isEqualTo(absent(fixture.audio()));
		assertThat(ref.textRevisionId()).isEqualTo(fixture.source().text());
		assertThat(ref.language()).isEqualTo("vi");
		assertThat(ref.locator()).isNull();
		assertThat(count("audio_review_ref")).isZero();
		assertThat(count("content_pair")).isZero();
		assertThat(detail(browser, fixture).appliedPair()).isNull();
	}

	@Test
	void savingAndUpdatingAudioReferencePreservesExactUnicodeAndDoesNotApproveOrApply() throws Exception {
		var browser = login();
		var fixture = seed("vi", false);
		String window = issue(browser);
		writeReview(browser, fixture, key(window), absent(fixture.audio()), " Hồ sơ nghe trọn – mục 2 ", "Giới hạn tên riêng")
				.andExpect(status().isOk()).andExpect(jsonPath("$.result.version").value(0));
		var ref = review(browser, fixture.audio());
		assertThat(ref.locator()).isEqualTo(" Hồ sơ nghe trọn – mục 2 ");
		assertThat(ref.recordedBy()).isEqualTo(accountId);
		writeReview(browser, fixture, key(window), ref.etag(), "Hồ sơ mới", null).andExpect(status().isOk());
		assertThat(review(browser, fixture.audio()).version()).isEqualTo(1);
		assertThat(count("content_pair")).isZero();
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?", Boolean.class, bytes(fixture.source().place()))).isFalse();
		assertThat(jdbc.queryForObject("SELECT SUM(ever_applied) FROM file_asset", Long.class)).isZero();
		assertThat(receipts.findAll()).allSatisfy(receipt -> assertThat(receipt.getResultSummary()).doesNotContain("Hồ sơ", "Giới hạn"));
	}

	@Test
	void staleOrWrongObjectReviewTagsAndInvalidPayloadsCannotOverwriteAReference() throws Exception {
		var browser = login();
		var f = seed("vi", false);
		String window = issue(browser);
		writeReview(browser, f, key(window), null, "Hồ sơ", null).andExpect(status().isBadRequest());
		writeReview(browser, f, key(window), "\"review:" + f.source().text() + ":none\"", "Hồ sơ", null).andExpect(status().isPreconditionFailed());
		writeReview(browser, f, key(window), absent(f.audio()), "  ", null).andExpect(status().isUnprocessableContent());
		writeReview(browser, f, key(window), absent(f.audio()), "Hồ sơ", "\ud800").andExpect(status().isUnprocessableContent());
		writeReview(browser, f, key(window), absent(f.audio()), "Hồ sơ", null).andExpect(status().isOk());
		writeReview(browser, f, key(window), absent(f.audio()), "Ghi đè", null).andExpect(status().isPreconditionFailed());
		assertThat(review(browser, f.audio()).locator()).isEqualTo("Hồ sơ");
	}

	@Test
	void anAudioWhoseFileIsMissingCannotReceiveAReviewReference() throws Exception {
		var browser = login();
		var f = seed("vi", false);
		Files.delete(ROOT.resolve(f.source().mp3() + ".mp3"));
		writeReview(browser, f, key(issue(browser)), absent(f.audio()), "Hồ sơ", null).andExpect(status().isConflict())
				.andExpect(jsonPath("$.missingConditions[0]").value("AUDIO_FILE_NOT_READY"));
		assertThat(count("audio_review_ref")).isZero();
	}

	@Test
	void firstApplyCommitsOneWholePairAndReceiptButDoesNotChangeDraftOrVisibility() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		var before = detail(browser, f);
		var result = read(apply(browser, f, key(issue(browser)), before.etag(), input(f)).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.previousPairId").isEmpty()).andExpect(header().string("Cache-Control", "no-store")));
		var after = detail(browser, f);
		assertThat(after.appliedPair().textRevisionId()).isEqualTo(f.source().text());
		assertThat(after.appliedPair().audioRevisionId()).isEqualTo(f.audio());
		assertThat(after.draftText().textRevisionId()).isEqualTo(before.draftText().textRevisionId());
		assertThat(after.appliedPair().missingConditions()).isEmpty();
		assertThat(after.version()).isEqualTo(before.version() + 1);
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?", Boolean.class, bytes(f.source().place()))).isFalse();
		assertThat(jdbc.queryForObject("SELECT SUM(ever_applied) FROM file_asset", Long.class)).isEqualTo(2);
		assertThat(result.result().get("previousPairId")).isNull();
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void replacingThePairKeepsOldRevisionsAndStartsProtectionAtTheLastUnapplyTime() throws Exception {
		var browser = login();
		var first = seed("vi", true);
		String window = issue(browser);
		apply(browser, first, key(window), detail(browser, first).etag(), input(first)).andExpect(status().isOk());
		var second = another(first, false);
		clock.set(clock.instant().plusSeconds(17).plusNanos(999));
		apply(browser, second, key(window), detail(browser, second).etag(), input(second)).andExpect(status().isOk());
		assertThat(count("content_pair")).isEqualTo(2);
		assertThat(detail(browser, first).appliedPair().audioRevisionId()).isEqualTo(second.audio());
		assertThat(jdbc.queryForObject("SELECT last_unapplied_at FROM file_asset WHERE id = ?", java.time.LocalDateTime.class, bytes(first.source().mp3())))
				.isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS).atOffset(java.time.ZoneOffset.UTC).toLocalDateTime());
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM audio_revision WHERE id = ?", Long.class, bytes(first.audio()))).isEqualTo(1);
	}

	@Test
	void changingOnlyAudioCanReuseTheReviewedOldTextEvenWhenANewerDraftExists() throws Exception {
		var browser = login();
		var first = seed("vi", true);
		String window = issue(browser);
		apply(browser, first, key(window), detail(browser, first).etag(), input(first)).andExpect(status().isOk());
		var nextDraft = another(first, false);
		var nextAudio = another(first, true);
		apply(browser, nextAudio, key(window), detail(browser, first).etag(), input(nextAudio)).andExpect(status().isOk());
		var state = detail(browser, first);
		assertThat(state.appliedPair().textRevisionId()).isEqualTo(first.source().text());
		assertThat(state.appliedPair().audioRevisionId()).isEqualTo(nextAudio.audio());
		assertThat(state.draftText().textRevisionId()).isEqualTo(nextDraft.source().text());
		assertThat(state.appliedPair().textReviewReference().version()).isZero();
	}

	@Test
	void mismatchedRevisionsLanguagesAndMissingReferencesLeaveTheOldPairIntact() throws Exception {
		var browser = login();
		var first = seed("vi", true);
		String window = issue(browser);
		apply(browser, first, key(window), detail(browser, first).etag(), input(first)).andExpect(status().isOk());
		UUID originalPair = detail(browser, first).appliedPair().pairId();
		var foreign = seed("en", true);
		var wrong = new ApplyPairRequest(first.source().text().toString(), foreign.audio().toString(), textHash(first), mp3Hash(foreign), 0L, 0L);
		apply(browser, first, key(window), detail(browser, first).etag(), wrong).andExpect(status().isConflict());
		apply(browser, first, key(window), detail(browser, first).etag(), input(foreign)).andExpect(status().isConflict());
		var next = another(first, false);
		jdbc.update("DELETE FROM text_review_ref WHERE text_revision_id = ?", bytes(next.source().text()));
		jdbc.update("DELETE FROM audio_review_ref WHERE audio_revision_id = ?", bytes(next.audio()));
		apply(browser, next, key(window), detail(browser, next).etag(), input(next)).andExpect(status().isConflict())
				.andExpect(jsonPath("$.missingConditions").value(org.hamcrest.Matchers.containsInAnyOrder("TEXT_REVIEW_REFERENCE_MISSING", "AUDIO_REVIEW_REFERENCE_MISSING")));
		assertThat(detail(browser, first).appliedPair().pairId()).isEqualTo(originalPair);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void changedReviewVersionsAreRejectedEvenWhenTheSlotTagIsStillCurrent() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		String tag = detail(browser, f).etag();
		jdbc.update("UPDATE text_review_ref SET version = 1 WHERE text_revision_id = ?", bytes(f.source().text()));
		apply(browser, f, key(window), tag, input(f)).andExpect(status().isPreconditionFailed());
		jdbc.update("UPDATE text_review_ref SET version = 0 WHERE text_revision_id = ?", bytes(f.source().text()));
		jdbc.update("UPDATE audio_review_ref SET version = 1 WHERE audio_revision_id = ?", bytes(f.audio()));
		apply(browser, f, key(window), tag, input(f)).andExpect(status().isPreconditionFailed());
		assertThat(count("content_pair")).isZero();
	}

	@Test
	void badHashesVersionsAndFileSizeDoNotCreateHalfPairs() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		String tag = detail(browser, f).etag();
		var wrongHash = new ApplyPairRequest(f.source().text().toString(), f.audio().toString(), "0".repeat(64), mp3Hash(f), 0L, 0L);
		apply(browser, f, key(window), tag, wrongHash).andExpect(status().isConflict()).andExpect(jsonPath("$.missingConditions[0]").value("TEXT_HASH_MISMATCH"));
		apply(browser, f, key(window), tag, new ApplyPairRequest(f.source().text().toString(), f.audio().toString(), "invalid", mp3Hash(f), 0L, 0L)).andExpect(status().isUnprocessableContent());
		apply(browser, f, key(window), tag, new ApplyPairRequest(f.source().text().toString(), f.audio().toString(), textHash(f), mp3Hash(f), -1L, 0L)).andExpect(status().isUnprocessableContent());
		Files.writeString(ROOT.resolve(f.source().mp3() + ".mp3"), "changed length");
		apply(browser, f, key(window), tag, input(f)).andExpect(status().isConflict()).andExpect(jsonPath("$.missingConditions[0]").value("AUDIO_FILE_NOT_READY"));
		assertThat(count("content_pair")).isZero();
	}

	@Test
	void replayReturnsTheCommittedEventEvenAfterAnotherPairHasBeenApplied() throws Exception {
		var browser = login();
		var first = seed("vi", true);
		String window = issue(browser);
		String operation = key(window);
		String originalTag = detail(browser, first).etag();
		var committed = read(apply(browser, first, operation, originalTag, input(first)).andExpect(status().isOk()));
		var next = another(first, false);
		apply(browser, next, key(window), detail(browser, next).etag(), input(next)).andExpect(status().isOk());
		var replay = read(apply(browser, first, operation, originalTag, input(first)).andExpect(status().isOk()));
		assertThat(replay.replayed()).isTrue();
		assertThat(replay.operationId()).isEqualTo(committed.operationId());
		assertThat(replay.result()).isEqualTo(committed.result());
		assertThat(detail(browser, first).appliedPair().audioRevisionId()).isEqualTo(next.audio());
		apply(browser, first, operation, detail(browser, first).etag(), input(first)).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("KEY_REUSED"));
	}

	@Test
	void aNewKeyForTheCurrentPairDoesNotCreateAPairOrResetTheSlotVersion() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		apply(browser, f, key(window), detail(browser, f).etag(), input(f)).andExpect(status().isOk());
		var before = detail(browser, f);
		apply(browser, f, key(window), before.etag(), input(f)).andExpect(status().isOk()).andExpect(jsonPath("$.result.changed").value(false));
		assertThat(detail(browser, f).version()).isEqualTo(before.version());
		assertThat(count("content_pair")).isEqualTo(1);
	}

	@Test
	void receiptFailureRollsBackPairPointerAndLifecycleFlagsAndSameKeyCanRetry() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String operation = key(issue(browser));
		String tag = detail(browser, f).etag();
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		apply(browser, f, operation, tag, input(f)).andExpect(status().isInternalServerError());
		assertThat(detail(browser, f).appliedPair()).isNull();
		assertThat(count("content_pair")).isZero();
		assertThat(jdbc.queryForObject("SELECT SUM(ever_applied) FROM file_asset", Long.class)).isZero();
		reset(receipts);
		apply(browser, f, operation, tag, input(f)).andExpect(status().isOk());
		var next = another(f, false);
		UUID original = detail(browser, f).appliedPair().pairId();
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		apply(browser, next, key(operation.substring(0, 36)), detail(browser, next).etag(), input(next)).andExpect(status().isInternalServerError());
		assertThat(detail(browser, f).appliedPair().pairId()).isEqualTo(original);
		assertThat(count("content_pair")).isEqualTo(1);
		assertThat(jdbc.queryForObject("SELECT last_unapplied_at FROM file_asset WHERE id = ?", Timestamp.class, bytes(f.source().mp3()))).isNull();
		assertThat(jdbc.queryForObject("SELECT ever_applied FROM file_asset WHERE id = ?", Boolean.class, bytes(next.source().mp3()))).isFalse();
	}

	@Test
	void receiptFailureRollsBackAudioReferenceCreationAndLaterUpdate() throws Exception {
		var browser = login();
		var f = seed("vi", false);
		String window = issue(browser);
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		writeReview(browser, f, key(window), absent(f.audio()), "Hồ sơ", null).andExpect(status().isInternalServerError());
		assertThat(count("audio_review_ref")).isZero();
		reset(receipts);
		writeReview(browser, f, key(window), absent(f.audio()), "Hồ sơ", null).andExpect(status().isOk());
		var ref = review(browser, f.audio());
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		writeReview(browser, f, key(window), ref.etag(), "Thay đổi không commit", null).andExpect(status().isInternalServerError());
		assertThat(review(browser, f.audio())).isEqualTo(ref);
	}

	@Test
	void concurrentSameKeyAppliesReturnOneCommitAndOneReplayWhileDifferentKeysRejectTheStaleSlot() throws Exception {
		var first = login();
		var second = login();
		var f = seed("vi", true);
		String operation = key(issue(first));
		String tag = detail(first, f).etag();
		assertThat(concurrently(() -> apply(first, f, operation, tag, input(f)), () -> apply(second, f, operation, tag, input(f))))
				.containsExactly(200, 200);
		assertThat(count("content_pair")).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
		var next = another(f, false);
		String updatedTag = detail(first, f).etag();
		String a = key(issue(first));
		String b = key(issue(second));
		assertThat(concurrently(() -> apply(first, next, a, updatedTag, input(next)), () -> apply(second, next, b, updatedTag, input(next))))
				.containsExactlyInAnyOrder(200, 412);
		assertThat(count("content_pair")).isEqualTo(2);
	}

	@Test
	void concurrentAudioReviewCreationCannotOverwriteAnotherBrowsersReference() throws Exception {
		var first = login();
		var second = login();
		var f = seed("vi", false);
		String a = key(issue(first));
		String b = key(issue(second));
		assertThat(concurrently(() -> writeReview(first, f, a, absent(f.audio()), "A", null),
				() -> writeReview(second, f, b, absent(f.audio()), "B", null))).containsExactlyInAnyOrder(200, 412);
		assertThat(count("audio_review_ref")).isEqualTo(1);
	}

	@Test
	void visibilityNeedsAppliedRegionAndVietnamesePairButNotTheOtherThreeLanguages() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		visibility(browser, f, key(window), placeTag(f), true).andExpect(status().isConflict())
				.andExpect(jsonPath("$.missingConditions").value(org.hamcrest.Matchers.containsInAnyOrder("REGION_NOT_APPLIED", "VI_PAIR_NOT_APPLIED")));
		apply(browser, f, key(window), detail(browser, f).etag(), input(f)).andExpect(status().isOk());
		visibility(browser, f, key(window), placeTag(f), true).andExpect(status().isConflict());
		applyRegion(f);
		mvc.perform(get(placePath(f)).session(browser.session())).andExpect(status().isOk()).andExpect(jsonPath("$.canShow").value(true));
		visibility(browser, f, key(window), placeTag(f), true).andExpect(status().isOk()).andExpect(jsonPath("$.result.visible").value(true));
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM content_slot WHERE place_id = ?", Long.class, bytes(f.source().place()))).isEqualTo(1);
		UUID pair = detail(browser, f).appliedPair().pairId();
		visibility(browser, f, key(window), placeTag(f), false).andExpect(status().isOk());
		assertThat(detail(browser, f).appliedPair().pairId()).isEqualTo(pair);
		assertThat(jdbc.queryForObject("SELECT last_unapplied_at FROM file_asset WHERE id = ?", Timestamp.class, bytes(f.source().mp3()))).isNull();
	}

	@Test
	void draftOrUnappliedPairsAndOtherLanguagesCannotSatisfyTheVietnameseGate() throws Exception {
		var browser = login();
		var vi = seed("vi", true);
		applyRegion(vi);
		new AudioDataFixture(jdbc).insertPair(vi.source().slot(), vi.source().text(), vi.audio());
		visibility(browser, vi, key(issue(browser)), placeTag(vi), true).andExpect(status().isConflict())
				.andExpect(jsonPath("$.missingConditions[0]").value("VI_PAIR_NOT_APPLIED"));
		var en = seed("en", true);
		applyRegion(en);
		String window = issue(browser);
		apply(browser, en, key(window), detail(browser, en).etag(), input(en)).andExpect(status().isOk());
		visibility(browser, en, key(window), placeTag(en), true).andExpect(status().isConflict())
				.andExpect(jsonPath("$.missingConditions[0]").value("VI_PAIR_NOT_APPLIED"));
	}

	@Test
	void visibilityReportsCorruptRegionMissingReviewAndMissingFileWithoutChangingState() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		apply(browser, f, key(window), detail(browser, f).etag(), input(f)).andExpect(status().isOk());
		applyRegion(f);
		jdbc.update("UPDATE region_revision SET geometry_hash = ?", new byte[32]);
		jdbc.update("DELETE FROM audio_review_ref WHERE audio_revision_id = ?", bytes(f.audio()));
		Files.delete(ROOT.resolve(f.source().mp3() + ".mp3"));
		visibility(browser, f, key(window), placeTag(f), true).andExpect(status().isConflict())
				.andExpect(jsonPath("$.missingConditions").value(org.hamcrest.Matchers.containsInAnyOrder("REGION_INVALID", "VI_AUDIO_REVIEW_REFERENCE_MISSING", "VI_AUDIO_FILE_NOT_READY")));
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?", Boolean.class, bytes(f.source().place()))).isFalse();
	}

	@Test
	void hidingAlwaysWorksWithoutPrerequisitesAndInvalidVisibilityInputsAreRejected() throws Exception {
		var browser = login();
		var f = seed("vi", false);
		String window = issue(browser);
		jdbc.update("UPDATE place SET visible = TRUE WHERE id = ?", bytes(f.source().place()));
		visibility(browser, f, key(window), placeTag(f), false).andExpect(status().isOk());
		visibility(browser, f, key(window), placeTag(f), null).andExpect(status().isUnprocessableContent());
		visibility(browser, f, key(window), null, false).andExpect(status().isBadRequest());
		visibility(browser, f, key(window), "W/\"0\"", false).andExpect(status().isBadRequest());
		visibility(browser, f, key(window), "\"0\"", false).andExpect(status().isPreconditionFailed());
	}

	@Test
	void visibilityReceiptFailureRollsBackAndReplayKeepsTheOriginalVisibilityEvent() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		apply(browser, f, key(window), detail(browser, f).etag(), input(f)).andExpect(status().isOk());
		applyRegion(f);
		String tag = placeTag(f);
		String operation = key(window);
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		visibility(browser, f, operation, tag, true).andExpect(status().isInternalServerError());
		assertThat(placeTag(f)).isEqualTo(tag);
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?", Boolean.class, bytes(f.source().place()))).isFalse();
		reset(receipts);
		visibility(browser, f, operation, tag, true).andExpect(status().isOk());
		visibility(browser, f, key(window), placeTag(f), false).andExpect(status().isOk());
		visibility(browser, f, operation, tag, true).andExpect(status().isOk()).andExpect(jsonPath("$.replayed").value(true))
				.andExpect(jsonPath("$.result.visible").value(true));
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?", Boolean.class, bytes(f.source().place()))).isFalse();
	}

	@Test
	void lastUnapplyForSharedFilesIsNotRecordedUntilBothPlacesHaveReplacedTheirPairs() throws Exception {
		var browser = login();
		var first = seed("vi", true);
		var originalSecond = seed("vi", true);
		jdbc.update("DELETE FROM audio_review_ref WHERE audio_revision_id = ?", bytes(originalSecond.audio()));
		jdbc.update("DELETE FROM audio_revision WHERE id = ?", bytes(originalSecond.audio()));
		jdbc.update("UPDATE tts_job SET source_wav_file_id = ? WHERE id = ?", bytes(first.source().wav()), bytes(originalSecond.source().job()));
		var s = originalSecond.source();
		var sharedSource = new Source(s.place(), s.slot(), s.text(), s.job(), first.source().wav(), first.source().mp3());
		var second = new Fixture(sharedSource, new AudioDataFixture(jdbc).insertAudio(sharedSource), "vi");
		references(second, false);
		String window = issue(browser);
		apply(browser, first, key(window), detail(browser, first).etag(), input(first)).andExpect(status().isOk());
		apply(browser, second, key(window), detail(browser, second).etag(), input(second)).andExpect(status().isOk());
		var nextFirst = another(first, false);
		apply(browser, nextFirst, key(window), detail(browser, first).etag(), input(nextFirst)).andExpect(status().isOk());
		assertThat(jdbc.queryForObject("SELECT last_unapplied_at FROM file_asset WHERE id = ?", Timestamp.class, bytes(first.source().mp3()))).isNull();
		clock.set(clock.instant().plusSeconds(45));
		var nextSecond = another(second, false);
		apply(browser, nextSecond, key(window), detail(browser, second).etag(), input(nextSecond)).andExpect(status().isOk());
		assertThat(jdbc.queryForObject("SELECT last_unapplied_at FROM file_asset WHERE id = ?", java.time.LocalDateTime.class, bytes(first.source().mp3())))
				.isEqualTo(clock.instant().atOffset(java.time.ZoneOffset.UTC).toLocalDateTime());
	}

	@Test
	void adminSessionCsrfWindowOwnerAndExpiryAreRequiredBeforeMutationOrReplay() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		String operation = key(window);
		String tag = detail(browser, f).etag();
		mvc.perform(get(reviewPath(f.audio()))).andExpect(status().isUnauthorized());
		mvc.perform(put(path(f) + "/active-pair").session(browser.session()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(input(f))))
				.andExpect(status().isForbidden());
		apply(browser, f, operation, tag, input(f)).andExpect(status().isOk());
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "other-pair-owner", passwords.encode(PASSWORD), true));
		var other = login("other-pair-owner");
		apply(other, f, operation, tag, input(f)).andExpect(status().isForbidden());
		clock.set(clock.instant().plus(Duration.ofHours(24)));
		apply(browser, f, operation, tag, input(f)).andExpect(status().isUnauthorized());
		var again = login();
		apply(again, f, operation, tag, input(f)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("COMMAND_WINDOW_EXPIRED"));
	}

	@Test
	void readOnlyPairAndGateRequestsDoNotExtendQ3ButExplicitApplyCountsAsActivity() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		clock.set(clock.instant().plus(Duration.ofMinutes(20)));
		detail(browser, f);
		review(browser, f.audio());
		mvc.perform(get(placePath(f)).session(browser.session())).andExpect(status().isOk());
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(jsonPath("$.lastInteractiveAt").value(AudioDataFixture.NOW.toString()));
		apply(browser, f, key(window), detail(browser, f).etag(), input(f)).andExpect(status().isOk());
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(jsonPath("$.lastInteractiveAt").value(clock.instant().toString()));
	}

	@Test
	void completenessReturnsFourRowsPerPlaceWithoutCreatingEmptySlotsOrClaimingApproval() throws Exception {
		var browser = login();
		var f = seed("vi", false);
		var result = mvc.perform(get("/api/v1/admin/content-completeness").session(browser.session())).andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store"))
				.andExpect(jsonPath("$.length()").value(4)).andExpect(jsonPath("$[0].language").value("vi"))
				.andExpect(jsonPath("$[1].language").value("en"))
				.andExpect(jsonPath("$[0].draftAudios[0].missingConditions").value(org.hamcrest.Matchers.containsInAnyOrder("TEXT_REVIEW_REFERENCE_MISSING", "AUDIO_REVIEW_REFERENCE_MISSING")))
				.andExpect(jsonPath("$[1].draftMissingConditions[0]").value("DRAFT_TEXT_MISSING")).andReturn();
		assertThat(result.getResponse().getContentAsString()).doesNotContain("storageKey", "password", "wavFileId", "providerKey", "approved");
		assertThat(count("content_slot")).isEqualTo(1);
		assertThat(count("content_pair")).isZero();
		assertThat(count("audio_review_ref")).isZero();
		assertThat(f.audio()).isNotNull();
	}

	@Test
	void completenessDistinguishesCurrentPairFromTheNewDraftAndReportsMissingAudio() throws Exception {
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		apply(browser, f, key(window), detail(browser, f).etag(), input(f)).andExpect(status().isOk());
		var state = detail(browser, f);
		perform(browser, post(path(f) + "/text-revisions"), key(window), state.etag(), new SaveTextRevisionRequest("Nháp mới", f.source().text().toString()))
				.andExpect(status().isCreated());
		mvc.perform(get("/api/v1/admin/content-completeness").session(browser.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].content.appliedPair.textRevisionId").value(f.source().text().toString()))
				.andExpect(jsonPath("$[0].content.draftText.body").value("Nháp mới"))
				.andExpect(jsonPath("$[0].draftAudios.length()").value(0))
				.andExpect(jsonPath("$[0].draftMissingConditions").value(org.hamcrest.Matchers.containsInAnyOrder("TEXT_REVIEW_REFERENCE_MISSING", "AUDIO_NOT_CREATED")));
	}

	@Test
	void completenessRequiresAdminAndDoesNotRenewQ3OrPretendAppliedMeansFilesStillAvailable() throws Exception {
		mvc.perform(get("/api/v1/admin/content-completeness")).andExpect(status().isUnauthorized());
		var browser = login();
		var f = seed("vi", true);
		String window = issue(browser);
		apply(browser, f, key(window), detail(browser, f).etag(), input(f)).andExpect(status().isOk());
		Files.delete(ROOT.resolve(f.source().mp3() + ".mp3"));
		clock.set(clock.instant().plus(Duration.ofMinutes(29)));
		mvc.perform(get("/api/v1/admin/content-completeness").session(browser.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].content.appliedPair.missingConditions[0]").value("AUDIO_FILE_NOT_READY"))
				.andExpect(jsonPath("$[0].draftAudios[0].missingConditions[0]").value("AUDIO_FILE_NOT_READY"));
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(jsonPath("$.lastInteractiveAt").value(AudioDataFixture.NOW.toString()));
	}

	@Test
	void concurrentShowRequestsWithOnePlaceVersionCannotBothCommit() throws Exception {
		var first = login();
		var second = login();
		var f = seed("vi", true);
		String window = issue(first);
		apply(first, f, key(window), detail(first, f).etag(), input(f)).andExpect(status().isOk());
		applyRegion(f);
		// Cả hai thao tác đều đổi trạng thái ẩn sang hiện, không dùng thao tác giữ nguyên để giả tranh chấp
		String tag = placeTag(f);
		String a = key(issue(first));
		String b = key(issue(second));
		assertThat(concurrently(() -> visibility(first, f, a, tag, true), () -> visibility(second, f, b, tag, true)))
				.containsExactlyInAnyOrder(200, 412);
	}

	@Test
	void applyAndHideTogetherAlwaysLeaveThePlaceHiddenWithoutLosingTheNewPair() throws Exception {
		var first = login();
		var second = login();
		var f = seed("vi", true);
		String window = issue(first);
		apply(first, f, key(window), detail(first, f).etag(), input(f)).andExpect(status().isOk());
		applyRegion(f);
		visibility(first, f, key(window), placeTag(f), true).andExpect(status().isOk());
		var next = another(f, false);
		String slotTag = detail(first, next).etag();
		String tag = placeTag(f);
		String a = key(issue(first));
		String b = key(issue(second));
		assertThat(concurrently(() -> apply(first, next, a, slotTag, input(next)), () -> visibility(second, f, b, tag, false)))
				.containsExactly(200, 200);
		assertThat(detail(first, next).appliedPair().audioRevisionId()).isEqualTo(next.audio());
		assertThat(jdbc.queryForObject("SELECT visible FROM place WHERE id = ?", Boolean.class, bytes(f.source().place()))).isFalse();
	}

	@Test
	void audioReviewReplayUsesTheOriginalTagAndDoesNotOverwriteALaterEdit() throws Exception {
		var browser = login();
		var f = seed("vi", false);
		String window = issue(browser);
		String operation = key(window);
		var first = read(writeReview(browser, f, operation, absent(f.audio()), "Hồ sơ đầu", null).andExpect(status().isOk()));
		writeReview(browser, f, key(window), review(browser, f.audio()).etag(), "Hồ sơ mới", null).andExpect(status().isOk());
		var replay = read(writeReview(browser, f, operation, absent(f.audio()), "Hồ sơ đầu", null).andExpect(status().isOk()));
		assertThat(replay.replayed()).isTrue();
		assertThat(replay.operationId()).isEqualTo(first.operationId());
		assertThat(review(browser, f.audio()).locator()).isEqualTo("Hồ sơ mới");
	}

	private void applyRegion(Fixture f) throws Exception {
		UUID rid = UUID.randomUUID();
		byte[] hash = digest(("WGS84_LON_LAT\n" + POLYGON).getBytes(StandardCharsets.UTF_8));
		jdbc.update("INSERT INTO region_revision (id, place_id, polygon, map_space_key, geometry_hash, saved_at) VALUES (?, ?, ?, 'WGS84_LON_LAT', ?, ?)",
				bytes(rid), bytes(f.source().place()), POLYGON, hash, AudioDataFixture.time());
		jdbc.update("UPDATE place SET active_region_revision_id = ?, version = version + 1 WHERE id = ?", bytes(rid), bytes(f.source().place()));
	}

	private String placeTag(Fixture f) {
		return "\"" + jdbc.queryForObject("SELECT version FROM place WHERE id = ?", Long.class, bytes(f.source().place())) + "\"";
	}

	private static String placePath(Fixture f) {
		return "/api/v1/admin/places/" + f.source().place();
	}

	private List<Integer> concurrently(Callable<ResultActions> a, Callable<ResultActions> b) throws Exception {
		// Hai phiên cùng vượt rào để kiểm tranh chấp TX thật trên MySQL
		var barrier = new CyclicBarrier(2);
		try (var executor = Executors.newFixedThreadPool(2)) {
			var first = executor.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return a.call().andReturn().getResponse().getStatus();
			});
			var second = executor.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return b.call().andReturn().getResponse().getStatus();
			});
			return List.of(first.get(30, TimeUnit.SECONDS), second.get(30, TimeUnit.SECONDS));
		}
	}

	private Fixture seed(String language, boolean reviewed) throws Exception {
		var data = new AudioDataFixture(jdbc);
		Source source = data.insertSource(language);
		prepareFiles(source);
		UUID audio = data.insertAudio(source);
		var fixture = new Fixture(source, audio, language);
		if (reviewed)
			references(fixture, true);
		return fixture;
	}

	private Fixture another(Fixture previous, boolean sameText) throws Exception {
		var data = new AudioDataFixture(jdbc);
		Source fresh = data.insertSource(previous.language());
		prepareFiles(fresh);
		jdbc.update("UPDATE content_slot SET draft_text_id = NULL WHERE id = ?", bytes(fresh.slot()));
		Source source;
		if (sameText) {
			jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL WHERE id = ?", bytes(fresh.text()));
			jdbc.update("UPDATE tts_job SET target_text_revision_id = ?, text_revision_key_snapshot = ?, text_hash_snapshot = ?, place_key_snapshot = ? WHERE id = ?",
					bytes(previous.source().text()), bytes(previous.source().text()), HexFormat.of().parseHex(textHash(previous)), bytes(previous.source().place()), bytes(fresh.job()));
			jdbc.update("DELETE FROM text_revision WHERE id = ?", bytes(fresh.text()));
			source = new Source(previous.source().place(), previous.source().slot(), previous.source().text(), fresh.job(), fresh.wav(), fresh.mp3());
		}
		else {
			jdbc.update("UPDATE text_revision SET slot_id = ? WHERE id = ?", bytes(previous.source().slot()), bytes(fresh.text()));
			jdbc.update("UPDATE tts_job SET place_key_snapshot = ? WHERE id = ?", bytes(previous.source().place()), bytes(fresh.job()));
			jdbc.update("UPDATE content_slot SET draft_text_id = ?, version = version + 1 WHERE id = ?", bytes(fresh.text()), bytes(previous.source().slot()));
			source = new Source(previous.source().place(), previous.source().slot(), fresh.text(), fresh.job(), fresh.wav(), fresh.mp3());
		}
		jdbc.update("DELETE FROM content_slot WHERE id = ?", bytes(fresh.slot()));
		jdbc.update("DELETE FROM place WHERE id = ?", bytes(fresh.place()));
		var fixture = new Fixture(source, data.insertAudio(source), previous.language());
		references(fixture, !sameText);
		return fixture;
	}

	private void prepareFiles(Source source) throws Exception {
		byte[] wav = WavFixture.oneSecond();
		// MP3 fixture chỉ kiểm quyền/quan hệ/file stat, không phải bằng chứng chuyển codec hoặc nghe duyệt
		byte[] mp3 = ("MP3 metadata fixture " + source.mp3()).getBytes(StandardCharsets.UTF_8);
		for (var item : java.util.Map.of(source.wav(), wav, source.mp3(), mp3).entrySet()) {
			String extension = item.getKey().equals(source.wav()) ? ".wav" : ".mp3";
			Files.write(ROOT.resolve(item.getKey() + extension), item.getValue());
			jdbc.update("UPDATE file_asset SET checksum = ?, byte_size = ? WHERE id = ?", digest(item.getValue()), item.getValue().length, bytes(item.getKey()));
		}
		byte[] textHash = digest("Bản đã lưu".getBytes(StandardCharsets.UTF_8));
		jdbc.update("UPDATE text_revision SET content_hash = ? WHERE id = ?", textHash, bytes(source.text()));
		jdbc.update("UPDATE tts_job SET text_hash_snapshot = ? WHERE id = ?", textHash, bytes(source.job()));
	}

	private void references(Fixture f, boolean text) {
		if (text)
			jdbc.update("INSERT INTO text_review_ref (text_revision_id, locator, recorded_by, recorded_at) VALUES (?, 'Hồ sơ văn bản fixture', ?, ?)", bytes(f.source().text()), bytes(accountId), AudioDataFixture.time());
		jdbc.update("INSERT INTO audio_review_ref (audio_revision_id, locator, recorded_by, recorded_at) VALUES (?, 'Hồ sơ audio fixture', ?, ?)", bytes(f.audio()), bytes(accountId), AudioDataFixture.time());
	}

	private Browser login() throws Exception {
		return login("pair-owner");
	}

	private Browser login(String name) throws Exception {
		var initial = mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk()).andReturn();
		var session = (MockHttpSession) initial.getRequest().getSession(false);
		var csrf = json.readValue(initial.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(post("/api/v1/auth/login").session(session).header(csrf.headerName(), csrf.token()).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new AdminLoginRequest(name, PASSWORD)))).andExpect(status().isOk());
		var current = mvc.perform(get("/api/v1/auth/csrf").session(session)).andReturn();
		return new Browser(session, json.readValue(current.getResponse().getContentAsString(), AdminCsrfResponse.class));
	}

	private String issue(Browser browser) throws Exception {
		var result = mvc.perform(post("/api/v1/admin/command-windows").session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token()))
				.andExpect(status().isCreated()).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminCommandWindowResponse.class).windowId().toString();
	}

	private AdminContentResponse detail(Browser browser, Fixture f) throws Exception {
		var result = mvc.perform(get(path(f)).session(browser.session())).andExpect(status().isOk()).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminContentResponse.class);
	}

	private AdminAudioReviewReferenceResponse review(Browser browser, UUID audio) throws Exception {
		var result = mvc.perform(get(reviewPath(audio)).session(browser.session())).andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store")).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminAudioReviewReferenceResponse.class);
	}

	private ApplyPairRequest input(Fixture f) {
		return new ApplyPairRequest(f.source().text().toString(), f.audio().toString(), textHash(f), mp3Hash(f), 0L, 0L);
	}

	private String textHash(Fixture f) {
		return HexFormat.of().formatHex(jdbc.queryForObject("SELECT content_hash FROM text_revision WHERE id = ?", byte[].class, bytes(f.source().text())));
	}

	private String mp3Hash(Fixture f) {
		return HexFormat.of().formatHex(jdbc.queryForObject("SELECT checksum FROM file_asset WHERE id = ?", byte[].class, bytes(f.source().mp3())));
	}

	private ResultActions apply(Browser browser, Fixture f, String key, String tag, ApplyPairRequest input) throws Exception {
		return perform(browser, put(path(f) + "/active-pair"), key, tag, input);
	}

	private ResultActions writeReview(Browser browser, Fixture f, String key, String tag, String locator, String limitations) throws Exception {
		return perform(browser, put(reviewPath(f.audio())), key, tag, new SaveAudioReviewReferenceRequest(locator, limitations));
	}

	private ResultActions visibility(Browser browser, Fixture f, String key, String tag, Boolean visible) throws Exception {
		return perform(browser, put("/api/v1/admin/places/" + f.source().place() + "/visibility"), key, tag, new SetPlaceVisibilityRequest(visible));
	}

	private ResultActions perform(Browser browser, MockHttpServletRequestBuilder request, String key, String tag, Object input) throws Exception {
		String payload = json.writeValueAsString(input).replace("\ud800", "\\ud800");
		request.session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token()).header("Idempotency-Key", key)
				.contentType(MediaType.APPLICATION_JSON).content(payload);
		if (tag != null)
			request.header("If-Match", tag);
		return mvc.perform(request);
	}

	private AdminOperationResponse read(ResultActions response) throws Exception {
		return json.readValue(response.andReturn().getResponse().getContentAsString(), AdminOperationResponse.class);
	}

	private long count(String table) {
		return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
	}

	private static String path(Fixture f) {
		return "/api/v1/admin/places/" + f.source().place() + "/content/" + f.language();
	}

	private static String reviewPath(UUID aid) {
		return "/api/v1/admin/audio-revisions/" + aid + "/review-reference";
	}

	private static String absent(UUID aid) {
		return "\"audio-review:" + aid + ":none\"";
	}

	private static String key(String window) {
		return window + ":" + UUID.randomUUID();
	}

	private static byte[] digest(byte[] bytes) throws Exception {
		return MessageDigest.getInstance("SHA-256").digest(bytes);
	}
}
