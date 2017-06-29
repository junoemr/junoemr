ALTER TABLE appointment ADD COLUMN partial_booking tinyint(1) NOT NULL DEFAULT 0;
ALTER TABLE appointmentArchive ADD COLUMN partial_booking tinyint(1) NOT NULL DEFAULT 0;