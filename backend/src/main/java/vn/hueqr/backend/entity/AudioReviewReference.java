package vn.hueqr.backend.entity;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;

// Tham chiếu hồ sơ bên ngoài gắn với đúng phiên bản audio, không phải kết luận phê duyệt
@Entity
@Table(name = "audio_review_ref")
public class AudioReviewReference {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "audio_revision_id", nullable = false, updatable = false, length = 16)
	private UUID audioRevisionId; // Id audio đã lưu, đồng thời là khóa ngoại trong database

	@Column(name = "locator", nullable = false, columnDefinition = "LONGTEXT")
	private String locator; // Vị trí và mục hồ sơ để Admin đối chiếu audio

	@Column(name = "limitations", columnDefinition = "LONGTEXT")
	private String limitations; // Giới hạn của việc đối chiếu, có thể chưa được ghi

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "recorded_by", nullable = false, length = 16)
	private UUID recordedBy; // Id tài khoản ghi tham chiếu, không phải người duyệt trong hồ sơ

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "recorded_at", nullable = false, columnDefinition = "DATETIME(6)")
	private Instant recordedAt; // Thời điểm ghi tham chiếu, không phải ngày duyệt bên ngoài

	@Version
	@Column(name = "version", nullable = false)
	private Long version; // Phiên bản riêng chống ghi đè tham chiếu đồng thời

	protected AudioReviewReference() {
	}

	public AudioReviewReference(UUID audioRevisionId, String locator, String limitations, UUID recordedBy, Instant recordedAt) {
		this.audioRevisionId = audioRevisionId;
		update(locator, limitations, recordedBy, recordedAt);
	}

	public UUID getAudioRevisionId() {
		return audioRevisionId;
	}

	public String getLocator() {
		return locator;
	}

	public String getLimitations() {
		return limitations;
	}

	public UUID getRecordedBy() {
		return recordedBy;
	}

	public Instant getRecordedAt() {
		return recordedAt;
	}

	public Long getVersion() {
		return version;
	}

	public void update(String locator, String limitations, UUID recordedBy, Instant recordedAt) {
		this.locator = locator;
		this.limitations = limitations;
		this.recordedBy = recordedBy;
		this.recordedAt = recordedAt;
	}
}
