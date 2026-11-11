ALTER TABLE tts_job
    DROP CHECK ck_tts_job_phase,
    DROP CHECK ck_tts_job_result,
    DROP CHECK ck_tts_job_terminal,
    ADD COLUMN conversion_instance BINARY(16) NULL,
    ADD COLUMN conversion_task BINARY(16) NULL,
    ADD COLUMN conversion_token BINARY(16) NULL,
    ADD COLUMN conversion_file_id BINARY(16) NULL,
    ADD CONSTRAINT fk_tts_conversion_instance FOREIGN KEY (conversion_instance)
        REFERENCES runtime_instance(id) ON DELETE RESTRICT,
    ADD CONSTRAINT ck_tts_job_phase CHECK (phase IN ('QUEUED', 'DISPATCHING', 'UNCERTAIN', 'RESULT_STAGED', 'CONVERTING', 'SUCCEEDED', 'FAILED', 'DISCARDED')),
    ADD CONSTRAINT ck_tts_job_result CHECK (
        (phase IN ('RESULT_STAGED', 'CONVERTING', 'SUCCEEDED') AND source_wav_file_id IS NOT NULL AND result_staged_at IS NOT NULL)
        OR (phase = 'FAILED' AND (source_wav_file_id IS NULL OR (result_staged_at IS NOT NULL AND failure_code IS NOT NULL AND LEFT(failure_code, 6) = 'LOCAL_')))
        OR (phase IN ('QUEUED', 'DISPATCHING', 'UNCERTAIN', 'DISCARDED') AND source_wav_file_id IS NULL)
    ),
    ADD CONSTRAINT ck_tts_job_terminal CHECK (
        (phase IN ('SUCCEEDED', 'FAILED', 'DISCARDED') AND terminal_at IS NOT NULL)
        OR (phase NOT IN ('SUCCEEDED', 'FAILED', 'DISCARDED') AND terminal_at IS NULL)
    ),
    ADD CONSTRAINT ck_tts_conversion_marker CHECK (phase <> 'CONVERTING' OR
        (conversion_instance IS NOT NULL AND conversion_task IS NOT NULL AND conversion_token IS NOT NULL AND conversion_file_id IS NOT NULL));

ALTER TABLE workspace_file
    DROP CHECK ck_workspace_file_kind,
    ADD CONSTRAINT ck_workspace_file_kind CHECK (kind IN ('WAV', 'MP3', 'TEMP', 'MANIFEST'));
