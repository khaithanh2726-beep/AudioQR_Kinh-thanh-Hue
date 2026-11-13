package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import vn.hueqr.backend.repository.ContentPairRepository;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.PlaceDeletionRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;

@Service
public class AdminPlaceDeletionService {
	private final PlaceRepository places;
	private final PlaceDeletionRepository deletion;
	private final FileAssetRepository files;
	private final ContentPairRepository pairs;
	private final AdminCommandService commands;
	private final Clock clock;

	public AdminPlaceDeletionService(PlaceRepository places, PlaceDeletionRepository deletion, FileAssetRepository files,
			ContentPairRepository pairs, AdminCommandService commands, Clock clock) {
		this.places = places;
		this.deletion = deletion;
		this.files = files;
		this.pairs = pairs;
		this.commands = commands;
		this.clock = clock;
	}

	public vn.hueqr.backend.dto.AdminOperationResponse delete(UUID account, String key, String placeId, String ifMatch) {
		UUID id = AdminPlacePreconditions.placeId(placeId);
		var command = new Command("DELETE", "/api/v1/admin/places/" + id, "", ifMatch, "DELETE_PLACE", 200, "PLACE_DELETED");
		return commands.execute(account, key, command, () -> {
			long version = AdminPlacePreconditions.version(ifMatch);
			var place = places.findByIdForUpdate(id).orElseThrow(() -> new AdminCommandException(404, "LOCATION_NOT_FOUND"));
			if (place.getVersion() != version)
				throw new AdminCommandException(412, "VERSION_CONFLICT");
			var assets = deletion.fileIds(id).stream().sorted().map(fid -> files.findByIdForUpdate(fid).orElseThrow()).toList();
			var applied = Set.copyOf(deletion.appliedFileIds(id));
			// Cắt đến micro giây để mốc ngừng áp dụng khớp DATETIME(6)
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			deletion.deletePrivateData(id, now);
			for (var file : assets)
				if (applied.contains(file.getId()) && pairs.countAppliedFileReferences(file.getId()) == 0)
					file.markUnapplied(now);
			files.flush();
			deletion.deletePlace(id);
			return Map.of("placeId", id.toString(), "databaseState", "DELETED", "fileCleanup", Map.of("state", "PENDING"), "historyPreserved", true);
		});
	}
}
