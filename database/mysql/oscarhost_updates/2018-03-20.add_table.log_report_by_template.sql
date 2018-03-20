
CREATE TABLE log_report_by_template (
  id int(10) AUTO_INCREMENT PRIMARY KEY,
  template_id int(10),
  provider_no VARCHAR(6),
  datetime_start DATETIME,
  datetime_end DATETIME,
  query_string text
);
