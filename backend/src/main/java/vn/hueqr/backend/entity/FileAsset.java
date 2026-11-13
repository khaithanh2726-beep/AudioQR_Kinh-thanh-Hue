package vn.hueqr.backend.entity;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

// Metadata file WAV hoặc MP3 hoàn chỉnh, độc lập với duyệt và áp dụng nội dung
@Entity
@Table(name = "file_asset")
public class FileAsset {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(nullable = false, updatable = false, length = 16)
	private UUID id; // Id file bất biến

	@Column(name = "storage_key", nullable = false, updatable = false, length = 80)
	private String storageKey; // Tên file do server cấp, không lấy path từ client

	@Column(nullable = false, updatable = false, length = 8)
	private String format; // Định dạng WAV hoặc MP3, không thay đổi sau khi đăng ký

	@JdbcTypeCode(Types.BINARY)
	@Column(nullable = false, updatable = false, length = 32)
	private byte[] checksum; // Mã Hash SHA-256 của toàn bộ bytes file

	@Column(name = "byte_size", nullable = false, updatable = false)
	private long byteSize; // Số byte file hoàn chỉnh

	@Column(nullable = false, updatable = false, precision = 20, scale = 9)
	private BigDecimal duration; // Thời lượng đã kiểm theo định dạng file, đơn vị giây

	@Column(nullable = false, length = 16)
	private String state; // READY khi dùng được, DELETING khi chờ unlink, DELETED khi đã xác nhận vắng file

	@Column(name = "ever_applied", nullable = false)
	private boolean everApplied; // File đã từng thuộc cặp áp dụng hay chưa

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "last_unapplied_at", columnDefinition = "DATETIME(6)")
	private Instant lastUnappliedAt; // Mốc bỏ tham chiếu áp dụng cuối cùng, chưa đặt khi chỉ tạo file

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "orphan_eligible_since", columnDefinition = "DATETIME(6)")
	private Instant orphanEligibleSince; // Mốc bắt đầu đủ điều kiện tính hạn file không còn tham chiếu

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "ready_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant readyAt; // Thời điểm đăng ký file hoàn chỉnh

	protected FileAsset() {
	}

	public FileAsset(UUID id, String storageKey, byte[] checksum, long byteSize, BigDecimal duration, Instant readyAt) {
		this(id, storageKey, "WAV", checksum, byteSize, duration, readyAt);
	}

	private FileAsset(UUID id, String storageKey, String format, byte[] checksum, long byteSize, BigDecimal duration, Instant readyAt) {
		this.id = id;
		this.storageKey = storageKey;
		this.checksum = checksum.clone();
		this.byteSize = byteSize;
		this.duration = duration;
		this.readyAt = readyAt;
		this.format = format;
		this.state = "READY";
	}

	public static FileAsset mp3(UUID id, String storageKey, byte[] checksum, long byteSize, BigDecimal duration, Instant readyAt) {
		return new FileAsset(id, storageKey, "MP3", checksum, byteSize, duration, readyAt);
	}

	public String getFormat() {
		return format;
	}

	public UUID getId() {
		return id;
	}

	public String getStorageKey() {
		return storageKey;
	}

	public byte[] getChecksum() {
		return checksum.clone();
	}

	public long getByteSize() {
		return byteSize;
	}

	public BigDecimal getDuration() {
		return duration;
	}

	public String getState() {
		return state;
	}

	public void retainReference() {
		orphanEligibleSince = null;
	}

	public void markApplied() {
		everApplied = true;
		orphanEligibleSince = null;
	}

	// Chỉ gọi sau khi đã bỏ tham chiếu áp dụng cuối cùng dưới khóa FILE
	public void markUnapplied(Instant now) {
		lastUnappliedAt = now;
	}
}
