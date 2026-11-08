package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import vn.hueqr.backend.service.StartTicketTimePolicy.TicketTimes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StartTicketTimePolicyTests {
	private static final Instant ISSUED_AT = Instant.parse("2026-11-05T00:00:00.123456Z");
	private static final Instant EXPIRES_AT = Instant.parse("2026-11-06T00:00:00.123456Z");
	private static final TicketTimes TIMES = new TicketTimes(ISSUED_AT, EXPIRES_AT);

	@Test
	void issuesExactly24HoursAtMicrosecondPrecision() {
		StartTicketTimePolicy policy = policyAt(Instant.parse("2026-11-05T00:00:00.123456789Z"));

		TicketTimes issued = policy.issueTimes();

		assertThat(issued.issuedAt()).isEqualTo(ISSUED_AT);
		assertThat(issued.ticketExpiresAt()).isEqualTo(EXPIRES_AT);
	}

	@Test
	void usesTheSameInstantRegardlessOfClockZone() {
		StartTicketTimePolicy utc = policyAt(ISSUED_AT);
		StartTicketTimePolicy vietnam = new StartTicketTimePolicy(
				Clock.fixed(ISSUED_AT, ZoneId.of("Asia/Ho_Chi_Minh")));

		assertThat(vietnam.issueTimes()).isEqualTo(TIMES);
		assertThat(vietnam.issueTimes()).isEqualTo(utc.issueTimes());
	}

	@Test
	void isNotExpiredOneMicrosecondBeforeTheDeadline() {
		StartTicketTimePolicy policy = policyAt(EXPIRES_AT.minusNanos(1_000));

		assertThat(policy.isExpiredForFirstCreation(TIMES)).isFalse();
	}

	@Test
	void isExpiredAtTheExactDeadline() {
		StartTicketTimePolicy policy = policyAt(EXPIRES_AT);

		assertThat(policy.isExpiredForFirstCreation(TIMES)).isTrue();
	}

	@Test
	void isExpiredAfterTheDeadline() {
		StartTicketTimePolicy policy = policyAt(EXPIRES_AT.plusNanos(1_000));

		assertThat(policy.isExpiredForFirstCreation(TIMES)).isTrue();
	}

	@Test
	void validatesExpiredTimesWithoutBlockingLaterLedgerLookup() {
		StartTicketTimePolicy policy = policyAt(Instant.parse("2026-12-01T00:00:00Z"));

		assertThatCode(() -> policy.validateTimes(TIMES)).doesNotThrowAnyException();
		assertThat(policy.isExpiredForFirstCreation(TIMES)).isTrue();
	}

	@Test
	void rejectsIssuedAtInTheFuture() {
		StartTicketTimePolicy policy = policyAt(ISSUED_AT.minusNanos(1_000));

		assertThatThrownBy(() -> policy.validateTimes(TIMES))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("future");
		assertThatThrownBy(() -> policy.isExpiredForFirstCreation(TIMES))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("future");
	}

	@Test
	void rejectsLifetimeShorterOrLongerThan24Hours() {
		StartTicketTimePolicy policy = policyAt(ISSUED_AT);
		TicketTimes tooShort = new TicketTimes(ISSUED_AT, EXPIRES_AT.minusNanos(1_000));
		TicketTimes tooLong = new TicketTimes(ISSUED_AT, EXPIRES_AT.plusNanos(1_000));

		assertThatThrownBy(() -> policy.validateTimes(tooShort))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("24 hours");
		assertThatThrownBy(() -> policy.validateTimes(tooLong))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("24 hours");
	}

	private static StartTicketTimePolicy policyAt(Instant now) {
		return new StartTicketTimePolicy(Clock.fixed(now, ZoneOffset.UTC));
	}
}
