-- Add new columns to animal table
ALTER TABLE animal
ADD COLUMN height DOUBLE PRECISION,
ADD COLUMN sterilized BOOLEAN;

-- Create table for Animal Conditions (Allergies and Chronic Diseases)
CREATE TABLE animal_conditions (
    id BIGSERIAL PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL, -- ALLERGY, CHRONIC_CONDITION
    name VARCHAR(255) NOT NULL,
    severity VARCHAR(50), -- MILD, MODERATE, SEVERE
    diagnosis_date DATE,
    diagnosed_by VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, MANAGED, RESOLVED
    notes TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    CONSTRAINT fk_animal_conditions_animal FOREIGN KEY (animal_id) REFERENCES animal (animal_id) ON DELETE CASCADE
);

CREATE INDEX idx_animal_conditions_animal_id ON animal_conditions(animal_id);
CREATE INDEX idx_animal_conditions_type ON animal_conditions(type);
