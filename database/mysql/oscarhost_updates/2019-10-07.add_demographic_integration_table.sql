CREATE TABLE IF NOT EXISTS demographic_integration(
  demographic_no int(10) PRIMARY KEY,
  created_at datetime NOT NULL DEFAULT NOW(),
  updated_at datetime NOT NULL DEFAULT NOW(),
  deleted_at datetime,
  integration_type varchar(64) NOT NULL,
  created_by_source varchar(64),
  created_by_remote_id varchar (32),
  remote_id varchar (32),

  CONSTRAINT `demographic_integration_demographic_no_fk`
  FOREIGN KEY(demographic_no) REFERENCES demographic (demographic_no)
  ON DELETE CASCADE ON UPDATE CASCADE
);