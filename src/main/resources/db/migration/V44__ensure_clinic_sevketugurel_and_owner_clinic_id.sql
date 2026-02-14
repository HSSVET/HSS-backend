-- V44: Ensure clinic with slug 'sevketugurel' exists (for /clinic/sevketugurel URLs) and owner.clinic_id is NOT NULL
-- Fixes 500 on GET/POST /api/owners when X-Clinic-Slug: sevketugurel is sent

-- 1. Insert clinic with slug 'sevketugurel' if it does not exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM clinic WHERE slug = 'sevketugurel') THEN
        INSERT INTO clinic (name, address, phone, email, slug, created_at, updated_at)
        VALUES ('Sevket Ugurel Kliniği', 'Adres girilecek', NULL, NULL, 'sevketugurel', NOW(), NOW());
        RAISE NOTICE 'Inserted clinic with slug sevketugurel';
    END IF;
END $$;

-- 2. Backfill owner.clinic_id for any owners that have NULL (assign to first clinic)
DO $$
DECLARE
    v_default_clinic_id BIGINT;
BEGIN
    SELECT clinic_id INTO v_default_clinic_id FROM clinic ORDER BY clinic_id LIMIT 1;
    IF v_default_clinic_id IS NOT NULL THEN
        UPDATE owner SET clinic_id = v_default_clinic_id, updated_at = COALESCE(updated_at, NOW()) WHERE clinic_id IS NULL;
    END IF;
END $$;

-- 3. Make owner.clinic_id NOT NULL so it matches the entity (only if no nulls remain)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM owner WHERE clinic_id IS NULL) THEN
        ALTER TABLE owner ALTER COLUMN clinic_id SET NOT NULL;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'owner.clinic_id NOT NULL skipped: %', SQLERRM;
END $$;
