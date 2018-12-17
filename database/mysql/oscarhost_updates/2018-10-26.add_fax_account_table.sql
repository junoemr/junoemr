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
