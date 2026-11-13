package vn.hueqr.backend.service;

import java.time.Duration;
import java.time.Instant;

public final class GuestRetentionPolicy {
	public static final Duration RETENTION = Duration.ofDays(30);

	private GuestRetentionPolicy() {
	}

	public static Instant anchor(Instant startedAt, Instant expiresAt, Instant endedAt, Instant now) {
		// Mốc kết thúc lỗi hoặc nằm trong tương lai không được đẩy lùi hay rút ngắn hạn lưu
		if (endedAt != null && !endedAt.isBefore(startedAt) && !endedAt.isAfter(expiresAt) && !endedAt.isAfter(now))
			return endedAt;
		return expiresAt;
	}
}
