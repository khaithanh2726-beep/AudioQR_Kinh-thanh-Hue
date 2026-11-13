package vn.hueqr.backend.service;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import vn.hueqr.backend.dto.AdminCommandWindowResponse;
import vn.hueqr.backend.dto.AdminOperationResponse;
import vn.hueqr.backend.entity.AdminAccount;
import vn.hueqr.backend.entity.Place;
import vn.hueqr.backend.repository.AdminAccountRepository;
import vn.hueqr.backend.repository.AdminCommandWindowRepository;
import vn.hueqr.backend.repository.OperationReceiptRepository;
import vn.hueqr.backend.repository.PlaceRepository;
import vn.hueqr.backend.service.AdminCommandService.Command;
import vn.hueqr.backend.support.GuestSessionTestClock;
import vn.hueqr.backend.support.GuestSessionTestClock.MutableClock;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, GuestSessionTestClock.class})
class AdminCommandTransactionTests {
	@Autowired
	private AdminCommandService commands;

	@Autowired
	private AdminCommandWindowRepository windows;

	@Autowired
	private OperationReceiptRepository receipts;

	@Autowired
	private AdminAccountRepository accounts;

	@Autowired
	private PlaceRepository places;

	@Autowired
	private MutableClock clock;

	private UUID accountId;
	private AdminCommandWindowResponse window;
	private String key;
	private Command command;

	@BeforeEach
	void reset() {
		clearData();
		clock.set(GuestSessionTestClock.BASE);
		accountId = UUID.randomUUID();
		accounts.saveAndFlush(new AdminAccount(accountId, "transaction-fixture", "{noop}test-only", true));
		window = commands.issue(accountId);
		key = window.windowId() + ":" + UUID.randomUUID();
		command = new Command("POST", "/api/v1/admin/places", "{\"displayName\":\"Ngọ Môn\"}", null,
				"CREATE_PLACE", 201, "PLACE_CREATED");
	}

	@AfterEach
	void clearData() {
		receipts.deleteAll();
		windows.deleteAll();
		places.deleteAll();
		accounts.deleteAll();
	}

	@Test
	void anExceptionAfterTheMutationRollsBackBothMutationAndReceiptAndAllowsAnExactRetry() {
		assertThrows(IllegalStateException.class, () -> commands.execute(accountId, key, command, () -> {
			savePlace();
			throw new IllegalStateException("fixture rollback");
		}));
		assertThat(places.count()).isZero();
		assertThat(receipts.count()).isZero();

		AdminOperationResponse result = commands.execute(accountId, key, command, this::savePlace);

		assertThat(result.replayed()).isFalse();
		assertThat(places.count()).isEqualTo(1);
		assertThat(receipts.count()).isEqualTo(1);
	}

	@Test
	void aReceiptInsertFailureAlsoRollsBackAnAlreadyFlushedPlace() {
		Command invalid = new Command(command.method(), command.resource(), command.canonicalBody(), command.ifMatch(),
				"x".repeat(65), command.httpStatus(), command.resultCode());

		assertThrows(DataIntegrityViolationException.class,
				() -> commands.execute(accountId, key, invalid, this::savePlace));
		assertThat(places.count()).isZero();
		assertThat(receipts.count()).isZero();
	}

	@Test
	void replayUsesTheStoredResultWithoutRunningTheActionOrItsCurrentPreconditions() {
		AdminOperationResponse first = commands.execute(accountId, key, command, this::savePlace);
		// Metadata kết quả của code mới không được làm đổi fingerprint của yêu cầu cũ
		Command newMetadata = new Command(command.method(), command.resource(), command.canonicalBody(), command.ifMatch(),
				"CREATE_PLACE_NEW_METADATA", 200, "NEW_CODE");
		AdminOperationResponse replay = commands.execute(accountId, key, newMetadata, () -> {
			throw new AssertionError("Replay không được chạy lại tác động hoặc kiểm phiên bản hiện tại");
		});

		assertThat(replay.operationId()).isEqualTo(first.operationId());
		assertThat(replay.httpStatus()).isEqualTo(201);
		assertThat(replay.resultCode()).isEqualTo("PLACE_CREATED");
		assertThat(replay.result()).isEqualTo(first.result());
		assertThat(replay.replayed()).isTrue();
	}

	@Test
	void receiptFailureRollsBackAFlushedRenameAndItsVersion() {
		Place original = places.saveAndFlush(new Place(UUID.randomUUID(), "Tên cũ", clock.instant()));
		Command invalid = new Command("PATCH", "/api/v1/admin/places/" + original.getId(), "{}", "\"0\"",
				"x".repeat(65), 200, "PLACE_UPDATED");

		assertThrows(DataIntegrityViolationException.class, () -> commands.execute(accountId, key, invalid, () -> {
			Place current = places.findByIdForUpdate(original.getId()).orElseThrow();
			current.rename("Tên mới", clock.instant().plusSeconds(1));
			places.flush();
			assertThat(current.getVersion()).isEqualTo(1L);
			return Map.of("placeId", current.getId().toString(), "version", current.getVersion());
		}));

		Place stored = places.findById(original.getId()).orElseThrow();
		assertThat(stored.getDisplayName()).isEqualTo("Tên cũ");
		assertThat(stored.getVersion()).isZero();
		assertThat(stored.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
		assertThat(receipts.count()).isZero();
	}

	@Test
	void methodResourceBodyAndIfMatchAllParticipateInTheFingerprint() {
		commands.execute(accountId, key, command, this::savePlace);
		for (Command changed : new Command[]{
				new Command("PATCH", command.resource(), command.canonicalBody(), null, "CREATE_PLACE", 201, "PLACE_CREATED"),
				new Command("POST", "/another-resource", command.canonicalBody(), null, "CREATE_PLACE", 201, "PLACE_CREATED"),
				new Command("POST", command.resource(), "{}", null, "CREATE_PLACE", 201, "PLACE_CREATED"),
				new Command("POST", command.resource(), command.canonicalBody(), "\"1\"", "CREATE_PLACE", 201, "PLACE_CREATED")}) {
			AdminCommandException error = assertThrows(AdminCommandException.class,
					() -> commands.execute(accountId, key, changed, this::savePlace));
			assertThat(error.status()).isEqualTo(409);
			assertThat(error.code()).isEqualTo("KEY_REUSED");
		}
		assertThat(places.count()).isEqualTo(1);
	}

	@Test
	void theSameOperationUuidInDifferentWindowsIsIndependent() {
		UUID operationKey = UUID.randomUUID();
		AdminCommandWindowResponse another = commands.issue(accountId);
		commands.execute(accountId, window.windowId() + ":" + operationKey, command, this::savePlace);
		commands.execute(accountId, another.windowId() + ":" + operationKey, command, this::savePlace);

		assertThat(places.count()).isEqualTo(2);
		assertThat(receipts.count()).isEqualTo(2);
	}

	private Map<String, Object> savePlace() {
		Place place = places.saveAndFlush(new Place(UUID.randomUUID(), "Ngọ Môn", clock.instant()));
		return Map.of("placeId", place.getId().toString(), "version", place.getVersion(), "visible", place.isVisible());
	}
}
