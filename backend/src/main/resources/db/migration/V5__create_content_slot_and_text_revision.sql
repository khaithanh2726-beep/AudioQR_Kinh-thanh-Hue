CREATE TABLE content_slot (
    id BINARY(16) NOT NULL,
    place_id BINARY(16) NOT NULL,
    language VARCHAR(2) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    draft_text_id BINARY(16) NULL,
    version BIGINT NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    CONSTRAINT uk_content_slot_place_language UNIQUE (place_id, language),
    CONSTRAINT uk_content_slot_id_place_language UNIQUE (id, place_id, language),
    CONSTRAINT ck_content_slot_language CHECK (language IN ('vi', 'en', 'fr', 'ru')),
    CONSTRAINT fk_content_slot_place FOREIGN KEY (place_id)
        REFERENCES place (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE text_revision (
    id BINARY(16) NOT NULL,
    slot_id BINARY(16) NOT NULL,
    body LONGTEXT NOT NULL,
    content_hash BINARY(32) NOT NULL,
    saved_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_text_revision_id_slot UNIQUE (id, slot_id),
    CONSTRAINT fk_text_revision_slot FOREIGN KEY (slot_id)
        REFERENCES content_slot (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Con trỏ bản nháp chỉ được chọn văn bản thuộc chính ngăn địa điểm/ngôn ngữ đó
ALTER TABLE content_slot
    ADD CONSTRAINT fk_content_slot_draft_text FOREIGN KEY (draft_text_id, id)
        REFERENCES text_revision (id, slot_id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE TABLE text_review_ref (
    text_revision_id BINARY(16) NOT NULL,
    locator LONGTEXT NOT NULL,
    limitations LONGTEXT NULL,
    recorded_by BINARY(16) NOT NULL,
    recorded_at DATETIME(6) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    PRIMARY KEY (text_revision_id),
    CONSTRAINT fk_text_review_ref_revision FOREIGN KEY (text_revision_id)
        REFERENCES text_revision (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_text_review_ref_account FOREIGN KEY (recorded_by)
        REFERENCES admin_account (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
