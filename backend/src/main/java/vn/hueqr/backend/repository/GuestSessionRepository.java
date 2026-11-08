package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.GuestSession;

public interface GuestSessionRepository extends JpaRepository<GuestSession, UUID> {
	Optional<GuestSession> findByStartRequestKey(UUID startRequestKey);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select guestSession from GuestSession guestSession where guestSession.id = :id")
	Optional<GuestSession> findByIdForUpdate(@Param("id") UUID id);
}
