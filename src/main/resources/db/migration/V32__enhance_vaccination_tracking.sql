-- V32: Enhance vaccination tracking with barcodes and workflow support
-- Adds barcode, serial number, appointment linking, and pre-exam tracking

-- Add new fields to vaccination_record
ALTER TABLE vaccination_record ADD COLUMN barcode VARCHAR(100) UNIQUE;
ALTER TABLE vaccination_record ADD COLUMN serial_number VARCHAR(100);
ALTER TABLE vaccination_record ADD COLUMN appointment_id INT REFERENCES appointment(appointment_id) ON DELETE SET NULL;
ALTER TABLE vaccination_record ADD COLUMN pre_exam_completed BOOLEAN DEFAULT false;
ALTER TABLE vaccination_record ADD COLUMN next_appointment_id INT REFERENCES appointment(appointment_id) ON DELETE SET NULL;
ALTER TABLE vaccination_record ADD COLUMN clinic_id INT REFERENCES clinic(clinic_id) ON DELETE CASCADE;

-- Create indexes
CREATE INDEX idx_vaccination_record_barcode ON vaccination_record(barcode);
CREATE INDEX idx_vaccination_record_serial_number ON vaccination_record(serial_number);
CREATE INDEX idx_vaccination_record_appointment_id ON vaccination_record(appointment_id);
CREATE INDEX idx_vaccination_record_next_appointment_id ON vaccination_record(next_appointment_id);
CREATE INDEX idx_vaccination_record_clinic_id ON vaccination_record(clinic_id);

-- Add clinic filter for multi-tenancy
-- Note: This assumes clinic_id is being added. If already exists, skip the ADD COLUMN above

-- Comments
COMMENT ON COLUMN vaccination_record.barcode IS 'Unique QR code or barcode for this vaccination record';
COMMENT ON COLUMN vaccination_record.serial_number IS 'System-generated serial number for tracking';
COMMENT ON COLUMN vaccination_record.appointment_id IS 'Link to the appointment where vaccination was administered';
COMMENT ON COLUMN vaccination_record.pre_exam_completed IS 'Whether pre-vaccination examination was performed';
COMMENT ON COLUMN vaccination_record.next_appointment_id IS 'Automatically created follow-up appointment for next vaccination';
