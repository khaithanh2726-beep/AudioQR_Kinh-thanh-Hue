package vn.hueqr.backend.repository;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import vn.hueqr.backend.entity.GuestSession;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class GuestSessionRepositoryTests {
	@Autowired
	private GuestSessionRepository sessions;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void savesAndReloadsStartSessionByRequestKeyWithUtcMicroseconds() {
		UUID id = UUID.randomUUID();
		UUID requestKey = UUID.randomUUID();
		byte[] credentialHash = hashFixture(1);
		byte[] fingerprint = hashFixture(2);
		byte[] ticketDigest = hashFixture(3);
		String serviceEpoch = "00000000-0000-0000-0000-000000000001";
		Instant ticketIssuedAt = Instant.parse("2026-11-05T00:00:00.123456Z");
		Instant ticketExpiresAt = ticketIssuedAt.plus(Duration.ofHours(24));
		Instant startedAt = ticketIssuedAt.plus(Duration.ofMinutes(5));
		Instant expiresAt = startedAt.plus(Duration.ofHours(8));

		sessions.saveAndFlush(new GuestSession(id, requestKey, credentialHash, serviceEpoch,
				fingerprint, ticketIssuedAt, ticketExpiresAt, ticketDigest,
				"Khách thử nghiệm", "vi", startedAt, expiresAt));
		// Xóa bộ nhớ đệm quản lý Entity để lần tìm tiếp theo đọc từ database
		entityManager.clear();

		GuestSession loaded = sessions.findByStartRequestKey(requestKey).orElseThrow();
		assertThat(loaded.getId()).isEqualTo(id);
		assertThat(loaded.getStartRequestKey()).isEqualTo(requestKey);
		assertThat(loaded.getCredentialHash()).containsExactly(credentialHash);
		assertThat(loaded.getServiceEpoch()).isEqualTo(serviceEpoch);
		assertThat(loaded.getStartFingerprint()).containsExactly(fingerprint);
		assertThat(loaded.getStartTicketIssuedAt()).isEqualTo(ticketIssuedAt);
		assertThat(loaded.getStartTicketExpiresAt()).isEqualTo(ticketExpiresAt);
		assertThat(loaded.getStartTicketDigest()).containsExactly(ticketDigest);
		assertThat(loaded.getDisplayName()).isEqualTo("Khách thử nghiệm");
		assertThat(loaded.getInitialLanguage()).isEqualTo("vi");
		assertThat(loaded.getStartedAt()).isEqualTo(startedAt);
		assertThat(loaded.getExpiresAt()).isEqualTo(expiresAt);
		assertThat(loaded.getEndedAt()).isNull();

		// Kiểm cả giá trị UTC trong MySQL để phát hiện sai múi giờ khi ghi và đọc
		LocalDateTime storedStartedAt = jdbc.queryForObject(
				"SELECT started_at FROM guest_session WHERE id = ?",
				LocalDateTime.class, uuidBytes(id));
		assertThat(storedStartedAt).isEqualTo(LocalDateTime.ofInstant(startedAt, ZoneOffset.UTC));
	}

	@Test
	void returnsEmptyWhenStartRequestKeyDoesNotExist() {
		assertThat(sessions.findByStartRequestKey(UUID.randomUUID())).isEmpty();
	}

	private static byte[] uuidBytes(UUID value) {
		return ByteBuffer.allocate(16)
				.putLong(value.getMostSignificantBits())
				.putLong(value.getLeastSignificantBits())
				.array();
	}

	private static byte[] hashFixture(int marker) {
		byte[] value = new byte[32];
		Arrays.fill(value, (byte) marker);
		return value;
	}
}
