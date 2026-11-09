package vn.hueqr.backend.repository;

import java.util.UUID;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
	java.util.List<Place> findAllByVisibleTrueOrderByIdAsc();

	// Q1 bỏ phân biệt hoa thường nhưng vẫn phân biệt dấu, khác collation mặc định của bảng
	@Query(value = "SELECT COUNT(*) FROM place WHERE display_name COLLATE utf8mb4_0900_as_ci = :name", nativeQuery = true)
	long countWithEquivalentName(@Param("name") String name);

	@Query(value = "SELECT COUNT(*) FROM place WHERE display_name COLLATE utf8mb4_0900_as_ci = :name AND id <> :excludedId", nativeQuery = true)
	long countWithEquivalentNameExcluding(@Param("name") String name, @Param("excludedId") UUID excludedId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select place from Place place where place.id = :id")
	Optional<Place> findByIdForUpdate(@Param("id") UUID id);
}
