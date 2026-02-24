-- V41: Insert vaccine data for vaccination system
-- This migration adds common veterinary vaccines

-- ============================================
-- INSERT VACCINES
-- ============================================
-- Note: protection_period might be INTERVAL or NUMERIC depending on schema
-- We'll skip it for now and only insert essential fields
INSERT INTO vaccine (vaccine_id, vaccine_name, administration_route, age_requirement_months, booster_required, notes, created_at, updated_at)
SELECT * FROM (VALUES
    -- Köpek Aşıları
    (1, 'Nobivac Rabies (Kuduz)', 'Subkutan/İntramüsküler', 3, true, 'Yasal zorunlu kuduz aşısı. Her yıl tekrarlanmalı. Koruma süresi: 12 ay', NOW(), NOW()),
    (2, 'Nobivac DHPPi (5''li Karma)', 'Subkutan', 2, true, 'Distemper, Hepatit, Parvovirüs, Parainfluenza, Leptospira. İlk aşı 6-8 haftalıkken, rapeller 3-4 hafta ara ile. Koruma: 12 ay', NOW(), NOW()),
    (3, 'Nobivac Lepto', 'Subkutan', 2, true, 'Leptospiroz hastalığına karşı koruma. Koruma süresi: 12 ay', NOW(), NOW()),
    (4, 'Eurican DHPPI+L', 'Subkutan', 2, true, 'Distemper, Hepatit, Parvovirüs, Parainfluenza ve Leptospira aşısı. Koruma: 12 ay', NOW(), NOW()),
    (5, 'Nobivac KC (Kennel Cough)', 'İntranazal', 2, true, 'Köpek öksürüğü aşısı, barınak/pansiyon öncesi önerilir. Koruma: 12 ay', NOW(), NOW()),
    
    -- Kedi Aşıları
    (6, 'Purevax Triple (Kedi 3''lü)', 'Subkutan', 2, true, 'Feline Viral Rhinotracheitis, Calicivirus, Panleukopenia. İlk aşı 8-9 haftalıkken. Koruma: 12 ay', NOW(), NOW()),
    (7, 'Purevax RCP', 'Subkutan', 2, true, 'Kedi Viral Rhinotracheitis, Calicivirus, Panleukopenia koruyucu aşı. Koruma: 12 ay', NOW(), NOW()),
    (8, 'Leucogen (FeLV)', 'Subkutan', 2, true, 'Feline Leukemia Virus aşısı. Dışarı çıkan kediler için önerilir. Koruma: 12 ay', NOW(), NOW()),
    (9, 'Nobivac Rabies (Kedi)', 'Subkutan/İntramüsküler', 3, true, 'Kediler için kuduz aşısı. Koruma: 12 ay', NOW(), NOW()),
    
    -- Ek Aşılar
    (10, 'Felocell CVR', 'Subkutan', 2, true, 'Kedi üçlü aşı alternatifi. Koruma: 12 ay', NOW(), NOW()),
    (11, 'Nobivac Piro', 'Subkutan', 5, true, 'Babesiosis hastalığı aşısı, kene bölgelerinde yaşayan köpekler için. Koruma: 6 ay', NOW(), NOW())
) AS new_data(vaccine_id, vaccine_name, administration_route, age_requirement_months, booster_required, notes, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM vaccine WHERE vaccine_id IN (1,2,3,4,5,6,7,8,9,10,11));

-- ============================================
-- UPDATE SEQUENCE
-- ============================================
SELECT setval('vaccine_vaccine_id_seq', GREATEST(15, (SELECT COALESCE(MAX(vaccine_id), 0) FROM vaccine)), true);

-- ============================================
-- SUCCESS MESSAGE
-- ============================================
DO $$
DECLARE
    vaccine_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO vaccine_count FROM vaccine;
    
    RAISE NOTICE '';
    RAISE NOTICE '====================================';
    RAISE NOTICE '✅ VACCINE DATA MIGRATION COMPLETED';
    RAISE NOTICE '====================================';
    RAISE NOTICE 'Total Vaccines: % records', vaccine_count;
    RAISE NOTICE '';
    RAISE NOTICE 'Köpek Aşıları:';
    RAISE NOTICE '  - Nobivac Rabies (Kuduz)';
    RAISE NOTICE '  - Nobivac DHPPi (5''li Karma)';
    RAISE NOTICE '  - Nobivac Lepto';
    RAISE NOTICE '  - Eurican DHPPI+L';
    RAISE NOTICE '  - Nobivac KC (Kennel Cough)';
    RAISE NOTICE '';
    RAISE NOTICE 'Kedi Aşıları:';
    RAISE NOTICE '  - Purevax Triple (3''lü)';
    RAISE NOTICE '  - Purevax RCP';
    RAISE NOTICE '  - Leucogen (FeLV)';
    RAISE NOTICE '  - Nobivac Rabies (Kedi)';
    RAISE NOTICE '====================================';
END $$;
