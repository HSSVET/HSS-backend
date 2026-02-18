-- V34: Create consent form management system
-- Tracks digital consent forms with signature capture

CREATE TABLE consent_form (
    consent_form_id SERIAL PRIMARY KEY,
    clinic_id INT NOT NULL REFERENCES clinic(clinic_id) ON DELETE CASCADE,
    
    -- Related entities
    owner_id INT NOT NULL REFERENCES owner(owner_id) ON DELETE CASCADE,
    animal_id INT NOT NULL REFERENCES animal(animal_id) ON DELETE CASCADE,
    surgery_id INT REFERENCES surgery(surgery_id) ON DELETE CASCADE,
    appointment_id INT REFERENCES appointment(appointment_id) ON DELETE CASCADE,
    
    -- Form details
    form_type VARCHAR(50) NOT NULL 
        CHECK (form_type IN ('ANESTHESIA', 'SURGERY', 'TREATMENT', 'EUTHANASIA', 'GENERAL', 'RESEARCH')),
    form_title VARCHAR(200) NOT NULL,
    form_content TEXT NOT NULL,
    
    -- Signature
    signature_data TEXT, -- Base64 encoded PNG image of signature
    signature_date TIMESTAMP,
    signer_name VARCHAR(200) NOT NULL,
    signer_relation VARCHAR(50), -- e.g., 'Owner', 'Guardian', 'Representative'
    
    -- Witness (optional)
    witness_name VARCHAR(200),
    witness_signature_data TEXT,
    witness_date TIMESTAMP,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'SIGNED', 'DECLINED', 'EXPIRED', 'REVOKED')),
    
    -- Additional info
    notes TEXT,
    expiry_date DATE,
    
    -- File storage (if PDF generated)
    document_url TEXT,
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Indexes for performance
CREATE INDEX idx_consent_form_clinic_id ON consent_form(clinic_id);
CREATE INDEX idx_consent_form_owner_id ON consent_form(owner_id);
CREATE INDEX idx_consent_form_animal_id ON consent_form(animal_id);
CREATE INDEX idx_consent_form_surgery_id ON consent_form(surgery_id);
CREATE INDEX idx_consent_form_appointment_id ON consent_form(appointment_id);
CREATE INDEX idx_consent_form_type ON consent_form(form_type);
CREATE INDEX idx_consent_form_status ON consent_form(status);
CREATE INDEX idx_consent_form_signature_date ON consent_form(signature_date);

-- Comments
COMMENT ON TABLE consent_form IS 'Digital consent forms with signature capture for surgeries, treatments, and procedures';
COMMENT ON COLUMN consent_form.form_type IS 'Type of consent: ANESTHESIA, SURGERY, TREATMENT, EUTHANASIA, GENERAL, RESEARCH';
COMMENT ON COLUMN consent_form.signature_data IS 'Base64 encoded PNG image of digital signature captured via canvas';
COMMENT ON COLUMN consent_form.status IS 'Form status: PENDING, SIGNED, DECLINED, EXPIRED, REVOKED';
COMMENT ON COLUMN consent_form.document_url IS 'URL to generated PDF document stored in cloud storage';
