-- V12: Seed test data for development

-- 1. Create a default Clinic if not exists
INSERT INTO clinic (name, address, phone, email, license_number, created_at, created_by)
SELECT 'Merkez Veteriner Kliniği', 'Atatürk Cad. No:1, İstanbul', '+90 212 123 45 67', 'info@merkezvet.com', 'LIC-2024-001', CURRENT_TIMESTAMP, 'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM clinic WHERE name = 'Merkez Veteriner Kliniği');

-- 2. Ensure Roles exist
INSERT INTO role (name, description, is_system_role, permissions) VALUES
('ADMIN', 'System Administrator', true, '{ALL_PERMISSIONS}'),
('VETERINARIAN', 'Veterinary Doctor', true, '{READ_ANIMALS,WRITE_ANIMALS,READ_APPOINTMENTS,WRITE_APPOINTMENTS,READ_MEDICAL_HISTORY,WRITE_MEDICAL_HISTORY}'),
('STAFF', 'Support Staff', true, '{READ_ANIMALS,READ_APPOINTMENTS,WRITE_APPOINTMENTS}'),
('OWNER', 'Pet Owner', true, '{READ_OWN_ANIMALS,READ_OWN_APPOINTMENTS}')
ON CONFLICT (name) DO NOTHING;

-- 3. Create a Test Staff Member (Veterinarian) linked to the Clinic
-- matches email: test@example.com
INSERT INTO staff (
    clinic_id, full_name, email, phone, hire_date, position, department, active, created_at, created_by
)
SELECT 
    (SELECT clinic_id FROM clinic WHERE name = 'Merkez Veteriner Kliniği'),
    'Test Veteriner',
    'test@example.com',
    '+90 555 111 22 33',
    CURRENT_DATE,
    'Sorumlu Veteriner',
    'Medikal',
    true,
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE email = 'test@example.com');

-- 4. Assign Admin and Veterinarian roles to the Test Staff
INSERT INTO staff_role (staff_id, role_id, assigned_date, assigned_by)
SELECT 
    s.staff_id,
    r.role_id,
    CURRENT_DATE,
    'SYSTEM'
FROM staff s, role r
WHERE s.email = 'test@example.com' 
AND r.name IN ('ADMIN', 'VETERINARIAN')
AND NOT EXISTS (
    SELECT 1 FROM staff_role sr 
    WHERE sr.staff_id = s.staff_id AND sr.role_id = r.role_id
);

-- 5. Create a Test Owner (Customer) linked to the Clinic
-- matches email: customer@example.com
INSERT INTO owner (
    clinic_id, first_name, last_name, email, phone, address, created_at, created_by
)
SELECT 
    (SELECT clinic_id FROM clinic WHERE name = 'Merkez Veteriner Kliniği'),
    'Test',
    'Müşteri',
    'customer@example.com',
    '+90 555 999 88 77',
    'Örnek Mah. Test Sok.',
    CURRENT_TIMESTAMP,
    'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM owner WHERE email = 'customer@example.com');
