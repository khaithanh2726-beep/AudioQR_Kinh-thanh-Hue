ALTER TABLE file_asset
    DROP CHECK ck_file_asset_format,
    ADD CONSTRAINT ck_file_asset_format CHECK (format IN ('WAV', 'MP3')),
    ADD CONSTRAINT uk_file_asset_id_format UNIQUE (id, format);

-- Mở MP3 không cho phép con trỏ WAV của job trỏ nhầm sang MP3
ALTER TABLE tts_job
    DROP FOREIGN KEY fk_tts_job_source_wav,
    ADD COLUMN source_wav_format VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'WAV',
    ADD CONSTRAINT ck_tts_job_source_format CHECK (source_wav_format = 'WAV'),
    ADD CONSTRAINT fk_tts_job_source_wav_format FOREIGN KEY (source_wav_file_id, source_wav_format)
        REFERENCES file_asset (id, format) ON DELETE RESTRICT ON UPDATE RESTRICT,
    ADD CONSTRAINT uk_tts_job_id_target_wav UNIQUE (id, target_text_revision_id, source_wav_file_id);

CREATE TABLE audio_revision (
    id BINARY(16) NOT NULL,
    text_revision_id BINARY(16) NOT NULL,
    job_id BINARY(16) NOT NULL,
    wav_file_id BINARY(16) NOT NULL,
    mp3_file_id BINARY(16) NOT NULL,
    wav_format VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'WAV',
    mp3_format VARCHAR(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'MP3',
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_audio_revision_job UNIQUE (job_id),
    CONSTRAINT uk_audio_revision_id_text UNIQUE (id, text_revision_id),
    CONSTRAINT ck_audio_revision_formats CHECK (wav_format = 'WAV' AND mp3_format = 'MP3'),
    CONSTRAINT ck_audio_revision_distinct_files CHECK (wav_file_id <> mp3_file_id),
    CONSTRAINT fk_audio_revision_text FOREIGN KEY (text_revision_id)
        REFERENCES text_revision (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_audio_revision_job_text_wav FOREIGN KEY (job_id, text_revision_id, wav_file_id)
        REFERENCES tts_job (id, target_text_revision_id, source_wav_file_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_audio_revision_wav FOREIGN KEY (wav_file_id, wav_format)
        REFERENCES file_asset (id, format) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_audio_revision_mp3 FOREIGN KEY (mp3_file_id, mp3_format)
        REFERENCES file_asset (id, format) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE audio_review_ref (
    audio_revision_id BINARY(16) NOT NULL,
    locator LONGTEXT NOT NULL,
    limitations LONGTEXT NULL,
    recorded_by BINARY(16) NOT NULL,
    recorded_at DATETIME(6) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    PRIMARY KEY (audio_revision_id),
    CONSTRAINT fk_audio_review_ref_revision FOREIGN KEY (audio_revision_id)
        REFERENCES audio_revision (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_audio_review_ref_account FOREIGN KEY (recorded_by)
        REFERENCES admin_account (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE content_pair (
    id BINARY(16) NOT NULL,
    slot_id BINARY(16) NOT NULL,
    text_revision_id BINARY(16) NOT NULL,
    audio_revision_id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_content_pair_members UNIQUE (slot_id, text_revision_id, audio_revision_id),
    CONSTRAINT uk_content_pair_id_slot UNIQUE (id, slot_id),
    CONSTRAINT fk_content_pair_slot FOREIGN KEY (slot_id)
        REFERENCES content_slot (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_content_pair_text_slot FOREIGN KEY (text_revision_id, slot_id)
        REFERENCES text_revision (id, slot_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_content_pair_audio_text FOREIGN KEY (audio_revision_id, text_revision_id)
        REFERENCES audio_revision (id, text_revision_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE content_slot
    ADD COLUMN active_pair_id BINARY(16) NULL,
    ADD CONSTRAINT fk_content_slot_active_pair FOREIGN KEY (active_pair_id, id)
        REFERENCES content_pair (id, slot_id) ON DELETE RESTRICT ON UPDATE RESTRICT;
