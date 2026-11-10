package vn.hueqr.backend.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hueqr.backend.dto.*;
import vn.hueqr.backend.service.GuestAudioService;
import vn.hueqr.backend.service.GuestContentService;

@RestController
@RequestMapping("/api/v1/guest/sessions/{sid}")
public class GuestContentController {
	private final GuestContentService guests;
	private final GuestAudioService audio;

	public GuestContentController(GuestContentService guests, GuestAudioService audio) {
		this.guests = guests;
		this.audio = audio;
	}

	@PostMapping("/resume")
	public ResponseEntity<GuestResumeResponse> resume(@PathVariable UUID sid,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential, @RequestBody GuestResumeRequest request) {
		return ok(guests.resume(sid, credential, request));
	}

	@GetMapping("/map")
	public ResponseEntity<GuestMapResponse> map(@PathVariable UUID sid,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential) {
		return ok(guests.map(sid, credential));
	}

	@PostMapping("/openings")
	public ResponseEntity<GuestContentResponse> select(@PathVariable UUID sid,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, @RequestBody GuestSelectionRequest request) {
		return content(guests.select(sid, credential, key, request));
	}

	@PutMapping("/openings/{oid}/language")
	public ResponseEntity<GuestContentResponse> language(@PathVariable UUID sid, @PathVariable UUID oid,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential,
			@RequestHeader(value = "Idempotency-Key", required = false) String key,
			@RequestHeader(value = "If-Match", required = false) String tag, @RequestBody GuestLanguageRequest request) {
		return content(guests.language(sid, oid, credential, key, tag, request));
	}

	@PostMapping("/openings/{oid}/view-ack")
	public ResponseEntity<GuestViewAckResponse> ack(@PathVariable UUID sid, @PathVariable UUID oid,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential, @RequestBody GuestViewAckRequest request) {
		var result = guests.ack(sid, oid, credential, request);
		return ResponseEntity.status(result.created() ? 201 : 200).cacheControl(CacheControl.noStore()).body(result);
	}

	@PostMapping("/openings/{oid}/leave")
	public ResponseEntity<Map<String, Object>> leave(@PathVariable UUID sid, @PathVariable UUID oid,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential,
			@RequestHeader(value = "Idempotency-Key", required = false) String key, @RequestBody GuestLeaveRequest request) {
		return ok(guests.leave(sid, oid, credential, key, request));
	}

	@GetMapping("/content-grants/{gid}/audio")
	public ResponseEntity<Resource> download(@PathVariable UUID sid, @PathVariable UUID gid,
			@RequestHeader(value = "X-Guest-Credential", required = false) String credential,
			@RequestHeader(value = "Range", required = false) String range) {
		var result = audio.download(sid, gid, credential, range);
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).header("Content-Type", "audio/mpeg")
				.header("X-Audio-Checksum", result.checksum()).header("X-Content-Type-Options", "nosniff")
				.contentLength(result.byteSize()).body(result.resource());
	}

	private static ResponseEntity<GuestContentResponse> content(GuestContentResponse result) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore())
				.eTag("\"opening:" + result.openingId() + ":" + result.openingVersion() + "\"").body(result);
	}

	private static <T> ResponseEntity<T> ok(T body) {
		return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(body);
	}
}
