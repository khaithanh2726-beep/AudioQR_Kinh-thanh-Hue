CREATE TABLE place (
    id BINARY(16) NOT NULL,
    display_name LONGTEXT NOT NULL,
    visible BOOLEAN NOT NULL DEFAULT FALSE,
    active_region_revision_id BINARY(16) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT ck_place_visible CHECK (visible IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE region_revision (
    id BINARY(16) NOT NULL,
    place_id BINARY(16) NOT NULL,
    polygon JSON NOT NULL,
    map_space_key LONGTEXT NOT NULL,
    geometry_hash BINARY(32) NOT NULL,
    saved_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_region_revision_id_place UNIQUE (id, place_id),
    CONSTRAINT fk_region_revision_place FOREIGN KEY (place_id)
        REFERENCES place (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Khóa tổ hợp chỉ cho phép trỏ đến bản vùng thuộc chính địa điểm đó
ALTER TABLE place
    ADD CONSTRAINT fk_place_active_region FOREIGN KEY (active_region_revision_id, id)
        REFERENCES region_revision (id, place_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
