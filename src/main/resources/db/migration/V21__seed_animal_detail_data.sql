-- V21: Comprehensive demo data for Animal Detail Page sections
-- This migration adds rich demo data for all animal detail tabs

-- ============================================================
-- 1. MEDICAL HISTORY (Hastalık Geçmişi)
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    boncuk_id INT;
    mavis_id INT;
BEGIN
    -- Get Rex (animal 127 most likely, but let's find by name)
    SELECT a.animal_id INTO rex_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    
    SELECT a.animal_id INTO boncuk_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    
    SELECT a.animal_id INTO mavis_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Maviş' AND o.email = 'ayse.kaya@demo.com' LIMIT 1;

    -- Rex medical history
    IF rex_id IS NOT NULL THEN
        INSERT INTO medical_history (animal_id, diagnosis, date, treatment, created_by)
        SELECT rex_id, 'Deri Enfeksiyonu - Bakteriyel Dermatit', '2023-03-15', 'Antibiyotik tedavisi (Amoksisilin 10 gün). Antifungal şampuan ile yıkama haftada 2 kez.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM medical_history WHERE animal_id = rex_id AND diagnosis LIKE '%Deri Enfeksiyonu%');
        
        INSERT INTO medical_history (animal_id, diagnosis, date, treatment, created_by)
        SELECT rex_id, 'Gastroenterit - Akut İshal', '2023-07-22', 'Diyet değişikliği, probiyotik takviyesi, IV sıvı tedavisi.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM medical_history WHERE animal_id = rex_id AND diagnosis LIKE '%Gastroenterit%');
        
        INSERT INTO medical_history (animal_id, diagnosis, date, treatment, created_by)
        SELECT rex_id, 'Kulak Enfeksiyonu - Otitis Externa', '2024-01-10', 'Kulak damlası (Otomax), günlük temizlik, 14 gün tedavi.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM medical_history WHERE animal_id = rex_id AND diagnosis LIKE '%Kulak Enfeksiyonu%');
    END IF;

    -- Boncuk medical history
    IF boncuk_id IS NOT NULL THEN
        INSERT INTO medical_history (animal_id, diagnosis, date, treatment, created_by)
        SELECT boncuk_id, 'Üst Solunum Yolu Enfeksiyonu', '2023-05-18', 'Antibiyotik tedavisi, buhar terapisi, C vitamini takviyesi.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM medical_history WHERE animal_id = boncuk_id AND diagnosis LIKE '%Solunum%');
        
        INSERT INTO medical_history (animal_id, diagnosis, date, treatment, created_by)
        SELECT boncuk_id, 'İdrar Yolu Enfeksiyonu', '2024-02-05', 'Antibiyotik tedavisi (Enrofloksasin), bol su içirme.', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM medical_history WHERE animal_id = boncuk_id AND diagnosis LIKE '%İdrar%');
    END IF;
END $$;

-- ============================================================
-- 2. CLINICAL EXAMINATIONS (Klinik Muayene)
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    boncuk_id INT;
BEGIN
    SELECT a.animal_id INTO rex_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    
    SELECT a.animal_id INTO boncuk_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF rex_id IS NOT NULL THEN
        INSERT INTO clinical_examination (animal_id, date, findings, veterinarian_name, created_by)
        SELECT rex_id, '2024-11-15', 
               'Genel Durum: İyi. Vücut Sıcaklığı: 38.5°C. Kalp Atış Hızı: 90/dk. Solunum Hızı: 22/dk. Mukoz Membranlar: Pembe, nemli. Lenf Düğümleri: Normal boyutta. Deri/Kürk: Parlak, döküm yok. Notlar: Rutin kontrol, herhangi bir anormallik saptanmadı.',
               'Dr. Ayşe Demir', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM clinical_examination WHERE animal_id = rex_id AND date = '2024-11-15');
        
        INSERT INTO clinical_examination (animal_id, date, findings, veterinarian_name, created_by)
        SELECT rex_id, '2024-06-20', 
               'Genel Durum: İyi. Vücut Sıcaklığı: 38.8°C. Kalp Atış Hızı: 95/dk. Sol arka bacakta hafif topallık gözlemlendi. Eklem muayenesinde hassasiyet var. Anti-enflamatuar ilaç önerildi.',
               'Dr. Mehmet Yılmaz', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM clinical_examination WHERE animal_id = rex_id AND date = '2024-06-20');
    END IF;

    IF boncuk_id IS NOT NULL THEN
        INSERT INTO clinical_examination (animal_id, date, findings, veterinarian_name, created_by)
        SELECT boncuk_id, '2024-12-01', 
               'Genel Durum: İyi. Vücut Sıcaklığı: 38.2°C. Kilo: 4.2kg (ideal aralıkta). Diş sağlığı iyi. Tüy dökümü mevsimsel sınırlar içinde.',
               'Dr. Ayşe Demir', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM clinical_examination WHERE animal_id = boncuk_id AND date = '2024-12-01');
    END IF;
END $$;

-- ============================================================
-- 3. RADIOLOGICAL IMAGING (Radyoloji Görüntüleme)
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    boncuk_id INT;
BEGIN
    SELECT a.animal_id INTO rex_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    
    SELECT a.animal_id INTO boncuk_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF rex_id IS NOT NULL THEN
        INSERT INTO radiological_imaging (animal_id, date, type, image_url, comment, created_by)
        SELECT rex_id, '2024-06-20', 'X-Ray', '/images/radiology/rex_xray_20240620.jpg', 
               'Sol arka bacak röntgeni. Eklem aralığı normal. Kırık veya çatlak bulgusu yok. Hafif yumuşak doku şişliği mevcut.',
               'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM radiological_imaging WHERE animal_id = rex_id AND date = '2024-06-20');
        
        INSERT INTO radiological_imaging (animal_id, date, type, image_url, comment, created_by)
        SELECT rex_id, '2023-08-15', 'Ultrasound', '/images/radiology/rex_ultrasound_20230815.jpg', 
               'Karın ultrasonografisi. Karaciğer, böbrekler ve dalak normal boyut ve ekoda. Mesane normal.',
               'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM radiological_imaging WHERE animal_id = rex_id AND date = '2023-08-15');
    END IF;

    IF boncuk_id IS NOT NULL THEN
        INSERT INTO radiological_imaging (animal_id, date, type, image_url, comment, created_by)
        SELECT boncuk_id, '2024-02-10', 'X-Ray', '/images/radiology/boncuk_xray_20240210.jpg', 
               'Göğüs röntgeni. Akciğerler temiz. Kalp boyutu normal sınırlarda.',
               'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM radiological_imaging WHERE animal_id = boncuk_id AND date = '2024-02-10');
    END IF;
END $$;

-- ============================================================
-- 4. PRESCRIPTIONS (Reçeteler)
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    boncuk_id INT;
    amoksisilin_id INT;
    meloksikam_id INT;
BEGIN
    SELECT a.animal_id INTO rex_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    
    SELECT a.animal_id INTO boncuk_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    
    SELECT medicine_id INTO amoksisilin_id FROM medicine WHERE medicine_name = 'Amoksisilin 250mg' LIMIT 1;
    SELECT medicine_id INTO meloksikam_id FROM medicine WHERE medicine_name = 'Meloksikam 1.5mg/ml' LIMIT 1;

    IF rex_id IS NOT NULL AND amoksisilin_id IS NOT NULL THEN
        INSERT INTO prescription (animal_id, medicine_id, date, medicines, dosage, instructions, duration_days, status, created_by)
        SELECT rex_id, amoksisilin_id, '2024-11-15', 'Amoksisilin 250mg Tablet', '1 tablet, günde 2 kez', 
               'Yemekle birlikte verin. Tedaviyi yarıda kesmeyin.', 10, 'COMPLETED', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM prescription WHERE animal_id = rex_id AND date = '2024-11-15');
    END IF;

    IF rex_id IS NOT NULL AND meloksikam_id IS NOT NULL THEN
        INSERT INTO prescription (animal_id, medicine_id, date, medicines, dosage, instructions, duration_days, status, created_by)
        SELECT rex_id, meloksikam_id, '2024-06-20', 'Meloksikam 1.5mg/ml Süspansiyon', '0.5ml, günde 1 kez', 
               'Ağrı ve şişlik için. Yemekten sonra verin.', 7, 'COMPLETED', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM prescription WHERE animal_id = rex_id AND date = '2024-06-20');
    END IF;

    IF boncuk_id IS NOT NULL AND amoksisilin_id IS NOT NULL THEN
        INSERT INTO prescription (animal_id, medicine_id, date, medicines, dosage, instructions, duration_days, status, created_by)
        SELECT boncuk_id, amoksisilin_id, '2024-12-15', 'Amoksisilin 250mg Tablet', '1/2 tablet, günde 2 kez', 
               'İdrar yolu enfeksiyonu tedavisi. Bol su içirmek önemli.', 7, 'ACTIVE', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM prescription WHERE animal_id = boncuk_id AND date = '2024-12-15');
    END IF;
END $$;

-- ============================================================
-- 5. PATHOLOGY FINDINGS (Patoloji Bulguları)
-- ============================================================
DO $$
DECLARE
    rex_id INT;
BEGIN
    SELECT a.animal_id INTO rex_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF rex_id IS NOT NULL THEN
        INSERT INTO pathology_findings (animal_id, report, date, pathologist_name, findings_summary, recommendations, created_by)
        SELECT rex_id, 
               'RAPOR NO: 2024-PAT-0542
TARİH: 15.11.2024
PATOLOJİ UZMAN HEKİM: Dr. Ahmet Yıldız

ÖRNEK BİLGİLERİ:
- Örnek Tipi: Deri Biyopsisi
- Alındığı Yer: Sırt bölgesi
- Örnek Numarası: S-2024-1542

MİKROSKOBİK BULGULAR:
1. DOKU BÜTÜNLÜĞÜ VE HİSTOLOJİK YAPI
   - Epidermis normal kalınlıkta
   - Dermiste hafif lenfositik infiltrasyon
   
2. HÜCRESEL DEĞİŞİKLİKLER
   - Malignite bulgusu yok
   - Keratinosit morfolojisi normal

TANI: Kronik dermatit, non-spesifik

ÖNERİLER: 
- Alerjen testleri önerilir
- Diyet değişikliği düşünülebilir',
               '2024-11-15', 'Dr. Ahmet Yıldız', 
               'Kronik dermatit, non-spesifik. Malignite bulgusu yok.',
               'Alerjen testleri önerilir. Diyet değişikliği düşünülebilir.',
               'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM pathology_findings WHERE animal_id = rex_id);
    END IF;
END $$;

-- ============================================================
-- 6. UPDATE ANIMAL ALLERGIES & CHRONIC CONDITIONS
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    boncuk_id INT;
BEGIN
    SELECT a.animal_id INTO rex_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;
    
    SELECT a.animal_id INTO boncuk_id FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Boncuk' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF rex_id IS NOT NULL THEN
        UPDATE animal SET 
            allergies = 'Tavuk proteini (Orta şiddette) - Kaşıntı, kızarıklık, deri tahrişi. Hipoalerjenik diyet önerildi.',
            chronic_diseases = 'Kronik Dermatit - Mevsimsel alevlenmeler görülür. Düzenli deri bakımı gerekli.',
            notes = 'Hasta sahibi düzenli kontrollere geliyor. İlaç uyumu iyi. 3 ayda bir kontrol önerilir.'
        WHERE animal_id = rex_id;
    END IF;

    IF boncuk_id IS NOT NULL THEN
        UPDATE animal SET 
            allergies = 'Bilinen alerji yok',
            chronic_diseases = 'Tekrarlayan idrar yolu enfeksiyonu - Yılda 2-3 kez görülür. Bol su içirmesi önemli.',
            notes = 'Uysal ve kolay muayene edilen bir hasta. Özel diyete ihtiyaç yok.'
        WHERE animal_id = boncuk_id;
    END IF;
END $$;

-- ============================================================
-- 7. SURGERIES (Ameliyatlar) - Add details to existing or create new
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    clinic_id_val INT;
BEGIN
    SELECT a.animal_id, a.clinic_id INTO rex_id, clinic_id_val FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF rex_id IS NOT NULL THEN
        -- Update existing or insert new surgery
        INSERT INTO surgery (clinic_id, animal_id, date, status, notes, pre_op_instructions, post_op_instructions, anesthesia_protocol, anesthesia_consent, created_by)
        SELECT clinic_id_val, rex_id, '2024-01-15 10:00:00', 'COMPLETED', 
               'Kısırlaştırma operasyonu başarıyla tamamlandı. Komplikasyon yok.',
               'Operasyondan 12 saat önce yiyecek verilmemeli. Su 6 saat öncesine kadar verilebilir.',
               'Elizabet yakalığı 10 gün takılacak. Yara temiz tutulacak. Aktivite kısıtlaması 2 hafta.',
               'Propofol indüksiyon, İzofluran idame. Toplam anestezi süresi: 45 dakika.',
               true, 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM surgery WHERE animal_id = rex_id AND status = 'COMPLETED');
    END IF;
END $$;

-- ============================================================
-- 8. HOSPITALIZATIONS (Yatış) - Update existing records
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    clinic_id_val INT;
    hosp_id INT;
BEGIN
    SELECT a.animal_id, a.clinic_id INTO rex_id, clinic_id_val FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF rex_id IS NOT NULL THEN
        -- Check for existing hospitalization
        SELECT hospitalization_id INTO hosp_id FROM hospitalization WHERE animal_id = rex_id LIMIT 1;
        
        IF hosp_id IS NOT NULL THEN
            UPDATE hospitalization SET 
                diagnosis_summary = 'Post-operatif izlem: Kısırlaştırma operasyonu sonrası 24 saatlik gözlem.',
                care_plan = '1. Vital bulgular 4 saatte bir kontrol
2. Ağrı yönetimi: Meloksikam 0.1mg/kg günde 1 kez
3. IV sıvı: Ringer Laktat 10ml/kg/saat
4. Yara kontrolü: Günde 2 kez',
                primary_veterinarian = 'Dr. Ayşe Demir'
            WHERE hospitalization_id = hosp_id;
        END IF;
    END IF;
END $$;

-- ============================================================
-- 9. DOCUMENTS (Notlar/Belgeler için ek kayıtlar)
-- ============================================================
DO $$
DECLARE
    rex_id INT;
    owner_id_val INT;
BEGIN
    SELECT a.animal_id, a.owner_id INTO rex_id, owner_id_val FROM animal a 
    JOIN owner o ON a.owner_id = o.owner_id 
    WHERE a.name = 'Rex' AND o.email = 'mehmet.demir@demo.com' LIMIT 1;

    IF rex_id IS NOT NULL THEN
        -- Clinical notes document
        INSERT INTO document (owner_id, animal_id, title, content, document_type, date, created_by)
        SELECT owner_id_val, rex_id, 'Klinik Not - 15.08.2023', 
               'Hasta sahibi düzenli ilaç kullanımı konusunda tekrar bilgilendirildi. Antibiyotik tedavisinin tamamlanmasının önemini vurguladık. Hasta sahibi anlayış gösterdi ve geri kalan tedaviyi tamamlayacağını belirtti.',
               'GENERAL', '2023-08-15', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM document WHERE animal_id = rex_id AND title LIKE '%15.08.2023%');
        
        INSERT INTO document (owner_id, animal_id, title, content, document_type, date, created_by)
        SELECT owner_id_val, rex_id, 'Klinik Not - 25.09.2023', 
               'Yaşına göre iyi durumda. Kilo takibi önerildi. Mevcut diyetine devam etmesi gerektiği konusunda uyarıldı. Aylık tartım ve 3 ayda bir kontrol önerildi. Diyet programında herhangi bir değişiklik gerekmemektedir.',
               'GENERAL', '2023-09-25', 'SYSTEM_DEMO'
        WHERE NOT EXISTS (SELECT 1 FROM document WHERE animal_id = rex_id AND title LIKE '%25.09.2023%');
    END IF;
END $$;
