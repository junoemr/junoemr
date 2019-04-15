DROP PROCEDURE IF EXISTS demographic_referral_source;
DROP PROCEDURE IF EXISTS demographicArchive_referral_source;

DELIMITER //

CREATE PROCEDURE demographic_referral_source()
  BEGIN
  IF NOT EXISTS
    (SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
      WHERE table_name = 'demographic'
      AND column_name='referral_source_id'
      AND table_schema=DATABASE()) THEN
    ALTER TABLE demographic
    ADD COLUMN referral_source_id INT DEFAULT NULL,
    ADD CONSTRAINT demographic_referral_source_id_fk FOREIGN KEY (referral_source_id) REFERENCES referral_source(id) ON DELETE SET NULL;
  END IF;
  END //

CREATE PROCEDURE demographicArchive_referral_source()
  BEGIN
  IF NOT EXISTS
    (SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
      WHERE table_name = 'demographicArchive'
      AND column_name='referral_source_id'
      AND table_schema=DATABASE()) THEN
    ALTER TABLE demographicArchive
    ADD COLUMN referral_source_id INT DEFAULT NULL,
    ADD CONSTRAINT demographicArchive_referral_source_id_fk FOREIGN KEY (referral_source_id) REFERENCES referral_source(id) ON DELETE SET NULL;
  END IF;
  END //

DELIMITER ;

CREATE TABLE IF NOT EXISTS referral_source (
  id INT AUTO_INCREMENT PRIMARY KEY,
  source VARCHAR(255),
  updated_at DATETIME DEFAULT NULL,
  deleted_at DATETIME DEFAULT NULL);

CALL demographic_referral_source();
CALL demographicArchive_referral_source();

DROP PROCEDURE IF EXISTS demographic_referral_source;
DROP PROCEDURE IF EXISTS demographicArchive_referral_source;