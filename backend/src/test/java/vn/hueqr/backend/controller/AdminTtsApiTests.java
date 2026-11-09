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
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminContentResponse;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.CreateTtsRequest;
import vn.hueqr.backend.dto.SaveTextRevisionRequest;
import vn.hueqr.backend.dto.TtsJobResponse;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.entity.ContentSlot;
import vn.hueqr.backend.entity.OperationReceipt;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.TextRevision;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.AdminCommandWindowRepository;
import vn.hueqr.backend.repository.ContentSlotRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.RegionRevisionRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TextReviewReferenceRepository;
import vn.hueqr.backend.repository.TtsJobRepository;
import vn.hueqr.backend.service.TtsDispatchService;
import vn.hueqr.backend.service.TtsJobService;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminTtsApiTests {
	private static final String PASSWORD = "Mat-khau-tts-fixture";

	private record Browser(MockHttpSession session, AdminCsrfResponse csrf) {
	}

	private record Source(UUID placeId, UUID slotId, UUID textId, String body, String hash) {
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
	private TtsJobRepository jobs;

	@Autowired
	private TtsDispatchService dispatch;

	@Autowired
	private JdbcTemplate jdbc;

	@BeforeEach
	void setUp() {
		clearData();
		clock.set(GuestSessionTestClock.BASE);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "tts-owner", passwords.encode(PASSWORD), true));
	}

	@AfterEach
	void clearData() {
		reset(receipts);
		receipts.deleteAll();
		windows.deleteAll();
		references.deleteAll();
		jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL, latest_tts_job_key = NULL");
		jobs.deleteAll();
		jdbc.update("UPDATE content_slot SET draft_text_id = NULL");
		texts.deleteAll();
		slots.deleteAll();
		jdbc.update("UPDATE place SET active_region_revision_id = NULL");
		regions.deleteAll();
		places.deleteAll();
		accounts.deleteAll();
	}

	@Test
	void acceptanceCommitsAQueuedJobWithExactSavedTextLanguageModelVoiceAndMicroseconds() throws Exception {
		Browser browser = login();
		Source source = source("fr");
		clock.set(clock.instant().plusNanos(123456789));
		var result = read(create(browser, source.textId(), key(issue(browser)), source.hash())
				.andExpect(status().isAccepted()).andExpect(header().string("Cache-Control", "no-store")));
		TtsJobResponse job = poll(browser, jobId(result));
		assertThat(job.textRevisionId()).isEqualTo(source.textId());
		assertThat(job.textHash()).isEqualTo(source.hash());
		assertThat(job.language()).isEqualTo("fr");
		assertThat(job.model()).isEqualTo("gemini-3.8-flash-tts");
		assertThat(job.voice()).isEqualTo("Charon");
		assertThat(job.phase()).isEqualTo("QUEUED");
		assertThat(job.state()).isEqualTo("PROCESSING");
		assertThat(job.targetValid()).isTrue();
		assertThat(job.outcomeUncertain()).isFalse();
		assertThat(job.acceptedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertThat(job.phaseVersion()).isZero();
		var text = texts.findById(source.textId()).orElseThrow();
		assertThat(text.getActiveTtsJobId()).isEqualTo(job.jobId());
		assertThat(text.getLatestTtsJobKey()).isEqualTo(job.jobId());
		assertThat(text.getBody()).isEqualTo(source.body());
		assertThat(text.getContentHash()).isEqualTo(HexFormat.of().parseHex(source.hash()));
		assertThat(jobs.findById(job.jobId()).orElseThrow().getWorkerToken()).isNull();
		assertThat(places.findById(source.placeId()).orElseThrow().isVisible()).isFalse();
		assertThat(places.findById(source.placeId()).orElseThrow().getVersion()).isZero();
		assertThat(slots.findById(source.slotId()).orElseThrow().getVersion()).isEqualTo(1L);
	}

	@Test
	void acceptanceDoesNotRequireAReviewReferenceAndDoesNotCreateApprovalOrAudioSuccess() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		var job = accepted(browser, source, key(issue(browser)));
		assertThat(job.state()).isEqualTo("PROCESSING");
		assertThat(references.count()).isZero();
		assertThat(receipts.findAll()).allSatisfy(receipt -> assertThat(receipt.getResultSummary())
				.doesNotContain(source.body(), "body", "prompt", "password", "workerToken"));
	}

	@Test
	void newKeysReuseTheQueuedJobWithoutChangingTheFirstAcceptanceOrTextVersion() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		var first = accepted(browser, source, key(window));
		clock.set(clock.instant().plusSeconds(10));
		var reused = read(create(browser, source.textId(), key(window), source.hash()).andExpect(status().isAccepted()));
		assertThat(reused.result().get("reused")).isEqualTo(true);
		assertThat(reused.replayed()).isFalse();
		assertThat(jobId(reused)).isEqualTo(first.jobId());
		assertThat(poll(browser, first.jobId())).isEqualTo(first);
		assertThat(jobs.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(2);
	}

	@Test
	void replayKeepsTheOriginalReceiptWhilePollShowsTheNewDispatchPhase() throws Exception {
		Browser browser = login();
		Source source = source("en");
		String key = key(issue(browser));
		var first = read(create(browser, source.textId(), key, source.hash()).andExpect(status().isAccepted()));
		dispatch.claim(jobId(first), UUID.randomUUID()).orElseThrow();
		var replay = read(create(browser, source.textId(), key, source.hash()).andExpect(status().isAccepted()));
		assertThat(replay.replayed()).isTrue();
		assertThat(replay.operationId()).isEqualTo(first.operationId());
		assertThat(replay.result()).isEqualTo(first.result());
		assertThat(replay.result().get("phase")).isEqualTo("QUEUED");
		assertThat(poll(browser, jobId(first)).phase()).isEqualTo("DISPATCHING");
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void keyReuseRejectsChangedHashOrTargetAndNeverWritesASecondJob() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		Source other = source("ru");
		String key = key(issue(browser));
		accepted(browser, source, key);
		create(browser, source.textId(), key, "0".repeat(64)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("KEY_REUSED"));
		create(browser, other.textId(), key, other.hash()).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("KEY_REUSED"));
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void validatesExactHashAndMissingTargetsBeforeReusingAnActiveJob() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		for (String hash : new String[]{null, "", "bad", source.hash().toUpperCase()})
			create(browser, source.textId(), key(window), hash).andExpect(status().isUnprocessableContent())
					.andExpect(jsonPath("$.fieldErrors.expectedTextHash").exists());
		create(browser, source.textId(), key(window), "0".repeat(64)).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("WRONG_REVISION"));
		create(browser, UUID.randomUUID(), key(window), source.hash()).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("TEXT_REVISION_NOT_FOUND"));
		assertThat(jobs.count()).isZero();
		assertThat(receipts.count()).isZero();
		accepted(browser, source, key(window));
		create(browser, source.textId(), key(window), "0".repeat(64)).andExpect(status().isConflict());
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void latestJobInContentIsBoundToTheCurrentDraftAndANewDraftDoesNotReplaceTheOldJobTarget() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		TtsJobResponse job = accepted(browser, source, key(window));
		var before = content(browser, source);
		assertThat(before.draftText().latestTtsJob()).isEqualTo(job);
		mvc.perform(post("/api/v1/admin/places/" + source.placeId() + "/content/vi/text-revisions").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).header("Idempotency-Key", key(window)).header("If-Match", before.etag())
				.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(new SaveTextRevisionRequest("Bản T2", source.textId().toString()))))
				.andExpect(status().isCreated());
		var after = content(browser, source);
		assertThat(after.draftText().latestTtsJob()).isNull();
		assertThat(poll(browser, job.jobId()).textRevisionId()).isEqualTo(source.textId());
		var lease = dispatch.claim(job.jobId(), UUID.randomUUID()).orElseThrow();
		assertThat(lease.body()).isEqualTo(source.body());
		assertThat(lease.body()).isNotEqualTo("Bản T2");
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void eachSavedRevisionAndLanguageMayHaveItsOwnJob() throws Exception {
		Browser browser = login();
		String window = issue(browser);
		for (String lang : new String[]{"vi", "en", "fr", "ru"}) {
			Source source = source(lang);
			assertThat(accepted(browser, source, key(window)).language()).isEqualTo(lang);
		}
		assertThat(jobs.count()).isEqualTo(4);
	}

	@Test
	void receiptFailureRollsBackJobAndBothPointersAndTheSameKeyCanRetry() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String key = key(issue(browser));
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		create(browser, source.textId(), key, source.hash()).andExpect(status().isInternalServerError());
		assertThat(jobs.count()).isZero();
		assertThat(receipts.count()).isZero();
		var text = texts.findById(source.textId()).orElseThrow();
		assertThat(text.getActiveTtsJobId()).isNull();
		assertThat(text.getLatestTtsJobKey()).isNull();
		reset(receipts);
		accepted(browser, source, key);
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void receiptFailureWhileReusingAJobLeavesTheOriginalJobAndPointersIntact() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		TtsJobResponse first = accepted(browser, source, key(window));
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		create(browser, source.textId(), key(window), source.hash()).andExpect(status().isInternalServerError());
		assertThat(poll(browser, first.jobId())).isEqualTo(first);
		assertThat(jobs.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void twoWindowsCreatingTheSameTextConcurrentlyCommitOneJobAndTwoReceipts() throws Exception {
		Browser first = login();
		Browser second = login();
		Source source = source("vi");
		String firstKey = key(issue(first));
		String secondKey = key(issue(second));
		var responses = concurrently(() -> read(create(first, source.textId(), firstKey, source.hash()).andExpect(status().isAccepted())),
				() -> read(create(second, source.textId(), secondKey, source.hash()).andExpect(status().isAccepted())));
		assertThat(responses).extracting(this::jobId).containsOnly(jobId(responses.getFirst()));
		assertThat(responses).extracting(r -> r.result().get("reused")).containsExactlyInAnyOrder(false, true);
		assertThat(jobs.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(2);
	}

	@Test
	void sameKeyConcurrentRequestsProduceOneReceiptAndOneReplay() throws Exception {
		Browser first = login();
		Browser second = login();
		Source source = source("vi");
		String key = key(issue(first));
		var responses = concurrently(() -> read(create(first, source.textId(), key, source.hash()).andExpect(status().isAccepted())),
				() -> read(create(second, source.textId(), key, source.hash()).andExpect(status().isAccepted())));
		assertThat(responses).extracting(AdminOperationResponse::replayed).containsExactlyInAnyOrder(false, true);
		assertThat(responses).extracting(AdminOperationResponse::operationId).containsOnly(responses.getFirst().operationId());
		assertThat(jobs.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void dispatchCommitsOwnershipBeforeReturningOnlyTheSavedTextAndCannotClaimAgain() throws Exception {
		Browser browser = login();
		Source source = source("ru");
		TtsJobResponse job = accepted(browser, source, key(issue(browser)));
		UUID instance = UUID.randomUUID();
		clock.set(clock.instant().plusNanos(123456789));
		var lease = dispatch.claim(job.jobId(), instance).orElseThrow();
		var persisted = jobs.findById(job.jobId()).orElseThrow();
		assertThat(persisted.getPhase()).isEqualTo("DISPATCHING");
		assertThat(persisted.getWorkerInstance()).isEqualTo(instance);
		assertThat(persisted.getWorkerToken()).isEqualTo(lease.workerToken());
		assertThat(persisted.getDispatchedAt()).isEqualTo(clock.instant().truncatedTo(ChronoUnit.MICROS));
		assertThat(persisted.getPhaseVersion()).isEqualTo(lease.phaseVersion()).isEqualTo(1L);
		assertThat(lease.body()).isEqualTo(source.body());
		assertThat(lease.language()).isEqualTo("ru");
		assertThat(lease.model()).isEqualTo(TtsJobService.MODEL);
		assertThat(lease.voice()).isEqualTo(TtsJobService.VOICE);
		assertThat(lease.toString()).doesNotContain(source.body(), lease.workerToken().toString());
		assertThat(dispatch.claim(job.jobId(), instance)).isEmpty();
		assertThat(dispatch.claim(job.jobId(), UUID.randomUUID())).isEmpty();
	}

	@Test
	void twoWorkerInstancesCannotBothAcquireTheSameQueuedJob() throws Exception {
		Browser browser = login();
		TtsJobResponse job = accepted(browser, source("vi"), key(issue(browser)));
		var leases = concurrently(() -> dispatch.claim(job.jobId(), UUID.randomUUID()),
				() -> dispatch.claim(job.jobId(), UUID.randomUUID()));
		assertThat(leases.stream().filter(java.util.Optional::isPresent).count()).isEqualTo(1);
		assertThat(jobs.findById(job.jobId()).orElseThrow().getPhaseVersion()).isEqualTo(1L);
	}

	@Test
	void uncertainRequiresTheRightTokenAndPhaseVersionAndNeverOpensAnotherDispatch() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		TtsJobResponse job = accepted(browser, source, key(window));
		assertThat(dispatch.markUncertain(job.jobId(), UUID.randomUUID(), 0)).isFalse();
		var lease = dispatch.claim(job.jobId(), UUID.randomUUID()).orElseThrow();
		assertThat(dispatch.markUncertain(job.jobId(), UUID.randomUUID(), lease.phaseVersion())).isFalse();
		assertThat(dispatch.markUncertain(job.jobId(), lease.workerToken(), lease.phaseVersion() - 1)).isFalse();
		assertThat(dispatch.markUncertain(job.jobId(), lease.workerToken(), lease.phaseVersion())).isTrue();
		var uncertain = poll(browser, job.jobId());
		assertThat(uncertain.phase()).isEqualTo("UNCERTAIN");
		assertThat(uncertain.state()).isEqualTo("UNCERTAIN");
		assertThat(uncertain.outcomeUncertain()).isTrue();
		assertThat(uncertain.phaseVersion()).isEqualTo(2L);
		assertThat(dispatch.markUncertain(job.jobId(), lease.workerToken(), lease.phaseVersion())).isFalse();
		assertThat(dispatch.claim(job.jobId(), UUID.randomUUID())).isEmpty();
		var reused = read(create(browser, source.textId(), key(window), source.hash()).andExpect(status().isAccepted()));
		assertThat(jobId(reused)).isEqualTo(job.jobId());
		assertThat(reused.result().get("phase")).isEqualTo("UNCERTAIN");
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void dispatchingJobIsReusedByAnotherExplicitCreateWithoutASecondLease() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		TtsJobResponse job = accepted(browser, source, key(window));
		dispatch.claim(job.jobId(), UUID.randomUUID()).orElseThrow();
		var reused = read(create(browser, source.textId(), key(window), source.hash()).andExpect(status().isAccepted()));
		assertThat(jobId(reused)).isEqualTo(job.jobId());
		assertThat(reused.result().get("reused")).isEqualTo(true);
		assertThat(dispatch.claim(job.jobId(), UUID.randomUUID())).isEmpty();
	}

	@Test
	void deletedTargetIsNotRecreatedAndTheJobKeepsItsIdentityForInspection() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		TtsJobResponse job = accepted(browser, source, key(issue(browser)));
		// Mô phỏng phần tháo liên kết của giao dịch xóa tương lai, không tạo lại đích từ snapshot
		jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL, latest_tts_job_key = NULL");
		jdbc.update("UPDATE tts_job SET target_valid = FALSE, target_text_revision_id = NULL");
		jdbc.update("UPDATE content_slot SET draft_text_id = NULL");
		texts.deleteAll();
		slots.deleteAll();
		places.deleteAll();
		assertThat(dispatch.claim(job.jobId(), UUID.randomUUID())).isEmpty();
		assertThat(poll(browser, job.jobId()).targetValid()).isFalse();
		assertThat(poll(browser, job.jobId()).textRevisionId()).isEqualTo(source.textId());
		assertThat(places.count()).isZero();
		assertThat(texts.count()).isZero();
	}

	@Test
	void missingAndMalformedJobIdsDoNotCreateJobsOrClaimAnything() throws Exception {
		Browser browser = login();
		mvc.perform(get("/api/v1/admin/tts-jobs/" + UUID.randomUUID()).session(browser.session())).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("TTS_JOB_NOT_FOUND"));
		mvc.perform(get("/api/v1/admin/tts-jobs/bad").session(browser.session())).andExpect(status().isUnprocessableContent());
		assertThat(dispatch.claim(UUID.randomUUID(), UUID.randomUUID())).isEmpty();
		assertThat(dispatch.markUncertain(UUID.randomUUID(), UUID.randomUUID(), 0)).isFalse();
		assertThat(jobs.count()).isZero();
	}

	@Test
	void pollingDoesNotExtendQ3AndExpiredSessionsCannotPollButDoNotCancelAcceptedJobs() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		TtsJobResponse job = accepted(browser, source, key(issue(browser)));
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(29)));
		poll(browser, job.jobId());
		content(browser, source);
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)));
		mvc.perform(get("/api/v1/admin/tts-jobs/" + job.jobId()).session(browser.session())).andExpect(status().isUnauthorized());
		assertThat(dispatch.claim(job.jobId(), UUID.randomUUID())).isPresent();
		Browser again = login();
		assertThat(poll(again, job.jobId()).phase()).isEqualTo("DISPATCHING");
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void explicitInvalidCreateCountsAsTtsActivityWhileCsrfAndMalformedJsonDoNot() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		clock.set(clock.instant().plus(Duration.ofMinutes(20)));
		mvc.perform(post(createPath(source.textId())).session(browser.session()).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
		mvc.perform(post(createPath(source.textId())).session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token())
				.contentType(MediaType.APPLICATION_JSON).content("{")).andExpect(status().isBadRequest());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
		create(browser, source.textId(), key(window), "0".repeat(64)).andExpect(status().isConflict());
		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(jsonPath("$.lastInteractiveAt").value(clock.instant().toString()));
		assertThat(jobs.count()).isZero();
	}

	@Test
	void commandWindowOwnershipExpiryAndServiceEpochAreCheckedBeforeReplay() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		String key = key(window);
		accepted(browser, source, key);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "other-tts-owner", passwords.encode(PASSWORD), true));
		Browser other = login("other-tts-owner");
		create(other, source.textId(), key, source.hash()).andExpect(status().isForbidden());
		jdbc.update("UPDATE admin_command_window SET service_epoch = ?", UUID.randomUUID().toString());
		create(browser, source.textId(), key, source.hash()).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("SERVICE_EPOCH_MISMATCH"));
		jdbc.update("UPDATE admin_command_window SET service_epoch = ?", "00000000-0000-0000-0000-000000000001");
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofHours(24)));
		Browser again = login();
		create(again, source.textId(), key, source.hash()).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("COMMAND_WINDOW_EXPIRED"));
		assertThat(jobs.count()).isEqualTo(1);
	}

	private <T> List<T> concurrently(Callable<T> a, Callable<T> b) throws Exception {
		var barrier = new CyclicBarrier(2);
		try (var pool = Executors.newFixedThreadPool(2)) {
			var first = pool.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return a.call();
			});
			var second = pool.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return b.call();
			});
			return List.of(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS));
		}
	}

	private Source source(String language) throws Exception {
		var place = places.saveAndFlush(new Place(UUID.randomUUID(), "Địa điểm TTS fixture", clock.instant()));
		var slot = slots.saveAndFlush(new ContentSlot(UUID.randomUUID(), place, language));
		String body = "  Huế 😀\nVăn bản đã lưu " + language + "\r\n";
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8));
		var text = texts.saveAndFlush(new TextRevision(UUID.randomUUID(), slot, body, hash, clock.instant()));
		slot.setDraftTextId(text.getId());
		slots.saveAndFlush(slot);
		return new Source(place.getId(), slot.getId(), text.getId(), body, HexFormat.of().formatHex(hash));
	}

	private AdminContentResponse content(Browser browser, Source source) throws Exception {
		var result = mvc.perform(get("/api/v1/admin/places/" + source.placeId() + "/content/vi").session(browser.session()))
				.andExpect(status().isOk()).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), AdminContentResponse.class);
	}

	private Browser login() throws Exception {
		return login("tts-owner");
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

	private ResultActions create(Browser browser, UUID text, String key, String hash) throws Exception {
		return mvc.perform(post(createPath(text)).session(browser.session()).header(browser.csrf().headerName(), browser.csrf().token())
				.header("Idempotency-Key", key).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(new CreateTtsRequest(hash))));
	}

	private AdminOperationResponse read(ResultActions response) throws Exception {
		return json.readValue(response.andReturn().getResponse().getContentAsString(), AdminOperationResponse.class);
	}

	private TtsJobResponse accepted(Browser browser, Source source, String key) throws Exception {
		return poll(browser, jobId(read(create(browser, source.textId(), key, source.hash()).andExpect(status().isAccepted()))));
	}

	private TtsJobResponse poll(Browser browser, UUID job) throws Exception {
		var result = mvc.perform(get("/api/v1/admin/tts-jobs/" + job).session(browser.session())).andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store")).andReturn();
		return json.readValue(result.getResponse().getContentAsString(), TtsJobResponse.class);
	}

	private UUID jobId(AdminOperationResponse response) {
		return UUID.fromString(response.result().get("jobId").toString());
	}

	private static String key(String window) {
		return window + ":" + UUID.randomUUID();
	}

	private static String createPath(UUID textId) {
		return "/api/v1/admin/text-revisions/" + textId + "/tts-jobs";
	}
}
