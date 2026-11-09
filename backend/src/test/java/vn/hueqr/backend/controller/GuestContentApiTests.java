package vn.hueqr.backend.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
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
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.*;
import vn.hueqr.backend.service.GuestAudioService;
import vn.hueqr.backend.service.GuestContentService;
import vn.hueqr.backend.service.GuestSessionService;
import vn.hueqr.backend.service.StartTicketService;
import vn.hueqr.backend.support.AudioDataFixture;
import vn.hueqr.backend.support.AudioDataFixture.Source;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;
import vn.hueqr.backend.support.WavFixture;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vn.hueqr.backend.support.AudioDataFixture.bytes;
import static vn.hueqr.backend.support.AudioDataFixture.time;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class GuestContentApiTests {
	private static final Path ROOT = Path.of("target/guest-content-files").toAbsolutePath();
	private static final String POLYGON = "{\"type\":\"Polygon\",\"coordinates\":[[[107.5,16.4],[107.6,16.4],[107.5,16.5],[107.5,16.4]]]}";
	private static final String CREDENTIAL = credential(1);
	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper json;

	@Autowired
	JdbcTemplate sql;

	@Autowired
	MutableClock clock;

	@Autowired
	GuestSessionService sessions;

	@Autowired
	StartTicketService tickets;

	@Autowired
	GuestContentService guests;

	@Autowired
	GuestAudioService media;

	private Source source;
	private UUID account;
	private SessionResponse session;
	private UUID nonce;

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("HUEQR_AUDIO_ROOT", ROOT::toString);
	}

	@BeforeEach
	void setUp() throws Exception {
		clearData();
		Files.createDirectories(ROOT);
		clock.set(AudioDataFixture.NOW);
		account = UUID.randomUUID();
		sql.update("INSERT INTO admin_account (id, login_name, password_hash, enabled) VALUES (?, ?, 'Fixture', TRUE)", bytes(account), account.toString());
		source = seed("vi", true);
		session = start(CREDENTIAL);
		nonce = UUID.randomUUID();
		resume(nonce, null).andExpect(status().isOk());
	}

	@AfterEach
	void clearData() {
		sql.update("UPDATE guest_session SET current_opening_id = NULL");
		for (String table : new String[]{"file_claim", "guest_request_result", "guest_content_grant", "guest_open_intent", "place_view", "guest_session", "operation_receipt", "admin_command_window"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE content_slot SET active_pair_id = NULL, draft_text_id = NULL");
		for (String table : new String[]{"content_pair", "audio_review_ref", "audio_revision", "text_review_ref"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE text_revision SET active_tts_job_id = NULL");
		for (String table : new String[]{"tts_job", "text_revision", "content_slot"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE place SET active_region_revision_id = NULL");
		for (String table : new String[]{"region_revision", "place", "file_asset", "admin_account"})
			sql.update("DELETE FROM " + table);
	}

	@Test
	void mapOnlyReturnsVisibleAppliedRegionsWithoutDraftTextOrViewWrites() throws Exception {
		seed("en", false);
		var result = call(get(path("/map")), null, null).andExpect(status().isOk())
				.andExpect(jsonPath("$.places.length()").value(1)).andExpect(jsonPath("$.places[0].id").value(source.place().toString()))
				.andExpect(jsonPath("$.places[0].polygon.type").value("Polygon"))
				.andExpect(jsonPath("$.places[0].mapSpaceKey").value("WGS84_LON_LAT")).andReturn().getResponse().getContentAsString();
		assertThat(result).doesNotContain("draft", "storageKey", "Bản đã lưu", "credential", "review");
		assertThat(count("guest_open_intent")).isZero();
		assertThat(count("place_view")).isZero();
	}

	@Test
	void firstSelectionReturnsOnlyAppliedPairAndTextAckCreatesExactlyOneSnapshot() throws Exception {
		var response = select(source.place(), "vi", 1);
		assertThat(response.text().revisionId()).isEqualTo(source.text());
		assertThat(response.text().body()).isEqualTo("Bản đã lưu");
		assertThat(response.audio().mp3FileId()).isEqualTo(source.mp3());
		assertThat(response.grant().expiresAt()).isEqualTo(session.expiresAt());
		assertThat(count("place_view")).isZero();
		var first = ack(response, 201);
		var repeat = ack(response, 200);
		assertThat(repeat.viewId()).isEqualTo(first.viewId());
		assertThat(repeat.viewedAt()).isEqualTo(first.viewedAt());
		assertThat(repeat.created()).isFalse();
		assertThat(count("place_view")).isEqualTo(1);
	}

	@Test
	void aNewerDraftIsNeverReturnedInPlaceOfTheAppliedText() throws Exception {
		UUID draft = UUID.randomUUID();
		sql.update("INSERT INTO text_revision (id, slot_id, body, content_hash, saved_at) VALUES (?, ?, 'Không được phục vụ', ?, ?)", bytes(draft), bytes(source.slot()), new byte[32], time());
		sql.update("UPDATE content_slot SET draft_text_id = ? WHERE id = ?", bytes(draft), bytes(source.slot()));
		assertThat(select(source.place(), "vi", 1).text().revisionId()).isEqualTo(source.text());
	}

	@Test
	void missingTranslationHasNoGrantNoFallbackAndNoViewUntilTheFirstAvailableLanguage() throws Exception {
		var missing = select(source.place(), "fr", 1);
		assertThat(missing.availability()).isEqualTo("TEXT_NOT_AVAILABLE");
		assertThat(missing.text()).isNull();
		assertThat(missing.audio()).isNull();
		assertThat(missing.grant()).isNull();
		assertThat(missing.availableLanguages()).containsExactly("vi");
		assertThat(count("place_view")).isZero();
		var translated = changeLanguage(missing, "vi", 2);
		assertThat(translated.openingId()).isEqualTo(missing.openingId());
		assertThat(ack(translated, 201).language()).isEqualTo("vi");
	}

	@Test
	void samePlaceAndLanguageChangesKeepOneOpeningAndOneViewWhileInitialLanguageIsImmutable() throws Exception {
		var first = select(source.place(), "vi", 1);
		ack(first, 201);
		var again = select(source.place(), "vi", 2);
		assertThat(again.openingId()).isEqualTo(first.openingId());
		assertThat(again.selectionKind()).isEqualTo("SAME_OPENING");
		var missing = changeLanguage(again, "en", 3);
		assertThat(missing.openingId()).isEqualTo(first.openingId());
		var back = changeLanguage(missing, "vi", 4);
		assertThat(ack(back, 200).language()).isEqualTo("vi");
		assertThat(sessions.status(session.sessionId(), CREDENTIAL).initialLanguage()).isEqualTo("vi");
		assertThat(count("guest_open_intent")).isEqualTo(1);
	}

	@Test
	void goingFromAToBAndBackCreatesThreeIndependentOpenings() throws Exception {
		Source second = seed("vi", true);
		var a = select(source.place(), "vi", 1);
		var b = select(second.place(), "vi", 2);
		var a2 = select(source.place(), "vi", 3);
		assertThat(a2.openingId()).isNotEqualTo(a.openingId()).isNotEqualTo(b.openingId());
		ack(a, 201);
		ack(b, 201);
		ack(a2, 201);
		assertThat(count("place_view")).isEqualTo(3);
	}

	@Test
	void leavingClosesTheCurrentOpeningButAnInFlightAckStillWorks() throws Exception {
		var first = select(source.place(), "vi", 1);
		call(post(path("/openings/" + first.openingId() + "/leave")), new GuestLeaveRequest(nonce, 2L), UUID.randomUUID().toString())
				.andExpect(status().isOk()).andExpect(jsonPath("$.closed").value(true));
		assertThat(ack(first, 201).openingId()).isEqualTo(first.openingId());
		assertThat(select(source.place(), "vi", 3).openingId()).isNotEqualTo(first.openingId());
	}

	@Test
	void delayedSelectionAndLeaveCannotOverwriteTheNewerSelection() throws Exception {
		var first = select(source.place(), "vi", 1);
		Source other = seed("vi", true);
		var latest = select(other.place(), "vi", 3);
		selection(source.place(), "vi", nonce, 2, UUID.randomUUID().toString()).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("STALE_SELECTION"));
		call(post(path("/openings/" + first.openingId() + "/leave")), new GuestLeaveRequest(nonce, 4L), UUID.randomUUID().toString()).andExpect(status().isConflict());
		assertThat(resumeRead(nonce, null).currentOpening()).isEqualTo(latest.openingId());
	}

	@Test
	void lostSelectionResponseReplaysOriginalGrantWithoutMovingTheCurrentOpeningBack() throws Exception {
		String key = UUID.randomUUID().toString();
		var first = read(selection(source.place(), "vi", nonce, 1, key));
		Source other = seed("vi", true);
		var latest = select(other.place(), "vi", 2);
		var replay = read(selection(source.place(), "vi", nonce, 1, key));
		assertThat(replay.grant().id()).isEqualTo(first.grant().id());
		assertThat(replay.openingVersion()).isEqualTo(first.openingVersion());
		assertThat(resumeRead(nonce, null).currentOpening()).isEqualTo(latest.openingId());
		assertThat(count("guest_open_intent")).isEqualTo(2);
	}

	@Test
	void aRequestKeyCannotBeReusedForAnotherPayloadOrOperation() throws Exception {
		String key = UUID.randomUUID().toString();
		var first = read(selection(source.place(), "vi", nonce, 1, key));
		selection(source.place(), "en", nonce, 1, key).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("KEY_REUSED"));
		call(post(path("/openings/" + first.openingId() + "/leave")), new GuestLeaveRequest(nonce, 2L), key)
				.andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("KEY_REUSED"));
	}

	@Test
	void languageVersionPreconditionFailsWithoutMovingSequenceOrLanguage() throws Exception {
		var first = select(source.place(), "vi", 1);
		call(put(path("/openings/" + first.openingId() + "/language")).header("If-Match", "\"opening:" + first.openingId() + ":99\""),
				new GuestLanguageRequest("en", nonce, 2L), UUID.randomUUID().toString()).andExpect(status().isPreconditionFailed());
		assertThat(resumeRead(nonce, null).selectionSeq()).isEqualTo(1);
		assertThat(sessions.status(session.sessionId(), CREDENTIAL).currentLanguage()).isEqualTo("vi");
	}

	@Test
	void reloadUsesCompareAndSetAndRestoreCannotCreateAView() throws Exception {
		var first = select(source.place(), "vi", 1);
		UUID next = UUID.randomUUID();
		resume(next, UUID.randomUUID()).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("DOCUMENT_CONFLICT"));
		var restored = resumeRead(next, nonce);
		assertThat(restored.content().source()).isEqualTo("RESTORE");
		assertThat(restored.content().grant().viewEligible()).isFalse();
		assertThat(restored.currentOpening()).isEqualTo(first.openingId());
		assertThat(restored.selectionSeq()).isZero();
		ack(restored.content(), 403);
		selection(source.place(), "vi", nonce, 2, UUID.randomUUID().toString()).andExpect(status().isConflict());
		assertThat(count("place_view")).isZero();
	}

	@Test
	void replayingTheSameResumeNonceDoesNotResetSequenceOrCreateAnotherOpening() throws Exception {
		select(source.place(), "vi", 1);
		assertThat(resumeRead(nonce, null).selectionSeq()).isEqualTo(1);
		assertThat(resumeRead(nonce, null).selectionSeq()).isEqualTo(1);
		assertThat(count("guest_open_intent")).isEqualTo(1);
		assertThat(count("place_view")).isZero();
	}

	@Test
	void hiddenPointCannotBeSelectedOrRestoredButPreviouslyIssuedGrantStillWorks() throws Exception {
		var first = select(source.place(), "vi", 1);
		sql.update("UPDATE place SET visible = FALSE WHERE id = ?", bytes(source.place()));
		selection(source.place(), "vi", nonce, 2, UUID.randomUUID().toString()).andExpect(status().isNotFound());
		assertThat(resumeRead(UUID.randomUUID(), nonce).content()).isNull();
		ack(first, 201);
		download(first).andExpect(status().isOk());
	}

	@Test
	void deletingPrivateContentDoesNotInvalidateAnAlreadyDeliveredTextAckOrPinnedMp3() throws Exception {
		var first = select(source.place(), "vi", 1);
		deleteSource(source);
		assertThat(ack(first, 201).viewId()).isNotNull();
		assertThat(sql.queryForObject("SELECT live_place_id FROM place_view", byte[].class)).isNull();
		assertThat(sql.queryForObject("SELECT place_name_snapshot FROM place_view", String.class)).isEqualTo("Fixture");
		download(first).andExpect(status().isOk());
	}

	@Test
	void replayNeverSubstitutesAnotherRevisionWhenTheOriginalTextHasBeenDeleted() throws Exception {
		String key = UUID.randomUUID().toString();
		read(selection(source.place(), "vi", nonce, 1, key));
		deleteSource(source);
		selection(source.place(), "vi", nonce, 1, key).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONTENT_UNAVAILABLE"));
	}

	@Test
	void missingRuntimeAudioKeepsTheAppliedTextReadableWithoutAnAudioDownloadGrant() throws Exception {
		Files.delete(ROOT.resolve(source.mp3() + ".mp3"));
		var first = select(source.place(), "vi", 1);
		assertThat(first.text()).isNotNull();
		assertThat(first.audio().availability()).isEqualTo("NOT_READY");
		assertThat(first.audio().mp3FileId()).isNull();
		download(first).andExpect(status().isServiceUnavailable());
		ack(first, 201);
	}

	@Test
	void missingReviewOrUnappliedPairNeverLeaksTextOrAudio() throws Exception {
		sql.update("DELETE FROM audio_review_ref");
		assertThat(select(source.place(), "vi", 1).text()).isNull();
		sql.update("UPDATE content_slot SET active_pair_id = NULL");
		assertThat(select(source.place(), "vi", 2).text()).isNull();
	}

	@Test
	void fullAudioResponseHasExactBytesChecksumAndClosesItsReadClaim() throws Exception {
		var first = select(source.place(), "vi", 1);
		download(first).andExpect(status().isOk()).andExpect(content().contentType("audio/mpeg"))
				.andExpect(header().string("X-Audio-Checksum", first.audio().checksum()))
				.andExpect(header().string("Content-Length", Long.toString(first.audio().byteSize())))
				.andExpect(content().bytes(Files.readAllBytes(ROOT.resolve(source.mp3() + ".mp3"))));
		assertThat(count("file_claim")).isZero();
		assertThat(count("place_view")).isZero();
	}

	@Test
	void claimRemainsUntilTheActualStreamIsClosedAndCloseIsIdempotent() throws Exception {
		var first = select(source.place(), "vi", 1);
		var result = media.download(session.sessionId(), first.grant().id(), CREDENTIAL, null);
		assertThat(count("file_claim")).isEqualTo(1);
		try (var stream = result.resource().getInputStream()) {
			assertThat(stream.read()).isNotNegative();
			assertThat(count("file_claim")).isEqualTo(1);
		}
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void rangeUnknownGrantWrongCredentialAndCrossSessionCannotDownloadOrAck() throws Exception {
		var first = select(source.place(), "vi", 1);
		call(get(path("/content-grants/" + first.grant().id() + "/audio")).header("Range", "bytes=0-"), null, null)
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("FULL_DOWNLOAD_REQUIRED"));
		call(get(path("/content-grants/" + UUID.randomUUID() + "/audio")), null, null).andExpect(status().isForbidden());
		mvc.perform(get(path("/content-grants/" + first.grant().id() + "/audio")).header("X-Guest-Credential", credential(2))).andExpect(status().isUnauthorized());
		var other = start(credential(2));
		mvc.perform(get("/api/v1/guest/sessions/" + other.sessionId() + "/content-grants/" + first.grant().id() + "/audio")
				.header("X-Guest-Credential", credential(2))).andExpect(status().isForbidden());
		call(post(path("/openings/" + UUID.randomUUID() + "/view-ack")), new GuestViewAckRequest(first.grant().id()), null).andExpect(status().isForbidden());
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void deletedOrWrongSizeAudioReturnsMediaNotReadyWithoutAClaim() throws Exception {
		var first = select(source.place(), "vi", 1);
		Files.write(ROOT.resolve(source.mp3() + ".mp3"), new byte[1]);
		download(first).andExpect(status().isServiceUnavailable()).andExpect(jsonPath("$.code").value("MEDIA_NOT_READY"));
		assertThat(count("file_claim")).isZero();
	}

	@Test
	void exactExpiryRejectsSelectionReplayAckAndDownloadButResumeOnlyReturnsState() throws Exception {
		String key = UUID.randomUUID().toString();
		var first = read(selection(source.place(), "vi", nonce, 1, key));
		clock.set(session.expiresAt());
		selection(source.place(), "vi", nonce, 1, key).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("SESSION_EXPIRED"));
		ack(first, 409);
		download(first).andExpect(status().isConflict());
		call(get(path("/map")), null, null).andExpect(status().isConflict());
		var resumed = resumeRead(UUID.randomUUID(), nonce);
		assertThat(resumed.session().state()).isEqualTo("EXPIRED");
		assertThat(resumed.content()).isNull();
		assertThat(resumed.session().expiresAt()).isEqualTo(session.expiresAt());
		assertThat(count("place_view")).isZero();
	}

	@Test
	void endingRevokesGrantsClosesOpeningsAndPreservesCountedHistory() throws Exception {
		var first = select(source.place(), "vi", 1);
		ack(first, 201);
		sessions.end(session.sessionId(), UUID.randomUUID().toString(), CREDENTIAL);
		download(first).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("SESSION_ENDED"));
		ack(first, 409);
		assertThat(sql.queryForObject("SELECT COUNT(*) FROM guest_content_grant WHERE revoked_at IS NULL", Long.class)).isZero();
		assertThat(sql.queryForObject("SELECT COUNT(*) FROM guest_open_intent WHERE state = 'OPEN'", Long.class)).isZero();
		assertThat(count("place_view")).isEqualTo(1);
		assertThat(resumeRead(UUID.randomUUID(), nonce).content()).isNull();
	}

	@Test
	void concurrentRetriesShareOneGrantAndConcurrentAcksShareOneView() throws Exception {
		String key = UUID.randomUUID().toString();
		var request = new GuestSelectionRequest(source.place(), "vi", nonce, 1L);
		var barrier = new CyclicBarrier(2);
		GuestContentResponse first;
		try (var pool = Executors.newFixedThreadPool(2)) {
			var a = pool.submit(() -> {
				barrier.await(15, TimeUnit.SECONDS);
				return guests.select(session.sessionId(), CREDENTIAL, key, request);
			});
			var b = pool.submit(() -> {
				barrier.await(15, TimeUnit.SECONDS);
				return guests.select(session.sessionId(), CREDENTIAL, key, request);
			});
			first = a.get(30, TimeUnit.SECONDS);
			assertThat(b.get(30, TimeUnit.SECONDS).grant().id()).isEqualTo(first.grant().id());
			var c = pool.submit(() -> guests.ack(session.sessionId(), first.openingId(), CREDENTIAL, new GuestViewAckRequest(first.grant().id())));
			var d = pool.submit(() -> guests.ack(session.sessionId(), first.openingId(), CREDENTIAL, new GuestViewAckRequest(first.grant().id())));
			var v1 = c.get(30, TimeUnit.SECONDS);
			var v2 = d.get(30, TimeUnit.SECONDS);
			assertThat(v1.viewId()).isEqualTo(v2.viewId());
			assertThat(v1.created()).isNotEqualTo(v2.created());
		}
		assertThat(count("guest_content_grant")).isEqualTo(1);
		assertThat(count("guest_request_result")).isEqualTo(1);
		assertThat(count("place_view")).isEqualTo(1);
	}

	@Test
	void invalidLanguageSequenceNonceAndMalformedJsonCannotMoveSelection() throws Exception {
		selection(source.place(), "de", nonce, 1, UUID.randomUUID().toString()).andExpect(status().is(422));
		selection(source.place(), "vi", nonce, 0, UUID.randomUUID().toString()).andExpect(status().is(422));
		selection(source.place(), "vi", null, 1, UUID.randomUUID().toString()).andExpect(status().is(422));
		selection(source.place(), "vi", UUID.randomUUID(), 1, UUID.randomUUID().toString()).andExpect(status().isConflict());
		call(post(path("/openings")).contentType(MediaType.APPLICATION_JSON).content("{"), null, null).andExpect(status().isBadRequest());
		assertThat(count("guest_open_intent")).isZero();
	}

	private Source seed(String language, boolean visible) throws Exception {
		var data = new AudioDataFixture(sql);
		Source f = data.insertSource(language);
		byte[] wav = WavFixture.oneSecond();
		// Bytes MP3 giả chỉ kiểm giao thức/quyền/checksum, không chứng minh codec hoặc nghe duyệt
		byte[] mp3 = ("MP3 permission fixture " + f.mp3()).getBytes(StandardCharsets.UTF_8);
		for (var entry : java.util.Map.of(f.wav(), wav, f.mp3(), mp3).entrySet()) {
			Files.write(ROOT.resolve(entry.getKey() + (entry.getKey().equals(f.wav()) ? ".wav" : ".mp3")), entry.getValue());
			sql.update("UPDATE file_asset SET checksum = ?, byte_size = ? WHERE id = ?", digest(entry.getValue()), entry.getValue().length, bytes(entry.getKey()));
		}
		sql.update("UPDATE text_revision SET content_hash = ? WHERE id = ?", digest("Bản đã lưu".getBytes(StandardCharsets.UTF_8)), bytes(f.text()));
		UUID audio = data.insertAudio(f);
		sql.update("INSERT INTO text_review_ref (text_revision_id, locator, recorded_by, recorded_at) VALUES (?, 'Hồ sơ fixture', ?, ?)", bytes(f.text()), bytes(account), time());
		sql.update("INSERT INTO audio_review_ref (audio_revision_id, locator, recorded_by, recorded_at) VALUES (?, 'Hồ sơ fixture', ?, ?)", bytes(audio), bytes(account), time());
		UUID pair = data.insertPair(f.slot(), f.text(), audio);
		sql.update("UPDATE content_slot SET active_pair_id = ? WHERE id = ?", bytes(pair), bytes(f.slot()));
		UUID region = UUID.randomUUID();
		sql.update("INSERT INTO region_revision (id, place_id, polygon, map_space_key, geometry_hash, saved_at) VALUES (?, ?, ?, 'WGS84_LON_LAT', ?, ?)", bytes(region), bytes(f.place()), POLYGON, new byte[32], time());
		sql.update("UPDATE place SET visible = ?, active_region_revision_id = ? WHERE id = ?", visible, bytes(region), bytes(f.place()));
		return f;
	}

	private void deleteSource(Source f) {
		sql.update("UPDATE content_slot SET active_pair_id = NULL, draft_text_id = NULL WHERE id = ?", bytes(f.slot()));
		sql.update("DELETE FROM content_pair WHERE slot_id = ?", bytes(f.slot()));
		sql.update("DELETE FROM audio_review_ref WHERE audio_revision_id IN (SELECT id FROM audio_revision WHERE text_revision_id = ?)", bytes(f.text()));
		sql.update("DELETE FROM audio_revision WHERE text_revision_id = ?", bytes(f.text()));
		sql.update("DELETE FROM text_review_ref WHERE text_revision_id = ?", bytes(f.text()));
		sql.update("UPDATE text_revision SET active_tts_job_id = NULL WHERE id = ?", bytes(f.text()));
		sql.update("DELETE FROM tts_job WHERE id = ?", bytes(f.job()));
		sql.update("DELETE FROM text_revision WHERE id = ?", bytes(f.text()));
		sql.update("DELETE FROM content_slot WHERE id = ?", bytes(f.slot()));
		sql.update("UPDATE place SET active_region_revision_id = NULL WHERE id = ?", bytes(f.place()));
		sql.update("DELETE FROM region_revision WHERE place_id = ?", bytes(f.place()));
		sql.update("DELETE FROM place WHERE id = ?", bytes(f.place()));
	}

	private SessionResponse start(String credential) {
		var ticket = tickets.issue(credential);
		return sessions.start(new StartRequest(ticket.startTicket(), "Khách fixture", "vi"), ticket.requestId().toString(), credential);
	}

	private GuestContentResponse select(UUID place, String language, long seq) throws Exception {
		return read(selection(place, language, nonce, seq, UUID.randomUUID().toString()));
	}

	private ResultActions selection(UUID place, String language, UUID document, long seq, String key) throws Exception {
		return call(post(path("/openings")), new GuestSelectionRequest(place, language, document, seq), key);
	}

	private GuestContentResponse changeLanguage(GuestContentResponse before, String language, long seq) throws Exception {
		return read(call(put(path("/openings/" + before.openingId() + "/language")).header("If-Match", "\"opening:" + before.openingId() + ":" + before.openingVersion() + "\""),
				new GuestLanguageRequest(language, nonce, seq), UUID.randomUUID().toString()));
	}

	private GuestViewAckResponse ack(GuestContentResponse response, int statusCode) throws Exception {
		var result = call(post(path("/openings/" + response.openingId() + "/view-ack")), new GuestViewAckRequest(response.grant().id()), null)
				.andExpect(status().is(statusCode)).andReturn();
		return statusCode < 300 ? json.readValue(result.getResponse().getContentAsString(), GuestViewAckResponse.class) : null;
	}

	private ResultActions download(GuestContentResponse response) throws Exception {
		return call(get(path("/content-grants/" + response.grant().id() + "/audio")), null, null);
	}

	private ResultActions resume(UUID next, UUID previous) throws Exception {
		return call(post(path("/resume")), new GuestResumeRequest(next, previous), null);
	}

	private GuestResumeResponse resumeRead(UUID next, UUID previous) throws Exception {
		return json.readValue(resume(next, previous).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), GuestResumeResponse.class);
	}

	private GuestContentResponse read(ResultActions result) throws Exception {
		return json.readValue(result.andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
				.andReturn().getResponse().getContentAsString(), GuestContentResponse.class);
	}

	private ResultActions call(MockHttpServletRequestBuilder request, Object body, String key) throws Exception {
		request.header("X-Guest-Credential", CREDENTIAL);
		if (body != null)
			request.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body));
		if (key != null)
			request.header("Idempotency-Key", key);
		return mvc.perform(request);
	}

	private String path(String suffix) {
		return "/api/v1/guest/sessions/" + session.sessionId() + suffix;
	}

	private long count(String table) {
		return sql.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
	}

	private static String credential(int marker) {
		byte[] value = new byte[32];
		Arrays.fill(value, (byte) marker);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
	}

	private static byte[] digest(byte[] value) throws Exception {
		return MessageDigest.getInstance("SHA-256").digest(value);
	}
}
