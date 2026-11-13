package vn.hueqr.backend.service;

import java.security.MessageDigest;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.CreateTtsRequest;
import vn.hueqr.backend.dto.TtsJobResponse;
import vn.hueqr.backend.entity.TextRevision;
import vn.hueqr.backend.entity.TtsJob;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TtsJobRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;

@Service
public class TtsJobService {
	public static final String MODEL = "gemini-3.8-flash-tts";
	public static final String VOICE = "Charon";
	private final TextRevisionRepository texts;
	private final PlaceRepository places;
	private final TtsJobRepository jobs;
	private final ContentTextPolicy input;
	private final AdminCommandService commands;
	private final ObjectMapper json;
	private final Clock clock;
	private final vn.hueqr.backend.repository.AudioRevisionRepository audios;
	private final vn.hueqr.backend.repository.FileAssetRepository files;

	public TtsJobService(TextRevisionRepository texts, PlaceRepository places, TtsJobRepository jobs,
			ContentTextPolicy input, AdminCommandService commands, ObjectMapper json, Clock clock,
			vn.hueqr.backend.repository.AudioRevisionRepository audios, vn.hueqr.backend.repository.FileAssetRepository files) {
		this.texts = texts;
		this.places = places;
		this.jobs = jobs;
		this.input = input;
		this.commands = commands;
		this.json = json;
		this.clock = clock;
		this.audios = audios;
		this.files = files;
	}

	public AdminOperationResponse accept(UUID accountId, String key, String textId, CreateTtsRequest request) {
		UUID tid = input.textId(textId, "textRevisionId");
		var command = new Command("POST", "/api/v1/admin/text-revisions/" + tid + "/tts-jobs",
				json.writeValueAsString(request), null, "CREATE_TTS_JOB", 202, "TTS_JOB_ACCEPTED");
		return commands.execute(accountId, key, command, () -> {
			UUID pid = texts.findPlaceIdById(tid).orElseThrow(() -> new AdminCommandException(404, "TEXT_REVISION_NOT_FOUND"));
			places.findByIdForUpdate(pid).orElseThrow(() -> new AdminCommandException(409, "TARGET_DELETED"));
			// Chỉ tra Id chủ sở hữu trước khóa PLACE để chưa nạp Entity text với con trỏ cũ vào persistence context
			TextRevision locked = lockFreshText(tid);
			validateHash(request.expectedTextHash(), locked.getContentHash());
			if (locked.getActiveTtsJobId() != null) {
				TtsJob existing = jobs.findByIdForUpdate(locked.getActiveTtsJobId()).orElseThrow();
				return summary(existing, true);
			}
			// Sau lỗi xác định, Tạo không ngầm thử provider lại, Admin phải dùng thao tác Retry có phiên bản
			if (locked.getLatestTtsJobKey() != null) {
				var previous = jobs.findByIdForUpdate(locked.getLatestTtsJobKey());
				if (previous.isPresent())
					return summary(previous.get(), true);
			}

			// Cắt đến micro giây để mốc job và receipt khớp DATETIME(6), chưa dispatch trong giao dịch này
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			TtsJob job = jobs.saveAndFlush(new TtsJob(UUID.randomUUID(), tid, pid, locked.getSlot().getLanguage(),
					locked.getContentHash(), MODEL, VOICE, accountId, UUID.fromString(key.substring(0, 36)),
					UUID.fromString(key.substring(37)), now));
			locked.trackTtsJob(job.getId());
			texts.flush();
			return summary(job, false);
		});
	}

	@Transactional(readOnly = true)
	public TtsJobResponse detail(String jobId) {
		UUID id = jobId(jobId);
		return response(jobs.findById(id).orElseThrow(() -> new AdminCommandException(404, "TTS_JOB_NOT_FOUND")));
	}

	public TtsJobResponse response(TtsJob job) {
		boolean uncertain = "UNCERTAIN".equals(job.getPhase());
		String state = switch (job.getPhase()) {
			case "SUCCEEDED" -> "SUCCEEDED";
			case "FAILED" -> "FAILED";
			case "DISCARDED" -> "DISCARDED";
			case "UNCERTAIN" -> "UNCERTAIN";
			default -> "PROCESSING";
		};
		TtsJobResponse.Result result = null;
		if ("SUCCEEDED".equals(state)) {
			var audio = audios.findByJobId(job.getId()).orElse(null);
			if (audio != null) {
				var mp3 = files.findById(audio.getMp3FileId()).orElseThrow();
				result = new TtsJobResponse.Result(audio.getId(), audio.getWavFileId(), audio.getMp3FileId(), HexFormat.of().formatHex(mp3.getChecksum()), mp3.getDuration());
			}
		}
		String retry = uncertain ? "INSPECT_ONLY" : "FAILED".equals(state)
				? ("LOCAL_SOURCE_INVALID".equals(job.getFailureCode()) ? "INSPECT_ONLY"
						: job.getSourceWavFileId() == null ? "CREATE_PROVIDER_ATTEMPT" : "REPROCESS_LOCAL") : null;
		return new TtsJobResponse(job.getId(), job.getTextRevisionKeySnapshot(), HexFormat.of().formatHex(job.getTextHashSnapshot()),
				job.getLanguageSnapshot(), job.getModel(), job.getVoice(), state,
				job.getPhase(), job.isTargetValid(), uncertain, job.getAcceptedAt(), job.getUpdatedAt(), job.getPhaseVersion(),
				job.getTerminalAt(), job.getSourceWavFileId(), job.getFailureCode(), retry, result);
	}

	private UUID jobId(String value) {
		try {
			UUID id = UUID.fromString(value);
			if (!id.toString().equals(value))
				throw new IllegalArgumentException();
			return id;
		}
		catch (IllegalArgumentException | NullPointerException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("jobId", "Cần mã tác vụ UUID chuẩn chữ thường."));
		}
	}

	private void validateHash(String hash, byte[] expected) {
		if (hash == null || !hash.matches("[0-9a-f]{64}"))
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("expectedTextHash", "Cần mã Hash SHA-256 gồm 64 ký tự hex thường."));
		if (!MessageDigest.isEqual(HexFormat.of().parseHex(hash), expected))
			throw new AdminCommandException(409, "WRONG_REVISION");
	}

	private TextRevision lockFreshText(UUID tid) {
		return texts.findByIdForUpdate(tid).orElseThrow(() -> new AdminCommandException(409, "TARGET_DELETED"));
	}

	private Map<String, Object> summary(TtsJob job, boolean reused) {
		return Map.of("jobId", job.getId().toString(), "textRevisionId", job.getTextRevisionKeySnapshot().toString(),
				"phase", job.getPhase(), "reused", reused, "acceptedAt", job.getAcceptedAt().toString());
	}
}
