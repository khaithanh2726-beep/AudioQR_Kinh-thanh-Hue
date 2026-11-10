package vn.hueqr.backend.dto;

import java.util.UUID;

public record GuestLeaveRequest(UUID documentNonce, Long selectionSeq) {
}
