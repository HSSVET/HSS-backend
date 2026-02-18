-- V53: Seed comprehensive patient management demo data
-- Adds 4 owners and 4 animals with full medical history

DO $$
DECLARE
    v_clinic_id BIGINT;
    v_owner_1_id BIGINT;
    v_owner_2_id BIGINT;
    v_owner_3_id BIGINT;
    v_owner_4_id BIGINT;
    v_species_dog_id BIGINT;
    v_species_cat_id BIGINT;
    v_breed_golden_id BIGINT;
    v_breed_tekir_id BIGINT;
    v_breed_persian_id BIGINT;
    v_breed_labrador_id BIGINT;
    v_animal_1_id BIGINT;
    v_animal_2_id BIGINT;
    v_animal_3_id BIGINT;
    v_animal_4_id BIGINT;
    v_test_1_id BIGINT;
    v_test_2_id BIGINT;
    v_test_3_id BIGINT;
BEGIN
    -- Get or create sevketugurel clinic
    SELECT clinic_id INTO v_clinic_id FROM clinic WHERE slug = 'sevketugurel' LIMIT 1;
    
    IF v_clinic_id IS NULL THEN
        RAISE NOTICE 'Clinic sevketugurel not found, skipping V53';
        RETURN;
    END IF;

    -- Get or create species
    SELECT species_id INTO v_species_dog_id FROM species WHERE name = 'Köpek' OR name = 'Dog' LIMIT 1;
    IF v_species_dog_id IS NULL THEN
        INSERT INTO species (name) VALUES ('Köpek') RETURNING species_id INTO v_species_dog_id;
    END IF;

    SELECT species_id INTO v_species_cat_id FROM species WHERE name = 'Kedi' OR name = 'Cat' LIMIT 1;
    IF v_species_cat_id IS NULL THEN
        INSERT INTO species (name) VALUES ('Kedi') RETURNING species_id INTO v_species_cat_id;
    END IF;

    -- Get or create breeds
    SELECT breed_id INTO v_breed_golden_id FROM breed WHERE name = 'Golden Retriever' AND species_id = v_species_dog_id LIMIT 1;
    IF v_breed_golden_id IS NULL THEN
        INSERT INTO breed (species_id, name) VALUES (v_species_dog_id, 'Golden Retriever') RETURNING breed_id INTO v_breed_golden_id;
    END IF;

    SELECT breed_id INTO v_breed_tekir_id FROM breed WHERE name = 'Tekir' AND species_id = v_species_cat_id LIMIT 1;
    IF v_breed_tekir_id IS NULL THEN
        INSERT INTO breed (species_id, name) VALUES (v_species_cat_id, 'Tekir') RETURNING breed_id INTO v_breed_tekir_id;
    END IF;

    SELECT breed_id INTO v_breed_persian_id FROM breed WHERE name = 'Persian' AND species_id = v_species_cat_id LIMIT 1;
    IF v_breed_persian_id IS NULL THEN
        INSERT INTO breed (species_id, name) VALUES (v_species_cat_id, 'Persian') RETURNING breed_id INTO v_breed_persian_id;
    END IF;

    SELECT breed_id INTO v_breed_labrador_id FROM breed WHERE name = 'Labrador' AND species_id = v_species_dog_id LIMIT 1;
    IF v_breed_labrador_id IS NULL THEN
        INSERT INTO breed (species_id, name) VALUES (v_species_dog_id, 'Labrador') RETURNING breed_id INTO v_breed_labrador_id;
    END IF;

    -- Create 4 owners
    INSERT INTO owner (clinic_id, first_name, last_name, email, phone, address, created_at)
    VALUES (v_clinic_id, 'Zeynep', 'Yıldız', 'zeynep.yildiz@example.com', '555-1001', 
            'Bağdat Cad. No:45 Kadıköy/İstanbul', CURRENT_TIMESTAMP)
    RETURNING owner_id INTO v_owner_1_id;

    INSERT INTO owner (clinic_id, first_name, last_name, email, phone, address, created_at)
    VALUES (v_clinic_id, 'Can', 'Demir', 'can.demir@example.com', '555-1002', 
            'Bahariye Cad. No:78 Kadıköy/İstanbul', CURRENT_TIMESTAMP)
    RETURNING owner_id INTO v_owner_2_id;

    INSERT INTO owner (clinic_id, first_name, last_name, email, phone, address, created_at)
    VALUES (v_clinic_id, 'Elif', 'Kaya', 'elif.kaya@example.com', '555-1003', 
            'Moda Cad. No:12 Kadıköy/İstanbul', CURRENT_TIMESTAMP)
    RETURNING owner_id INTO v_owner_3_id;

    INSERT INTO owner (clinic_id, first_name, last_name, email, phone, address, created_at)
    VALUES (v_clinic_id, 'Mehmet', 'Şahin', 'mehmet.sahin@example.com', '555-1004', 
            'Fenerbahçe Mah. No:89 Kadıköy/İstanbul', CURRENT_TIMESTAMP)
    RETURNING owner_id INTO v_owner_4_id;

    -- ===== ANIMAL 1: Sarı (Golden Retriever, ACTIVE) =====
    INSERT INTO animal (clinic_id, owner_id, name, species_id, breed_id, gender, birth_date, 
                        weight, height, sterilized, color, microchip_no, status, created_at)
    VALUES (v_clinic_id, v_owner_1_id, 'Sarı', v_species_dog_id, v_breed_golden_id, 'MALE',
            CURRENT_DATE - INTERVAL '3 years', 28.5, 55.0, true, 'Altın Sarısı', 
            '900123456789001', 'ACTIVE', CURRENT_TIMESTAMP)
    RETURNING animal_id INTO v_animal_1_id;

    -- Vaccinations for Animal 1
    INSERT INTO vaccination_record (animal_id, vaccine_name, date, next_due_date, 
                                   batch_number, veterinarian_name, notes, created_at)
    VALUES 
        (v_animal_1_id, 'Kuduz Aşısı', CURRENT_DATE - INTERVAL '6 months', 
         CURRENT_DATE + INTERVAL '6 months', 'KB-2024-001', 'Dr. Ayşe Veteriner', 
         'Yıllık rutin aşı', CURRENT_TIMESTAMP),
        (v_animal_1_id, 'Karma Aşı (DHPPi)', CURRENT_DATE - INTERVAL '1 year', 
         CURRENT_DATE, 'DH-2023-456', 'Dr. Mehmet Veteriner', 
         'Yıllık karma aşı tamamlandı', CURRENT_TIMESTAMP),
        (v_animal_1_id, 'Leptospirosis', CURRENT_DATE - INTERVAL '8 months', 
         CURRENT_DATE + INTERVAL '4 months', 'LP-2024-078', 'Dr. Ayşe Veteriner', 
         'Su ile temas riski nedeniyle önerildi', CURRENT_TIMESTAMP);

    -- Clinical examinations for Animal 1
    INSERT INTO clinical_examination (animal_id, date, veterinarian_name, findings, created_at)
    VALUES 
        (v_animal_1_id, CURRENT_DATE - INTERVAL '2 months', 'Dr. Ayşe Veteriner',
         'Vücut sıcaklığı: 38.5°C, Nabız: 80/dk, Solunum: 24/dk, Ağırlık: 28.5kg. Genel durum iyi, mukozalar pembe, hidrasyon normal. Tanı: Sağlıklı. Tedavi Planı: Rutin kontrol, aşı takvimi güncellendi', 
         CURRENT_TIMESTAMP),
        (v_animal_1_id, CURRENT_DATE - INTERVAL '8 months', 'Dr. Mehmet Veteriner',
         'Vücut sıcaklığı: 38.8°C, Nabız: 85/dk, Ağırlık: 27.8kg. Hafif diş taşı gözlemlendi. Tanı: Diş temizliği önerildi. Tedavi Planı: Diş hijyeni için özel mama ve kemik önerildi', 
         CURRENT_TIMESTAMP);

    -- Weight history for Animal 1
    INSERT INTO animal_weight_history (animal_id, measured_at, weight, note, created_at)
    VALUES 
        (v_animal_1_id, CURRENT_DATE, 28.5, 'İdeal kiloda', CURRENT_TIMESTAMP),
        (v_animal_1_id, CURRENT_DATE - INTERVAL '3 months', 28.2, 'Hafif kilo artışı', CURRENT_TIMESTAMP),
        (v_animal_1_id, CURRENT_DATE - INTERVAL '6 months', 27.5, 'Normal gelişim', CURRENT_TIMESTAMP),
        (v_animal_1_id, CURRENT_DATE - INTERVAL '1 year', 26.8, 'Büyüme devam ediyor', CURRENT_TIMESTAMP);

    -- Medical history for Animal 1
    INSERT INTO medical_history (animal_id, diagnosis, date, treatment, created_at)
    VALUES 
        (v_animal_1_id, 'Gastroenterit', CURRENT_DATE - INTERVAL '1 year 3 months',
         'Kusma, ishal, iştahsızlık belirtileri. IV sıvı desteği, probiyotik, diyet değişikliği uygulandı. 3 gün içinde tamamen iyileşti.', 
         CURRENT_TIMESTAMP);

    -- Behavior notes for Animal 1
    INSERT INTO behavior_note (animal_id, clinic_id, category, title, description, severity, 
                               observed_date, observed_by, recommendations, created_at)
    VALUES 
        (v_animal_1_id, v_clinic_id, 'SOCIAL', 'Sosyal Davranış',
         'Sosyal ve arkadaş canlısı davranış sergiliyor. Diğer köpeklerle iyi geçiniyor.', 
         'LOW', CURRENT_DATE - INTERVAL '1 month', 'Sahibi: Zeynep Yıldız',
         'Sosyalizasyon egzersizlerine devam edilmesi önerildi', CURRENT_TIMESTAMP);

    -- Radiological imaging for Animal 1
    INSERT INTO radiological_imaging (animal_id, date, type, comment, created_at)
    VALUES 
        (v_animal_1_id, CURRENT_DATE - INTERVAL '1 year', 'Röntgen',
         'Kalça Eklemi: Her iki kalça ekleminde hafif dejeneratif değişiklikler. Hafif kalça displazisi (Grade 1). Yaşa bağlı normal bulgular. Ağırlık kontrolü ve eklem desteği önerildi.',
         CURRENT_TIMESTAMP);

    -- ===== ANIMAL 2: Minnoş (Tekir, FOLLOW_UP - CKD) =====
    INSERT INTO animal (clinic_id, owner_id, name, species_id, breed_id, gender, birth_date, 
                        weight, height, sterilized, color, microchip_no, status, 
                        allergies, chronic_diseases, notes, created_at)
    VALUES (v_clinic_id, v_owner_2_id, 'Minnoş', v_species_cat_id, v_breed_tekir_id, 'FEMALE',
            CURRENT_DATE - INTERVAL '7 years', 4.2, 23.0, true, 'Tekir Desenli', 
            '900123456789002', 'FOLLOW_UP', 'Tavuk proteini', 'Kronik Böbrek Yetmezliği (CKD Stage 2)', 
            'Özel diyetle besleniyor. 3 ayda bir kontrol gereklidir.', CURRENT_TIMESTAMP)
    RETURNING animal_id INTO v_animal_2_id;

    -- Vaccinations for Animal 2
    INSERT INTO vaccination_record (animal_id, vaccine_name, date, next_due_date, 
                                   batch_number, veterinarian_name, notes, created_at)
    VALUES 
        (v_animal_2_id, 'Kedi Karma Aşısı (FVRCP)', CURRENT_DATE - INTERVAL '10 months', 
         CURRENT_DATE + INTERVAL '2 months', 'FC-2024-123', 'Dr. Ayşe Veteriner', 
         'Kronik hastalık nedeniyle aşı protokolü özelleştirildi', CURRENT_TIMESTAMP),
        (v_animal_2_id, 'Kuduz Aşısı', CURRENT_DATE - INTERVAL '1 year 2 months', 
         CURRENT_DATE - INTERVAL '2 months', 'KB-2023-789', 'Dr. Mehmet Veteriner', 
         'Yenilenmesi gerekiyor', CURRENT_TIMESTAMP);

    -- Clinical examinations for Animal 2
    INSERT INTO clinical_examination (animal_id, date, veterinarian_name, findings, created_at)
    VALUES 
        (v_animal_2_id, CURRENT_DATE - INTERVAL '1 month', 'Dr. Ayşe Veteriner',
         'Sıcaklık: 38.3°C, Nabız: 160/dk, Solunum: 32/dk, Ağırlık: 4.2kg, Vücut Skoru: Zayıf. BUN ve kreatinin seviyeleri yüksek, dehidrasyon bulguları hafif. Tanı: CKD Stage 2 - stabil. Tedavi Planı: Renal diyet devam, günlük SC sıvı desteği, her 3 ayda bir kan tahlili', 
         CURRENT_TIMESTAMP),
        (v_animal_2_id, CURRENT_DATE - INTERVAL '4 months', 'Dr. Ayşe Veteriner',
         'Sıcaklık: 38.6°C, Nabız: 155/dk, Ağırlık: 4.5kg. Böbrek değerleri hafif yükselme gösterdi. Tanı: CKD Stage 2 tanısı konuldu. Tedavi Planı: Renal diyete geçiş, sıvı desteği başlandı', 
         CURRENT_TIMESTAMP);

    -- Lab tests for Animal 2
    INSERT INTO lab_tests (animal_id, test_name, date, status, created_at)
    VALUES 
        (v_animal_2_id, 'Biyokimya Paneli', CURRENT_DATE - INTERVAL '1 month', 'COMPLETED', CURRENT_TIMESTAMP)
    RETURNING test_id INTO v_test_1_id;

    INSERT INTO lab_results (test_id, result, value, unit, normal_range, interpretation, created_at)
    VALUES 
        (v_test_1_id, 'BUN', '42', 'mg/dL', '15-35', 'Yüksek - CKD ile uyumlu', CURRENT_TIMESTAMP),
        (v_test_1_id, 'Kreatinin', '2.8', 'mg/dL', '0.8-1.8', 'Yüksek - CKD ile uyumlu', CURRENT_TIMESTAMP),
        (v_test_1_id, 'Fosfor', '6.2', 'mg/dL', '2.5-6.0', 'Hafif yüksek - izlenecek', CURRENT_TIMESTAMP);

    INSERT INTO lab_tests (animal_id, test_name, date, status, created_at)
    VALUES 
        (v_animal_2_id, 'İdrar Analizi', CURRENT_DATE - INTERVAL '1 month', 'COMPLETED', CURRENT_TIMESTAMP)
    RETURNING test_id INTO v_test_2_id;

    INSERT INTO lab_results (test_id, result, value, interpretation, created_at)
    VALUES 
        (v_test_2_id, 'Yoğunluk', '1.015', 'Düşük - konsantrasyon sorunu', CURRENT_TIMESTAMP),
        (v_test_2_id, 'Protein', '++', 'Hafif proteinüri - izlenecek', CURRENT_TIMESTAMP);

    -- Treatment for Animal 2
    INSERT INTO treatment (animal_id, clinic_id, treatment_type, title, diagnosis, start_date, 
                          status, veterinarian_name, notes, created_at)
    VALUES 
        (v_animal_2_id, v_clinic_id, 'MEDICATION', 'Kronik Böbrek Yetmezliği Tedavisi', 
         'CKD Stage 2', CURRENT_DATE - INTERVAL '4 months', 'ONGOING',
         'Dr. Ayşe Veteriner',
         'Renal diyetle birlikte günlük SC sıvı desteği. SC Ringer Laktat: 100ml/gün, Renaltec: 2x1 toz (yemle karıştırılacak). Sahibi evde günlük sıvı vermekte. 3 ayda bir kontrol planlandı.', 
         CURRENT_TIMESTAMP);

    -- Behavior notes for Animal 2
    INSERT INTO behavior_note (animal_id, clinic_id, category, title, description, severity, 
                               observed_date, observed_by, recommendations, created_at)
    VALUES 
        (v_animal_2_id, v_clinic_id, 'OTHER', 'Genel Durum İyi',
         'İştah iyi, oyuncu ve aktif. Kronik hastalığına rağmen yaşam kalitesi yüksek.', 
         'LOW', CURRENT_DATE - INTERVAL '2 weeks', 'Sahibi: Can Demir',
         'Mevcut tedaviye devam', CURRENT_TIMESTAMP);

    -- Weight history for Animal 2
    INSERT INTO animal_weight_history (animal_id, measured_at, weight, note, created_at)
    VALUES 
        (v_animal_2_id, CURRENT_DATE - INTERVAL '1 month', 4.2, 'CKD nedeniyle hafif zayıf', CURRENT_TIMESTAMP),
        (v_animal_2_id, CURRENT_DATE - INTERVAL '4 months', 4.5, 'CKD tanısı öncesi', CURRENT_TIMESTAMP);

    -- ===== ANIMAL 3: Pamuk (Persian, ACTIVE - Post Surgery) =====
    INSERT INTO animal (clinic_id, owner_id, name, species_id, breed_id, gender, birth_date, 
                        weight, height, sterilized, color, microchip_no, status, 
                        notes, created_at)
    VALUES (v_clinic_id, v_owner_3_id, 'Pamuk', v_species_cat_id, v_breed_persian_id, 'FEMALE',
            CURRENT_DATE - INTERVAL '2 years', 3.8, 22.0, true, 'Beyaz', 
            '900123456789003', 'ACTIVE', 
            'Yeni kısırlaştırıldı. İyileşme süreci sorunsuz.', CURRENT_TIMESTAMP)
    RETURNING animal_id INTO v_animal_3_id;

    -- Vaccinations for Animal 3
    INSERT INTO vaccination_record (animal_id, vaccine_name, date, next_due_date, 
                                   batch_number, veterinarian_name, notes, created_at)
    VALUES 
        (v_animal_3_id, 'Kedi Karma Aşısı (FVRCP)', CURRENT_DATE - INTERVAL '4 months', 
         CURRENT_DATE + INTERVAL '8 months', 'FC-2024-456', 'Dr. Ayşe Veteriner', 
         'Ameliyat öncesi aşı kontrolü yapıldı', CURRENT_TIMESTAMP);

    -- Clinical examinations for Animal 3
    INSERT INTO clinical_examination (animal_id, date, veterinarian_name, findings, created_at)
    VALUES 
        (v_animal_3_id, CURRENT_DATE - INTERVAL '1 week', 'Dr. Mehmet Veteriner',
         'Sıcaklık: 38.4°C, Nabız: 145/dk, Ağırlık: 3.8kg. Cerrahi yara iyileşmesi mükemmel, dikişler temiz. Tanı: Kısırlaştırma sonrası kontrol - normal. Plan: Dikişler 3 gün içinde alınacak', 
         CURRENT_TIMESTAMP),
        (v_animal_3_id, CURRENT_DATE - INTERVAL '2 weeks', 'Dr. Mehmet Veteriner',
         'Sıcaklık: 38.5°C, Nabız: 150/dk, Ağırlık: 3.9kg. Pre-operatif muayene - sağlıklı. Tanı: Ovariohysterectomy için uygun. Plan: Ameliyat sabah açlıkla yapılacak', 
         CURRENT_TIMESTAMP);

    -- Treatment for Animal 3
    INSERT INTO treatment (animal_id, clinic_id, treatment_type, title, diagnosis, start_date, 
                          end_date, status, veterinarian_name, notes, created_at)
    VALUES 
        (v_animal_3_id, v_clinic_id, 'SURGERY', 'Kısırlaştırma Ameliyatı ve Post-op Tedavi', 
         'Rutin Ovariohysterectomy', CURRENT_DATE - INTERVAL '2 weeks',
         CURRENT_DATE - INTERVAL '3 days', 'COMPLETED', 'Dr. Mehmet Veteriner',
         'Operasyon sorunsuz tamamlandı. Meloxicam (ağrı kesici), Cefazolin (antibiyotik). Meloxicam: 0.1mg/kg günde 1 kez (5 gün), Cefazolin: 20mg/kg günde 2 kez (7 gün). İyileşme süreci mükemmel. Dikişler 10. günde alındı.', 
         CURRENT_TIMESTAMP);

    -- Behavior notes for Animal 3
    INSERT INTO behavior_note (animal_id, clinic_id, category, title, description, severity, 
                               observed_date, observed_by, recommendations, created_at)
    VALUES 
        (v_animal_3_id, v_clinic_id, 'OTHER', 'Ameliyat Sonrası İyileşme',
         'Ameliyat sonrası uysal ve sakin. İştah iyi, hareket kısıtlamasına uyuluyor.', 
         'LOW', CURRENT_DATE - INTERVAL '1 week', 'Sahibi: Elif Kaya',
         'E-collar kullanımına devam, 3 gün sonra kontrol', CURRENT_TIMESTAMP);

    -- Weight history for Animal 3
    INSERT INTO animal_weight_history (animal_id, measured_at, weight, note, created_at)
    VALUES 
        (v_animal_3_id, CURRENT_DATE - INTERVAL '1 week', 3.8, 'Post-op kontrol - stabil', CURRENT_TIMESTAMP),
        (v_animal_3_id, CURRENT_DATE - INTERVAL '2 weeks', 3.9, 'Pre-op ağırlık', CURRENT_TIMESTAMP);

    -- ===== ANIMAL 4: Karabaş (Labrador, ACTIVE - Food Allergy) =====
    INSERT INTO animal (clinic_id, owner_id, name, species_id, breed_id, gender, birth_date, 
                        weight, height, sterilized, color, microchip_no, status, 
                        allergies, notes, created_at)
    VALUES (v_clinic_id, v_owner_4_id, 'Karabaş', v_species_dog_id, v_breed_labrador_id, 'MALE',
            CURRENT_DATE - INTERVAL '5 years', 32.0, 58.0, true, 'Siyah', 
            '900123456789004', 'ACTIVE', 'Sığır proteini', 
            'Gıda alerjisi nedeniyle özel diyetle besleniyor.', CURRENT_TIMESTAMP)
    RETURNING animal_id INTO v_animal_4_id;

    -- Vaccinations for Animal 4
    INSERT INTO vaccination_record (animal_id, vaccine_name, date, next_due_date, 
                                   batch_number, veterinarian_name, notes, created_at)
    VALUES 
        (v_animal_4_id, 'Kuduz Aşısı', CURRENT_DATE - INTERVAL '8 months', 
         CURRENT_DATE + INTERVAL '4 months', 'KB-2024-234', 'Dr. Ayşe Veteriner', 
         'Yıllık rutin', CURRENT_TIMESTAMP),
        (v_animal_4_id, 'Karma Aşı (DHPPi)', CURRENT_DATE - INTERVAL '8 months', 
         CURRENT_DATE + INTERVAL '4 months', 'DH-2024-567', 'Dr. Ayşe Veteriner', 
         'Aynı gün kuduz ile birlikte yapıldı', CURRENT_TIMESTAMP);

    -- Clinical examinations for Animal 4
    INSERT INTO clinical_examination (animal_id, date, veterinarian_name, findings, created_at)
    VALUES 
        (v_animal_4_id, CURRENT_DATE - INTERVAL '3 days', 'Dr. Ayşe Veteriner',
         'Sıcaklık: 38.7°C, Nabız: 88/dk, Ağırlık: 32.0kg. Kulak kızarıklığı ve kaşıntı mevcut. Dermatolojik inceleme yapıldı. Tanı: Otitis Externa (Dış Kulak İltihabı). Plan: Kulak temizliği, topikal antibiyotik damla, oral antihistaminik. Not: Gıda alerjisine bağlı sekonder enfeksiyon olabilir', 
         CURRENT_TIMESTAMP);

    -- Lab tests for Animal 4
    INSERT INTO lab_tests (animal_id, test_name, date, status, created_at)
    VALUES 
        (v_animal_4_id, 'Allerji Paneli', CURRENT_DATE - INTERVAL '6 months', 'COMPLETED', CURRENT_TIMESTAMP)
    RETURNING test_id INTO v_test_3_id;

    INSERT INTO lab_results (test_id, result, value, interpretation, created_at)
    VALUES 
        (v_test_3_id, 'Sığır Proteini', '++++', 'Şiddetli alerji - kesinlikle kaçınılmalı', CURRENT_TIMESTAMP),
        (v_test_3_id, 'Tavuk', '+', 'Hafif reaksiyon - sınırlı tüketim', CURRENT_TIMESTAMP),
        (v_test_3_id, 'Çevre Alerjenleri', '-', 'Negatif', CURRENT_TIMESTAMP);

    -- Treatment for Animal 4
    INSERT INTO treatment (animal_id, clinic_id, treatment_type, title, diagnosis, start_date, 
                          end_date, status, veterinarian_name, notes, created_at)
    VALUES 
        (v_animal_4_id, v_clinic_id, 'MEDICATION', 'Otitis Externa Tedavisi', 
         'Dış Kulak İltihabı (Gıda Alerjisi Sekonder)', 
         CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '10 days',
         'ONGOING', 'Dr. Ayşe Veteriner',
         'Otomax: Her kulağa günde 2 kez 5 damla (14 gün), Apoquel: 16mg günde 1 kez (14 gün). Tedaviye yanıt iyi. 1 hafta sonra kontrol planlandı.', 
         CURRENT_TIMESTAMP);

    -- Behavior notes for Animal 4
    INSERT INTO behavior_note (animal_id, clinic_id, category, title, description, severity, 
                               observed_date, observed_by, recommendations, created_at)
    VALUES 
        (v_animal_4_id, v_clinic_id, 'ANXIETY', 'Kulak Kaşıntısı Nedeniyle Huzursuzluk',
         'Kulak kaşıntısı nedeniyle huzursuz ve gece uykusu bölünüyor.', 
         'MEDIUM', CURRENT_DATE - INTERVAL '1 week', 'Sahibi: Mehmet Şahin',
         'Tedavi başlandı, e-collar önerildi', CURRENT_TIMESTAMP);

    -- Weight history for Animal 4
    INSERT INTO animal_weight_history (animal_id, measured_at, weight, note, created_at)
    VALUES 
        (v_animal_4_id, CURRENT_DATE - INTERVAL '3 days', 32.0, 'Stabil - ideal kiloda', CURRENT_TIMESTAMP),
        (v_animal_4_id, CURRENT_DATE - INTERVAL '6 months', 31.5, 'Diyet değişikliği sonrası', CURRENT_TIMESTAMP);

    RAISE NOTICE '=== V53 Migration completed successfully ===';
    RAISE NOTICE 'Created 4 owners and 4 animals with comprehensive medical records:';
    RAISE NOTICE '- Sarı (Golden Retriever): Healthy, Active';
    RAISE NOTICE '- Minnoş (Tekir): CKD Stage 2, Follow-up';
    RAISE NOTICE '- Pamuk (Persian): Recent surgery, Recovering';
    RAISE NOTICE '- Karabaş (Labrador): Food allergy, Active treatment';

END $$;
