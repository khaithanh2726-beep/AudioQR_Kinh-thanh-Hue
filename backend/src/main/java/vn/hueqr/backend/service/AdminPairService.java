package vn.hueqr.backend.service;

import java.security.MessageDigest;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminAppliedPairResponse;
import vn.hueqr.backend.dto.AdminAudioReviewReferenceResponse;
import vn.hueqr.backend.dto.AdminContentCompletenessResponse.AudioCandidate;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminTextReviewReferenceResponse;
import vn.hueqr.backend.dto.ApplyPairRequest;
import vn.hueqr.backend.dto.SaveAudioReviewReferenceRequest;
import vn.hueqr.backend.entity.AudioRevision;
import vn.hueqr.backend.entity.AudioReviewReference;
import vn.hueqr.backend.entity.ContentPair;
import vn.hueqr.backend.entity.ContentSlot;
import vn.hueqr.backend.repository.AudioRevisionRepository;
import vn.hueqr.backend.repository.AudioReviewReferenceRepository;
import vn.hueqr.backend.repository.ContentPairRepository;
import vn.hueqr.backend.repository.ContentSlotRepository;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.repository.TextReviewReferenceRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;

@Service
public class AdminPairService {
	private final PlaceRepository places;
	private final ContentSlotRepository slots;
	private final TextRevisionRepository texts;
	private final AudioRevisionRepository audios;
	private final ContentPairRepository pairs;
	private final TextReviewReferenceRepository textReviews;
	private final AudioReviewReferenceRepository audioReviews;
	private final FileAssetRepository files;
	private final ContentPairReadiness readiness;
	private final ContentTextPolicy policy;
	private final AdminCommandService commands;
	private final ObjectMapper json;
	private final Clock clock;

	public AdminPairService(PlaceRepository places, ContentSlotRepository slots, TextRevisionRepository texts,
			AudioRevisionRepository audios, ContentPairRepository pairs, TextReviewReferenceRepository textReviews,
			AudioReviewReferenceRepository audioReviews, FileAssetRepository files, ContentPairReadiness readiness,
			ContentTextPolicy policy, AdminCommandService commands, ObjectMapper json, Clock clock) {
		this.places = places;
		this.slots = slots;
		this.texts = texts;
		this.audios = audios;
		this.pairs = pairs;
		this.textReviews = textReviews;
		this.audioReviews = audioReviews;
		this.files = files;
		this.readiness = readiness;
		this.policy = policy;
		this.commands = commands;
		this.json = json;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public AdminAudioReviewReferenceResponse review(String audioId) {
		return reference(audio(id(audioId, "audioRevisionId")));
	}

	public AdminOperationResponse saveReview(UUID account, String key, String audioId, String ifMatch, SaveAudioReviewReferenceRequest input) {
		UUID aid = id(audioId, "audioRevisionId");
		var command = new Command("PUT", "/api/v1/admin/audio-revisions/" + aid + "/review-reference",
				json.writeValueAsString(input), ifMatch, "SAVE_AUDIO_REVIEW_REFERENCE", 200, "AUDIO_REVIEW_REFERENCE_RECORDED");
		return commands.execute(account, key, command, () -> {
			// Tra Id chủ sở hữu trước khóa, chỉ nạp Entity và hồ sơ sau khi đã khóa PLACE
			UUID pid = audios.findPlaceIdById(aid).orElseThrow(() -> new AdminCommandException(404, "AUDIO_REVISION_NOT_FOUND"));
			places.findByIdForUpdate(pid).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
			var audio = audio(aid);
			var ref = audioReviews.findById(aid).orElse(null);
			policy.requireMatch(ifMatch, reviewTag(aid, ref));
			policy.requireText(input.locator(), "locator");
			policy.requireUnicode(input.limitations(), "limitations");
			var assets = readiness.loadFiles(readiness.fileIds(audio), true);
			ContentPairReadiness.require(readiness.filesReady(audio, assets) ? List.of() : List.of("AUDIO_FILE_NOT_READY"));
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			if (ref == null)
				ref = audioReviews.saveAndFlush(new AudioReviewReference(aid, input.locator(), input.limitations(), account, now));
			else {
				ref.update(input.locator(), input.limitations(), account, now);
				audioReviews.flush();
			}
			return Map.of("audioRevisionId", aid.toString(), "textRevisionId", audio.getTextRevision().getId().toString(),
					"mp3Checksum", hex(assets.get(audio.getMp3FileId()).getChecksum()), "version", ref.getVersion(),
					"etag", reviewTag(aid, ref), "recordedAt", now.toString());
		});
	}

	public AdminOperationResponse apply(UUID account, String key, String placeId, String language, String ifMatch, ApplyPairRequest input) {
		UUID pid = AdminPlacePreconditions.placeId(placeId);
		policy.language(language);
		var command = new Command("PUT", "/api/v1/admin/places/" + pid + "/content/" + language + "/active-pair",
				json.writeValueAsString(input), ifMatch, "APPLY_PAIR", 200, "PAIR_APPLIED");
		return commands.execute(account, key, command, () -> {
			places.findByIdForUpdate(pid).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
			var slot = slots.findByPlace_IdAndLanguageForUpdate(pid, language).orElseThrow(() -> new AdminCommandException(404, "CONTENT_SLOT_NOT_FOUND"));
			policy.requireMatch(ifMatch, policy.tag("slot", slot.getId(), slot.getVersion()));
			UUID tid = id(input.textRevisionId(), "textRevisionId");
			UUID aid = id(input.audioRevisionId(), "audioRevisionId");
			var text = texts.findByIdAndSlot_Id(tid, slot.getId()).orElseThrow(() -> requirements("PAIR_REVISION_MISMATCH"));
			var audio = audio(aid);
			if (!audio.getTextRevision().getId().equals(tid))
				throw requirements("PAIR_REVISION_MISMATCH");
			var fileIds = new HashSet<>(readiness.fileIds(audio));
			UUID previousId = slot.getActivePairId();
			if (previousId != null)
				fileIds.addAll(readiness.fileIds(audio(pairs.findById(previousId).orElseThrow().getAudioRevisionId())));
			var assets = readiness.loadFiles(fileIds, true);
			var missing = new ArrayList<>(readiness.conditions(slot, text, audio, assets));
			if (!matches(input.expectedTextHash(), text.getContentHash(), "expectedTextHash"))
				missing.add("TEXT_HASH_MISMATCH");
			var mp3 = assets.get(audio.getMp3FileId());
			if (!matches(input.expectedMp3Checksum(), mp3 == null ? null : mp3.getChecksum(), "expectedMp3Checksum"))
				missing.add("MP3_CHECKSUM_MISMATCH");
			ContentPairReadiness.require(missing);
			reviewVersion(input.textReviewVersion(), "textReviewVersion");
			reviewVersion(input.audioReviewVersion(), "audioReviewVersion");
			if (!textReviews.findById(tid).orElseThrow().getVersion().equals(input.textReviewVersion())
					|| !audioReviews.findById(aid).orElseThrow().getVersion().equals(input.audioReviewVersion()))
				throw new AdminCommandException(412, "VERSION_CONFLICT");
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			var pair = pairs.findBySlot_IdAndTextRevisionIdAndAudioRevisionId(slot.getId(), tid, aid)
					.orElseGet(() -> pairs.saveAndFlush(new ContentPair(UUID.randomUUID(), slot, tid, aid, now)));
			boolean changed = !pair.getId().equals(previousId);
			if (changed) {
				slot.setActivePairId(pair.getId());
				slots.flush();
				readiness.fileIds(audio).forEach(fid -> assets.get(fid).markApplied());
				for (UUID fid : fileIds)
					if (!readiness.fileIds(audio).contains(fid) && pairs.countAppliedFileReferences(fid) == 0)
						assets.get(fid).markUnapplied(now);
				files.flush();
			}
			var result = new LinkedHashMap<String, Object>();
			result.put("placeId", pid.toString());
			result.put("language", language);
			result.put("slotId", slot.getId().toString());
			result.put("previousPairId", previousId == null ? null : previousId.toString());
			result.put("appliedPair", Map.of("id", pair.getId().toString(), "textRevisionId", tid.toString(), "audioRevisionId", aid.toString()));
			result.put("slotVersion", slot.getVersion());
			result.put("etag", policy.tag("slot", slot.getId(), slot.getVersion()));
			result.put("appliedAt", now.toString());
			result.put("changed", changed);
			return result;
		});
	}

	public AdminAppliedPairResponse applied(ContentSlot slot) {
		if (slot.getActivePairId() == null)
			return null;
		var pair = pairs.findByIdAndSlot_Id(slot.getActivePairId(), slot.getId()).orElseThrow();
		var text = texts.findById(pair.getTextRevisionId()).orElseThrow();
		var audio = audio(pair.getAudioRevisionId());
		var ref = textReviews.findById(text.getId()).orElse(null);
		var textRef = new AdminTextReviewReferenceResponse(text.getId(), hex(text.getContentHash()),
				policy.tag("review", text.getId(), ref == null ? "none" : ref.getVersion()), ref == null ? null : ref.getLocator(),
				ref == null ? null : ref.getLimitations(), ref == null ? null : ref.getRecordedBy(),
				ref == null ? null : ref.getRecordedAt(), ref == null ? null : ref.getVersion());
		var assets = readiness.loadFiles(readiness.fileIds(audio), false);
		return new AdminAppliedPairResponse(pair.getId(), text.getId(), text.getBody(), hex(text.getContentHash()), audio.getId(),
				hex(assets.get(audio.getMp3FileId()).getChecksum()), pair.getCreatedAt(), textRef, reference(audio), readiness.conditions(slot, text, audio, assets));
	}

	public List<AudioCandidate> candidates(ContentSlot slot) {
		if (slot.getDraftTextId() == null)
			return List.of();
		var text = texts.findByIdAndSlot_Id(slot.getDraftTextId(), slot.getId()).orElseThrow();
		return audios.findByTextRevision_IdOrderByCreatedAtAscIdAsc(text.getId()).stream().map(audio -> {
			var assets = readiness.loadFiles(readiness.fileIds(audio), false);
			return new AudioCandidate(audio.getId(), text.getId(), hex(text.getContentHash()), hex(assets.get(audio.getMp3FileId()).getChecksum()),
					reference(audio), readiness.conditions(slot, text, audio, assets));
		}).toList();
	}

	private AdminAudioReviewReferenceResponse reference(AudioRevision audio) {
		var ref = audioReviews.findById(audio.getId()).orElse(null);
		var text = audio.getTextRevision();
		return new AdminAudioReviewReferenceResponse(audio.getId(), text.getId(), text.getSlot().getLanguage(), hex(text.getContentHash()),
				hex(files.findById(audio.getMp3FileId()).orElseThrow().getChecksum()), reviewTag(audio.getId(), ref),
				ref == null ? null : ref.getLocator(), ref == null ? null : ref.getLimitations(), ref == null ? null : ref.getRecordedBy(),
				ref == null ? null : ref.getRecordedAt(), ref == null ? null : ref.getVersion());
	}

	private AudioRevision audio(UUID id) {
		return audios.findById(id).orElseThrow(() -> new AdminCommandException(404, "AUDIO_REVISION_NOT_FOUND"));
	}

	private String reviewTag(UUID aid, AudioReviewReference ref) {
		return policy.tag("audio-review", aid, ref == null ? "none" : ref.getVersion());
	}

	private static UUID id(String value, String field) {
		try {
			UUID result = UUID.fromString(value);
			if (!result.toString().equals(value))
				throw new IllegalArgumentException();
			return result;
		}
		catch (IllegalArgumentException | NullPointerException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of(field, "Cần UUID chuẩn chữ thường."));
		}
	}

	private static void reviewVersion(Long version, String field) {
		if (version == null || version < 0)
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of(field, "Cần phiên bản hồ sơ không âm."));
	}

	private static boolean matches(String supplied, byte[] actual, String field) {
		if (supplied == null || !supplied.matches("[0-9a-f]{64}"))
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of(field, "Cần mã Hash SHA-256 hex chữ thường."));
		return MessageDigest.isEqual(HexFormat.of().parseHex(supplied), actual);
	}

	private static String hex(byte[] value) {
		return HexFormat.of().formatHex(value);
	}

	private static AdminCommandException requirements(String condition) {
		return new AdminCommandException(409, "REQUIREMENTS_NOT_MET", Map.of(), List.of(condition));
	}
}
