package vn.hueqr.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hueqr.backend.entity.AudioReviewReference;

public interface AudioReviewReferenceRepository extends JpaRepository<AudioReviewReference, UUID> {
}
