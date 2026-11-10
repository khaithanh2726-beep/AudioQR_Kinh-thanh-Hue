package vn.hueqr.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.*;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.repository.*;
import vn.hueqr.backend.repository.GuestContentRepository.Grant;
import vn.hueqr.backend.repository.GuestContentRepository.Opening;
import vn.hueqr.backend.repository.GuestContentRepository.Result;

@Service
public class GuestContentService {
	private final GuestSessionRepository sessions;
	private final GuestContentRepository content;
	private final PlaceRepository places;
	private final RegionRevisionRepository regions;
	private final ContentSlotRepository slots;
	private final ContentPairRepository pairs;
	private final TextRevisionRepository texts;
	private final AudioRevisionRepository audio;
	private final FileAssetRepository files;
	private final ContentPairReadiness readiness;
	private final GuestInputPolicy input;
	private final StartTicketService tickets;
	private final Clock clock;
	private final ObjectMapper json;
	private final TransactionTemplate transaction;

	public GuestContentService(GuestSessionRepository sessions, GuestContentRepository content,
			PlaceRepository places, RegionRevisionRepository regions, ContentSlotRepository slots,
			ContentPairRepository pairs, TextRevisionRepository texts, AudioRevisionRepository audio,
			FileAssetRepository files, ContentPairReadiness readiness, GuestInputPolicy input,
			StartTicketService tickets, Clock clock, ObjectMapper json, PlatformTransactionManager manager) {
		this.sessions = sessions;
		this.content = content;
		this.places = places;
		this.regions = regions;
		this.slots = slots;
		this.pairs = pairs;
		this.texts = texts;
		this.audio = audio;
		this.files = files;
		this.readiness = readiness;
		this.input = input;
		this.tickets = tickets;
		this.clock = clock;
		this.json = json;
		transaction = new TransactionTemplate(manager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public GuestResumeResponse resume(UUID sid, String credential, GuestResumeRequest request) {
		required(request.documentNonce(), "documentNonce");
		return transaction.execute(tx -> {
			GuestSession session = authenticate(sid, credential, false);
			if (!active(session))
				return new GuestResumeResponse(sessionResponse(session), session.getDocumentNonce(), session.getSelectionSeq(), null, null, "SESSION_INACTIVE");
			if (!request.documentNonce().equals(session.getDocumentNonce())
					&& !Objects.equals(request.previousDocumentNonce(), session.getDocumentNonce()))
				throw new GuestSessionException("DOCUMENT_CONFLICT");
			session.resumeDocument(request.documentNonce());
			GuestContentResponse restored = null;
			String reason = "NO_OPENING";
			if (session.getCurrentOpeningId() != null) {
				Opening opening = current(session, session.getCurrentOpeningId());
				var place = places.findByIdForUpdate(opening.placeId());
				if (place.isPresent() && place.get().isVisible()) {
					restored = issue(session, opening, place.get(), "RESTORE", "SAME_OPENING");
					reason = restored.text() == null ? "TEXT_NOT_AVAILABLE" : "RESTORED";
				}
				else {
					reason = "PLACE_UNAVAILABLE";
				}
			}
			return new GuestResumeResponse(sessionResponse(session), session.getDocumentNonce(), session.getSelectionSeq(),
					session.getCurrentOpeningId(), restored, reason);
		});
	}

	public GuestMapResponse map(UUID sid, String credential) {
		return transaction.execute(tx -> {
			authenticate(sid, credential, true);
			var result = new java.util.ArrayList<GuestMapResponse.MapPlace>();
			for (Place place : places.findAllByVisibleTrueOrderByIdAsc()) {
				if (place.getActiveRegionRevisionId() == null)
					continue;
				regions.findById(place.getActiveRegionRevisionId()).filter(region -> region.getPlace().getId().equals(place.getId()))
						.ifPresent(region -> result.add(new GuestMapResponse.MapPlace(place.getId(), place.getDisplayName(), region.getId(),
								region.getMapSpaceKey(), json.readTree(region.getPolygon()))));
			}
			return new GuestMapResponse(List.copyOf(result), now());
		});
	}

	public GuestContentResponse select(UUID sid, String credential, String keyHeader, GuestSelectionRequest request) {
		required(request.placeId(), "placeId");
		String language = language(request.language());
		UUID key = input.requestKey(keyHeader);
		validateSequence(request.documentNonce(), request.selectionSeq());
		byte[] fingerprint = fingerprint("SELECT", request.placeId(), language, request.documentNonce(), request.selectionSeq());
		return transaction.execute(tx -> {
			GuestSession session = authenticate(sid, credential, true);
			Result old = replay(session, key, fingerprint);
			if (old != null)
				return render(session, old);
			fresh(session, request.documentNonce(), request.selectionSeq());
			Place place = visible(request.placeId());
			Opening opening = session.getCurrentOpeningId() == null ? null : current(session, session.getCurrentOpeningId());
			boolean same = opening != null && opening.placeId().equals(place.getId());
			if (!same) {
				if (opening != null)
					content.close(opening.id(), now());
				opening = content.createOpening(sid, place.getId(), place.getDisplayName(), language, now());
			}
			else if (!opening.language().equals(language)) {
				opening = content.language(opening, language);
			}
			session.select(opening.id(), language, request.selectionSeq());
			GuestContentResponse response = issue(session, opening, place, "SELECT", same ? "SAME_OPENING" : "NEW_OPENING");
			remember(session, key, fingerprint, "SELECT", response);
			return response;
		});
	}

	public GuestContentResponse language(UUID sid, UUID oid, String credential, String keyHeader, String tag, GuestLanguageRequest request) {
		String language = language(request.language());
		UUID key = input.requestKey(keyHeader);
		validateSequence(request.documentNonce(), request.selectionSeq());
		byte[] fingerprint = fingerprint("LANGUAGE", oid, language, request.documentNonce(), request.selectionSeq(), tag);
		return transaction.execute(tx -> {
			GuestSession session = authenticate(sid, credential, true);
			Result old = replay(session, key, fingerprint);
			if (old != null)
				return render(session, old);
			fresh(session, request.documentNonce(), request.selectionSeq());
			Opening opening = current(session, oid);
			Place place = visible(opening.placeId());
			if (!openingTag(opening).equals(tag))
				throw new GuestSessionException("VERSION_CONFLICT");
			opening = content.language(opening, language);
			session.select(oid, language, request.selectionSeq());
			GuestContentResponse response = issue(session, opening, place, "LANGUAGE", "SAME_OPENING");
			remember(session, key, fingerprint, "LANGUAGE", response);
			return response;
		});
	}

	public Map<String, Object> leave(UUID sid, UUID oid, String credential, String keyHeader, GuestLeaveRequest request) {
		UUID key = input.requestKey(keyHeader);
		validateSequence(request.documentNonce(), request.selectionSeq());
		byte[] fingerprint = fingerprint("LEAVE", oid, request.documentNonce(), request.selectionSeq());
		return transaction.execute(tx -> {
			GuestSession session = authenticate(sid, credential, true);
			Result old = replay(session, key, fingerprint);
			if (old != null)
				return Map.of("openingId", oid, "closed", true, "replayed", true);
			fresh(session, request.documentNonce(), request.selectionSeq());
			Opening opening = current(session, oid);
			content.close(oid, now());
			session.select(null, session.getCurrentLanguage(), request.selectionSeq());
			content.insertResult(sid, key, session.getDocumentNonce(), request.selectionSeq(), new Result(oid, opening.version() + 1,
					null, "LEAVE", "SAME_OPENING", opening.language(), "", fingerprint));
			return Map.of("openingId", oid, "closed", true, "replayed", false);
		});
	}

	public GuestViewAckResponse ack(UUID sid, UUID oid, String credential, GuestViewAckRequest request) {
		required(request.contentGrantId(), "contentGrantId");
		return transaction.execute(tx -> {
			authenticate(sid, credential, true);
			Grant grant = validGrant(sid, request.contentGrantId());
			if (!oid.equals(grant.openingId()) || !grant.viewEligible())
				throw new GuestSessionException("GRANT_FORBIDDEN");
			// ACK cũ được dùng snapshot dù đã chọn điểm khác hoặc Admin đã ẩn/xóa điểm
			boolean exists = places.findByIdForUpdate(grant.placeId()).isPresent();
			if (!clock.instant().isBefore(grant.expiresAt()))
				throw new GuestSessionException("SESSION_EXPIRED");
			return content.view(sid, oid).orElseGet(() -> content.insertView(sid, grant, exists, now()));
		});
	}

	GuestSession authenticate(UUID sid, String credential, boolean requireActive) {
		byte[] hash = input.credentialHash(credential);
		GuestSession session = sessions.findByIdForUpdate(sid).orElseThrow(() -> new GuestSessionException("GUEST_CREDENTIAL_INVALID"));
		if (!MessageDigest.isEqual(hash, session.getCredentialHash()) || !tickets.currentEpoch(session.getServiceEpoch()))
			throw new GuestSessionException("GUEST_CREDENTIAL_INVALID");
		if (requireActive && !active(session))
			throw new GuestSessionException(session.getEndedAt() == null ? "SESSION_EXPIRED" : "SESSION_ENDED");
		return session;
	}

	Grant validGrant(UUID sid, UUID gid) {
		Grant grant = content.grant(gid, sid).orElseThrow(() -> new GuestSessionException("GRANT_FORBIDDEN"));
		if (grant.revokedAt() != null || !now().isBefore(grant.expiresAt()))
			throw new GuestSessionException("GRANT_FORBIDDEN");
		return grant;
	}

	private GuestContentResponse issue(GuestSession session, Opening opening, Place place, String source, String kind) {
		List<String> available = List.of("vi", "en", "fr", "ru").stream().filter(lang -> permitted(place.getId(), lang)).toList();
		var slot = slots.findByPlace_IdAndLanguage(place.getId(), opening.language()).orElse(null);
		if (slot == null || slot.getActivePairId() == null || !available.contains(opening.language()))
			return unavailable(opening, opening.version(), opening.language(), source, kind, available);
		var pair = pairs.findById(slot.getActivePairId()).orElseThrow();
		var text = texts.findById(pair.getTextRevisionId()).orElseThrow();
		var revision = audio.findById(pair.getAudioRevisionId()).orElseThrow();
		var assets = readiness.loadFiles(readiness.fileIds(revision), true);
		UUID mp3 = readiness.filesReady(revision, assets) ? revision.getMp3FileId() : null;
		if (mp3 != null)
			assets.get(mp3).retainReference();
		Grant grant = new Grant(UUID.randomUUID(), opening.id(), place.getId(), place.getDisplayName(), opening.language(), text.getId(),
				text.getContentHash(), pair.getId(), revision.getId(), mp3, source, !source.equals("RESTORE"), String.join(",", available),
				session.getExpiresAt(), null);
		// Lấy lại giờ sau khi chờ khóa FILE, không cấp quyền nếu phiên đã chạm hạn trong lúc chờ
		if (!active(session))
			throw new GuestSessionException("SESSION_EXPIRED");
		content.insertGrant(session.getId(), grant, now());
		return renderGrant(opening, opening.version(), kind, grant);
	}

	private boolean permitted(UUID place, String language) {
		var slot = slots.findByPlace_IdAndLanguage(place, language).orElse(null);
		if (slot == null || slot.getActivePairId() == null)
			return false;
		var pair = pairs.findById(slot.getActivePairId()).orElse(null);
		if (pair == null || !pair.getSlot().getId().equals(slot.getId()))
			return false;
		var text = texts.findById(pair.getTextRevisionId()).orElse(null);
		var revision = audio.findById(pair.getAudioRevisionId()).orElse(null);
		if (text == null || revision == null)
			return false;
		// Thiếu bytes runtime chỉ khóa audio, không làm mất văn bản của cặp đã có hồ sơ
		return readiness.conditions(slot, text, revision, Map.of()).stream().allMatch("AUDIO_FILE_NOT_READY"::equals);
	}

	private GuestContentResponse render(GuestSession session, Result result) {
		Opening opening = content.opening(result.openingId(), session.getId()).orElseThrow(() -> new GuestSessionException("CONTENT_UNAVAILABLE"));
		if (result.grantId() == null)
			return unavailable(opening, result.openingVersion(), result.language(), result.action(), result.selectionKind(),
					GuestContentRepository.languages(result.availableLanguages()));
		return renderGrant(opening, result.openingVersion(), result.selectionKind(), validGrant(session.getId(), result.grantId()));
	}

	private GuestContentResponse renderGrant(Opening opening, long version, String kind, Grant grant) {
		var text = texts.findById(grant.textId()).filter(t -> MessageDigest.isEqual(t.getContentHash(), grant.textHash())).orElse(null);
		if (text == null)
			throw new GuestSessionException("CONTENT_UNAVAILABLE");
		var file = grant.mp3Id() == null ? null : files.findById(grant.mp3Id()).orElse(null);
		var audioDto = new GuestContentResponse.AudioContent(grant.audioId(), grant.mp3Id(), file == null ? null : hex(file.getChecksum()),
				file == null ? 0 : file.getByteSize(), file == null ? null : file.getDuration(), file == null ? "NOT_READY" : "READY");
		return new GuestContentResponse(opening.id(), version, kind, new GuestContentResponse.PlaceSummary(grant.placeId(), grant.name()),
				grant.language(), grant.source(), "AVAILABLE", GuestContentRepository.languages(grant.availableLanguages()),
				new GuestContentResponse.TextContent(grant.textId(), hex(grant.textHash()), text.getBody()), audioDto,
				new GuestContentResponse.GrantSummary(grant.id(), grant.expiresAt(), grant.viewEligible()), now());
	}

	private GuestContentResponse unavailable(Opening opening, long version, String language, String source, String kind, List<String> available) {
		return new GuestContentResponse(opening.id(), version, kind, new GuestContentResponse.PlaceSummary(opening.placeId(), opening.name()),
				language, source, "TEXT_NOT_AVAILABLE", available, null, null, null, now());
	}

	private void remember(GuestSession session, UUID key, byte[] fingerprint, String action, GuestContentResponse response) {
		content.insertResult(session.getId(), key, session.getDocumentNonce(), session.getSelectionSeq(), new Result(response.openingId(),
				response.openingVersion(), response.grant() == null ? null : response.grant().id(), action, response.selectionKind(),
				response.language(), String.join(",", response.availableLanguages()), fingerprint));
	}

	private Result replay(GuestSession session, UUID key, byte[] fingerprint) {
		Result old = content.result(session.getId(), key).orElse(null);
		if (old != null && !MessageDigest.isEqual(fingerprint, old.fingerprint()))
			throw new GuestSessionException("KEY_REUSED");
		return old;
	}

	private void fresh(GuestSession session, UUID nonce, long seq) {
		if (!nonce.equals(session.getDocumentNonce()) || seq <= session.getSelectionSeq() || content.selectionUsed(session.getId(), nonce, seq))
			throw new GuestSessionException("STALE_SELECTION");
	}

	private Opening current(GuestSession session, UUID oid) {
		if (!oid.equals(session.getCurrentOpeningId()))
			throw new GuestSessionException("STALE_SELECTION");
		return content.opening(oid, session.getId()).filter(o -> o.state().equals("OPEN"))
				.orElseThrow(() -> new GuestSessionException("STALE_SELECTION"));
	}

	private Place visible(UUID id) {
		return places.findByIdForUpdate(id).filter(Place::isVisible).orElseThrow(() -> new GuestSessionException("PLACE_UNAVAILABLE"));
	}

	private boolean active(GuestSession session) {
		return session.getEndedAt() == null && clock.instant().isBefore(session.getExpiresAt());
	}

	private Instant now() {
		return clock.instant().truncatedTo(ChronoUnit.MICROS);
	}

	private SessionResponse sessionResponse(GuestSession session) {
		return new SessionResponse(session.getId(), session.getEndedAt() != null ? "ENDED" : active(session) ? "ACTIVE" : "EXPIRED",
				session.getDisplayName(), session.getInitialLanguage(), active(session) ? session.getCurrentLanguage() : null,
				session.getStartedAt(), session.getExpiresAt(), session.getEndedAt(), now(), false, false);
	}

	private String language(String language) {
		if (language == null || !List.of("vi", "en", "fr", "ru").contains(language))
			throw validation("language", "Chọn vi, en, fr hoặc ru");
		return language;
	}

	private static void validateSequence(UUID nonce, Long seq) {
		required(nonce, "documentNonce");
		if (seq == null || seq <= 0)
			throw validation("selectionSeq", "Cần số nguyên dương");
	}

	private static void required(Object value, String field) {
		if (value == null)
			throw validation(field, "Không được bỏ trống");
	}

	private static GuestSessionException validation(String field, String message) {
		return new GuestSessionException("VALIDATION_FAILED", Map.of(field, message));
	}

	public static String openingTag(Opening opening) {
		return "\"opening:" + opening.id() + ":" + opening.version() + "\"";
	}

	private static String hex(byte[] value) {
		return HexFormat.of().formatHex(value);
	}

	private static byte[] fingerprint(Object... parts) {
		try {
			var bytes = new ByteArrayOutputStream();
			var data = new DataOutputStream(bytes);
			for (Object part : parts) {
				byte[] value = part == null ? null : part.toString().getBytes(StandardCharsets.UTF_8);
				data.writeInt(value == null ? -1 : value.length);
				if (value != null)
					data.write(value);
			}
			return MessageDigest.getInstance("SHA-256").digest(bytes.toByteArray());
		}
		catch (IOException | NoSuchAlgorithmException error) {
			throw new IllegalStateException(error);
		}
	}
}
