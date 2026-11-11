package vn.hueqr.backend.service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MediaToolProcess {
	public void run(List<String> command, OutputStream output, Duration timeout) throws IOException {
		if (timeout.isNegative() || timeout.isZero())
			throw new IllegalArgumentException("MEDIA_TIMEOUT_MUST_BE_POSITIVE");
		Process process;
		try {
			process = new ProcessBuilder(command).redirectError(ProcessBuilder.Redirect.DISCARD).start();
		}
		catch (IOException error) {
			throw new IOException("LOCAL_TOOL_UNAVAILABLE");
		}
		try (var pumps = Executors.newVirtualThreadPerTaskExecutor()) {
			process.getOutputStream().close();
			var copied = pumps.submit(() -> {
				try (var bytes = process.getInputStream()) {
					bytes.transferTo(output);
				}
				return null;
			});
			try {
				if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS))
					throw new IOException("LOCAL_TOOL_TIMEOUT");
				copied.get();
				if (process.exitValue() != 0)
					throw new IOException("LOCAL_CONVERSION_FAILED");
			}
			catch (InterruptedException error) {
				Thread.currentThread().interrupt();
				throw new IOException("LOCAL_TOOL_INTERRUPTED");
			}
			catch (java.util.concurrent.ExecutionException error) {
				throw new IOException("LOCAL_OUTPUT_FAILED");
			}
			finally {
				// Không nhả scope hoặc file khi tiến trình chuyển đổi vẫn có thể ghi bytes
				process.descendants().forEach(ProcessHandle::destroyForcibly);
				if (process.isAlive())
					process.destroyForcibly();
				process.onExit().join();
			}
		}
		finally {
			if (process.isAlive()) {
				process.destroyForcibly();
				process.onExit().join();
			}
		}
	}
}
