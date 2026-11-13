-- Khóa bảo trì bền, không tự mở lại hoặc xóa lease khi ứng dụng khởi động lại.
CREATE TABLE ops_guard (
    id TINYINT NOT NULL PRIMARY KEY,
    mode VARCHAR(10) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    owner_token CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NULL,
    changed_at DATETIME(6) NOT NULL,
    CONSTRAINT ck_ops_guard_singleton CHECK (id = 1),
    CONSTRAINT ck_ops_guard_mode CHECK (
        (mode = 'RUNNING' AND owner_token IS NULL)
        OR (mode IN ('QUIESCING', 'FROZEN') AND owner_token IS NOT NULL)
    )
) ENGINE=InnoDB;

INSERT INTO ops_guard(id, mode, changed_at) VALUES(1, 'RUNNING', UTC_TIMESTAMP(6));

CREATE TABLE ops_activity (
    id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY,
    instance_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    kind VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    started_at DATETIME(6) NOT NULL,
    CONSTRAINT ck_ops_activity_kind CHECK (kind IN ('HTTP', 'TTS_QUEUE', 'LOCAL_AUDIO', 'CLEANUP', 'RETENTION'))
) ENGINE=InnoDB;
