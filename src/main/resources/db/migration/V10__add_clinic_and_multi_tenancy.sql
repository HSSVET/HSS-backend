-- Create Clinic table
CREATE TABLE IF NOT EXISTS clinic (
    clinic_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    license_number VARCHAR(50)
);

-- Add clinic_id to staff
ALTER TABLE staff ADD COLUMN IF NOT EXISTS clinic_id BIGINT;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_staff_clinic') THEN
        ALTER TABLE staff ADD CONSTRAINT fk_staff_clinic FOREIGN KEY (clinic_id) REFERENCES clinic (clinic_id);
    END IF;
END $$;

-- Add clinic_id to owner
ALTER TABLE owner ADD COLUMN IF NOT EXISTS clinic_id BIGINT;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_owner_clinic') THEN
        ALTER TABLE owner ADD CONSTRAINT fk_owner_clinic FOREIGN KEY (clinic_id) REFERENCES clinic (clinic_id);
    END IF;
END $$;

-- Update user_account for multi-tenancy and Firebase
ALTER TABLE user_account ADD COLUMN IF NOT EXISTS owner_id BIGINT;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_account_owner') THEN
        ALTER TABLE user_account ADD CONSTRAINT fk_user_account_owner FOREIGN KEY (owner_id) REFERENCES owner (owner_id);
    END IF;
END $$;

ALTER TABLE user_account ADD COLUMN IF NOT EXISTS firebase_uid VARCHAR(128);
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_user_account_firebase_uid') THEN
        ALTER TABLE user_account ADD CONSTRAINT uk_user_account_firebase_uid UNIQUE (firebase_uid);
    END IF;
END $$;

ALTER TABLE user_account ALTER COLUMN staff_id DROP NOT NULL;
ALTER TABLE user_account ALTER COLUMN username DROP NOT NULL;
