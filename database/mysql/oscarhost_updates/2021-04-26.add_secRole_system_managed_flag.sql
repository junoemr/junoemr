ALTER TABLE secRole ADD COLUMN IF NOT EXISTS system_managed BOOLEAN NOT NULL DEFAULT FALSE AFTER description;
UPDATE secRole SET system_managed = TRUE WHERE role_name IN ('admin', 'doctor');