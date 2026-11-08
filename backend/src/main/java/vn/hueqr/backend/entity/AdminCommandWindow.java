package vn.hueqr.backend.entity;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

// Cửa sổ 24 giờ để tài khoản quản trị dùng lại khóa lệnh và tra kết quả
@Entity
@Table(name = "admin_command_window")
public class AdminCommandWindow {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id cửa sổ lệnh

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "admin_account_id", nullable = false, updatable = false, length = 16)
	private UUID adminAccountId; // Id tài khoản sở hữu, database kiểm khóa ngoại tới admin_account

	@JdbcTypeCode(Types.CHAR)
	@Column(name = "service_epoch", nullable = false, updatable = false, length = 36)
	private String serviceEpoch; // Epoch dịch vụ khi cấp cửa sổ

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "issued_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant issuedAt; // Thời điểm server cấp cửa sổ

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "expires_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant expiresAt; // Hạn cố định 24 giờ từ lúc cấp

	protected AdminCommandWindow() {
	}

	public AdminCommandWindow(UUID id, UUID adminAccountId, String serviceEpoch, Instant issuedAt, Instant expiresAt) {
		this.id = id;
		this.adminAccountId = adminAccountId;
		this.serviceEpoch = serviceEpoch;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
	}

	public UUID getId() {
		return id;
	}

	public UUID getAdminAccountId() {
		return adminAccountId;
	}

	public String getServiceEpoch() {
		return serviceEpoch;
	}

	public Instant getIssuedAt() {
		return issuedAt;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}
}
