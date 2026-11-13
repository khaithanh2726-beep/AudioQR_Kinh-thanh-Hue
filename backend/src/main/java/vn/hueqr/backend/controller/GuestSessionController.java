package vn.hueqr.backend.controller;

import java.util.UUID;
import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hueqr.backend.dto.SessionResponse;
import vn.hueqr.backend.dto.StartRequest;
import vn.hueqr.backend.dto.StartTicketResponse;
import vn.hueqr.backend.service.GuestSessionService;
import vn.hueqr.backend.service.GuestSessionException;
import vn.hueqr.backend.service.StartTicketService;

@RestController
@RequestMapping("/api/v1/guest")
public class GuestSessionController {
	private final StartTicketService tickets;
	private final GuestSessionService sessions;

	public GuestSessionController(StartTicketService tickets, GuestSessionService sessions) {
		this.tickets = tickets;
		this.sessions = sessions;
	}

	@PostMapping("/start-ticket")
	public ResponseEntity<StartTicketResponse> ticket(
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential,
			@RequestBody(required = false) byte[] body) {
		if (body != null && body.length > 0)
			throw new GuestSessionException("VALIDATION_FAILED", Map.of("body", "Yêu cầu cấp vé phải có body rỗng."));

		return ResponseEntity.status(201).cacheControl(CacheControl.noStore()).body(tickets.issue(credential));
	}

	@PostMapping("/sessions")
	public ResponseEntity<SessionResponse> start(@RequestBody StartRequest request,
			@RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential) {
		SessionResponse result = sessions.start(request, key, credential);
		return ResponseEntity.status(result.created() ? 201 : 200).cacheControl(CacheControl.noStore()).body(result);
	}

	@GetMapping("/sessions/{id}")
	public ResponseEntity<SessionResponse> status(@PathVariable UUID id,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(sessions.status(id, credential));
	}

	@PostMapping("/sessions/{id}/end")
	public ResponseEntity<SessionResponse> end(@PathVariable UUID id,
			@RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential,
			@RequestBody(required = false) byte[] body) {
		if (body != null && body.length > 0)
			throw new GuestSessionException("VALIDATION_FAILED",
					Map.of("body", "Yêu cầu kết thúc phiên phải có body rỗng."));

		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(sessions.end(id, key, credential));
	}
}

