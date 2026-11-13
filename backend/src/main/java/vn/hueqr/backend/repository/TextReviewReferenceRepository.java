package vn.hueqr.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hueqr.backend.entity.TextReviewReference;

public interface TextReviewReferenceRepository extends JpaRepository<TextReviewReference, UUID> {
}
