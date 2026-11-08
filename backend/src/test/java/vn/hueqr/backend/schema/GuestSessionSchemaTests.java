package vn.hueqr.backend.schema;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class GuestSessionSchemaTests {
	private static final String DISPLAY_NAME = "Khách thử nghiệm";
	private static final String SERVICE_EPOCH = "00000000-0000-0000-0000-000000000001";
	private static final LocalDateTime TICKET_ISSUED_AT = LocalDateTime.of(2026, 11, 5, 0, 0);
	private static final LocalDateTime STARTED_AT = TICKET_ISSUED_AT.plusMinutes(5);

	private static final String INSERT_SESSION = """
			INSERT INTO guest_session (
			    id, start_request_key, credential_hash, service_epoch,
			    start_fingerprint, start_ticket_issued_at, start_ticket_expires_at,
			    start_ticket_digest, display_name, initial_language,
			    started_at, expires_at, ended_at
			) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)
			""";

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void allowsIndependentSessionsWithTheSameVietnameseDisplayName() {
		UUID firstId = UUID.randomUUID();
		insertSession(firstId, UUID.randomUUID(), hashFixture(1), "vi");
		insertSession(UUID.randomUUID(), UUID.randomUUID(), hashFixture(2), "vi");

		assertThat(jdbc.queryForObject(
				"SELECT COUNT(*) FROM guest_session WHERE display_name = ?",
				Long.class, DISPLAY_NAME)).isEqualTo(2L);
		assertThat(jdbc.queryForObject(
				"SELECT display_name FROM guest_session WHERE id = ?",
				String.class, uuidBytes(firstId))).isEqualTo(DISPLAY_NAME);
	}

	@Test
	void rejectsDuplicateStartRequestKey() {
		UUID requestKey = UUID.randomUUID();
		insertSession(UUID.randomUUID(), requestKey, hashFixture(1), "vi");

		assertRejectedByConstraint(
				() -> insertSession(UUID.randomUUID(), requestKey, hashFixture(2), "vi"),
				1062, "uk_guest_session_start_request_key");
	}

	@Test
	void rejectsDuplicateCredentialHash() {
		byte[] credentialHash = hashFixture(1);
		insertSession(UUID.randomUUID(), UUID.randomUUID(), credentialHash, "vi");

		assertRejectedByConstraint(
				() -> insertSession(UUID.randomUUID(), UUID.randomUUID(), credentialHash, "vi"),
				1062, "uk_guest_session_credential_hash");
	}

	@Test
	void rejectsLanguageOutsideTheSupportedSet() {
		// Mã de có hai ký tự để kiểm đúng ràng buộc CHECK của ngôn ngữ
		assertRejectedByConstraint(
				() -> insertSession(UUID.randomUUID(), UUID.randomUUID(), hashFixture(1), "de"),
				3819, "ck_guest_session_initial_language");
	}

	private void insertSession(UUID id, UUID requestKey, byte[] credentialHash, String language) {
		assertThat(jdbc.update(INSERT_SESSION,
				uuidBytes(id), uuidBytes(requestKey), credentialHash, SERVICE_EPOCH,
				hashFixture(3), Timestamp.valueOf(TICKET_ISSUED_AT),
				Timestamp.valueOf(TICKET_ISSUED_AT.plusHours(24)), hashFixture(4),
				DISPLAY_NAME, language, Timestamp.valueOf(STARTED_AT),
				Timestamp.valueOf(STARTED_AT.plusHours(8)))).isEqualTo(1);
	}

	private static void assertRejectedByConstraint(Runnable failedInsert, int errorCode, String constraint) {
		DataAccessException error = assertThrows(DataAccessException.class, failedInsert::run);
		assertThat(error.getMostSpecificCause()).isInstanceOf(SQLException.class);

		SQLException sqlError = (SQLException) error.getMostSpecificCause();
		assertThat(sqlError.getErrorCode()).isEqualTo(errorCode);
		assertThat(sqlError.getMessage()).contains(constraint);
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
