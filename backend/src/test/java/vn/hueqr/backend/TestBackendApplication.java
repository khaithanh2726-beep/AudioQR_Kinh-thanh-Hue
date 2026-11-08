package vn.hueqr.backend;

import org.springframework.boot.SpringApplication;
import vn.hueqr.backend.support.TestcontainersConfiguration;

public class TestBackendApplication {
	public static void main(String[] args) {
		SpringApplication.from(BackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}
}