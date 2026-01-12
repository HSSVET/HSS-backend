ALTER TABLE owner ADD COLUMN deleted BOOLEAN DEFAULT FALSE;

-- Update existing records to have deleted = false (redundant with default but explicit)
UPDATE owner SET deleted = FALSE WHERE deleted IS NULL;
