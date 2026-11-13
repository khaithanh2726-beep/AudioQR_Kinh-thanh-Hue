-- Chỉ chạy trên database đích mới, ứng dụng chưa khởi động và gate FROZEN.
-- Giữ anchor gốc, không gán ended_at hoặc thay tuổi dữ liệu bằng mốc restore.
SET time_zone = '+00:00';
START TRANSACTION;
SET @restore_now = UTC_TIMESTAMP(6);
CREATE TEMPORARY TABLE expired_restore_session (id BINARY(16) PRIMARY KEY);
INSERT INTO expired_restore_session
SELECT id FROM guest_session
WHERE DATE_ADD(
    CASE WHEN ended_at >= started_at AND ended_at <= expires_at AND ended_at <= @restore_now
         THEN ended_at ELSE expires_at END,
    INTERVAL 30 DAY) <= @restore_now;
UPDATE guest_session SET current_opening_id=NULL, current_language=NULL, document_nonce=NULL, selection_seq=0;
DELETE FROM guest_request_result;
DELETE FROM guest_content_grant;
DELETE FROM guest_open_intent;
DELETE v FROM place_view v JOIN expired_restore_session e ON e.id=v.session_id;
DELETE s FROM guest_session s JOIN expired_restore_session e ON e.id=s.id;
DROP TEMPORARY TABLE expired_restore_session;
COMMIT;
