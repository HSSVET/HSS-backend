-- V37: Create Pending Transaction (Cart) Tables
-- This migration adds support for cart/pending balance system
-- where services can be added and paid partially or fully at different stages.

-- Pending Transaction table (Cart)
CREATE TABLE IF NOT EXISTS pending_transaction (
    pending_transaction_id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinic(clinic_id),
    owner_id BIGINT NOT NULL REFERENCES owner(owner_id),
    animal_id BIGINT REFERENCES animal(animal_id),
    appointment_id BIGINT REFERENCES appointment(appointment_id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    paid_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    invoiced_at TIMESTAMP,
    invoice_id BIGINT REFERENCES invoice(invoice_id),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT chk_pending_transaction_status CHECK (status IN ('PENDING', 'PARTIAL_PAID', 'INVOICED', 'CANCELLED'))
);

-- Pending Transaction Item table
CREATE TABLE IF NOT EXISTS pending_transaction_item (
    pending_transaction_item_id BIGSERIAL PRIMARY KEY,
    pending_transaction_id BIGINT NOT NULL REFERENCES pending_transaction(pending_transaction_id) ON DELETE CASCADE,
    service_type VARCHAR(50) NOT NULL,
    service_id BIGINT,
    description TEXT NOT NULL,
    quantity INT DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) DEFAULT 0.00,
    line_total DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vet_service_id BIGINT REFERENCES vet_service(service_id),
    stock_product_id BIGINT REFERENCES stock_product(product_id),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT chk_pending_item_service_type CHECK (service_type IN (
        'EXAMINATION', 'VACCINATION', 'SURGERY', 'LAB_TEST', 'RADIOLOGY',
        'HOSPITALIZATION', 'MEDICATION', 'GROOMING', 'CONSULTATION',
        'EMERGENCY', 'FOLLOW_UP', 'OTHER'
    ))
);

-- Indexes for Pending Transaction
CREATE INDEX IF NOT EXISTS idx_pending_transaction_clinic ON pending_transaction(clinic_id);
CREATE INDEX IF NOT EXISTS idx_pending_transaction_owner ON pending_transaction(owner_id);
CREATE INDEX IF NOT EXISTS idx_pending_transaction_animal ON pending_transaction(animal_id);
CREATE INDEX IF NOT EXISTS idx_pending_transaction_status ON pending_transaction(status);
CREATE INDEX IF NOT EXISTS idx_pending_transaction_appointment ON pending_transaction(appointment_id);
CREATE INDEX IF NOT EXISTS idx_pending_transaction_created ON pending_transaction(created_at DESC);

-- Indexes for Pending Transaction Item
CREATE INDEX IF NOT EXISTS idx_pending_item_transaction ON pending_transaction_item(pending_transaction_id);
CREATE INDEX IF NOT EXISTS idx_pending_item_service ON pending_transaction_item(service_type, service_id);
CREATE INDEX IF NOT EXISTS idx_pending_item_vet_service ON pending_transaction_item(vet_service_id);

-- Comment on tables
COMMENT ON TABLE pending_transaction IS 'Cart/pending balance for services that have not been fully paid yet';
COMMENT ON TABLE pending_transaction_item IS 'Individual items in a pending transaction';
COMMENT ON COLUMN pending_transaction.status IS 'PENDING: open cart, PARTIAL_PAID: some payment made, INVOICED: converted to invoice, CANCELLED: voided';
COMMENT ON COLUMN pending_transaction_item.service_type IS 'Type of service: EXAMINATION, VACCINATION, SURGERY, LAB_TEST, etc.';
COMMENT ON COLUMN pending_transaction_item.service_id IS 'ID of the related entity (VaccinationRecord, Surgery, LabTest, etc.)';
