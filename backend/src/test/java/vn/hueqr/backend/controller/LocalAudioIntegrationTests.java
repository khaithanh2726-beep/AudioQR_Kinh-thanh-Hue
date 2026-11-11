package vn.hueqr.backend.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.RetryTtsRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.LocalAudioRepository;
import vn.hueqr.backend.repository.LifecycleRepository;
import vn.hueqr.backend.service.*;
import vn.hueqr.backend.support.*;
import vn.hueqr.backend.support.AudioDataFixture.Source;
import static vn.hueqr.backend.repository.LifecycleRepository.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class, LocalAudioIntegrationTests.Configuration.class})
class LocalAudioIntegrationTests {
	private static final Path ROOT = temporary();
	// Bytes giả chỉ kiểm giao dịch và vòng đời, không là file đã qua codec hoặc nghe duyệt
	private static final byte[] MP3 = "fixture-mp3-transport-only".getBytes(java.nio.charset.StandardCharsets.UTF_8);

	@Autowired LocalAudioService service;
	@Autowired TtsJobService jobs;
	@Autowired TtsRecoveryService recovery;
	@Autowired AdminPlaceDeletionService deletion;
	@Autowired AdminCommandService commands;
	@Autowired WorkspaceCleanupService workspace;
	@Autowired JdbcTemplate sql;
	@Autowired GuestSessionTestClock.MutableClock clock;
	@Autowired FakeEncoder encoder;
	@Autowired AdminAccountRepository accounts;
	@Autowired PasswordEncoder passwords;
	@Autowired MetadataCleanupService cleanup;
	@Autowired RuntimeOwnerService owners;
	@Autowired LifecycleRepository lifecycle;
	@MockitoSpyBean LocalAudioRepository local;
	@MockitoSpyBean vn.hueqr.backend.repository.OperationReceiptRepository receipts;
	private Source source;
	private UUID account;

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("HUEQR_AUDIO_ROOT", ROOT::toString);
	}

	@BeforeEach
	void setUp() throws Exception {
		clear();
		reset(local);
		reset(receipts);
		encoder.failure = null;
		encoder.afterWrite = () -> {};
		encoder.bytes = MP3;
		encoder.encodes.set(0);
		clock.set(AudioDataFixture.NOW);
		account = UUID.randomUUID();
		accounts.saveAndFlush(new AdminAccount(account,"local-fixture",passwords.encode("Local-fixture-pass"),true));
		source = seed("vi");
	}

	@AfterEach
	void clear() {
		sql.update("UPDATE guest_session SET current_opening_id = NULL");
		for (String table : new String[]{"file_claim","guest_request_result","guest_content_grant","guest_open_intent","place_view","guest_session","operation_receipt","admin_command_window"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE content_slot SET active_pair_id = NULL, draft_text_id = NULL");
		for (String table : new String[]{"content_pair","audio_review_ref","audio_revision","text_review_ref"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE text_revision SET active_tts_job_id = NULL");
		for (String table : new String[]{"workspace_file","runtime_task","tts_job","text_revision","content_slot"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE place SET active_region_revision_id = NULL");
		for (String table : new String[]{"region_revision","place","file_asset","admin_account"})
			sql.update("DELETE FROM " + table);
	}

	@Test
	void succeedsOnceWithExactSourceAndNoReviewApplyOrVisibilityChange() {
		assertThat(service.run(source.job())).isTrue();
		var response = jobs.detail(source.job().toString());
		assertThat(response.state()).isEqualTo("SUCCEEDED");
		assertThat(response.terminalAt()).isEqualTo(clock.instant());
		assertThat(response.result().wavFileId()).isEqualTo(source.wav());
		assertThat(response.result().checksum()).isEqualTo(WavPolicy.hash(MP3));
		assertThat(response.result().durationSeconds()).isEqualByComparingTo("1.25");
		assertThat(service.run(source.job())).isFalse();
		assertThat(encoder.encodes.get()).isEqualTo(1);
		assertThat(count("audio_revision")).isEqualTo(1);
		assertThat(count("audio_review_ref") + count("text_review_ref") + count("content_pair")).isZero();
		assertThat(sql.queryForObject("SELECT visible FROM place",Boolean.class)).isFalse();
		assertThat(sql.queryForObject("SELECT active_tts_job_id FROM text_revision",byte[].class)).isNull();
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void localFailureRetainsTheWavAndOffersLocalRetryWithoutANewProviderJob() {
		encoder.failure = new IOException("LOCAL_TOOL_UNAVAILABLE");
		assertThat(service.run(source.job())).isTrue();
		var response = jobs.detail(source.job().toString());
		assertThat(response.state()).isEqualTo("FAILED");
		assertThat(response.retryAction()).isEqualTo("REPROCESS_LOCAL");
		assertThat(response.sourceWavFileId()).isEqualTo(source.wav());
		assertThat(response.failureCode()).isEqualTo("LOCAL_TOOL_UNAVAILABLE");
		assertThat(count("audio_revision")).isZero();
		assertThat(service.run(source.job())).isFalse();
	}

	@Test
	void explicitLocalRetryKeepsTheSameJobAndReplaysTheReceiptInsteadOfRetryingTwice() {
		encoder.failure = new IOException("LOCAL_CONVERSION_FAILED");
		service.run(source.job());
		var version = jobs.detail(source.job().toString()).phaseVersion();
		String key = key();
		var first = recovery.retry(account,key,source.job().toString(),new RetryTtsRequest(version));
		assertThat(first.result()).containsEntry("jobId",source.job().toString()).containsEntry("reprocessLocal",true);
		assertThat(recovery.retry(account,key,source.job().toString(),new RetryTtsRequest(version)).replayed()).isTrue();
		encoder.failure = null;
		assertThat(service.run(source.job())).isTrue();
		assertThat(count("tts_job")).isEqualTo(1);
		assertThat(jobs.detail(source.job().toString()).state()).isEqualTo("SUCCEEDED");
	}

	@Test
	void staleRetryVersionCannotQueueALocalAttempt() {
		encoder.failure = new IOException("LOCAL_CONVERSION_FAILED");
		service.run(source.job());
		assertThatThrownBy(() -> recovery.retry(account,key(),source.job().toString(),new RetryTtsRequest(0L)))
				.isInstanceOf(AdminCommandException.class).hasMessage("VERSION_CONFLICT");
		assertThat(jobs.detail(source.job().toString()).phase()).isEqualTo("FAILED");
	}

	@Test
	void aNewDraftCannotRedirectTheAudioAwayFromTheOriginallySavedText() {
		UUID draft = UUID.randomUUID();
		sql.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, 'Nháp mới', ?, ?)",bytes(draft),bytes(source.slot()),new byte[32],time(clock.instant()));
		sql.update("UPDATE content_slot SET draft_text_id = ?",bytes(draft));
		service.run(source.job());
		assertThat(sql.queryForObject("SELECT text_revision_id FROM audio_revision",byte[].class)).isEqualTo(bytes(source.text()));
		assertThat(sql.queryForObject("SELECT draft_text_id FROM content_slot",byte[].class)).isEqualTo(bytes(draft));
	}

	@Test
	void deletingTheTargetDuringConversionDiscardsTheResultWithoutRecreatingData() {
		encoder.afterWrite = () -> deletion.delete(account,key(),source.place().toString(),"\"0\"");
		service.run(source.job());
		assertThat(jobs.detail(source.job().toString()).phase()).isEqualTo("DISCARDED");
		assertThat(count("audio_revision") + count("place") + count("text_revision")).isZero();
		assertThat(count("file_asset")).isEqualTo(2);
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void aFailureAfterRealAudioInsertRollsBackAudioPointerAndSuccessButKeepsTheCompleteFile() {
		doAnswer(call -> {
			call.callRealMethod();
			throw new DataIntegrityViolationException("LOCAL_FINALIZE_FIXTURE");
		}).when(local).succeed(any(),any(),any(),any(),any(),any());
		assertThatThrownBy(() -> service.run(source.job())).isInstanceOf(DataIntegrityViolationException.class);
		assertThat(count("audio_revision")).isZero();
		assertThat(jobs.detail(source.job().toString()).phase()).isEqualTo("CONVERTING");
		assertThat(count("file_asset")).isEqualTo(2);
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void completedManifestCanFinalizeAfterRollbackWithoutEncodingAgain() {
		doThrow(new DataIntegrityViolationException("LOCAL_FINALIZE_FIXTURE")).when(local).succeed(any(),any(),any(),any(),any(),any());
		assertThatThrownBy(() -> service.run(source.job())).isInstanceOf(DataIntegrityViolationException.class);
		reset(local);
		assertThat(service.run(source.job())).isTrue();
		assertThat(encoder.encodes.get()).isEqualTo(1);
		assertThat(count("audio_revision")).isEqualTo(1);
	}

	@Test
	void twoWorkersCannotEncodeTheSameJobAndTheLiveScopeRemainsUntilBytesFinish() throws Exception {
		var entered = new CountDownLatch(1);
		var release = new CountDownLatch(1);
		encoder.afterWrite = () -> {
			entered.countDown();
			try {
				if (!release.await(15,TimeUnit.SECONDS))
					throw new IllegalStateException("fixture-timeout");
			}
			catch (InterruptedException error) {
				throw new RuntimeException(error);
			}
		};
		try (var executor = Executors.newSingleThreadExecutor()) {
			var first = executor.submit(() -> service.run(source.job()));
			try {
				assertThat(entered.await(15,TimeUnit.SECONDS)).isTrue();
				assertThat(count("runtime_task")).isEqualTo(1);
				assertThat(service.run(source.job())).isFalse();
				assertThat(count("runtime_task")).isEqualTo(1);
				assertThat(recovery.check(account,key(),source.job().toString()).result()).containsEntry("phase","CONVERTING");
				assertThat(count("audio_revision")).isZero();
			}
			finally {
				release.countDown();
			}
			assertThat(first.get(15,TimeUnit.SECONDS)).isTrue();
		}
		assertThat(encoder.encodes.get()).isEqualTo(1);
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void corruptSourceWavIsNotConvertedOrOfferedAsAProviderRetry() throws Exception {
		Files.write(ROOT.resolve(source.wav()+".wav"),new byte[]{1,2,3});
		service.run(source.job());
		assertThat(encoder.encodes.get()).isZero();
		assertThat(jobs.detail(source.job().toString()).failureCode()).isEqualTo("LOCAL_SOURCE_INVALID");
		assertThat(jobs.detail(source.job().toString()).retryAction()).isEqualTo("INSPECT_ONLY");
		long version = jobs.detail(source.job().toString()).phaseVersion();
		assertThatThrownBy(() -> recovery.retry(account,key(),source.job().toString(),new RetryTtsRequest(version))).hasMessage("TTS_RETRY_NOT_ALLOWED");
	}

	@Test
	void anEmptyOutputCannotBecomeAReadyMp3OrAudio() {
		encoder.bytes = new byte[0];
		service.run(source.job());
		assertThat(jobs.detail(source.job().toString()).failureCode()).isEqualTo("LOCAL_RESULT_INVALID");
		assertThat(count("audio_revision")).isZero();
		assertThat(count("file_asset")).isEqualTo(1);
	}

	@Test
	void allFourLanguagesRemainBoundToTheirOwnSavedText() throws Exception {
		for (String language : new String[]{"vi","en","fr","ru"}) {
			Source current = language.equals("vi") ? source : seed(language);
			service.run(current.job());
			var result = jobs.detail(current.job().toString());
			assertThat(result.language()).isEqualTo(language);
			assertThat(result.textRevisionId()).isEqualTo(current.text());
			assertThat(result.result().wavFileId()).isEqualTo(current.wav());
		}
		assertThat(count("audio_revision")).isEqualTo(4);
	}

	@Test
	void unknownProviderWorkIsNeverPickedUpAsLocalConversion() {
		sql.update("UPDATE tts_job SET phase='UNCERTAIN', source_wav_file_id=NULL, result_staged_at=NULL WHERE id=?",bytes(source.job()));
		assertThat(service.run(source.job())).isFalse();
		assertThat(jobs.detail(source.job().toString()).state()).isEqualTo("UNCERTAIN");
		assertThat(encoder.encodes.get()).isZero();
	}

	@Test
	void succeededWorkspaceCanBeReleasedAfterItsHourWithoutDeletingReferencedMp3() {
		service.run(source.job());
		assertThat(workspace.sweep(100)).isZero();
		clock.set(clock.instant().plus(Duration.ofHours(1)));
		assertThat(workspace.sweep(100)).isPositive();
		assertThat(count("file_asset")).isEqualTo(2);
		assertThat(count("audio_revision")).isEqualTo(1);
	}

	@Test
	void schemaRejectsConvertingWithoutAnOwnerAndSuccessWithoutTerminalTime() {
		for (String phase : new String[]{"CONVERTING","SUCCEEDED"}) {
			Throwable error = catchThrowable(() -> sql.update("UPDATE tts_job SET phase=? WHERE id=?",phase,bytes(source.job())));
			assertThat(error).isInstanceOf(org.springframework.dao.DataAccessException.class);
			assertThat(error.getCause()).isInstanceOfSatisfying(java.sql.SQLException.class, sqlError -> {
				assertThat(sqlError.getErrorCode()).isEqualTo(3819);
				assertThat(sqlError.getMessage()).contains(phase.equals("CONVERTING") ? "ck_tts_conversion_marker" : "ck_tts_job_terminal");
			});
		}
		assertThat(jobs.detail(source.job().toString()).phase()).isEqualTo("RESULT_STAGED");
	}

	@Test
	void aCompleteFileWithoutItsManifestIsNotOverwrittenOrSilentlyAccepted() throws Exception {
		doThrow(new DataIntegrityViolationException("LOCAL_FINALIZE_FIXTURE")).when(local).succeed(any(),any(),any(),any(),any(),any());
		assertThatThrownBy(() -> service.run(source.job())).isInstanceOf(DataIntegrityViolationException.class);
		var current = local.job(source.job(),false).orElseThrow();
		Files.delete(ROOT.resolve(source.job()+"-"+current.token()+".mp3.json"));
		reset(local);
		service.run(source.job());
		assertThat(jobs.detail(source.job().toString()).failureCode()).isEqualTo("LOCAL_RESULT_INCOMPLETE");
		assertThat(encoder.encodes.get()).isEqualTo(1);
		assertThat(Files.readAllBytes(ROOT.resolve(current.file()+".mp3"))).isEqualTo(MP3);
		assertThat(count("audio_revision")).isZero();
	}

	@Test
	void sourceChecksumIsCheckedEvenWhenTheFileSizeStillMatches() throws Exception {
		Path path = ROOT.resolve(source.wav()+".wav");
		byte[] changed = Files.readAllBytes(path);
		changed[changed.length-1] ^= 1;
		Files.write(path,changed);
		service.run(source.job());
		assertThat(jobs.detail(source.job().toString()).failureCode()).isEqualTo("LOCAL_SOURCE_INVALID");
		assertThat(encoder.encodes.get()).isZero();
	}

	@Test
	void checkFinalizesVerifiedMp3WithItsReceiptAndCanReplayAfterSuccess() {
		failFinalization();
		String key = key();
		var result = recovery.check(account,key,source.job().toString());
		assertThat(result.result()).containsEntry("phase","SUCCEEDED").containsEntry("inspection","LOCAL_MP3_VERIFIED");
		assertThat(recovery.check(account,key,source.job().toString()).replayed()).isTrue();
		assertThat(encoder.encodes.get()).isEqualTo(1);
		assertThat(count("audio_revision")).isEqualTo(1);
	}

	@Test
	void checkReceiptFailureRollsBackTheActualAudioInsertAndSuccessState() {
		failFinalization();
		doAnswer(call -> {
			assertThat(count("audio_revision")).isEqualTo(1);
			assertThat(sql.queryForObject("SELECT phase FROM tts_job WHERE id=?",String.class,bytes(source.job()))).isEqualTo("SUCCEEDED");
			throw new DataIntegrityViolationException("CHECK_RECEIPT_FIXTURE");
		}).when(receipts).saveAndFlush(any());
		assertThatThrownBy(() -> recovery.check(account,key(),source.job().toString())).isInstanceOf(DataIntegrityViolationException.class);
		assertThat(count("audio_revision")).isZero();
		assertThat(jobs.detail(source.job().toString()).phase()).isEqualTo("CONVERTING");
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void invalidCommandWindowCannotRegisterAnAudioEvenWithValidLocalEvidence() {
		failFinalization();
		assertThatThrownBy(() -> recovery.check(account,UUID.randomUUID()+":"+UUID.randomUUID(),source.job().toString())).isInstanceOf(AdminCommandException.class);
		assertThat(count("audio_revision")).isZero();
		assertThat(jobs.detail(source.job().toString()).phase()).isEqualTo("CONVERTING");
	}

	@Test
	void checkWithoutAManifestCannotEncodeOrInventAFailure() throws Exception {
		failFinalization();
		var row = local.job(source.job(),false).orElseThrow();
		Files.delete(ROOT.resolve(source.job()+"-"+row.token()+".mp3.json"));
		assertThat(recovery.check(account,key(),source.job().toString()).result()).containsEntry("phase","CONVERTING").containsEntry("inspection","NO_LOCAL_EVIDENCE");
		assertThat(encoder.encodes.get()).isEqualTo(1);
		assertThat(count("audio_revision")).isZero();
	}

	@Test
	void localRetryReceiptFailureRestoresTheFailedJobAndDoesNotLeaveAnActivePointer() {
		encoder.failure = new IOException("LOCAL_CONVERSION_FAILED");
		service.run(source.job());
		var before = jobs.detail(source.job().toString());
		doThrow(new DataIntegrityViolationException("LOCAL_RETRY_RECEIPT_FIXTURE")).when(receipts).saveAndFlush(any());
		assertThatThrownBy(() -> recovery.retry(account,key(),source.job().toString(),new RetryTtsRequest(before.phaseVersion()))).isInstanceOf(DataIntegrityViolationException.class);
		assertThat(jobs.detail(source.job().toString()).phase()).isEqualTo("FAILED");
		assertThat(jobs.detail(source.job().toString()).phaseVersion()).isEqualTo(before.phaseVersion());
		assertThat(sql.queryForObject("SELECT active_tts_job_id FROM text_revision",byte[].class)).isNull();
	}

	@Test
	void positiveEvidenceOfAStoppedConversionOwnerAllowsLocalRecovery() {
		var actual = owners.identity();
		var old = new LifecycleRepository.Instance(UUID.randomUUID(),actual.node(),actual.root(),Long.MAX_VALUE,clock.instant(),"RUNNING");
		lifecycle.register(old,clock.instant());
		UUID task = UUID.randomUUID();
		lifecycle.beginTask(task,old.id(),"TTS",source.job(),clock.instant());
		local.claim(source.job(),old.id(),task,UUID.randomUUID(),UUID.randomUUID(),clock.instant());
		assertThat(cleanup.reconcile(old.id())).isTrue();
		assertThat(service.run(source.job())).isTrue();
		assertThat(jobs.detail(source.job().toString()).state()).isEqualTo("SUCCEEDED");
		assertThat(count("tts_job")).isEqualTo(1);
	}

	@Test
	void discardedLocalFailureDoesNotOfferCheckOrRetry() throws Exception {
		Files.write(ROOT.resolve(source.wav()+".wav"),new byte[]{1,2,3});
		service.run(source.job());
		assertThat(jobs.detail(source.job().toString()).retryAction()).isEqualTo("INSPECT_ONLY");
		deletion.delete(account,key(),source.place().toString(),"\"0\"");
		cleanup.jobs(100);
		assertThat(jobs.detail(source.job().toString()).state()).isEqualTo("DISCARDED");
		assertThat(jobs.detail(source.job().toString()).retryAction()).isNull();
	}

	private void failFinalization() {
		doThrow(new DataIntegrityViolationException("LOCAL_FINALIZE_FIXTURE")).when(local).succeed(any(),any(),any(),any(),any(),any());
		assertThatThrownBy(() -> service.run(source.job())).isInstanceOf(DataIntegrityViolationException.class);
		reset(local);
	}

	private Source seed(String language) throws IOException {
		Source result = new AudioDataFixture(sql).insertStagedWav(language);
		byte[] wav = WavFixture.oneSecond();
		Files.write(ROOT.resolve(result.wav()+".wav"),wav);
		sql.update("UPDATE file_asset SET checksum=?, duration=1 WHERE id=?",java.util.HexFormat.of().parseHex(WavPolicy.hash(wav)),bytes(result.wav()));
		return result;
	}

	private String key() {
		return commands.issue(account).windowId()+":"+UUID.randomUUID();
	}

	private long count(String table) {
		return sql.queryForObject("SELECT COUNT(*) FROM "+table,Long.class);
	}

	private static Path temporary() {
		try {
			return Files.createTempDirectory("hueqr-local-audio-files-");
		}
		catch (IOException error) {
			throw new java.io.UncheckedIOException(error);
		}
	}

	static class FakeEncoder implements Mp3Encoder {
		final AtomicInteger encodes = new AtomicInteger();
		IOException failure;
		Runnable afterWrite = () -> {};
		byte[] bytes = MP3;

		@Override
		public void encode(Path wav, OutputStream output) throws IOException {
			encodes.incrementAndGet();
			if (failure != null)
				throw failure;
			output.write(bytes);
			afterWrite.run();
		}

		@Override
		public BigDecimal inspect(Path mp3) {
			return new BigDecimal("1.25");
		}
	}

	@TestConfiguration(proxyBeanMethods=false)
	static class Configuration {
		@Bean
		FakeEncoder fakeEncoder() {
			return new FakeEncoder();
		}

		@Bean
		Mp3ResultStore mp3ResultStore(FakeEncoder encoder, WorkspaceCleanupService workspace, ObjectMapper json) throws IOException {
			return new Mp3ResultStore(ROOT,encoder,workspace,json);
		}
	}
}
