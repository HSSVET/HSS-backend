-- V19: Move V18 demo data to the correct clinic ('sevketugurel')
-- The user reported that the active clinic is 'sevketugurel' (ID 6), not 'Sevket Ugurel Klinigi' (ID 18)

-- 1. Handle constraints for past appointments (just in case update triggers it)
ALTER TABLE appointment DROP CONSTRAINT IF EXISTS chk_appointment_date_future;

DO $$
DECLARE
    target_clinic_id INT;
BEGIN
    -- Find the target clinic ID (ID 6 based on user dump, but look up by name/email to be safe)
    SELECT clinic_id INTO target_clinic_id 
    FROM clinic 
    WHERE name = 'sevketugurel' OR (email = 'ugurelsevket@gmail.com' AND name != 'Sevket Ugurel Klinigi')
    ORDER BY clinic_id ASC -- Prefer lower ID (ID 6) if multiple
    LIMIT 1;

    -- If target clinic found, move the data
    IF target_clinic_id IS NOT NULL THEN
        RAISE NOTICE 'Moving demo data to Clinic ID: %', target_clinic_id;

        -- Update Owners (Mehmet & Ayse)
        UPDATE owner 
        SET clinic_id = target_clinic_id 
        WHERE email IN ('mehmet.demir@demo.com', 'ayse.kaya@demo.com');

        -- Update Animals belonging to these owners
        UPDATE animal 
        SET clinic_id = target_clinic_id 
        WHERE owner_id IN (SELECT owner_id FROM owner WHERE email IN ('mehmet.demir@demo.com', 'ayse.kaya@demo.com'));

        -- Update Appointments for these animals
        UPDATE appointment 
        SET clinic_id = target_clinic_id 
        WHERE animal_id IN (
            SELECT a.animal_id 
            FROM animal a 
            JOIN owner o ON a.owner_id = o.owner_id 
            WHERE o.email IN ('mehmet.demir@demo.com', 'ayse.kaya@demo.com')
        );

        -- Update Surgeries for these animals
        UPDATE surgery 
        SET clinic_id = target_clinic_id 
        WHERE animal_id IN (
            SELECT a.animal_id 
            FROM animal a 
            JOIN owner o ON a.owner_id = o.owner_id 
            WHERE o.email IN ('mehmet.demir@demo.com', 'ayse.kaya@demo.com')
        );

        -- Update Hospitalizations for these animals
        UPDATE hospitalization 
        SET clinic_id = target_clinic_id 
        WHERE animal_id IN (
            SELECT a.animal_id 
            FROM animal a 
            JOIN owner o ON a.owner_id = o.owner_id 
            WHERE o.email IN ('mehmet.demir@demo.com', 'ayse.kaya@demo.com')
        );
        
    ELSE
        RAISE NOTICE 'Target clinic "sevketugurel" not found. Skipping update.';
    END IF;
END $$;

-- 2. Restore constraint (NOT VALID to bypass existing past appointments)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_appointment_date_future') THEN
        ALTER TABLE appointment ADD CONSTRAINT chk_appointment_date_future CHECK (date_time >= CURRENT_TIMESTAMP - INTERVAL '1 day') NOT VALID;
    END IF;
END $$;
