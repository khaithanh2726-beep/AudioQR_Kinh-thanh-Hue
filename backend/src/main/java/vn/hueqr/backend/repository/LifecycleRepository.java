package vn.hueqr.backend.repository;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LifecycleRepository {
	public record Instance(UUID id, String node, String root, long pid, Instant processStartedAt, String state) {
	}

	public record Job(UUID id, UUID place, UUID text, UUID instance, String phase, boolean valid, Instant terminalAt, Instant localFinishedAt) {
	}

	public record Asset(UUID id, String key, String format, byte[] checksum, long size, String state,
			boolean applied, Instant unappliedAt, Instant orphanSince, UUID deleteToken, UUID deleteOwner, UUID deleteTask) {
	}

	public record Workspace(String key, UUID job, UUID token, UUID instance, UUID file, String kind, Instant eligibleSince) {
	}

	private final JdbcTemplate sql;

	public LifecycleRepository(JdbcTemplate sql) {
		this.sql = sql;
	}

	public void register(Instance owner, Instant now) {
		sql.update("INSERT INTO runtime_instance (id, node_key, root_key, process_id, process_started_at, registered_at, state) VALUES (?, ?, ?, ?, ?, ?, 'RUNNING')",
				bytes(owner.id()), owner.node(), owner.root(), owner.pid(), time(owner.processStartedAt()), time(now));
	}

	public Optional<Instance> instance(UUID id, boolean lock) {
		return sql.query("SELECT * FROM runtime_instance WHERE id = ?" + (lock ? " FOR UPDATE" : ""), (row, index) ->
				new Instance(uuid(row, "id"), row.getString("node_key"), row.getString("root_key"), row.getLong("process_id"),
						instant(row, "process_started_at"), row.getString("state")), bytes(id)).stream().findFirst();
	}

	public List<UUID> instances(int limit) {
		return ids("SELECT id FROM runtime_instance WHERE EXISTS (SELECT 1 FROM file_claim c WHERE c.owner_instance = runtime_instance.id) OR EXISTS (SELECT 1 FROM runtime_task t WHERE t.instance_id = runtime_instance.id) ORDER BY registered_at, id LIMIT ?", limit);
	}

	public void stop(UUID id, Instant now) {
		sql.update("UPDATE runtime_instance SET state = 'STOPPED', stopped_at = COALESCE(stopped_at, ?) WHERE id = ?", time(now), bytes(id));
	}

	public void beginTask(UUID id, UUID owner, String kind, UUID key, Instant now) {
		int added = sql.update("INSERT INTO runtime_task (id, instance_id, kind, owner_key, created_at) SELECT ?, id, ?, ?, ? FROM runtime_instance WHERE id = ? AND state = 'RUNNING'",
				bytes(id), kind, bytes(key), time(now), bytes(owner));
		if (added != 1)
			throw new IllegalStateException("RUNTIME_INSTANCE_STOPPED");
	}

	public void endTask(UUID task) {
		sql.update("DELETE FROM runtime_task WHERE id = ?", bytes(task));
	}

	public boolean jobInUse(UUID job) {
		return sql.queryForObject("SELECT COUNT(*) FROM runtime_task WHERE owner_key = ? AND kind IN ('TTS', 'RECOVER')", Long.class, bytes(job)) > 0;
	}

	public boolean ownsTask(UUID job, UUID instance) {
		return sql.queryForObject("SELECT COUNT(*) FROM runtime_task WHERE owner_key = ? AND instance_id = ? AND kind = 'TTS'", Long.class, bytes(job), bytes(instance)) > 0;
	}

	public boolean taskExists(UUID task) {
		return task != null && sql.queryForObject("SELECT COUNT(*) FROM runtime_task WHERE id = ?", Long.class, bytes(task)) > 0;
	}

	public boolean claimWorkspace(String key, UUID task) {
		return sql.update("UPDATE workspace_file w SET cleanup_task = ? WHERE storage_key = ? AND NOT EXISTS (SELECT 1 FROM runtime_task t WHERE t.id = w.cleanup_task)", bytes(task), key) == 1;
	}

	public int releaseClaims(UUID instance) {
		int count = sql.update("DELETE FROM file_claim WHERE owner_instance = ?", bytes(instance));
		sql.update("DELETE FROM runtime_task WHERE instance_id = ?", bytes(instance));
		return count;
	}

	public List<UUID> ownerJobs(UUID instance) {
		return ids("SELECT id FROM tts_job WHERE (worker_instance = ? OR conversion_instance = ?) AND phase IN ('DISPATCHING', 'UNCERTAIN', 'RESULT_STAGED', 'CONVERTING', 'SUCCEEDED', 'FAILED')", bytes(instance), bytes(instance));
	}

	public Optional<Job> job(UUID id, boolean lock) {
		return sql.query("SELECT * FROM tts_job WHERE id = ?" + (lock ? " FOR UPDATE" : ""), (r, n) -> new Job(
				uuid(r, "id"), uuid(r, "place_key_snapshot"), uuid(r, "target_text_revision_id"), uuid(r, "conversion_instance") == null ? uuid(r, "worker_instance") : uuid(r, "conversion_instance"),
				r.getString("phase"), r.getBoolean("target_valid"), instant(r, "terminal_at"), instant(r, "local_finished_at")), bytes(id)).stream().findFirst();
	}

	public void finishedLocally(UUID job, UUID instance, Instant now) {
		sql.update("UPDATE tts_job SET local_finished_at = ? WHERE id = ? AND (worker_instance = ? OR conversion_instance = ?)", time(now), bytes(job), bytes(instance), bytes(instance));
	}

	public List<UUID> detachedJobs(int limit) {
		return ids("SELECT id FROM tts_job j WHERE target_valid = FALSE AND phase IN ('DISPATCHING', 'UNCERTAIN', 'RESULT_STAGED', 'CONVERTING', 'SUCCEEDED', 'FAILED') AND local_finished_at IS NOT NULL AND NOT EXISTS (SELECT 1 FROM runtime_task t WHERE t.owner_key = j.id) ORDER BY local_finished_at, id LIMIT ?", limit);
	}

	public void lockTarget(Job job) {
		sql.query("SELECT id FROM place WHERE id = ? FOR UPDATE", (r, n) -> r.getBytes(1), bytes(job.place()));
		if (job.text() != null)
			sql.query("SELECT id FROM text_revision WHERE id = ? FOR UPDATE", (r, n) -> r.getBytes(1), bytes(job.text()));
	}

	public void reconcileJob(Job job, Instant now) {
		if (!job.valid())
			sql.update("UPDATE tts_job SET phase = 'DISCARDED', terminal_at = ?, source_wav_file_id = NULL, result_staged_at = NULL, phase_version = phase_version + 1, updated_at = ? WHERE id = ? AND phase IN ('DISPATCHING', 'UNCERTAIN', 'RESULT_STAGED', 'CONVERTING', 'SUCCEEDED', 'FAILED') AND NOT EXISTS (SELECT 1 FROM audio_revision a WHERE a.job_id = tts_job.id)", time(now), time(now), bytes(job.id()));
		else if ("DISPATCHING".equals(job.phase()))
			sql.update("UPDATE tts_job SET phase = 'UNCERTAIN', phase_version = phase_version + 1, updated_at = ? WHERE id = ?", time(now), bytes(job.id()));
	}

	public List<UUID> windows(Instant now, int limit) {
		return ids("SELECT id FROM admin_command_window WHERE expires_at <= ? ORDER BY expires_at, id LIMIT ?", time(now), limit);
	}

	public boolean ownsConversion(UUID job, UUID token, UUID instance) {
		return sql.queryForObject("SELECT EXISTS(SELECT 1 FROM tts_job j JOIN runtime_task t ON t.id = j.conversion_task WHERE j.id = ? AND j.phase = 'CONVERTING' AND j.conversion_token = ? AND j.conversion_instance = ? AND t.instance_id = ? AND t.kind = 'TTS' AND t.owner_key = j.id)", Boolean.class, bytes(job), bytes(token), bytes(instance), bytes(instance));
	}

	public boolean purgeWindow(UUID id, Instant now) {
		var rows = sql.query("SELECT expires_at FROM admin_command_window WHERE id = ? FOR UPDATE", (r, n) -> instant(r, "expires_at"), bytes(id));
		if (rows.isEmpty() || now.isBefore(rows.getFirst()))
			return false;
		sql.update("DELETE FROM operation_receipt WHERE window_id = ?", bytes(id));
		sql.update("DELETE FROM admin_command_window WHERE id = ?", bytes(id));
		return true;
	}

	public List<UUID> oldJobs(Instant cutoff, int limit) {
		return ids("SELECT id FROM tts_job j WHERE j.phase IN ('FAILED', 'DISCARDED') AND j.terminal_at <= ? AND NOT EXISTS (SELECT 1 FROM text_revision t WHERE t.active_tts_job_id = j.id OR t.latest_tts_job_key = j.id) AND NOT EXISTS (SELECT 1 FROM audio_revision a WHERE a.job_id = j.id) AND NOT EXISTS (SELECT 1 FROM tts_job next_job WHERE next_job.retry_of_job_id = j.id) AND NOT EXISTS (SELECT 1 FROM runtime_task t WHERE t.owner_key = j.id) AND NOT EXISTS (SELECT 1 FROM workspace_file w WHERE w.job_key = j.id) ORDER BY j.terminal_at, j.id LIMIT ?", time(cutoff), limit);
	}

	public boolean purgeJob(Job job, Instant cutoff) {
		return sql.update("DELETE j FROM tts_job j LEFT JOIN tts_job next_job ON next_job.retry_of_job_id = j.id WHERE j.id = ? AND j.terminal_at <= ? AND j.phase IN ('FAILED', 'DISCARDED') AND j.source_wav_file_id IS NULL AND next_job.id IS NULL AND NOT EXISTS (SELECT 1 FROM text_revision t WHERE t.active_tts_job_id = j.id OR t.latest_tts_job_key = j.id) AND NOT EXISTS (SELECT 1 FROM audio_revision a WHERE a.job_id = j.id) AND NOT EXISTS (SELECT 1 FROM runtime_task t WHERE t.owner_key = j.id) AND NOT EXISTS (SELECT 1 FROM workspace_file w WHERE w.job_key = j.id)", bytes(job.id()), time(cutoff)) == 1;
	}

	public List<UUID> assets(int limit) {
		return ids("""
				SELECT f.id FROM file_asset f WHERE state <> 'DELETED'
				AND (state = 'DELETING' OR ever_applied = FALSE OR last_unapplied_at IS NOT NULL)
				AND NOT EXISTS (SELECT 1 FROM audio_revision WHERE wav_file_id = f.id OR mp3_file_id = f.id)
				AND NOT EXISTS (SELECT 1 FROM tts_job WHERE source_wav_file_id = f.id)
				AND NOT EXISTS (SELECT 1 FROM guest_content_grant WHERE mp3_file_id = f.id)
				AND NOT EXISTS (SELECT 1 FROM file_claim WHERE file_id = f.id)
				AND NOT EXISTS (SELECT 1 FROM workspace_file w LEFT JOIN tts_job j ON j.id = w.job_key WHERE w.file_key = f.id
				    AND (j.id IS NULL OR j.phase NOT IN ('FAILED', 'DISCARDED') OR EXISTS(SELECT 1 FROM runtime_task t WHERE t.owner_key = w.job_key)))
				ORDER BY CASE WHEN state = 'DELETING' THEN deletion_started_at
				    WHEN ever_applied = TRUE THEN DATE_ADD(last_unapplied_at, INTERVAL 8 HOUR)
				    ELSE DATE_ADD(COALESCE(orphan_eligible_since, ready_at), INTERVAL 24 HOUR) END, id LIMIT ?
				""", limit);
	}

	public Optional<Asset> asset(UUID id, boolean lock) {
		return sql.query("SELECT * FROM file_asset WHERE id = ?" + (lock ? " FOR UPDATE" : ""), (r, n) -> new Asset(uuid(r, "id"),
				r.getString("storage_key"), r.getString("format"), r.getBytes("checksum"), r.getLong("byte_size"), r.getString("state"),
				r.getBoolean("ever_applied"), instant(r, "last_unapplied_at"), instant(r, "orphan_eligible_since"),
				uuid(r, "deletion_token"), uuid(r, "deletion_owner"), uuid(r, "deletion_task")), bytes(id)).stream().findFirst();
	}

	public boolean referenced(UUID file) {
		return sql.queryForObject("""
				SELECT EXISTS(SELECT 1 FROM audio_revision WHERE wav_file_id = ? OR mp3_file_id = ?)
				OR EXISTS(SELECT 1 FROM tts_job WHERE source_wav_file_id = ?)
				OR EXISTS(SELECT 1 FROM guest_content_grant WHERE mp3_file_id = ?)
				OR EXISTS(SELECT 1 FROM file_claim WHERE file_id = ?)
				OR EXISTS(SELECT 1 FROM workspace_file w LEFT JOIN tts_job j ON j.id = w.job_key
				    WHERE w.file_key = ? AND (j.id IS NULL OR j.phase NOT IN ('FAILED', 'DISCARDED')
				    OR EXISTS(SELECT 1 FROM runtime_task t WHERE t.owner_key = w.job_key)))
				""", Boolean.class, bytes(file), bytes(file), bytes(file), bytes(file), bytes(file), bytes(file));
	}

	public void orphan(UUID file, Instant now) {
		sql.update("UPDATE file_asset SET orphan_eligible_since = ? WHERE id = ?", now == null ? null : time(now), bytes(file));
	}

	public void deleting(UUID id, UUID token, UUID owner, UUID task, Instant now) {
		sql.update("UPDATE file_asset SET state = 'DELETING', deletion_token = ?, deletion_owner = ?, deletion_task = ?, deletion_started_at = ?, cleanup_error = NULL WHERE id = ?", bytes(token), bytes(owner), bytes(task), time(now), bytes(id));
	}

	public void deletionResult(UUID id, UUID token, Instant deletedAt, String error) {
		if (deletedAt == null)
			sql.update("UPDATE file_asset SET cleanup_error = ? WHERE id = ? AND state = 'DELETING' AND deletion_token = ?", error, bytes(id), bytes(token));
		else
			sql.update("UPDATE file_asset SET state = 'DELETED', deleted_at = ?, cleanup_error = NULL WHERE id = ? AND state = 'DELETING' AND deletion_token = ?", time(deletedAt), bytes(id), bytes(token));
	}

	public void trackWorkspace(Workspace w, Instant now) {
		sql.update("INSERT INTO workspace_file (storage_key, job_key, worker_token, owner_instance, file_key, kind, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)", w.key(), bytes(w.job()), bytes(w.token()), bytes(w.instance()), bytes(w.file()), w.kind(), time(now));
	}

	public List<Workspace> workspaces(int limit) {
		return sql.query("SELECT w.* FROM workspace_file w JOIN tts_job j ON j.id = w.job_key WHERE (j.phase IN ('FAILED', 'DISCARDED', 'SUCCEEDED') OR (j.phase = 'RESULT_STAGED' AND j.local_finished_at IS NOT NULL)) AND NOT EXISTS (SELECT 1 FROM runtime_task t WHERE t.owner_key = j.id AND t.kind IN ('TTS', 'RECOVER', 'TEMP')) ORDER BY COALESCE(w.eligible_since, w.created_at), w.storage_key LIMIT ?", (r, n) -> workspace(r), limit);
	}

	public Optional<Workspace> workspace(String key, boolean lock) {
		return sql.query("SELECT * FROM workspace_file WHERE storage_key = ?" + (lock ? " FOR UPDATE" : ""), (r, n) -> workspace(r), key).stream().findFirst();
	}

	public void workspaceEligible(String key, Instant now) {
		sql.update("UPDATE workspace_file SET eligible_since = ? WHERE storage_key = ?", now == null ? null : time(now), key);
	}

	public void forgetWorkspace(String key) {
		sql.update("DELETE FROM workspace_file WHERE storage_key = ?", key);
	}

	private static Workspace workspace(ResultSet r) throws SQLException {
		return new Workspace(r.getString("storage_key"), uuid(r, "job_key"), uuid(r, "worker_token"), uuid(r, "owner_instance"), uuid(r, "file_key"), r.getString("kind"), instant(r, "eligible_since"));
	}

	private List<UUID> ids(String query, Object... arguments) {
		return sql.query(query, (r, n) -> uuid(r, "id"), arguments);
	}

	public static LocalDateTime time(Instant value) {
		// Cắt trước khi bind để JDBC không làm tròn cutoff lên micro giây kế tiếp của DATETIME(6)
		return LocalDateTime.ofInstant(value.truncatedTo(java.time.temporal.ChronoUnit.MICROS), ZoneOffset.UTC);
	}

	public static Instant instant(ResultSet row, String name) throws SQLException {
		var value = row.getObject(name, LocalDateTime.class);
		return value == null ? null : value.toInstant(ZoneOffset.UTC);
	}

	public static UUID uuid(ResultSet row, String name) throws SQLException {
		byte[] value = row.getBytes(name);
		if (value == null)
			return null;
		var buffer = ByteBuffer.wrap(value);
		return new UUID(buffer.getLong(), buffer.getLong());
	}

	public static byte[] bytes(UUID value) {
		return value == null ? null : ByteBuffer.allocate(16).putLong(value.getMostSignificantBits()).putLong(value.getLeastSignificantBits()).array();
	}
}
