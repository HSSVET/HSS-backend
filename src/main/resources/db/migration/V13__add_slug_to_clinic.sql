ALTER TABLE clinic ADD COLUMN slug VARCHAR(255);

-- Simple backfill for existing records (PostgreSQL specific functions)
-- Replaces spaces with dashes and converts to lower case.
-- Note: This is a basic migration. Complex slugification (Turkish chars) happens in Java for new records.
UPDATE clinic 
SET slug = LOWER(REGEXP_REPLACE(name, '\s+', '-', 'g'))
WHERE slug IS NULL;

-- Ensure uniqueness constraint is applied after backfill
ALTER TABLE clinic ADD CONSTRAINT uc_clinic_slug UNIQUE (slug);
