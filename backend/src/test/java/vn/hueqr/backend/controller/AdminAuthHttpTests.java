package vn.hueqr.backend.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminAuthHttpTests {
	private static final String PASSWORD = "Mat-khau-HTTP-fixture";

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private ObjectMapper json;

	@Autowired
	private AdminAccountRepository accounts;

	@Autowired
	private PasswordEncoder passwords;

	@Autowired
	private MutableClock clock;

	@BeforeEach
	void reset() {
		accounts.deleteAll();
		accounts.saveAndFlush(new AdminAccount(UUID.randomUUID(), "http-admin", passwords.encode(PASSWORD), true));
		clock.set(GuestSessionTestClock.BASE);
	}

	@Test
	void csrfResponseSetsSecureHttpOnlySameSiteSessionCookieOnTheRealServletContainer() throws Exception {
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpResponse<String> result = send(client, "GET", "/auth/csrf", null, null, null);
			String cookie = result.headers().firstValue("Set-Cookie").orElseThrow();

			assertThat(result.statusCode()).isEqualTo(200);
			assertThat(cookie).contains("JSESSIONID=", "Secure", "HttpOnly", "SameSite=Strict");
			assertThat(result.headers().firstValue("Cache-Control")).hasValue("no-store");
			assertThat(json.readValue(result.body(), AdminCsrfResponse.class).headerName()).isEqualTo("X-CSRF-TOKEN");
		}
	}

	@Test
	void realCookiesAndCsrfAreRotatedOnLoginAndLogoutRevokesTheAuthenticatedContext() throws Exception {
		// Cookie Secure được gửi thủ công trên localhost HTTP, ca này kiểm header và vòng đời, chưa kiểm TLS qua proxy
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpResponse<String> initial = send(client, "GET", "/auth/csrf", null, null, null);
			String originalCookie = cookie(initial);
			AdminCsrfResponse oldToken = json.readValue(initial.body(), AdminCsrfResponse.class);
			HttpResponse<String> login = send(client, "POST", "/auth/login", originalCookie, oldToken,
					json.writeValueAsString(new AdminLoginRequest("http-admin", PASSWORD)));
			String authenticatedCookie = cookie(login);

			assertThat(login.statusCode()).isEqualTo(200);
			assertThat(authenticatedCookie).isNotEqualTo(originalCookie);
			assertThat(send(client, "GET", "/admin/session", originalCookie, null, null).statusCode()).isEqualTo(401);
			assertThat(send(client, "GET", "/admin/session", authenticatedCookie, null, null).statusCode()).isEqualTo(200);
			assertThat(send(client, "POST", "/admin/session/activity", authenticatedCookie, oldToken,
					"{\"activityType\":\"EDIT\"}").statusCode()).isEqualTo(403);

			AdminCsrfResponse currentToken = json.readValue(
					send(client, "GET", "/auth/csrf", authenticatedCookie, null, null).body(), AdminCsrfResponse.class);
			assertThat(send(client, "POST", "/auth/logout", authenticatedCookie, currentToken, "").statusCode()).isEqualTo(204);
			assertThat(send(client, "GET", "/admin/session", authenticatedCookie, null, null).statusCode()).isEqualTo(401);
		}
	}

	private HttpResponse<String> send(HttpClient client, String method, String path, String cookie,
			AdminCsrfResponse csrf, String body) throws Exception {
		var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1" + path));
		if (cookie != null)
			request.header("Cookie", cookie);
		if (csrf != null)
			request.header(csrf.headerName(), csrf.token());
		if (body != null)
			request.header("Content-Type", "application/json");
		request.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
		return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
	}

	private static String cookie(HttpResponse<String> response) {
		return response.headers().firstValue("Set-Cookie").orElseThrow().split(";", 2)[0];
	}
}
