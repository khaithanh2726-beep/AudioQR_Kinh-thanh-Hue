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

// Một bản audio bất biến gắn với văn bản, job và hai file WAV/MP3, chưa tự được duyệt
@Entity
@Table(name = "audio_revision")
public class AudioRevision {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(nullable = false, updatable = false, length = 16)
	private UUID id; // Id phiên bản audio

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "text_revision_id", nullable = false, updatable = false)
	private TextRevision textRevision; // Entity văn bản trong Java, database lưu khóa text_revision_id

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "job_id", nullable = false, updatable = false, length = 16)
	private UUID jobId; // Id job tạo audio, database kiểm đúng văn bản và WAV nguồn

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "wav_file_id", nullable = false, updatable = false, length = 16)
	private UUID wavFileId; // Id file nguồn có định dạng WAV

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "mp3_file_id", nullable = false, updatable = false, length = 16)
	private UUID mp3FileId; // Id file MP3 dùng để nghe thử và phục vụ khi đủ quyền

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant createdAt; // Thời điểm đăng ký phiên bản audio hoàn chỉnh

	protected AudioRevision() {
	}

	public AudioRevision(UUID id, TextRevision textRevision, UUID jobId, UUID wavFileId, UUID mp3FileId, Instant createdAt) {
		this.id = id;
		this.textRevision = textRevision;
		this.jobId = jobId;
		this.wavFileId = wavFileId;
		this.mp3FileId = mp3FileId;
		this.createdAt = createdAt;
	}

	public UUID getId() {
		return id;
	}

	public TextRevision getTextRevision() {
		return textRevision;
	}

	public UUID getJobId() {
		return jobId;
	}

	public UUID getWavFileId() {
		return wavFileId;
	}

	public UUID getMp3FileId() {
		return mp3FileId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
