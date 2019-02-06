ALTER TABLE reportTemplates ADD COLUMN super_admin_verified tinyint(1) NOT NULL DEFAULT 0;

-- all existing templates set to approved, so that they continue working.
UPDATE reportTemplates SET super_admin_verified = '1';