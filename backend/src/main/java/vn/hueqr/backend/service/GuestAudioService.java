package vn.hueqr.backend.service;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.GuestContentRepository;

@Service
public class GuestAudioService {
	public record Download(InputStreamResource resource, long byteSize, String checksum) {
	}

	private final GuestContentService guests;
	private final GuestContentRepository content;
	private final FileAssetRepository files;
	private final Clock clock;
	private final Path root;
	private final TransactionTemplate transaction;

	public GuestAudioService(GuestContentService guests, GuestContentRepository content, FileAssetRepository files,
			Clock clock, @Value("${HUEQR_AUDIO_ROOT:}") String root, PlatformTransactionManager manager) {
		this.guests = guests;
		this.content = content;
		this.files = files;
		this.clock = clock;
		this.root = root.isBlank() ? null : Path.of(root);
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public Download download(UUID sid, UUID gid, String credential, String range) {
		var opened = new AtomicReference<InputStream>();
		UUID claim = UUID.randomUUID();
		try {
			Download result = transaction.execute(tx -> {
				var session = guests.authenticate(sid, credential, true);
				var grant = guests.validGrant(sid, gid);
				if (range != null)
					throw new GuestSessionException("FULL_DOWNLOAD_REQUIRED");
				if (grant.mp3Id() == null)
					throw new GuestSessionException("MEDIA_NOT_READY");
				var file = files.findByIdForUpdate(grant.mp3Id()).filter(f -> "READY".equals(f.getState()) && "MP3".equals(f.getFormat()))
						.orElseThrow(() -> new GuestSessionException("MEDIA_NOT_READY"));
				if (!clock.instant().isBefore(session.getExpiresAt()))
					throw new GuestSessionException("SESSION_EXPIRED");
				try {
					if (root == null || !root.isAbsolute())
						throw new IOException();
					Path directory = root.toRealPath();
					Path path = directory.resolve(file.getStorageKey()).normalize();
					if (!directory.equals(path.getParent()) || !file.getStorageKey().equals(path.getFileName().toString())
							|| !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) || Files.size(path) != file.getByteSize())
						throw new IOException();
					opened.set(Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS));
				}
				catch (IOException | IllegalArgumentException | SecurityException error) {
					throw new GuestSessionException("MEDIA_NOT_READY");
				}
				// Giữ claim sau commit tới khi stream thực đóng, không giữ giao dịch trong lúc truyền file
				content.claim(claim, file.getId(), grant.id(), clock.instant().truncatedTo(ChronoUnit.MICROS));
				return new Download(null, file.getByteSize(), HexFormat.of().formatHex(file.getChecksum()));
			});
			InputStream tracked = new FilterInputStream(opened.get()) {
				private boolean closed;

				@Override
				public synchronized void close() throws IOException {
					if (closed)
						return;
					closed = true;
					try {
						super.close();
					}
					finally {
						transaction.executeWithoutResult(tx -> content.release(claim));
					}
				}
			};
			return new Download(new InputStreamResource(tracked), result.byteSize(), result.checksum());
		}
		catch (RuntimeException error) {
			if (opened.get() != null) {
				try {
					opened.get().close();
				}
				catch (IOException closeError) {
					error.addSuppressed(closeError);
				}
			}
			throw error;
		}
	}
}
