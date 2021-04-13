ALTER TABLE demographic ADD COLUMN IF NOT EXISTS electronic_messaging_consent_given_at DATETIME;
ALTER TABLE demographic ADD COLUMN IF NOT EXISTS electronic_messaging_consent_rejected_at DATETIME;
ALTER TABLE demographicArchive ADD COLUMN IF NOT EXISTS electronic_messaging_consent_given_at DATETIME;
ALTER TABLE demographicArchive ADD COLUMN IF NOT EXISTS electronic_messaging_consent_rejected_at DATETIME;