-- V51: Add patient management enhancements - treatment and behavior_note tables

-- Create treatment table for tracking animal treatments
CREATE TABLE treatment (
    treatment_id SERIAL PRIMARY KEY,
    animal_id INTEGER NOT NULL REFERENCES animal(animal_id),
    clinic_id INTEGER NOT NULL REFERENCES clinic(clinic_id),
    treatment_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    diagnosis TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ONGOING',
    veterinarian_name VARCHAR(100),
    notes TEXT,
    cost DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Add indexes for treatment table
CREATE INDEX idx_treatment_animal ON treatment(animal_id);
CREATE INDEX idx_treatment_clinic ON treatment(clinic_id);
CREATE INDEX idx_treatment_status ON treatment(status);
CREATE INDEX idx_treatment_type ON treatment(treatment_type);
CREATE INDEX idx_treatment_start_date ON treatment(start_date);

-- Add check constraint for treatment_type
ALTER TABLE treatment ADD CONSTRAINT check_treatment_type 
    CHECK (treatment_type IN ('MEDICATION', 'SURGERY', 'THERAPY', 'PROCEDURE', 'OTHER'));

-- Add check constraint for treatment status
ALTER TABLE treatment ADD CONSTRAINT check_treatment_status 
    CHECK (status IN ('ONGOING', 'COMPLETED', 'CANCELLED'));

-- Create behavior_note table for tracking animal behavior observations
CREATE TABLE behavior_note (
    behavior_note_id SERIAL PRIMARY KEY,
    animal_id INTEGER NOT NULL REFERENCES animal(animal_id),
    clinic_id INTEGER NOT NULL REFERENCES clinic(clinic_id),
    category VARCHAR(50),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(20),
    observed_date DATE NOT NULL,
    observed_by VARCHAR(100),
    recommendations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Add indexes for behavior_note table
CREATE INDEX idx_behavior_note_animal ON behavior_note(animal_id);
CREATE INDEX idx_behavior_note_clinic ON behavior_note(clinic_id);
CREATE INDEX idx_behavior_note_category ON behavior_note(category);
CREATE INDEX idx_behavior_note_severity ON behavior_note(severity);
CREATE INDEX idx_behavior_note_observed_date ON behavior_note(observed_date);

-- Add check constraint for behavior category
ALTER TABLE behavior_note ADD CONSTRAINT check_behavior_category 
    CHECK (category IN ('AGGRESSION', 'ANXIETY', 'FEEDING', 'SOCIAL', 'TRAINING', 'OTHER'));

-- Add check constraint for severity
ALTER TABLE behavior_note ADD CONSTRAINT check_behavior_severity 
    CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'));

-- Add missing indexes on existing tables for performance optimization
CREATE INDEX IF NOT EXISTS idx_clinical_examination_animal ON clinical_examination(animal_id);
CREATE INDEX IF NOT EXISTS idx_clinical_examination_date ON clinical_examination(date);

CREATE INDEX IF NOT EXISTS idx_radiological_imaging_animal ON radiological_imaging(animal_id);
CREATE INDEX IF NOT EXISTS idx_radiological_imaging_date ON radiological_imaging(date);
CREATE INDEX IF NOT EXISTS idx_radiological_imaging_type ON radiological_imaging(type);

CREATE INDEX IF NOT EXISTS idx_medical_history_animal ON medical_history(animal_id);
CREATE INDEX IF NOT EXISTS idx_medical_history_date ON medical_history(date);

CREATE INDEX IF NOT EXISTS idx_prescription_animal ON prescription(animal_id);

CREATE INDEX IF NOT EXISTS idx_animal_weight_history_animal ON animal_weight_history(animal_id);
CREATE INDEX IF NOT EXISTS idx_animal_weight_history_measured_at ON animal_weight_history(measured_at);
