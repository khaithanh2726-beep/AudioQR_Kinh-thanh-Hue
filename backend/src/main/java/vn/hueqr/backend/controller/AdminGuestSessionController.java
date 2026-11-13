package vn.hueqr.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminGuestSessionListResponse;
import vn.hueqr.backend.dto.AdminGuestSessionResponse;
import vn.hueqr.backend.service.AdminGuestSessionService;
import vn.hueqr.backend.service.AdminSessionService;

@RestController
@RequestMapping("/api/v1/admin/guest-sessions")
public class AdminGuestSessionController {
	private final AdminGuestSessionService guests;
	private final AdminSessionService sessions;

	public AdminGuestSessionController(AdminGuestSessionService guests, AdminSessionService sessions) {
		this.guests = guests;
		this.sessions = sessions;
	}

	@GetMapping
	public ResponseEntity<AdminGuestSessionListResponse> list(@RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate, @RequestParam(required = false) String status,
			@RequestParam(required = false) String searchNameOrId, HttpServletRequest request) {
		sessions.current(request);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(guests.list(fromDate, toDate, status, searchNameOrId));
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<AdminGuestSessionResponse> detail(@PathVariable String sessionId, HttpServletRequest request) {
		sessions.current(request);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(guests.detail(sessionId));
	}
}
