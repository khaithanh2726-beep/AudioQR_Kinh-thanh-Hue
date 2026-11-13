package vn.hueqr.backend.service;

import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.OperationsGuardRepository;

@Service
public class OperationsGateService {

	public final class Lease implements AutoCloseable {
		private final UUID id;
		private boolean released;

		private Lease(UUID id) {
			this.id = id;
		}

		@Override
		public synchronized void close() {
			if (!released) {
				transaction.executeWithoutResult(status -> repository.leave(id));
				released = true;
			}
		}
	}

	private static final Set<String> KINDS = Set.of("HTTP", "TTS_QUEUE", "LOCAL_AUDIO", "CLEANUP", "RETENTION");
	private final UUID instance = UUID.randomUUID();
	private final OperationsGuardRepository repository;
	private final TransactionTemplate transaction;

	public OperationsGateService(OperationsGuardRepository repository, PlatformTransactionManager manager) {
		this.repository = repository;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public Lease enter(String kind) {
		if (!KINDS.contains(kind))
			throw new IllegalArgumentException("INVALID_OPERATIONS_KIND");
		return transaction.execute(status -> {
			if (!repository.runningWithAdmissionLock())
				return null;
			UUID id = UUID.randomUUID();
			repository.enter(id, instance, kind);
			return new Lease(id);
		});
	}

	public void runIfOpen(String kind, Runnable work) {
		try (var lease = enter(kind)) {
			if (lease != null)
				work.run();
		}
	}
}
