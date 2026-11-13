package vn.hueqr.backend.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.PlaceDeletionRepository;
import vn.hueqr.backend.service.AdminCommandService;
import vn.hueqr.backend.service.AdminPlaceDeletionService;
import vn.hueqr.backend.service.StartTicketService;
import vn.hueqr.backend.service.GuestSessionService;
import vn.hueqr.backend.dto.StartRequest;
import vn.hueqr.backend.support.AudioDataFixture;
import vn.hueqr.backend.support.AudioDataFixture.Source;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vn.hueqr.backend.support.AudioDataFixture.bytes;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminPlaceDeletionTests {
	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper json;

	@Autowired
	JdbcTemplate sql;

	@Autowired
	MutableClock clock;

	@Autowired
	PasswordEncoder passwords;

	@Autowired
	AdminAccountRepository accounts;

	@Autowired
	AdminCommandService commands;

	@Autowired
	AdminPlaceDeletionService deletion;

	@Autowired
	StartTicketService tickets;

	@Autowired
	GuestSessionService guests;

	@MockitoSpyBean
	PlaceDeletionRepository repository;

	private UUID account;
	private Source source;
	private AudioDataFixture fixture;

	@BeforeEach
	void setUp() {
		clearData();
		clock.set(AudioDataFixture.NOW);
		account = UUID.randomUUID();
		accounts.saveAndFlush(new AdminAccount(account, "delete-owner", passwords.encode("Delete-fixture"), true));
		fixture = new AudioDataFixture(sql);
		source = fixture.insertSource("vi");
		var audio = fixture.insertAudio(source);
		var pair = fixture.insertPair(source.slot(), source.text(), audio);
		sql.update("UPDATE content_slot SET active_pair_id = ? WHERE id = ?", bytes(pair), bytes(source.slot()));
		sql.update("UPDATE file_asset SET ever_applied = TRUE WHERE id IN (?, ?)", bytes(source.wav()), bytes(source.mp3()));
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
		for (String table : new String[]{"workspace_file", "runtime_task", "tts_job", "text_revision", "content_slot"})
			sql.update("DELETE FROM " + table);
		sql.update("UPDATE place SET active_region_revision_id = NULL");
		for (String table : new String[]{"region_revision", "place", "file_asset", "runtime_instance", "admin_account"})
			sql.update("DELETE FROM " + table);
	}

	@Test
	void deletesPrivateRecordsButLeavesFilesAndAReceiptThatCanBeReplayed() {
		String key = key();
		var first = deletion.delete(account, key, source.place().toString(), "\"0\"");
		assertThat(first.result()).containsEntry("databaseState", "DELETED").containsEntry("fileCleanup", Map.of("state", "PENDING"))
				.containsEntry("historyPreserved", true);
		for (String table : new String[]{"place", "content_slot", "text_revision", "audio_revision", "content_pair"})
			assertThat(count(table)).isZero();
		assertThat(count("file_asset")).isEqualTo(2);
		assertThat(sql.queryForObject("SELECT target_valid FROM tts_job", Boolean.class)).isFalse();
		assertThat(sql.queryForObject("SELECT target_text_revision_id FROM tts_job", byte[].class)).isNull();
		assertThat(sql.queryForObject("SELECT last_unapplied_at FROM file_asset WHERE id = ?", LocalDateTime.class, bytes(source.mp3())))
				.isEqualTo(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
		assertThat(deletion.delete(account, key, source.place().toString(), "\"0\"").operationId()).isEqualTo(first.operationId());
		assertThat(count("operation_receipt")).isEqualTo(1);
	}

	@Test
	void deletingOnePointCannotRemoveAnotherPointWithTheSameNameOrSharedMp3() {
		Source other = fixture.insertSource("vi");
		UUID audio = UUID.randomUUID();
		fixture.insertAudio(audio, other.text(), other.job(), other.wav(), source.mp3());
		UUID pair = fixture.insertPair(other.slot(), other.text(), audio);
		sql.update("UPDATE content_slot SET active_pair_id = ? WHERE id = ?", bytes(pair), bytes(other.slot()));
		deletion.delete(account, key(), source.place().toString(), "\"0\"");
		assertThat(count("place")).isEqualTo(1);
		assertThat(sql.queryForObject("SELECT id FROM place", byte[].class)).isEqualTo(bytes(other.place()));
		assertThat(sql.queryForObject("SELECT last_unapplied_at FROM file_asset WHERE id = ?", LocalDateTime.class, bytes(source.mp3()))).isNull();
		assertThat(count("audio_revision")).isEqualTo(1);
	}

	@Test
	void deletingAnUnappliedOldRevisionDoesNotRestartItsEightHourProtection() {
		sql.update("UPDATE content_slot SET active_pair_id = NULL WHERE id = ?", bytes(source.slot()));
		var original = LocalDateTime.ofInstant(clock.instant().minus(Duration.ofHours(4)), ZoneOffset.UTC);
		sql.update("UPDATE file_asset SET last_unapplied_at = ?", original);
		deletion.delete(account, key(), source.place().toString(), "\"0\"");
		assertThat(sql.queryForList("SELECT last_unapplied_at FROM file_asset", LocalDateTime.class)).containsOnly(original);
	}

	@Test
	void preservesTheSessionAndViewSnapshotWhileClearingOnlyTheLivePlaceReference() {
		String credential = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32]);
		var ticket = tickets.issue(credential);
		var session = guests.start(new StartRequest(ticket.startTicket(), "Khách fixture", "vi"), ticket.requestId().toString(), credential);
		sql.update("INSERT INTO place_view (id, session_id, opening_key, live_place_id, place_key_snapshot, place_name_snapshot, language, viewed_at) VALUES (?, ?, ?, ?, ?, 'Tên lịch sử', 'vi', ?)",
				bytes(UUID.randomUUID()), bytes(session.sessionId()), bytes(UUID.randomUUID()), bytes(source.place()), bytes(source.place()), LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
		deletion.delete(account, key(), source.place().toString(), "\"0\"");
		assertThat(count("guest_session")).isEqualTo(1);
		assertThat(count("place_view")).isEqualTo(1);
		assertThat(sql.queryForObject("SELECT live_place_id FROM place_view", byte[].class)).isNull();
		assertThat(sql.queryForObject("SELECT place_name_snapshot FROM place_view", String.class)).isEqualTo("Tên lịch sử");
	}

	@Test
	void aFailureAfterTheActualDeleteRollsBackAllChildrenFileMarkersAndReceipt() {
		doAnswer(call -> {
			call.callRealMethod();
			throw new DataIntegrityViolationException("DELETE_FIXTURE_FAILURE");
		}).when(repository).deletePlace(source.place());
		assertThatThrownBy(() -> deletion.delete(account, key(), source.place().toString(), "\"0\""))
				.isInstanceOf(DataIntegrityViolationException.class);
		assertThat(count("place")).isEqualTo(1);
		assertThat(count("text_revision")).isEqualTo(1);
		assertThat(count("audio_revision")).isEqualTo(1);
		assertThat(count("content_pair")).isEqualTo(1);
		assertThat(count("operation_receipt")).isZero();
		assertThat(sql.queryForObject("SELECT target_valid FROM tts_job", Boolean.class)).isTrue();
		assertThat(sql.queryForList("SELECT last_unapplied_at FROM file_asset", LocalDateTime.class)).containsOnlyNulls();
	}

	@Test
	void staleOrMissingVersionAndReusedKeysCannotDeleteANewTarget() {
		assertThatThrownBy(() -> deletion.delete(account, key(), source.place().toString(), null)).hasMessage("PRECONDITION_REQUIRED");
		assertThatThrownBy(() -> deletion.delete(account, key(), source.place().toString(), "\"99\"")).hasMessage("VERSION_CONFLICT");
		String key = key();
		deletion.delete(account, key, source.place().toString(), "\"0\"");
		Source other = fixture.insertSource("vi");
		assertThatThrownBy(() -> deletion.delete(account, key, other.place().toString(), "\"0\"")).hasMessage("KEY_REUSED");
		assertThat(count("place")).isEqualTo(1);
	}

	@Test
	void httpDeleteRequiresAnAuthenticatedAdminAndCsrf() throws Exception {
		String path = "/api/v1/admin/places/" + source.place();
		mvc.perform(delete(path)).andExpect(status().isUnauthorized());
		var response = mvc.perform(get("/api/v1/auth/csrf")).andReturn();
		var session = (MockHttpSession) response.getRequest().getSession(false);
		var csrf = json.readValue(response.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(post("/api/v1/auth/login").session(session).header(csrf.headerName(), csrf.token())
				.contentType("application/json").content(json.writeValueAsString(new AdminLoginRequest("delete-owner", "Delete-fixture"))))
				.andExpect(status().isOk());
		mvc.perform(delete(path).session(session).header("Idempotency-Key", key()).header("If-Match", "\"0\""))
				.andExpect(status().isForbidden());
		var current = mvc.perform(get("/api/v1/auth/csrf").session(session)).andReturn();
		csrf = json.readValue(current.getResponse().getContentAsString(), AdminCsrfResponse.class);
		mvc.perform(delete(path).session(session).header(csrf.headerName(), csrf.token()).header("Idempotency-Key", key()).header("If-Match", "\"0\""))
				.andExpect(status().isOk()).andExpect(jsonPath("$.result.databaseState").value("DELETED"))
				.andExpect(jsonPath("$.result.historyPreserved").value(true))
				.andExpect(jsonPath("$.result.fileCleanup.state").value("PENDING"));
	}

	private String key() {
		return commands.issue(account).windowId() + ":" + UUID.randomUUID();
	}

	private long count(String table) {
		return sql.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
	}
}
