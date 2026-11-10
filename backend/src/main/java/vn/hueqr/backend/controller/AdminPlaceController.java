package vn.hueqr.backend.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminPlaceResponse;
import vn.hueqr.backend.dto.CreatePlaceRequest;
import vn.hueqr.backend.dto.UpdatePlaceRequest;
import vn.hueqr.backend.dto.SetPlaceVisibilityRequest;
import vn.hueqr.backend.service.AdminPlaceService;
import vn.hueqr.backend.service.AdminSessionService;
import vn.hueqr.backend.service.AdminVisibilityService;

@RestController
@RequestMapping("/api/v1/admin/places")
public class AdminPlaceController {
	private final AdminPlaceService places;
	private final AdminSessionService sessions;
	private final AdminVisibilityService visibility;

	public AdminPlaceController(AdminPlaceService places, AdminSessionService sessions, AdminVisibilityService visibility) {
		this.places = places;
		this.sessions = sessions;
		this.visibility = visibility;
	}

	@GetMapping
	public ResponseEntity<List<AdminPlaceResponse>> list(HttpServletRequest request) {
		sessions.current(request);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(places.list());
	}

	@GetMapping("/{placeId}")
	public ResponseEntity<AdminPlaceResponse> detail(@PathVariable String placeId, HttpServletRequest request) {
		sessions.current(request);
		var place = places.detail(placeId);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).eTag("\"" + place.version() + "\"").body(place);
	}

	@PostMapping
	public ResponseEntity<AdminOperationResponse> create(@RequestBody CreatePlaceRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, HttpServletRequest request) {
		// Bấm Lưu trong phiên còn hạn được tính Q3, kể cả khi dữ liệu cần sửa hoặc window đã hết hạn
		var account = sessions.activity(request, "SAVE");
		var result = places.create(account.accountId(), key, input);
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore()).body(result);
	}

	@PutMapping("/{placeId}/visibility")
	public ResponseEntity<AdminOperationResponse> visibility(@PathVariable String placeId, @RequestBody SetPlaceVisibilityRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, @RequestHeader(value = "If-Match", required = false) String ifMatch,
			HttpServletRequest request) {
		var account = sessions.activity(request, "VISIBILITY");
		var result = visibility.set(account.accountId(), key, placeId, ifMatch, input);
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore()).eTag("\"" + result.result().get("version") + "\"").body(result);
	}

	@PatchMapping("/{placeId}")
	public ResponseEntity<AdminOperationResponse> rename(@PathVariable String placeId, @RequestBody UpdatePlaceRequest input,
			@RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "If-Match", required = false) String ifMatch, HttpServletRequest request) {
		var account = sessions.activity(request, "SAVE");
		var result = places.rename(account.accountId(), key, placeId, ifMatch, input);
		return ResponseEntity.status(result.httpStatus()).cacheControl(CacheControl.noStore())
				.eTag("\"" + result.result().get("version") + "\"").body(result);
	}
}
