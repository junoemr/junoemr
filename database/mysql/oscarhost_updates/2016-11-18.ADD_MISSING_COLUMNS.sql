-- New column to store the result status for docs (a way to set a document as "abnormal")
alter table document add column doc_result_status varchar(1);

-- New column for demographic family doctor
ALTER TABLE demographic ADD COLUMN `family_doctor_2` varchar(80);
ALTER TABLE demographicArchive ADD COLUMN `family_doctor_2` varchar(80);

-- New column for demographic scanned chart
ALTER TABLE demographic ADD COLUMN `scanned_chart` char(1);
ALTER TABLE demographicArchive ADD COLUMN `scanned_chart` char(1);

-- New column to store the requesting client number for labs
alter table hl7TextInfo add column requesting_client_no varchar(20);