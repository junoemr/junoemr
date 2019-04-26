ALTER TABLE demographic ADD COLUMN IF NOT EXISTS name_of_mother varchar(32) AFTER `family_doctor_2`;
ALTER TABLE demographic ADD COLUMN IF NOT EXISTS name_of_father varchar(32) AFTER `name_of_mother`;
