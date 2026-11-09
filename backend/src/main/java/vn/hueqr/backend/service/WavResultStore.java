package vn.hueqr.backend.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;

import tools.jackson.databind.ObjectMapper;

public class WavResultStore {
	public record Staged(UUID jobId, UUID workerToken, String textHash, UUID fileId, String storageKey,
			String checksum, long byteSize, BigDecimal duration) {
	}

	private final Path root;
	private final ObjectMapper json;

	public WavResultStore(Path root, ObjectMapper json) throws IOException {
		Files.createDirectories(root);
		this.root = root.toRealPath();
		this.json = json;
	}

	public Staged save(UUID jobId, UUID token, String textHash, byte[] wav) throws IOException {
		WavPolicy.Metadata info = WavPolicy.inspect(wav);
		UUID fileId = UUID.randomUUID();
		String key = fileId + ".wav";
		writeNew(root.resolve(key), wav);
		var staged = new Staged(jobId, token, textHash, fileId, key, info.checksum(), info.byteSize(), info.duration());
		// Manifest hoàn chỉnh xuất hiện bằng rename nguyên tử, không lộ JSON đang ghi dở cho Check
		Path temporary = root.resolve(UUID.randomUUID() + ".manifest-part");
		writeNew(temporary, json.writeValueAsBytes(staged));
		Path manifest = manifest(jobId, token);
		if (Files.exists(manifest, LinkOption.NOFOLLOW_LINKS))
			throw new IOException("WAV_MANIFEST_ALREADY_EXISTS");
		Files.move(temporary, manifest, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
		return staged;
	}

	public Optional<Staged> recover(UUID jobId, UUID token, String textHash) throws IOException {
		Path path = manifest(jobId, token);
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
			return Optional.empty();
		if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
			throw new IOException("WAV_MANIFEST_INVALID");
		try {
			Staged staged = json.readValue(Files.readAllBytes(path), Staged.class);
			if (!jobId.equals(staged.jobId()) || !token.equals(staged.workerToken()) || !textHash.equals(staged.textHash())
					|| staged.fileId() == null || !(staged.fileId() + ".wav").equals(staged.storageKey()))
				throw new IOException("WAV_MANIFEST_MISMATCH");
			Path file = root.resolve(staged.storageKey());
			if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS))
				throw new IOException("WAV_FILE_MISSING");
			WavPolicy.Metadata info = WavPolicy.inspect(Files.readAllBytes(file));
			if (!info.checksum().equals(staged.checksum()) || info.byteSize() != staged.byteSize()
					|| info.duration().compareTo(staged.duration()) != 0)
				throw new IOException("WAV_FILE_MISMATCH");
			return Optional.of(staged);
		}
		catch (IllegalArgumentException | NullPointerException error) {
			throw new IOException("WAV_MANIFEST_INVALID", error);
		}
	}

	private Path manifest(UUID job, UUID token) {
		return root.resolve(job + "-" + token + ".json");
	}

	private void writeNew(Path path, byte[] bytes) throws IOException {
		try (FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			while (buffer.hasRemaining())
				channel.write(buffer);
			channel.force(true);
		}
	}
}
