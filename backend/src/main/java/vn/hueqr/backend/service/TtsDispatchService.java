package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TtsJobRepository;

@Service
public class TtsDispatchService {
	public record DispatchLease(UUID jobId, UUID workerToken, long phaseVersion, String body,
			String language, String model, String voice) {
		@Override
		public String toString() {
			return "DispatchLease[redacted]";
		}
	}

	private final TtsJobRepository jobs;
	private final TextRevisionRepository texts;
	private final PlaceRepository places;
	private final Clock clock;
	private final TransactionTemplate transaction;

	public TtsDispatchService(TtsJobRepository jobs, TextRevisionRepository texts, PlaceRepository places,
			Clock clock, PlatformTransactionManager manager) {
		this.jobs = jobs;
		this.texts = texts;
		this.places = places;
		this.clock = clock;
		this.transaction = new TransactionTemplate(manager);
		this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	// Lease chỉ được trả sau commit marker, lời gọi provider sau này phải nằm ngoài giao dịch này
	public Optional<DispatchLease> claim(UUID jobId, UUID workerInstance) {
		Objects.requireNonNull(workerInstance);
		return transaction.execute(status -> {
			var target = jobs.findTarget(jobId).orElse(null);
			if (target == null || target.getTargetTextRevisionId() == null)
				return Optional.empty();
			if (places.findByIdForUpdate(target.getPlaceKeySnapshot()).isEmpty())
				return Optional.empty();
			var text = texts.findByIdForUpdate(target.getTargetTextRevisionId()).orElse(null);
			var job = jobs.findByIdForUpdate(jobId).orElse(null);
			if (text == null || job == null || !job.isTargetValid() || !"QUEUED".equals(job.getPhase())
					|| !jobId.equals(text.getActiveTtsJobId()) || !text.getId().equals(job.getTargetTextRevisionId()))
				return Optional.empty();
			UUID token = UUID.randomUUID();
			job.dispatch(workerInstance, token, clock.instant().truncatedTo(ChronoUnit.MICROS));
			jobs.flush();
			return Optional.of(new DispatchLease(jobId, token, job.getPhaseVersion(), text.getBody(),
					job.getLanguageSnapshot(), job.getModel(), job.getVoice()));
		});
	}

	// Thiếu kết quả sau marker không phải lỗi xác định, giữ job active để chặn gửi provider lần hai
	public boolean markUncertain(UUID jobId, UUID workerToken, long expectedVersion) {
		return Boolean.TRUE.equals(transaction.execute(status -> {
			var job = jobs.findByIdForUpdate(jobId).orElse(null);
			if (job == null || !"DISPATCHING".equals(job.getPhase()) || !Objects.equals(workerToken, job.getWorkerToken())
					|| job.getPhaseVersion() != expectedVersion)
				return false;
			job.markUncertain(clock.instant().truncatedTo(ChronoUnit.MICROS));
			jobs.flush();
			return true;
		}));
	}
}
