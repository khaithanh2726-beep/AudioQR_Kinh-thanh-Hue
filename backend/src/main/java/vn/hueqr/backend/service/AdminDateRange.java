package vn.hueqr.backend.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class AdminDateRange {
	public record Range(LocalDate fromDate, LocalDate toDate, Instant start, Instant endExclusive) {
	}

	private static final ZoneId ZONE = ZoneId.of("Asia/Saigon");
	private final Clock clock;

	public AdminDateRange(Clock clock) {
		this.clock = clock;
	}

	public Range parse(String from, String to) {
		LocalDate today = LocalDate.now(clock.withZone(ZONE));
		LocalDate first = date(from, today, "fromDate");
		LocalDate last = date(to, today, "toDate");
		if (first.isAfter(last) || last.equals(LocalDate.MAX))
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of("toDate", "Ngày cuối phải từ ngày đầu trở đi và có ngày kế tiếp hợp lệ."));
		// Cận trên là đầu ngày kế tiếp, giữ cả phần micro giây cuối ngày và không đặt giới hạn khoảng ngày mới
		return new Range(first, last, first.atStartOfDay(ZONE).toInstant(), last.plusDays(1).atStartOfDay(ZONE).toInstant());
	}

	private LocalDate date(String value, LocalDate fallback, String field) {
		if (value == null)
			return fallback;
		try {
			LocalDate parsed = LocalDate.parse(value);
			if (!parsed.toString().equals(value))
				throw new DateTimeParseException("NON_CANONICAL_DATE", value, 0);
			return parsed;
		}
		catch (DateTimeParseException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED", Map.of(field, "Cần ngày ISO hợp lệ dạng yyyy-MM-dd."));
		}
	}
}
