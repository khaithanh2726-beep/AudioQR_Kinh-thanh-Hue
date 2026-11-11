package vn.hueqr.backend.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.hueqr.backend.repository.LocalAudioRepository;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "hueqr.tts.local.enabled", havingValue = "true")
public class LocalAudioRunner {
	private final LocalAudioRepository jobs;
	private final LocalAudioService service;

	public LocalAudioRunner(LocalAudioRepository jobs, LocalAudioService service) {
		this.jobs = jobs;
		this.service = service;
	}

	@Scheduled(fixedDelayString = "${hueqr.tts.local.poll-delay-ms:1000}")
	public void tick() {
		// Chỉ nhận WAV đã stage hoặc đối chiếu local bị gián đoạn, không tự thử lại lỗi/provider
		for (var job : jobs.pending(20))
			service.run(job);
	}
}
