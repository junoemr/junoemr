
# add Connect Care id to provider record.
ALTER TABLE provider ADD COLUMN IF NOT EXISTS alberta_connect_care_id varchar(128);
