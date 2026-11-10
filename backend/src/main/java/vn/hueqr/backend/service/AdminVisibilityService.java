package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.SetPlaceVisibilityRequest;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.repository.AudioRevisionRepository;
import vn.hueqr.backend.repository.ContentPairRepository;
import vn.hueqr.backend.repository.ContentSlotRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.RegionRevisionRepository;
import vn.hueqr.backend.repository.TextRevisionRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;

@Service
public class AdminVisibilityService {
	private final PlaceRepository places;
	private final RegionRevisionRepository regions;
	private final ContentSlotRepository slots;
	private final ContentPairRepository pairs;
	private final TextRevisionRepository texts;
	private final AudioRevisionRepository audios;
	private final ContentPairReadiness readiness;
	private final RegionGeometryPolicy geometry;
	private final AdminCommandService commands;
	private final ObjectMapper json;
	private final Clock clock;

	public AdminVisibilityService(PlaceRepository places, RegionRevisionRepository regions, ContentSlotRepository slots,
			ContentPairRepository pairs, TextRevisionRepository texts, AudioRevisionRepository audios, ContentPairReadiness readiness,
			RegionGeometryPolicy geometry, AdminCommandService commands, ObjectMapper json, Clock clock) {
		this.places = places;
		this.regions = regions;
		this.slots = slots;
		this.pairs = pairs;
		this.texts = texts;
		this.audios = audios;
		this.readiness = readiness;
		this.geometry = geometry;
		this.commands = commands;
		this.json = json;
		this.clock = clock;
	}

	public AdminOperationResponse set(UUID account, String key, String placeId, String ifMatch, SetPlaceVisibilityRequest input) {
		UUID pid = AdminPlacePreconditions.placeId(placeId);
		var command = new Command("PUT", "/api/v1/admin/places/" + pid + "/visibility", json.writeValueAsString(input),
				ifMatch, "SET_VISIBILITY", 200, "VISIBILITY_UPDATED");
		return commands.execute(account, key, command, () -> {
			long expected = AdminPlacePreconditions.version(ifMatch);
			var place = places.findByIdForUpdate(pid).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
			if (place.getVersion() != expected)
				throw new AdminCommandException(412, "VERSION_CONFLICT");
			if (input.visible() == null)
				throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("visible", "Cần giá trị true hoặc false."));
			if (input.visible())
				ContentPairReadiness.require(conditions(place, true));
			boolean changed = place.isVisible() != input.visible();
			if (changed) {
				place.setVisible(input.visible(), clock.instant().truncatedTo(ChronoUnit.MICROS));
				places.flush();
			}
			return Map.of("placeId", pid.toString(), "visible", place.isVisible(), "version", place.getVersion(),
					"changed", changed, "updatedAt", place.getUpdatedAt().toString());
		});
	}

	public List<String> conditions(Place place, boolean lock) {
		var missing = new ArrayList<String>();
		try {
			if (!DisplayNamePolicy.normalize(place.getDisplayName(), 100).equals(place.getDisplayName()))
				missing.add("PLACE_INFORMATION_INVALID");
		}
		catch (IllegalArgumentException error) {
			missing.add("PLACE_INFORMATION_INVALID");
		}
		if (place.getActiveRegionRevisionId() == null)
			missing.add("REGION_NOT_APPLIED");
		else {
			var region = regions.findByIdAndPlace_Id(place.getActiveRegionRevisionId(), place.getId()).orElse(null);
			if (region == null)
				missing.add("REGION_INVALID");
			else {
				try {
					geometry.validate(json.readTree(region.getPolygon()), region.getMapSpaceKey(), HexFormat.of().formatHex(region.getGeometryHash()));
				}
				catch (IllegalArgumentException | AdminCommandException | tools.jackson.core.JacksonException error) {
					missing.add("REGION_INVALID");
				}
			}
		}
		var slot = (lock ? slots.findByPlace_IdAndLanguageForUpdate(place.getId(), "vi") : slots.findByPlace_IdAndLanguage(place.getId(), "vi")).orElse(null);
		if (slot == null || slot.getActivePairId() == null)
			missing.add("VI_PAIR_NOT_APPLIED");
		else {
			var pair = pairs.findByIdAndSlot_Id(slot.getActivePairId(), slot.getId()).orElseThrow();
			var text = texts.findById(pair.getTextRevisionId()).orElseThrow();
			var audio = audios.findById(pair.getAudioRevisionId()).orElseThrow();
			var assets = readiness.loadFiles(readiness.fileIds(audio), lock);
			readiness.conditions(slot, text, audio, assets).forEach(condition -> missing.add("VI_" + condition));
		}
		return List.copyOf(missing);
	}
}
