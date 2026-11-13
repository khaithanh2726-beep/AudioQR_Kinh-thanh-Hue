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

// Địa điểm có định danh độc lập tên và con trỏ đến bản vùng đang áp dụng
@Entity
@Table(name = "place")
public class Place {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id ổn định của địa điểm

	@Column(name = "display_name", nullable = false, columnDefinition = "LONGTEXT")
	private String displayName; // Tên hiển thị của địa điểm, có thể trùng địa điểm khác

	@Column(name = "visible", nullable = false)
	private boolean visible; // Trạng thái cho phép địa điểm xuất hiện với khách

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "active_region_revision_id", length = 16)
	private UUID activeRegionRevisionId; // Id bản vùng áp dụng, database kiểm bản vùng thuộc địa điểm này

	@Version
	@Column(name = "version", nullable = false)
	private Long version; // Phiên bản chống ghi đè thay đổi đồng thời

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant createdAt; // Thời điểm tạo địa điểm

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
	private Instant updatedAt; // Thời điểm cập nhật địa điểm gần nhất

	protected Place() {
	}

	public Place(UUID id, String displayName, Instant createdAt) {
		this.id = id;
		this.displayName = displayName;
		this.createdAt = createdAt;
		this.updatedAt = createdAt;
	}

	public UUID getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isVisible() {
		return visible;
	}

	public UUID getActiveRegionRevisionId() {
		return activeRegionRevisionId;
	}

	public Long getVersion() {
		return version;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void rename(String displayName, Instant updatedAt) {
		this.displayName = displayName;
		this.updatedAt = updatedAt;
	}

	public void setActiveRegionRevisionId(UUID activeRegionRevisionId, Instant updatedAt) {
		this.activeRegionRevisionId = activeRegionRevisionId;
		this.updatedAt = updatedAt;
	}

	public void setVisible(boolean visible, Instant updatedAt) {
		this.visible = visible;
		this.updatedAt = updatedAt;
	}
}
