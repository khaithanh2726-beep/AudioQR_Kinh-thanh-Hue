package vn.hueqr.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.FileReadClaimRepository;
import vn.hueqr.backend.repository.LifecycleRepository;
import vn.hueqr.backend.service.AdminAudioPreviewService;
import vn.hueqr.backend.service.AdminCommandService;
import vn.hueqr.backend.service.AdminPlaceDeletionService;
import vn.hueqr.backend.service.AdminSessionException;
import vn.hueqr.backend.service.AdminSessionService;
import vn.hueqr.backend.service.FileCleanupService;
import vn.hueqr.backend.service.RuntimeOwnerService;
import vn.hueqr.backend.service.WavPolicy;
import vn.hueqr.backend.support.AudioDataFixture;
import vn.hueqr.backend.support.AudioDataFixture.Source;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vn.hueqr.backend.repository.LifecycleRepository.bytes;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminAudioPreviewTests {
	private static final Path ROOT = temporaryRoot();
	// Bytes tổng hợp chỉ kiểm truyền tải và mã Hash, không phải MP3 đã giải mã hoặc nghe duyệt
	private static final byte[] MP3 = "ID3-fixture-preview-one".getBytes(java.nio.charset.StandardCharsets.UTF_8);

	@Autowired
	MockMvc mvc;

	@Autowired
	JdbcTemplate sql;

	@Autowired
	MutableClock clock;

	@Autowired
	ObjectMapper json;

	@Autowired
	AdminAccountRepository accounts;

	@Autowired
	PasswordEncoder passwords;

	@Autowired
	AdminAudioPreviewService preview;

	@Autowired
	AdminSessionService sessions;

	@Autowired
	AdminPlaceDeletionService deletion;

	@Autowired
	AdminCommandService commands;

	@Autowired
	LifecycleRepository lifecycle;

	@Autowired
	RuntimeOwnerService owners;

	@Autowired
	FileCleanupService cleanup;

	@Autowired
	PlatformTransactionManager manager;

	@MockitoSpyBean
	FileReadClaimRepository claims;

	private UUID account;
	private Source source;
	private UUID audio;
	private AudioDataFixture fixture;

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("HUEQR_AUDIO_ROOT", ROOT::toString);
	}

	private static Path temporaryRoot() {
		try {
			return Files.createTempDirectory("hueqr-admin-preview-files-");
		}
		catch (IOException error) {
			throw new java.io.UncheckedIOException(error);
		}
	}

	@BeforeEach
	void setUp() throws IOException {
		clearData();
		clock.set(AudioDataFixture.NOW);
		account = UUID.randomUUID();
		accounts.saveAndFlush(new AdminAccount(account, "preview-owner", passwords.encode("Preview-fixture"), true));
		fixture = new AudioDataFixture(sql);
		source = fixture.insertSource("vi");
		audio = fixture.insertAudio(source);
		write(source, MP3);
	}

	@AfterEach
	void clearData() {
		SecurityContextHolder.clearContext();
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
	void anonymousOrGuestCredentialCannotUseAdminPreview() throws Exception {
		mvc.perform(get(path(audio))).andExpect(status().isUnauthorized());
		mvc.perform(get(path(audio)).header("X-Guest-Credential", "Fixture-guest"))
				.andExpect(status().isUnauthorized());
		assertThat(count("file_claim")).isZero();
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void hiddenUnreviewedDraftCanBePreviewedWithoutApplyingOrCountingGuestViews() throws Exception {
		mvc.perform(get(path(audio)).session(login())).andExpect(status().isOk())
				.andExpect(content().contentType("audio/mpeg")).andExpect(content().bytes(MP3))
				.andExpect(header().string("X-Audio-Checksum", WavPolicy.hash(MP3)))
				.andExpect(header().string("X-Text-Revision-Id", source.text().toString()))
				.andExpect(header().string("Content-Language", "vi"))
				.andExpect(header().string("Content-Length", String.valueOf(MP3.length)))
				.andExpect(header().string("Cache-Control", "no-store"));
		for (String table : new String[]{"file_claim", "runtime_task", "place_view", "guest_session", "content_pair", "audio_review_ref", "operation_receipt"})
			assertThat(count(table)).isZero();
		assertThat(sql.queryForObject("SELECT visible FROM place", Boolean.class)).isFalse();
	}

	@Test
	void theRequestedAudioKeepsItsOwnTextLanguageAndBytes() throws Exception {
		Source other = fixture.insertSource("fr");
		UUID aid = fixture.insertAudio(other);
		byte[] different = "ID3-fixture-fr".getBytes(java.nio.charset.StandardCharsets.UTF_8);
		write(other, different);
		mvc.perform(get(path(aid)).session(login())).andExpect(status().isOk()).andExpect(content().bytes(different))
				.andExpect(header().string("X-Text-Revision-Id", other.text().toString())).andExpect(header().string("Content-Language", "fr"));
	}

	@Test
	void automaticGetDoesNotExtendQ3AndExactThirtyMinutesRejectsAnotherDownload() throws Exception {
		MockHttpSession browser = login();
		clock.set(AudioDataFixture.NOW.plus(Duration.ofMinutes(29)));
		mvc.perform(get(path(audio)).session(browser)).andExpect(status().isOk());
		mvc.perform(get("/api/v1/admin/session").session(browser)).andExpect(status().isOk())
				.andExpect(jsonPath("$.expiresAt").value(AudioDataFixture.NOW.plus(Duration.ofMinutes(30)).toString()));
		clock.set(AudioDataFixture.NOW.plus(Duration.ofMinutes(30)));
		mvc.perform(get(path(audio)).session(browser)).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("ADMIN_SESSION_EXPIRED"));
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void headAndRangeRequestsCannotLeaveAnUnreadClaim() throws Exception {
		MockHttpSession browser = login();
		mvc.perform(head(path(audio)).session(browser)).andExpect(status().isOk()).andExpect(content().bytes(new byte[0]))
				.andExpect(header().string("Content-Length", String.valueOf(MP3.length)));
		assertThat(count("file_claim")).isZero();
		mvc.perform(get(path(audio)).session(browser).header("Range", "bytes=1-2"))
				.andExpect(status().isOk()).andExpect(content().bytes(MP3)).andExpect(header().string("Accept-Ranges", "none"));
		assertThat(count("file_claim")).isZero();
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void malformedUnknownAndDeletedIdsReturnStructuredErrorsWithoutPrivatePaths() throws Exception {
		MockHttpSession browser = login();
		mvc.perform(get("/api/v1/admin/audio-revisions/not-a-uuid/preview").session(browser)).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
		String absent = mvc.perform(get(path(UUID.randomUUID())).session(browser)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("AUDIO_REVISION_NOT_FOUND")).andReturn().getResponse().getContentAsString();
		assertThat(absent).doesNotContain(ROOT.toString(), "storageKey", "checksum");
		deletion.delete(account, key(), source.place().toString(), "\"0\"");
		mvc.perform(get(path(audio)).session(browser)).andExpect(status().isNotFound());
	}

	@Test
	void missingOrWrongSizeMp3ReturnsNotReadyWithoutClaimingAWavFallback() throws Exception {
		MockHttpSession browser = login();
		Files.delete(ROOT.resolve(source.mp3() + ".mp3"));
		mvc.perform(get(path(audio)).session(browser)).andExpect(status().isServiceUnavailable()).andExpect(jsonPath("$.code").value("MEDIA_NOT_READY"));
		Files.write(ROOT.resolve(source.mp3() + ".mp3"), new byte[]{1});
		mvc.perform(get(path(audio)).session(browser)).andExpect(status().isServiceUnavailable());
		assertThat(count("file_claim")).isZero();
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void directoryOrNonCanonicalStorageKeyIsNotServed() throws Exception {
		MockHttpSession browser = login();
		Path path = ROOT.resolve(source.mp3() + ".mp3");
		Files.delete(path);
		Files.createDirectory(path);
		try {
			mvc.perform(get(path(audio)).session(browser)).andExpect(status().isServiceUnavailable());
		}
		finally {
			Files.delete(path);
		}
		Files.write(path, MP3);
		sql.update("UPDATE file_asset SET storage_key = '../outside.mp3' WHERE id = ?", bytes(source.mp3()));
		mvc.perform(get(path(audio)).session(browser)).andExpect(status().isServiceUnavailable());
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void aDeletingFileIsNeverOpenedEvenIfBytesAreStillPresent() throws Exception {
		lifecycle.deleting(source.mp3(), UUID.randomUUID(), owners.identity().id(), UUID.randomUUID(), clock.instant());
		mvc.perform(get(path(audio)).session(login())).andExpect(status().isServiceUnavailable());
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void actualStreamOwnsAReadScopeUntilCloseAndRepeatedCloseIsSafe() throws Exception {
		var request = authorizedRequest();
		var result = preview.download(audio.toString(), () -> sessions.current(request));
		assertThat(count("file_claim")).isEqualTo(1);
		assertThat(sql.queryForObject("SELECT owner_instance FROM file_claim", byte[].class)).isEqualTo(bytes(owners.identity().id()));
		assertThat(count("runtime_task")).isEqualTo(1);
		var stream = result.resource().getInputStream();
		try {
			assertThat(stream.readNBytes(MP3.length)).isEqualTo(MP3);
			assertThat(count("file_claim")).isEqualTo(1);
		}
		finally {
			stream.close();
			stream.close();
		}
		assertThat(count("file_claim")).isZero();
		assertThat(count("runtime_task")).isZero();
	}

	@Test
	void twoStreamsUseSeparateClaimsAndClosingOneCannotUnpinTheOther() throws Exception {
		var request = authorizedRequest();
		try (var first = preview.download(audio.toString(), () -> sessions.current(request)).resource().getInputStream();
				var second = preview.download(audio.toString(), () -> sessions.current(request)).resource().getInputStream()) {
			assertThat(count("file_claim")).isEqualTo(2);
			first.close();
			assertThat(count("file_claim")).isEqualTo(1);
			assertThat(second.readAllBytes()).isEqualTo(MP3);
		}
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void expiryDuringDownloadStopsRemainingReadsAndReleasesTheClaim() throws Exception {
		var request = authorizedRequest();
		try (var stream = preview.download(audio.toString(), () -> sessions.current(request)).resource().getInputStream()) {
			assertThat(stream.read()).isEqualTo(MP3[0]);
			clock.set(AudioDataFixture.NOW.plus(Duration.ofMinutes(30)));
			assertThatThrownBy(stream::readAllBytes).isInstanceOf(IOException.class).hasMessage("ADMIN_PREVIEW_FORBIDDEN");
			assertThat(count("file_claim")).isZero();
			assertThat(count("runtime_task")).isZero();
		}
	}

	@Test
	void logoutOrDisabledAccountStopsAnAlreadyOpenedStream() throws Exception {
		var request = authorizedRequest();
		try (var stream = preview.download(audio.toString(), () -> sessions.current(request)).resource().getInputStream()) {
			request.getSession().invalidate();
			assertThatThrownBy(stream::read).isInstanceOf(IOException.class);
		}
		var current = authorizedRequest();
		try (var stream = preview.download(audio.toString(), () -> sessions.current(current)).resource().getInputStream()) {
			sql.update("UPDATE admin_account SET enabled = FALSE WHERE id = ?", bytes(account));
			assertThatThrownBy(() -> stream.read(new byte[8])).isInstanceOf(IOException.class);
		}
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void deletingThePlaceCannotUnlinkTheFileOfAnAuthorizedReadAlreadyInProgress() throws Exception {
		var request = authorizedRequest();
		try (var stream = preview.download(audio.toString(), () -> sessions.current(request)).resource().getInputStream()) {
			deletion.delete(account, key(), source.place().toString(), "\"0\"");
			assertThat(count("audio_revision")).isZero();
			assertThat(count("file_claim")).isEqualTo(1);
			assertThat(cleanup.clean(source.mp3())).isFalse();
			assertThat(stream.readAllBytes()).isEqualTo(MP3);
			assertThat(count("place_view")).isZero();
		}
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void failureAfterClaimInsertRollsItBackAndClosesTheRuntimeScope() {
		var request = authorizedRequest();
		doAnswer(call -> {
			call.callRealMethod();
			throw new DataIntegrityViolationException("CLAIM_FIXTURE_FAILURE");
		}).when(claims).claim(any(), any(), any(), any(), any());
		assertThatThrownBy(() -> preview.download(audio.toString(), () -> sessions.current(request))).isInstanceOf(DataIntegrityViolationException.class);
		assertThat(count("file_claim")).isZero();
		assertThat(count("runtime_task")).isZero();
		assertThat(count("audio_revision")).isEqualTo(1);
	}

	@Test
	void permissionIsRecheckedAfterWaitingForTheFileLock() throws Exception {
		var request = authorizedRequest();
		var locked = new CountDownLatch(1);
		var release = new CountDownLatch(1);
		var checked = new CountDownLatch(1);
		try (var pool = Executors.newFixedThreadPool(2)) {
			var holder = pool.submit(() -> new TransactionTemplate(manager).executeWithoutResult(tx -> {
				sql.queryForObject("SELECT id FROM file_asset WHERE id = ? FOR UPDATE", byte[].class, bytes(source.mp3()));
				locked.countDown();
				try {
					if (!release.await(15, TimeUnit.SECONDS))
						throw new IllegalStateException("FIXTURE_TIMEOUT");
				}
				catch (InterruptedException error) {
					Thread.currentThread().interrupt();
					throw new IllegalStateException(error);
				}
			}));
			try {
				assertThat(locked.await(15, TimeUnit.SECONDS)).isTrue();
				var reader = pool.submit(() -> {
					authenticate();
					try {
						return preview.download(audio.toString(), () -> {
							sessions.current(request);
							checked.countDown();
						});
					}
					finally {
						SecurityContextHolder.clearContext();
					}
				});
				assertThat(checked.await(15, TimeUnit.SECONDS)).isTrue();
				clock.set(AudioDataFixture.NOW.plus(Duration.ofMinutes(30)));
				release.countDown();
				holder.get(15, TimeUnit.SECONDS);
				assertThatThrownBy(() -> reader.get(15, TimeUnit.SECONDS)).hasCauseInstanceOf(AdminSessionException.class);
				assertThat(count("file_claim")).isZero();
				assertThat(count("runtime_task")).isZero();
			}
			finally {
				release.countDown();
			}
		}
	}

	private MockHttpSession login() throws Exception {
		var initial = mvc.perform(get("/api/v1/auth/csrf")).andReturn();
		var browser = (MockHttpSession) initial.getRequest().getSession(false);
		var csrf = json.readValue(initial.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(post("/api/v1/auth/login").session(browser).header(csrf.headerName(), csrf.token()).contentType("application/json")
				.content(json.writeValueAsString(new AdminLoginRequest("preview-owner", "Preview-fixture")))).andExpect(status().isOk());
		return browser;
	}

	private MockHttpServletRequest authorizedRequest() {
		authenticate();
		var request = new MockHttpServletRequest();
		request.setSession(new MockHttpSession());
		sessions.start(request, account);
		return request;
	}

	private void authenticate() {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(account.toString(), "Fixture",
				List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
	}

	private void write(Source source, byte[] bytes) throws IOException {
		Files.write(ROOT.resolve(source.mp3() + ".mp3"), bytes);
		sql.update("UPDATE file_asset SET byte_size = ?, checksum = ? WHERE id = ?", bytes.length,
				java.util.HexFormat.of().parseHex(WavPolicy.hash(bytes)), bytes(source.mp3()));
	}

	private String path(UUID id) {
		return "/api/v1/admin/audio-revisions/" + id + "/preview";
	}

	private String key() {
		return commands.issue(account).windowId() + ":" + UUID.randomUUID();
	}

	private long count(String table) {
		return sql.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
	}
}
