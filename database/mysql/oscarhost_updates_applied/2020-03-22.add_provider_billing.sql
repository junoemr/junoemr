CREATE TABLE IF NOT EXISTS provider_billing (
  id INT PRIMARY KEY AUTO_INCREMENT,
  # BC
  bc_rural_retention_code VARCHAR(10) DEFAULT '00',
  bc_rural_retention_name VARCHAR(256),
  bc_service_location_code VARCHAR(10),
  bc_bcp_eligible BOOLEAN DEFAULT 0 NOT NULL,
  # ON
  on_master_number VARCHAR(4),
  on_service_location VARCHAR(16),
  # AB
  ab_source_code VARCHAR(2),
  ab_skill_code VARCHAR(16),
  ab_location_code VARCHAR(16),
  ab_BA_number INT,
  ab_facility_number INT,
  ab_functional_center VARCHAR(16),
  ab_time_role_modifier VARCHAR(16),
  # SK
  sk_mode INT,
  sk_location VARCHAR(1),
  sk_submission_type VARCHAR(1),
  sk_corporation_indicator VARCHAR(256)
);

ALTER TABLE provider ADD COLUMN IF NOT EXISTS provider_billing_id INT;
ALTER TABLE provider ADD CONSTRAINT provider_billing_id_fk FOREIGN KEY IF NOT EXISTS (provider_billing_id) REFERENCES provider_billing(id);


# Single site config (BCP eligibility is stored in provider billing)
ALTER TABLE clinic
  ADD COLUMN IF NOT EXISTS bc_facility_number
    VARCHAR(128);

# Multisite config (BCP eligibiliy is now on a per site basis)

ALTER TABLE site
  ADD COLUMN IF NOT EXISTS bc_facility_number
    VARCHAR(128);

ALTER TABLE providersite
  ADD COLUMN IF NOT EXISTS bc_bcp_eligible
    BOOLEAN DEFAULT 0 NOT NULL;
