ALTER TABLE appointment ADD COLUMN IF NOT EXISTS confirmed_at DATETIME;
ALTER TABLE appointment ADD COLUMN IF NOT EXISTS confirmed_by VARCHAR(36);
ALTER TABLE appointment ADD COLUMN IF NOT EXISTS confirmed_by_type VARCHAR(36);

ALTER TABLE appointmentArchive ADD COLUMN IF NOT EXISTS confirmed_at DATETIME;
ALTER TABLE appointmentArchive ADD COLUMN IF NOT EXISTS confirmed_by VARCHAR(36);
ALTER TABLE appointmentArchive ADD COLUMN IF NOT EXISTS confirmed_by_type VARCHAR(36);
