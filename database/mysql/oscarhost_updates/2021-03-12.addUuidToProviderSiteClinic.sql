ALTER TABLE clinic ADD COLUMN IF NOT EXISTS uuid varchar(36);
ALTER TABLE provider ADD COLUMN IF NOT EXISTS imd_health_uuid varchar(36);
ALTER TABLE site ADD COLUMN IF NOT EXISTS uuid varchar(36);
