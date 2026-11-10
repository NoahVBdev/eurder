-- Optimistic locking for items: prevents lost updates when concurrent orders change the stock.
-- DEFAULT 0 fills existing rows, and keeps plain-SQL inserts (schema tests) working.
ALTER TABLE item ADD COLUMN version INTEGER NOT NULL DEFAULT 0;