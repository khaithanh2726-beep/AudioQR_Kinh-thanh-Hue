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

// Bản hình vùng đã lưu, giữ nguyên dữ liệu khi địa điểm chọn bản vùng khác
@Entity
@Table(name = "region_revision")
public class RegionRevision {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id của bản vùng đã lưu

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "place_id", nullable = false, updatable = false)
	private Place place; // Entity địa điểm trong Java, database lưu khóa place_id

	@Column(name = "polygon", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
	private String polygon; // JSON chuẩn giữ nguyên số thập phân để đọc lại không đổi mã Hash

	@Column(name = "map_space_key", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
	private String mapSpaceKey; // Mã không gian nền dùng để diễn giải tọa độ vùng

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "geometry_hash", nullable = false, updatable = false, length = 32)
	private byte[] geometryHash; // Mã Hash SHA-256 của hình vùng do server xác định

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "saved_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant savedAt; // Thời điểm lưu bản vùng

	protected RegionRevision() {
	}

	public RegionRevision(UUID id, Place place, String polygon, String mapSpaceKey,
			byte[] geometryHash, Instant savedAt) {
		this.id = id;
		this.place = place;
		this.polygon = polygon;
		this.mapSpaceKey = mapSpaceKey;
		this.geometryHash = geometryHash.clone();
		this.savedAt = savedAt;
	}

	public UUID getId() {
		return id;
	}

	public Place getPlace() {
		return place;
	}

	public String getPolygon() {
		return polygon;
	}

	public String getMapSpaceKey() {
		return mapSpaceKey;
	}

	public byte[] getGeometryHash() {
		return geometryHash.clone();
	}

	public Instant getSavedAt() {
		return savedAt;
	}
}
