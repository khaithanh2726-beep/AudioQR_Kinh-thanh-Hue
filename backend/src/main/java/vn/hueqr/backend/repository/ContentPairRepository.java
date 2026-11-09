package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.ContentPair;

public interface ContentPairRepository extends JpaRepository<ContentPair, UUID> {
	Optional<ContentPair> findByIdAndSlot_Id(UUID id, UUID slotId);

	Optional<ContentPair> findBySlot_IdAndTextRevisionIdAndAudioRevisionId(UUID slotId, UUID textRevisionId, UUID audioRevisionId);

	@Query("select count(pair) from ContentPair pair, ContentSlot slot, AudioRevision audio "
			+ "where slot.activePairId = pair.id and pair.audioRevisionId = audio.id and (audio.wavFileId = :fileId or audio.mp3FileId = :fileId)")
	long countAppliedFileReferences(@Param("fileId") UUID fileId);
}
