package vn.hueqr.backend.schema;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class AdminCommandSchemaTests {
	@Autowired
	private JdbcTemplate jdbc;

	private UUID account;
	private UUID otherAccount;
	private UUID window;

	@BeforeEach
	void insertFixtures() {
		account = UUID.randomUUID();
		otherAccount = UUID.randomUUID();
		window = UUID.randomUUID();
		for (UUID id : new UUID[]{account, otherAccount}) {
			jdbc.update("INSERT INTO admin_account (id, login_name, password_hash) VALUES (?, ?, ?)",
					uuidBytes(id), id.toString(), "{noop}schema-test-only");
		}
		jdbc.update("""
				INSERT INTO admin_command_window (id, admin_account_id, service_epoch, issued_at, expires_at)
				VALUES (?, ?, ?, ?, ?)
				""", uuidBytes(window), uuidBytes(account), "00000000-0000-0000-0000-000000000001",
				Timestamp.from(Instant.parse("2026-11-08T00:00:00Z")), Timestamp.from(Instant.parse("2026-11-09T00:00:00Z")));
	}

	@Test
	void receiptMustBelongToTheSameAccountAsItsWindow() {
		assertRejected(() -> insertReceipt(otherAccount, window, UUID.randomUUID()), 1452, "fk_receipt_window_account");
	}

	@Test
	void receiptRequiresAnExistingWindow() {
		assertRejected(() -> insertReceipt(account, UUID.randomUUID(), UUID.randomUUID()), 1452, "fk_receipt_window_account");
	}

	@Test
	void aCommittedAccountWindowAndOperationKeyCannotBeInsertedTwice() {
		UUID operation = UUID.randomUUID();
		insertReceipt(account, window, operation);

		assertRejected(() -> insertReceipt(account, window, operation), 1062, "uk_receipt_account_window_key");
	}

	@Test
	void referencesRequireExplicitCleanupBeforeDeletingWindowsOrAccounts() {
		insertReceipt(account, window, UUID.randomUUID());

		assertRejected(() -> jdbc.update("DELETE FROM admin_command_window WHERE id = ?", uuidBytes(window)),
				1451, "fk_receipt_window_account");
		assertRejected(() -> jdbc.update("DELETE FROM admin_account WHERE id = ?", uuidBytes(account)),
				1451, "fk_admin_window_account");
	}

	private void insertReceipt(UUID owner, UUID windowId, UUID operation) {
		jdbc.update("""
				INSERT INTO operation_receipt (id, window_id, admin_account_id, operation_key, fingerprint,
				    operation_type, http_status, result_code, result_summary, committed_at)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""", uuidBytes(UUID.randomUUID()), uuidBytes(windowId), uuidBytes(owner), uuidBytes(operation),
				new byte[32], "CREATE_PLACE", 201, "PLACE_CREATED", "{}", Timestamp.from(Instant.parse("2026-11-08T00:00:01Z")));
	}

	private static void assertRejected(Runnable command, int code, String constraint) {
		DataAccessException error = assertThrows(DataAccessException.class, command::run);
		assertThat(error.getMostSpecificCause()).isInstanceOf(SQLException.class);
		SQLException cause = (SQLException) error.getMostSpecificCause();
		assertThat(cause.getErrorCode()).isEqualTo(code);
		assertThat(cause.getMessage()).contains(constraint);
	}

	private static byte[] uuidBytes(UUID id) {
		return ByteBuffer.allocate(16).putLong(id.getMostSignificantBits()).putLong(id.getLeastSignificantBits()).array();
	}
}
