-- V38: Add StockProduct link to VaccinationRecord
-- This migration adds a foreign key to link vaccination records with the stock product used.

-- Add stock_product_id column to vaccination_record
ALTER TABLE vaccination_record 
ADD COLUMN IF NOT EXISTS stock_product_id BIGINT REFERENCES stock_product(product_id);

-- Add index for the new column
CREATE INDEX IF NOT EXISTS idx_vaccination_stock_product ON vaccination_record(stock_product_id);

-- Comment on column
COMMENT ON COLUMN vaccination_record.stock_product_id IS 'Reference to the stock product (vaccine batch) used for this vaccination';
