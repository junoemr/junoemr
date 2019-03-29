ALTER TABLE demographic
  ADD id_referral_source BIGINT DEFAULT NULL;

ALTER TABLE demographicArchive
  ADD id_referral_source BIGINT DEFAULT NULL;

CREATE TABLE IF NOT EXISTS referral_source (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  source varchar(255),
  updated_at DATETIME DEFAULT NULL,
  deleted_at DATETIME DEFAULT NULL);
