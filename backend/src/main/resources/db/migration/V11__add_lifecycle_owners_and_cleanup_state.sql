ALTER TABLE file_asset
    DROP CHECK ck_file_asset_ready,
    ADD COLUMN deletion_token BINARY(16) NULL,
    ADD COLUMN deletion_owner BINARY(16) NULL,
    ADD COLUMN deletion_task BINARY(16) NULL,
    ADD COLUMN deletion_started_at DATETIME(6) NULL,
    ADD COLUMN deleted_at DATETIME(6) NULL,
    ADD COLUMN cleanup_error VARCHAR(32) CHARACTER SET ascii COLLATE ascii_bin NULL,
    ADD CONSTRAINT ck_file_asset_ready CHECK (byte_size > 0 AND duration > 0 AND state IN ('READY', 'DELETING', 'DELETED')),
    ADD CONSTRAINT ck_file_asset_deletion CHECK (
        (state = 'READY' AND deletion_token IS NULL AND deletion_owner IS NULL AND deletion_task IS NULL AND deletion_started_at IS NULL AND deleted_at IS NULL)
        OR (state = 'DELETING' AND deletion_token IS NOT NULL AND deletion_owner IS NOT NULL AND deletion_task IS NOT NULL AND deletion_started_at IS NOT NULL AND deleted_at IS NULL)
        OR (state = 'DELETED' AND deletion_token IS NOT NULL AND deletion_owner IS NOT NULL AND deletion_task IS NOT NULL AND deletion_started_at IS NOT NULL AND deleted_at IS NOT NULL)
    );

-- Tác vụ chưa dispatch có thể bị loại bỏ khi địa điểm bị xóa, không tạo marker gửi provider giả
ALTER TABLE tts_job
    ADD COLUMN local_finished_at DATETIME(6) NULL,
    DROP CHECK ck_tts_job_dispatch,
    ADD CONSTRAINT ck_tts_job_dispatch CHECK (
        (phase IN ('QUEUED', 'DISCARDED') AND worker_instance IS NULL AND worker_token IS NULL AND dispatched_at IS NULL)
        OR (phase <> 'QUEUED' AND worker_instance IS NOT NULL AND worker_token IS NOT NULL AND dispatched_at IS NOT NULL)
    );

CREATE TABLE runtime_instance (
    id BINARY(16) NOT NULL PRIMARY KEY,
    node_key CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    root_key CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    process_id BIGINT NOT NULL,
    process_started_at DATETIME(6) NOT NULL,
    registered_at DATETIME(6) NOT NULL,
    stopped_at DATETIME(6) NULL,
    state VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    CONSTRAINT ck_runtime_instance_state CHECK (
        (state = 'RUNNING' AND stopped_at IS NULL) OR (state = 'STOPPED' AND stopped_at IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE runtime_task (
    id BINARY(16) NOT NULL PRIMARY KEY,
    instance_id BINARY(16) NOT NULL,
    kind VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    owner_key BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT ck_runtime_task_kind CHECK (kind IN ('READ', 'TTS', 'RECOVER', 'DELETE', 'TEMP')),
    CONSTRAINT fk_runtime_task_instance FOREIGN KEY (instance_id) REFERENCES runtime_instance(id) ON DELETE RESTRICT,
    INDEX ix_runtime_task_owner (owner_key, kind),
    INDEX ix_runtime_task_instance (instance_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE file_claim
    ADD COLUMN owner_instance BINARY(16) NULL,
    ADD CONSTRAINT fk_file_claim_instance FOREIGN KEY (owner_instance) REFERENCES runtime_instance(id) ON DELETE RESTRICT,
    ADD INDEX ix_file_claim_owner (owner_key, kind),
    ADD INDEX ix_file_claim_instance (owner_instance);

CREATE TABLE workspace_file (
    storage_key VARCHAR(120) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY,
    job_key BINARY(16) NOT NULL,
    worker_token BINARY(16) NOT NULL,
    owner_instance BINARY(16) NOT NULL,
    file_key BINARY(16) NULL,
    kind VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    created_at DATETIME(6) NOT NULL,
    eligible_since DATETIME(6) NULL,
    cleanup_task BINARY(16) NULL,
    CONSTRAINT ck_workspace_file_kind CHECK (kind IN ('WAV', 'TEMP', 'MANIFEST')),
    CONSTRAINT fk_workspace_file_instance FOREIGN KEY (owner_instance) REFERENCES runtime_instance(id) ON DELETE RESTRICT,
    INDEX ix_workspace_file_job (job_key),
    INDEX ix_workspace_file_asset (file_key)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
