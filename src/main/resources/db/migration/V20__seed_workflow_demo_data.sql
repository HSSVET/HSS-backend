-- V20: Comprehensive Workflow Demo Data for 'sevketugurel' clinic
-- This migration adds realistic data to demonstrate all 3 workflow scenarios

-- ============================================================
-- 1. VACCINES AND MEDICINES (Reference Data)
-- ============================================================

-- Insert vaccines
INSERT INTO vaccine (vaccine_name, administration_route, age_requirement_months, booster_required, notes, created_by)
SELECT 'Nobivac Rabies', 'Subcutaneous', 3, true, 'Kuduz aşısı - Yıllık tekrar gerekir', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM vaccine WHERE vaccine_name = 'Nobivac Rabies');

INSERT INTO vaccine (vaccine_name, administration_route, age_requirement_months, booster_required, notes, created_by)
SELECT 'Eurican DHPPi', 'Subcutaneous', 2, true, 'Köpek karma aşısı (Distemper, Hepatit, Parvo, Parainfluenza)', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM vaccine WHERE vaccine_name = 'Eurican DHPPi');

INSERT INTO vaccine (vaccine_name, administration_route, age_requirement_months, booster_required, notes, created_by)
SELECT 'Felocell CVR', 'Subcutaneous', 2, true, 'Kedi karma aşısı (Calicivirus, Rhinotracheitis)', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM vaccine WHERE vaccine_name = 'Felocell CVR');

INSERT INTO vaccine (vaccine_name, administration_route, age_requirement_months, booster_required, notes, created_by)
SELECT 'Nobivac Lepto', 'Subcutaneous', 3, true, 'Leptospiroz aşısı', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM vaccine WHERE vaccine_name = 'Nobivac Lepto');

-- Insert medicines
INSERT INTO medicine (medicine_name, active_substance, usage_area, administration_route, dosage_form, strength, notes, created_by)
SELECT 'Amoksisilin 250mg', 'Amoksisilin', 'Antibiyotik', 'Oral', 'Tablet', '250mg', 'Geniş spektrumlu antibiyotik', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM medicine WHERE medicine_name = 'Amoksisilin 250mg');

INSERT INTO medicine (medicine_name, active_substance, usage_area, administration_route, dosage_form, strength, notes, created_by)
SELECT 'Meloksikam 1.5mg/ml', 'Meloksikam', 'Ağrı Kesici/Antiinflamatuar', 'Oral', 'Süspansiyon', '1.5mg/ml', 'NSAİİ - Ağrı ve iltihaplanma', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM medicine WHERE medicine_name = 'Meloksikam 1.5mg/ml');

INSERT INTO medicine (medicine_name, active_substance, usage_area, administration_route, dosage_form, strength, notes, created_by)
SELECT 'Ketamin %10', 'Ketamin HCl', 'Anestezi', 'IV/IM', 'Enjeksiyon', '100mg/ml', 'Genel anestezi indüksiyonu', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM medicine WHERE medicine_name = 'Ketamin %10');

-- ============================================================
-- 2. STOCK PRODUCTS (Inventory)
-- ============================================================

INSERT INTO stock_product (name, barcode, lot_no, production_date, expiration_date, min_stock, max_stock, current_stock, unit_cost, selling_price, category, supplier, location, created_by)
SELECT 'Nobivac Rabies Aşısı', '8713184100115', 'LOT2024A', '2024-01-15', '2026-01-15', 10, 100, 45, 85.00, 150.00, 'VACCINE', 'MSD Animal Health', 'Buzdolabı A', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM stock_product WHERE barcode = '8713184100115');

INSERT INTO stock_product (name, barcode, lot_no, production_date, expiration_date, min_stock, max_stock, current_stock, unit_cost, selling_price, category, supplier, location, created_by)
SELECT 'Eurican DHPPi Aşısı', '3661103048503', 'LOT2024B', '2024-02-20', '2026-02-20', 10, 100, 38, 120.00, 200.00, 'VACCINE', 'Boehringer Ingelheim', 'Buzdolabı A', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM stock_product WHERE barcode = '3661103048503');

INSERT INTO stock_product (name, barcode, lot_no, production_date, expiration_date, min_stock, max_stock, current_stock, unit_cost, selling_price, category, supplier, location, created_by)
SELECT 'Amoksisilin 250mg Tablet', '8699536090122', 'LOT2024C', '2024-03-10', '2026-03-10', 20, 200, 5, 2.50, 8.00, 'MEDICINE', 'Deva Holding', 'Raf B2', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM stock_product WHERE barcode = '8699536090122');

INSERT INTO stock_product (name, barcode, lot_no, production_date, expiration_date, min_stock, max_stock, current_stock, unit_cost, selling_price, category, supplier, location, created_by)
SELECT 'Kedi Aşıları', '8713184100200', 'LOT2024D', '2024-04-01', '2026-04-01', 5, 50, 8, 95.00, 180.00, 'VACCINE', 'Zoetis', 'Buzdolabı A', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM stock_product WHERE barcode = '8713184100200');

INSERT INTO stock_product (name, barcode, lot_no, production_date, expiration_date, min_stock, max_stock, current_stock, unit_cost, selling_price, category, supplier, location, created_by)
SELECT 'Cerrahi Eldiven (M)', '8699536001234', 'LOT2024E', '2024-01-01', '2027-01-01', 50, 500, 12, 0.50, 2.00, 'SUPPLY', 'Medical Supplies Co', 'Depo C', 'SYSTEM_DEMO'
WHERE NOT EXISTS (SELECT 1 FROM stock_product WHERE barcode = '8699536001234');

-- ============================================================
-- 3. VACCINATION RECORDS (Past vaccinations for demo animals)
-- ============================================================
DO $$
DECLARE
    target_clinic_id INT;
    animal_boncuk_id INT;
    animal_rex_id INT;
    animal_mavis_id INT;
    vaccine_rabies_id INT;
    vaccine_dhppi_id INT;
    vaccine_felocell_id INT;
BEGIN
    -- Get clinic ID
    SELECT clinic_id INTO target_clinic_id FROM clinic WHERE name = 'sevketugurel' LIMIT 1;
    
    -- Get animal IDs
    SELECT a.animal_id INTO animal_boncuk_id FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    SELECT a.animal_id INTO animal_rex_id FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    SELECT a.animal_id INTO animal_mavis_id FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Maviş' AND o.email = 'ayse.kaya@demo.com' LIMIT 1;
    
    -- Get vaccine IDs
    SELECT vaccine_id INTO vaccine_rabies_id FROM vaccine WHERE vaccine_name = 'Nobivac Rabies' LIMIT 1;
    SELECT vaccine_id INTO vaccine_dhppi_id FROM vaccine WHERE vaccine_name = 'Eurican DHPPi' LIMIT 1;
    SELECT vaccine_id INTO vaccine_felocell_id FROM vaccine WHERE vaccine_name = 'Felocell CVR' LIMIT 1;

    -- Boncuk (Cat) - Past vaccinations
    IF animal_boncuk_id IS NOT NULL AND vaccine_felocell_id IS NOT NULL THEN
        INSERT INTO vaccination_record (animal_id, vaccine_id, vaccine_name, date, next_due_date, batch_number, veterinarian_name, notes, created_by)
        SELECT animal_boncuk_id, vaccine_felocell_id, 'Felocell CVR', CURRENT_DATE - INTERVAL '6 months', CURRENT_DATE + INTERVAL '6 months', 'FEL2024001', 'Dr. Şevket Uğurel', 'İlk doz kedi karma aşısı uygulandı.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM vaccination_record WHERE animal_id = animal_boncuk_id AND vaccine_id = vaccine_felocell_id);
        
        INSERT INTO vaccination_record (animal_id, vaccine_id, vaccine_name, date, next_due_date, batch_number, veterinarian_name, notes, created_by)
        SELECT animal_boncuk_id, vaccine_rabies_id, 'Nobivac Rabies', CURRENT_DATE - INTERVAL '11 months', CURRENT_DATE + INTERVAL '1 month', 'RAB2024001', 'Dr. Şevket Uğurel', 'Kuduz aşısı uygulandı. Yakında rapel gerekecek.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM vaccination_record WHERE animal_id = animal_boncuk_id AND vaccine_id = vaccine_rabies_id);
    END IF;

    -- Rex (Dog) - Past vaccinations
    IF animal_rex_id IS NOT NULL AND vaccine_dhppi_id IS NOT NULL THEN
        INSERT INTO vaccination_record (animal_id, vaccine_id, vaccine_name, date, next_due_date, batch_number, veterinarian_name, notes, created_by)
        SELECT animal_rex_id, vaccine_dhppi_id, 'Eurican DHPPi', CURRENT_DATE - INTERVAL '3 months', CURRENT_DATE + INTERVAL '9 months', 'DHP2024001', 'Dr. Şevket Uğurel', 'Köpek karma aşısı rapeli yapıldı.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM vaccination_record WHERE animal_id = animal_rex_id AND vaccine_id = vaccine_dhppi_id);
        
        INSERT INTO vaccination_record (animal_id, vaccine_id, vaccine_name, date, next_due_date, batch_number, veterinarian_name, notes, created_by)
        SELECT animal_rex_id, vaccine_rabies_id, 'Nobivac Rabies', CURRENT_DATE - INTERVAL '2 months', CURRENT_DATE + INTERVAL '10 months', 'RAB2024002', 'Dr. Şevket Uğurel', 'Yıllık kuduz aşısı yapıldı.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM vaccination_record WHERE animal_id = animal_rex_id AND vaccine_id = vaccine_rabies_id);
    END IF;
END $$;


-- ============================================================
-- 4. LAB TESTS (Recent tests for demo animals)
-- ============================================================
DO $$
DECLARE
    animal_boncuk_id INT;
    animal_rex_id INT;
    animal_mavis_id INT;
BEGIN
    -- Get animal IDs
    SELECT a.animal_id INTO animal_boncuk_id FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    SELECT a.animal_id INTO animal_rex_id FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    SELECT a.animal_id INTO animal_mavis_id FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Maviş' AND o.email = 'ayse.kaya@demo.com' LIMIT 1;

    -- Rex - Preoperative blood test (Completed)
    IF animal_rex_id IS NOT NULL THEN
        INSERT INTO lab_tests (animal_id, test_name, date, status, created_by)
        SELECT animal_rex_id, 'Tam Kan Sayımı (CBC)', CURRENT_DATE - INTERVAL '7 days', 'COMPLETED', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM lab_tests WHERE animal_id = animal_rex_id AND test_name = 'Tam Kan Sayımı (CBC)');
        
        INSERT INTO lab_tests (animal_id, test_name, date, status, created_by)
        SELECT animal_rex_id, 'Biyokimya Paneli', CURRENT_DATE - INTERVAL '7 days', 'COMPLETED', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM lab_tests WHERE animal_id = animal_rex_id AND test_name = 'Biyokimya Paneli');
    END IF;

    -- Boncuk - Recent test (Pending)
    IF animal_boncuk_id IS NOT NULL THEN
        INSERT INTO lab_tests (animal_id, test_name, date, status, created_by)
        SELECT animal_boncuk_id, 'İdrar Tahlili', CURRENT_DATE, 'PENDING', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM lab_tests WHERE animal_id = animal_boncuk_id AND test_name = 'İdrar Tahlili');
    END IF;
    
    -- Maviş - Recent test (In Progress)
    IF animal_mavis_id IS NOT NULL THEN
        INSERT INTO lab_tests (animal_id, test_name, date, status, created_by)
        SELECT animal_mavis_id, 'Röntgen - Göğüs', CURRENT_DATE, 'IN_PROGRESS', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM lab_tests WHERE animal_id = animal_mavis_id AND test_name = 'Röntgen - Göğüs');
    END IF;
END $$;


-- ============================================================
-- 5. INVOICES (Payment records)
-- ============================================================
DO $$
DECLARE
    owner_mehmet_id INT;
    owner_ayse_id INT;
BEGIN
    -- Get owner IDs
    SELECT owner_id INTO owner_mehmet_id FROM owner WHERE email = 'mehmet.demir@demo.com' LIMIT 1;
    SELECT owner_id INTO owner_ayse_id FROM owner WHERE email = 'ayse.kaya@demo.com' LIMIT 1;

    -- Mehmet - Paid invoice for surgery
    IF owner_mehmet_id IS NOT NULL THEN
        INSERT INTO invoice (owner_id, invoice_number, date, due_date, amount, tax_amount, total_amount, status, payment_method, payment_date, description, created_by)
        SELECT owner_mehmet_id, 'INV-2024-0001', CURRENT_DATE - INTERVAL '6 months', CURRENT_DATE - INTERVAL '5 months' - INTERVAL '15 days', 2500.00, 450.00, 2950.00, 'PAID', 'Kredi Kartı', CURRENT_DATE - INTERVAL '6 months', 'Rex - Kısırlaştırma Operasyonu', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM invoice WHERE invoice_number = 'INV-2024-0001');
        
        INSERT INTO invoice (owner_id, invoice_number, date, due_date, amount, tax_amount, total_amount, status, payment_method, payment_date, description, created_by)
        SELECT owner_mehmet_id, 'INV-2024-0015', CURRENT_DATE - INTERVAL '1 week', CURRENT_DATE + INTERVAL '1 week', 350.00, 63.00, 413.00, 'PENDING', NULL, NULL, 'Boncuk - Aşı ve Muayene', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM invoice WHERE invoice_number = 'INV-2024-0015');
    END IF;

    -- Ayse - Overdue invoice
    IF owner_ayse_id IS NOT NULL THEN
        INSERT INTO invoice (owner_id, invoice_number, date, due_date, amount, tax_amount, total_amount, status, description, created_by)
        SELECT owner_ayse_id, 'INV-2024-0010', CURRENT_DATE - INTERVAL '2 months', CURRENT_DATE - INTERVAL '1 month', 180.00, 32.40, 212.40, 'OVERDUE', 'Maviş - Muayene ve Test', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM invoice WHERE invoice_number = 'INV-2024-0010');
    END IF;
END $$;


-- ============================================================
-- 6. REMINDERS (SMS notifications setup)
-- ============================================================
DO $$
DECLARE
    upcoming_appointment_id INT;
BEGIN
    -- Find an upcoming appointment for reminder
    SELECT appointment_id INTO upcoming_appointment_id 
    FROM appointment 
    WHERE status = 'SCHEDULED' AND date_time > CURRENT_TIMESTAMP 
    ORDER BY date_time ASC LIMIT 1;

    IF upcoming_appointment_id IS NOT NULL THEN
        INSERT INTO reminder (appointment_id, channel, send_time, message, status, created_by)
        SELECT upcoming_appointment_id, 'SMS', (SELECT date_time - INTERVAL '1 day' FROM appointment WHERE appointment_id = upcoming_appointment_id), 
               'Sayın hasta sahibi, yarın saat 10:00 randevunuz bulunmaktadır. Lütfen zamanında geliniz.', 'PENDING', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM reminder WHERE appointment_id = upcoming_appointment_id AND channel = 'SMS');
    END IF;
END $$;


-- ============================================================
-- 7. DOCUMENTS (Consent forms)
-- ============================================================
DO $$
DECLARE
    animal_rex_id INT;
BEGIN
    SELECT a.animal_id INTO animal_rex_id FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF animal_rex_id IS NOT NULL THEN
        INSERT INTO document (animal_id, document_type, title, file_name, file_url, date, created_by)
        SELECT animal_rex_id, 'CONSENT', 'Anestezi Onam Formu - Kısırlaştırma Operasyonu', 'anestezi_onam_formu_rex.pdf', '/documents/consent/anestezi_onam_formu_rex.pdf', CURRENT_DATE - INTERVAL '6 months', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM document WHERE animal_id = animal_rex_id AND document_type = 'CONSENT');
        
        INSERT INTO document (animal_id, document_type, title, file_name, file_url, date, created_by)
        SELECT animal_rex_id, 'REPORT', 'Operasyon Raporu - Kısırlaştırma', 'operasyon_raporu_rex.pdf', '/documents/reports/operasyon_raporu_rex.pdf', CURRENT_DATE - INTERVAL '6 months', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM document WHERE animal_id = animal_rex_id AND document_type = 'REPORT');
    END IF;
END $$;
