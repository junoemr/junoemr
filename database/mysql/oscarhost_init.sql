update program set defaultServiceRestrictionDays = 0 where defaultServiceRestrictionDays is null; 

-- Enable Rich Text Letter
update eform set status = 1 where form_name = "Rich Text Letter" and fid = 2;

-- Makes Lab Uploads faster
create index measurementVal_index ON measurementsExt(val(5), keyval);

-- Make the demographic_merged_full table for doing full demographic merges
create table demographic_merged_full (
	provider_no int,
	to_demographic_no int,
	from_demographic_no int,
	created_tstamp timestamp
);

-- Indexes
create index scheduledate_sdate on scheduledate(sdate);
create index scheduledate_provider_no on scheduledate (provider_no);
create index scheduledate_status on scheduledate (status);
create index scheduledate_key1 on scheduledate (sdate,provider_no,hour,status);
create index secObjPrivilege_objectName on secObjPrivilege (objectName);
create index appointment_date_provider on appointment ( appointment_date, provider_no);
create index appointment_status_active on appointment_status(active);
create index eform_form_name on eform(form_name);


-- New column to store the requesting client number for labs
alter table hl7TextInfo add column requesting_client_no varchar(20);

-- New column to store the result status for docs (a way to set a document as "abnormal")
alter table document add column doc_result_status varchar(1);

-- New column for demographic family doctor
ALTER TABLE demographic ADD COLUMN `family_doctor_2` varchar(80);
ALTER TABLE demographicArchive ADD COLUMN `family_doctor_2` varchar(80);

-- New column for demographic scanned chart
ALTER TABLE demographic ADD COLUMN `scanned_chart` char(1);
ALTER TABLE demographicArchive ADD COLUMN `scanned_chart` char(1);

ALTER TABLE measurementMap ADD COLUMN flowsheet varchar(15);
UPDATE measurementMap SET flowsheet = 'FLOWSHEET' WHERE lab_type = "FLOWSHEET";
UPDATE measurementMap SET flowsheet = CONCAT(id,loinc_code) WHERE lab_type != "FLOWSHEET";
create unique index flowsheet_loinc on measurementMap(loinc_code, lab_type, flowsheet);
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES ("4548-4","A1C","A1C","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("9318-7","ACR","Alb creat ratio","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("1742-6","ALT","ALT","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("1920-8","AST","AST","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("14682-9","SCR","Creatinine","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("12195-4","CRCL","Creatinine Clearance","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("33914-3","EGFR","EGFR","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("14771-0","FBS","FBS","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("14914-6","FTST","Free Testost","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("14646-4","HDL","HDL","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("5193-8","HpBA","Hep BS Ab","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("5196-1","HpBS","Hep BS Ag","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("X50060","HpCA","Hep C Ab","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("6301-6","INR","INR","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("39469-2","LDL","LDL","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("22748-8","LDL","LIPIDS LDL","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("9322-9","TCHD","LIPIDS TC/HDL","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("14927-8","TG","LIPIDS TG","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("32309-7","TCHD","TC/HDL","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("14647-2","TCHL","Total Cholestorol","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("X100666","VDRL","VDRL","FLOWSHEET","FLOWSHEET");
INSERT IGNORE INTO measurementMap(loinc_code,ident_code,name,lab_type,flowsheet) VALUES("14685-2","VB12","Vit B12","FLOWSHEET","FLOWSHEET");
drop index flowsheet_loinc on measurementMap;
ALTER TABLE measurementMap DROP COLUMN flowsheet;
