-- Thêm sau import vào container restore riêng, không chạy trên nguồn backup
SET time_zone = '+00:00';
SET @case_now = UTC_TIMESTAMP(6);
CREATE TEMPORARY TABLE restore_cases (
    label VARCHAR(40), started DATETIME(6), expiry DATETIME(6), ended DATETIME(6));
INSERT INTO restore_cases VALUES
('restore-test-expired', @case_now - INTERVAL 32 DAY, @case_now - INTERVAL 32 DAY + INTERVAL 8 HOUR, NULL),
('restore-test-boundary', @case_now - INTERVAL 30 DAY - INTERVAL 8 HOUR, @case_now - INTERVAL 30 DAY, NULL),
('restore-test-invalid-end', @case_now - INTERVAL 32 DAY, @case_now - INTERVAL 32 DAY + INTERVAL 8 HOUR, @case_now + INTERVAL 1 DAY),
('restore-test-retained', @case_now - INTERVAL 29 DAY, @case_now - INTERVAL 29 DAY + INTERVAL 8 HOUR, @case_now - INTERVAL 29 DAY + INTERVAL 1 HOUR);

INSERT INTO guest_session (id,start_request_key,credential_hash,service_epoch,start_fingerprint,
    start_ticket_issued_at,start_ticket_expires_at,start_ticket_digest,display_name,initial_language,
    started_at,expires_at,ended_at)
SELECT UUID_TO_BIN(UUID()),UUID_TO_BIN(UUID()),RANDOM_BYTES(32),'00000000-0000-4000-8000-000000000001',
    RANDOM_BYTES(32),started,started + INTERVAL 24 HOUR,RANDOM_BYTES(32),label,'vi',started,expiry,ended
FROM restore_cases;
INSERT INTO place_view (id,session_id,opening_key,live_place_id,place_key_snapshot,place_name_snapshot,language,viewed_at)
SELECT UUID_TO_BIN(UUID()),id,UUID_TO_BIN(UUID()),NULL,UUID_TO_BIN(UUID()),'Restore fixture','vi',started_at
FROM guest_session WHERE display_name LIKE 'restore-test-%';
DROP TEMPORARY TABLE restore_cases;
