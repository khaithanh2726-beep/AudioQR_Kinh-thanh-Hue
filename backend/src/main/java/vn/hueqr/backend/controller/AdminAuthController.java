package vn.hueqr.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminActivityRequest;
import vn.hueqr.backend.dto.AdminCsrfResponse;
import vn.hueqr.backend.dto.AdminLoginRequest;
import vn.hueqr.backend.dto.AdminSessionResponse;
import vn.hueqr.backend.service.AdminAuthService;
import vn.hueqr.backend.service.AdminSessionService;

@RestController
@RequestMapping("/api/v1")
public class AdminAuthController {
	private final AdminAuthService authentication;
	private final AdminSessionService sessions;

	public AdminAuthController(AdminAuthService authentication, AdminSessionService sessions) {
		this.authentication = authentication;
		this.sessions = sessions;
	}

	@GetMapping("/auth/csrf")
	public ResponseEntity<AdminCsrfResponse> csrf(CsrfToken token) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore())
				.body(new AdminCsrfResponse(token.getHeaderName(), token.getToken()));
	}

	@PostMapping("/auth/login")
	public ResponseEntity<AdminSessionResponse> login(@RequestBody AdminLoginRequest input,
			HttpServletRequest request, HttpServletResponse response) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(authentication.login(input, request, response));
	}

	@GetMapping("/admin/session")
	public ResponseEntity<AdminSessionResponse> session(HttpServletRequest request) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(sessions.current(request));
	}

	@PostMapping("/admin/session/activity")
	public ResponseEntity<AdminSessionResponse> activity(@RequestBody AdminActivityRequest activity, HttpServletRequest request) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore())
				.body(sessions.activity(request, activity.activityType()));
	}

	@PostMapping("/auth/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		authentication.logout(request, response);
		return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
	}
}
