package vn.hueqr.backend.config;

import java.time.Clock;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.hueqr.backend.service.StartTicketCodec;
import vn.hueqr.backend.service.StartTicketSigner;
import vn.hueqr.backend.service.StartTicketTimePolicy;

@Configuration(proxyBeanMethods = false)
public class GuestRuntimeConfiguration {
	@Bean
	Clock guestClock() {
		return Clock.systemUTC();
	}

	@Bean
	StartTicketSigner startTicketSigner(@Value("${hueqr.guest.signing-key-base64}") String encoded) {
		try {
			byte[] key = Base64.getDecoder().decode(encoded);
			if (!Base64.getEncoder().encodeToString(key).equals(encoded))
				throw new IllegalArgumentException();
			return new StartTicketSigner(key);
		}
		catch (IllegalArgumentException error) {
			throw new IllegalStateException("Cấu hình khóa ký khách không hợp lệ.");
		}
	}

	@Bean
	UUID guestServiceEpoch(@Value("${hueqr.guest.service-epoch}") String value) {
		try {
			UUID epoch = UUID.fromString(value);
			if (!epoch.toString().equals(value))
				throw new IllegalArgumentException();
			return epoch;
		}
		catch (IllegalArgumentException error) {
			throw new IllegalStateException("Cấu hình epoch khách không hợp lệ.");
		}
	}

	@Bean
	StartTicketCodec startTicketCodec(StartTicketSigner signer) {
		return new StartTicketCodec(signer);
	}

	@Bean
	StartTicketTimePolicy startTicketTimePolicy(Clock clock) {
		return new StartTicketTimePolicy(clock);
	}
}

