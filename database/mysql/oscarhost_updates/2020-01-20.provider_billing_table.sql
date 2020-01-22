
CREATE TABLE IF NOT EXISTS provider_billing (
  id INT PRIMARY KEY AUTO_INCREMENT,
  provider_no VARCHAR(6),
  # BC
  bc_rural_retention_code VARCHAR(10) DEFAULT '00',
  bc_rural_retention_name VARCHAR(256),
  bc_service_location_code VARCHAR(10),
  CONSTRAINT idx_unique_provider_no UNIQUE (provider_no)
);
