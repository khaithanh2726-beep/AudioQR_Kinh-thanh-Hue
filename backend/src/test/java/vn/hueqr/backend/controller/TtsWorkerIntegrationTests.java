package vn.hueqr.backend.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import vn.hueqr.backend.service.*;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.entity.FileAsset;
import vn.hueqr.backend.dto.RetryTtsRequest;
import vn.hueqr.backend.support.WavFixture;
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
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class, TtsWorkerIntegrationTests.StoreConfiguration.class})
class TtsWorkerIntegrationTests {
	private static final String PASSWORD = "Mat-khau-tts-fixture";
	private static final Path ROOT = temporaryRoot();

	@org.springframework.test.context.DynamicPropertySource
	static void properties(org.springframework.test.context.DynamicPropertyRegistry registry) {
		registry.add("HUEQR_AUDIO_ROOT", ROOT::toString);
	}

	private static Path temporaryRoot() {
		try {
			return Files.createTempDirectory("hueqr-wav-owner-test-");
		}
		catch (IOException error) {
			throw new java.io.UncheckedIOException(error);
		}
	}

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

	@TestConfiguration(proxyBeanMethods = false)
	static class StoreConfiguration {
		@Bean
		WavResultStore resultStore(ObjectMapper json) throws IOException {
			return new WavResultStore(ROOT, json);
		}
	}

	@Autowired
	private WavResultStore store;

	@Autowired
	private TtsResultService results;

	@Autowired
	private TtsJobService tts;

	@Autowired
	private TtsRecoveryService recovery;

	@MockitoSpyBean
	private FileAssetRepository files;

	@BeforeEach
	void setUp() {
		clearData();
		clock.set(GuestSessionTestClock.BASE);
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "tts-owner", passwords.encode(PASSWORD), true));
	}

	@AfterEach
	void clearData() {
		reset(receipts);
		reset(files);
		receipts.deleteAll();
		windows.deleteAll();
		references.deleteAll();
		jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL, latest_tts_job_key = NULL");
		jobs.deleteAll();
		files.deleteAll();
		jdbc.update("UPDATE content_slot SET draft_text_id = NULL");
		texts.deleteAll();
		slots.deleteAll();
		jdbc.update("UPDATE place SET active_region_revision_id = NULL");
		regions.deleteAll();
		places.deleteAll();
		accounts.deleteAll();
	}

	@Test
	void workerPersistsVerifiedWavMetadataAndStopsBeforeMp3WithoutPublishing() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		TtsJobResponse job = accepted(browser, source, key(issue(browser)));
		var calls = new AtomicInteger();
		TtsProvider provider = lease -> {
			calls.incrementAndGet();
			assertThat(jobs.findById(job.jobId()).orElseThrow().getPhase()).isEqualTo("DISPATCHING");
			assertThat(lease.body()).isEqualTo(source.body());
			assertThat(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
			return WavFixture.oneSecond();
		};
		TtsWorker worker = worker(provider);
		assertThat(worker.run(job.jobId())).isTrue();
		assertThat(worker.run(job.jobId())).isFalse();
		TtsJobResponse result = poll(browser, job.jobId());
		assertThat(result.phase()).isEqualTo("RESULT_STAGED");
		assertThat(result.state()).isEqualTo("PROCESSING");
		assertThat(result.sourceWavFileId()).isNotNull();
		assertThat(result.terminalAt()).isNull();
		assertThat(calls.get()).isEqualTo(1);
		var file = files.findById(result.sourceWavFileId()).orElseThrow();
		assertThat(file.getState()).isEqualTo("READY");
		assertThat(file.getByteSize()).isEqualTo(48044);
		assertThat(file.getDuration()).isEqualByComparingTo("1");
		assertThat(texts.findById(source.textId()).orElseThrow().getActiveTtsJobId()).isEqualTo(job.jobId());
		assertThat(references.count()).isZero();
		assertThat(places.findById(source.placeId()).orElseThrow().isVisible()).isFalse();
	}

	@Test
	void definiteFailureIsTerminalAndRequiresExplicitVersionedRetryInsteadOfCreateAgain() throws Exception {
		Browser browser = login();
		Source source = source("fr");
		String window = issue(browser);
		TtsJobResponse job = accepted(browser, source, key(window));
		worker(lease -> { throw new TtsProvider.Failure("PROVIDER_QUOTA", true); }).run(job.jobId());
		var failed = poll(browser, job.jobId());
		assertThat(failed.phase()).isEqualTo("FAILED");
		assertThat(failed.failureCode()).isEqualTo("PROVIDER_QUOTA");
		assertThat(failed.retryAction()).isEqualTo("CREATE_PROVIDER_ATTEMPT");
		assertThat(failed.terminalAt()).isNotNull();
		assertThat(texts.findById(source.textId()).orElseThrow().getActiveTtsJobId()).isNull();
		var reused = read(create(browser, source.textId(), key(window), source.hash()).andExpect(status().isAccepted()));
		assertThat(jobId(reused)).isEqualTo(job.jobId());
		assertThat(jobs.count()).isEqualTo(1);
		var retried = read(retry(browser, job.jobId(), key(window), failed.phaseVersion()).andExpect(status().isAccepted()));
		assertThat(jobId(retried)).isNotEqualTo(job.jobId());
		var next = jobs.findById(jobId(retried)).orElseThrow();
		assertThat(next.getRetryOfJobId()).isEqualTo(job.jobId());
		assertThat(next.getPhase()).isEqualTo("QUEUED");
		assertThat(next.getTextRevisionKeySnapshot()).isEqualTo(source.textId());
		assertThat(next.getLanguageSnapshot()).isEqualTo("fr");
		assertThat(poll(browser, job.jobId())).isEqualTo(failed);
	}

	@Test
	void timeoutOrUnexpectedProviderExceptionRemainUncertainAndDoNotAllowNewAttempt() throws Exception {
		Browser browser = login();
		String window = issue(browser);
		for (TtsProvider provider : List.<TtsProvider>of(
				lease -> { throw new TtsProvider.Failure("PROVIDER_OUTCOME_UNCERTAIN", false); },
				lease -> { throw new IllegalStateException("fixture lost result"); })) {
			Source source = source("vi");
			var job = accepted(browser, source, key(window));
			worker(provider).run(job.jobId());
			var uncertain = poll(browser, job.jobId());
			assertThat(uncertain.phase()).isEqualTo("UNCERTAIN");
			assertThat(uncertain.retryAction()).isEqualTo("INSPECT_ONLY");
			assertThat(uncertain.terminalAt()).isNull();
			retry(browser, job.jobId(), key(window), uncertain.phaseVersion()).andExpect(status().isConflict())
					.andExpect(jsonPath("$.code").value("TTS_RETRY_NOT_ALLOWED"));
			assertThat(jobId(read(create(browser, source.textId(), key(window), source.hash())))).isEqualTo(job.jobId());
			var checked = read(check(browser, job.jobId(), key(window)).andExpect(status().isOk()));
			assertThat(checked.result().get("inspection")).isEqualTo("NO_LOCAL_EVIDENCE");
			assertThat(poll(browser, job.jobId()).phase()).isEqualTo("UNCERTAIN");
		}
		assertThat(jobs.count()).isEqualTo(2);
	}

	@Test
	void invalidProviderWavIsADefiniteOutputFailureAndNeverCreatesReadyFiles() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		var job = accepted(browser, source, key(issue(browser)));
		worker(lease -> new byte[8]).run(job.jobId());
		var failed = poll(browser, job.jobId());
		assertThat(failed.phase()).isEqualTo("FAILED");
		assertThat(failed.failureCode()).isEqualTo("PROVIDER_OUTPUT_INVALID");
		assertThat(files.count()).isZero();
	}

	@Test
	void databaseRegistrationFailureKeepsManifestAndExplicitCheckRecoversWithoutCallingProviderAgain() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		var job = accepted(browser, source, key(window));
		var calls = new AtomicInteger();
		doThrow(new DataIntegrityViolationException("fixture registration failure")).when(files).saveAndFlush(any(FileAsset.class));
		TtsWorker worker = worker(lease -> { calls.incrementAndGet(); return WavFixture.oneSecond(); });
		worker.run(job.jobId());
		assertThat(poll(browser, job.jobId()).phase()).isEqualTo("UNCERTAIN");
		assertThat(files.count()).isZero();
		reset(files);
		String key = key(window);
		var checked = read(check(browser, job.jobId(), key).andExpect(status().isOk()));
		assertThat(checked.result().get("inspection")).isEqualTo("LOCAL_WAV_VERIFIED");
		assertThat(checked.result().get("phase")).isEqualTo("RESULT_STAGED");
		var replay = read(check(browser, job.jobId(), key).andExpect(status().isOk()));
		assertThat(replay.replayed()).isTrue();
		assertThat(replay.result()).isEqualTo(checked.result());
		assertThat(files.count()).isEqualTo(1);
		assertThat(worker.run(job.jobId())).isFalse();
		assertThat(calls.get()).isEqualTo(1);
	}

	@Test
	void receiptFailureRollsBackRecoveredFileAndJobAndAllowsSameCheckKeyToRecoverLater() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		var job = accepted(browser, source, key(window));
		var lease = dispatch.claim(job.jobId(), UUID.randomUUID()).orElseThrow();
		store.save(job.jobId(), lease.workerToken(), source.hash(), WavFixture.oneSecond());
		dispatch.markUncertain(job.jobId(), lease.workerToken(), lease.phaseVersion());
		String key = key(window);
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		check(browser, job.jobId(), key).andExpect(status().isInternalServerError());
		assertThat(poll(browser, job.jobId()).phase()).isEqualTo("UNCERTAIN");
		assertThat(files.count()).isZero();
		reset(receipts);
		check(browser, job.jobId(), key).andExpect(status().isOk()).andExpect(jsonPath("$.result.phase").value("RESULT_STAGED"));
		assertThat(files.count()).isEqualTo(1);
	}

	@Test
	void retryReceiptFailureRollsBackNewJobAndPointersAndPreservesFailedAttempt() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		String window = issue(browser);
		var job = accepted(browser, source, key(window));
		worker(lease -> { throw new TtsProvider.Failure("PROVIDER_REJECTED", true); }).run(job.jobId());
		var failed = poll(browser, job.jobId());
		String key = key(window);
		doThrow(new DataIntegrityViolationException("fixture receipt failure")).when(receipts).saveAndFlush(any(OperationReceipt.class));
		retry(browser, job.jobId(), key, failed.phaseVersion()).andExpect(status().isInternalServerError());
		assertThat(jobs.count()).isEqualTo(1);
		var text = texts.findById(source.textId()).orElseThrow();
		assertThat(text.getActiveTtsJobId()).isNull();
		assertThat(text.getLatestTtsJobKey()).isEqualTo(job.jobId());
		assertThat(poll(browser, job.jobId())).isEqualTo(failed);
		reset(receipts);
		retry(browser, job.jobId(), key, failed.phaseVersion()).andExpect(status().isAccepted());
		assertThat(jobs.count()).isEqualTo(2);
	}

	@Test
	void retryRejectsQueuedStagedUnknownMissingVersionAndStaleVersion() throws Exception {
		Browser browser = login();
		String window = issue(browser);
		var job = accepted(browser, source("vi"), key(window));
		retry(browser, job.jobId(), key(window), null).andExpect(status().isUnprocessableContent());
		retry(browser, job.jobId(), key(window), -1L).andExpect(status().isUnprocessableContent());
		retry(browser, job.jobId(), key(window), 999L).andExpect(status().isPreconditionFailed());
		retry(browser, job.jobId(), key(window), job.phaseVersion()).andExpect(status().isConflict());
		worker(lease -> WavFixture.oneSecond()).run(job.jobId());
		var staged = poll(browser, job.jobId());
		retry(browser, job.jobId(), key(window), staged.phaseVersion()).andExpect(status().isConflict());
		assertThat(jobs.count()).isEqualTo(1);
	}

	@Test
	void retryReplayReturnsSameNewAttemptButAnotherKeyCannotRepeatTheOldFailedJob() throws Exception {
		Browser browser = login();
		String window = issue(browser);
		var job = accepted(browser, source("vi"), key(window));
		worker(lease -> { throw new TtsProvider.Failure("PROVIDER_QUOTA", true); }).run(job.jobId());
		long version = poll(browser, job.jobId()).phaseVersion();
		String key = key(window);
		var first = read(retry(browser, job.jobId(), key, version).andExpect(status().isAccepted()));
		var replay = read(retry(browser, job.jobId(), key, version).andExpect(status().isAccepted()));
		assertThat(replay.replayed()).isTrue();
		assertThat(replay.result()).isEqualTo(first.result());
		retry(browser, job.jobId(), key(window), version).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("WRONG_REVISION"));
		retry(browser, job.jobId(), key, version + 1).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("KEY_REUSED"));
		assertThat(jobs.count()).isEqualTo(2);
	}

	@Test
	void twoExplicitRetriesFromDifferentWindowsCreateOnlyOneNewAttempt() throws Exception {
		Browser first = login();
		Browser second = login();
		String firstWindow = issue(first);
		String secondWindow = issue(second);
		var job = accepted(first, source("vi"), key(firstWindow));
		worker(lease -> { throw new TtsProvider.Failure("PROVIDER_QUOTA", true); }).run(job.jobId());
		long version = poll(first, job.jobId()).phaseVersion();
		var results = concurrently(() -> retry(first, job.jobId(), key(firstWindow), version).andReturn().getResponse().getStatus(),
				() -> retry(second, job.jobId(), key(secondWindow), version).andReturn().getResponse().getStatus());
		assertThat(results).containsExactlyInAnyOrder(202, 409);
		assertThat(jobs.count()).isEqualTo(2);
	}

	@Test
	void twoWorkersOnlyCallProviderOnceAndRegisterOneWav() throws Exception {
		Browser browser = login();
		var job = accepted(browser, source("vi"), key(issue(browser)));
		var calls = new AtomicInteger();
		TtsProvider provider = lease -> { calls.incrementAndGet(); return WavFixture.oneSecond(); };
		var answers = concurrently(() -> worker(provider).run(job.jobId()), () -> worker(provider).run(job.jobId()));
		assertThat(answers).containsExactlyInAnyOrder(true, false);
		assertThat(calls.get()).isEqualTo(1);
		assertThat(files.count()).isEqualTo(1);
	}

	@Test
	void staleTokenCannotFinalizeOrFailAJobAndRepeatedStagingIsIdempotent() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		var job = accepted(browser, source, key(issue(browser)));
		var lease = dispatch.claim(job.jobId(), UUID.randomUUID()).orElseThrow();
		var staged = store.save(job.jobId(), lease.workerToken(), source.hash(), WavFixture.oneSecond());
		var wrong = new WavResultStore.Staged(staged.jobId(), UUID.randomUUID(), staged.textHash(), staged.fileId(), staged.storageKey(),
				staged.checksum(), staged.byteSize(), staged.duration());
		assertThat(results.stage(wrong)).isFalse();
		assertThat(results.fail(job.jobId(), UUID.randomUUID(), lease.phaseVersion(), "PROVIDER_QUOTA")).isFalse();
		assertThat(results.stage(staged)).isTrue();
		assertThat(results.stage(staged)).isTrue();
		assertThat(results.fail(job.jobId(), lease.workerToken(), lease.phaseVersion(), "PROVIDER_QUOTA")).isFalse();
		assertThat(files.count()).isEqualTo(1);
	}

	@Test
	void deletedTargetDiscardsLateWavWithoutRecreatingPlaceTextOrRegisteringFile() throws Exception {
		Browser browser = login();
		Source source = source("vi");
		var job = accepted(browser, source, key(issue(browser)));
		var lease = dispatch.claim(job.jobId(), UUID.randomUUID()).orElseThrow();
		var staged = store.save(job.jobId(), lease.workerToken(), source.hash(), WavFixture.oneSecond());
		jdbc.update("UPDATE text_revision SET active_tts_job_id = NULL, latest_tts_job_key = NULL");
		jdbc.update("UPDATE tts_job SET target_valid = FALSE, target_text_revision_id = NULL");
		jdbc.update("UPDATE content_slot SET draft_text_id = NULL");
		texts.deleteAll();
		slots.deleteAll();
		places.deleteAll();
		assertThat(results.stage(staged)).isFalse();
		assertThat(poll(browser, job.jobId()).phase()).isEqualTo("DISCARDED");
		assertThat(poll(browser, job.jobId()).terminalAt()).isNotNull();
		assertThat(files.count()).isZero();
		assertThat(places.count()).isZero();
	}

	@Test
	void sessionExpiryDoesNotCancelWorkerButCheckAndRetryRequireLoginAndCsrf() throws Exception {
		Browser browser = login();
		var job = accepted(browser, source("vi"), key(issue(browser)));
		mvc.perform(post("/api/v1/admin/tts-jobs/" + job.jobId() + "/check").session(browser.session())).andExpect(status().isForbidden());
		mvc.perform(post("/api/v1/admin/tts-jobs/" + job.jobId() + "/retry").session(browser.session())
				.contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
		clock.set(clock.instant().plus(Duration.ofMinutes(30)));
		assertThat(worker(lease -> WavFixture.oneSecond()).run(job.jobId())).isTrue();
		mvc.perform(get("/api/v1/admin/tts-jobs/" + job.jobId()).session(browser.session())).andExpect(status().isUnauthorized());
		assertThat(poll(login(), job.jobId()).phase()).isEqualTo("RESULT_STAGED");
	}

	@Test
	void twoChecksRecoverOneManifestWithoutDuplicateFilesAndBothHaveReceipts() throws Exception {
		Browser first = login();
		Browser second = login();
		String firstWindow = issue(first);
		String secondWindow = issue(second);
		Source source = source("vi");
		var job = accepted(first, source, key(firstWindow));
		var lease = dispatch.claim(job.jobId(), UUID.randomUUID()).orElseThrow();
		store.save(job.jobId(), lease.workerToken(), source.hash(), WavFixture.oneSecond());
		dispatch.markUncertain(job.jobId(), lease.workerToken(), lease.phaseVersion());
		var answers = concurrently(() -> check(first, job.jobId(), key(firstWindow)).andReturn().getResponse().getStatus(),
				() -> check(second, job.jobId(), key(secondWindow)).andReturn().getResponse().getStatus());
		assertThat(answers).containsOnly(200);
		assertThat(files.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(3);
	}

	private TtsWorker worker(TtsProvider provider) {
		return new TtsWorker(UUID.randomUUID(), dispatch, provider, store, results);
	}

	private ResultActions check(Browser browser, UUID job, String key) throws Exception {
		return mvc.perform(post("/api/v1/admin/tts-jobs/" + job + "/check").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).header("Idempotency-Key", key));
	}

	private ResultActions retry(Browser browser, UUID job, String key, Long version) throws Exception {
		return mvc.perform(post("/api/v1/admin/tts-jobs/" + job + "/retry").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).header("Idempotency-Key", key)
				.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(new RetryTtsRequest(version))));
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
