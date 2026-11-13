ALTER TABLE guest_session
    ADD COLUMN current_language CHAR(2) CHARACTER SET ascii COLLATE ascii_bin NULL,
    ADD COLUMN document_nonce BINARY(16) NULL,
    ADD COLUMN selection_seq BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN current_opening_id BINARY(16) NULL,
    ADD CONSTRAINT ck_guest_selection_seq CHECK (selection_seq >= 0),
    ADD CONSTRAINT ck_guest_current_language CHECK (current_language IS NULL OR current_language IN ('vi', 'en', 'fr', 'ru'));

UPDATE guest_session SET current_language = initial_language WHERE ended_at IS NULL;

CREATE TABLE guest_open_intent (
    id BINARY(16) NOT NULL,
    session_id BINARY(16) NOT NULL,
    live_place_id BINARY(16) NULL,
    place_key_snapshot BINARY(16) NOT NULL,
    place_name_snapshot LONGTEXT NOT NULL,
    current_language CHAR(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    state VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'OPEN',
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    closed_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_guest_opening_owner UNIQUE (id, session_id),
    CONSTRAINT ck_guest_opening_language CHECK (current_language IN ('vi', 'en', 'fr', 'ru')),
    CONSTRAINT ck_guest_opening_state CHECK ((state = 'OPEN' AND closed_at IS NULL) OR (state = 'CLOSED' AND closed_at IS NOT NULL)),
    CONSTRAINT fk_guest_opening_session FOREIGN KEY (session_id) REFERENCES guest_session (id) ON DELETE RESTRICT,
    CONSTRAINT fk_guest_opening_place FOREIGN KEY (live_place_id) REFERENCES place (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE guest_session
    ADD CONSTRAINT fk_guest_current_opening FOREIGN KEY (current_opening_id, id)
        REFERENCES guest_open_intent (id, session_id) ON DELETE RESTRICT;

CREATE TABLE guest_content_grant (
    id BINARY(16) NOT NULL,
    session_id BINARY(16) NOT NULL,
    opening_id BINARY(16) NOT NULL,
    place_key_snapshot BINARY(16) NOT NULL,
    place_name_snapshot LONGTEXT NOT NULL,
    language CHAR(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    text_revision_key BINARY(16) NOT NULL,
    text_hash BINARY(32) NOT NULL,
    pair_key BINARY(16) NOT NULL,
    audio_revision_key BINARY(16) NOT NULL,
    mp3_file_id BINARY(16) NULL,
    source VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    view_eligible BOOLEAN NOT NULL,
    available_languages VARCHAR(11) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    issued_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_guest_grant_language CHECK (language IN ('vi', 'en', 'fr', 'ru')),
    CONSTRAINT ck_guest_grant_source CHECK (source IN ('SELECT', 'LANGUAGE', 'RESTORE')),
    CONSTRAINT ck_guest_grant_view CHECK (source <> 'RESTORE' OR view_eligible = FALSE),
    CONSTRAINT ck_guest_grant_expiry CHECK (expires_at > issued_at),
    CONSTRAINT fk_guest_grant_session FOREIGN KEY (session_id) REFERENCES guest_session (id) ON DELETE RESTRICT,
    CONSTRAINT fk_guest_grant_opening FOREIGN KEY (opening_id, session_id) REFERENCES guest_open_intent (id, session_id) ON DELETE RESTRICT,
    CONSTRAINT fk_guest_grant_mp3 FOREIGN KEY (mp3_file_id) REFERENCES file_asset (id) ON DELETE RESTRICT,
    INDEX ix_guest_grant_expiry (expires_at, id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE guest_request_result (
    session_id BINARY(16) NOT NULL,
    request_key BINARY(16) NOT NULL,
    document_nonce BINARY(16) NOT NULL,
    selection_seq BIGINT NOT NULL,
    fingerprint BINARY(32) NOT NULL,
    action VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    opening_key BINARY(16) NOT NULL,
    opening_version BIGINT NOT NULL,
    grant_key BINARY(16) NULL,
    selection_kind VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    language CHAR(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    available_languages VARCHAR(11) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    PRIMARY KEY (session_id, request_key),
    CONSTRAINT uk_guest_request_selection UNIQUE (session_id, document_nonce, selection_seq),
    CONSTRAINT ck_guest_request_seq CHECK (selection_seq > 0),
    CONSTRAINT ck_guest_request_action CHECK (action IN ('SELECT', 'LANGUAGE', 'LEAVE')),
    CONSTRAINT fk_guest_request_session FOREIGN KEY (session_id) REFERENCES guest_session (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Claim đọc chỉ được nhả khi stream đóng, không tự hết hiệu lực theo đồng hồ
CREATE TABLE file_claim (
    id BINARY(16) NOT NULL,
    file_id BINARY(16) NOT NULL,
    owner_key BINARY(16) NOT NULL,
    kind VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_file_claim_kind CHECK (kind = 'READ'),
    CONSTRAINT fk_file_claim_file FOREIGN KEY (file_id) REFERENCES file_asset (id) ON DELETE RESTRICT,
    INDEX ix_file_claim_file (file_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
