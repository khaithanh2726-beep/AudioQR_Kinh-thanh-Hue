package vn.hueqr.backend.config;

import java.time.Clock;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import vn.hueqr.backend.dto.StartTicketResponse;
import vn.hueqr.backend.service.GuestInputPolicy;
import vn.hueqr.backend.service.StartTicketCodec;
import vn.hueqr.backend.service.StartTicketService;
import vn.hueqr.backend.service.StartTicketTimePolicy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuestRuntimeConfigurationTests {
	private static final String EPOCH = "00000000-0000-0000-0000-000000000001";
	private final ApplicationContextRunner runner = new ApplicationContextRunner()
			.withUserConfiguration(GuestRuntimeConfiguration.class);

	@Test
	void validExternalPropertiesCreateTheRequiredBeans() {
		configured().run(context -> {
			assertThat(context).hasNotFailed().hasSingleBean(StartTicketCodec.class)
					.hasSingleBean(StartTicketTimePolicy.class).hasSingleBean(Clock.class);
			assertThat(context.getBean("guestServiceEpoch")).isEqualTo(UUID.fromString(EPOCH));
		});
	}

	@Test
	void missingKeyFailsWithoutGeneratingAFallback() {
		runner.withPropertyValues("hueqr.guest.service-epoch=" + EPOCH)
				.run(context -> assertThat(context).hasFailed());
	}

	@Test
	void missingEpochFailsWithoutGeneratingAFallback() {
		runner.withPropertyValues("hueqr.guest.signing-key-base64=" + key())
				.run(context -> assertThat(context).hasFailed());
	}

	@Test
	void shortMalformedAndNonCanonicalKeysFail() {
		for (String invalid : new String[]{"bad!", "AA==", key().replace("=", "")}) {
			runner.withPropertyValues("hueqr.guest.signing-key-base64=" + invalid,
					"hueqr.guest.service-epoch=" + EPOCH)
					.run(context -> assertThat(context).hasFailed());
		}
	}

	@Test
	void malformedEpochFails() {
		runner.withPropertyValues("hueqr.guest.signing-key-base64=" + key(),
				"hueqr.guest.service-epoch=not-a-uuid").run(context -> assertThat(context).hasFailed());
	}

	// Dựng lại ngữ cảnh Spring trong bộ nhớ để đối chiếu cấu hình khóa và epoch
	@Test
	void reconstructedContextUsesTheSameKeyAndEpochButChangedEpochRejectsOldTickets() {
		AtomicReference<StartTicketResponse> issued = new AtomicReference<>();
		configured().run(context -> issued.set(service(context.getBean(StartTicketCodec.class), EPOCH)
				.issue(credential())));

		configured().run(context -> {
			StartTicketService service = service(context.getBean(StartTicketCodec.class), EPOCH);
			var ticket = issued.get();
			assertThat(service.verify(ticket.startTicket(), ticket.requestId(),
					new GuestInputPolicy().credentialHash(credential())).serviceEpoch())
					.isEqualTo(UUID.fromString(EPOCH));

			StartTicketService restored = service(context.getBean(StartTicketCodec.class),
					"00000000-0000-0000-0000-000000000002");
			assertThatThrownBy(() -> restored.verify(ticket.startTicket(), ticket.requestId(),
					new GuestInputPolicy().credentialHash(credential())))
					.hasMessage("SERVICE_EPOCH_MISMATCH");
		});
	}

	private ApplicationContextRunner configured() {
		return runner.withPropertyValues("hueqr.guest.signing-key-base64=" + key(),
				"hueqr.guest.service-epoch=" + EPOCH);
	}

	private StartTicketService service(StartTicketCodec codec, String epoch) {
		Clock clock = Clock.systemUTC();
		return new StartTicketService(codec, new StartTicketTimePolicy(clock), new GuestInputPolicy(),
				UUID.fromString(epoch), clock);
	}

	private static String key() {
		byte[] bytes = new byte[32];
		Arrays.fill(bytes, (byte) 0x55);
		return Base64.getEncoder().encodeToString(bytes);
	}

	private static String credential() {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32]);
	}
}
