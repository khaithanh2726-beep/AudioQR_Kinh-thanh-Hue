package vn.hueqr.backend.controller;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminSessionResponse;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.GuestSessionRepository;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminAuthApiTests {
	private static final String PASSWORD = "Mat-khau-fixture-chi-dung-trong-test";
	private record Browser(MockHttpSession session, AdminCsrfResponse csrf) {
	}

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper json;

	@Autowired
	private AdminAccountRepository accounts;

	@Autowired
	private GuestSessionRepository guests;

	@Autowired
	private PasswordEncoder passwords;

	@Autowired
	private MutableClock clock;

	private AdminAccount account;

	@BeforeEach
	void reset() {
		accounts.deleteAll();
		guests.deleteAll();
		clock.set(GuestSessionTestClock.BASE);
		account = accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "admin-test", passwords.encode(PASSWORD), true));
	}

	@Test
	void csrfDoesNotAuthenticateOrCreateAGuestSession() throws Exception {
		Browser browser = csrf(null);

		mvc.perform(get("/api/v1/admin/session").session(browser.session()))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("ADMIN_SESSION_EXPIRED"));
		assertThat(guests.count()).isZero();
	}

	@Test
	void loginChangesSessionIdAndReturnsOnlySafeAccountAndServerTimes() throws Exception {
		Browser browser = csrf(null);
		String originalId = browser.session().getId();
		var result = login(browser, "admin-test", PASSWORD).andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store"))
				.andExpect(jsonPath("$.accountId").value(account.getId().toString()))
				.andExpect(jsonPath("$.loginName").value("admin-test")).andExpect(jsonPath("$.role").value("ADMIN"))
				.andExpect(jsonPath("$.passwordHash").doesNotExist());
		AdminSessionResponse response = read(result);

		assertThat(browser.session().getId()).isNotEqualTo(originalId);
		assertThat(response.lastInteractiveAt()).isEqualTo(GuestSessionTestClock.BASE);
		assertThat(response.expiresAt()).isEqualTo(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)));
		assertThat(response.serverNow()).isEqualTo(GuestSessionTestClock.BASE);
		assertThat(guests.count()).isZero();
	}

	@Test
	void loginRequiresCsrfAndDoesNotAcceptBasicAuthentication() throws Exception {
		mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new AdminLoginRequest("admin-test", PASSWORD))))
				.andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("CSRF_INVALID"));
		mvc.perform(get("/api/v1/admin/session").header("Authorization", "Basic YWRtaW4tdGVzdDp3cm9uZw=="))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void wrongUnknownDisabledAndMissingCredentialsHaveTheSameSafeError() throws Exception {
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "disabled", passwords.encode(PASSWORD), false));
		for (AdminLoginRequest input : new AdminLoginRequest[]{new AdminLoginRequest("admin-test", "wrong"),
				new AdminLoginRequest("unknown", PASSWORD), new AdminLoginRequest("disabled", PASSWORD),
				new AdminLoginRequest(null, null), new AdminLoginRequest("admin-test", "x".repeat(1000))}) {
			Browser browser = csrf(null);
			login(browser, input.loginName(), input.password()).andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.code").value("ADMIN_LOGIN_FAILED"))
					.andExpect(jsonPath("$.message").value("Yêu cầu không thể thực hiện."))
					.andExpect(jsonPath("$.password").doesNotExist());
			mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isUnauthorized());
		}
		assertThat(new AdminLoginRequest("admin-test", PASSWORD).toString()).doesNotContain(PASSWORD);
	}

	@Test
	void loginNameIsCaseSensitiveAndDuplicateProvisioningIsRejected() throws Exception {
		login(csrf(null), "ADMIN-TEST", PASSWORD).andExpect(status().isUnauthorized());
		assertThrows(DataIntegrityViolationException.class, () -> accounts.saveAndFlush(
				new AdminAccount(UUID.randomUUID(), "admin-test", passwords.encode(PASSWORD), true)));
	}

	@Test
	void repeatedSessionAndCsrfPollingNeverMoveTheInteractiveDeadline() throws Exception {
		Browser browser = signedIn();
		for (int minute : new int[]{5, 15, 29}) {
			clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(minute)));
			csrf(browser.session());
			mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isOk())
					.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()))
					.andExpect(jsonPath("$.expiresAt").value(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)).toString()));
		}
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)));
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("ADMIN_SESSION_EXPIRED"));
		assertThat(browser.session().isInvalid()).isTrue();
	}

	@Test
	void explicitActivityUsesServerTimeAndDoesNotCreateANewSession() throws Exception {
		Browser browser = signedIn();
		String id = browser.session().getId();
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(20)));
		AdminSessionResponse result = read(activity(browser, "{\"activityType\":\"EDIT\",\"timestamp\":\"2099-01-01T00:00:00Z\"}")
				.andExpect(status().isOk()));

		assertThat(result.lastInteractiveAt()).isEqualTo(clock.instant());
		assertThat(result.expiresAt()).isEqualTo(clock.instant().plus(Duration.ofMinutes(30)));
		assertThat(browser.session().getId()).isEqualTo(id);
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(49)));
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isOk());
	}

	@Test
	void backgroundAndUnknownActivityTypesDoNotRenewTheSession() throws Exception {
		Browser browser = signedIn();
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(29)));
		for (String type : new String[]{"POLL", "AUTO_SAVE", "AUDIO_PLAYING", "MOUSE_MOVE", "unknown"}) {
			activity(browser, json.writeValueAsString(java.util.Map.of("activityType", type)))
					.andExpect(status().is(422)).andExpect(jsonPath("$.fieldErrors.activityType").isNotEmpty());
		}
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
	}

	@Test
	void activityAtTheExactDeadlineCannotReviveTheSession() throws Exception {
		Browser browser = signedIn();
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)));

		activity(browser, "{\"activityType\":\"EDIT\"}").andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("ADMIN_SESSION_EXPIRED"));
		assertThat(browser.session().isInvalid()).isTrue();
	}

	@Test
	void activityOneNanosecondBeforeExpiryCanRenewTheSession() throws Exception {
		Browser browser = signedIn();
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)).minusNanos(1));

		assertThat(read(activity(browser, "{\"activityType\":\"DRAW\"}").andExpect(status().isOk())).lastInteractiveAt())
				.isEqualTo(clock.instant());
	}

	@Test
	void csrfIsRotatedOnLoginAndInvalidTokensCannotExtendOrLogOut() throws Exception {
		Browser anonymous = csrf(null);
		login(anonymous, "admin-test", PASSWORD).andExpect(status().isOk());
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(20)));

		activity(anonymous, "{\"activityType\":\"EDIT\"}").andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("CSRF_INVALID"));
		mvc.perform(post("/api/v1/auth/logout").session(anonymous.session())).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/admin/session").session(anonymous.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
	}

	@Test
	void logoutInvalidatesTheSessionAndASeparateLoginStartsAtANewServerTime() throws Exception {
		Browser browser = signedIn();
		String firstId = browser.session().getId();
		mvc.perform(post("/api/v1/auth/logout").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token())).andExpect(status().isNoContent());
		assertThat(browser.session().isInvalid()).isTrue();
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofHours(1)));
		Browser fresh = signedIn();

		assertThat(fresh.session().getId()).isNotEqualTo(firstId);
		assertThat(read(mvc.perform(get("/api/v1/admin/session").session(fresh.session()))
				.andExpect(status().isOk())).lastInteractiveAt()).isEqualTo(clock.instant());
	}

	@Test
	void expiredSessionCanFetchANewCsrfContextForReauthentication() throws Exception {
		Browser browser = signedIn();
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(30)));
		Browser fresh = csrf(browser.session());

		assertThat(browser.session().isInvalid()).isTrue();
		assertThat(fresh.session().getId()).isNotEqualTo(browser.session().getId());
		login(fresh, "admin-test", PASSWORD).andExpect(status().isOk());
	}

	@Test
	void disablingAnAccountRevokesAnExistingAdminSession() throws Exception {
		Browser browser = signedIn();
		accounts.saveAndFlush(new AdminAccount(account.getId(), account.getLoginName(), account.getPasswordHash(), false));

		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("ADMIN_SESSION_EXPIRED"));
		assertThat(browser.session().isInvalid()).isTrue();
	}

	@Test
	void adminCookiesAndGuestCredentialsCannotReplaceEachOther() throws Exception {
		Browser browser = signedIn();

		mvc.perform(post("/api/v1/guest/start-ticket").session(browser.session())).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("GUEST_CREDENTIAL_INVALID"));
		mvc.perform(get("/api/v1/admin/session").header("X-Guest-Credential",
				java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32])))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("ADMIN_SESSION_EXPIRED"));
	}

	@Test
	void malformedBodiesDoNotMoveTheActivityDeadline() throws Exception {
		Browser browser = signedIn();
		clock.set(GuestSessionTestClock.BASE.plus(Duration.ofMinutes(29)));
		activity(browser, "{invalid").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
		mvc.perform(get("/api/v1/admin/session").session(browser.session())).andExpect(status().isOk())
				.andExpect(jsonPath("$.lastInteractiveAt").value(GuestSessionTestClock.BASE.toString()));
	}

	private Browser signedIn() throws Exception {
		Browser browser = csrf(null);
		login(browser, "admin-test", PASSWORD).andExpect(status().isOk());
		return csrf(browser.session());
	}

	private Browser csrf(MockHttpSession session) throws Exception {
		var request = get("/api/v1/auth/csrf");
		if (session != null)
			request.session(session);
		var result = mvc.perform(request).andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
				.andReturn();
		return new Browser((MockHttpSession) result.getRequest().getSession(false),
				json.readValue(result.getResponse().getContentAsString(), AdminCsrfResponse.class));
	}

	private ResultActions login(Browser browser, String name, String password) throws Exception {
		return mvc.perform(post("/api/v1/auth/login").session(browser.session())
				// MockMvc cần đánh dấu id phiên hợp lệ để tái hiện điều kiện đổi id như cookie thật
				.with(request -> {
					request.setRequestedSessionId(browser.session().getId());
					request.setRequestedSessionIdValid(true);
					return request;
				})
				.header(browser.csrf().headerName(), browser.csrf().token()).contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(new AdminLoginRequest(name, password))));
	}

	private ResultActions activity(Browser browser, String body) throws Exception {
		return mvc.perform(post("/api/v1/admin/session/activity").session(browser.session())
				.header(browser.csrf().headerName(), browser.csrf().token()).contentType(MediaType.APPLICATION_JSON).content(body));
	}

	private AdminSessionResponse read(ResultActions result) throws Exception {
		return json.readValue(result.andReturn().getResponse().getContentAsString(), AdminSessionResponse.class);
	}
}
