-- Existing PostgreSQL databases managed outside Liquibase.
-- Apply before starting a backend that maps UserDb.language.
ALTER TABLE public."user"
    ADD COLUMN IF NOT EXISTS language VARCHAR(2) NOT NULL DEFAULT 'es';
