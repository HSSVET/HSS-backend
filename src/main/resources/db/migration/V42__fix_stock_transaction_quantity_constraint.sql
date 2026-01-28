-- V42: Fix stock_transaction quantity constraint to allow OUT (negative) movements

-- Previous constraint required quantity > 0, but the inventory
-- logic uses negative quantities for OUT movements (e.g. vaccinations).
-- This migration relaxes the constraint so that both positive (IN)
-- and negative (OUT) quantities are allowed, only zero is forbidden.

ALTER TABLE stock_transaction
  DROP CONSTRAINT IF EXISTS chk_stock_transaction_quantity_positive;

ALTER TABLE stock_transaction
  ADD CONSTRAINT chk_stock_transaction_quantity_nonzero
  CHECK (quantity <> 0);

COMMENT ON CONSTRAINT chk_stock_transaction_quantity_nonzero ON stock_transaction
  IS 'Quantity can be positive (IN) or negative (OUT), but not zero.';

