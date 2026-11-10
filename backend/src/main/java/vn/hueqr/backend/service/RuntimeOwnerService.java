package vn.hueqr.backend.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.LifecycleRepository;
import vn.hueqr.backend.repository.LifecycleRepository.Instance;

@Service
public class RuntimeOwnerService {
	public final class Task implements AutoCloseable {
		private final UUID id;
		private boolean released;

		private Task(UUID id) {
			this.id = id;
		}

		public UUID id() {
			return id;
		}

		public UUID instanceId() {
			return owner.id();
		}

		@Override
		public void close() {
			synchronized (RuntimeOwnerService.this) {
				if (released)
					return;
				transaction.executeWithoutResult(tx -> repository.endTask(id));
				released = true;
				active--;
				stopIfIdle();
			}
		}
	}

	private final LifecycleRepository repository;
	private final Clock clock;
	private final String root;
	private final TransactionTemplate transaction;
	private Instance owner;
	private int active;
	private boolean closing;

	public RuntimeOwnerService(LifecycleRepository repository, Clock clock, PlatformTransactionManager manager,
			@Value("${HUEQR_AUDIO_ROOT:}") String root) {
		this.repository = repository;
		this.clock = clock;
		this.root = root;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public synchronized Instance identity() {
		if (closing)
			throw new IllegalStateException("RUNTIME_INSTANCE_CLOSING");
		if (owner == null) {
			try {
				if (root.isBlank() || !Path.of(root).isAbsolute())
					throw new IllegalArgumentException("AUDIO_ROOT_MUST_BE_ABSOLUTE");
				String host = System.getenv("COMPUTERNAME");
				if (host == null || host.isBlank())
					host = System.getenv("HOSTNAME");
				if (host == null || host.isBlank())
					throw new IllegalStateException("RUNTIME_NODE_UNKNOWN");
				var process = ProcessHandle.current();
				var started = process.info().startInstant().orElseThrow().truncatedTo(ChronoUnit.MICROS);
				var instance = new Instance(UUID.randomUUID(), hash(System.getProperty("os.name") + "|" + host + "|" + System.getProperty("user.name")),
						hash(Path.of(root).toRealPath().toString()), process.pid(), started, "RUNNING");
				transaction.executeWithoutResult(tx -> repository.register(instance, now()));
				owner = instance;
			}
			catch (java.io.IOException | java.security.GeneralSecurityException error) {
				throw new IllegalStateException("RUNTIME_IDENTITY_UNAVAILABLE", error);
			}
		}
		return owner;
	}

	public synchronized Task begin(String kind, UUID key) {
		var instance = identity();
		UUID task = UUID.randomUUID();
		transaction.executeWithoutResult(tx -> repository.beginTask(task, instance.id(), kind, key, now()));
		active++;
		return new Task(task);
	}

	public void finishedJob(UUID job) {
		var instance = identity();
		transaction.executeWithoutResult(tx -> repository.finishedLocally(job, instance.id(), now()));
	}

	// PID không còn tồn tại hoặc đã được tiến trình khác dùng lại mới chứng minh owner cũ đã dừng
	public boolean provedStopped(Instance candidate) {
		var local = identity();
		if (!local.node().equals(candidate.node()) || !local.root().equals(candidate.root()))
			return false;
		if ("STOPPED".equals(candidate.state()))
			return true;
		try {
			var handle = ProcessHandle.of(candidate.pid());
			if (handle.isEmpty() || !handle.get().isAlive())
				return true;
			return handle.get().info().startInstant().map(start -> !start.truncatedTo(ChronoUnit.MICROS).equals(candidate.processStartedAt())).orElse(false);
		}
		catch (SecurityException | UnsupportedOperationException error) {
			return false;
		}
	}

	@PreDestroy
	public synchronized void close() {
		closing = true;
		stopIfIdle();
	}

	private void stopIfIdle() {
		if (closing && owner != null && active == 0)
			transaction.executeWithoutResult(tx -> repository.stop(owner.id(), now()));
	}

	private Instant now() {
		return clock.instant().truncatedTo(ChronoUnit.MICROS);
	}

	private static String hash(String value) throws java.security.GeneralSecurityException {
		return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
	}
}
