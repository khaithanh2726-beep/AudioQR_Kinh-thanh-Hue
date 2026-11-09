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

// Lượt xem đã tính theo một lần mở địa điểm, giữ snapshot nhận biết khi địa điểm bị xóa
@Entity
@Table(name = "place_view")
public class PlaceView {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(nullable = false, updatable = false, length = 16)
	private UUID id; // Id lượt xem đã ghi

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "session_id", nullable = false, updatable = false)
	private GuestSession session; // Entity phiên trong Java, database lưu khóa session_id

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "opening_key", nullable = false, updatable = false, length = 16)
	private UUID openingKey; // Khóa logic lần mở, không FK giữ dữ liệu opening tạm

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "live_place_id", length = 16)
	private UUID livePlaceId; // Id địa điểm còn sống, database đặt null khi địa điểm bị xóa

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "place_key_snapshot", nullable = false, updatable = false, length = 16)
	private UUID placeKeySnapshot; // Id địa điểm tại lúc tính lượt, không gộp điểm mới trùng tên

	@Column(name = "place_name_snapshot", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
	private String placeNameSnapshot; // Tên địa điểm tại lúc tính lượt

	@Column(nullable = false, updatable = false, length = 2)
	private String language; // Ngôn ngữ nội dung tại lúc tính lượt

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "viewed_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant viewedAt; // Thời điểm server ghi lượt xem

	protected PlaceView() {
	}

	public PlaceView(UUID id, GuestSession session, UUID openingKey, UUID livePlaceId, UUID snapshotId,
			String snapshotName, String language, Instant viewedAt) {
		this.id = id;
		this.session = session;
		this.openingKey = openingKey;
		this.livePlaceId = livePlaceId;
		this.placeKeySnapshot = snapshotId;
		this.placeNameSnapshot = snapshotName;
		this.language = language;
		this.viewedAt = viewedAt;
	}

	public UUID getId() {
		return id;
	}

	public GuestSession getSession() {
		return session;
	}

	public UUID getLivePlaceId() {
		return livePlaceId;
	}

	public UUID getPlaceKeySnapshot() {
		return placeKeySnapshot;
	}

	public String getPlaceNameSnapshot() {
		return placeNameSnapshot;
	}

	public String getLanguage() {
		return language;
	}

	public Instant getViewedAt() {
		return viewedAt;
	}
}
