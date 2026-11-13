package vn.hueqr.backend.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class GeminiTtsProvider implements TtsProvider {
	public static final URI ENDPOINT = URI.create("https://generativelanguage.googleapis.com/v1beta/interactions");
	private final HttpClient http;
	private final URI endpoint;
	private final String apiKey;
	private final ObjectMapper json;

	public GeminiTtsProvider(HttpClient http, String apiKey, ObjectMapper json) {
		this(http, ENDPOINT, apiKey, json);
	}

	GeminiTtsProvider(HttpClient http, URI endpoint, String apiKey, ObjectMapper json) {
		if (apiKey == null || apiKey.isBlank() || apiKey.contains("\r") || apiKey.contains("\n"))
			throw new IllegalArgumentException("TTS_API_KEY_REQUIRED");
		if (http.followRedirects() != HttpClient.Redirect.NEVER)
			throw new IllegalArgumentException("TTS_REDIRECTS_FORBIDDEN");
		this.http = http;
		this.endpoint = endpoint;
		this.apiKey = apiKey;
		this.json = json;
	}

	@Override
	public byte[] generate(TtsDispatchService.DispatchLease input) throws Failure {
		var payload = Map.of("model", input.model(), "input", List.of(Map.of("type", "user_input", "content",
				List.of(Map.of("type", "text", "text", input.body())))), "response_format", Map.of("type", "audio", "mime_type", "audio/wav"),
				"generation_config", Map.of("speech_config", List.of(Map.of("voice", input.voice()))));
		HttpRequest request = HttpRequest.newBuilder(endpoint).timeout(Duration.ofMinutes(2))
				.header("x-goog-api-key", apiKey).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofByteArray(json.writeValueAsBytes(payload))).build();
		try {
			// Một lần gọi unary, không retry timeout/429/5xx và không chuyển hướng kèm khóa API
			var response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
			int status = response.statusCode();
			if (status != 200) {
				if (status == 401 || status == 403)
					throw new Failure("PROVIDER_AUTH_FAILED", true);
				if (status == 429)
					throw new Failure("PROVIDER_QUOTA", true);
				if (status == 400 || status == 404 || status == 422)
					throw new Failure("PROVIDER_REQUEST_INVALID", true);
				throw new Failure("PROVIDER_OUTCOME_UNCERTAIN", false);
			}
			return audio(json.readTree(response.body()));
		}
		catch (InterruptedException error) {
			Thread.currentThread().interrupt();
			throw new Failure("PROVIDER_OUTCOME_UNCERTAIN", false);
		}
		catch (IOException | RuntimeException error) {
			throw new Failure("PROVIDER_OUTCOME_UNCERTAIN", false);
		}
	}

	private byte[] audio(JsonNode response) throws Failure {
		if (!"completed".equals(response.path("status").asString()))
			throw new Failure("PROVIDER_OUTCOME_UNCERTAIN", false);
		byte[] result = null;
		for (JsonNode step : response.path("steps")) {
			if (!"model_output".equals(step.path("type").asString()))
				continue;
			for (JsonNode block : step.path("content")) {
				if (!"audio".equals(block.path("type").asString()))
					continue;
				if (result != null || !"audio/wav".equals(block.path("mime_type").asString()))
					throw new Failure("PROVIDER_OUTPUT_INVALID", true);
				try {
					result = Base64.getDecoder().decode(block.path("data").asString());
				}
				catch (IllegalArgumentException | NullPointerException error) {
					throw new Failure("PROVIDER_OUTPUT_INVALID", true);
				}
			}
		}
		if (result == null)
			throw new Failure("PROVIDER_OUTPUT_INVALID", true);
		return result;
	}
}
