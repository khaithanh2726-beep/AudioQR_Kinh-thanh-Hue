package vn.hueqr.backend.service;

import java.io.IOException;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.LifecycleRepository;
import vn.hueqr.backend.repository.LocalAudioRepository;
import vn.hueqr.backend.repository.LocalAudioRepository.Source;

@Service
public class LocalAudioService {
	public record Lease(UUID job, UUID token, UUID task, UUID file, String hash, Source source) {
	}

	public record Checked(Lease lease, Mp3ResultStore.Staged result) {
	}

	private final LocalAudioRepository repository;
	private final LifecycleRepository lifecycle;
	private final FileAssetRepository files;
	private final RuntimeOwnerService owners;
	private final ObjectProvider<Mp3ResultStore> stores;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public LocalAudioService(LocalAudioRepository repository, LifecycleRepository lifecycle, FileAssetRepository files,
			RuntimeOwnerService owners, ObjectProvider<Mp3ResultStore> stores, Clock clock, PlatformTransactionManager manager) {
		this.repository = repository;
		this.lifecycle = lifecycle;
		this.files = files;
		this.owners = owners;
		this.stores = stores;
		this.clock = clock;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public boolean run(UUID id) {
		var store = stores.getIfAvailable();
		if (store == null)
			return false;
		try (var task = owners.begin("TTS", id)) {
			try {
				Lease lease = transaction.execute(tx -> claim(id, task));
				if (lease == null)
					return false;
				Mp3ResultStore.Staged result;
				try {
					result = store.prepare(lease);
				}
				catch (IOException | RuntimeException error) {
					String code = Set.of("LOCAL_SOURCE_INVALID", "LOCAL_TOOL_UNAVAILABLE", "LOCAL_TOOL_TIMEOUT", "LOCAL_TOOL_INTERRUPTED", "LOCAL_RESULT_INCOMPLETE", "LOCAL_RESULT_INVALID")
							.contains(error.getMessage() == null ? "" : error.getMessage()) ? error.getMessage() : "LOCAL_CONVERSION_FAILED";
					transaction.executeWithoutResult(tx -> fail(lease, code));
					return true;
				}
				// Nếu commit audio lỗi, giữ CONVERTING và manifest để lần nhận lại chỉ đối chiếu local, không gọi provider
				transaction.executeWithoutResult(tx -> finish(lease, result));
				return true;
			}
			finally {
				owners.finishedJob(id);
			}
		}
	}

	private Lease claim(UUID id, RuntimeOwnerService.Task task) {
		var found = repository.job(id, false).orElse(null);
		if (found == null || !Set.of("RESULT_STAGED", "CONVERTING").contains(found.phase()))
			return null;
		repository.lockTarget(found);
		var job = repository.job(id, true).orElseThrow();
		if (!Set.of("RESULT_STAGED", "CONVERTING").contains(job.phase()))
			return null;
		// Task cũ chỉ mất khi scope đã đóng hoặc owner có bằng chứng dừng, không dùng tuổi job để giành lại
		if ("CONVERTING".equals(job.phase()) && lifecycle.taskExists(job.task()))
			return null;
		if (!job.valid() || !repository.matchesTarget(id)) {
			repository.discard(id, now());
			return null;
		}
		var source = repository.wav(job.wav()).orElse(null);
		if (source == null) {
			repository.fail(id, job.text(), "LOCAL_SOURCE_INVALID", now());
			return null;
		}
		UUID token = "CONVERTING".equals(job.phase()) ? job.token() : UUID.randomUUID();
		UUID file = "CONVERTING".equals(job.phase()) ? job.file() : UUID.randomUUID();
		repository.claim(id, task.instanceId(), task.id(), token, file, now());
		return new Lease(id, token, task.id(), file, repository.textHash(id), source);
	}

	java.util.Optional<Checked> inspectForCheck(UUID id) throws IOException {
		var store = stores.getIfAvailable();
		if (store == null)
			return java.util.Optional.empty();
		Lease lease = transaction.execute(tx -> {
			var found = repository.job(id,false).orElse(null);
			if (found == null || !"CONVERTING".equals(found.phase()))
				return null;
			repository.lockTarget(found);
			var job = repository.job(id,true).orElseThrow();
			if (!"CONVERTING".equals(job.phase()) || lifecycle.taskExists(job.task()))
				return null;
			var source = repository.wav(job.wav()).orElse(null);
			return source == null ? null : new Lease(id,job.token(),job.task(),job.file(),repository.textHash(id),source);
		});
		return lease == null ? java.util.Optional.empty() : store.recover(lease).map(result -> new Checked(lease,result));
	}

	void finishChecked(Checked checked) {
		if (!org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive())
			throw new IllegalStateException("LOCAL_CHECK_REQUIRES_RECEIPT_TRANSACTION");
		if (!lifecycle.taskExists(checked.lease().task()))
			finish(checked.lease(),checked.result());
	}

	private boolean current(Lease lease) {
		var found = repository.job(lease.job(), false).orElse(null);
		if (found == null)
			return false;
		repository.lockTarget(found);
		var job = repository.job(lease.job(), true).orElseThrow();
		return "CONVERTING".equals(job.phase()) && lease.task().equals(job.task()) && lease.token().equals(job.token());
	}

	private void fail(Lease lease, String code) {
		if (!current(lease))
			return;
		var job = repository.job(lease.job(), true).orElseThrow();
		if (!job.valid() || !repository.matchesTarget(job.id()))
			repository.discard(job.id(), now());
		else
			repository.fail(job.id(), job.text(), code, now());
	}

	private void finish(Lease lease, Mp3ResultStore.Staged result) {
		if (!current(lease))
			return;
		var job = repository.job(lease.job(), true).orElseThrow();
		if (!job.valid() || !repository.matchesTarget(job.id())) {
			repository.discard(job.id(), now());
			return;
		}
		if (!lease.job().equals(result.jobId()) || !lease.file().equals(result.fileId()) || !lease.token().equals(result.token())
				|| !lease.source().id().equals(result.wavFileId()) || !lease.hash().equals(result.textHash()))
			throw new IllegalArgumentException("LOCAL_RESULT_MISMATCH");
		var locked = new java.util.HashMap<UUID, vn.hueqr.backend.entity.FileAsset>();
		for (UUID id : java.util.stream.Stream.of(lease.source().id(), result.fileId()).sorted().toList())
			locked.put(id, files.findByIdForUpdate(id).orElseThrow());
		var wav = locked.get(lease.source().id());
		var mp3 = locked.get(result.fileId());
		if (!"READY".equals(wav.getState()) || !"WAV".equals(wav.getFormat()) || !"READY".equals(mp3.getState())
				|| !"MP3".equals(mp3.getFormat()) || !result.checksum().equals(HexFormat.of().formatHex(mp3.getChecksum()))
				|| !result.storageKey().equals(mp3.getStorageKey()) || result.byteSize() != mp3.getByteSize()
				|| result.duration().compareTo(mp3.getDuration()) != 0)
			throw new IllegalArgumentException("LOCAL_RESULT_MISMATCH");
		wav.retainReference();
		mp3.retainReference();
		repository.succeed(job.id(), UUID.randomUUID(), job.text(), wav.getId(), mp3.getId(), now());
	}

	private java.time.Instant now() {
		return clock.instant().truncatedTo(ChronoUnit.MICROS);
	}
}
