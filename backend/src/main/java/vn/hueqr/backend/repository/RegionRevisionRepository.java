package vn.hueqr.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hueqr.backend.entity.RegionRevision;

public interface RegionRevisionRepository extends JpaRepository<RegionRevision, UUID> {
}
