package vn.hueqr.backend.support;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration(proxyBeanMethods = false)
public class GuestSessionTestClock {
	public static final Instant BASE = Instant.parse("2026-11-05T00:00:00.123456Z");

	@Bean
	@Primary
	MutableClock testGuestClock() {
		return new MutableClock();
	}

	public static class MutableClock extends Clock {
		private final AtomicReference<Instant> now = new AtomicReference<>(BASE);

		public void set(Instant value) {
			now.set(value);
		}

		@Override
		public ZoneId getZone() {
			return ZoneOffset.UTC;
		}

		// Đồng hồ đổi múi giờ giữ mốc tại lúc gọi, tách khỏi các lần set sau
		@Override
		public Clock withZone(ZoneId zone) {
			return Clock.fixed(instant(), zone);
		}

		@Override
		public Instant instant() {
			return now.get();
		}
	}
}

