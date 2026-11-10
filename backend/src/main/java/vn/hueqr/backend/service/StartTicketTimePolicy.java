package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class StartTicketTimePolicy {
	private static final Duration TICKET_LIFETIME = Duration.ofHours(24);

	private final Clock clock;

	public StartTicketTimePolicy(Clock clock) {
		this.clock = Objects.requireNonNull(clock, "clock");
	}

	public TicketTimes issueTimes() {
		// Cắt đến micro giây để mốc ký khớp độ chính xác DATETIME(6) khi lưu
		Instant issuedAt = clock.instant().truncatedTo(ChronoUnit.MICROS);
		return new TicketTimes(issuedAt, issuedAt.plus(TICKET_LIFETIME));
	}

	// Kiểm các mốc của vé để tra lại phiên cũ, kể cả khi vé đã hết hạn
	public void validateTimes(TicketTimes times) {
		validateTimesAt(times, clock.instant());
	}

	// Chỉ xét hết hạn khi tạo phiên lần đầu, tại đúng hạn vé đã hết hiệu lực
	public boolean isExpiredForFirstCreation(TicketTimes times) {
		Instant now = clock.instant();
		validateTimesAt(times, now);

		return !now.isBefore(times.ticketExpiresAt());
	}

	private static void validateTimesAt(TicketTimes times, Instant now) {
		Objects.requireNonNull(times, "times");
		if (!times.issuedAt().plus(TICKET_LIFETIME).equals(times.ticketExpiresAt())) {
			throw new IllegalArgumentException("Ticket lifetime must be exactly 24 hours");
		}

		if (times.issuedAt().isAfter(now)) {
			throw new IllegalArgumentException("Ticket issuedAt must not be in the future");
		}
	}

	public record TicketTimes(Instant issuedAt, Instant ticketExpiresAt) {
		public TicketTimes {
			Objects.requireNonNull(issuedAt, "issuedAt");
			Objects.requireNonNull(ticketExpiresAt, "ticketExpiresAt");
		}
	}
}
