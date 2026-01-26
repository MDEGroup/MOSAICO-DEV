-- Add benchmark_id column to performance_kpis table to link KPIs with benchmarks
ALTER TABLE performance_kpis ADD COLUMN IF NOT EXISTS benchmark_id VARCHAR(255);

-- Create index for faster lookups by benchmark_id
CREATE INDEX IF NOT EXISTS idx_performance_kpis_benchmark_id ON performance_kpis(benchmark_id);
