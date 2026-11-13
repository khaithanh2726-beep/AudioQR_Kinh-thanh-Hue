package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import vn.hueqr.backend.entity.TextRevision;

public interface TextRevisionRepository extends JpaRepository<TextRevision, UUID> {
	Optional<TextRevision> findByIdAndSlot_Id(UUID id, UUID slotId);

	@Query("select text.slot.place.id from TextRevision text where text.id = :id")
	Optional<UUID> findPlaceIdById(@Param("id") UUID id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select text from TextRevision text where text.id = :id")
	Optional<TextRevision> findByIdForUpdate(@Param("id") UUID id);
}
