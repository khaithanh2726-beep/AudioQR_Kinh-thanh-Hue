package vn.hueqr.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GuestContentResponse(UUID openingId, long openingVersion, String selectionKind,
		PlaceSummary place, String language, String source, String availability, List<String> availableLanguages,
		TextContent text, AudioContent audio, GrantSummary grant, Instant serverNow) {
	public record PlaceSummary(UUID id, String name) {
	}

	public record TextContent(UUID revisionId, String hash, String body) {
	}

	public record AudioContent(UUID revisionId, UUID mp3FileId, String checksum, long byteSize,
			BigDecimal durationSeconds, String availability) {
	}

	public record GrantSummary(UUID id, Instant expiresAt, boolean viewEligible) {
	}
}
