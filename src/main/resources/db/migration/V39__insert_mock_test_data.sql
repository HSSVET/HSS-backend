-- V39: Insert basic mock test data (simplified - no clinic_id dependencies)
-- This migration adds minimal test data for development and UI testing

-- Note: We skip multi-tenancy fields (clinic_id) because they may not exist yet
-- or may have complex constraints. This focuses on core data only.

-- ============================================
-- 1. SPECIES & BREEDS
-- ============================================
INSERT INTO species (species_id, name, created_at, updated_at)
SELECT * FROM (VALUES
  (100, 'Köpek', NOW(), NOW()),
  (101, 'Kedi', NOW(), NOW())
) AS new_data(species_id, name, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM species WHERE species_id IN (100, 101));

INSERT INTO breed (breed_id, species_id, name, created_at, updated_at)
SELECT * FROM (VALUES
  (100, 100, 'Golden Retriever', NOW(), NOW()),
  (101, 100, 'Labrador', NOW(), NOW()),
  (102, 101, 'British Shorthair', NOW(), NOW()),
  (103, 101, 'Tekir', NOW(), NOW())
) AS new_data(breed_id, species_id, name, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM breed WHERE breed_id IN (100, 101, 102, 103));

-- ============================================
-- 2. STOCK PRODUCTS (for barcode scanning)
-- ============================================
INSERT INTO stock_product (product_id, name, barcode, lot_no, category, current_stock, min_stock, max_stock, expiration_date, unit_cost, selling_price, supplier, is_active, created_at, updated_at)
SELECT * FROM (VALUES
  (100, 'Nobivac Rabies', '8699456789012', 'RAB-2024-001', 'VACCINE', 50, 10, 100, '2026-12-31'::DATE, 35.00, 150.00, 'MSD', true, NOW(), NOW()),
  (101, 'Nobivac DHPPi', '8699456789029', 'DHPPI-2024-002', 'VACCINE', 45, 10, 100, '2026-11-30'::DATE, 45.00, 200.00, 'MSD', true, NOW(), NOW()),
  (102, 'Purevax Triple', '8699456789036', 'TRIPLE-2024-003', 'VACCINE', 40, 8, 80, '2026-10-31'::DATE, 40.00, 180.00, 'Boehringer', true, NOW(), NOW()),
  (103, 'Amoksisilin 500mg', '8699456789067', 'AMOX-2024-006', 'MEDICINE', 100, 20, 200, '2027-06-30'::DATE, 8.00, 25.00, 'Pfizer', true, NOW(), NOW())
) AS new_data(product_id, name, barcode, lot_no, category, current_stock, min_stock, max_stock, expiration_date, unit_cost, selling_price, supplier, is_active, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM stock_product WHERE product_id IN (100, 101, 102, 103));

-- ============================================
-- SEQUENCE UPDATES
-- ============================================
SELECT setval('species_species_id_seq', GREATEST(105, (SELECT COALESCE(MAX(species_id), 0) FROM species)), true);
SELECT setval('breed_breed_id_seq', GREATEST(110, (SELECT COALESCE(MAX(breed_id), 0) FROM breed)), true);
SELECT setval('stock_product_product_id_seq', GREATEST(110, (SELECT COALESCE(MAX(product_id), 0) FROM stock_product)), true);

-- SUCCESS MESSAGE
DO $$
BEGIN
  RAISE NOTICE '✅ Mock test data inserted successfully!';
  RAISE NOTICE '   - Species: 2 records';
  RAISE NOTICE '   - Breeds: 4 records';
  RAISE NOTICE '   - Stock Products: 4 records (with barcodes for testing)';
END $$;
