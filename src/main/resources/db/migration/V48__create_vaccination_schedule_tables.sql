-- V13: Create vaccination schedule and protocol tables

CREATE TABLE vaccination_protocol (
    protocol_id SERIAL PRIMARY KEY,
    species_id INT REFERENCES species(species_id) ON DELETE CASCADE,
    vaccine_id INT REFERENCES vaccine(vaccine_id) ON DELETE RESTRICT,
    protocol_name VARCHAR(100) NOT NULL,
    first_dose_age_weeks INT NOT NULL DEFAULT 0,
    dose_interval_weeks INT NOT NULL DEFAULT 4,
    total_doses INT NOT NULL DEFAULT 1,
    booster_interval_months INT,
    is_required BOOLEAN DEFAULT true,
    priority INT DEFAULT 0,
    notes TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE vaccination_schedule (
    schedule_id SERIAL PRIMARY KEY,
    animal_id INT NOT NULL REFERENCES animal(animal_id) ON DELETE CASCADE,
    vaccine_id INT NOT NULL REFERENCES vaccine(vaccine_id) ON DELETE RESTRICT,
    protocol_id INT REFERENCES vaccination_protocol(protocol_id) ON DELETE SET NULL,
    scheduled_date DATE NOT NULL,
    dose_number INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'OVERDUE', 'SKIPPED')),
    priority VARCHAR(10) DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    is_overdue BOOLEAN DEFAULT false,
    completed_date DATE,
    vaccination_record_id INT REFERENCES vaccination_record(vaccination_record_id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Add indexes for performance
CREATE INDEX idx_vaccination_protocol_species_id ON vaccination_protocol(species_id);
CREATE INDEX idx_vaccination_protocol_vaccine_id ON vaccination_protocol(vaccine_id);
CREATE INDEX idx_vaccination_protocol_active ON vaccination_protocol(is_active);
CREATE INDEX idx_vaccination_schedule_animal_id ON vaccination_schedule(animal_id);
CREATE INDEX idx_vaccination_schedule_vaccine_id ON vaccination_schedule(vaccine_id);
CREATE INDEX idx_vaccination_schedule_date ON vaccination_schedule(scheduled_date);
CREATE INDEX idx_vaccination_schedule_status ON vaccination_schedule(status);
CREATE INDEX idx_vaccination_schedule_overdue ON vaccination_schedule(is_overdue);
CREATE INDEX idx_vaccination_schedule_priority ON vaccination_schedule(priority);

