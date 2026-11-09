package vn.hueqr.backend.entity;

import java.sql.Types;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;

// Ngăn nội dung của một địa điểm và ngôn ngữ, tách con trỏ nháp với cặp đang áp dụng
@Entity
@Table(name = "content_slot")
public class ContentSlot {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id ngăn nội dung

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "place_id", nullable = false, updatable = false)
	private Place place; // Entity địa điểm trong Java, database lưu khóa place_id

	@Column(name = "language", nullable = false, updatable = false, length = 2)
	private String language; // Mã ngôn ngữ vi, en, fr hoặc ru

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "draft_text_id", length = 16)
	private UUID draftTextId; // Id bản nháp đã lưu, database kiểm văn bản thuộc ngăn này

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "active_pair_id", length = 16)
	private UUID activePairId; // Id cặp đang áp dụng, database kiểm cặp thuộc chính ngăn này

	@Version
	@Column(name = "version", nullable = false)
	private Long version; // Phiên bản chống ghi đè con trỏ nháp hoặc cặp khi cập nhật đồng thời

	protected ContentSlot() {
	}

	public ContentSlot(UUID id, Place place, String language) {
		this.id = id;
		this.place = place;
		this.language = language;
	}

	public UUID getId() {
		return id;
	}

	public Place getPlace() {
		return place;
	}

	public String getLanguage() {
		return language;
	}

	public UUID getDraftTextId() {
		return draftTextId;
	}

	public Long getVersion() {
		return version;
	}

	public UUID getActivePairId() {
		return activePairId;
	}

	public void setActivePairId(UUID activePairId) {
		this.activePairId = activePairId;
	}

	public void setDraftTextId(UUID draftTextId) {
		this.draftTextId = draftTextId;
	}
}
