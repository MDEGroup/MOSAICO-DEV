ALTER TABLE benchmark_runs
    ADD COLUMN IF NOT EXISTS langfuse_run_name VARCHAR(255);

ALTER TABLE schedule_configs
    ADD COLUMN IF NOT EXISTS langfuse_run_name VARCHAR(255);
