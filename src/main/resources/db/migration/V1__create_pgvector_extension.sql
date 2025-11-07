-- Flyway migration: create pgvector extension for semantic search
-- This migration will succeed only if running on PostgreSQL with superuser rights
CREATE EXTENSION IF NOT EXISTS vector;
