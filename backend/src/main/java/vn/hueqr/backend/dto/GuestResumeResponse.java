package vn.hueqr.backend.dto;

import java.util.UUID;

public record GuestResumeResponse(SessionResponse session, UUID documentNonce, long selectionSeq,
		UUID currentOpening, GuestContentResponse content, String restoreReason) {
}
