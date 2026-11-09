package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.entity.FileAsset;
import vn.hueqr.backend.entity.TtsJob;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TtsJobRepository;

@Service
public class TtsResultService {
	private static final Set<String> DEFINITE_FAILURES = Set.of("PROVIDER_REJECTED", "PROVIDER_AUTH_FAILED", "PROVIDER_QUOTA",
			"PROVIDER_OUTPUT_INVALID", "PROVIDER_REQUEST_INVALID");
	private final TtsJobRepository jobs;
	private final TextRevisionRepository texts;
	private final PlaceRepository places;
	private final FileAssetRepository files;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public TtsResultService(TtsJobRepository jobs, TextRevisionRepository texts, PlaceRepository places,
			FileAssetRepository files, Clock clock, PlatformTransactionManager manager) {
		this.jobs = jobs;
		this.texts = texts;
		this.places = places;
		this.files = files;
		this.clock = clock;
		this.transaction = new TransactionTemplate(manager);
		this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public boolean stage(WavResultStore.Staged staged) {
		return Boolean.TRUE.equals(transaction.execute(status -> stageInCurrentTransaction(staged)));
	}

	boolean stageInCurrentTransaction(WavResultStore.Staged staged) {
		var target = jobs.findTarget(staged.jobId()).orElse(null);
		if (target == null)
			return false;
		boolean placeExists = places.findByIdForUpdate(target.getPlaceKeySnapshot()).isPresent();
		var text = !placeExists || target.getTargetTextRevisionId() == null ? null
				: texts.findByIdForUpdate(target.getTargetTextRevisionId()).orElse(null);
		TtsJob job = jobs.findByIdForUpdate(staged.jobId()).orElseThrow();
		if (!Objects.equals(job.getWorkerToken(), staged.workerToken())
				|| !HexFormat.of().formatHex(job.getTextHashSnapshot()).equals(staged.textHash()))
			return false;
		if ("RESULT_STAGED".equals(job.getPhase()))
			return staged.fileId().equals(job.getSourceWavFileId());
		if (!Set.of("DISPATCHING", "UNCERTAIN").contains(job.getPhase()))
			return false;
		var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
		if (text == null || !job.isTargetValid() || !job.getId().equals(text.getActiveTtsJobId())) {
			job.discard(now);
			if (text != null)
				text.clearActiveTtsJob(job.getId());
			return false;
		}
		files.saveAndFlush(new FileAsset(staged.fileId(), staged.storageKey(), HexFormat.of().parseHex(staged.checksum()),
				staged.byteSize(), staged.duration(), now));
		job.stage(staged.fileId(), now);
		jobs.flush();
		return true;
	}

	public boolean fail(UUID id, UUID token, long version, String code) {
		if (!DEFINITE_FAILURES.contains(code))
			throw new IllegalArgumentException("UNSUPPORTED_FAILURE_CODE");
		return Boolean.TRUE.equals(transaction.execute(status -> {
			var target = jobs.findTarget(id).orElse(null);
			if (target == null)
				return false;
			boolean placeExists = places.findByIdForUpdate(target.getPlaceKeySnapshot()).isPresent();
			var text = !placeExists || target.getTargetTextRevisionId() == null ? null
					: texts.findByIdForUpdate(target.getTargetTextRevisionId()).orElse(null);
			var job = jobs.findByIdForUpdate(id).orElseThrow();
			if (!"DISPATCHING".equals(job.getPhase()) || !Objects.equals(token, job.getWorkerToken()) || job.getPhaseVersion() != version)
				return false;
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			if (text == null || !job.isTargetValid())
				job.discard(now);
			else
				job.fail(code, now);
			if (text != null)
				text.clearActiveTtsJob(id);
			jobs.flush();
			return true;
		}));
	}
}
