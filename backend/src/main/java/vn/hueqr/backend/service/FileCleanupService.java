package vn.hueqr.backend.service;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.LifecycleRepository;
import vn.hueqr.backend.repository.LifecycleRepository.Asset;

@Service
public class FileCleanupService {
	private final LifecycleRepository repository;
	private final RuntimeOwnerService owners;
	private final CleanupFileStore store;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public FileCleanupService(LifecycleRepository repository, RuntimeOwnerService owners, CleanupFileStore store,
			Clock clock, PlatformTransactionManager manager) {
		this.repository = repository;
		this.owners = owners;
		this.store = store;
		this.clock = clock;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public int sweep(int limit) {
		if (limit < 1)
			throw new IllegalArgumentException("CLEANUP_BATCH_MUST_BE_POSITIVE");
		int deleted = 0;
		for (UUID id : repository.assets(limit))
			if (clean(id))
				deleted++;
		return deleted;
	}

	public boolean clean(UUID id) {
		try (var task = owners.begin("DELETE", id)) {
			Asset prepared = transaction.execute(tx -> prepare(id, task));
			if (prepared == null)
				return false;
			try {
				if (!prepared.key().equals(id + "." + prepared.format().toLowerCase(java.util.Locale.ROOT)))
					throw new IOException("CLEANUP_PATH_INVALID");
				// DELETING chặn tham chiếu mới trước khi rời giao dịch để unlink
				store.remove(prepared.key());
				transaction.executeWithoutResult(tx -> repository.deletionResult(id, prepared.deleteToken(), now(), null));
				return true;
			}
			catch (IOException | SecurityException error) {
				transaction.executeWithoutResult(tx -> repository.deletionResult(id, prepared.deleteToken(), null, "UNLINK_PENDING"));
				return false;
			}
		}
	}

	private Asset prepare(UUID id, RuntimeOwnerService.Task task) {
		Asset asset = repository.asset(id, true).orElse(null);
		if (asset == null || "DELETED".equals(asset.state()))
			return null;
		if (repository.referenced(id)) {
			if (asset.orphanSince() != null)
				repository.orphan(id, null);
			return null;
		}
		if ("DELETING".equals(asset.state()) && repository.taskExists(asset.deleteTask()))
			return null;
		if ("READY".equals(asset.state())) {
			Instant anchor = asset.applied() ? asset.unappliedAt() : asset.orphanSince();
			if (anchor == null) {
				if (!asset.applied())
					repository.orphan(id, now());
				return null;
			}
			if (clock.instant().isBefore(anchor.plus(Duration.ofHours(asset.applied() ? 8 : 24))))
				return null;
		}
		repository.deleting(id, UUID.randomUUID(), task.instanceId(), task.id(), now());
		return repository.asset(id, false).orElseThrow();
	}

	private Instant now() {
		return clock.instant().truncatedTo(ChronoUnit.MICROS);
	}
}
