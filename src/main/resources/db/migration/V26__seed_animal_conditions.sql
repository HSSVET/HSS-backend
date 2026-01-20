-- Add conditions for Boncuk (Cat)
INSERT INTO animal_conditions (animal_id, type, name, severity, diagnosis_date, status, notes, created_by)
SELECT 
    a.animal_id,
    'ALLERGY',
    'Tavuk Proteini',
    'MODERATE',
    CURRENT_DATE - INTERVAL '1 year',
    'ACTIVE',
    'Tavuk içeren mamalarda kusma ve kaşıntı yapıyor.',
    'SYSTEM_DEMO'
FROM animal a 
JOIN owner o ON a.owner_id = o.owner_id 
WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM animal_conditions ac WHERE ac.animal_id = a.animal_id AND ac.name = 'Tavuk Proteini');

INSERT INTO animal_conditions (animal_id, type, name, severity, diagnosis_date, status, notes, created_by)
SELECT 
    a.animal_id,
    'CHRONIC_CONDITION',
    'Böbrek Yetmezliği (Evre 1)',
    'MILD',
    CURRENT_DATE - INTERVAL '6 months',
    'MANAGED',
    'Düzenli kan tahlili ve renal mama kullanımı gerekli.',
    'SYSTEM_DEMO'
FROM animal a 
JOIN owner o ON a.owner_id = o.owner_id 
WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM animal_conditions ac WHERE ac.animal_id = a.animal_id AND ac.name = 'Böbrek Yetmezliği (Evre 1)');

-- Update Boncuk details
UPDATE animal a 
SET height = 25.0, sterilized = true 
FROM owner o 
WHERE a.owner_id = o.owner_id AND a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com';


-- Add conditions for Rex (Dog)
INSERT INTO animal_conditions (animal_id, type, name, severity, diagnosis_date, status, notes, created_by)
SELECT 
    a.animal_id,
    'ALLERGY',
    'Pire Alerjisi',
    'SEVERE',
    CURRENT_DATE - INTERVAL '2 years',
    'MANAGED',
    'Düzenli dış parazit uygulaması şart.',
    'SYSTEM_DEMO'
FROM animal a 
JOIN owner o ON a.owner_id = o.owner_id 
WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM animal_conditions ac WHERE ac.animal_id = a.animal_id AND ac.name = 'Pire Alerjisi');

INSERT INTO animal_conditions (animal_id, type, name, severity, diagnosis_date, status, notes, created_by)
SELECT 
    a.animal_id,
    'CHRONIC_CONDITION',
    'Kalça Displazisi',
    'MODERATE',
    CURRENT_DATE - INTERVAL '1 year',
    'ACTIVE',
    'Aşırı egzersizden kaçınılmalı, eklem destekleyici kullanılmalı.',
    'SYSTEM_DEMO'
FROM animal a 
JOIN owner o ON a.owner_id = o.owner_id 
WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com'
AND NOT EXISTS (SELECT 1 FROM animal_conditions ac WHERE ac.animal_id = a.animal_id AND ac.name = 'Kalça Displazisi');

-- Update Rex details
UPDATE animal a 
SET height = 60.0, sterilized = true 
FROM owner o 
WHERE a.owner_id = o.owner_id AND a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com';


-- Update Maviş (Bird) details - No conditions, just details
UPDATE animal a 
SET height = 15.0, sterilized = false 
FROM owner o 
WHERE a.owner_id = o.owner_id AND a.name = 'Maviş' AND o.email = 'ayse.kaya@demo.com';
