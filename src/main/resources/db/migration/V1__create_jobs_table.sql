CREATE TYPE job_status AS ENUM (
    'PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED'
);

CREATE TABLE jobs
(
    id BIGSERIAL PRIMARY KEY,
    job_type TEXT NOT NULL,
    lane TEXT NOT NULL DEFAULT 'medium',
    payload JSONB NOT NULL,
    priority INTEGER NOT NULL DEFAULT 5,
    worker_id TEXT,
    max_attempts INTEGER NOT NULL DEFAULT 5,
    status job_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    run_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    attempts_made INTEGER NOT NULL DEFAULT 0,
    progress INTEGER NOT NULL DEFAULT 0 CHECK (progress BETWEEN 0 AND 100),
    -- claimed_at is written once and records when a worker took the job.
    -- last_progress_at is overwritten and records whether it is still alive.
    -- Do not merge them. Merging destroys claimed_at - created_at,
    -- which is how long the job waited before anyone picked it up.

    claimed_at TIMESTAMPTZ,
    last_progress_at TIMESTAMPTZ,
    last_error TEXT,
    result_key TEXT,
    duration_ms INTEGER,
    failed_at TIMESTAMPTZ

);






