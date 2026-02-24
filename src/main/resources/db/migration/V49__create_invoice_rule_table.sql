-- V14: Create invoice_rule table for automatic invoice generation

CREATE TABLE invoice_rule (
    rule_id SERIAL PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    rule_type VARCHAR(50) NOT NULL CHECK (rule_type IN ('APPOINTMENT_AFTER', 'MONTHLY_SUBSCRIPTION', 'TREATMENT_AFTER', 'VACCINATION_AFTER', 'LAB_TEST_AFTER', 'CUSTOM')),
    trigger_entity VARCHAR(50), -- e.g., 'APPOINTMENT', 'TREATMENT', 'VACCINATION'
    trigger_status VARCHAR(50), -- e.g., 'COMPLETED', 'CONFIRMED'
    conditions JSONB, -- Store rule conditions as JSON
    invoice_template JSONB, -- Store invoice items template as JSON
    due_days INT DEFAULT 30, -- Days until invoice due date
    is_active BOOLEAN DEFAULT true,
    priority INT DEFAULT 0,
    description TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Add indexes for performance
CREATE INDEX idx_invoice_rule_type ON invoice_rule(rule_type);
CREATE INDEX idx_invoice_rule_active ON invoice_rule(is_active);
CREATE INDEX idx_invoice_rule_trigger_entity ON invoice_rule(trigger_entity);

