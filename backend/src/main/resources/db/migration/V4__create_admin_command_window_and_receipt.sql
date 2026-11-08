CREATE TABLE admin_command_window (
    id BINARY(16) NOT NULL,
    admin_account_id BINARY(16) NOT NULL,
    service_epoch CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    issued_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_admin_window_id_account UNIQUE (id, admin_account_id),
    CONSTRAINT fk_admin_window_account FOREIGN KEY (admin_account_id)
        REFERENCES admin_account (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    INDEX ix_admin_window_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE operation_receipt (
    id BINARY(16) NOT NULL,
    window_id BINARY(16) NOT NULL,
    admin_account_id BINARY(16) NOT NULL,
    operation_key BINARY(16) NOT NULL,
    fingerprint BINARY(32) NOT NULL,
    operation_type VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    http_status SMALLINT NOT NULL,
    result_code VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    result_summary JSON NOT NULL,
    committed_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_receipt_account_window_key UNIQUE (admin_account_id, window_id, operation_key),
    CONSTRAINT fk_receipt_window_account FOREIGN KEY (window_id, admin_account_id)
        REFERENCES admin_command_window (id, admin_account_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
