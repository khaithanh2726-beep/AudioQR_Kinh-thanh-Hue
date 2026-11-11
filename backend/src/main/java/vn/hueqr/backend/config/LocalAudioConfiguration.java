package vn.hueqr.backend.config;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.service.*;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "hueqr.tts.local.enabled", havingValue = "true")
public class LocalAudioConfiguration {
	@Bean
	Mp3ResultStore mp3ResultStore(@Value("${HUEQR_AUDIO_ROOT}") String root,
			@Value("${HUEQR_FFMPEG_PATH:ffmpeg}") String ffmpeg, @Value("${HUEQR_FFPROBE_PATH:ffprobe}") String ffprobe,
			@Value("${hueqr.tts.local.process-timeout-seconds:300}") long seconds, WorkspaceCleanupService workspace, ObjectMapper json) throws IOException {
		if (seconds < 1)
			throw new IllegalArgumentException("MEDIA_TIMEOUT_MUST_BE_POSITIVE");
		return new Mp3ResultStore(Path.of(root), new FfmpegMp3Encoder(ffmpeg, ffprobe, Duration.ofSeconds(seconds), new MediaToolProcess(), json), workspace, json);
	}
}
