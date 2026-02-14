-- V12: Create stock_alert table for stock level alerts

CREATE TABLE stock_alert (
    alert_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES stock_product(product_id) ON DELETE CASCADE,
    alert_type VARCHAR(20) NOT NULL CHECK (alert_type IN ('LOW_STOCK', 'CRITICAL_STOCK', 'OUT_OF_STOCK', 'EXPIRING_SOON', 'EXPIRED')),
    current_stock INT NOT NULL,
    threshold_value INT,
    expiration_date DATE,
    message TEXT,
    is_resolved BOOLEAN DEFAULT false,
    resolved_at TIMESTAMP,
    resolved_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Add indexes for performance
CREATE INDEX idx_stock_alert_product_id ON stock_alert(product_id);
CREATE INDEX idx_stock_alert_type ON stock_alert(alert_type);
CREATE INDEX idx_stock_alert_resolved ON stock_alert(is_resolved);
CREATE INDEX idx_stock_alert_created_at ON stock_alert(created_at);

