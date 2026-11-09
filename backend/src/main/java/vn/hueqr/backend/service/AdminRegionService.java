package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminRegionRevisionResponse;
import vn.hueqr.backend.dto.ApplyRegionRequest;
import vn.hueqr.backend.dto.SaveRegionRequest;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.entity.RegionRevision;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.repository.RegionRevisionRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;

@Service
public class AdminRegionService {
	private final PlaceRepository places;
	private final RegionRevisionRepository regions;
	private final AdminCommandService commands;
	private final RegionGeometryPolicy geometry;
	private final ObjectMapper json;
	private final Clock clock;

	public AdminRegionService(PlaceRepository places, RegionRevisionRepository regions, AdminCommandService commands,
			RegionGeometryPolicy geometry, ObjectMapper json, Clock clock) {
		this.places = places;
		this.regions = regions;
		this.commands = commands;
		this.geometry = geometry;
		this.json = json;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public AdminRegionRevisionResponse detail(String placeId, String revisionId) {
		UUID pid = AdminPlacePreconditions.placeId(placeId);
		UUID rid = AdminPlacePreconditions.revisionId(revisionId, "revisionId");
		if (!places.existsById(pid))
			throw new AdminCommandException(404, "LOCATION_NOT_FOUND");
		RegionRevision region = region(pid, rid);
		return new AdminRegionRevisionResponse(region.getId(), pid, json.readTree(region.getPolygon()),
				region.getMapSpaceKey(), HexFormat.of().formatHex(region.getGeometryHash()), region.getSavedAt());
	}

	public AdminOperationResponse save(UUID accountId, String key, String placeId, String ifMatch, SaveRegionRequest input) {
		UUID pid = AdminPlacePreconditions.placeId(placeId);
		Command command = new Command("POST", "/api/v1/admin/places/" + pid + "/region-revisions",
				geometry.canonicalJson(json.valueToTree(input)), ifMatch, "SAVE_REGION", 201, "REGION_SAVED");
		return commands.execute(accountId, key, command, () -> {
			Place place = lockPlace(pid, ifMatch);
			var shape = geometry.validate(input.polygon(), input.mapSpaceKey(), input.geometryHash());
			// Khớp DATETIME(6), lưu revision riêng nên không đổi version hoặc con trỏ của Place
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			RegionRevision saved = regions.saveAndFlush(new RegionRevision(UUID.randomUUID(), place, shape.polygon(),
					shape.mapSpaceKey(), HexFormat.of().parseHex(shape.geometryHash()), now));
			return Map.of("placeId", pid.toString(), "regionRevisionId", saved.getId().toString(),
					"version", place.getVersion(), "geometryHash", shape.geometryHash(), "mapSpaceKey", shape.mapSpaceKey(),
					"savedAt", now.toString());
		});
	}

	public AdminOperationResponse apply(UUID accountId, String key, String placeId, String ifMatch, ApplyRegionRequest input) {
		UUID pid = AdminPlacePreconditions.placeId(placeId);
		Command command = new Command("PUT", "/api/v1/admin/places/" + pid + "/active-region",
				geometry.canonicalJson(json.valueToTree(input)), ifMatch, "APPLY_REGION", 200, "REGION_APPLIED");
		return commands.execute(accountId, key, command, () -> {
			Place place = lockPlace(pid, ifMatch);
			UUID rid = AdminPlacePreconditions.revisionId(input.savedRevisionId(), "savedRevisionId");
			RegionRevision saved = region(pid, rid);
			if (!RegionGeometryPolicy.MAP_SPACE_KEY.equals(saved.getMapSpaceKey()))
				throw new AdminCommandException(409, "MAP_SPACE_MISMATCH");
			if (!geometry.matches(HexFormat.of().formatHex(saved.getGeometryHash()), input.previewedGeometryHash(),
					"previewedGeometryHash"))
				throw new AdminCommandException(409, "GEOMETRY_HASH_MISMATCH");

			// Áp dụng lại đúng vùng hiện hành là thao tác không đổi dữ liệu, vẫn có receipt cho key mới
			if (!rid.equals(place.getActiveRegionRevisionId())) {
				place.setActiveRegionRevisionId(rid, clock.instant().truncatedTo(ChronoUnit.MICROS));
				places.flush();
			}
			return Map.of("placeId", pid.toString(), "regionRevisionId", rid.toString(), "version", place.getVersion(),
					"visible", place.isVisible(), "geometryHash", HexFormat.of().formatHex(saved.getGeometryHash()),
					"updatedAt", place.getUpdatedAt().toString());
		});
	}

	private Place lockPlace(UUID id, String ifMatch) {
		// Chạy trong action sau nhánh replay của cửa sổ lệnh, thứ tự khóa WINDOW rồi PLACE
		long expected = AdminPlacePreconditions.version(ifMatch);
		Place place = places.findByIdForUpdate(id).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
		if (place.getVersion() != expected)
			throw new AdminCommandException(412, "VERSION_CONFLICT");
		return place;
	}

	private RegionRevision region(UUID placeId, UUID revisionId) {
		return regions.findByIdAndPlace_Id(revisionId, placeId)
				.orElseThrow(() -> new AdminCommandException(404, "REGION_REVISION_NOT_FOUND"));
	}
}
