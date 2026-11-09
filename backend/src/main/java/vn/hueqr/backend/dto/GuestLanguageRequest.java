package vn.hueqr.backend.dto;

import java.util.UUID;

public record GuestLanguageRequest(String language, UUID documentNonce, Long selectionSeq) {
}
