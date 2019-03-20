ALTER TABLE demographic ADD COLUMN IF NOT EXISTS nameOfMother varchar(32) AFTER `family_doctor_2`;
ALTER TABLE demographic ADD COLUMN IF NOT EXISTS nameOfFather varchar(32) AFTER `nameOfMother`;
