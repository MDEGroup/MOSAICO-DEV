-- Create the join table for Benchmark-Skill ManyToMany relationship
CREATE TABLE IF NOT EXISTS benchmark_assess (
    benchmark_id VARCHAR(255) NOT NULL,
    skill_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (benchmark_id, skill_id),
    CONSTRAINT fk_benchmark_assess_benchmark FOREIGN KEY (benchmark_id) REFERENCES benchmarks(id),
    CONSTRAINT fk_benchmark_assess_skill FOREIGN KEY (skill_id) REFERENCES skills(id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_benchmark_assess_benchmark_id ON benchmark_assess(benchmark_id);
CREATE INDEX IF NOT EXISTS idx_benchmark_assess_skill_id ON benchmark_assess(skill_id);
