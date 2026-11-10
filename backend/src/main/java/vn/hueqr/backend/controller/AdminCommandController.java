package vn.hueqr.backend.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminOperationLookupResponse;
import vn.hueqr.backend.service.AdminCommandException;
import vn.hueqr.backend.service.AdminCommandService;
import vn.hueqr.backend.service.AdminSessionService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminCommandController {
	private final AdminCommandService commands;
	private final AdminSessionService sessions;

	public AdminCommandController(AdminCommandService commands, AdminSessionService sessions) {
		this.commands = commands;
		this.sessions = sessions;
	}

	@PostMapping("/command-windows")
	public ResponseEntity<AdminCommandWindowResponse> issue(@RequestBody(required = false) byte[] body, HttpServletRequest request) {
		if (body != null && body.length > 0)
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("body", "Yêu cầu cấp cửa sổ phải có body rỗng."));
		return ResponseEntity.status(201).cacheControl(CacheControl.noStore())
				.body(commands.issue(sessions.current(request).accountId()));
	}

	@GetMapping("/operations/{key}")
	public ResponseEntity<AdminOperationLookupResponse> lookup(@PathVariable String key, HttpServletRequest request) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore())
				.body(commands.lookup(sessions.current(request).accountId(), key));
	}
}
