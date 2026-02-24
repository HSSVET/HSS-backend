-- V43: Insert mock clinic data for super-admin testing
-- This migration adds sample clinics to test the admin panel

-- ============================================
-- INSERT CLINICS
-- ============================================
DO $$
BEGIN
    -- Check if clinics already exist
    IF NOT EXISTS (SELECT 1 FROM clinic WHERE clinic_id IN (1, 2, 3)) THEN
        INSERT INTO clinic (clinic_id, name, address, phone, email, license_number, license_type, license_status, slug, created_at, updated_at)
        VALUES
        (1, 'Test Veteriner Kliniği', 'Bağdat Cad. No:123 Kadıköy/İstanbul', '+905551234567', 'test@vet.com', 'VET-TEST-001', 'STRT', 'ACTIVE', 'test-veteriner-klinigi', NOW(), NOW()),
        (2, 'VetCare Plus Kliniği', 'Nispetiye Cad. No:45 Etiler/İstanbul', '+905559876543', 'info@vetcareplus.com', 'VET-CARE-002', 'PRO', 'ACTIVE', 'vetcare-plus-klinigi', NOW(), NOW()),
        (3, 'Pati Dostları Veteriner', 'Şişli Cad. No:78 Şişli/İstanbul', '+905556547890', 'contact@patidostlari.com', 'VET-PATI-003', 'ENT', 'ACTIVE', 'pati-dostlari-veteriner', NOW(), NOW());
        
        RAISE NOTICE '✅ Inserted 3 mock clinics';
    ELSE
        RAISE NOTICE 'ℹ️  Clinics already exist, skipping';
    END IF;
END $$;

-- ============================================
-- UPDATE SEQUENCE
-- ============================================
SELECT setval('clinic_clinic_id_seq', GREATEST(10, (SELECT COALESCE(MAX(clinic_id), 0) FROM clinic)), true);

-- ============================================
-- SUCCESS MESSAGE
-- ============================================
DO $$
DECLARE
    clinic_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO clinic_count FROM clinic;
    
    RAISE NOTICE '';
    RAISE NOTICE '====================================';
    RAISE NOTICE '✅ CLINIC MOCK DATA COMPLETED';
    RAISE NOTICE '====================================';
    RAISE NOTICE 'Total Clinics: % records', clinic_count;
    RAISE NOTICE '';
    RAISE NOTICE 'Test Clinics:';
    RAISE NOTICE '  1. Test Veteriner Kliniği (STRT)';
    RAISE NOTICE '  2. VetCare Plus Kliniği (PRO)';
    RAISE NOTICE '  3. Pati Dostları Veteriner (ENT)';
    RAISE NOTICE '====================================';
END $$;
