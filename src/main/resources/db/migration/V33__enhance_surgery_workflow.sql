-- V33: Enhance surgery workflow with pre-op, operative, and post-op tracking
-- Adds comprehensive surgery workflow management fields

-- Add workflow fields to surgery table
ALTER TABLE surgery ADD COLUMN appointment_id INT REFERENCES appointment(appointment_id) ON DELETE SET NULL;
ALTER TABLE surgery ADD COLUMN operation_room VARCHAR(50);
ALTER TABLE surgery ADD COLUMN fasting_hours INT DEFAULT 12;

-- Pre-operative fields
ALTER TABLE surgery ADD COLUMN pre_op_exam_completed BOOLEAN DEFAULT false;
ALTER TABLE surgery ADD COLUMN pre_op_exam_date TIMESTAMP;
ALTER TABLE surgery ADD COLUMN pre_op_tests_completed BOOLEAN DEFAULT false;
ALTER TABLE surgery ADD COLUMN required_tests TEXT; -- JSON array of required test types

-- Consent tracking
ALTER TABLE surgery ADD COLUMN anesthesia_consent_signed BOOLEAN DEFAULT false;
ALTER TABLE surgery ADD COLUMN anesthesia_consent_date TIMESTAMP;
ALTER TABLE surgery ADD COLUMN surgery_consent_signed BOOLEAN DEFAULT false;
ALTER TABLE surgery ADD COLUMN surgery_consent_date TIMESTAMP;

-- Operative fields
ALTER TABLE surgery ADD COLUMN actual_start_time TIMESTAMP;
ALTER TABLE surgery ADD COLUMN actual_end_time TIMESTAMP;
ALTER TABLE surgery ADD COLUMN complications TEXT;

-- Post-operative fields
ALTER TABLE surgery ADD COLUMN discharge_type VARCHAR(20) 
    CHECK (discharge_type IN ('SAME_DAY', 'HOSPITALIZATION', 'TRANSFER'));
ALTER TABLE surgery ADD COLUMN discharge_date TIMESTAMP;
ALTER TABLE surgery ADD COLUMN follow_up_appointment_id INT REFERENCES appointment(appointment_id) ON DELETE SET NULL;
ALTER TABLE surgery ADD COLUMN prescription_id INT; -- Could link to prescription table if exists

-- SMS reminder tracking
ALTER TABLE surgery ADD COLUMN pre_op_sms_sent BOOLEAN DEFAULT false;
ALTER TABLE surgery ADD COLUMN pre_op_sms_sent_at TIMESTAMP;

-- Create indexes
CREATE INDEX idx_surgery_appointment_id ON surgery(appointment_id);
CREATE INDEX idx_surgery_operation_room ON surgery(operation_room);
CREATE INDEX idx_surgery_follow_up_appointment_id ON surgery(follow_up_appointment_id);
CREATE INDEX idx_surgery_discharge_type ON surgery(discharge_type);
CREATE INDEX idx_surgery_pre_op_exam_completed ON surgery(pre_op_exam_completed);
CREATE INDEX idx_surgery_pre_op_tests_completed ON surgery(pre_op_tests_completed);

-- Comments
COMMENT ON COLUMN surgery.appointment_id IS 'Link to scheduled surgery appointment';
COMMENT ON COLUMN surgery.operation_room IS 'Assigned operating room identifier';
COMMENT ON COLUMN surgery.fasting_hours IS 'Required fasting hours before surgery (default 12)';
COMMENT ON COLUMN surgery.pre_op_exam_completed IS 'Whether pre-operative examination was completed';
COMMENT ON COLUMN surgery.pre_op_tests_completed IS 'Whether all required pre-op tests (blood work, imaging) are completed';
COMMENT ON COLUMN surgery.required_tests IS 'JSON array of required test types';
COMMENT ON COLUMN surgery.discharge_type IS 'Discharge type: SAME_DAY, HOSPITALIZATION, TRANSFER';
COMMENT ON COLUMN surgery.follow_up_appointment_id IS 'Automatically created post-operative follow-up appointment';
COMMENT ON COLUMN surgery.pre_op_sms_sent IS 'Whether pre-operative SMS reminder (fasting instructions) was sent';
