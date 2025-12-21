-- V15: Create surgery and hospitalization tables

CREATE TABLE surgery (
    surgery_id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animal(animal_id) ON DELETE CASCADE,
    veterinarian_id INT, -- Can be linked to a staff/user table if strict FK required, currently loosely coupled in plan
    date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    notes TEXT,
    pre_op_instructions TEXT,
    post_op_instructions TEXT,
    anesthesia_protocol TEXT,
    anesthesia_consent BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE surgery_medication (
    surgery_med_id SERIAL PRIMARY KEY,
    surgery_id INT REFERENCES surgery(surgery_id) ON DELETE CASCADE,
    medicine_id INT REFERENCES medicine(medicine_id), -- Assuming medicine table exists from V3
    quantity INT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hospitalization (
    hospitalization_id SERIAL PRIMARY KEY,
    animal_id INT REFERENCES animal(animal_id) ON DELETE CASCADE,
    admission_date TIMESTAMP NOT NULL,
    discharge_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DISCHARGED', 'TRANSFERRED')),
    primary_veterinarian VARCHAR(100),
    diagnosis_summary TEXT,
    care_plan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE hospitalization_log (
    log_id SERIAL PRIMARY KEY,
    hospitalization_id INT REFERENCES hospitalization(hospitalization_id) ON DELETE CASCADE,
    log_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    vital_signs JSONB, -- For flexiblity: temp, pulse, respiration, etc.
    entry_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_surgery_animal ON surgery(animal_id);
CREATE INDEX idx_surgery_date ON surgery(date);
CREATE INDEX idx_surgery_status ON surgery(status);

CREATE INDEX idx_hospitalization_animal ON hospitalization(animal_id);
CREATE INDEX idx_hospitalization_status ON hospitalization(status);
CREATE INDEX idx_hospitalization_admission ON hospitalization(admission_date);

CREATE INDEX idx_hospitalization_log_hospitalization ON hospitalization_log(hospitalization_id);
