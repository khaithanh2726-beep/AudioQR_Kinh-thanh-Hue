CREATE TABLE guest_session (
    id BINARY(16) NOT NULL,
    start_request_key BINARY(16) NOT NULL,
    credential_hash BINARY(32) NOT NULL,
    service_epoch CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    start_fingerprint BINARY(32) NOT NULL,
    start_ticket_issued_at DATETIME(6) NOT NULL,
    start_ticket_expires_at DATETIME(6) NOT NULL,
    start_ticket_digest BINARY(32) NOT NULL,
    display_name LONGTEXT NOT NULL,
    initial_language CHAR(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    started_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    ended_at DATETIME(6) NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_guest_session_start_request_key UNIQUE (start_request_key),
    CONSTRAINT uk_guest_session_credential_hash UNIQUE (credential_hash),
    CONSTRAINT ck_guest_session_initial_language
        CHECK (initial_language IN ('vi', 'en', 'fr', 'ru'))
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
