-- Fix missing clinic associations for owners and animals
-- This ensures all owners have a clinic_id and all animals have proper owner-clinic associations

DO $$
DECLARE
    v_default_clinic_id BIGINT;
BEGIN
    -- Get the first available clinic (or create one if none exists)
    SELECT clinic_id INTO v_default_clinic_id FROM clinic ORDER BY clinic_id LIMIT 1;
    
    IF v_default_clinic_id IS NULL THEN
        -- Create a default clinic if none exists
        INSERT INTO clinic (name, slug, phone, email, address, city, district, created_at)
        VALUES ('Default Clinic', 'default-clinic', '555-0000', 'info@defaultclinic.com', 
                'Default Address', 'Default City', 'Default District', CURRENT_TIMESTAMP)
        RETURNING clinic_id INTO v_default_clinic_id;
        
        RAISE NOTICE 'Created default clinic with ID: %', v_default_clinic_id;
    END IF;
    
    -- Update all owners that don't have a clinic_id
    UPDATE owner 
    SET clinic_id = v_default_clinic_id, 
        updated_at = CURRENT_TIMESTAMP
    WHERE clinic_id IS NULL;
    
    RAISE NOTICE 'Updated owners without clinic_id to use clinic: %', v_default_clinic_id;
    
    -- Update all animals that don't have a clinic_id (if the column exists)
    UPDATE animal 
    SET clinic_id = (SELECT clinic_id FROM owner WHERE owner.owner_id = animal.owner_id),
        updated_at = CURRENT_TIMESTAMP
    WHERE clinic_id IS NULL 
      AND owner_id IS NOT NULL;
    
    RAISE NOTICE 'Updated animals without clinic_id from their owner clinic';
    
END $$;
