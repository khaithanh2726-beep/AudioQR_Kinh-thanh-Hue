package vn.hueqr.backend.entity;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

// Phiên bản văn bản bất biến được tạo bằng thao tác lưu bản nháp rõ ràng
@Entity
@Table(name = "text_revision")
public class TextRevision {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id phiên bản văn bản đã lưu

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "slot_id", nullable = false, updatable = false)
	private ContentSlot slot; // Entity ngăn nội dung trong Java, database lưu khóa slot_id

	@Column(name = "body", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
	private String body; // Nguyên văn nội dung đã lưu, không tự dịch hoặc chuẩn hóa

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "content_hash", nullable = false, updatable = false, length = 32)
	private byte[] contentHash; // Mã Hash SHA-256 của văn bản UTF-8 đã lưu

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "saved_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant savedAt; // Thời điểm lưu phiên bản văn bản

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "active_tts_job_id", length = 16)
	private UUID activeTtsJobId; // Id job đang xử lý hoặc chưa rõ kết quả, database kiểm job thuộc văn bản này

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "latest_tts_job_key", length = 16)
	private UUID latestTtsJobKey; // Khóa logic của job gần nhất, không FK bắt giữ metadata job cũ

	protected TextRevision() {
	}

	public TextRevision(UUID id, ContentSlot slot, String body, byte[] contentHash, Instant savedAt) {
		this.id = id;
		this.slot = slot;
		this.body = body;
		this.contentHash = contentHash.clone();
		this.savedAt = savedAt;
	}

	public UUID getId() {
		return id;
	}

	public ContentSlot getSlot() {
		return slot;
	}

	public String getBody() {
		return body;
	}

	public byte[] getContentHash() {
		return contentHash.clone();
	}

	public Instant getSavedAt() {
		return savedAt;
	}

	public UUID getActiveTtsJobId() {
		return activeTtsJobId;
	}

	public UUID getLatestTtsJobKey() {
		return latestTtsJobKey;
	}

	public void trackTtsJob(UUID jobId) {
		this.activeTtsJobId = jobId;
		this.latestTtsJobKey = jobId;
	}

	public void clearActiveTtsJob(UUID expectedId) {
		if (expectedId.equals(activeTtsJobId))
			this.activeTtsJobId = null;
	}
}
