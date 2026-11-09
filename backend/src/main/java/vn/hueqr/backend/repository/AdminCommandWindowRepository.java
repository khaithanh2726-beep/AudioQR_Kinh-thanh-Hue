package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.AdminCommandWindow;

public interface AdminCommandWindowRepository extends JpaRepository<AdminCommandWindow, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select commandWindow from AdminCommandWindow commandWindow where commandWindow.id = :id")
	Optional<AdminCommandWindow> findByIdForUpdate(@Param("id") UUID id);
}
