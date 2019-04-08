DROP PROCEDURE IF EXISTS demographic_referral_source;
DROP PROCEDURE IF EXISTS demographicArchive_referral_source;

DELIMITER //

CREATE PROCEDURE demographic_referral_source()
  BEGIN
  IF NOT EXISTS
    (SELECT NULL FROM INFORMATION_SCHEMA.COLUMNS
      WHERE table_name = 'demographic'
      AND column_name='referral_source') THEN
    ALTER TABLE demographic ADD COLUMN id_referral_source INT DEFAULT NULL;
  END IF;
  END //

CREATE PROCEDURE demographicArchive_referral_source()
  BEGIN
  IF NOT EXISTS
    (SELECT NULL FROM INFORMATION_SCHEMA.COLUMNS
      WHERE table_name = 'demographicArchive'
      AND column_name='referral_source') THEN

    ALTER TABLE demographicArchive ADD COLUMN id_referral_source INT DEFAULT NULL;

  END IF;
  END //

DELIMITER ;

CREATE TABLE IF NOT EXISTS referral_source (
  id INT AUTO_INCREMENT PRIMARY KEY,
  source VARCHAR(255),
  updated_at DATETIME DEFAULT NULL,
  deleted_at DATETIME DEFAULT NULL);

DROP PROCEDURE IF EXISTS demographic_referral_source;
DROP PROCEDURE IF EXISTS demographicArchive_referral_source;