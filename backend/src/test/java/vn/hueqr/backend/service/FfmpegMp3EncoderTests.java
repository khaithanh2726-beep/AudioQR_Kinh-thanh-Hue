package vn.hueqr.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.*;

class FfmpegMp3EncoderTests {
	private static final String VALID = "{\"streams\":[{\"codec_type\":\"audio\",\"codec_name\":\"mp3\",\"sample_rate\":\"24000\",\"channels\":1}],\"format\":{\"duration\":\"1.024\"}}";

	@Test
	void conversionUsesLiteralPathsAndBinaryStdoutWithoutOverwritingAnOutputFile() throws Exception {
		var process = new Probe(VALID);
		var output = new ByteArrayOutputStream();
		encoder(process).encode(Path.of("wav có dấu.wav"), output);
		assertThat(process.calls.getFirst()).containsSubsequence("-i", "wav có dấu.wav").containsSubsequence("-c:a", "libmp3lame")
				.endsWith("-f", "mp3", "pipe:1").doesNotContain("-y");
	}

	@Test
	void metadataIsAcceptedOnlyAfterACompleteDecodeCommand() throws Exception {
		var process = new Probe(VALID);
		assertThat(encoder(process).inspect(Path.of("audio.mp3"))).isEqualByComparingTo("1.024");
		assertThat(process.calls).hasSize(2);
		assertThat(process.calls.get(1)).contains("-xerror").containsSubsequence("-f", "null", "-");
	}

	@Test
	void invalidCodecChannelsSampleRateOrDurationCannotBecomeReadyMetadata() {
		for (String bad : List.of("{}", "[]", VALID.replace("mp3", "aac"), VALID.replace("24000", "44100"),
				VALID.replace("channels\":1", "channels\":2"), VALID.replace("1.024", "0"), VALID.replace("1.024", "NaN"),
				VALID.replace("1.024", "-1"), "not-json"))
			assertThatIOException().isThrownBy(() -> encoder(new Probe(bad)).inspect(Path.of("audio.mp3")));
	}

	@Test
	void decodeFailureIsNotConvertedToSuccessfulMetadata() {
		var process = new Probe(VALID) {
			@Override
			public void run(List<String> args, OutputStream output, Duration timeout) throws IOException {
				if (args.getFirst().equals("ffmpeg"))
					throw new IOException("LOCAL_CONVERSION_FAILED");
				super.run(args,output,timeout);
			}
		};
		assertThatIOException().isThrownBy(() -> encoder(process).inspect(Path.of("audio.mp3")));
	}

	@Test
	void excessiveProbeJsonIsRejectedWithoutAnAudioLengthLimit() {
		assertThatIOException().isThrownBy(() -> encoder(new Probe(" ".repeat(65537))).inspect(Path.of("audio.mp3")));
	}

	private FfmpegMp3Encoder encoder(MediaToolProcess process) {
		return new FfmpegMp3Encoder("ffmpeg", "ffprobe", Duration.ofSeconds(10), process, new ObjectMapper());
	}

	private static class Probe extends MediaToolProcess {
		final String json;
		final List<List<String>> calls = new ArrayList<>();

		Probe(String json) {
			this.json = json;
		}

		@Override
		public void run(List<String> args, OutputStream output, Duration timeout) throws IOException {
			calls.add(args);
			if (args.getFirst().equals("ffprobe"))
				output.write(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
		}
	}
}
