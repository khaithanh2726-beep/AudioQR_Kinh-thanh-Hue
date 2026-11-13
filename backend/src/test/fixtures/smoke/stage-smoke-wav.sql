-- Chạy trong MySQL tạm với backend đã dừng, đặt năm biến theo hướng dẫn trước khi chạy
SET @target = NULL, @place = NULL, @text_hash = NULL, @admin = NULL;
SET @job = UUID_TO_BIN(UUID()), @wav = UUID_TO_BIN(@smoke_wav_id), @now = UTC_TIMESTAMP(6);
START TRANSACTION;

SELECT t.id, p.id, t.content_hash, a.id
INTO @target, @place, @text_hash, @admin
FROM text_revision t
JOIN content_slot s ON s.draft_text_id = t.id AND s.id = t.slot_id
JOIN place p ON p.id = s.place_id
JOIN admin_account a ON a.login_name = 'smoke-admin' AND a.enabled = TRUE
WHERE DATABASE() = 'hueqr_smoke'
  AND SUBSTRING_INDEX(CURRENT_USER(), '@', 1) = 'hueqr_smoke'
  AND t.id = UUID_TO_BIN(@smoke_text_id)
  AND s.language = 'vi' AND p.visible = FALSE
  AND t.active_tts_job_id IS NULL AND t.latest_tts_job_key IS NULL
  AND @wav IS NOT NULL AND @smoke_wav_hash REGEXP '^[0-9a-f]{64}$'
  AND @smoke_wav_size > 44 AND @smoke_wav_duration > 0
FOR UPDATE;

INSERT INTO file_asset (id, storage_key, format, checksum, byte_size, duration, state, ready_at)
SELECT @wav, CONCAT(BIN_TO_UUID(@wav), '.wav'), 'WAV', UNHEX(@smoke_wav_hash),
       @smoke_wav_size, @smoke_wav_duration, 'READY', @now
WHERE @target IS NOT NULL;

INSERT INTO tts_job (id, target_text_revision_id, place_key_snapshot, text_revision_key_snapshot,
    language_snapshot, text_hash_snapshot, model, voice, phase, worker_instance, worker_token,
    admin_account_key, request_window_key, request_operation_key, accepted_at, updated_at,
    dispatched_at, source_wav_file_id, result_staged_at)
SELECT @job, @target, @place, @target, 'vi', @text_hash, 'smoke-fixture', 'fixture',
       'RESULT_STAGED', UUID_TO_BIN(UUID()), UUID_TO_BIN(UUID()), @admin,
       UUID_TO_BIN(UUID()), UUID_TO_BIN(UUID()), @now, @now, @now, @wav, @now
WHERE @target IS NOT NULL;

UPDATE text_revision SET active_tts_job_id = @job, latest_tts_job_key = @job WHERE id = @target;
COMMIT;
SELECT IF(@target IS NULL, 'NO_FIXTURE', BIN_TO_UUID(@job)) AS smoke_job;
