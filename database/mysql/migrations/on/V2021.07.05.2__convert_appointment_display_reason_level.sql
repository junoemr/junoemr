DROP PROCEDURE IF EXISTS convert_appointment_display_reason;

DELIMITER $$

CREATE PROCEDURE convert_appointment_display_reason()
BEGIN
ALTER TABLE ProviderPreference DROP COLUMN IF EXISTS appointmentReasonDisplayLevel2;
IF EXISTS (
    (SELECT * FROM information_schema.columns
      WHERE TABLE_SCHEMA = (SELECT DATABASE())
      AND TABLE_NAME = 'ProviderPreference'
      AND COLUMN_NAME = 'appointmentReasonDisplayLevel' AND DATA_TYPE = 'enum')
    ) THEN
      ALTER TABLE ProviderPreference ADD COLUMN appointmentReasonDisplayLevel2 VARCHAR(64) DEFAULT 'DEFAULT_ALL';
      UPDATE ProviderPreference SET appointmentReasonDisplayLevel2 = appointmentReasonDisplayLevel;
      ALTER TABLE ProviderPreference DROP COLUMN IF EXISTS appointmentReasonDisplayLevel;
      ALTER TABLE ProviderPreference CHANGE COLUMN appointmentReasonDisplayLevel2 appointmentReasonDisplayLevel VARCHAR(64) DEFAULT 'DEFAULT_ALL';
END IF;
ALTER TABLE ProviderPreference DROP COLUMN IF EXISTS appointmentReasonDisplayLevel2;
END $$

DELIMITER ;

CALL convert_appointment_display_reason();
DROP PROCEDURE IF EXISTS convert_appointment_display_reason;