-- V40: Insert owner and animal mock data for testing
-- This migration adds realistic test data with clinic associations

-- ============================================
-- 1. ENSURE TEST CLINIC EXISTS
-- ============================================
DO $$
DECLARE
    v_clinic_id BIGINT;
BEGIN
    -- Try to find existing clinic or create a test one
    SELECT clinic_id INTO v_clinic_id FROM clinic LIMIT 1;
    
    IF v_clinic_id IS NULL THEN
        -- No clinic exists, create one
        INSERT INTO clinic (clinic_id, name, address, phone, email, license_number, created_at, updated_at)
        VALUES (1, 'Test Veteriner Kliniği', 'Test Adres, İstanbul', '+905551234567', 'test@vet.com', 'VET-TEST-001', NOW(), NOW())
        RETURNING clinic_id INTO v_clinic_id;
        
        RAISE NOTICE 'Created new test clinic with ID: %', v_clinic_id;
    ELSE
        RAISE NOTICE 'Using existing clinic with ID: %', v_clinic_id;
    END IF;
    
    -- Store clinic_id in a temporary table for use in next steps
    CREATE TEMP TABLE IF NOT EXISTS temp_clinic_id (id BIGINT);
    DELETE FROM temp_clinic_id;
    INSERT INTO temp_clinic_id VALUES (v_clinic_id);
END $$;

-- ============================================
-- 2. INSERT OWNERS (Hasta Sahipleri)
-- ============================================
DO $$
DECLARE
    v_clinic_id BIGINT;
BEGIN
    SELECT id INTO v_clinic_id FROM temp_clinic_id LIMIT 1;
    
    -- Check if owners already exist
    IF NOT EXISTS (SELECT 1 FROM owner WHERE owner_id >= 100 AND owner_id <= 105) THEN
        INSERT INTO owner (owner_id, clinic_id, first_name, last_name, phone, email, address, created_at, updated_at)
        VALUES 
        (100, v_clinic_id, 'Elif', 'Yılmaz', '+905551112233', 'elif.yilmaz@test.com', 'Bağdat Cad. No:45 Kadıköy/İstanbul', NOW(), NOW()),
        (101, v_clinic_id, 'Burak', 'Öztürk', '+905552223344', 'burak.ozturk@test.com', 'Nispetiye Cad. No:78 Etiler/İstanbul', NOW(), NOW()),
        (102, v_clinic_id, 'Selin', 'Aydın', '+905553334455', 'selin.aydin@test.com', 'Şişli Cad. No:23 Şişli/İstanbul', NOW(), NOW()),
        (103, v_clinic_id, 'Can', 'Koç', '+905554445566', 'can.koc@test.com', 'Barbaros Bulvarı No:156 Beşiktaş/İstanbul', NOW(), NOW()),
        (104, v_clinic_id, 'Deniz', 'Arslan', '+905555556677', 'deniz.arslan@test.com', 'Bostancı Cad. No:89 Bostancı/İstanbul', NOW(), NOW());
        
        RAISE NOTICE '✅ Inserted 5 owners';
    ELSE
        RAISE NOTICE 'ℹ️  Owners already exist, skipping';
    END IF;
END $$;

-- ============================================
-- 3. INSERT ANIMALS (Hastalar)
-- ============================================
DO $$
DECLARE
    v_clinic_id BIGINT;
BEGIN
    SELECT id INTO v_clinic_id FROM temp_clinic_id LIMIT 1;
    
    -- Check if animals already exist  
    IF NOT EXISTS (SELECT 1 FROM animal WHERE animal_id = 100) THEN
        INSERT INTO animal (animal_id, clinic_id, owner_id, name, species_id, breed_id, gender, birth_date, weight, color, microchip_no, allergies, chronic_diseases, notes, created_at, updated_at)
        VALUES 
        -- Elif Yılmaz'ın hayvanları
        (100, v_clinic_id, 100, 'Max', 100, 100, 'MALE', '2020-05-15', 28.50, 'Altın Sarısı', '123456789000001', NULL, NULL, 'Çok enerjik ve oyuncu köpek', NOW(), NOW()),
        (101, v_clinic_id, 100, 'Luna', 101, 102, 'FEMALE', '2021-03-20', 4.20, 'Gri', '123456789000002', 'Tavuk proteini', NULL, 'Sakin ve sevecen kedi', NOW(), NOW()),
        
        -- Burak Öztürk'ün hayvanları
        (102, v_clinic_id, 101, 'Rocky', 100, 100, 'MALE', '2019-08-10', 35.00, 'Siyah-Kahverengi', '123456789000003', NULL, 'Kalça Displazisi', 'Düzenli kontrol gerekli', NOW(), NOW()),
        (103, v_clinic_id, 101, 'Bella', 100, 101, 'FEMALE', '2022-01-15', 22.00, 'Sarı', '123456789000004', NULL, NULL, 'Genç ve enerjik labrador', NOW(), NOW()),
        
        -- Selin Aydın'ın hayvanları
        (104, v_clinic_id, 102, 'Pamuk', 101, 103, 'FEMALE', '2020-11-25', 3.80, 'Beyaz-Tekir', '123456789000005', NULL, NULL, 'Çok temiz ve düzenli kedi', NOW(), NOW()),
        (105, v_clinic_id, 102, 'Minnoş', 101, 103, 'MALE', '2023-02-14', 3.50, 'Tekir', '123456789000006', NULL, NULL, 'Yavru kedi, çok oyuncu', NOW(), NOW()),
        
        -- Can Koç'un hayvanı
        (106, v_clinic_id, 103, 'Zeus', 100, 100, 'MALE', '2018-12-05', 30.00, 'Siyah-Beyaz', '123456789000007', 'Sığır eti', 'Deri alerjisi', 'Özel gıda gerektiriyor', NOW(), NOW()),
        
        -- Deniz Arslan'ın hayvanları
        (107, v_clinic_id, 104, 'Charlie', 100, 101, 'MALE', '2021-07-20', 12.00, 'Üç Renkli', '123456789000008', NULL, NULL, 'Çok meraklı köpek', NOW(), NOW()),
        (108, v_clinic_id, 104, 'Karamel', 101, 102, 'FEMALE', '2023-06-01', 3.20, 'Kahverengi', '123456789000009', NULL, NULL, 'Scottish Fold kedi', NOW(), NOW());
        
        RAISE NOTICE '✅ Inserted 9 animals';
    ELSE
        RAISE NOTICE 'ℹ️  Animals already exist, skipping';
    END IF;
END $$;

-- ============================================
-- 4. VACCINATION RECORDS - SKIPPED FOR NOW
-- ============================================
-- Skipping vaccination records to avoid FK constraint issues
-- These can be added later via UI or separate migration

-- ============================================
-- 5. UPDATE SEQUENCES
-- ============================================
SELECT setval('owner_owner_id_seq', GREATEST(110, (SELECT COALESCE(MAX(owner_id), 0) FROM owner)), true);
SELECT setval('animal_animal_id_seq', GREATEST(115, (SELECT COALESCE(MAX(animal_id), 0) FROM animal)), true);
SELECT setval('vaccination_record_vaccination_record_id_seq', GREATEST(120, (SELECT COALESCE(MAX(vaccination_record_id), 0) FROM vaccination_record)), true);

-- ============================================
-- CLEANUP
-- ============================================
DROP TABLE IF EXISTS temp_clinic_id;

-- ============================================
-- SUCCESS SUMMARY
-- ============================================
DO $$
DECLARE
    owner_count INTEGER;
    animal_count INTEGER;
    vacc_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO owner_count FROM owner WHERE owner_id >= 100;
    SELECT COUNT(*) INTO animal_count FROM animal WHERE animal_id >= 100;
    SELECT COUNT(*) INTO vacc_count FROM vaccination_record WHERE vaccination_record_id >= 100;
    
    RAISE NOTICE '';
    RAISE NOTICE '====================================';
    RAISE NOTICE '✅ MOCK DATA MIGRATION COMPLETED';
    RAISE NOTICE '====================================';
    RAISE NOTICE 'Owners: % records', owner_count;
    RAISE NOTICE 'Animals: % records', animal_count;
    RAISE NOTICE 'Vaccinations: % records', vacc_count;
    RAISE NOTICE '';
    RAISE NOTICE 'Test Data Ready:';
    RAISE NOTICE '  - Max (Golden Retriever) - Elif Yılmaz';
    RAISE NOTICE '  - Luna (British Shorthair) - Elif Yılmaz';
    RAISE NOTICE '  - Rocky (Golden Retriever) - Burak Öztürk';
    RAISE NOTICE '  - Bella (Labrador) - Burak Öztürk';
    RAISE NOTICE '  - Pamuk (Tekir) - Selin Aydın';
    RAISE NOTICE '  - Minnoş (Tekir) - Selin Aydın';
    RAISE NOTICE '  - Zeus (Golden Retriever) - Can Koç';
    RAISE NOTICE '  - Charlie (Labrador) - Deniz Arslan';
    RAISE NOTICE '  - Karamel (British Shorthair) - Deniz Arslan';
    RAISE NOTICE '====================================';
END $$;
