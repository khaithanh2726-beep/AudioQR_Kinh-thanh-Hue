package vn.hueqr.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "hueqr.cleanup.enabled", havingValue = "true")
public class LifecycleCleanupRunner {
	private final MetadataCleanupService metadata;
	private final WorkspaceCleanupService workspace;
	private final FileCleanupService files;
	private final int batch;

	public LifecycleCleanupRunner(MetadataCleanupService metadata, WorkspaceCleanupService workspace, FileCleanupService files,
			@Value("${hueqr.cleanup.batch-size:100}") int batch) {
		if (batch < 1)
			throw new IllegalArgumentException("CLEANUP_BATCH_MUST_BE_POSITIVE");
		this.metadata = metadata;
		this.workspace = workspace;
		this.files = files;
		this.batch = batch;
	}

	// Nhịp chạy không thay các hạn nghiệp vụ 24 giờ, 7 ngày, 8 giờ và 1 giờ
	@Scheduled(fixedDelayString = "${hueqr.cleanup.poll-delay-ms:60000}", initialDelayString = "${hueqr.cleanup.poll-delay-ms:60000}")
	public void tick() {
		metadata.reconcile(batch);
		metadata.windows(batch);
		metadata.jobs(batch);
		workspace.sweep(batch);
		files.sweep(batch);
	}
}
