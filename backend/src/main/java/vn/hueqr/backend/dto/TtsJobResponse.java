package vn.hueqr.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record TtsJobResponse(UUID jobId, UUID textRevisionId, String textHash, String language, String model,
		String voice, String state, String phase, boolean targetValid, boolean outcomeUncertain,
		Instant acceptedAt, Instant updatedAt, long phaseVersion, Instant terminalAt, UUID sourceWavFileId,
		String failureCode, String retryAction) {
}
