-- V45: Link all owners to the 'sevketugurel' clinic and ensure sample owners exist
-- This makes /clinic/sevketugurel/owners show data after login.

DO $$
DECLARE
    v_clinic_id BIGINT;
    v_owner_count INTEGER;
BEGIN
    -- Find the sevketugurel clinic
    SELECT clinic_id INTO v_clinic_id FROM clinic WHERE slug = 'sevketugurel';

    IF v_clinic_id IS NULL THEN
        RAISE NOTICE 'Clinic with slug sevketugurel not found, skipping';
        RETURN;
    END IF;

    -- Update all existing owners to belong to this clinic
    UPDATE owner SET clinic_id = v_clinic_id, updated_at = NOW() WHERE clinic_id != v_clinic_id;

    -- Check how many owners exist for this clinic
    SELECT COUNT(*) INTO v_owner_count FROM owner WHERE clinic_id = v_clinic_id;

    -- If no owners, insert sample data
    IF v_owner_count = 0 THEN
        INSERT INTO owner (clinic_id, first_name, last_name, phone, email, address, type, created_at, updated_at)
        VALUES
        (v_clinic_id, 'Elif', 'Yılmaz', '+905551112233', 'elif.yilmaz@example.com', 'Bağdat Cad. No:45 Kadıköy/İstanbul', 'INDIVIDUAL', NOW(), NOW()),
        (v_clinic_id, 'Burak', 'Öztürk', '+905552223344', 'burak.ozturk@example.com', 'Nispetiye Cad. No:78 Etiler/İstanbul', 'INDIVIDUAL', NOW(), NOW()),
        (v_clinic_id, 'Selin', 'Aydın', '+905553334455', 'selin.aydin@example.com', 'Şişli Cad. No:23 Şişli/İstanbul', 'INDIVIDUAL', NOW(), NOW()),
        (v_clinic_id, 'Can', 'Koç', '+905554445566', 'can.koc@example.com', 'Barbaros Bulvarı No:156 Beşiktaş/İstanbul', 'INDIVIDUAL', NOW(), NOW()),
        (v_clinic_id, 'Vetcare A.Ş.', 'Kurumsal', '+902121234567', 'info@vetcare.com.tr', 'Levent Mah. No:10 Beşiktaş/İstanbul', 'CORPORATE', NOW(), NOW());

        -- Set corporate fields for the last one
        UPDATE owner SET corporate_name = 'Vetcare Hayvan Sağlığı A.Ş.', tax_no = '1234567890', tax_office = 'Beşiktaş V.D.'
        WHERE email = 'info@vetcare.com.tr' AND clinic_id = v_clinic_id;

        RAISE NOTICE 'Inserted 5 sample owners for clinic %', v_clinic_id;
    ELSE
        RAISE NOTICE 'Clinic % already has % owners, skipping insert', v_clinic_id, v_owner_count;
    END IF;
END $$;
