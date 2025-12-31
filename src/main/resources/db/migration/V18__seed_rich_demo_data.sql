-- V18: Seed rich demo data for 'Sevket Ugurel Klinigi' (ugurelsevket@gmail.com)

-- Define Clinic ID variable (will be used in subqueries)
-- Note: We use subqueries directly to be safe in pure SQL execution without variables

-- 1. Create Owners
INSERT INTO owner (clinic_id, first_name, last_name, email, phone, address, created_at, created_by)
SELECT 
    c.clinic_id,
    'Mehmet', 'Demir', 'mehmet.demir@demo.com', '+90 555 100 20 00', 'Moda, Kadikoy, Istanbul',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM clinic c WHERE c.name = 'Sevket Ugurel Klinigi'
AND NOT EXISTS (SELECT 1 FROM owner WHERE email = 'mehmet.demir@demo.com');

INSERT INTO owner (clinic_id, first_name, last_name, email, phone, address, created_at, created_by)
SELECT 
    c.clinic_id,
    'Ayse', 'Kaya', 'ayse.kaya@demo.com', '+90 555 300 40 00', 'Bagdat Caddesi, Istanbul',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM clinic c WHERE c.name = 'Sevket Ugurel Klinigi'
AND NOT EXISTS (SELECT 1 FROM owner WHERE email = 'ayse.kaya@demo.com');

-- 2. Create Animals
-- Helper species/breeds if not exist (Generic fallback)
INSERT INTO species (name) SELECT 'Kus' WHERE NOT EXISTS (SELECT 1 FROM species WHERE name = 'Kus');
INSERT INTO breed (species_id, name) 
SELECT s.species_id, 'Papagan' FROM species s WHERE s.name = 'Kus'
AND NOT EXISTS (SELECT 1 FROM breed b WHERE b.name = 'Papagan' AND b.species_id = s.species_id);

INSERT INTO breed (species_id, name) 
SELECT s.species_id, 'Siyam' FROM species s WHERE s.name = 'Kedi'
AND NOT EXISTS (SELECT 1 FROM breed b WHERE b.name = 'Siyam' AND b.species_id = s.species_id);

INSERT INTO breed (species_id, name) 
SELECT s.species_id, 'Alman Kurdu' FROM species s WHERE s.name = 'Kopek'
AND NOT EXISTS (SELECT 1 FROM breed b WHERE b.name = 'Alman Kurdu' AND b.species_id = s.species_id);

-- Boncuk (Cat) - Owner: Mehmet
INSERT INTO animal (owner_id, clinic_id, name, species_id, breed_id, gender, birth_date, weight, color, microchip_no, created_at, created_by)
SELECT 
    o.owner_id,
    o.clinic_id,
    'Boncuk',
    (SELECT species_id FROM species WHERE name = 'Kedi' LIMIT 1),
    (SELECT breed_id FROM breed WHERE name = 'Siyam' LIMIT 1),
    'FEMALE',
    CURRENT_DATE - INTERVAL '3 years',
    4.1,
    'Krem/Kahve',
    '999111222333444',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM owner o WHERE o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM animal WHERE name = 'Boncuk' AND owner_id = o.owner_id);

-- Rex (Dog) - Owner: Mehmet
INSERT INTO animal (owner_id, clinic_id, name, species_id, breed_id, gender, birth_date, weight, color, microchip_no, created_at, created_by)
SELECT 
    o.owner_id,
    o.clinic_id,
    'Rex',
    (SELECT species_id FROM species WHERE name = 'Kopek' LIMIT 1),
    (SELECT breed_id FROM breed WHERE name = 'Alman Kurdu' LIMIT 1),
    'MALE',
    CURRENT_DATE - INTERVAL '4 years',
    32.5,
    'Siyah/Sari',
    '999555666777888',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM owner o WHERE o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM animal WHERE name = 'Rex' AND owner_id = o.owner_id);

-- Maviş (Bird) - Owner: Ayse
INSERT INTO animal (owner_id, clinic_id, name, species_id, breed_id, gender, birth_date, weight, color, microchip_no, created_at, created_by)
SELECT 
    o.owner_id,
    o.clinic_id,
    'Maviş',
    (SELECT species_id FROM species WHERE name = 'Kus' LIMIT 1),
    (SELECT breed_id FROM breed WHERE name = 'Papagan' LIMIT 1),
    'MALE',
    CURRENT_DATE - INTERVAL '1 year',
    0.3,
    'Mavi/Yesil',
    '999888999000111',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM owner o WHERE o.email = 'ayse.kaya@demo.com'
AND NOT EXISTS (SELECT 1 FROM animal WHERE name = 'Maviş' AND owner_id = o.owner_id);


-- 3. Create Appointments
-- Temporarily drop the constraint to allow past appointments
ALTER TABLE appointment DROP CONSTRAINT IF EXISTS chk_appointment_date_future;
-- Past Appointment for Boncuk (Completed)
INSERT INTO appointment (clinic_id, animal_id, veterinarian_id, date_time, status, subject, notes, created_at, created_by)
SELECT 
    a.clinic_id,
    a.animal_id,
    (SELECT staff_id FROM staff WHERE email = 'ugurelsevket@gmail.com' LIMIT 1),
    CURRENT_TIMESTAMP - INTERVAL '7 days',
    'COMPLETED',
    'Yillik Kontrol',
    'Genel durum iyi. Asi takvimi guncellendi.',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM appointment WHERE animal_id = a.animal_id AND date_time = CURRENT_TIMESTAMP - INTERVAL '7 days');

-- Past Appointment for Rex (Completed)
INSERT INTO appointment (clinic_id, animal_id, veterinarian_id, date_time, status, subject, notes, created_at, created_by)
SELECT 
    a.clinic_id,
    a.animal_id,
    (SELECT staff_id FROM staff WHERE email = 'ugurelsevket@gmail.com' LIMIT 1),
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    'COMPLETED',
    'Kuduz Asisi',
    'Asi yapildi without complications.',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM appointment WHERE animal_id = a.animal_id AND date_time = CURRENT_TIMESTAMP - INTERVAL '2 days');

-- Future Appointment for Maviş (Scheduled)
INSERT INTO appointment (clinic_id, animal_id, veterinarian_id, date_time, status, subject, notes, created_at, created_by)
SELECT 
    a.clinic_id,
    a.animal_id,
    (SELECT staff_id FROM staff WHERE email = 'ugurelsevket@gmail.com' LIMIT 1),
    CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '10 hours', -- 2 days later at 10:00 (approx)
    'SCHEDULED',
    'Gaga Bakimi',
    'Tirnak ve gaga kontrolu yapilacak.',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Maviş' AND o.email = 'ayse.kaya@demo.com'
AND NOT EXISTS (SELECT 1 FROM appointment WHERE animal_id = a.animal_id AND status = 'SCHEDULED');

-- Re-enable the constraint (without validating existing rows)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_appointment_date_future') THEN
        ALTER TABLE appointment ADD CONSTRAINT chk_appointment_date_future CHECK (date_time >= CURRENT_TIMESTAMP - INTERVAL '1 day') NOT VALID;
    END IF;
END $$;


-- 4. Create Surgeries
-- Old surgery for Rex
INSERT INTO surgery (clinic_id, animal_id, veterinarian_id, date, status, notes, created_at, created_by)
SELECT 
    a.clinic_id,
    a.animal_id,
    (SELECT staff_id FROM staff WHERE email = 'ugurelsevket@gmail.com' LIMIT 1),
    CURRENT_DATE - INTERVAL '6 months',
    'COMPLETED',
    'Kirik Tedavisi: Sol arka bacak kirigi platin ile sabitlendi.',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM surgery WHERE animal_id = a.animal_id AND notes LIKE 'Kirik Tedavisi%');


-- 5. Create Hospitalization
-- Past hospitalization for Boncuk
INSERT INTO hospitalization (clinic_id, animal_id, admission_date, discharge_date, status, diagnosis_summary, care_plan, created_at, created_by)
SELECT 
    a.clinic_id,
    a.animal_id,
    CURRENT_TIMESTAMP - INTERVAL '1 month' - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '1 month',
    'DISCHARGED',
    'Siddetli Kusma',
    'Serum ve gozlem altinda tutuldu.',
    CURRENT_TIMESTAMP, 'SYSTEM_DEMO'
FROM animal a JOIN owner o ON a.owner_id = o.owner_id WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM hospitalization WHERE animal_id = a.animal_id AND status = 'DISCHARGED');
