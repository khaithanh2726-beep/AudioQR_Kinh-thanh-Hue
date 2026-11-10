package vn.hueqr.backend.service;

import java.io.IOException;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.RetryTtsRequest;
import vn.hueqr.backend.entity.TtsJob;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TtsJobRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;
import tools.jackson.databind.ObjectMapper;

@Service
public class TtsRecoveryService {
	private final TtsJobRepository jobs;
	private final TextRevisionRepository texts;
	private final PlaceRepository places;
	private final AdminCommandService commands;
	private final ContentTextPolicy input;
	private final java.time.Clock clock;
	private final ObjectMapper json;
	private final ObjectProvider<WavResultStore> stores;
	private final TtsResultService results;
	private final RuntimeOwnerService owners;

	public TtsRecoveryService(TtsJobRepository jobs, TextRevisionRepository texts, PlaceRepository places,
			AdminCommandService commands, ContentTextPolicy input, java.time.Clock clock, ObjectMapper json,
			ObjectProvider<WavResultStore> stores, TtsResultService results, RuntimeOwnerService owners) {
		this.jobs = jobs;
		this.texts = texts;
		this.places = places;
		this.commands = commands;
		this.input = input;
		this.clock = clock;
		this.json = json;
		this.stores = stores;
		this.results = results;
		this.owners = owners;
	}

	public AdminOperationResponse retry(UUID account, String key, String jobId, RetryTtsRequest request) {
		UUID id = input.textId(jobId, "jobId");
		var command = new Command("POST", "/api/v1/admin/tts-jobs/" + id + "/retry", json.writeValueAsString(request),
				null, "RETRY_TTS_JOB", 202, "TTS_JOB_ACCEPTED");
		return commands.execute(account, key, command, () -> {
			var target = jobs.findTarget(id).orElseThrow(() -> new AdminCommandException(404, "TTS_JOB_NOT_FOUND"));
			if (target.getTargetTextRevisionId() == null || places.findByIdForUpdate(target.getPlaceKeySnapshot()).isEmpty())
				throw new AdminCommandException(409, "TARGET_DELETED");
			var text = texts.findByIdForUpdate(target.getTargetTextRevisionId()).orElseThrow(() -> new AdminCommandException(409, "TARGET_DELETED"));
			var previous = jobs.findByIdForUpdate(id).orElseThrow();
			if (request.expectedPhaseVersion() == null || request.expectedPhaseVersion() < 0)
				throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("expectedPhaseVersion", "Cần phiên bản trạng thái đang xem."));
			if (previous.getPhaseVersion().longValue() != request.expectedPhaseVersion())
				throw new AdminCommandException(412, "VERSION_CONFLICT");
			if (!previous.isTargetValid() || !id.equals(text.getLatestTtsJobKey()))
				throw new AdminCommandException(409, "WRONG_REVISION");
			if (!"FAILED".equals(previous.getPhase()) || previous.getFailureCode() == null || text.getActiveTtsJobId() != null)
				throw new AdminCommandException(409, "TTS_RETRY_NOT_ALLOWED");
			var now = clock.instant().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
			var next = new TtsJob(UUID.randomUUID(), text.getId(), previous.getPlaceKeySnapshot(), previous.getLanguageSnapshot(),
					previous.getTextHashSnapshot(), previous.getModel(), previous.getVoice(), account,
					UUID.fromString(key.substring(0, 36)), UUID.fromString(key.substring(37)), now);
			next.setRetryOfJobId(id);
			jobs.saveAndFlush(next);
			text.trackTtsJob(next.getId());
			texts.flush();
			return Map.of("jobId", next.getId().toString(), "retryOfJobId", id.toString(), "phase", "QUEUED", "acceptedAt", now.toString());
		});
	}

	public AdminOperationResponse check(UUID account, String key, String jobId) {
		UUID id = input.textId(jobId, "jobId");
		// Chỉ đọc bytes bên ngoài TX, việc gắn file vào job và receipt được commit cùng giao dịch sau đó
		var snapshot = jobs.findById(id).orElseThrow(() -> new AdminCommandException(404, "TTS_JOB_NOT_FOUND"));
		WavResultStore.Staged evidence = null;
		String inspection = "NO_LOCAL_EVIDENCE";
		WavResultStore store = stores.getIfAvailable();
		if (snapshot.getWorkerToken() != null && store != null && java.util.Set.of("DISPATCHING", "UNCERTAIN").contains(snapshot.getPhase())) {
			try (var task = owners.begin("RECOVER", id)) {
				evidence = store.recover(id, snapshot.getWorkerToken(), HexFormat.of().formatHex(snapshot.getTextHashSnapshot())).orElse(null);
				if (evidence != null)
					inspection = "LOCAL_WAV_VERIFIED";
			}
			catch (IOException | RuntimeException error) {
				inspection = "LOCAL_EVIDENCE_INVALID";
			}
		}
		final var staged = evidence;
		final var finding = inspection;
		var command = new Command("POST", "/api/v1/admin/tts-jobs/" + id + "/check", "{}", null, "CHECK_TTS_JOB", 200, "TTS_JOB_CHECKED");
		return commands.execute(account, key, command, () -> {
			if (staged != null)
				results.stageInCurrentTransaction(staged);
			var job = jobs.findByIdForUpdate(id).orElseThrow(() -> new AdminCommandException(404, "TTS_JOB_NOT_FOUND"));
			return Map.of("jobId", id.toString(), "phase", job.getPhase(), "inspection", finding,
					"phaseVersion", job.getPhaseVersion(), "outcomeUncertain", "UNCERTAIN".equals(job.getPhase()));
		});
	}
}
