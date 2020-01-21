# BC
ALTER TABLE provider ADD COLUMN IF NOT EXISTS bc_rural_retention_code VARCHAR(10) DEFAULT "00";
ALTER TABLE provider ADD COLUMN IF NOT EXISTS bc_rural_retention_name VARCHAR(256);
ALTER TABLE provider ADD COLUMN IF NOT EXISTS bc_service_location_code VARCHAR(10);