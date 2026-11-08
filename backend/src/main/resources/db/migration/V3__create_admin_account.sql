CREATE TABLE admin_account (
    id BINARY(16) NOT NULL,
    login_name VARCHAR(255) COLLATE utf8mb4_0900_as_cs NOT NULL,
    password_hash VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id),
    CONSTRAINT uk_admin_account_login_name UNIQUE (login_name),
    CONSTRAINT ck_admin_account_enabled CHECK (enabled IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
