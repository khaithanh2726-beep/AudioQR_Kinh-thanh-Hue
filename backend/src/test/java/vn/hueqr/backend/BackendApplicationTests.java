package vn.hueqr.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import vn.hueqr.backend.support.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BackendApplicationTests {
	@Test
	void contextLoads() {
	}
}
