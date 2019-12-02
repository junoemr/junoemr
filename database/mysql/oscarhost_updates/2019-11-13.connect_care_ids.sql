
# add Connect Care id to provider record.
ALTER TABLE provider ADD COLUMN IF NOT EXISTS alberta_connect_care_id varchar(128);

# add Connect Care ids to clinic record.
ALTER TABLE clinic ADD COLUMN IF NOT EXISTS alberta_connect_care_lab_id varchar(128);
ALTER TABLE clinic ADD COLUMN IF NOT EXISTS alberta_connect_care_department_id varchar(128);

# add Connect Care ids to site record.
ALTER TABLE site ADD COLUMN IF NOT EXISTS alberta_connect_care_lab_id varchar(128);
ALTER TABLE site ADD COLUMN IF NOT EXISTS alberta_connect_care_department_id varchar(128);