CREATE TABLE file_asset (
    id BINARY(16) NOT NULL,
    storage_key VARCHAR(80) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    format VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    checksum BINARY(32) NOT NULL,
    byte_size BIGINT NOT NULL,
    duration DECIMAL(20,9) NOT NULL,
    state VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    ever_applied BOOLEAN NOT NULL DEFAULT FALSE,
    last_unapplied_at DATETIME(6) NULL,
    orphan_eligible_since DATETIME(6) NULL,
    ready_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_file_asset_storage UNIQUE (storage_key),
    CONSTRAINT ck_file_asset_format CHECK (format = 'WAV'),
    CONSTRAINT ck_file_asset_ready CHECK (state = 'READY' AND byte_size > 0 AND duration > 0)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE tts_job
    DROP CHECK ck_tts_job_phase,
    DROP CHECK ck_tts_job_dispatch,
    ADD COLUMN source_wav_file_id BINARY(16) NULL,
    ADD COLUMN result_staged_at DATETIME(6) NULL,
    ADD COLUMN terminal_at DATETIME(6) NULL,
    ADD COLUMN failure_code VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NULL,
    ADD COLUMN retry_of_job_id BINARY(16) NULL,
    ADD CONSTRAINT fk_tts_job_source_wav FOREIGN KEY (source_wav_file_id)
        REFERENCES file_asset (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    ADD CONSTRAINT ck_tts_job_phase CHECK (phase IN ('QUEUED', 'DISPATCHING', 'UNCERTAIN', 'RESULT_STAGED', 'FAILED', 'DISCARDED')),
    ADD CONSTRAINT ck_tts_job_dispatch CHECK (
        (phase = 'QUEUED' AND worker_instance IS NULL AND worker_token IS NULL AND dispatched_at IS NULL)
        OR (phase <> 'QUEUED' AND worker_instance IS NOT NULL AND worker_token IS NOT NULL AND dispatched_at IS NOT NULL)
    ),
    ADD CONSTRAINT ck_tts_job_result CHECK (
        (phase = 'RESULT_STAGED' AND source_wav_file_id IS NOT NULL AND result_staged_at IS NOT NULL AND terminal_at IS NULL)
        OR (phase <> 'RESULT_STAGED' AND source_wav_file_id IS NULL)
    ),
    ADD CONSTRAINT ck_tts_job_terminal CHECK (
        (phase IN ('FAILED', 'DISCARDED') AND terminal_at IS NOT NULL)
        OR (phase NOT IN ('FAILED', 'DISCARDED') AND terminal_at IS NULL)
    );
