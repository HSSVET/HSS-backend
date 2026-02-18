-- 1. Create the Clinic
INSERT INTO clinic (name, address, phone, email, license_number, slug, created_at, created_by)
SELECT 'Sevket Ugurel Klinigi', 'Demo Adres, Istanbul', '+90 555 000 00 00', 'info@sevketugurel.com', 'LIC-2025-DEMO', 'sevket-ugurel-klinigi', CURRENT_TIMESTAMP, 'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM clinic WHERE name = 'Sevket Ugurel Klinigi');

-- 2. Create the Veterinarian (Sevket Ugurel)
INSERT INTO staff (
    clinic_id, full_name, email, phone, hire_date, position, department, active, created_at, created_by
)
SELECT 
    (SELECT clinic_id FROM clinic WHERE name = 'Sevket Ugurel Klinigi' LIMIT 1),
    'Sevket Ugurel',
    'ugurelsevket@gmail.com',
    '+90 532 000 00 00',
    CURRENT_DATE,
    'Bas Hekim',
    'Yonetim',
    true,
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE email = 'ugurelsevket@gmail.com');

-- 3. Assign Roles to Sevket Ugurel (Admin & Veterinarian)
INSERT INTO staff_role (staff_id, role_id, assigned_date, assigned_by)
SELECT 
    s.staff_id,
    r.role_id,
    CURRENT_DATE,
    'SYSTEM'
FROM staff s, role r
WHERE s.email = 'ugurelsevket@gmail.com' 
AND r.name IN ('ADMIN', 'VETERINARIAN')
AND NOT EXISTS (SELECT 1 FROM staff_role sr WHERE sr.staff_id = s.staff_id AND sr.role_id = r.role_id);

-- 4. Create a Demo Owner
INSERT INTO owner (
    clinic_id, first_name, last_name, email, phone, address, created_at, created_by
)
SELECT 
    (SELECT clinic_id FROM clinic WHERE name = 'Sevket Ugurel Klinigi' LIMIT 1),
    'Ahmet',
    'Yilmaz',
    'ahmet.yilmaz@demo.com',
    '+90 533 111 22 33',
    'Kadikoy, Istanbul',
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM owner WHERE email = 'ahmet.yilmaz@demo.com');

-- 5. Create Demo Animals
-- Pamuk (Kedi)
INSERT INTO species (name) 
SELECT 'Kedi' 
WHERE NOT EXISTS (SELECT 1 FROM species WHERE name = 'Kedi');

INSERT INTO breed (species_id, name) 
SELECT s.species_id, 'Tekir' FROM species s WHERE s.name = 'Kedi'
AND NOT EXISTS (SELECT 1 FROM breed b WHERE b.name = 'Tekir' AND b.species_id = s.species_id);

INSERT INTO animal (
    owner_id, name, species_id, breed_id, gender, birth_date, weight, color, microchip_no, created_at, created_by
)
SELECT 
    (SELECT owner_id FROM owner WHERE email = 'ahmet.yilmaz@demo.com' LIMIT 1),
    'Pamuk',
    (SELECT species_id FROM species WHERE name = 'Kedi' LIMIT 1),
    (SELECT breed_id FROM breed WHERE name = 'Tekir' LIMIT 1),
    'FEMALE',
    CURRENT_DATE - INTERVAL '2 years',
    3.20,
    'Gri',
    '999000111222333',
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE name = 'Pamuk' AND owner_id = (SELECT owner_id FROM owner WHERE email = 'ahmet.yilmaz@demo.com'));

-- Karabas (Kopek)
INSERT INTO species (name) 
SELECT 'Kopek'
WHERE NOT EXISTS (SELECT 1 FROM species WHERE name = 'Kopek');

INSERT INTO breed (species_id, name) 
SELECT s.species_id, 'Golden Retriever' FROM species s WHERE s.name = 'Kopek'
AND NOT EXISTS (SELECT 1 FROM breed b WHERE b.name = 'Golden Retriever' AND b.species_id = s.species_id);

INSERT INTO animal (
    owner_id, name, species_id, breed_id, gender, birth_date, weight, color, microchip_no, created_at, created_by
)
SELECT 
    (SELECT owner_id FROM owner WHERE email = 'ahmet.yilmaz@demo.com' LIMIT 1),
    'Karabas',
    (SELECT species_id FROM species WHERE name = 'Kopek' LIMIT 1),
    (SELECT breed_id FROM breed WHERE name = 'Golden Retriever' LIMIT 1),
    'MALE',
    CURRENT_DATE - INTERVAL '5 years',
    28.50,
    'Sari',
    '999000444555666',
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE name = 'Karabas' AND owner_id = (SELECT owner_id FROM owner WHERE email = 'ahmet.yilmaz@demo.com'));

-- 6. Add some Surgeries and Hospitalizations for 'Pamuk'
-- Surgery for Pamuk
INSERT INTO surgery (
    animal_id, veterinarian_id, date, status, notes, created_at, created_by
)
SELECT 
    (SELECT animal_id FROM animal WHERE name = 'Pamuk' LIMIT 1),
    (SELECT staff_id FROM staff WHERE email = 'ugurelsevket@gmail.com' LIMIT 1),
    CURRENT_TIMESTAMP - INTERVAL '1 month',
    'COMPLETED',
    'Kisirlastirma operasyonu basariyla tamamlandi.',
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM surgery WHERE animal_id = (SELECT animal_id FROM animal WHERE name = 'Pamuk'));

-- Active Hospitalization for Karabas
-- Active Hospitalization for Karabas
INSERT INTO hospitalization (
    animal_id, admission_date, status, diagnosis_summary, care_plan, created_at, created_by
)
SELECT
    (SELECT animal_id FROM animal WHERE name = 'Karabas' LIMIT 1),
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    'ACTIVE',
    'Gastroenterit',
    'Serum tedavisi ve diyet mama.',
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM hospitalization WHERE animal_id = (SELECT animal_id FROM animal WHERE name = 'Karabas' LIMIT 1) AND status = 'ACTIVE');
