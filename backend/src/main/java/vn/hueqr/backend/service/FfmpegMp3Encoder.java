package vn.hueqr.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import tools.jackson.databind.ObjectMapper;

public class FfmpegMp3Encoder implements Mp3Encoder {
	private final String ffmpeg;
	private final String ffprobe;
	private final Duration timeout;
	private final MediaToolProcess process;
	private final ObjectMapper json;

	public FfmpegMp3Encoder(String ffmpeg, String ffprobe, Duration timeout, MediaToolProcess process, ObjectMapper json) {
		this.ffmpeg = ffmpeg;
		this.ffprobe = ffprobe;
		this.timeout = timeout;
		this.process = process;
		this.json = json;
	}

	@Override
	public void encode(Path wav, OutputStream output) throws IOException {
		process.run(List.of(ffmpeg, "-nostdin", "-hide_banner", "-loglevel", "error", "-xerror", "-i", wav.toString(),
				"-map", "0:a:0", "-vn", "-sn", "-dn", "-c:a", "libmp3lame", "-ar", "24000", "-ac", "1", "-f", "mp3", "pipe:1"), output, timeout);
	}

	@Override
	public BigDecimal inspect(Path mp3) throws IOException {
		var bytes = new ByteArrayOutputStream();
		var bounded = new OutputStream() {
			@Override
			public void write(int value) throws IOException {
				// Giới hạn JSON kỹ thuật, không giới hạn độ dài bài thuyết minh
				if (bytes.size() >= 65536)
					throw new IOException("LOCAL_PROBE_INVALID");
				bytes.write(value);
			}
		};
		process.run(List.of(ffprobe, "-v", "error", "-show_entries", "stream=codec_name,codec_type,sample_rate,channels:format=duration", "-of", "json", mp3.toString()), bounded, timeout);
		try {
			var result = json.readTree(bytes.toByteArray());
			var streams = result.path("streams");
			if (streams.size() != 1 || !"audio".equals(streams.get(0).path("codec_type").asText())
					|| !"mp3".equals(streams.get(0).path("codec_name").asText())
					|| streams.get(0).path("channels").asInt() != 1 || !"24000".equals(streams.get(0).path("sample_rate").asText()))
				throw new IllegalArgumentException();
			var duration = new BigDecimal(result.path("format").path("duration").asText()).setScale(9, RoundingMode.UNNECESSARY);
			if (duration.signum() <= 0 || duration.precision() > 20)
				throw new IllegalArgumentException();
			// Probe metadata chưa đủ, đọc giải mã toàn file để phát hiện lỗi codec mà công cụ báo
			process.run(List.of(ffmpeg, "-nostdin", "-v", "error", "-xerror", "-i", mp3.toString(), "-map", "0:a:0", "-f", "null", "-"), OutputStream.nullOutputStream(), timeout);
			return duration;
		}
		catch (RuntimeException error) {
			throw new IOException("LOCAL_PROBE_INVALID");
		}
	}
}
