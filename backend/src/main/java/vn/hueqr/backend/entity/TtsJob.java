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

// Tác vụ TTS đã tiếp nhận, giữ snapshot văn bản và marker trước khi gửi đến nhà cung cấp
@Entity
@Table(name = "tts_job")
public class TtsJob {
	@Id
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "id", nullable = false, updatable = false, length = 16)
	private UUID id; // Id tác vụ TTS

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "target_text_revision_id", length = 16)
	private UUID targetTextRevisionId; // Id văn bản còn sống, database kiểm khóa ngoại đến text_revision

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "place_key_snapshot", nullable = false, updatable = false, length = 16)
	private UUID placeKeySnapshot; // Id địa điểm tại lúc tiếp nhận, giữ để đối chiếu khi đích mất

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "text_revision_key_snapshot", nullable = false, updatable = false, length = 16)
	private UUID textRevisionKeySnapshot; // Id văn bản gốc của tác vụ, không tráo sang nháp mới

	@Column(name = "language_snapshot", nullable = false, updatable = false, length = 2)
	private String languageSnapshot; // Ngôn ngữ của ngăn chứa văn bản đã lưu

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "text_hash_snapshot", nullable = false, updatable = false, length = 32)
	private byte[] textHashSnapshot; // Mã Hash SHA-256 của đúng văn bản được yêu cầu tạo audio

	@Column(name = "model", nullable = false, updatable = false, length = 128)
	private String model; // Model TTS theo lựa chọn của dự án tại lúc tiếp nhận

	@Column(name = "voice", nullable = false, updatable = false, length = 64)
	private String voice; // Giọng đọc theo lựa chọn của dự án tại lúc tiếp nhận

	@Column(name = "phase", nullable = false, length = 32)
	private String phase; // Chặng tiếp nhận, provider, chuyển đổi local hoặc kết quả cuối của tác vụ

	@Column(name = "target_valid", nullable = false)
	private boolean targetValid; // Cờ cho phép xử lý đích, không dùng để kết luận kết quả provider

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "worker_instance", length = 16)
	private UUID workerInstance; // Định danh tiến trình giành quyền xử lý tác vụ

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "worker_token", length = 16)
	private UUID workerToken; // Token ràng buộc lần giành quyền, không phải khóa API provider

	@Version
	@Column(name = "phase_version", nullable = false)
	private Long phaseVersion; // Phiên bản chống ghi trạng thái từ callback cũ

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "admin_account_key", nullable = false, updatable = false, length = 16)
	private UUID adminAccountKey; // Snapshot tài khoản tiếp nhận lệnh, không FK giữ tài khoản

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "request_window_key", nullable = false, updatable = false, length = 16)
	private UUID requestWindowKey; // Snapshot cửa sổ lệnh, không FK giữ cửa sổ

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "request_operation_key", nullable = false, updatable = false, length = 16)
	private UUID requestOperationKey; // Snapshot khóa thao tác tạo job đầu tiên

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "accepted_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
	private Instant acceptedAt; // Thời điểm tiếp nhận tác vụ đã commit

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
	private Instant updatedAt; // Thời điểm đổi trạng thái tác vụ gần nhất

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "dispatched_at", columnDefinition = "DATETIME(6)")
	private Instant dispatchedAt; // Thời điểm ghi marker trước lời gọi provider, không chứng minh đã gửi

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "source_wav_file_id", length = 16)
	private UUID sourceWavFileId; // Id file WAV còn được job giữ để xử lý local, database kiểm khóa ngoại

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "result_staged_at", columnDefinition = "DATETIME(6)")
	private Instant resultStagedAt; // Thời điểm đăng ký kết quả WAV hoàn chỉnh

	@JdbcTypeCode(Types.TIMESTAMP)
	@Column(name = "terminal_at", columnDefinition = "DATETIME(6)")
	private Instant terminalAt; // Thời điểm kết thúc xác định, không tự đặt cho UNKNOWN

	@Column(name = "failure_code", length = 64)
	private String failureCode; // Mã nguyên nhân đã phân loại, không lưu body lỗi provider

	@JdbcTypeCode(Types.BINARY)
	@Column(name = "retry_of_job_id", length = 16)
	private UUID retryOfJobId; // Khóa logic job trước khi Admin chủ động thử lại

	protected TtsJob() {
	}

	public TtsJob(UUID id, UUID textId, UUID placeId, String language, byte[] textHash, String model, String voice,
			UUID accountId, UUID windowId, UUID operationId, Instant acceptedAt) {
		this.id = id;
		this.targetTextRevisionId = textId;
		this.textRevisionKeySnapshot = textId;
		this.placeKeySnapshot = placeId;
		this.languageSnapshot = language;
		this.textHashSnapshot = textHash.clone();
		this.model = model;
		this.voice = voice;
		this.adminAccountKey = accountId;
		this.requestWindowKey = windowId;
		this.requestOperationKey = operationId;
		this.acceptedAt = acceptedAt;
		this.updatedAt = acceptedAt;
		this.phase = "QUEUED";
		this.targetValid = true;
	}

	public UUID getId() {
		return id;
	}

	public UUID getTargetTextRevisionId() {
		return targetTextRevisionId;
	}

	public UUID getTextRevisionKeySnapshot() {
		return textRevisionKeySnapshot;
	}

	public UUID getPlaceKeySnapshot() {
		return placeKeySnapshot;
	}

	public String getLanguageSnapshot() {
		return languageSnapshot;
	}

	public byte[] getTextHashSnapshot() {
		return textHashSnapshot.clone();
	}

	public String getModel() {
		return model;
	}

	public String getVoice() {
		return voice;
	}

	public String getPhase() {
		return phase;
	}

	public boolean isTargetValid() {
		return targetValid;
	}

	public UUID getWorkerInstance() {
		return workerInstance;
	}

	public UUID getWorkerToken() {
		return workerToken;
	}

	public Long getPhaseVersion() {
		return phaseVersion;
	}

	public Instant getAcceptedAt() {
		return acceptedAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public Instant getDispatchedAt() {
		return dispatchedAt;
	}

	public void dispatch(UUID instance, UUID token, Instant now) {
		this.workerInstance = instance;
		this.workerToken = token;
		this.dispatchedAt = now;
		this.updatedAt = now;
		this.phase = "DISPATCHING";
	}

	public void markUncertain(Instant now) {
		this.phase = "UNCERTAIN";
		this.updatedAt = now;
	}

	public UUID getSourceWavFileId() {
		return sourceWavFileId;
	}

	public Instant getTerminalAt() {
		return terminalAt;
	}

	public String getFailureCode() {
		return failureCode;
	}

	public UUID getRetryOfJobId() {
		return retryOfJobId;
	}

	public void setRetryOfJobId(UUID id) {
		this.retryOfJobId = id;
	}

	public void stage(UUID fileId, Instant now) {
		this.sourceWavFileId = fileId;
		this.resultStagedAt = now;
		this.updatedAt = now;
		this.phase = "RESULT_STAGED";
		this.failureCode = null;
	}

	public void fail(String code, Instant now) {
		this.phase = "FAILED";
		this.failureCode = code;
		this.updatedAt = now;
		this.terminalAt = now;
	}

	// Thử lại local giữ đúng WAV và job cũ, không tạo marker gọi provider mới
	public void reprocessLocal(Instant now) {
		phase = "RESULT_STAGED";
		failureCode = null;
		terminalAt = null;
		updatedAt = now;
	}

	public void discard(Instant now) {
		this.phase = "DISCARDED";
		this.updatedAt = now;
		this.terminalAt = now;
	}
}
