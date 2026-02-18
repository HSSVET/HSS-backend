-- V46: Seed animals (pets) and invoices for sevketugurel clinic owners
-- So owners have pets to display and "Odeme Al" has invoices to select.

DO $$
DECLARE
    v_clinic_id BIGINT;
    v_species_id BIGINT;
    v_breed_id BIGINT;
    r RECORD;
    v_animal_count INTEGER;
    v_invoice_count INTEGER;
    v_inv_num TEXT;
BEGIN
    SELECT clinic_id INTO v_clinic_id FROM clinic WHERE slug = 'sevketugurel';
    IF v_clinic_id IS NULL THEN
        RAISE NOTICE 'Clinic sevketugurel not found, skipping V46';
        RETURN;
    END IF;

    -- Get first species and breed for inserts
    SELECT species_id INTO v_species_id FROM species ORDER BY species_id LIMIT 1;
    SELECT breed_id INTO v_breed_id FROM breed WHERE species_id = v_species_id ORDER BY breed_id LIMIT 1;

    IF v_species_id IS NULL OR v_breed_id IS NULL THEN
        RAISE NOTICE 'Species or breed not found, skipping animal seed';
    ELSE
        -- For each owner in this clinic: if no animals, add 1-2
        FOR r IN SELECT owner_id, first_name, last_name FROM owner WHERE clinic_id = v_clinic_id
        LOOP
            SELECT COUNT(*) INTO v_animal_count FROM animal WHERE owner_id = r.owner_id;
            IF v_animal_count = 0 THEN
                INSERT INTO animal (clinic_id, owner_id, name, species_id, breed_id, gender, birth_date, weight, color, microchip_no, notes, created_at, updated_at)
                VALUES
                (v_clinic_id, r.owner_id, 'Pati', v_species_id, v_breed_id, 'MALE', CURRENT_DATE - INTERVAL '2 years', 5.0, 'Karışık', NULL, 'Örnek kayıt', NOW(), NOW()),
                (v_clinic_id, r.owner_id, 'Minnoş', v_species_id, v_breed_id, 'FEMALE', CURRENT_DATE - INTERVAL '1 year', 3.5, 'Beyaz', NULL, NULL, NOW(), NOW());
                RAISE NOTICE 'Inserted 2 animals for owner %', r.owner_id;
            END IF;
        END LOOP;
    END IF;

    -- Insert sample PENDING invoices for first few owners (so "Odeme Al" has options)
    FOR r IN SELECT owner_id FROM owner WHERE clinic_id = v_clinic_id ORDER BY owner_id LIMIT 3
    LOOP
        SELECT COUNT(*) INTO v_invoice_count FROM invoice WHERE owner_id = r.owner_id;
        IF v_invoice_count = 0 THEN
            v_inv_num := 'INV-SEV-' || to_char(r.owner_id, 'FM000') || '-' || to_char(NOW(), 'YYYYMMDD');
            INSERT INTO invoice (owner_id, invoice_number, date, due_date, amount, tax_amount, total_amount, status, description, created_at, updated_at)
            VALUES (r.owner_id, v_inv_num, CURRENT_DATE, CURRENT_DATE + INTERVAL '14 days', 250.00, 45.00, 295.00, 'PENDING', 'Örnek muayene ve hizmet', NOW(), NOW());
            RAISE NOTICE 'Inserted invoice % for owner %', v_inv_num, r.owner_id;
        END IF;
    END LOOP;
END $$;
