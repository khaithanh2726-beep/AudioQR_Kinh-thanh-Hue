package vn.hueqr.backend.repository;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OperationsGuardRepository {

	private final JdbcTemplate sql;

	public OperationsGuardRepository(JdbcTemplate sql) {
		this.sql = sql;
	}

	public boolean runningWithAdmissionLock() {
		// Khóa chia sẻ giữ tới khi lease đã commit, tranh chấp với lệnh QUIESCING.
		return "RUNNING".equals(sql.queryForObject("SELECT mode FROM ops_guard WHERE id=1 FOR SHARE", String.class));
	}

	public void enter(UUID id, UUID instance, String kind) {
		sql.update("INSERT INTO ops_activity(id,instance_id,kind,started_at) VALUES(?,?,?,UTC_TIMESTAMP(6))",
				id.toString(), instance.toString(), kind);
	}

	public void leave(UUID id) {
		if (sql.update("DELETE FROM ops_activity WHERE id=?", id.toString()) != 1)
			throw new IllegalStateException("OPERATIONS_LEASE_MISSING");
	}
}
