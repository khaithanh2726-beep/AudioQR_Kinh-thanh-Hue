package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.dto.AdminPlaceResponse;
import vn.hueqr.backend.dto.CreatePlaceRequest;
import vn.hueqr.backend.dto.UpdatePlaceRequest;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;

@Service
public class AdminPlaceService {
	private final PlaceRepository places;
	private final AdminCommandService commands;
	private final ObjectMapper json;
	private final Clock clock;
	private final AdminVisibilityService visibility;

	public AdminPlaceService(PlaceRepository places, AdminCommandService commands, ObjectMapper json, Clock clock, AdminVisibilityService visibility) {
		this.places = places;
		this.commands = commands;
		this.json = json;
		this.clock = clock;
		this.visibility = visibility;
	}

	@Transactional(readOnly = true)
	public List<AdminPlaceResponse> list() {
		return places.findAll(Sort.by("createdAt", "id")).stream().map(this::response).toList();
	}

	@Transactional(readOnly = true)
	public AdminPlaceResponse detail(String placeId) {
		return response(places.findById(AdminPlacePreconditions.placeId(placeId))
				.orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND")));
	}

	public AdminOperationResponse create(UUID accountId, String key, CreatePlaceRequest input) {
		Command command = new Command("POST", "/api/v1/admin/places", json.writeValueAsString(input), null,
				"CREATE_PLACE", 201, "PLACE_CREATED");
		return commands.execute(accountId, key, command, () -> {
			String name = name(input.displayName());
			boolean duplicate = places.countWithEquivalentName(name) > 0;
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			Place place = places.saveAndFlush(new Place(UUID.randomUUID(), name, now));
			return Map.of("placeId", place.getId().toString(), "version", place.getVersion(), "visible", place.isVisible(),
					"duplicateNameWarning", duplicate, "createdAt", now.toString());
		});
	}

	public AdminOperationResponse rename(UUID accountId, String key, String placeId, String ifMatch, UpdatePlaceRequest input) {
		UUID id = AdminPlacePreconditions.placeId(placeId);
		Command command = new Command("PATCH", "/api/v1/admin/places/" + id, json.writeValueAsString(input), ifMatch,
				"RENAME_PLACE", 200, "PLACE_UPDATED");
		return commands.execute(accountId, key, command, () -> {
			// Chỉ kiểm điều kiện mới sau nhánh replay, If-Match gốc có thể đã cũ sau lần commit đầu
			long expected = AdminPlacePreconditions.version(ifMatch);
			Place place = places.findByIdForUpdate(id)
					.orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
			if (place.getVersion() != expected)
				throw new AdminCommandException(412, "VERSION_CONFLICT");

			String name = name(input.displayName());
			boolean duplicate = places.countWithEquivalentNameExcluding(name, id) > 0;
			// Khớp độ chính xác DATETIME(6), không đổi trạng thái hiển thị hoặc con trỏ vùng
			place.rename(name, clock.instant().truncatedTo(ChronoUnit.MICROS));
			places.flush();
			return Map.of("placeId", id.toString(), "version", place.getVersion(), "visible", place.isVisible(),
					"duplicateNameWarning", duplicate, "updatedAt", place.getUpdatedAt().toString());
		});
	}

	private String name(String raw) {
		try {
			return DisplayNamePolicy.normalize(raw, 100);
		}
		catch (IllegalArgumentException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("displayName", error.getMessage()));
		}
	}

	private AdminPlaceResponse response(Place place) {
		var missing = visibility.conditions(place, false);
		return new AdminPlaceResponse(place.getId(), place.getDisplayName(), place.isVisible(), place.getActiveRegionRevisionId(),
				place.getVersion(), place.getCreatedAt(), place.getUpdatedAt(), missing.isEmpty(), missing);
	}
}
