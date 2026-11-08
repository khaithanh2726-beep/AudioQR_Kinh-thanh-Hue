package vn.hueqr.backend.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import java.util.Arrays;
import java.util.Base64;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
	@Bean
	DynamicPropertyRegistrar guestTestProperties() {
		return registry -> {
			byte[] fixture = new byte[32];
			Arrays.fill(fixture, (byte) 0x44);

			registry.add("hueqr.guest.signing-key-base64", () -> Base64.getEncoder().encodeToString(fixture));
			registry.add("hueqr.guest.service-epoch", () -> "00000000-0000-0000-0000-000000000001");
		};
	}

	@Bean
	@ServiceConnection
	MySQLContainer mysqlContainer() {
		return new MySQLContainer(DockerImageName.parse("mysql:8.4.13"));
	}
}
