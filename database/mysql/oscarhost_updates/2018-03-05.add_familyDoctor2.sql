-- New column for demographic family doctor
ALTER TABLE demographic ADD COLUMN `family_doctor_2` varchar(80);
ALTER TABLE demographicArchive ADD COLUMN `family_doctor_2` varchar(80);