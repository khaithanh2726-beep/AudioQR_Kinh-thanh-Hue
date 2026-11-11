package vn.hueqr.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

class MediaToolProcessTests {
	@TempDir
	Path directory;

	@Test
	void keepsArgumentsLiteralAndTransfersBinaryOutputWithoutShellExpansion() throws Exception {
		var bytes = new ByteArrayOutputStream();
		new MediaToolProcess().run(command("echo", "literal path & $() ; \""), bytes, Duration.ofSeconds(10));
		assertThat(bytes.toString(java.nio.charset.StandardCharsets.UTF_8)).isEqualTo("literal path & $() ; \"");
	}

	@Test
	void binaryPipePreservesUtf8BytesIndependentlyOfTheWindowsArgumentCodePage() throws Exception {
		var bytes = new ByteArrayOutputStream();
		new MediaToolProcess().run(command("unicode"), bytes, Duration.ofSeconds(10));
		assertThat(bytes.toString(java.nio.charset.StandardCharsets.UTF_8)).isEqualTo("Tên có dấu");
	}

	@Test
	void largeStandardOutputIsDrainedWhileTheChildIsRunning() throws Exception {
		var bytes = new ByteArrayOutputStream();
		new MediaToolProcess().run(command("binary"), bytes, Duration.ofSeconds(10));
		assertThat(bytes.size()).isEqualTo(200000);
		assertThat(bytes.toByteArray()[199999]).isEqualTo((byte) 63);
	}

	@Test
	void nonzeroExitDoesNotExposePrivateDiagnosticOutput() {
		assertThatIOException().isThrownBy(() -> new MediaToolProcess().run(command("error"), new ByteArrayOutputStream(), Duration.ofSeconds(10)))
				.withMessage("LOCAL_CONVERSION_FAILED");
	}

	@Test
	void unavailableExecutableHasASanitizedError() {
		assertThatIOException().isThrownBy(() -> new MediaToolProcess().run(List.of(directory.resolve("missing-tool.exe").toString()),
				new ByteArrayOutputStream(), Duration.ofSeconds(2))).withMessage("LOCAL_TOOL_UNAVAILABLE");
	}

	@Test
	void timeoutReturnsOnlyAfterTheChildCanNoLongerWrite() throws Exception {
		Path pid = directory.resolve("pid.txt");
		assertThatIOException().isThrownBy(() -> new MediaToolProcess().run(command("sleep", pid.toString()),
				new ByteArrayOutputStream(), Duration.ofSeconds(2))).withMessage("LOCAL_TOOL_TIMEOUT");
		assertThat(Files.exists(pid)).isTrue();
		assertThat(ProcessHandle.of(Long.parseLong(Files.readString(pid))).map(ProcessHandle::isAlive).orElse(false)).isFalse();
	}

	@Test
	void refusesAnInvalidTimeoutBeforeLaunchingAnything() {
		assertThatIllegalArgumentException().isThrownBy(() -> new MediaToolProcess().run(command("binary"), new ByteArrayOutputStream(), Duration.ZERO));
	}

	private List<String> command(String... arguments) {
		var args = new ArrayList<String>();
		args.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
		args.addAll(List.of("-Dfile.encoding=UTF-8", "-cp", System.getProperty("java.class.path"), Fixture.class.getName()));
		args.addAll(List.of(arguments));
		return args;
	}

	public static class Fixture {
		public static void main(String[] args) throws Exception {
			switch (args[0]) {
				case "echo" -> System.out.write(args[1].getBytes(java.nio.charset.StandardCharsets.UTF_8));
				case "unicode" -> System.out.write("Tên có dấu".getBytes(java.nio.charset.StandardCharsets.UTF_8));
				case "binary" -> {
					for (int i=0; i<200000; i++)
						System.out.write(i & 255);
				}
				case "error" -> {
					System.err.print("private-provider-diagnostic");
					System.exit(7);
				}
				case "sleep" -> {
					Files.writeString(Path.of(args[1]), Long.toString(ProcessHandle.current().pid()));
					Thread.sleep(30000);
				}
				default -> throw new IllegalArgumentException();
			}
			System.out.flush();
		}
	}
}
