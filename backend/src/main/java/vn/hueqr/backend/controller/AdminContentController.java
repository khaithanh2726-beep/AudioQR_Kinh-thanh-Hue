package vn.hueqr.backend.controller;

import vn.hueqr.backend.dto.AdminContentCompletenessResponse;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminContentResponse;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminTextReviewReferenceResponse;
import vn.hueqr.backend.dto.SaveTextRevisionRequest;
import vn.hueqr.backend.dto.SaveTextReviewReferenceRequest;
import vn.hueqr.backend.service.AdminContentService;
import vn.hueqr.backend.service.AdminSessionService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminContentController {
	private final AdminContentService content;
	private final AdminSessionService sessions;

	public AdminContentController(AdminContentService content, AdminSessionService sessions) {
		this.content = content;
		this.sessions = sessions;
	}

	@GetMapping("/places/{placeId}/content/{language}")
	public ResponseEntity<AdminContentResponse> detail(@PathVariable String placeId, @PathVariable String language,
			HttpServletRequest request) {
		sessions.current(request);
		var result = content.detail(placeId, language);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).eTag(result.etag()).body(result);
	}

	@GetMapping("/content-completeness")
	public ResponseEntity<List<AdminContentCompletenessResponse>> completeness(HttpServletRequest request) {
		sessions.current(request);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(content.completeness());
	}

	@PostMapping("/places/{placeId}/content/{language}/text-revisions")
	public ResponseEntity<AdminOperationResponse> save(@PathVariable String placeId, @PathVariable String language,
			@RequestBody SaveTextRevisionRequest input, @RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "If-Match", required = false) String ifMatch, HttpServletRequest request) {
		var account = sessions.activity(request, "SAVE");
		return operation(content.save(account.accountId(), key, placeId, language, ifMatch, input));
	}

	@GetMapping("/text-revisions/{textId}/review-reference")
	public ResponseEntity<AdminTextReviewReferenceResponse> review(@PathVariable String textId, HttpServletRequest request) {
		sessions.current(request);
		var result = content.review(textId);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).eTag(result.etag()).body(result);
	}

	@PutMapping("/text-revisions/{textId}/review-reference")
	public ResponseEntity<AdminOperationResponse> saveReview(@PathVariable String textId,
			@RequestBody SaveTextReviewReferenceRequest input, @RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "If-Match", required = false) String ifMatch, HttpServletRequest request) {
		var account = sessions.activity(request, "SAVE");
		return operation(content.saveReview(account.accountId(), key, textId, ifMatch, input));
	}

	private ResponseEntity<AdminOperationResponse> operation(AdminOperationResponse result) {
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore())
				.eTag(result.result().get("etag").toString()).body(result);
	}
}
