-- V9: Add additional indexes, constraints, and audit triggers

-- Add additional performance indexes
CREATE INDEX idx_animal_name ON animal(name);
CREATE INDEX idx_animal_birth_date ON animal(birth_date);
CREATE INDEX idx_animal_weight ON animal(weight);
CREATE INDEX idx_owner_first_name ON owner(first_name);
CREATE INDEX idx_owner_last_name ON owner(last_name);
CREATE INDEX idx_appointment_veterinarian_date ON appointment(veterinarian_id, date_time);
CREATE INDEX idx_invoice_owner_date ON invoice(owner_id, date);
CREATE INDEX idx_prescription_animal_date ON prescription(animal_id, date);
CREATE INDEX idx_vaccination_record_next_due ON vaccination_record(next_due_date) WHERE next_due_date IS NOT NULL;

-- Add check constraints for data integrity
ALTER TABLE animal ADD CONSTRAINT chk_animal_weight_positive CHECK (weight > 0);
ALTER TABLE animal ADD CONSTRAINT chk_animal_birth_date_future CHECK (birth_date <= CURRENT_DATE);
ALTER TABLE appointment ADD CONSTRAINT chk_appointment_date_future CHECK (date_time >= CURRENT_TIMESTAMP - INTERVAL '1 day');
ALTER TABLE invoice ADD CONSTRAINT chk_invoice_amount_positive CHECK (amount >= 0);
ALTER TABLE invoice_item ADD CONSTRAINT chk_invoice_item_quantity_positive CHECK (quantity > 0);
ALTER TABLE invoice_item ADD CONSTRAINT chk_invoice_item_unit_price_positive CHECK (unit_price >= 0);
ALTER TABLE stock_product ADD CONSTRAINT chk_stock_product_min_max CHECK (min_stock <= max_stock);
ALTER TABLE stock_transaction ADD CONSTRAINT chk_stock_transaction_quantity_positive CHECK (quantity > 0);

-- Add unique constraints
ALTER TABLE invoice ADD CONSTRAINT uk_invoice_number UNIQUE (invoice_number);
ALTER TABLE stock_product ADD CONSTRAINT uk_stock_product_barcode UNIQUE (barcode) WHERE barcode IS NOT NULL;
ALTER TABLE equipment ADD CONSTRAINT uk_equipment_serial UNIQUE (serial_no) WHERE serial_no IS NOT NULL;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Add updated_at triggers to all tables
CREATE TRIGGER trigger_species_updated_at BEFORE UPDATE ON species FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_breed_updated_at BEFORE UPDATE ON breed FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_owner_updated_at BEFORE UPDATE ON owner FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_animal_updated_at BEFORE UPDATE ON animal FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_medical_history_updated_at BEFORE UPDATE ON medical_history FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_clinical_examination_updated_at BEFORE UPDATE ON clinical_examination FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_appointment_updated_at BEFORE UPDATE ON appointment FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_lab_tests_updated_at BEFORE UPDATE ON lab_tests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_lab_results_updated_at BEFORE UPDATE ON lab_results FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_radiological_imaging_updated_at BEFORE UPDATE ON radiological_imaging FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_medicine_updated_at BEFORE UPDATE ON medicine FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_prescription_updated_at BEFORE UPDATE ON prescription FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_vaccine_updated_at BEFORE UPDATE ON vaccine FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_vaccination_record_updated_at BEFORE UPDATE ON vaccination_record FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_pathology_findings_updated_at BEFORE UPDATE ON pathology_findings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_invoice_updated_at BEFORE UPDATE ON invoice FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_invoice_item_updated_at BEFORE UPDATE ON invoice_item FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_document_updated_at BEFORE UPDATE ON document FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_communication_updated_at BEFORE UPDATE ON communication FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_staff_updated_at BEFORE UPDATE ON staff FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_user_account_updated_at BEFORE UPDATE ON user_account FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_role_updated_at BEFORE UPDATE ON role FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_stock_product_updated_at BEFORE UPDATE ON stock_product FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_stock_transaction_updated_at BEFORE UPDATE ON stock_transaction FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_equipment_updated_at BEFORE UPDATE ON equipment FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_maintenance_updated_at BEFORE UPDATE ON maintenance FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_reminder_updated_at BEFORE UPDATE ON reminder FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_report_schedule_updated_at BEFORE UPDATE ON report_schedule FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add comments to tables for documentation
COMMENT ON TABLE species IS 'Animal species catalog';
COMMENT ON TABLE breed IS 'Animal breeds by species';
COMMENT ON TABLE owner IS 'Pet owners information';
COMMENT ON TABLE animal IS 'Pet/animal records';
COMMENT ON TABLE medical_history IS 'Medical history records for animals';
COMMENT ON TABLE clinical_examination IS 'Clinical examination records';
COMMENT ON TABLE appointment IS 'Veterinary appointments';
COMMENT ON TABLE lab_tests IS 'Laboratory test orders';
COMMENT ON TABLE lab_results IS 'Laboratory test results';
COMMENT ON TABLE radiological_imaging IS 'Radiological images and reports';
COMMENT ON TABLE medicine IS 'Medicine catalog';
COMMENT ON TABLE prescription IS 'Prescription records';
COMMENT ON TABLE vaccine IS 'Vaccine catalog';
COMMENT ON TABLE vaccination_record IS 'Vaccination records';
COMMENT ON TABLE pathology_findings IS 'Pathology examination results';
COMMENT ON TABLE invoice IS 'Billing invoices';
COMMENT ON TABLE invoice_item IS 'Invoice line items';
COMMENT ON TABLE document IS 'Document storage and management';
COMMENT ON TABLE communication IS 'Communication log with owners';
COMMENT ON TABLE staff IS 'Staff/employee records';
COMMENT ON TABLE user_account IS 'User authentication accounts';
COMMENT ON TABLE role IS 'User roles and permissions';
COMMENT ON TABLE staff_role IS 'Staff role assignments';
COMMENT ON TABLE stock_product IS 'Inventory product catalog';
COMMENT ON TABLE stock_transaction IS 'Inventory transaction log';
COMMENT ON TABLE equipment IS 'Equipment and asset management';
COMMENT ON TABLE maintenance IS 'Equipment maintenance records';
COMMENT ON TABLE reminder IS 'Appointment reminder system';
COMMENT ON TABLE report_schedule IS 'Automated report scheduling';
