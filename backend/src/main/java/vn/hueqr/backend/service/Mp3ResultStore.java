package vn.hueqr.backend.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.service.LocalAudioService.Lease;

public class Mp3ResultStore {
	public record Staged(UUID jobId, UUID token, UUID fileId, UUID wavFileId, String textHash, String storageKey,
			String checksum, long byteSize, BigDecimal duration) {
	}

	private final Path root;
	private final Mp3Encoder encoder;
	private final WorkspaceCleanupService workspace;
	private final ObjectMapper json;

	public Mp3ResultStore(Path root, Mp3Encoder encoder, WorkspaceCleanupService workspace, ObjectMapper json) throws IOException {
		if (!root.isAbsolute())
			throw new IllegalArgumentException("AUDIO_ROOT_MUST_BE_ABSOLUTE");
		Files.createDirectories(root);
		this.root = root.toRealPath();
		this.encoder = encoder;
		this.workspace = workspace;
		this.json = json;
	}

	public Staged prepare(Lease lease) throws IOException {
		Path wav = source(lease);
		String key = lease.file() + ".mp3";
		Path mp3 = root.resolve(key);
		Path manifest = root.resolve(lease.job() + "-" + lease.token() + ".mp3.json");
		var recovered = recover(lease);
		if (recovered.isPresent()) {
			workspace.completedMp3(recovered.get());
			return recovered.get();
		}
		// File không có manifest hoàn chỉnh chưa chứng minh chuyển đổi đã xong, không ghi đè hoặc gắn vào audio
		if (Files.exists(mp3, LinkOption.NOFOLLOW_LINKS))
			throw new IOException("LOCAL_RESULT_INCOMPLETE");
		workspace.track(key, lease.job(), lease.token(), lease.file(), "MP3");
		try (var channel = FileChannel.open(mp3, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
			encoder.encode(wav, Channels.newOutputStream(channel));
			channel.force(true);
		}
		Staged info = metadata(lease, mp3);
		// Đầu ra hoàn chỉnh được giữ như file READY trước khi manifest hoặc giao dịch audio có thể lỗi
		workspace.completedMp3(info);
		Path temporary = root.resolve(UUID.randomUUID() + ".manifest-part");
		workspace.track(temporary.getFileName().toString(), lease.job(), lease.token(), null, "TEMP");
		try (var channel = FileChannel.open(temporary, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
			var buffer = java.nio.ByteBuffer.wrap(json.writeValueAsBytes(info));
			while (buffer.hasRemaining())
				channel.write(buffer);
			channel.force(true);
		}
		workspace.track(manifest.getFileName().toString(), lease.job(), lease.token(), null, "MANIFEST");
		Files.move(temporary, manifest, StandardCopyOption.ATOMIC_MOVE);
		return info;
	}

	private Path source(Lease lease) throws IOException {
		Path wav = root.resolve(lease.source().key());
		try {
			if (!lease.source().key().equals(lease.source().id() + ".wav") || !Files.isRegularFile(wav, LinkOption.NOFOLLOW_LINKS))
				throw new IllegalArgumentException();
			var info = WavPolicy.inspect(read(wav));
			if (!info.checksum().equals(lease.source().checksum()) || info.byteSize() != lease.source().size()
					|| info.duration().compareTo(lease.source().duration()) != 0)
				throw new IllegalArgumentException();
		}
		catch (IOException | RuntimeException error) {
			throw new IOException("LOCAL_SOURCE_INVALID");
		}
		return wav;
	}

	// Chỉ đối chiếu kết quả cũ, không encode hoặc ghi metadata trước khi Check kiểm cửa sổ lệnh
	public java.util.Optional<Staged> recover(Lease lease) throws IOException {
		source(lease);
		String key = lease.file() + ".mp3";
		Path mp3 = root.resolve(key);
		Path manifest = root.resolve(lease.job() + "-" + lease.token() + ".mp3.json");
		if (Files.exists(manifest, LinkOption.NOFOLLOW_LINKS)) {
			try {
				var staged = json.readValue(read(manifest), Staged.class);
				if (!lease.job().equals(staged.jobId()) || !lease.token().equals(staged.token()) || !lease.file().equals(staged.fileId())
						|| !lease.source().id().equals(staged.wavFileId()) || !lease.hash().equals(staged.textHash()) || !key.equals(staged.storageKey()))
					throw new IllegalArgumentException();
				var info = metadata(lease, mp3);
				if (!info.checksum().equals(staged.checksum()) || info.byteSize() != staged.byteSize() || info.duration().compareTo(staged.duration()) != 0)
					throw new IllegalArgumentException();
				return java.util.Optional.of(info);
			}
			catch (RuntimeException error) {
				throw new IOException("LOCAL_RESULT_INVALID");
			}
		}
		return java.util.Optional.empty();
	}

	private Staged metadata(Lease lease, Path path) throws IOException {
		byte[] bytes = read(path);
		if (bytes.length == 0)
			throw new IOException("LOCAL_RESULT_INVALID");
		BigDecimal duration = encoder.inspect(path);
		if (duration == null || duration.signum() <= 0)
			throw new IOException("LOCAL_RESULT_INVALID");
		return new Staged(lease.job(), lease.token(), lease.file(), lease.source().id(), lease.hash(), path.getFileName().toString(),
				WavPolicy.hash(bytes), bytes.length, duration);
	}

	private byte[] read(Path path) throws IOException {
		if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
			throw new IOException("LOCAL_RESULT_INVALID");
		try (var stream = Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS)) {
			return stream.readAllBytes();
		}
	}
}
