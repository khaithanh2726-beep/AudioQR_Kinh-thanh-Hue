package vn.hueqr.backend.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.hueqr.backend.repository.TtsJobRepository;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "hueqr.tts.enabled", havingValue = "true")
public class TtsQueueRunner {
	private final TtsJobRepository jobs;
	private final TtsWorker worker;

	public TtsQueueRunner(TtsJobRepository jobs, TtsWorker worker) {
		this.jobs = jobs;
		this.worker = worker;
	}

	@Scheduled(fixedDelayString = "${hueqr.tts.poll-delay-ms:1000}")
	public void tick() {
		// Chỉ nhận QUEUED, không tự retry job FAILED hoặc DISPATCHING/UNCERTAIN còn chưa đối chiếu
		jobs.findFirstByPhaseAndTargetValidTrueOrderByAcceptedAtAscIdAsc("QUEUED").ifPresent(job -> worker.run(job.getId()));
	}
}
