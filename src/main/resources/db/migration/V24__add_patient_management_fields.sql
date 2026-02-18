-- Add new fields to animal table
ALTER TABLE animal ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE';
ALTER TABLE animal ADD COLUMN IF NOT EXISTS behavior_notes TEXT;
ALTER TABLE animal ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(255);

-- Create animal_weight_history table
CREATE TABLE IF NOT EXISTS animal_weight_history (
    weight_history_id BIGSERIAL PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    measured_at DATE NOT NULL,
    note VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_animal_weight_history_animal FOREIGN KEY (animal_id) REFERENCES animal (animal_id)
);

-- Index for performance
CREATE INDEX IF NOT EXISTS idx_animal_weight_history_animal_id ON animal_weight_history(animal_id);
