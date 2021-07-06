BEGIN;
ALTER TABLE ProviderPreference ADD COLUMN appointmentReasonDisplayLevel2 VARCHAR(64) DEFAULT "DEFAULT_ALL";
UPDATE ProviderPreference SET appointmentReasonDisplayLevel2 = appointmentReasonDisplayLevel;
ALTER TABLE ProviderPreference DROP COLUMN appointmentReasonDisplayLevel;
ALTER TABLE ProviderPreference CHANGE COLUMN appointmentReasonDisplayLevel2 appointmentReasonDisplayLevel VARCHAR(64) DEFAULT "DEFAULT_ALL";
COMMIT;