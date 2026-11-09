package vn.hueqr.backend.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hueqr.backend.entity.TtsJob;

public interface TtsJobRepository extends JpaRepository<TtsJob, UUID> {
	Optional<TtsJob> findFirstByPhaseAndTargetValidTrueOrderByAcceptedAtAscIdAsc(String phase);

	interface Target {
		UUID getTargetTextRevisionId();

		UUID getPlaceKeySnapshot();
	}

	@Query("select job.targetTextRevisionId as targetTextRevisionId, job.placeKeySnapshot as placeKeySnapshot from TtsJob job where job.id = :id")
	Optional<Target> findTarget(@Param("id") UUID id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select job from TtsJob job where job.id = :id")
	Optional<TtsJob> findByIdForUpdate(@Param("id") UUID id);
}
