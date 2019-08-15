ALTER TABLE reportTemplates ADD COLUMN IF NOT EXISTS super_admin_verified tinyint(1) NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS log_report_by_template (
  id int(10) AUTO_INCREMENT PRIMARY KEY,
  template_id int(10),
  provider_no VARCHAR(6),
  datetime_start DATETIME,
  datetime_end DATETIME,
  rows_returned BIGINT,
  query_string text
);
-- to ensure upgrades from 12 also work
ALTER TABLE log_report_by_template ADD COLUMN IF NOT EXISTS rows_returned BIGINT AFTER datetime_end;
ALTER TABLE log_report_by_template ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS log_report_by_template_explain (
  id int(10) AUTO_INCREMENT PRIMARY KEY,
  `log_report_by_template_id` int(10) NOT NULL,
  `select_type` VARCHAR(32),
  `table` VARCHAR(64),
  `type` VARCHAR(32),
  `possible_keys` text,
  `key` text,
  `key_len` integer(10),
  `ref` VARCHAR(64),
  `rows` INTEGER(10),
  `extra` text,
  FOREIGN KEY log_report_by_template_explain_log_report_by_template_fk(log_report_by_template_id)
  REFERENCES log_report_by_template(id)
);

ALTER TABLE reportByExamples ADD COLUMN IF NOT EXISTS datetime_end DATETIME;
ALTER TABLE reportByExamples ADD COLUMN IF NOT EXISTS rows_returned BIGINT;
ALTER TABLE reportByExamples ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS reportByExamples_explain (
  id int(10) AUTO_INCREMENT PRIMARY KEY,
  `reportByExamples_id` int(10) NOT NULL,
  `select_type` VARCHAR(32),
  `table` VARCHAR(64),
  `type` VARCHAR(32),
  `possible_keys` text,
  `key` text,
  `key_len` integer(10),
  `ref` VARCHAR(64),
  `rows` INTEGER(10),
  `extra` text,
  FOREIGN KEY reportByExamples_explain_reportByExamples_fk(reportByExamples_id)
  REFERENCES reportByExamples(id)
);