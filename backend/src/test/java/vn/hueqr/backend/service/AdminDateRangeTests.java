package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminDateRangeTests {
	private final AdminDateRange dates = new AdminDateRange(Clock.fixed(Instant.parse("2026-11-08T18:00:00Z"), ZoneOffset.UTC));

	@Test
	void defaultTodayUsesSaigonRatherThanTheUtcDate() {
		var result = dates.parse(null, null);
		assertThat(result.fromDate().toString()).isEqualTo("2026-11-09");
		assertThat(result.start()).isEqualTo(Instant.parse("2026-11-08T17:00:00Z"));
		assertThat(result.endExclusive()).isEqualTo(Instant.parse("2026-11-09T17:00:00Z"));
	}

	@Test
	void inclusiveLocalDatesBecomeHalfOpenUtcBoundsAcrossMonthAndYear() {
		var result = dates.parse("2025-12-31", "2026-01-01");
		assertThat(result.start()).isEqualTo(Instant.parse("2025-12-30T17:00:00Z"));
		assertThat(result.endExclusive()).isEqualTo(Instant.parse("2026-01-01T17:00:00Z"));
	}

	@Test
	void rejectsInvalidNoncanonicalBlankAndReversedDatesWithoutInventingAMaxRange() {
		for (String invalid : new String[]{"", "2026-2-01", "2026-02-29", "09/11/2026", "2026-11-09 "})
			assertThat(assertThrows(AdminCommandException.class, () -> dates.parse(invalid, null)).fields()).containsKey("fromDate");
		assertThat(assertThrows(AdminCommandException.class, () -> dates.parse("2026-11-10", "2026-11-09")).status()).isEqualTo(422);
		assertThat(dates.parse("2000-01-01", "2026-11-09").fromDate().getYear()).isEqualTo(2000);
	}

	@Test
	void supportsLeapDayAndDefaultsOnlyTheMissingBound() {
		assertThat(dates.parse("2024-02-29", "2024-02-29").endExclusive()).isEqualTo(Instant.parse("2024-02-29T17:00:00Z"));
		assertThat(dates.parse("2026-11-08", null).toDate().toString()).isEqualTo("2026-11-09");
	}
}
