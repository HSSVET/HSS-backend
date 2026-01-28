-- V31: Create queue management system
-- Tracks real-time patient queue status and flow through clinic

CREATE TABLE queue_entry (
    queue_entry_id SERIAL PRIMARY KEY,
    clinic_id INT NOT NULL REFERENCES clinic(clinic_id) ON DELETE CASCADE,
    appointment_id INT REFERENCES appointment(appointment_id) ON DELETE SET NULL,
    animal_id INT NOT NULL REFERENCES animal(animal_id) ON DELETE CASCADE,
    queue_number INT NOT NULL,
    queue_date DATE NOT NULL DEFAULT CURRENT_DATE,
    
    -- Queue status tracking
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING' 
        CHECK (status IN ('WAITING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW')),
    priority VARCHAR(10) NOT NULL DEFAULT 'NORMAL'
        CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT', 'EMERGENCY')),
    
    -- Timestamps for each status
    check_in_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_time TIMESTAMP,
    completed_time TIMESTAMP,
    
    -- Assignment
    assigned_veterinarian_id BIGINT,
    assigned_room VARCHAR(50),
    
    -- Estimated times
    estimated_duration_minutes INT DEFAULT 30,
    estimated_start_time TIMESTAMP,
    
    -- Notes
    notes TEXT,
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Indexes for performance
CREATE INDEX idx_queue_entry_clinic_id ON queue_entry(clinic_id);
CREATE INDEX idx_queue_entry_appointment_id ON queue_entry(appointment_id);
CREATE INDEX idx_queue_entry_animal_id ON queue_entry(animal_id);
CREATE INDEX idx_queue_entry_queue_date ON queue_entry(queue_date);
CREATE INDEX idx_queue_entry_status ON queue_entry(status);
CREATE INDEX idx_queue_entry_priority ON queue_entry(priority);
CREATE INDEX idx_queue_entry_veterinarian_id ON queue_entry(assigned_veterinarian_id);
CREATE INDEX idx_queue_entry_check_in_time ON queue_entry(check_in_time);

-- Composite index for finding today's queue for a clinic
CREATE INDEX idx_queue_entry_clinic_date_status ON queue_entry(clinic_id, queue_date, status);

-- Unique constraint: one queue number per clinic per day
CREATE UNIQUE INDEX idx_unique_queue_number_per_day ON queue_entry(clinic_id, queue_date, queue_number);

-- Comments
COMMENT ON TABLE queue_entry IS 'Tracks patient queue status and flow through the clinic';
COMMENT ON COLUMN queue_entry.status IS 'Current queue status: WAITING, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW';
COMMENT ON COLUMN queue_entry.priority IS 'Priority level: LOW, NORMAL, HIGH, URGENT, EMERGENCY';
COMMENT ON COLUMN queue_entry.queue_number IS 'Sequential number for the day, unique per clinic';
COMMENT ON COLUMN queue_entry.estimated_duration_minutes IS 'Estimated duration for this appointment in minutes';
