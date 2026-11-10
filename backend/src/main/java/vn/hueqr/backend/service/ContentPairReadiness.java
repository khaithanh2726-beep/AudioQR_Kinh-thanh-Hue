package vn.hueqr.backend.service;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.hueqr.backend.entity.AudioRevision;
import vn.hueqr.backend.entity.ContentSlot;
import vn.hueqr.backend.entity.FileAsset;
import vn.hueqr.backend.entity.TextRevision;
import vn.hueqr.backend.repository.AudioReviewReferenceRepository;
import vn.hueqr.backend.repository.FileAssetRepository;
import vn.hueqr.backend.repository.TextReviewReferenceRepository;

@Component
public class ContentPairReadiness {
	private final FileAssetRepository files;
	private final TextReviewReferenceRepository textReviews;
	private final AudioReviewReferenceRepository audioReviews;
	private final Path root;

	public ContentPairReadiness(FileAssetRepository files, TextReviewReferenceRepository textReviews,
			AudioReviewReferenceRepository audioReviews, @Value("${HUEQR_AUDIO_ROOT:}") String root) {
		this.files = files;
		this.textReviews = textReviews;
		this.audioReviews = audioReviews;
		this.root = root.isBlank() ? null : Path.of(root);
	}

	public Set<UUID> fileIds(AudioRevision audio) {
		return Set.of(audio.getWavFileId(), audio.getMp3FileId());
	}

	public Map<UUID, FileAsset> loadFiles(Collection<UUID> ids, boolean lock) {
		var result = new LinkedHashMap<UUID, FileAsset>();
		// Thứ tự cố định tránh hai điểm dùng chung file lấy khóa FILE ngược nhau
		for (UUID id : ids.stream().distinct().sorted().toList())
			(lock ? files.findByIdForUpdate(id) : files.findById(id)).ifPresent(file -> result.put(id, file));
		return result;
	}

	public List<String> conditions(ContentSlot slot, TextRevision text, AudioRevision audio, Map<UUID, FileAsset> assets) {
		var missing = new ArrayList<String>();
		if (!text.getSlot().getId().equals(slot.getId()) || !audio.getTextRevision().getId().equals(text.getId()))
			missing.add("PAIR_REVISION_MISMATCH");
		if (textReviews.findById(text.getId()).isEmpty())
			missing.add("TEXT_REVIEW_REFERENCE_MISSING");
		if (audioReviews.findById(audio.getId()).isEmpty())
			missing.add("AUDIO_REVIEW_REFERENCE_MISSING");
		if (!filesReady(audio, assets))
			missing.add("AUDIO_FILE_NOT_READY");
		return List.copyOf(missing);
	}

	public boolean filesReady(AudioRevision audio, Map<UUID, FileAsset> assets) {
		return ready(assets.get(audio.getWavFileId()), "WAV") && ready(assets.get(audio.getMp3FileId()), "MP3");
	}

	private boolean ready(FileAsset file, String format) {
		if (file == null || !"READY".equals(file.getState()) || !format.equals(file.getFormat())
				|| root == null || !root.isAbsolute() || file.getByteSize() <= 0 || file.getDuration().signum() <= 0)
			return false;
		try {
			Path directory = root.toRealPath();
			Path path = directory.resolve(file.getStorageKey()).normalize();
			if (!directory.equals(path.getParent()) || !file.getStorageKey().equals(path.getFileName().toString()))
				return false;
			// Trong lệnh ghi, giữ khóa FILE xuyên suốt stat và commit để cleaner cùng protocol không chen vào
			return Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) && Files.size(path) == file.getByteSize();
		}
		catch (java.io.IOException | IllegalArgumentException | SecurityException error) {
			return false;
		}
	}

	public static void require(List<String> missing) {
		if (!missing.isEmpty())
			throw new AdminCommandException(409, "REQUIREMENTS_NOT_MET", Map.of(), missing);
	}
}
