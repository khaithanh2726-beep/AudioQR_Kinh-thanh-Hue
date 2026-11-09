package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.FileAsset;

public interface FileAssetRepository extends JpaRepository<FileAsset, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select file from FileAsset file where file.id = :id")
	Optional<FileAsset> findByIdForUpdate(@Param("id") UUID id);
}
