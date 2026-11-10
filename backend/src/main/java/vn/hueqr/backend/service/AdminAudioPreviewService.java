package vn.hueqr.backend.service;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Clock;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.AudioRevisionRepository;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.FileReadClaimRepository;
import vn.hueqr.backend.repository.PlaceRepository;

@Service
public class AdminAudioPreviewService {
	public record Download(InputStreamResource resource, long byteSize, String checksum, UUID textId, String language) {
	}

	private final AudioRevisionRepository audios;
	private final PlaceRepository places;
	private final FileAssetRepository files;
	private final FileReadClaimRepository claims;
	private final RuntimeOwnerService owners;
	private final Clock clock;
	private final Path root;
	private final TransactionTemplate transaction;

	public AdminAudioPreviewService(AudioRevisionRepository audios, PlaceRepository places, FileAssetRepository files,
			FileReadClaimRepository claims, RuntimeOwnerService owners, Clock clock,
			@Value("${HUEQR_AUDIO_ROOT:}") String root, PlatformTransactionManager manager) {
		this.audios = audios;
		this.places = places;
		this.files = files;
		this.claims = claims;
		this.owners = owners;
		this.clock = clock;
		this.root = root.isBlank() ? null : Path.of(root);
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public Download download(String audioId, Runnable permission) {
		permission.run();
		UUID id = id(audioId);
		UUID claim = UUID.randomUUID();
		var opened = new AtomicReference<InputStream>();
		var task = new AtomicReference<RuntimeOwnerService.Task>();
		try {
			Download info = transaction.execute(tx -> {
				UUID place = audios.findPlaceIdById(id).orElseThrow(() -> new AdminCommandException(404, "AUDIO_REVISION_NOT_FOUND"));
				places.findByIdForUpdate(place).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
				var audio = audios.findById(id).orElseThrow(() -> new AdminCommandException(404, "AUDIO_REVISION_NOT_FOUND"));
				var file = files.findByIdForUpdate(audio.getMp3FileId())
						.filter(f -> "READY".equals(f.getState()) && "MP3".equals(f.getFormat()))
						.orElseThrow(AdminAudioPreviewService::notReady);
				// Kiểm lại quyền sau khi chờ khóa, không dùng hạn phiên ở đầu request để mở file
				permission.run();
				try {
					if (root == null || !root.isAbsolute() || !file.getStorageKey().equals(file.getId() + ".mp3"))
						throw new IOException();
					Path directory = root.toRealPath();
					Path path = directory.resolve(file.getStorageKey()).normalize();
					if (!directory.equals(path.getParent()) || !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)
							|| Files.size(path) != file.getByteSize())
						throw new IOException();
					opened.set(Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS));
				}
				catch (IOException | IllegalArgumentException | SecurityException error) {
					throw notReady();
				}
				task.set(owners.begin("READ", id));
				claims.claim(claim, file.getId(), id, task.get().instanceId(), clock.instant());
				file.retainReference();
				var text = audio.getTextRevision();
				return new Download(null, file.getByteSize(), HexFormat.of().formatHex(file.getChecksum()), text.getId(), text.getSlot().getLanguage());
			});
			InputStream stream = new FilterInputStream(opened.get()) {
				private boolean closed;

				private void authorize() throws IOException {
					if (closed)
						throw new IOException("ADMIN_PREVIEW_CLOSED");
					try {
						permission.run();
					}
					catch (RuntimeException denied) {
						var error = new IOException("ADMIN_PREVIEW_FORBIDDEN");
						try {
							close();
						}
						catch (IOException | RuntimeException closeError) {
							error.addSuppressed(closeError);
						}
						throw error;
					}
				}

				@Override
				public int read() throws IOException {
					authorize();
					return in.read();
				}

				@Override
				public int read(byte[] buffer, int offset, int length) throws IOException {
					authorize();
					return in.read(buffer, offset, length);
				}

				@Override
				public long skip(long count) throws IOException {
					authorize();
					return in.skip(count);
				}

				@Override
				public int available() throws IOException {
					authorize();
					return in.available();
				}

				@Override
				public synchronized void close() throws IOException {
					if (closed)
						return;
					closed = true;
					try {
						super.close();
					}
					finally {
						try {
							transaction.executeWithoutResult(tx -> claims.release(claim));
						}
						finally {
							task.get().close();
						}
					}
				}
			};
			return new Download(new InputStreamResource(stream), info.byteSize(), info.checksum(), info.textId(), info.language());
		}
		catch (RuntimeException error) {
			if (opened.get() != null)
				try {
					opened.get().close();
				}
				catch (IOException closeError) {
					error.addSuppressed(closeError);
				}
			if (task.get() != null)
				task.get().close();
			throw error;
		}
	}

	private static UUID id(String value) {
		try {
			UUID id = UUID.fromString(value);
			if (!id.toString().equals(value))
				throw new IllegalArgumentException();
			return id;
		}
		catch (IllegalArgumentException | NullPointerException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("audioRevisionId", "Cần UUID chuẩn chữ thường."));
		}
	}

	private static AdminCommandException notReady() {
		return new AdminCommandException(503, "MEDIA_NOT_READY");
	}
}
