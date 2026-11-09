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

// Cặp văn bản và audio khớp cùng ngăn nội dung, tách khỏi con trỏ nháp và áp dụng
@Entity
@Table(name = "content_pair")
public class ContentPair {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(nullable = false, updatable = false, length = 16)
	private UUID id; // Id cặp nội dung bất biến

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "slot_id", nullable = false, updatable = false)
	private ContentSlot slot; // Entity ngăn trong Java, database lưu khóa slot_id

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "text_revision_id", nullable = false, updatable = false, length = 16)
	private UUID textRevisionId; // Id văn bản thuộc chính ngăn của cặp

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "audio_revision_id", nullable = false, updatable = false, length = 16)
	private UUID audioRevisionId; // Id audio được database kiểm khớp văn bản của cặp

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant createdAt; // Thời điểm tạo cặp, không phải thời điểm duyệt hoặc áp dụng

	protected ContentPair() {
	}

	public ContentPair(UUID id, ContentSlot slot, UUID textRevisionId, UUID audioRevisionId, Instant createdAt) {
		this.id = id;
		this.slot = slot;
		this.textRevisionId = textRevisionId;
		this.audioRevisionId = audioRevisionId;
		this.createdAt = createdAt;
	}

	public UUID getId() {
		return id;
	}

	public ContentSlot getSlot() {
		return slot;
	}

	public UUID getTextRevisionId() {
		return textRevisionId;
	}

	public UUID getAudioRevisionId() {
		return audioRevisionId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
