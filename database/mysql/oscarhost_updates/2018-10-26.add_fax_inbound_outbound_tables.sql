-- add a table for tracking inbound faxes
CREATE TABLE IF NOT EXISTS `fax_inbound` (
  id                      BIGINT UNSIGNED AUTO_INCREMENT,
  fax_account_id          BIGINT UNSIGNED,
  document_no             int(20),
  created_at              datetime NOT NULL,
  file_name               VARCHAR(255),
  external_account_id     VARCHAR(255),
  external_account_type   VARCHAR(32),
  external_reference_id   BIGINT UNSIGNED,
  PRIMARY KEY (`id`)
);

-- add a table for tracking outbound faxes
CREATE TABLE IF NOT EXISTS `fax_outbound` (
  id                      BIGINT UNSIGNED AUTO_INCREMENT,
  fax_account_id          BIGINT UNSIGNED,
  provider_no             VARCHAR(32),
  demographic_no          int(10),
  created_at              datetime NOT NULL,
  status                  VARCHAR(16),
  status_message          text,
  sent_to                 VARCHAR(32),
  file_type               VARCHAR(16),
  file_name               VARCHAR(255),
  external_account_id     VARCHAR(255),
  external_account_type   VARCHAR(32),
  external_reference_id   BIGINT UNSIGNED,
  PRIMARY KEY (`id`)
);