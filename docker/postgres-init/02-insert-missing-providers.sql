-- Insert placeholder providers for any provider_id referenced by agents but missing in providers
-- This is idempotent: it only inserts providers that don't already exist.
-- Useful during migration to satisfy FK constraints when agents reference provider ids
-- that were created outside of the providers table (e.g. migrated data from Mongo).

INSERT INTO providers (id, name, description, contact_url)
SELECT DISTINCT a.provider_id,
       ('migrated-provider-' || a.provider_id) AS name,
       'Auto-created placeholder provider to satisfy foreign key constraints during migration',
       NULL
FROM agents a
WHERE a.provider_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM providers p WHERE p.id = a.provider_id
  );

-- Optionally, you can later update these placeholder providers with real metadata.
