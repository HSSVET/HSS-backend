-- Robust seeding script to guarantee demo data exists
DO $$
DECLARE
    v_clinic_id BIGINT;
    v_owner_id BIGINT;
    v_animal_id BIGINT;
    v_species_id BIGINT;
    v_breed_id BIGINT;
BEGIN
    -- 1. Get or Create Clinic (UseCase: User might not have the exact clinic name from V18)
    -- Try to find ANY clinic, fallback to creating one
    SELECT clinic_id INTO v_clinic_id FROM clinic LIMIT 1;
    
    IF v_clinic_id IS NULL THEN
        INSERT INTO clinic (name, slug, phone, email, address, city, district) 
        VALUES ('Demo Klinigi', 'demo-klinik', '555-1111', 'demo@hss.com', 'Demo Adres', 'Istanbul', 'Kadikoy') 
        RETURNING clinic_id INTO v_clinic_id;
    END IF;

    -- 2. Get or Create Owner 'Demo Admin'
    SELECT owner_id INTO v_owner_id FROM owner WHERE email = 'demo.admin@hss.com';
    
    IF v_owner_id IS NULL THEN
        INSERT INTO owner (clinic_id, first_name, last_name, email, phone, address, created_at, created_by) 
        VALUES (v_clinic_id, 'Demo', 'Admin', 'demo.admin@hss.com', '+90 500 000 00 00', 'Demo Address', CURRENT_TIMESTAMP, NULL) 
        RETURNING owner_id INTO v_owner_id;
    END IF;

    -- 3. Get or Create Species 'Kedi'
    SELECT species_id INTO v_species_id FROM species WHERE name = 'Kedi';
    IF v_species_id IS NULL THEN
        INSERT INTO species (name) VALUES ('Kedi') RETURNING species_id INTO v_species_id;
    END IF;
    
    -- 4. Get or Create Breed 'Tekir'
    SELECT breed_id INTO v_breed_id FROM breed WHERE name = 'Tekir' AND species_id = v_species_id;
    IF v_breed_id IS NULL THEN
        INSERT INTO breed (species_id, name) VALUES (v_species_id, 'Tekir') RETURNING breed_id INTO v_breed_id;
    END IF;

    -- 5. Get or Create Animal 'Pamuk'
    SELECT animal_id INTO v_animal_id FROM animal WHERE name = 'Pamuk' AND owner_id = v_owner_id;
    
    IF v_animal_id IS NULL THEN
        INSERT INTO animal (owner_id, clinic_id, name, species_id, breed_id, gender, birth_date, weight, height, sterilized, color, microchip_no, status, created_at, created_by)
        VALUES (v_owner_id, v_clinic_id, 'Pamuk', v_species_id, v_breed_id, 'FEMALE', CURRENT_DATE - INTERVAL '2 years', 4.5, 25.0, true, 'Beyaz', '999000111222', 'ACTIVE', CURRENT_TIMESTAMP, NULL)
        RETURNING animal_id INTO v_animal_id;
    ELSE
        -- Update existing Pamuk to have details
        UPDATE animal SET height = 25.0, sterilized = true WHERE animal_id = v_animal_id;
    END IF;

    -- 6. Insert Conditions for Pamuk if not exist
    INSERT INTO animal_conditions (animal_id, type, name, severity, diagnosis_date, status, notes, created_by)
    SELECT v_animal_id, 'ALLERGY', 'Polen Alerjisi', 'MILD', CURRENT_DATE - INTERVAL '1 year', 'ACTIVE', 'Bahar aylarında hapşırma ve kaşıntı.', NULL
    WHERE NOT EXISTS (SELECT 1 FROM animal_conditions WHERE animal_id = v_animal_id AND name = 'Polen Alerjisi');

    INSERT INTO animal_conditions (animal_id, type, name, severity, diagnosis_date, status, notes, created_by)
    SELECT v_animal_id, 'CHRONIC_CONDITION', 'Astım', 'MODERATE', CURRENT_DATE - INTERVAL '6 months', 'MANAGED', 'Stres durumlarında atak geçirebilir.', NULL
    WHERE NOT EXISTS (SELECT 1 FROM animal_conditions WHERE animal_id = v_animal_id AND name = 'Astım');

END $$;
