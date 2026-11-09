package vn.hueqr.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "hueqr.retention.enabled", havingValue = "true")
public class GuestRetentionRunner {
	private static final Logger LOG = LoggerFactory.getLogger(GuestRetentionRunner.class);
	private final GuestRetentionService retention;
	private final int batchSize;

	public GuestRetentionRunner(GuestRetentionService retention, @Value("${hueqr.retention.batch-size:100}") int batchSize) {
		if (batchSize < 1)
			throw new IllegalArgumentException("RETENTION_BATCH_MUST_BE_POSITIVE");
		this.retention = retention;
		this.batchSize = batchSize;
	}

	// Nhịp quét cấu hình kỹ thuật không thay mốc hết hạn quyền hoặc hạn lưu 30 ngày
	@Scheduled(fixedDelayString = "${hueqr.retention.poll-delay-ms:60000}", initialDelayString = "${hueqr.retention.poll-delay-ms:60000}")
	public void tick() {
		var result = retention.sweep(batchSize);
		if (result.examined() > 0)
			LOG.info("Guest retention: examined={}, contexts={}, sessions={}, views={}, blocked={}",
					result.examined(), result.clearedContexts(), result.purgedSessions(), result.deletedViews(), result.blockedReads());
	}
}
