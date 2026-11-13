package vn.hueqr.backend.service;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import vn.hueqr.backend.repository.OperationsGuardRepository;
import vn.hueqr.backend.support.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OperationsGateTests {
	@Autowired OperationsGateService gate;
	@Autowired OperationsGuardRepository repository;
	@Autowired PlatformTransactionManager manager;
	@Autowired JdbcTemplate sql;
	@Autowired MockMvc mvc;

	@AfterEach
	void resetOnlyFixture() {
		sql.update("DELETE FROM ops_activity");
		sql.update("UPDATE ops_guard SET mode='RUNNING',owner_token=NULL WHERE id=1");
	}

	private void quiesce() {
		sql.update("UPDATE ops_guard SET mode='QUIESCING',owner_token=?,changed_at=UTC_TIMESTAMP(6) WHERE id=1", UUID.randomUUID().toString());
	}

	private long activities() {
		return sql.queryForObject("SELECT COUNT(*) FROM ops_activity", Long.class);
	}

	@Test
	void admittedWorkRemainsVisibleUntilItsActualCompletion() {
		var lease = gate.enter("HTTP");
		assertThat(lease).isNotNull();
		assertThat(activities()).isEqualTo(1);
		quiesce();
		assertThat(gate.enter("HTTP")).isNull();
		assertThat(activities()).isEqualTo(1);
		lease.close();
		lease.close();
		assertThat(activities()).isZero();
	}

	@Test
	void restartDoesNotOpenFrozenGuardOrDiscardUnfinishedActivity() {
		gate.enter("LOCAL_AUDIO");
		quiesce();
		sql.update("UPDATE ops_guard SET mode='FROZEN' WHERE id=1");
		var restarted = new OperationsGateService(repository, manager);
		assertThat(restarted.enter("HTTP")).isNull();
		assertThat(activities()).isEqualTo(1);
	}

	@Test
	void oldActivityIsNeverDiscardedBasedOnElapsedTime() {
		gate.enter("TTS_QUEUE");
		sql.update("UPDATE ops_activity SET started_at='2000-01-01'");
		quiesce();
		assertThat(gate.enter("TTS_QUEUE")).isNull();
		assertThat(activities()).isEqualTo(1);
	}

	@Test
	void allScheduledKindsStopAcceptingWorkAndThrownWorkReleasesItsLease() {
		assertThatThrownBy(() -> gate.runIfOpen("CLEANUP", () -> { throw new IllegalStateException("fixture"); }))
				.isInstanceOf(IllegalStateException.class);
		assertThat(activities()).isZero();
		quiesce();
		for (String kind : new String[]{"TTS_QUEUE", "LOCAL_AUDIO", "CLEANUP", "RETENTION"}) {
			var ran = new AtomicBoolean();
			gate.runIfOpen(kind, () -> ran.set(true));
			assertThat(ran).isFalse();
		}
		assertThat(activities()).isZero();
	}

	@Test
	void apiIsBlockedBeforeAuthenticationOrSessionCreationWithoutPretendingExpiry() throws Exception {
		quiesce();
		mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isServiceUnavailable())
				.andExpect(header().string("Cache-Control", "no-store"))
				.andExpect(header().doesNotExist("Set-Cookie"))
				.andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"));
		assertThat(activities()).isZero();
	}

	@Test
	void encodedApiPathIsStillBlockedUsingTheContainerDecodedServletPath() throws Exception {
		quiesce();
		mvc.perform(get(java.net.URI.create("/%61pi/v1/auth/csrf")).with(request -> {
			request.setServletPath("/api/v1/auth/csrf");
			return request;
		}))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"));
		assertThat(activities()).isZero();
	}

	@Test
	void successfulApiReleasesItsActivity() throws Exception {
		mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk());
		assertThat(activities()).isZero();
	}

	@Test
	void concurrentAdmissionWaitsForQuiescingCommitAndCannotSlipThrough() throws Exception {
		var locked = new CountDownLatch(1);
		var commit = new CountDownLatch(1);
		try (var threads = Executors.newVirtualThreadPerTaskExecutor()) {
			var transition = threads.submit(() -> new TransactionTemplate(manager).executeWithoutResult(tx -> {
				sql.queryForObject("SELECT mode FROM ops_guard WHERE id=1 FOR UPDATE", String.class);
				quiesce();
				locked.countDown();
				try { if (!commit.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("fixture timeout"); }
				catch (InterruptedException error) { Thread.currentThread().interrupt(); throw new IllegalStateException(error); }
			}));
			try {
				assertThat(locked.await(10, TimeUnit.SECONDS)).isTrue();
				var admission = threads.submit(() -> gate.enter("HTTP"));
				commit.countDown();
				transition.get(10, TimeUnit.SECONDS);
				assertThat(admission.get(10, TimeUnit.SECONDS)).isNull();
				assertThat(activities()).isZero();
			}
			finally { commit.countDown(); }
		}
	}
}
