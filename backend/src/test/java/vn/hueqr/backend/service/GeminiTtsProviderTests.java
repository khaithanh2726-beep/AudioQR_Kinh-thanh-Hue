package vn.hueqr.backend.service;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.support.WavFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeminiTtsProviderTests {
	private HttpServer server;
	private URI endpoint;
	private final ObjectMapper json = new ObjectMapper();
	private final AtomicInteger requests = new AtomicInteger();
	private final AtomicReference<String> requestBody = new AtomicReference<>();
	private final AtomicReference<String> keyHeader = new AtomicReference<>();
	private int status;
	private String response;

	@BeforeEach
	void startServer() throws Exception {
		status = 200;
		response = success();
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/interactions", exchange -> {
			requests.incrementAndGet();
			requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			keyHeader.set(exchange.getRequestHeaders().getFirst("x-goog-api-key"));
			byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(status, bytes.length);
			exchange.getResponseBody().write(bytes);
			exchange.close();
		});
		server.start();
		endpoint = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/interactions");
	}

	@AfterEach
	void stopServer() {
		server.stop(0);
	}

	@Test
	void sendsExactTranscriptSeparatelyFromVoiceAndDecodesWavBytesWithoutAddingHeader() throws Exception {
		assertThat(provider().generate(lease())).isEqualTo(WavFixture.oneSecond());
		var payload = json.readTree(requestBody.get());
		assertThat(payload.path("model").asString()).isEqualTo("gemini-3.8-flash-tts");
		assertThat(payload.path("input").get(0).path("content").get(0).path("text").asString()).isEqualTo(lease().body());
		assertThat(payload.path("generation_config").path("speech_config").get(0).path("voice").asString()).isEqualTo("Charon");
		assertThat(payload.path("response_format").path("mime_type").asString()).isEqualTo("audio/wav");
		assertThat(requestBody.get()).doesNotContain("fixture-key", "workerToken");
		assertThat(keyHeader.get()).isEqualTo("fixture-key");
		assertThat(requests.get()).isEqualTo(1);
	}

	@Test
	void definiteAuthQuotaAndRequestFailuresNeverRetryOrExposeProviderBody() {
		for (int code : new int[]{400, 401, 403, 404, 422, 429}) {
			status = code;
			response = "secret provider body fixture-key";
			var failure = assertThrows(TtsProvider.Failure.class, () -> provider().generate(lease()));
			assertThat(failure.definite()).isTrue();
			assertThat(failure.getMessage()).doesNotContain(response, "fixture-key");
		}
		assertThat(requests.get()).isEqualTo(6);
	}

	@Test
	void serverErrorsRedirectsAndMalformedResponsesRemainUncertainWithoutRetry() {
		for (int code : new int[]{200, 302, 408, 500, 503}) {
			status = code;
			response = "{invalid";
			assertThat(assertThrows(TtsProvider.Failure.class, () -> provider().generate(lease())).definite()).isFalse();
		}
		assertThat(requests.get()).isEqualTo(5);
	}

	@Test
	void nonterminalResponsesAreNotMistakenForAnAudioResult() {
		response = "{\"status\":\"in_progress\",\"steps\":[]}";
		assertThat(assertThrows(TtsProvider.Failure.class, () -> provider().generate(lease())).definite()).isFalse();
	}

	@Test
	void completedResponseMustContainOneWavAudioBlockWithValidBase64() {
		for (String replacement : new String[]{"audio/l16", "audio/mp3"}) {
			response = success().replace("audio/wav", replacement);
			assertThat(assertThrows(TtsProvider.Failure.class, () -> provider().generate(lease())).code()).isEqualTo("PROVIDER_OUTPUT_INVALID");
		}
		response = "{\"status\":\"completed\",\"steps\":[]}";
		assertThat(assertThrows(TtsProvider.Failure.class, () -> provider().generate(lease())).definite()).isTrue();
		response = success().replace(Base64.getEncoder().encodeToString(WavFixture.oneSecond()), "bad$base64");
		assertThat(assertThrows(TtsProvider.Failure.class, () -> provider().generate(lease())).definite()).isTrue();
	}

	@Test
	void configurationRefusesMissingKeysAndRedirectFollowingBeforeSendingAnything() {
		assertThrows(IllegalArgumentException.class, () -> new GeminiTtsProvider(HttpClient.newHttpClient(), " ", json));
		assertThrows(IllegalArgumentException.class, () -> new GeminiTtsProvider(HttpClient.newHttpClient(), "bad\nkey", json));
		assertThrows(IllegalArgumentException.class, () -> new GeminiTtsProvider(
				HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build(), "fixture", json));
		assertThat(requests.get()).isZero();
	}

	private GeminiTtsProvider provider() {
		return new GeminiTtsProvider(HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build(), endpoint, "fixture-key", json);
	}

	private TtsDispatchService.DispatchLease lease() {
		return new TtsDispatchService.DispatchLease(UUID.randomUUID(), UUID.randomUUID(), 1, "  Huế 😀\r\nBản gốc", "vi", "gemini-3.8-flash-tts", "Charon");
	}

	private String success() {
		return json.writeValueAsString(Map.of("status", "completed", "steps", java.util.List.of(Map.of("type", "model_output", "content",
				java.util.List.of(Map.of("type", "audio", "mime_type", "audio/wav", "data", Base64.getEncoder().encodeToString(WavFixture.oneSecond())))))));
	}
}
