package vn.hueqr.backend.config;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.service.GeminiTtsProvider;
import vn.hueqr.backend.service.TtsDispatchService;
import vn.hueqr.backend.service.TtsProvider;
import vn.hueqr.backend.service.TtsResultService;
import vn.hueqr.backend.service.TtsWorker;
import vn.hueqr.backend.service.WavResultStore;
import vn.hueqr.backend.service.RuntimeOwnerService;
import vn.hueqr.backend.service.WorkspaceCleanupService;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "hueqr.tts.enabled", havingValue = "true")
public class TtsWorkerConfiguration {
	@Bean
	TtsProvider ttsProvider(@Value("${HUEQR_TTS_API_KEY}") String apiKey, ObjectMapper json) {
		return new GeminiTtsProvider(HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER)
				.connectTimeout(Duration.ofSeconds(20)).build(), apiKey, json);
	}

	@Bean
	WavResultStore wavResultStore(@Value("${HUEQR_AUDIO_ROOT}") String root, ObjectMapper json, WorkspaceCleanupService workspace) throws IOException {
		Path path = Path.of(root);
		if (!path.isAbsolute())
			throw new IllegalArgumentException("AUDIO_ROOT_MUST_BE_ABSOLUTE");
		return new WavResultStore(path, json, workspace);
	}

	@Bean
	TtsWorker ttsWorker(TtsDispatchService dispatch, TtsProvider provider, WavResultStore store, TtsResultService results, RuntimeOwnerService owners) {
		return new TtsWorker(owners.identity().id(), dispatch, provider, store, results, owners);
	}
}
