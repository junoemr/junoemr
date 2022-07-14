ALTER TABLE casemgmt_tmpsave ADD COLUMN IF NOT EXISTS observation_date DATETIME;
ALTER TABLE casemgmt_tmpsave ADD COLUMN IF NOT EXISTS encounter_type VARCHAR(100);