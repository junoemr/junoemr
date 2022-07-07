-- fix any legacy data, just in case
UPDATE tickler SET service_date = '1900-01-01 00:00:00' WHERE service_date IS NULL;
UPDATE tickler SET update_date = NOW() WHERE update_date IS NULL;
-- add table restrictions
ALTER TABLE tickler MODIFY COLUMN service_date datetime NOT NULL;
ALTER TABLE tickler MODIFY COLUMN update_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP;