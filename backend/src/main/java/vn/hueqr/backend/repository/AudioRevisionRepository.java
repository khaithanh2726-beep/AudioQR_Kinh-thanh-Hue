package vn.hueqr.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.AudioRevision;

public interface AudioRevisionRepository extends JpaRepository<AudioRevision, UUID> {
	Optional<AudioRevision> findByJobId(UUID jobId);

	List<AudioRevision> findByTextRevision_IdOrderByCreatedAtAscIdAsc(UUID textRevisionId);

	@Query("select audio.textRevision.slot.place.id from AudioRevision audio where audio.id = :id")
	Optional<UUID> findPlaceIdById(@Param("id") UUID id);
}
