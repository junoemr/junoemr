-- creates a table for tracking recent patient access
CREATE TABLE `provider_recent_demographic_access` (
  provider_no       INT(10)    NOT NULL,
  demographic_no    INT(10)    NOT NULL,
  access_datetime   DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`provider_no`, `demographic_no`),
  INDEX `idx_last_access_datetime` (`access_datetime`)
);