-- Create mosaico database for mosaico-app (executed only on fresh postgres initialisation)
DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'mosaico_db') THEN
    EXECUTE 'CREATE DATABASE mosaico_db';
  END IF;
END
$$;

-- Note: this DO block is idempotent and safe to run on already-initialized clusters.
