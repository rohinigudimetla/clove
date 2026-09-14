CREATE TABLE jobs
(
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(255) UNIQUE CHECK (idempotency_key <> ''),
    payload JSONB NOT NULL CHECK (payload <> '{}'::jsonb) ,
    status VARCHAR(50) DEFAULT 'PENDING' NOT NULL,
    priority INTEGER DEFAULT 0 NOT NULL,
    worker_id VARCHAR(100) CHECK (worker_id <> ''),
    attempts_made INTEGER DEFAULT 0 NOT NULL CHECK (attempts_made >= 0),
    max_attempts INTEGER DEFAULT 5 NOT NULL CHECK (max_attempts > 0),
    last_error TEXT CHECK (last_error <> ''),
    run_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    locked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    finished_at TIMESTAMPTZ,
    failed_at TIMESTAMPTZ
);

CREATE INDEX idx_jobs_polling
    ON jobs (status, run_at, priority, id)
    WHERE status = 'PENDING';





