package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.ContentSlot;

public interface ContentSlotRepository extends JpaRepository<ContentSlot, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select slot from ContentSlot slot where slot.place.id = :placeId and slot.language = :language")
	Optional<ContentSlot> findByPlace_IdAndLanguageForUpdate(@Param("placeId") UUID placeId,
			@Param("language") String language);

	Optional<ContentSlot> findByPlace_IdAndLanguage(UUID placeId, String language);
}
