package vn.hueqr.backend.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TtsWorker {
	private final UUID instanceId;
	private final TtsDispatchService dispatch;
	private final TtsProvider provider;
	private final WavResultStore store;
	private final TtsResultService results;

	public TtsWorker(UUID instanceId, TtsDispatchService dispatch, TtsProvider provider, WavResultStore store, TtsResultService results) {
		this.instanceId = instanceId;
		this.dispatch = dispatch;
		this.provider = provider;
		this.store = store;
		this.results = results;
	}

	public boolean run(UUID jobId) {
		var claimed = dispatch.claim(jobId, instanceId);
		if (claimed.isEmpty())
			return false;
		var lease = claimed.get();
		byte[] wav;
		try {
			wav = provider.generate(lease);
		}
		catch (TtsProvider.Failure error) {
			if (error.definite())
				results.fail(jobId, lease.workerToken(), lease.phaseVersion(), error.code());
			else
				dispatch.markUncertain(jobId, lease.workerToken(), lease.phaseVersion());
			return true;
		}
		catch (RuntimeException error) {
			dispatch.markUncertain(jobId, lease.workerToken(), lease.phaseVersion());
			return true;
		}

		try {
			WavPolicy.inspect(wav);
		}
		catch (IllegalArgumentException error) {
			results.fail(jobId, lease.workerToken(), lease.phaseVersion(), "PROVIDER_OUTPUT_INVALID");
			return true;
		}
		try {
			String hash = WavPolicy.hash(lease.body().getBytes(StandardCharsets.UTF_8));
			var staged = store.save(jobId, lease.workerToken(), hash, wav);
			results.stage(staged);
		}
		catch (IOException | RuntimeException error) {
			// Đã có WAV nhưng chưa xác nhận đăng ký DB, giữ manifest để Check và không gọi provider lần nữa
			dispatch.markUncertain(jobId, lease.workerToken(), lease.phaseVersion());
		}
		return true;
	}
}
