package vn.hueqr.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.hueqr.backend.entity.PlaceView;

public interface PlaceViewRepository extends JpaRepository<PlaceView, UUID>, JpaSpecificationExecutor<PlaceView> {
	List<PlaceView> findBySession_IdOrderByViewedAtAscIdAsc(UUID sessionId);
}
