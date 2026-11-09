package vn.hueqr.backend.dto;

import java.util.UUID;

public record GuestSelectionRequest(UUID placeId, String language, UUID documentNonce, Long selectionSeq) {
}
