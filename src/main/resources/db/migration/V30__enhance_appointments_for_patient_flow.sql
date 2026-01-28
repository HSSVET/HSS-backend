-- V30: Enhance appointments for patient flow management
-- Adds appointment types, check-in tracking, and queue management fields

-- Add appointment type enum
ALTER TABLE appointment ADD COLUMN appointment_type VARCHAR(20) DEFAULT 'GENERAL_EXAM';

-- Add check-in and queue management fields
ALTER TABLE appointment ADD COLUMN check_in_time TIMESTAMP;
ALTER TABLE appointment ADD COLUMN queue_number INTEGER;
ALTER TABLE appointment ADD COLUMN estimated_start_time TIMESTAMP;

-- Add constraint for appointment type
ALTER TABLE appointment ADD CONSTRAINT chk_appointment_type 
    CHECK (appointment_type IN ('GENERAL_EXAM', 'VACCINATION', 'SURGERY', 'FOLLOW_UP', 'EMERGENCY', 'LAB_RESULTS'));

-- Create indexes for performance
CREATE INDEX idx_appointment_type ON appointment(appointment_type);
CREATE INDEX idx_appointment_check_in_time ON appointment(check_in_time);
CREATE INDEX idx_appointment_queue_number ON appointment(queue_number);

-- Add comment
COMMENT ON COLUMN appointment.appointment_type IS 'Type of appointment: GENERAL_EXAM, VACCINATION, SURGERY, FOLLOW_UP, EMERGENCY, LAB_RESULTS';
COMMENT ON COLUMN appointment.check_in_time IS 'Timestamp when patient checked in at clinic';
COMMENT ON COLUMN appointment.queue_number IS 'Queue position number for the day';
COMMENT ON COLUMN appointment.estimated_start_time IS 'Estimated time for appointment to start based on queue';
