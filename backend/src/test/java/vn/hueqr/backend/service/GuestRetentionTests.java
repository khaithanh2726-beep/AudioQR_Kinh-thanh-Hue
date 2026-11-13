package vn.hueqr.backend.service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.dto.StartRequest;
import vn.hueqr.backend.repository.GuestContentRepository;
import vn.hueqr.backend.repository.GuestRetentionRepository;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static vn.hueqr.backend.service.GuestRetentionService.State.*;
import static vn.hueqr.backend.support.AudioDataFixture.bytes;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class GuestRetentionTests {
	private static final Instant START = GuestSessionTestClock.BASE;
	private static final Instant EXPIRY = START.plus(Duration.ofHours(8));
	private static final Duration RETENTION = Duration.ofDays(30);
	@Autowired
	JdbcTemplate sql;

	@Autowired
	GuestRetentionService retention;

	@Autowired
	OperationsGateService operations;

	@Autowired
	GuestContentRepository content;

	@Autowired
	MutableClock clock;

	@Autowired
	StartTicketService tickets;

	@Autowired
	GuestSessionService guests;

	@Autowired
	AdminStatisticsService statistics;

	@Autowired
	ApplicationContext context;

	@MockitoSpyBean
	GuestRetentionRepository repository;

	@Autowired
	GuestSessionRepository sessionRepository;

	@Autowired
	PlatformTransactionManager manager;

	private UUID file;

	@BeforeEach
	void setUp() {
		clearData();
		clock.set(START);
		file = UUID.randomUUID();
		sql.update("INSERT INTO file_asset (id, storage_key, format, checksum, byte_size, duration, state, ready_at) VALUES (?, ?, 'MP3', ?, 10, 1, 'READY', ?)",
				bytes(file), file + ".mp3", new byte[32], time(START));
	}

	@AfterEach
	void clearData() {
		sql.update("UPDATE guest_session SET current_opening_id = NULL");
		for (String table : new String[]{"file_claim", "guest_request_result", "guest_content_grant", "guest_open_intent", "place_view", "guest_session", "file_asset"})
			sql.update("DELETE FROM " + table);
	}

	@Test
	void activeSessionAndItsPendingAckAreUntouched() {
		UUID sid = session(null);
		addContext(sid, true);
		clock.set(EXPIRY.minusNanos(1000));
		assertThat(retention.clean(sid).state()).isEqualTo(ACTIVE);
		assertThat(retention.sweep(100).examined()).isZero();
		assertThat(count("guest_content_grant")).isEqualTo(1);
		assertThat(count("guest_request_result")).isEqualTo(1);
		assertThat(count("guest_open_intent")).isEqualTo(1);
	}

	@Test
	void exactExpiryClearsTechnicalContextButKeepsLedgerSnapshotsAndFiles() {
		UUID sid = session(null);
		addContext(sid, true);
		clock.set(EXPIRY);
		assertThat(retention.clean(sid).state()).isEqualTo(CONTEXT_CLEARED);
		assertContextEmpty();
		assertThat(count("guest_session")).isEqualTo(1);
		assertThat(count("place_view")).isEqualTo(1);
		assertThat(count("file_asset")).isEqualTo(1);
		assertThat(sql.queryForObject("SELECT place_name_snapshot FROM place_view", String.class)).isEqualTo("Tên lúc ghi lượt");
		assertThat(sql.queryForObject("SELECT ended_at FROM guest_session", Timestamp.class)).isNull();
		assertThat(sql.queryForObject("SELECT initial_language FROM guest_session", String.class)).isEqualTo("vi");
		assertThat(storedTime("expires_at")).isEqualTo(EXPIRY);
		assertThat(sql.queryForObject("SELECT document_nonce FROM guest_session", byte[].class)).isNull();
		assertThat(sql.queryForObject("SELECT selection_seq FROM guest_session", Long.class)).isZero();
		assertThat(retention.clean(sid).state()).isEqualTo(UNCHANGED);
		assertThat(retention.sweep(100).examined()).isZero();
	}

	@Test
	void aValidEarlyEndClearsContextImmediatelyWithoutChangingTheRetentionAnchor() {
		Instant end = START.plusSeconds(600);
		UUID sid = session(end);
		addContext(sid, true);
		clock.set(end);
		assertThat(retention.clean(sid).state()).isEqualTo(CONTEXT_CLEARED);
		assertThat(count("place_view")).isEqualTo(1);
		assertThat(storedTime("ended_at")).isEqualTo(end);
	}

	@Test
	void theLastMicrosecondBeforeThirtyDaysCannotDeleteTheSessionOrViews() {
		UUID sid = session(null);
		addContext(sid, true);
		clock.set(EXPIRY.plus(RETENTION).minusNanos(1000));
		retention.sweep(100);
		assertThat(count("guest_session")).isEqualTo(1);
		assertThat(count("place_view")).isEqualTo(1);
		clock.set(EXPIRY.plus(RETENTION));
		assertThat(retention.sweep(100).purgedSessions()).isEqualTo(1);
		assertThat(count("guest_session")).isZero();
		assertThat(count("place_view")).isZero();
	}

	@Test
	void expiryPlusThirtyDaysDeletesTheWholeDependencyTreeInOneTransaction() {
		UUID sid = session(null);
		addContext(sid, true);
		clock.set(EXPIRY.plus(RETENTION));
		var result = retention.clean(sid);
		assertThat(result.state()).isEqualTo(PURGED);
		assertThat(result.deletedViews()).isEqualTo(1);
		assertContextEmpty();
		assertThat(count("guest_session")).isZero();
		assertThat(count("place_view")).isZero();
		assertThat(count("file_asset")).isEqualTo(1);
		assertThat(retention.clean(sid).state()).isEqualTo(NOT_FOUND);
	}

	@Test
	void earlyEndPlusThirtyDaysPurgesWithoutWaitingAnotherEightHours() {
		Instant end = START.plusSeconds(600);
		UUID sid = session(end);
		addContext(sid, true);
		clock.set(end.plus(RETENTION));
		assertThat(retention.sweep(100).purgedSessions()).isEqualTo(1);
		assertThat(count("guest_session")).isZero();
		assertThat(count("place_view")).isZero();
	}

	@Test
	void invalidEndBeforeStartDoesNotDeleteEarlyAndInvalidLateEndDoesNotPostponePurge() {
		UUID early = session(START.minusSeconds(1));
		UUID late = session(EXPIRY.plusSeconds(600));
		clock.set(START.plus(RETENTION));
		assertThat(retention.clean(early).state()).isEqualTo(CONTEXT_CLEARED);
		assertThat(count("guest_session")).isEqualTo(2);
		clock.set(EXPIRY.plus(RETENTION));
		assertThat(retention.sweep(100).purgedSessions()).isEqualTo(2);
		assertThat(retention.clean(late).state()).isEqualTo(NOT_FOUND);
	}

	@Test
	void anOpenStreamClaimPreventsDeletionEvenLongAfterExpiryUntilItIsReleased() {
		UUID sid = session(null);
		UUID grant = addContext(sid, true);
		UUID claim = UUID.randomUUID();
		content.claim(claim, file, grant, START);
		clock.set(EXPIRY.plus(Duration.ofDays(100)));
		assertThat(retention.clean(sid).state()).isEqualTo(READ_IN_PROGRESS);
		assertThat(retention.sweep(100).examined()).isZero();
		assertThat(count("guest_content_grant")).isEqualTo(1);
		assertThat(count("place_view")).isEqualTo(1);
		assertThat(count("file_claim")).isEqualTo(1);
		content.release(claim);
		assertThat(retention.clean(sid).state()).isEqualTo(PURGED);
		assertThat(count("file_asset")).isEqualTo(1);
	}

	@Test
	void oneClaimedSessionCannotStarveOtherDueSessionsInASmallBatch() {
		UUID held = session(null);
		UUID grant = addContext(held, false);
		content.claim(UUID.randomUUID(), file, grant, START);
		UUID ready = session(null);
		clock.set(EXPIRY.plus(RETENTION));
		assertThat(retention.sweep(1).purgedSessions()).isEqualTo(1);
		assertThat(retention.clean(ready).state()).isEqualTo(NOT_FOUND);
		assertThat(retention.clean(held).state()).isEqualTo(READ_IN_PROGRESS);
	}

	@Test
	void cleanupWaitsForTheDownloadTransactionAndSeesItsCommittedClaim() throws Exception {
		UUID sid = session(null);
		UUID grant = addContext(sid, true);
		clock.set(EXPIRY.plus(RETENTION));
		var transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		CountDownLatch locked = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		try (var pool = Executors.newFixedThreadPool(2)) {
			// Giữ khóa như đường tải audio, chèn claim chưa commit rồi cho cleaner chạy tranh chấp
			var download = pool.submit(() -> transaction.executeWithoutResult(tx -> {
				repository.lock(sid).orElseThrow();
				content.claim(UUID.randomUUID(), file, grant, START);
				locked.countDown();
				try {
					if (!release.await(20, TimeUnit.SECONDS))
						throw new IllegalStateException("FIXTURE_LOCK_TIMEOUT");
				}
				catch (InterruptedException error) {
					Thread.currentThread().interrupt();
					throw new IllegalStateException(error);
				}
			}));
			try {
				assertThat(locked.await(20, TimeUnit.SECONDS)).isTrue();
				var cleanup = pool.submit(() -> retention.clean(sid));
				assertThatThrownBy(() -> cleanup.get(200, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);
				release.countDown();
				download.get(20, TimeUnit.SECONDS);
				assertThat(cleanup.get(20, TimeUnit.SECONDS).state()).isEqualTo(READ_IN_PROGRESS);
				assertThat(count("place_view")).isEqualTo(1);
			}
			finally {
				release.countDown();
			}
		}
	}

	@Test
	void boundedBatchesEventuallyDrainAllEligibleSessionsWithoutTouchingActiveOnes() {
		for (int n = 0; n < 3; n++)
			session(null);
		clock.set(EXPIRY.plus(RETENTION));
		for (int n = 0; n < 3; n++)
			assertThat(retention.sweep(1).purgedSessions()).isEqualTo(1);
		assertThat(retention.sweep(1).examined()).isZero();
		assertThatThrownBy(() -> retention.sweep(0)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void aDatabaseFailureRollsBackLedgerViewsContextAndFilePinsTogether() {
		UUID sid = session(null);
		addContext(sid, true);
		clock.set(EXPIRY.plus(RETENTION));
		// Gây lỗi sau lệnh DELETE thật cuối cùng để kiểm toàn giao dịch cùng hoàn tác
		doAnswer(invocation -> {
			invocation.callRealMethod();
			throw new DataIntegrityViolationException("RETENTION_FIXTURE_FAILURE");
		}).when(repository).deleteSession(sid);
		assertThatThrownBy(() -> retention.clean(sid)).isInstanceOf(DataAccessException.class);
		assertThat(count("guest_session")).isEqualTo(1);
		assertThat(count("place_view")).isEqualTo(1);
		assertThat(count("guest_content_grant")).isEqualTo(1);
		assertThat(count("guest_request_result")).isEqualTo(1);
		assertThat(count("guest_open_intent")).isEqualTo(1);
		assertThat(sql.queryForObject("SELECT current_opening_id FROM guest_session", byte[].class)).isNotNull();
	}

	@Test
	void concurrentCleanersPurgeOnceAndTheSecondSeesTheSessionIsGone() throws Exception {
		UUID sid = session(null);
		addContext(sid, true);
		clock.set(EXPIRY.plus(RETENTION));
		CyclicBarrier barrier = new CyclicBarrier(2);
		try (var pool = Executors.newFixedThreadPool(2)) {
			var first = pool.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return retention.clean(sid).state();
			});
			var second = pool.submit(() -> {
				barrier.await(10, TimeUnit.SECONDS);
				return retention.clean(sid).state();
			});
			assertThat(Arrays.asList(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS)))
					.containsExactlyInAnyOrder(PURGED, NOT_FOUND);
		}
		assertThat(count("place_view")).isZero();
	}

	@Test
	void purgingTheOriginalStartLedgerCannotMakeAnOldTicketCreateANewSession() {
		String credential = Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32]);
		var ticket = tickets.issue(credential);
		var request = new StartRequest(ticket.startTicket(), "Khách fixture", "vi");
		var session = guests.start(request, ticket.requestId().toString(), credential);
		clock.set(session.expiresAt().plus(RETENTION));
		assertThat(retention.clean(session.sessionId()).state()).isEqualTo(PURGED);
		assertThatThrownBy(() -> guests.start(request, ticket.requestId().toString(), credential))
				.isInstanceOf(GuestSessionException.class).hasMessage("START_TICKET_EXPIRED");
		assertThat(count("guest_session")).isZero();
	}

	@Test
	void jdbcCleanupAndJpaCreatedSessionUseTheSameUtcExpiry() {
		String credential = Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32]);
		var ticket = tickets.issue(credential);
		var session = guests.start(new StartRequest(ticket.startTicket(), "Khách fixture", "vi"), ticket.requestId().toString(), credential);
		clock.set(session.expiresAt().minusNanos(1000));
		assertThat(retention.clean(session.sessionId()).state()).isEqualTo(ACTIVE);
		clock.set(session.expiresAt());
		assertThat(retention.clean(session.sessionId()).state()).isEqualTo(CONTEXT_CLEARED);
		assertThat(sessionRepository.findById(session.sessionId()).orElseThrow().getExpiresAt()).isEqualTo(session.expiresAt());
	}

	@Test
	void statisticsExcludeDueSnapshotsBeforeCleanupAndStayCorrectAfterPhysicalDeletion() {
		UUID sid = session(null);
		addContext(sid, true);
		assertThat(sessionRepository.findById(sid).orElseThrow().getExpiresAt()).isEqualTo(EXPIRY);
		String day = START.atZone(java.time.ZoneId.of("Asia/Saigon")).toLocalDate().toString();
		assertThat(statistics.read(day, day).viewTotal()).isEqualTo(1);
		clock.set(EXPIRY.plus(RETENTION));
		assertThat(statistics.read(day, day).viewTotal()).isZero();
		assertThat(count("place_view")).isEqualTo(1);
		retention.sweep(100);
		assertThat(statistics.read(day, day).sessionTotal()).isZero();
		assertThat(statistics.read(day, day).viewTotal()).isZero();
		assertThat(count("place_view")).isZero();
	}

	@Test
	void theRunnerRequiresExplicitEnablementAndCanBeInvokedWithoutChangingExpiry() {
		assertThat(context.getBeansOfType(GuestRetentionRunner.class)).isEmpty();
		UUID sid = session(null);
		clock.set(EXPIRY.plus(RETENTION));
		new GuestRetentionRunner(retention, 10, operations).tick();
		assertThat(retention.clean(sid).state()).isEqualTo(NOT_FOUND);
		assertThatThrownBy(() -> new GuestRetentionRunner(retention, 0, operations)).isInstanceOf(IllegalArgumentException.class);
	}

	private UUID session(Instant ended) {
		UUID id = UUID.randomUUID();
		sql.update("""
				INSERT INTO guest_session (id, start_request_key, credential_hash, service_epoch, start_fingerprint,
				start_ticket_issued_at, start_ticket_expires_at, start_ticket_digest, display_name, initial_language,
				current_language, started_at, expires_at, ended_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Khách fixture', 'vi', 'vi', ?, ?, ?)
				""", bytes(id), bytes(UUID.randomUUID()), randomHash(), "00000000-0000-0000-0000-000000000001", new byte[32],
				time(START), time(START.plus(Duration.ofHours(24))), new byte[32], time(START), time(EXPIRY), ended == null ? null : time(ended));
		return id;
	}

	private UUID addContext(UUID sid, boolean view) {
		UUID opening = UUID.randomUUID();
		UUID place = UUID.randomUUID();
		UUID nonce = UUID.randomUUID();
		sql.update("INSERT INTO guest_open_intent (id, session_id, place_key_snapshot, place_name_snapshot, current_language, created_at) VALUES (?, ?, ?, 'Điểm fixture', 'vi', ?)",
				bytes(opening), bytes(sid), bytes(place), time(START));
		sql.update("UPDATE guest_session SET current_opening_id = ?, document_nonce = ?, selection_seq = 1 WHERE id = ?", bytes(opening), bytes(nonce), bytes(sid));
		UUID grant = UUID.randomUUID();
		sql.update("""
				INSERT INTO guest_content_grant (id, session_id, opening_id, place_key_snapshot, place_name_snapshot,
				language, text_revision_key, text_hash, pair_key, audio_revision_key, mp3_file_id, source, view_eligible,
				available_languages, issued_at, expires_at) VALUES (?, ?, ?, ?, 'Điểm fixture', 'vi', ?, ?, ?, ?, ?, 'SELECT', TRUE, 'vi', ?, ?)
				""", bytes(grant), bytes(sid), bytes(opening), bytes(place), bytes(UUID.randomUUID()), new byte[32],
				bytes(UUID.randomUUID()), bytes(UUID.randomUUID()), bytes(file), time(START), time(EXPIRY));
		sql.update("""
				INSERT INTO guest_request_result (session_id, request_key, document_nonce, selection_seq, fingerprint,
				action, opening_key, opening_version, grant_key, selection_kind, language, available_languages)
				VALUES (?, ?, ?, 1, ?, 'SELECT', ?, 0, ?, 'NEW_OPENING', 'vi', 'vi')
				""", bytes(sid), bytes(UUID.randomUUID()), bytes(nonce), new byte[32], bytes(opening), bytes(grant));
		if (view)
			sql.update("INSERT INTO place_view (id, session_id, opening_key, place_key_snapshot, place_name_snapshot, language, viewed_at) VALUES (?, ?, ?, ?, 'Tên lúc ghi lượt', 'vi', ?)",
					bytes(UUID.randomUUID()), bytes(sid), bytes(opening), bytes(place), time(START));
		return grant;
	}

	private void assertContextEmpty() {
		assertThat(count("guest_content_grant")).isZero();
		assertThat(count("guest_open_intent")).isZero();
		assertThat(count("guest_request_result")).isZero();
	}

	private long count(String table) {
		return sql.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
	}

	private static byte[] randomHash() {
		byte[] value = new byte[32];
		System.arraycopy(bytes(UUID.randomUUID()), 0, value, 0, 16);
		return value;
	}

	private Instant storedTime(String column) {
		return sql.queryForObject("SELECT " + column + " FROM guest_session", (row, index) -> row.getObject(1, LocalDateTime.class).toInstant(ZoneOffset.UTC));
	}

	private static LocalDateTime time(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}
}
