package vn.hueqr.backend.controller;

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
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminRegionRevisionResponse;
import vn.hueqr.backend.dto.ApplyRegionRequest;
import vn.hueqr.backend.dto.SaveRegionRequest;
import vn.hueqr.backend.service.AdminRegionService;
import vn.hueqr.backend.service.AdminSessionService;

@RestController
@RequestMapping("/api/v1/admin/places/{placeId}")
public class AdminRegionController {
	private final AdminRegionService regions;
	private final AdminSessionService sessions;

	public AdminRegionController(AdminRegionService regions, AdminSessionService sessions) {
		this.regions = regions;
		this.sessions = sessions;
	}

	@GetMapping("/region-revisions/{revisionId}")
	public ResponseEntity<AdminRegionRevisionResponse> detail(@PathVariable String placeId, @PathVariable String revisionId,
			HttpServletRequest request) {
		sessions.current(request);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(regions.detail(placeId, revisionId));
	}

	@PostMapping("/region-revisions")
	public ResponseEntity<AdminOperationResponse> save(@PathVariable String placeId, @RequestBody SaveRegionRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "If-Match", required = false) String ifMatch, HttpServletRequest request) {
		var account = sessions.activity(request, "SAVE");
		var result = regions.save(account.accountId(), key, placeId, ifMatch, input);
		return operation(result);
	}

	@PutMapping("/active-region")
	public ResponseEntity<AdminOperationResponse> apply(@PathVariable String placeId, @RequestBody ApplyRegionRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "If-Match", required = false) String ifMatch, HttpServletRequest request) {
		var account = sessions.activity(request, "SAVE");
		var result = regions.apply(account.accountId(), key, placeId, ifMatch, input);
		return operation(result);
	}

	private ResponseEntity<AdminOperationResponse> operation(AdminOperationResponse result) {
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore())
				.eTag("\"" + result.result().get("version") + "\"").body(result);
	}
}
