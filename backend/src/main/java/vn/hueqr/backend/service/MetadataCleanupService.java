package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.LifecycleRepository;

@Service
public class MetadataCleanupService {
	private final LifecycleRepository repository;
	private final RuntimeOwnerService owners;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public MetadataCleanupService(LifecycleRepository repository, RuntimeOwnerService owners, Clock clock, PlatformTransactionManager manager) {
		this.repository = repository;
		this.owners = owners;
		this.clock = clock;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public int windows(int limit) {
		positive(limit);
		int deleted = 0;
		for (UUID id : repository.windows(clock.instant(), limit))
			if (Boolean.TRUE.equals(transaction.execute(tx -> repository.purgeWindow(id, clock.instant()))))
				deleted++;
		return deleted;
	}

	public int jobs(int limit) {
		positive(limit);
		for (UUID id : repository.detachedJobs(limit))
			transaction.executeWithoutResult(tx -> {
				var found = repository.job(id, false);
				if (found.isEmpty())
					return;
				repository.lockTarget(found.get());
				var current = repository.job(id, true).orElse(null);
				if (current != null && !current.valid() && current.localFinishedAt() != null && !repository.jobInUse(id))
					repository.reconcileJob(current, now());
			});
		int deleted = 0;
		for (UUID id : repository.oldJobs(clock.instant().minus(Duration.ofDays(7)), limit))
			if (Boolean.TRUE.equals(transaction.execute(tx -> {
				var found = repository.job(id, false);
				if (found.isEmpty())
					return false;
				repository.lockTarget(found.get());
				var current = repository.job(id, true).orElse(null);
				return current != null && repository.purgeJob(current, clock.instant().minus(Duration.ofDays(7)));
			})))
				deleted++;
		return deleted;
	}

	public boolean reconcile(UUID instance) {
		boolean stopped = Boolean.TRUE.equals(transaction.execute(tx -> {
			var candidate = repository.instance(instance, true).orElse(null);
			if (candidate == null || !owners.provedStopped(candidate))
				return false;
			repository.stop(instance, now());
			repository.releaseClaims(instance);
			return true;
		}));
		if (stopped)
			for (UUID id : repository.ownerJobs(instance))
				transaction.executeWithoutResult(tx -> {
					var found = repository.job(id, false);
					if (found.isEmpty())
						return;
					repository.lockTarget(found.get());
					var current = repository.job(id, true).orElse(null);
					if (current != null && instance.equals(current.instance()) && !repository.jobInUse(id)) {
						repository.finishedLocally(id, instance, now());
						repository.reconcileJob(current, now());
					}
				});
		return stopped;
	}

	public int reconcile(int limit) {
		positive(limit);
		int stopped = 0;
		for (UUID id : repository.instances(limit))
			if (reconcile(id))
				stopped++;
		return stopped;
	}

	private java.time.Instant now() {
		return clock.instant().truncatedTo(ChronoUnit.MICROS);
	}

	private static void positive(int value) {
		if (value < 1)
			throw new IllegalArgumentException("CLEANUP_BATCH_MUST_BE_POSITIVE");
	}
}
