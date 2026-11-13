package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.GuestSession;

public interface GuestSessionRepository extends JpaRepository<GuestSession, UUID>, JpaSpecificationExecutor<GuestSession> {
	Optional<GuestSession> findByStartRequestKey(UUID startRequestKey);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select guestSession from GuestSession guestSession where guestSession.id = :id")
	Optional<GuestSession> findByIdForUpdate(@Param("id") UUID id);

	@Modifying
	@Query(value = "UPDATE guest_content_grant SET revoked_at = :at WHERE session_id = :id AND revoked_at IS NULL", nativeQuery = true)
	void revokeGrants(@Param("id") UUID id, @Param("at") java.time.Instant at);

	@Modifying
	@Query(value = "UPDATE guest_open_intent SET state = 'CLOSED', closed_at = :at, version = version + 1 WHERE session_id = :id AND state = 'OPEN'", nativeQuery = true)
	void closeOpenings(@Param("id") UUID id, @Param("at") java.time.Instant at);
}
