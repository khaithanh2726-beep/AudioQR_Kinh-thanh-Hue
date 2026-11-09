package vn.hueqr.backend.entity;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// Kết quả tối thiểu được lưu cùng giao dịch của một lệnh quản trị
@Entity
@Table(name = "operation_receipt")
public class OperationReceipt {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id kết quả thao tác

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "window_id", nullable = false, updatable = false, length = 16)
	private UUID windowId; // Id cửa sổ chứa khóa lệnh

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "admin_account_id", nullable = false, updatable = false, length = 16)
	private UUID adminAccountId; // Id chủ lệnh, database kiểm cùng chủ với window_id bằng khóa ngoại tổ hợp

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "operation_key", nullable = false, updatable = false, length = 16)
	private UUID operationKey; // Khóa thao tác do client giữ khi kết quả chưa rõ

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "fingerprint", nullable = false, updatable = false, length = 32)
	private byte[] fingerprint; // Mã Hash của method, resource, body chuẩn và điều kiện phiên bản

	@Column(name = "operation_type", nullable = false, updatable = false, length = 64)
	private String operationType; // Loại nghiệp vụ đã thực hiện

	@Column(name = "http_status", nullable = false, updatable = false)
	private short httpStatus; // HTTP status của kết quả đã ghi

	@Column(name = "result_code", nullable = false, updatable = false, length = 64)
	private String resultCode; // Mã kết quả nghiệp vụ

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "result_summary", nullable = false, updatable = false, columnDefinition = "JSON")
	private String resultSummary; // JSON kết quả tối thiểu, không giữ toàn bộ payload lệnh

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "committed_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant committedAt; // Mốc server ghi kết quả trong giao dịch thành công

	protected OperationReceipt() {
	}

	public OperationReceipt(UUID id, UUID windowId, UUID adminAccountId, UUID operationKey, byte[] fingerprint,
			String operationType, short httpStatus, String resultCode, String resultSummary, Instant committedAt) {
		this.id = id;
		this.windowId = windowId;
		this.adminAccountId = adminAccountId;
		this.operationKey = operationKey;
		this.fingerprint = fingerprint.clone();
		this.operationType = operationType;
		this.httpStatus = httpStatus;
		this.resultCode = resultCode;
		this.resultSummary = resultSummary;
		this.committedAt = committedAt;
	}

	public UUID getId() {
		return id;
	}

	public byte[] getFingerprint() {
		return fingerprint.clone();
	}

	public String getOperationType() {
		return operationType;
	}

	public short getHttpStatus() {
		return httpStatus;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultSummary() {
		return resultSummary;
	}

	public Instant getCommittedAt() {
		return committedAt;
	}
}
