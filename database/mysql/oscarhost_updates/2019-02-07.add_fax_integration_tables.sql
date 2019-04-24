-- add a table for fax accounts
CREATE TABLE IF NOT EXISTS `fax_account` (
  id                      BIGINT AUTO_INCREMENT,
  login_id                VARCHAR(128),
  login_password          VARCHAR(128),
  integration_type        VARCHAR(32),
  integration_enabled     TINYINT(1) NOT NULL DEFAULT 0,
  inbound_enabled         TINYINT(1) NOT NULL DEFAULT 0,
  outbound_enabled        TINYINT(1) NOT NULL DEFAULT 0,
  reply_fax_number        VARCHAR(12),
  email                   VARCHAR(128),
  cover_letter_option     VARCHAR(255),
  display_name            VARCHAR(255),
  deleted_at              datetime,
  PRIMARY KEY (`id`)
);

-- add a table for tracking inbound faxes
CREATE TABLE IF NOT EXISTS `fax_inbound` (
  id                      BIGINT AUTO_INCREMENT,
  fax_account_id          BIGINT,
  document_no             int(20),
  created_at              datetime NOT NULL,
  sent_from               VARCHAR(32),
  external_account_id     VARCHAR(255),
  external_account_type   VARCHAR(32),
  external_reference_id   BIGINT,
  PRIMARY KEY (`id`)
);

-- add a table for tracking outbound faxes
CREATE TABLE IF NOT EXISTS `fax_outbound` (
  id                      BIGINT AUTO_INCREMENT,
  fax_account_id          BIGINT,
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
  external_reference_id   BIGINT,
  PRIMARY KEY (`id`)
);
