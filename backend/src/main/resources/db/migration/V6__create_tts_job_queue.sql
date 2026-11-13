CREATE TABLE tts_job (
    id BINARY(16) NOT NULL,
    target_text_revision_id BINARY(16) NULL,
    place_key_snapshot BINARY(16) NOT NULL,
    text_revision_key_snapshot BINARY(16) NOT NULL,
    language_snapshot VARCHAR(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    text_hash_snapshot BINARY(32) NOT NULL,
    model VARCHAR(128) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    voice VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    phase VARCHAR(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    target_valid BOOLEAN NOT NULL DEFAULT TRUE,
    worker_instance BINARY(16) NULL,
    worker_token BINARY(16) NULL,
    phase_version BIGINT NOT NULL DEFAULT 0,
    admin_account_key BINARY(16) NOT NULL,
    request_window_key BINARY(16) NOT NULL,
    request_operation_key BINARY(16) NOT NULL,
    accepted_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    dispatched_at DATETIME(6) NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_tts_job_id_target UNIQUE (id, target_text_revision_id),
    CONSTRAINT uk_tts_job_request UNIQUE (admin_account_key, request_window_key, request_operation_key),
    CONSTRAINT ck_tts_job_language CHECK (language_snapshot IN ('vi', 'en', 'fr', 'ru')),
    CONSTRAINT ck_tts_job_phase CHECK (phase IN ('QUEUED', 'DISPATCHING', 'UNCERTAIN')),
    CONSTRAINT ck_tts_job_target CHECK (target_valid IN (0, 1) AND (target_valid = FALSE OR target_text_revision_id IS NOT NULL)),
    CONSTRAINT ck_tts_job_dispatch CHECK (
        (phase = 'QUEUED' AND worker_instance IS NULL AND worker_token IS NULL AND dispatched_at IS NULL)
        OR (phase IN ('DISPATCHING', 'UNCERTAIN') AND worker_instance IS NOT NULL AND worker_token IS NOT NULL AND dispatched_at IS NOT NULL)
    ),
    CONSTRAINT fk_tts_job_text FOREIGN KEY (target_text_revision_id)
        REFERENCES text_revision (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    INDEX ix_tts_job_queue (phase, accepted_at, id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Khóa snapshot của cửa sổ/tài khoản không giữ window bằng FK khi dọn metadata sau này
ALTER TABLE text_revision
    ADD COLUMN active_tts_job_id BINARY(16) NULL,
    ADD COLUMN latest_tts_job_key BINARY(16) NULL,
    ADD CONSTRAINT fk_text_active_tts_job FOREIGN KEY (active_tts_job_id, id)
        REFERENCES tts_job (id, target_text_revision_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
