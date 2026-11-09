package vn.hueqr.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.CreateTtsRequest;
import vn.hueqr.backend.dto.TtsJobResponse;
import vn.hueqr.backend.service.AdminSessionService;
import vn.hueqr.backend.service.TtsJobService;
import vn.hueqr.backend.service.TtsRecoveryService;
import vn.hueqr.backend.dto.RetryTtsRequest;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminTtsController {
	private final TtsJobService jobs;
	private final AdminSessionService sessions;
	private final TtsRecoveryService recovery;

	public AdminTtsController(TtsJobService jobs, AdminSessionService sessions, TtsRecoveryService recovery) {
		this.jobs = jobs;
		this.sessions = sessions;
		this.recovery = recovery;
	}

	@PostMapping("/text-revisions/{textId}/tts-jobs")
	public ResponseEntity<AdminOperationResponse> create(@PathVariable String textId, @RequestBody CreateTtsRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, HttpServletRequest request) {
		var account = sessions.activity(request, "TTS_CREATE");
		var result = jobs.accept(account.accountId(), key, textId, input);
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore()).body(result);
	}

	@GetMapping("/tts-jobs/{jobId}")
	public ResponseEntity<TtsJobResponse> detail(@PathVariable String jobId, HttpServletRequest request) {
		sessions.current(request);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(jobs.detail(jobId));
	}

	@PostMapping("/tts-jobs/{jobId}/check")
	public ResponseEntity<AdminOperationResponse> check(@PathVariable String jobId,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, HttpServletRequest request) {
		var account = sessions.activity(request, "TTS_CHECK");
		var result = recovery.check(account.accountId(), key, jobId);
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore()).body(result);
	}

	@PostMapping("/tts-jobs/{jobId}/retry")
	public ResponseEntity<AdminOperationResponse> retry(@PathVariable String jobId, @RequestBody RetryTtsRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, HttpServletRequest request) {
		var account = sessions.activity(request, "TTS_RETRY");
		var result = recovery.retry(account.accountId(), key, jobId, input);
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore()).body(result);
	}
}
