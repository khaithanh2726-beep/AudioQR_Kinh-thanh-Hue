package vn.hueqr.backend.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FileReadClaimRepository {
	private final JdbcTemplate sql;

	public FileReadClaimRepository(JdbcTemplate sql) {
		this.sql = sql;
	}

	public void claim(UUID id, UUID file, UUID ownerKey, UUID instance, Instant now) {
		sql.update("INSERT INTO file_claim (id, file_id, owner_key, kind, owner_instance, created_at) VALUES (?, ?, ?, 'READ', ?, ?)",
				LifecycleRepository.bytes(id), LifecycleRepository.bytes(file), LifecycleRepository.bytes(ownerKey),
				LifecycleRepository.bytes(instance), LifecycleRepository.time(now));
	}

	public void release(UUID id) {
		sql.update("DELETE FROM file_claim WHERE id = ?", LifecycleRepository.bytes(id));
	}
}
