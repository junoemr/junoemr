# Normal configs

ALTER TABLE clinic
  ADD COLUMN IF NOT EXISTS bc_facility_number
    VARCHAR(128);

ALTER TABLE provider_billing
  ADD COLUMN IF NOT EXISTS bc_bcp_eligible
    BOOLEAN
    DEFAULT 0
    AFTER bc_service_location_code;

# Multisite config

ALTER TABLE site
  ADD COLUMN IF NOT EXISTS bc_facility_number
    VARCHAR(128);

ALTER TABLE providersite
  ADD COLUMN IF NOT EXISTS bc_bcp_eligible
    BOOLEAN
    DEFAULT 0;