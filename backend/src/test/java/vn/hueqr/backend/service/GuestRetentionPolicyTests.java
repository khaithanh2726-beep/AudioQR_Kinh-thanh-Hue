package vn.hueqr.backend.service;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuestRetentionPolicyTests {
	private static final Instant START = Instant.parse("2026-10-01T00:00:00Z");
	private static final Instant EXPIRY = START.plus(Duration.ofHours(8));
	private static final Instant NOW = EXPIRY.plus(Duration.ofDays(30));

	@Test
	void expiryIsTheAnchorWhenTheVisitorDidNotEndEarly() {
		assertThat(GuestRetentionPolicy.anchor(START, EXPIRY, null, NOW)).isEqualTo(EXPIRY);
	}

	@Test
	void aValidEarlyEndIsTheAnchorWithoutWaitingForTheOriginalExpiry() {
		Instant ended = START.plusSeconds(600);
		assertThat(GuestRetentionPolicy.anchor(START, EXPIRY, ended, NOW)).isEqualTo(ended);
	}

	@Test
	void anEndBeforeStartCannotCauseEarlyDeletion() {
		assertThat(GuestRetentionPolicy.anchor(START, EXPIRY, START.minusSeconds(1), NOW)).isEqualTo(EXPIRY);
	}

	@Test
	void anEndAfterExpiryCannotExtendRetention() {
		assertThat(GuestRetentionPolicy.anchor(START, EXPIRY, EXPIRY.plusSeconds(1), NOW)).isEqualTo(EXPIRY);
	}

	@Test
	void aFutureEndIsNotAcceptedAsEvidenceThatTheSessionHasFinished() {
		assertThat(GuestRetentionPolicy.anchor(START, EXPIRY, START.plusSeconds(60), START)).isEqualTo(EXPIRY);
	}

	@Test
	void immediateEndAndEndAtExpiryKeepTheirOriginalAnchors() {
		assertThat(GuestRetentionPolicy.anchor(START, EXPIRY, START, NOW)).isEqualTo(START);
		assertThat(GuestRetentionPolicy.anchor(START, EXPIRY, EXPIRY, NOW)).isEqualTo(EXPIRY);
	}

	@Test
	void retentionIsThirtyElapsedDaysAcrossCalendarBoundaries() {
		assertThat(GuestRetentionPolicy.RETENTION).isEqualTo(Duration.ofHours(720));
		assertThat(Instant.parse("2026-01-31T23:00:00Z").plus(GuestRetentionPolicy.RETENTION))
				.isEqualTo(Instant.parse("2026-03-02T23:00:00Z"));
	}
}
