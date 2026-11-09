package vn.hueqr.backend.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminContentResponse;
import vn.hueqr.backend.dto.AdminContentCompletenessResponse;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminTextReviewReferenceResponse;
import vn.hueqr.backend.dto.SaveTextRevisionRequest;
import vn.hueqr.backend.dto.SaveTextReviewReferenceRequest;
import vn.hueqr.backend.entity.ContentSlot;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.TextRevision;
import vn.hueqr.backend.entity.TextReviewReference;
import vn.hueqr.backend.repository.ContentSlotRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TextReviewReferenceRepository;
import vn.hueqr.backend.repository.TtsJobRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;

@Service
public class AdminContentService {
	private final PlaceRepository places;
	private final ContentSlotRepository slots;
	private final TextRevisionRepository texts;
	private final TextReviewReferenceRepository references;
	private final ContentTextPolicy policy;
	private final AdminCommandService commands;
	private final ObjectMapper json;
	private final Clock clock;
	private final TtsJobRepository jobs;
	private final TtsJobService tts;
	private final AdminPairService pairs;

	public AdminContentService(PlaceRepository places, ContentSlotRepository slots, TextRevisionRepository texts,
			TextReviewReferenceRepository references, ContentTextPolicy policy, AdminCommandService commands,
			ObjectMapper json, Clock clock, TtsJobRepository jobs, TtsJobService tts, AdminPairService pairs) {
		this.places = places;
		this.slots = slots;
		this.texts = texts;
		this.references = references;
		this.policy = policy;
		this.commands = commands;
		this.json = json;
		this.clock = clock;
		this.jobs = jobs;
		this.tts = tts;
		this.pairs = pairs;
	}

	@Transactional(readOnly = true)
	public AdminContentResponse detail(String placeId, String language) {
		UUID pid = AdminPlacePreconditions.placeId(placeId);
		policy.language(language);
		Place place = places.findById(pid).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
		var slot = slots.findByPlace_IdAndLanguage(pid, language).orElse(null);
		if (slot == null)
			return new AdminContentResponse(pid, language, null, null, policy.tag("place", pid, place.getVersion()), null, null);
		var draft = slot.getDraftTextId() == null ? null : savedText(texts.findByIdAndSlot_Id(slot.getDraftTextId(), slot.getId()).orElseThrow());
		return new AdminContentResponse(pid, language, slot.getId(), slot.getVersion(), slotTag(slot), draft, pairs.applied(slot));
	}

	@Transactional(readOnly = true)
	public AdminTextReviewReferenceResponse review(String textId) {
		UUID tid = policy.textId(textId, "textRevisionId");
		return reference(text(tid));
	}

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	public List<AdminContentCompletenessResponse> completeness() {
		var result = new ArrayList<AdminContentCompletenessResponse>();
		for (Place place : places.findAll(Sort.by("createdAt", "id"))) {
			for (String language : List.of("vi", "en", "fr", "ru")) {
				var slot = slots.findByPlace_IdAndLanguage(place.getId(), language).orElse(null);
				var content = detail(place.getId().toString(), language);
				var candidates = slot == null ? List.<AdminContentCompletenessResponse.AudioCandidate>of() : pairs.candidates(slot);
				var missing = new ArrayList<String>();
				if (content.draftText() == null)
					missing.add("DRAFT_TEXT_MISSING");
				else {
					if (content.draftText().reviewReference().version() == null)
						missing.add("TEXT_REVIEW_REFERENCE_MISSING");
					if (candidates.isEmpty())
						missing.add("AUDIO_NOT_CREATED");
					else if (candidates.stream().noneMatch(audio -> audio.missingConditions().isEmpty()))
						missing.add("NO_APPLICABLE_DRAFT_AUDIO");
				}
				result.add(new AdminContentCompletenessResponse(place.getId(), place.getDisplayName(), language, content, candidates, List.copyOf(missing)));
			}
		}
		return List.copyOf(result);
	}

	public AdminOperationResponse save(UUID accountId, String key, String placeId, String language,
			String ifMatch, SaveTextRevisionRequest input) {
		UUID pid = AdminPlacePreconditions.placeId(placeId);
		policy.language(language);
		var command = new Command("POST", "/api/v1/admin/places/" + pid + "/content/" + language + "/text-revisions",
				json.writeValueAsString(input), ifMatch, "SAVE_TEXT", 201, "TEXT_SAVED");
		return commands.execute(accountId, key, command, () -> {
			// WINDOW rồi PLACE bảo vệ cả lần tạo ngăn đầu tiên và các lần đổi con trỏ bản nháp
			Place place = lockPlace(pid);
			ContentSlot slot = slots.findByPlace_IdAndLanguage(pid, language).orElse(null);
			policy.requireMatch(ifMatch, slot == null ? policy.tag("place", pid, place.getVersion()) : slotTag(slot));
			UUID base = input.baseTextId() == null ? null : policy.textId(input.baseTextId(), "baseTextId");
			if (!Objects.equals(base, slot == null ? null : slot.getDraftTextId()))
				throw new AdminCommandException(409, "BASE_TEXT_CONFLICT");
			byte[] hash = policy.bodyHash(input.body());
			if (slot == null)
				slot = slots.saveAndFlush(new ContentSlot(UUID.randomUUID(), place, language));

			// Khớp DATETIME(6), giữ nguyên văn bản cũ và hồ sơ của nó khi tạo phiên bản mới
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			TextRevision saved = texts.saveAndFlush(new TextRevision(UUID.randomUUID(), slot, input.body(), hash, now));
			slot.setDraftTextId(saved.getId());
			slots.flush();
			return Map.of("placeId", pid.toString(), "language", language, "slotId", slot.getId().toString(),
					"textRevisionId", saved.getId().toString(), "version", slot.getVersion(), "etag", slotTag(slot),
					"contentHash", HexFormat.of().formatHex(hash), "savedAt", now.toString());
		});
	}

	public AdminOperationResponse saveReview(UUID accountId, String key, String textId, String ifMatch,
			SaveTextReviewReferenceRequest input) {
		UUID tid = policy.textId(textId, "textRevisionId");
		var command = new Command("PUT", "/api/v1/admin/text-revisions/" + tid + "/review-reference",
				json.writeValueAsString(input), ifMatch, "SAVE_TEXT_REVIEW_REFERENCE", 200, "TEXT_REVIEW_REFERENCE_RECORDED");
		return commands.execute(accountId, key, command, () -> {
			TextRevision saved = text(tid);
			// Cùng khóa PLACE với lưu nháp, bản text bất biến nên có thể tra chủ sở hữu trước khóa
			lockPlace(saved.getSlot().getPlace().getId());
			var ref = references.findById(tid).orElse(null);
			policy.requireMatch(ifMatch, reviewTag(tid, ref));
			policy.requireText(input.locator(), "locator");
			policy.requireUnicode(input.limitations(), "limitations");
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			if (ref == null)
				ref = references.saveAndFlush(new TextReviewReference(tid, input.locator(), input.limitations(), accountId, now));
			else {
				ref.update(input.locator(), input.limitations(), accountId, now);
				references.flush();
			}
			// Receipt không sao chép nội dung văn bản hoặc vị trí hồ sơ bên ngoài
			return Map.of("textRevisionId", tid.toString(), "contentHash", HexFormat.of().formatHex(saved.getContentHash()),
					"version", ref.getVersion(), "etag", reviewTag(tid, ref), "recordedAt", now.toString());
		});
	}

	private Place lockPlace(UUID id) {
		return places.findByIdForUpdate(id).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
	}

	private TextRevision text(UUID id) {
		return texts.findById(id).orElseThrow(() -> new AdminCommandException(404, "TEXT_REVISION_NOT_FOUND"));
	}

	private AdminContentResponse.SavedText savedText(TextRevision text) {
		return new AdminContentResponse.SavedText(text.getId(), text.getBody(), HexFormat.of().formatHex(text.getContentHash()),
				text.getSavedAt(), reference(text), text.getLatestTtsJobKey() == null ? null
						: jobs.findById(text.getLatestTtsJobKey()).map(tts::response).orElse(null));
	}

	private AdminTextReviewReferenceResponse reference(TextRevision text) {
		var ref = references.findById(text.getId()).orElse(null);
		return new AdminTextReviewReferenceResponse(text.getId(), HexFormat.of().formatHex(text.getContentHash()),
				reviewTag(text.getId(), ref), ref == null ? null : ref.getLocator(), ref == null ? null : ref.getLimitations(),
				ref == null ? null : ref.getRecordedBy(), ref == null ? null : ref.getRecordedAt(), ref == null ? null : ref.getVersion());
	}

	private String slotTag(ContentSlot slot) {
		return policy.tag("slot", slot.getId(), slot.getVersion());
	}

	private String reviewTag(UUID textId, TextReviewReference reference) {
		return policy.tag("review", textId, reference == null ? "none" : reference.getVersion());
	}
}
