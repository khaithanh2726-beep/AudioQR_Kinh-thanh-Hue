CREATE TABLE place_view (
    id BINARY(16) NOT NULL,
    session_id BINARY(16) NOT NULL,
    opening_key BINARY(16) NOT NULL,
    live_place_id BINARY(16) NULL,
    place_key_snapshot BINARY(16) NOT NULL,
    place_name_snapshot LONGTEXT NOT NULL,
    language VARCHAR(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    viewed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_place_view_session_opening UNIQUE (session_id, opening_key),
    CONSTRAINT ck_place_view_language CHECK (language IN ('vi', 'en', 'fr', 'ru')),
    CONSTRAINT fk_place_view_session FOREIGN KEY (session_id)
        REFERENCES guest_session (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_place_view_live_place FOREIGN KEY (live_place_id)
        REFERENCES place (id) ON DELETE SET NULL ON UPDATE RESTRICT,
    INDEX ix_place_view_time (viewed_at, place_key_snapshot, language),
    INDEX ix_place_view_session_time (session_id, viewed_at, id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE INDEX ix_guest_session_started ON guest_session (started_at, id);
