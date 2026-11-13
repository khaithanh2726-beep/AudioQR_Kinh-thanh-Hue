package vn.hueqr.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.repository.LifecycleRepository;
import vn.hueqr.backend.repository.LifecycleRepository.Instance;
import vn.hueqr.backend.repository.LifecycleRepository.Workspace;
import vn.hueqr.backend.support.AudioDataFixture;
import vn.hueqr.backend.support.AudioDataFixture.Source;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;
import vn.hueqr.backend.support.WavFixture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static vn.hueqr.backend.repository.LifecycleRepository.bytes;
import static vn.hueqr.backend.repository.LifecycleRepository.time;

@SpringBootTest(properties = "hueqr.lifecycle-tests=true")
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class LifecycleCleanupTests {
	private static final Instant NOW = AudioDataFixture.NOW;
	private static final Path ROOT = temporaryRoot();

	@Autowired
	JdbcTemplate sql;

	@Autowired
	MutableClock clock;

	@Autowired
	LifecycleRepository repository;

	@Autowired
	RuntimeOwnerService owners;

	@Autowired
	MetadataCleanupService metadata;

	@Autowired
	FileCleanupService files;

	@Autowired
	WorkspaceCleanupService workspace;

	@Autowired
	AdminCommandService commands;

	@Autowired
	AdminPlaceDeletionService deletion;

	@Autowired
	TtsDispatchService dispatch;

	@Autowired
	TtsResultService results;

	@Autowired
	ObjectMapper json;

	@Autowired
	ApplicationContext context;

	@Autowired
	PlatformTransactionManager manager;

	@MockitoSpyBean
	CleanupFileStore store;

	private AudioDataFixture fixture;

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("HUEQR_AUDIO_ROOT", ROOT::toString);
	}

	private static Path temporaryRoot() {
		try {
			return Files.createTempDirectory("hueqr-lifecycle-test-");
		}
		catch (IOException error) {
			throw new java.io.UncheckedIOException(error);
		}
	}

	@BeforeEach
	void setUp() {
		clearData();
		clock.set(NOW);
		fixture = new AudioDataFixture(sql);
	}

	@AfterEach
	void clearData() {
		reset(store);
		sql.update("UPDATE guest_session SET current_opening_id = NULL");
		for (String table : new String[]{"file_claim", "guest_request_result", "guest_content_grant", "guest_open_intent", "place_view", "guest_session", "operation_receipt", "admin_command_window"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE content_slot SET active_pair_id = NULL, draft_text_id = NULL");
		for (String table : new String[]{"content_pair", "audio_review_ref", "audio_revision", "text_review_ref"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE text_revision SET active_tts_job_id = NULL");
		for (String table : new String[]{"workspace_file", "runtime_task", "tts_job", "text_revision", "content_slot"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE place SET active_region_revision_id = NULL");
		for (String table : new String[]{"region_revision", "place", "file_asset", "admin_account"})
			sql.update("DELETE FROM " + table);
	}

	@Test
	void schedulerIsAbsentUntilExplicitlyEnabled() {
		assertThat(context.getBeansOfType(LifecycleCleanupRunner.class)).isEmpty();
		assertThatThrownBy(() -> files.sweep(0)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> metadata.jobs(0)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> workspace.sweep(0)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void windowAndReceiptSurviveUntilExactlyTwentyFourHoursThenOldKeyCannotRunAgain() {
		UUID account = account();
		var window = commands.issue(account);
		String key = window.windowId() + ":" + UUID.randomUUID();
		var command = new AdminCommandService.Command("POST", "/fixture", "{}", null, "FIXTURE", 200, "OK");
		commands.execute(account, key, command, () -> java.util.Map.of("done", true));
		clock.set(NOW.plus(Duration.ofHours(24)).minusNanos(1000));
		assertThat(metadata.windows(10)).isZero();
		assertThat(count("operation_receipt")).isEqualTo(1);
		clock.set(NOW.plus(Duration.ofHours(24)));
		assertThat(metadata.windows(10)).isEqualTo(1);
		assertThat(count("operation_receipt")).isZero();
		assertThat(count("admin_command_window")).isZero();
		assertThatThrownBy(() -> commands.execute(account, key, command, () -> {
			throw new AssertionError("OLD_KEY_MUST_NOT_EXECUTE");
		})).isInstanceOf(AdminCommandException.class);
	}

	@Test
	void cleanupWaitsForAnInflightCommandBeforeDeletingItsWindowAndReceipt() throws Exception {
		UUID account = account();
		var window = commands.issue(account);
		var entered = new java.util.concurrent.CountDownLatch(1);
		var release = new java.util.concurrent.CountDownLatch(1);
		try (var pool = java.util.concurrent.Executors.newFixedThreadPool(2)) {
			var command = pool.submit(() -> commands.execute(account, window.windowId() + ":" + UUID.randomUUID(),
					new AdminCommandService.Command("POST", "/fixture", "{}", null, "FIXTURE", 200, "OK"), () -> {
						entered.countDown();
						try {
							if (!release.await(15, TimeUnit.SECONDS))
								throw new IllegalStateException("FIXTURE_TIMEOUT");
						}
						catch (InterruptedException error) {
							Thread.currentThread().interrupt();
							throw new IllegalStateException(error);
						}
						return java.util.Map.of("done", true);
					}));
			try {
				assertThat(entered.await(15, TimeUnit.SECONDS)).isTrue();
				clock.set(NOW.plus(Duration.ofHours(24)));
				var cleaner = pool.submit(() -> metadata.windows(10));
				assertThatThrownBy(() -> cleaner.get(200, TimeUnit.MILLISECONDS)).isInstanceOf(java.util.concurrent.TimeoutException.class);
				release.countDown();
				assertThat(command.get(15, TimeUnit.SECONDS).resultCode()).isEqualTo("OK");
				assertThat(cleaner.get(15, TimeUnit.SECONDS)).isEqualTo(1);
				assertThat(count("operation_receipt")).isZero();
			}
			finally {
				release.countDown();
			}
		}
	}

	@Test
	void sevenDayClockStartsAtTerminalTimeAndDeletesOnlyAtTheBoundary() {
		Source source = terminal();
		clock.set(NOW.plus(Duration.ofDays(7)).minusNanos(1000));
		assertThat(metadata.jobs(10)).isZero();
		clock.set(NOW.plus(Duration.ofDays(7)));
		assertThat(metadata.jobs(10)).isEqualTo(1);
		assertThat(repository.job(source.job(), false)).isEmpty();
		assertThat(count("text_revision")).isEqualTo(1);
	}

	@Test
	void aSubMicrosecondCutoffIsNotRoundedUpToDeleteAnOldJobEarly() {
		terminal();
		clock.set(NOW.plus(Duration.ofDays(7)).minusNanos(1));
		assertThat(metadata.jobs(10)).isZero();
		assertThat(count("tts_job")).isEqualTo(1);
		clock.set(NOW.plus(Duration.ofDays(7)));
		assertThat(metadata.jobs(10)).isEqualTo(1);
	}

	@Test
	void latestJobAndRetryPredecessorCannotBePurged() {
		Source previous = terminal();
		Source next = terminal();
		sql.update("UPDATE text_revision SET latest_tts_job_key = ? WHERE id = ?", bytes(next.job()), bytes(next.text()));
		sql.update("UPDATE tts_job SET retry_of_job_id = ? WHERE id = ?", bytes(previous.job()), bytes(next.job()));
		clock.set(NOW.plus(Duration.ofDays(10)));
		assertThat(metadata.jobs(10)).isZero();
		assertThat(count("tts_job")).isEqualTo(2);
	}

	@Test
	void activeTaskAndWorkspaceKeepOldTerminalMetadata() {
		Source source = terminal();
		clock.set(NOW.plus(Duration.ofDays(10)));
		try (var task = owners.begin("RECOVER", source.job())) {
			assertThat(metadata.jobs(10)).isZero();
		}
		tracked(source, UUID.randomUUID() + ".manifest-part", "TEMP", null);
		assertThat(metadata.jobs(10)).isZero();
	}

	@Test
	void uncertainJobsAreNeverPurgedOrRetriedByAge() {
		Source source = queued();
		sql.update("UPDATE tts_job SET phase = 'UNCERTAIN', worker_instance = ?, worker_token = ?, dispatched_at = ? WHERE id = ?",
				bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), time(NOW), bytes(source.job()));
		clock.set(NOW.plus(Duration.ofDays(100)));
		assertThat(metadata.jobs(10)).isZero();
		assertThat(repository.job(source.job(), false).orElseThrow().phase()).isEqualTo("UNCERTAIN");
	}

	@Test
	void deletingAQueuedTargetDiscardsItWithoutInventingDispatchEvidence() {
		Source source = queued();
		UUID account = account();
		deletion.delete(account, key(account), source.place().toString(), "\"0\"");
		var job = repository.job(source.job(), false).orElseThrow();
		assertThat(job.phase()).isEqualTo("DISCARDED");
		assertThat(job.instance()).isNull();
		assertThat(job.terminalAt()).isEqualTo(NOW);
		assertThat(count("place")).isZero();
	}

	@Test
	void stagedDeletedTargetWaitsForPositiveLocalCompletionBeforeDroppingItsWavReference() {
		Source source = fixture.insertStagedWav("vi");
		UUID account = account();
		deletion.delete(account, key(account), source.place().toString(), "\"0\"");
		metadata.jobs(10);
		assertThat(repository.job(source.job(), false).orElseThrow().phase()).isEqualTo("RESULT_STAGED");
		sql.update("UPDATE tts_job SET local_finished_at = ? WHERE id = ?", time(NOW), bytes(source.job()));
		metadata.jobs(10);
		assertThat(repository.job(source.job(), false).orElseThrow().phase()).isEqualTo("DISCARDED");
		clock.set(NOW.plusSeconds(60));
		metadata.jobs(10);
		assertThat(repository.job(source.job(), false).orElseThrow().terminalAt()).isEqualTo(NOW);
	}

	@Test
	void liveOwnerKeepsItsClaimEvenAfterOneHundredDaysAndDeadChildReleasesIt() throws Exception {
		UUID file = asset(false, null);
		String executable = Path.of(System.getProperty("java.home"), "bin", System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString();
		var child = new ProcessBuilder(executable, "-cp", System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
				Child.class.getName()).redirectErrorStream(true).start();
		try {
			try (var pool = java.util.concurrent.Executors.newSingleThreadExecutor()) {
				var ready = pool.submit(() -> child.inputReader().readLine());
				try {
					assertThat(ready.get(15, TimeUnit.SECONDS)).isEqualTo("READY");
				}
				finally {
					if (!ready.isDone()) {
						child.destroyForcibly();
						ready.cancel(true);
					}
				}
			}
			var local = owners.identity();
			UUID id = UUID.randomUUID();
			var owner = new Instance(id, local.node(), local.root(), child.pid(), child.info().startInstant().orElseThrow()
					.truncatedTo(java.time.temporal.ChronoUnit.MICROS), "RUNNING");
			repository.register(owner, NOW);
			repository.beginTask(UUID.randomUUID(), id, "READ", UUID.randomUUID(), NOW);
			sql.update("INSERT INTO file_claim (id, file_id, owner_key, kind, created_at, owner_instance) VALUES (?, ?, ?, 'READ', ?, ?)",
					bytes(UUID.randomUUID()), bytes(file), bytes(UUID.randomUUID()), time(NOW), bytes(id));
			clock.set(NOW.plus(Duration.ofDays(100)));
			assertThat(metadata.reconcile(id)).isFalse();
			assertThat(count("file_claim")).isEqualTo(1);
			child.destroyForcibly();
			assertThat(child.waitFor(15, TimeUnit.SECONDS)).isTrue();
			assertThat(metadata.reconcile(id)).isTrue();
			assertThat(count("file_claim")).isZero();
			assertThat(count("runtime_task")).isZero();
		}
		finally {
			if (child.isAlive())
				child.destroyForcibly();
		}
	}

	public static class Child {
		public static void main(String[] arguments) throws Exception {
			System.out.println("READY");
			System.out.flush();
			System.in.read();
		}
	}

	@Test
	void anotherNodeAndLegacyClaimsArePreservedWhenOwnerCannotBeProvedStopped() {
		var local = owners.identity();
		UUID id = UUID.randomUUID();
		repository.register(new Instance(id, "f".repeat(64), local.root(), local.pid(), NOW, "RUNNING"), NOW);
		UUID file = asset(false, null);
		sql.update("INSERT INTO file_claim (id, file_id, owner_key, kind, created_at) VALUES (?, ?, ?, 'READ', ?)",
				bytes(UUID.randomUUID()), bytes(file), bytes(UUID.randomUUID()), time(NOW));
		assertThat(metadata.reconcile(id)).isFalse();
		clock.set(NOW.plus(Duration.ofDays(100)));
		assertThat(files.clean(file)).isFalse();
		assertThat(count("file_claim")).isEqualTo(1);
	}

	@Test
	void gracefulStopIsRecordedOnlyAfterActualWorkCloses() {
		var owner = new RuntimeOwnerService(repository, clock, manager, ROOT.toString());
		var task = owner.begin("READ", UUID.randomUUID());
		UUID id = task.instanceId();
		owner.close();
		assertThat(repository.instance(id, false).orElseThrow().state()).isEqualTo("RUNNING");
		task.close();
		assertThat(repository.instance(id, false).orElseThrow().state()).isEqualTo("STOPPED");
		assertThatThrownBy(() -> owner.begin("READ", UUID.randomUUID())).hasMessage("RUNTIME_INSTANCE_CLOSING");
	}

	@Test
	void stoppedDispatcherBecomesUncertainWithoutSendingProviderAgain() {
		Source source = queued();
		var local = owners.identity();
		UUID old = UUID.randomUUID();
		// PID giống nhưng thời điểm khởi động khác chứng minh đây không còn là tiến trình cũ
		repository.register(new Instance(old, local.node(), local.root(), local.pid(), local.processStartedAt().minusSeconds(1), "RUNNING"), NOW);
		dispatch.claim(source.job(), old).orElseThrow();
		assertThat(metadata.reconcile(old)).isTrue();
		assertThat(repository.job(source.job(), false).orElseThrow().phase()).isEqualTo("UNCERTAIN");
		assertThat(dispatch.claim(source.job(), local.id())).isEmpty();
	}

	@Test
	void eightHourProtectionIsMeasuredFromLastUnapplyAndDeletesActualBytesAtBoundary() throws Exception {
		UUID id = asset(true, NOW);
		Path path = ROOT.resolve(id + ".wav");
		Files.write(path, new byte[]{1, 2});
		clock.set(NOW.plus(Duration.ofHours(8)).minusNanos(1000));
		assertThat(files.clean(id)).isFalse();
		assertThat(Files.exists(path)).isTrue();
		clock.set(NOW.plus(Duration.ofHours(8)));
		assertThat(files.clean(id)).isTrue();
		assertThat(Files.exists(path)).isFalse();
		assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("DELETED");
		assertThat(files.clean(id)).isFalse();
	}

	@Test
	void unknownUnapplyMarkerCannotBeReplacedByFileCreationTime() {
		UUID id = asset(true, null);
		clock.set(NOW.plus(Duration.ofDays(100)));
		assertThat(files.clean(id)).isFalse();
		assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("READY");
	}

	@Test
	void neverAppliedFileWaitsTwentyFourHoursAfterFirstSafeObservation() throws Exception {
		UUID id = asset(false, null);
		Path path = ROOT.resolve(id + ".wav");
		Files.write(path, new byte[]{1});
		clock.set(NOW.plus(Duration.ofDays(100)));
		assertThat(files.clean(id)).isFalse();
		assertThat(repository.asset(id, false).orElseThrow().orphanSince()).isEqualTo(clock.instant());
		clock.set(clock.instant().plus(Duration.ofHours(24)).minusNanos(1000));
		assertThat(files.clean(id)).isFalse();
		clock.set(clock.instant().plusNanos(1000));
		assertThat(files.clean(id)).isTrue();
		assertThat(Files.exists(path)).isFalse();
	}

	@Test
	void aReadClaimResetsOrphanClockAndProtectsActualFile() throws Exception {
		UUID id = asset(false, null);
		Files.write(ROOT.resolve(id + ".wav"), new byte[]{1});
		assertThat(files.clean(id)).isFalse();
		sql.update("INSERT INTO file_claim (id, file_id, owner_key, kind, created_at) VALUES (?, ?, ?, 'READ', ?)", bytes(UUID.randomUUID()), bytes(id), bytes(UUID.randomUUID()), time(NOW));
		clock.set(NOW.plus(Duration.ofDays(3)));
		assertThat(files.clean(id)).isFalse();
		assertThat(repository.asset(id, false).orElseThrow().orphanSince()).isNull();
		sql.update("DELETE FROM file_claim");
		assertThat(files.clean(id)).isFalse();
		assertThat(repository.asset(id, false).orElseThrow().orphanSince()).isEqualTo(clock.instant());
	}

	@Test
	void jobSourceAndAudioReferencesAreNeverUnlinked() {
		Source source = fixture.insertSource("vi");
		fixture.insertAudio(source);
		sql.update("UPDATE file_asset SET ever_applied = TRUE, last_unapplied_at = ?", time(NOW.minus(Duration.ofDays(10))));
		assertThat(files.clean(source.wav())).isFalse();
		assertThat(files.clean(source.mp3())).isFalse();
		assertThat(files.sweep(10)).isZero();
	}

	@Test
	void unlinkFailureRemainsPendingAndCanResumeAfterCrashFollowingUnlink() throws Exception {
		UUID id = asset(true, NOW.minus(Duration.ofHours(8)));
		Path path = ROOT.resolve(id + ".wav");
		Files.write(path, new byte[]{1});
		doThrow(new java.nio.file.AccessDeniedException(path.toString())).when(store).remove(id + ".wav");
		assertThat(files.clean(id)).isFalse();
		assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("DELETING");
		assertThat(Files.exists(path)).isTrue();
		reset(store);
		Files.delete(path);
		assertThat(files.clean(id)).isTrue();
		assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("DELETED");
	}

	@Test
	void anUnfinishedDeletionTaskBlocksRetryUntilItsOwnerIsKnownStopped() throws Exception {
		UUID id = asset(true, NOW.minus(Duration.ofHours(8)));
		try (var task = owners.begin("DELETE", id)) {
			repository.deleting(id, UUID.randomUUID(), task.instanceId(), task.id(), NOW);
			assertThat(files.clean(id)).isFalse();
		}
		assertThat(files.clean(id)).isTrue();
	}

	@Test
	void secondCleanerCannotUnlinkWhileTheFirstCleanerIsOutsideItsDatabaseTransaction() throws Exception {
		UUID id = asset(true, NOW.minus(Duration.ofHours(8)));
		Files.write(ROOT.resolve(id + ".wav"), new byte[]{1});
		var entered = new java.util.concurrent.CountDownLatch(1);
		var release = new java.util.concurrent.CountDownLatch(1);
		doAnswer(call -> {
			entered.countDown();
			if (!release.await(15, TimeUnit.SECONDS))
				throw new IOException("FIXTURE_TIMEOUT");
			return call.callRealMethod();
		}).when(store).remove(id + ".wav");
		try (var pool = java.util.concurrent.Executors.newSingleThreadExecutor()) {
			var first = pool.submit(() -> files.clean(id));
			try {
				assertThat(entered.await(15, TimeUnit.SECONDS)).isTrue();
				assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("DELETING");
				assertThat(files.clean(id)).isFalse();
				release.countDown();
				assertThat(first.get(15, TimeUnit.SECONDS)).isTrue();
				verify(store, times(1)).remove(id + ".wav");
			}
			finally {
				release.countDown();
			}
		}
	}

	@Test
	void malformedStorageKeyCannotDeleteAnOutsideFile() throws Exception {
		Path outside = Files.createTempFile("hueqr-outside-", ".wav");
		try {
			UUID id = asset(true, NOW.minus(Duration.ofHours(8)));
			sql.update("UPDATE file_asset SET storage_key = ? WHERE id = ?", "../" + outside.getFileName(), bytes(id));
			assertThat(files.clean(id)).isFalse();
			assertThat(Files.exists(outside)).isTrue();
			assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("DELETING");
		}
		finally {
			Files.deleteIfExists(outside);
		}
	}

	@Test
	void aDirectoryAtTheFilePathIsNotTreatedAsAbsentOrDeleted() throws Exception {
		UUID id = asset(true, NOW.minus(Duration.ofHours(8)));
		Path path = ROOT.resolve(id + ".wav");
		Files.createDirectory(path);
		assertThat(files.clean(id)).isFalse();
		assertThat(Files.isDirectory(path)).isTrue();
		assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("DELETING");
		Files.delete(path);
	}

	@Test
	void temporaryFileWaitsOneHourFromConfirmedTerminalAndThenIsPhysicallyRemoved() throws Exception {
		Source source = terminal();
		String key = UUID.randomUUID() + ".manifest-part";
		tracked(source, key, "TEMP", null);
		Files.write(ROOT.resolve(key), new byte[]{1});
		assertThat(workspace.clean(key)).isFalse();
		clock.set(NOW.plus(Duration.ofHours(1)).minusNanos(1000));
		assertThat(workspace.clean(key)).isFalse();
		clock.set(NOW.plus(Duration.ofHours(1)));
		assertThat(workspace.clean(key)).isTrue();
		assertThat(Files.exists(ROOT.resolve(key))).isFalse();
		assertThat(repository.workspace(key, false)).isEmpty();
	}

	@Test
	void activeWriterAndUnknownJobKeepTemporaryFilesEvenWhenTheirMtimeIsOld() throws Exception {
		Source source = terminal();
		String key = UUID.randomUUID() + ".manifest-part";
		tracked(source, key, "TEMP", null);
		Files.write(ROOT.resolve(key), new byte[]{1});
		try (var task = owners.begin("TTS", source.job())) {
			clock.set(NOW.plus(Duration.ofDays(100)));
			assertThat(workspace.clean(key)).isFalse();
			assertThat(repository.workspace(key, false).orElseThrow().eligibleSince()).isNull();
		}
		sql.update("DELETE FROM tts_job WHERE id = ?", bytes(source.job()));
		assertThat(workspace.clean(key)).isFalse();
		assertThat(Files.exists(ROOT.resolve(key))).isTrue();
	}

	@Test
	void temporaryUnlinkFailureKeepsItsRegistryForRetry() throws Exception {
		Source source = terminal();
		String key = UUID.randomUUID() + ".manifest-part";
		tracked(source, key, "TEMP", null);
		Files.write(ROOT.resolve(key), new byte[]{1});
		workspace.clean(key);
		clock.set(NOW.plus(Duration.ofHours(1)));
		doThrow(new IOException("FIXTURE_UNLINK_FAILED")).when(store).remove(key);
		assertThat(workspace.clean(key)).isFalse();
		assertThat(repository.workspace(key, false)).isPresent();
		assertThat(Files.exists(ROOT.resolve(key))).isTrue();
		reset(store);
		assertThat(workspace.clean(key)).isTrue();
		assertThat(repository.workspace(key, false)).isEmpty();
	}

	@Test
	void completeWavAfterWriterCrashIsRegisteredInsteadOfDeletedAsOneHourTemporaryData() throws Exception {
		Source source = terminal();
		UUID id = UUID.randomUUID();
		String key = id + ".wav";
		tracked(source, key, "WAV", id);
		Files.write(ROOT.resolve(key), WavFixture.oneSecond());
		assertThat(workspace.clean(key)).isFalse();
		clock.set(NOW.plus(Duration.ofHours(1)));
		assertThat(workspace.clean(key)).isTrue();
		assertThat(Files.readAllBytes(ROOT.resolve(key))).isEqualTo(WavFixture.oneSecond());
		assertThat(repository.asset(id, false).orElseThrow().state()).isEqualTo("READY");
		assertThat(files.clean(id)).isFalse();
		clock.set(NOW.plus(Duration.ofHours(25)));
		assertThat(files.clean(id)).isTrue();
	}

	@Test
	void partialWavCanBeRemovedAfterOneHourButUntrackedFilesAreUntouched() throws Exception {
		Source source = terminal();
		UUID id = UUID.randomUUID();
		String key = id + ".wav";
		tracked(source, key, "WAV", id);
		Files.write(ROOT.resolve(key), new byte[]{1, 2, 3});
		Path unknown = ROOT.resolve(UUID.randomUUID() + ".wav");
		Files.write(unknown, new byte[]{4});
		workspace.sweep(100);
		clock.set(NOW.plus(Duration.ofHours(1)));
		assertThat(workspace.clean(key)).isTrue();
		assertThat(Files.exists(ROOT.resolve(key))).isFalse();
		assertThat(Files.exists(unknown)).isTrue();
		assertThat(repository.asset(id, false)).isEmpty();
	}

	@Test
	void lateTtsResultAfterDeletionKeepsKnownOrphanBytesWithoutRecreatingThePlace() throws Exception {
		Source source = queued();
		UUID account = account();
		var trackedStore = new WavResultStore(ROOT, json, workspace);
		var worker = new TtsWorker(owners.identity().id(), dispatch, lease -> {
			deletion.delete(account, key(account), source.place().toString(), "\"0\"");
			return WavFixture.oneSecond();
		}, trackedStore, results, owners);
		assertThat(worker.run(source.job())).isTrue();
		assertThat(count("place")).isZero();
		assertThat(repository.job(source.job(), false).orElseThrow().phase()).isEqualTo("DISCARDED");
		assertThat(count("file_asset")).isEqualTo(2);
		assertThat(count("workspace_file")).isEqualTo(3);
		assertThat(count("runtime_task")).isZero();
		assertThat(sql.queryForObject("SELECT source_wav_file_id FROM tts_job", byte[].class)).isNull();
		assertThat(repository.job(source.job(), false).orElseThrow().localFinishedAt()).isEqualTo(NOW);
	}

	@Test
	void stagedWavRemainsReferencedWhenFinishedWriterManifestAndTemporaryRegistryAreCleaned() throws Exception {
		Source source = queued();
		var trackedStore = new WavResultStore(ROOT, json, workspace);
		var worker = new TtsWorker(owners.identity().id(), dispatch, lease -> WavFixture.oneSecond(), trackedStore, results, owners);
		assertThat(worker.run(source.job())).isTrue();
		assertThat(repository.job(source.job(), false).orElseThrow().phase()).isEqualTo("RESULT_STAGED");
		assertThat(count("workspace_file")).isEqualTo(3);
		assertThat(workspace.sweep(10)).isZero();
		clock.set(NOW.plus(Duration.ofHours(1)));
		assertThat(workspace.sweep(10)).isEqualTo(3);
		assertThat(count("workspace_file")).isZero();
		UUID file = sql.queryForObject("SELECT source_wav_file_id FROM tts_job", (row, n) -> LifecycleRepository.uuid(row, "source_wav_file_id"));
		assertThat(Files.readAllBytes(ROOT.resolve(file + ".wav"))).isEqualTo(WavFixture.oneSecond());
		clock.set(NOW.plus(Duration.ofDays(100)));
		assertThat(files.clean(file)).isFalse();
		assertThat(Files.exists(ROOT.resolve(file + ".wav"))).isTrue();
	}

	private UUID account() {
		UUID id = UUID.randomUUID();
		sql.update("INSERT INTO admin_account (id, login_name, password_hash, enabled) VALUES (?, ?, 'Fixture', TRUE)", bytes(id), id.toString());
		return id;
	}

	private String key(UUID account) {
		return commands.issue(account).windowId() + ":" + UUID.randomUUID();
	}

	private Source queued() {
		Source source = fixture.insertStagedWav("vi");
		sql.update("UPDATE tts_job SET phase = 'QUEUED', source_wav_file_id = NULL, result_staged_at = NULL, worker_instance = NULL, worker_token = NULL, dispatched_at = NULL WHERE id = ?", bytes(source.job()));
		byte[] hash = java.util.HexFormat.of().parseHex(WavPolicy.hash("Bản đã lưu".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		sql.update("UPDATE text_revision SET content_hash = ? WHERE id = ?", hash, bytes(source.text()));
		sql.update("UPDATE tts_job SET text_hash_snapshot = ? WHERE id = ?", hash, bytes(source.job()));
		return source;
	}

	private Source terminal() {
		Source source = fixture.insertStagedWav("vi");
		sql.update("UPDATE text_revision SET active_tts_job_id = NULL, latest_tts_job_key = NULL WHERE id = ?", bytes(source.text()));
		sql.update("UPDATE tts_job SET phase = 'FAILED', source_wav_file_id = NULL, result_staged_at = NULL, failure_code = 'PROVIDER_REJECTED', terminal_at = ? WHERE id = ?", time(NOW), bytes(source.job()));
		return source;
	}

	private UUID asset(boolean applied, Instant unapplied) {
		UUID id = UUID.randomUUID();
		fixture.insertFile(id, "WAV");
		sql.update("UPDATE file_asset SET ever_applied = ?, last_unapplied_at = ? WHERE id = ?", applied, unapplied == null ? null : time(unapplied), bytes(id));
		return id;
	}

	private void tracked(Source source, String key, String kind, UUID file) {
		repository.trackWorkspace(new Workspace(key, source.job(), UUID.randomUUID(), owners.identity().id(), file, kind, null), NOW);
	}

	private long count(String table) {
		return sql.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
	}
}
