-- V17: Add clinic_id to operational tables for direct multi-tenancy

-- 1. Add clinic_id to ANIMAL
-- Ensure at least one clinic exists to prevent NOT NULL violations during backfill
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM clinic) THEN
        INSERT INTO clinic (name, slug, created_at, address, phone, email) 
        VALUES ('Default Clinic', 'default-clinic', NOW(), 'System Generated', '555-0000', 'admin@hss.local');
    END IF;
END $$;

-- 1. Add clinic_id to ANIMAL
ALTER TABLE animal ADD COLUMN clinic_id INT REFERENCES clinic(clinic_id);

-- Backfill animal.clinic_id from owner.clinic_id
UPDATE animal 
SET clinic_id = (SELECT clinic_id FROM owner WHERE owner.owner_id = animal.owner_id);

-- Fallback for orphans or missing owner links: assign to the first clinic
UPDATE animal 
SET clinic_id = (SELECT clinic_id FROM clinic ORDER BY clinic_id LIMIT 1) 
WHERE clinic_id IS NULL;

-- Make it NOT NULL after backfill
ALTER TABLE animal ALTER COLUMN clinic_id SET NOT NULL;

-- Create index for performance
CREATE INDEX idx_animal_clinic ON animal(clinic_id);


-- 2. Add clinic_id to APPOINTMENT
-- Temporarily drop the check constraint that blocks updates to past appointments
ALTER TABLE appointment DROP CONSTRAINT IF EXISTS chk_appointment_date_future;

ALTER TABLE appointment ADD COLUMN clinic_id INT REFERENCES clinic(clinic_id);

-- Backfill appointment.clinic_id from animal.clinic_id
UPDATE appointment 
SET clinic_id = (SELECT clinic_id FROM animal WHERE animal.animal_id = appointment.animal_id);

-- Fallback
UPDATE appointment 
SET clinic_id = (SELECT clinic_id FROM clinic ORDER BY clinic_id LIMIT 1) 
WHERE clinic_id IS NULL;

-- Make it NOT NULL after backfill
ALTER TABLE appointment ALTER COLUMN clinic_id SET NOT NULL;

-- Create index
CREATE INDEX idx_appointment_clinic ON appointment(clinic_id);

-- Re-add the check constraint (allow 1 day grace period for edge cases)
ALTER TABLE appointment ADD CONSTRAINT chk_appointment_date_future CHECK (date_time >= CURRENT_TIMESTAMP - INTERVAL '1 day') NOT VALID;


-- 3. Add clinic_id to SURGERY
ALTER TABLE surgery ADD COLUMN clinic_id INT REFERENCES clinic(clinic_id);

-- Backfill surgery.clinic_id from animal.clinic_id
UPDATE surgery 
SET clinic_id = (SELECT clinic_id FROM animal WHERE animal.animal_id = surgery.animal_id);

-- Fallback
UPDATE surgery 
SET clinic_id = (SELECT clinic_id FROM clinic ORDER BY clinic_id LIMIT 1) 
WHERE clinic_id IS NULL;

-- Make it NOT NULL
ALTER TABLE surgery ALTER COLUMN clinic_id SET NOT NULL;

-- Create index
CREATE INDEX idx_surgery_clinic ON surgery(clinic_id);


-- 4. Add clinic_id to HOSPITALIZATION
ALTER TABLE hospitalization ADD COLUMN clinic_id INT REFERENCES clinic(clinic_id);

-- Backfill hospitalization.clinic_id from animal.clinic_id
UPDATE hospitalization 
SET clinic_id = (SELECT clinic_id FROM animal WHERE animal.animal_id = hospitalization.animal_id);

-- Fallback
UPDATE hospitalization 
SET clinic_id = (SELECT clinic_id FROM clinic ORDER BY clinic_id LIMIT 1) 
WHERE clinic_id IS NULL;

-- Make it NOT NULL
ALTER TABLE hospitalization ALTER COLUMN clinic_id SET NOT NULL;

-- Create index
CREATE INDEX idx_hospitalization_clinic ON hospitalization(clinic_id);
