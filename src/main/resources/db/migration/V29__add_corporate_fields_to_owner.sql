-- V29: Add corporate and additional fields to owner table
-- These fields exist in the Entity but were missing from the database schema

ALTER TABLE owner 
    ADD COLUMN IF NOT EXISTS corporate_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS tax_no VARCHAR(50),
    ADD COLUMN IF NOT EXISTS tax_office VARCHAR(100),
    ADD COLUMN IF NOT EXISTS type VARCHAR(20) DEFAULT 'INDIVIDUAL',
    ADD COLUMN IF NOT EXISTS notes TEXT,
    ADD COLUMN IF NOT EXISTS warnings TEXT;

-- Create index for performance on commonly searched fields
CREATE INDEX IF NOT EXISTS idx_owner_corporate_name ON owner(corporate_name);
CREATE INDEX IF NOT EXISTS idx_owner_tax_no ON owner(tax_no);
