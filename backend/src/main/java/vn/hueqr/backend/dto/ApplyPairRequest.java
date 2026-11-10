package vn.hueqr.backend.dto;

public record ApplyPairRequest(String textRevisionId, String audioRevisionId, String expectedTextHash,
		String expectedMp3Checksum, Long textReviewVersion, Long audioReviewVersion) {
}
