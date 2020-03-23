
ALTER TABLE site
  ADD COLUMN IF NOT EXISTS bc_facility_number
    VARCHAR(128);

ALTER TABLE providersite
  ADD COLUMN IF NOT EXISTS bc_bcp_eligible
    BOOLEAN
    DEFAULT 0;

