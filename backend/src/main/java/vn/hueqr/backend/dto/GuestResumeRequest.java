package vn.hueqr.backend.dto;

import java.util.UUID;

public record GuestResumeRequest(UUID documentNonce, UUID previousDocumentNonce) {
}
