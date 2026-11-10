package vn.hueqr.backend.service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.entity.FileAsset;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.LifecycleRepository;
import vn.hueqr.backend.repository.LifecycleRepository.Workspace;

@Service
public class WorkspaceCleanupService {
	private final LifecycleRepository repository;
	private final FileAssetRepository files;
	private final RuntimeOwnerService owners;
	private final CleanupFileStore store;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public WorkspaceCleanupService(LifecycleRepository repository, FileAssetRepository files, RuntimeOwnerService owners,
			CleanupFileStore store, Clock clock, PlatformTransactionManager manager) {
		this.repository = repository;
		this.files = files;
		this.owners = owners;
		this.store = store;
		this.clock = clock;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	// Đăng ký trước CREATE_NEW để cả file ghi dở cũng có nguồn gốc, không quét file lạ theo mtime
	public void track(String key, UUID job, UUID token, UUID file, String kind) {
		var owner = owners.identity();
		transaction.executeWithoutResult(tx -> {
			var current = repository.job(job, true).orElseThrow();
			if (!owner.id().equals(current.instance()) || !repository.ownsTask(job, owner.id()))
				throw new IllegalStateException("WORKSPACE_WRITER_NOT_REGISTERED");
			repository.trackWorkspace(new Workspace(key, job, token, owner.id(), file, kind, null), now());
		});
	}

	public void completed(WavResultStore.Staged staged) {
		transaction.executeWithoutResult(tx -> {
			var job = repository.job(staged.jobId(), true).orElseThrow();
			if (!owners.identity().id().equals(job.instance()) || !repository.ownsTask(job.id(), job.instance()))
				throw new IllegalStateException("WORKSPACE_WRITER_NOT_REGISTERED");
			register(staged.fileId(), staged.storageKey(), new WavPolicy.Metadata(staged.checksum(), staged.byteSize(), staged.duration()));
		});
	}

	public int sweep(int limit) {
		if (limit < 1)
			throw new IllegalArgumentException("CLEANUP_BATCH_MUST_BE_POSITIVE");
		int cleaned = 0;
		for (var entry : repository.workspaces(limit))
			if (clean(entry.key()))
				cleaned++;
		return cleaned;
	}

	public boolean clean(String key) {
		var found = repository.workspace(key, false).orElse(null);
		if (found == null)
			return false;
		try (var task = owners.begin("TEMP", found.job())) {
			var ready = transaction.execute(tx -> {
				var job = repository.job(found.job(), true).orElse(null);
				var entry = repository.workspace(key, true).orElse(null);
				if (job == null || entry == null)
					return null;
				boolean finished = java.util.Set.of("FAILED", "DISCARDED").contains(job.phase())
						|| ("RESULT_STAGED".equals(job.phase()) && job.localFinishedAt() != null);
				if (!finished || repository.jobInUse(job.id())) {
					repository.workspaceEligible(key, null);
					return null;
				}
				if (entry.eligibleSince() == null) {
					repository.workspaceEligible(key, now());
					return null;
				}
				if (clock.instant().isBefore(entry.eligibleSince().plus(Duration.ofHours(1))) || !repository.claimWorkspace(key, task.id()))
					return null;
				return entry;
			});
			if (ready == null)
				return false;
			try {
				validateKey(ready);
				if ("WAV".equals(ready.kind())) {
					if (repository.asset(ready.file(), false).isPresent()) {
						forget(key);
						return true;
					}
					byte[] data = null;
					try {
						data = store.read(key);
					}
					catch (NoSuchFileException absent) {
						forget(key);
						return true;
					}
					WavPolicy.Metadata complete = null;
					try {
						complete = WavPolicy.inspect(data);
					}
					catch (IllegalArgumentException partial) {
						// WAV ghi dở đã hết tác vụ được xử lý theo hạn file tạm
					}
					if (complete != null) {
						var metadata = complete;
						transaction.executeWithoutResult(tx -> register(ready.file(), key, metadata));
						forget(key);
						return true;
					}
				}
				store.remove(key);
				forget(key);
				return true;
			}
			catch (IOException | SecurityException error) {
				return false;
			}
		}
	}

	private void register(UUID id, String key, WavPolicy.Metadata info) {
		var asset = files.findByIdForUpdate(id).orElse(null);
		if (asset == null)
			files.saveAndFlush(new FileAsset(id, key, HexFormat.of().parseHex(info.checksum()), info.byteSize(), info.duration(), now()));
		else if (!"READY".equals(asset.getState()) || !key.equals(asset.getStorageKey()) || !"WAV".equals(asset.getFormat())
				|| info.byteSize() != asset.getByteSize() || !info.checksum().equals(HexFormat.of().formatHex(asset.getChecksum()))
				|| info.duration().compareTo(asset.getDuration()) != 0)
			throw new IllegalStateException("WORKSPACE_FILE_MISMATCH");
	}

	private void forget(String key) {
		transaction.executeWithoutResult(tx -> repository.forgetWorkspace(key));
	}

	private static void validateKey(Workspace entry) throws IOException {
		boolean valid = switch (entry.kind()) {
			case "WAV" -> entry.file() != null && entry.key().equals(entry.file() + ".wav");
			case "MANIFEST" -> entry.key().equals(entry.job() + "-" + entry.token() + ".json");
			case "TEMP" -> entry.key().matches("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}\\.manifest-part");
			default -> false;
		};
		if (!valid)
			throw new IOException("WORKSPACE_PATH_INVALID");
	}

	private java.time.Instant now() {
		return clock.instant().truncatedTo(ChronoUnit.MICROS);
	}
}
