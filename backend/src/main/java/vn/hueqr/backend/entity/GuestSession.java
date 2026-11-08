package vn.hueqr.backend.entity;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

// Phiên tham quan và dữ liệu đối chiếu yêu cầu bắt đầu phiên
@Entity
@Table(name = "guest_session")
public class GuestSession {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id phiên tham quan

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "start_request_key", nullable = false, updatable = false, length = 16)
	private UUID startRequestKey; // Khóa chống tạo trùng phiên

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "credential_hash", nullable = false, updatable = false, length = 32)
	private byte[] credentialHash; // Mã Hash SHA-256 của thông tin xác thực tab

	@JdbcTypeCode(Types.CHAR)
	@Column(name = "service_epoch", nullable = false, updatable = false, length = 36)
	private String serviceEpoch; // Epoch dịch vụ lưu lúc tạo phiên để đối chiếu quyền

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "start_fingerprint", nullable = false, updatable = false, length = 32)
	private byte[] startFingerprint; // Mã Hash của tên chuẩn hóa, ngôn ngữ và mã Hash vé

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "start_ticket_issued_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant startTicketIssuedAt; // Thời điểm cấp vé bắt đầu phiên

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "start_ticket_expires_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant startTicketExpiresAt; // Thời điểm hết hạn vé bắt đầu phiên

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "start_ticket_digest", nullable = false, updatable = false, length = 32)
	private byte[] startTicketDigest; // Mã Hash SHA-256 của chuỗi vé bắt đầu phiên

	@Column(name = "display_name", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
	private String displayName; // Tên hiển thị của khách trong phiên

	@JdbcTypeCode(Types.CHAR)
	@Column(name = "initial_language", nullable = false, updatable = false, length = 2)
	private String initialLanguage; // Ngôn ngữ được chọn khi bắt đầu phiên

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "started_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant startedAt; // Thời điểm bắt đầu phiên tham quan

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "expires_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant expiresAt; // Thời điểm hết hạn cố định của phiên tham quan

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "ended_at", columnDefinition = "DATETIME(6)")
	private Instant endedAt; // Thời điểm kết thúc chủ động, null khi chưa kết thúc chủ động

	protected GuestSession() {
	}

	public GuestSession(UUID id, UUID startRequestKey, byte[] credentialHash, String serviceEpoch,
			byte[] startFingerprint, Instant startTicketIssuedAt, Instant startTicketExpiresAt,
			byte[] startTicketDigest, String displayName, String initialLanguage,
			Instant startedAt, Instant expiresAt) {
		this.id = id;
		this.startRequestKey = startRequestKey;
		this.credentialHash = credentialHash.clone();
		this.serviceEpoch = serviceEpoch;
		this.startFingerprint = startFingerprint.clone();
		this.startTicketIssuedAt = startTicketIssuedAt;
		this.startTicketExpiresAt = startTicketExpiresAt;
		this.startTicketDigest = startTicketDigest.clone();
		this.displayName = displayName;
		this.initialLanguage = initialLanguage;
		this.startedAt = startedAt;
		this.expiresAt = expiresAt;
	}

	public UUID getId() {
		return id;
	}

	public UUID getStartRequestKey() {
		return startRequestKey;
	}

	public byte[] getCredentialHash() {
		return credentialHash.clone();
	}

	public String getServiceEpoch() {
		return serviceEpoch;
	}

	public byte[] getStartFingerprint() {
		return startFingerprint.clone();
	}

	public Instant getStartTicketIssuedAt() {
		return startTicketIssuedAt;
	}

	public Instant getStartTicketExpiresAt() {
		return startTicketExpiresAt;
	}

	public byte[] getStartTicketDigest() {
		return startTicketDigest.clone();
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getInitialLanguage() {
		return initialLanguage;
	}

	public Instant getStartedAt() {
		return startedAt;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getEndedAt() {
		return endedAt;
	}

	public void endAt(Instant endedAt) {
		this.endedAt = endedAt;
	}
}
