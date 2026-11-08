package vn.hueqr.backend.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminOperationLookupResponse;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.entity.AdminCommandWindow;
import vn.hueqr.backend.entity.OperationReceipt;
import vn.hueqr.backend.repository.AdminCommandWindowRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;

@Service
public class AdminCommandService {
	public record Command(String method, String resource, String canonicalBody, String ifMatch,
			String operationType, int httpStatus, String resultCode) {
	}

	private record Key(UUID windowId, UUID operationKey) {
	}

	private record Fingerprint(String method, String resource, String canonicalBody, String ifMatch) {
	}

	private final AdminCommandWindowRepository windows;
	private final OperationReceiptRepository receipts;
	private final Clock clock;
	private final UUID serviceEpoch;
	private final ObjectMapper json;
	private final TransactionTemplate transaction;

	public AdminCommandService(AdminCommandWindowRepository windows, OperationReceiptRepository receipts,
			Clock clock, UUID serviceEpoch, ObjectMapper json, PlatformTransactionManager manager) {
		this.windows = windows;
		this.receipts = receipts;
		this.clock = clock;
		this.serviceEpoch = serviceEpoch;
		this.json = json;
		this.transaction = new TransactionTemplate(manager);
		this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public AdminCommandWindowResponse issue(UUID accountId) {
		return transaction.execute(status -> {
			// Cắt đến micro giây để mốc cấp và hạn cửa sổ khớp DATETIME(6) khi đọc lại
			var now = clock.instant().truncatedTo(ChronoUnit.MICROS);
			var window = windows.saveAndFlush(new AdminCommandWindow(UUID.randomUUID(), accountId,
					serviceEpoch.toString(), now, now.plus(Duration.ofHours(24))));
			return new AdminCommandWindowResponse(window.getId(), window.getServiceEpoch(), window.getIssuedAt(), window.getExpiresAt());
		});
	}

	public AdminOperationLookupResponse lookup(UUID accountId, String header) {
		Key key = key(header);
		return transaction.execute(status -> {
			checkWindow(windows.findById(key.windowId()).orElseThrow(() -> new AdminCommandException(403, "FORBIDDEN")), accountId);
			var result = receipts.findByAdminAccountIdAndWindowIdAndOperationKey(accountId, key.windowId(), key.operationKey());
			// Chưa quan sát thấy receipt vẫn có thể là lệnh chưa commit, không kết luận thất bại
			return new AdminOperationLookupResponse(result.isPresent() ? "COMMITTED" : "NOT_OBSERVED",
					result.map(receipt -> response(receipt, true)).orElse(null), clock.instant());
		});
	}

	public AdminOperationResponse execute(UUID accountId, String header, Command command, Supplier<Map<String, Object>> action) {
		Key key = key(header);
		byte[] fingerprint = fingerprint(command);
		return transaction.execute(status -> {
			// Giữ khóa cửa sổ đến commit để request cùng khóa không cùng thực hiện tác động
			AdminCommandWindow window = windows.findByIdForUpdate(key.windowId())
					.orElseThrow(() -> new AdminCommandException(403, "FORBIDDEN"));
			checkWindow(window, accountId);
			var existing = receipts.findByAdminAccountIdAndWindowIdAndOperationKey(accountId, key.windowId(), key.operationKey());
			if (existing.isPresent()) {
				if (!MessageDigest.isEqual(existing.get().getFingerprint(), fingerprint))
					throw new AdminCommandException(409, "KEY_REUSED");
				return response(existing.get(), true);
			}

			// action chỉ chứa thao tác DB, exception phải hoàn tác cả tác động lẫn receipt
			Map<String, Object> result = action.get();
			var receipt = receipts.saveAndFlush(new OperationReceipt(UUID.randomUUID(), key.windowId(), accountId,
					key.operationKey(), fingerprint, command.operationType(), (short) command.httpStatus(), command.resultCode(),
					json.writeValueAsString(result), clock.instant().truncatedTo(ChronoUnit.MICROS)));
			return response(receipt, false);
		});
	}

	private void checkWindow(AdminCommandWindow window, UUID accountId) {
		if (!window.getAdminAccountId().equals(accountId))
			throw new AdminCommandException(403, "FORBIDDEN");
		if (!window.getServiceEpoch().equals(serviceEpoch.toString()))
			throw new AdminCommandException(403, "SERVICE_EPOCH_MISMATCH");
		// Kiểm sau khi lấy khóa, request chờ khóa không được dùng mốc giờ trước lúc chờ
		if (!clock.instant().isBefore(window.getExpiresAt()))
			throw new AdminCommandException(409, "COMMAND_WINDOW_EXPIRED");
	}

	private Key key(String value) {
		try {
			if (value == null || value.length() != 73 || value.charAt(36) != ':')
				throw new IllegalArgumentException();
			UUID window = UUID.fromString(value.substring(0, 36));
			UUID operation = UUID.fromString(value.substring(37));
			if (!(window + ":" + operation).equals(value))
				throw new IllegalArgumentException();
			return new Key(window, operation);
		}
		catch (IllegalArgumentException error) {
			throw new AdminCommandException(422, "VALIDATION_FAILED",
					Map.of("Idempotency-Key", "Cần windowId:operationId gồm hai UUID chuẩn chữ thường."));
		}
	}

	private byte[] fingerprint(Command command) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(json.writeValueAsBytes(
					new Fingerprint(command.method(), command.resource(), command.canonicalBody(), command.ifMatch())));
		}
		catch (NoSuchAlgorithmException error) {
			throw new IllegalStateException("SHA-256 unavailable", error);
		}
	}

	private AdminOperationResponse response(OperationReceipt receipt, boolean replayed) {
		Map<String, Object> summary = json.readValue(receipt.getResultSummary(), new TypeReference<Map<String, Object>>() {});
		return new AdminOperationResponse(receipt.getId(), receipt.getOperationType(), receipt.getHttpStatus(),
				receipt.getResultCode(), summary, receipt.getCommittedAt(), replayed);
	}
}
