package vn.hueqr.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminAudioReviewReferenceResponse;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.ApplyPairRequest;
import vn.hueqr.backend.dto.SaveAudioReviewReferenceRequest;
import vn.hueqr.backend.service.AdminPairService;
import vn.hueqr.backend.service.AdminSessionService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminPairController {
	private final AdminPairService pairs;
	private final AdminSessionService sessions;

	public AdminPairController(AdminPairService pairs, AdminSessionService sessions) {
		this.pairs = pairs;
		this.sessions = sessions;
	}

	@GetMapping("/audio-revisions/{audioId}/review-reference")
	public ResponseEntity<AdminAudioReviewReferenceResponse> review(@PathVariable String audioId, HttpServletRequest request) {
		sessions.current(request);
		var result = pairs.review(audioId);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).eTag(result.etag()).body(result);
	}

	@PutMapping("/audio-revisions/{audioId}/review-reference")
	public ResponseEntity<AdminOperationResponse> saveReview(@PathVariable String audioId, @RequestBody SaveAudioReviewReferenceRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, @RequestHeader(value = "If-Match", required = false) String ifMatch,
			HttpServletRequest request) {
		var account = sessions.activity(request, "SAVE");
		return operation(pairs.saveReview(account.accountId(), key, audioId, ifMatch, input));
	}

	@PutMapping("/places/{placeId}/content/{language}/active-pair")
	public ResponseEntity<AdminOperationResponse> apply(@PathVariable String placeId, @PathVariable String language,
			@RequestBody ApplyPairRequest input, @RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "If-Match", required = false) String ifMatch, HttpServletRequest request) {
		var account = sessions.activity(request, "APPLY");
		return operation(pairs.apply(account.accountId(), key, placeId, language, ifMatch, input));
	}

	private ResponseEntity<AdminOperationResponse> operation(AdminOperationResponse result) {
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore()).eTag(result.result().get("etag").toString()).body(result);
	}
}
