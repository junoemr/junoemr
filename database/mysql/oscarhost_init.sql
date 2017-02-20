update program set defaultServiceRestrictionDays = 0 where defaultServiceRestrictionDays is null;

-- Disable Rich Text Letter
update eform set status = 0 where form_name = "Rich Text Letter" and fid = 2;

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

-- New columns for demographic parent names
ALTER TABLE demographic ADD COLUMN `name_of_mother` varchar(32);
ALTER TABLE demographicArchive ADD COLUMN `name_of_mother` varchar(32);
ALTER TABLE demographic ADD COLUMN `name_of_father` varchar(32);
ALTER TABLE demographicArchive ADD COLUMN `name_of_father` varchar(32);

-- New columns for demographic veteran number
ALTER TABLE demographic ADD COLUMN `veteran_no` varchar(32);
ALTER TABLE demographicArchive ADD COLUMN `veteran_no` varchar(32);

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

INSERT IGNORE INTO `measurementMap` (loinc_code,ident_code,name,lab_type) VALUES
('14771-0','14771-0','Glucose Fasting','PATHL7'),
('1920-8','1920-8','AST','PATHL7'),
('12195-4','12195-4','Creatinine Clearance 24h Corrected','PATHL7'),
('39469-2','39469-2','LDL Cholesterol','PATHL7'),
('32309-7','32309-7','Chol/HDL (Risk Ratio)','PATHL7'),
('14771-0','-GLU-F','GLUCOSE-FASTING','MDS'),
('1920-8','-AST','ASPARTATE TRANSAMINASE(AST)','MDS'),
('12195-4','-CCL','CREATININE CLEARANCE','MDS'),
('39469-2','-LDL','LDL CHOLESTEROL(CALCULATED)','MDS'),
('32309-7','-HDLRAT','CHOLESTEROL/HDL RATIO','MDS'),
('X100666','-VDRL','PUBLIC HEALTH TEST','MDS'),
('14771-0','601.1200','Glucose Fasting','IHA'),
('1920-8','602.0200','AST','IHA'),
('32309-7','602.2400','Chol/HDL Ratio','IHA'),
('39469-2','LDLM','LDL CHOLESTEROL CALC.','GDML'),
('32309-7','R/RM','TC/HDL-C RATIO','GDML'),
('14771-0','111G','GLUCOSE SERUM FASTING','GDML'),
('1920-8','222M','AST','GDML'),
("1920-8","AST","AST","FLOWSHEET"),
("12195-4","CRCL","Creatinine Clearance","FLOWSHEET"),
("14771-0","FBS","FBS","FLOWSHEET"),
("39469-2","LDL","LDL","FLOWSHEET"),
("32309-7","TCHD","TC/HDL","FLOWSHEET"),
("X100666","VDRL","VDRL","FLOWSHEET"),
('39469-2','602.2300','LDL Cholesterol','IHA'),
('25386-4','220.0450','Creatinine Fluid','IHA'),
('39469-2','260.0300','Creatinine POC','IHA'),
('14682-9','601.3400','Creatinine','IHA'),
('14684-5','720.1500','Creatinine-24h Urine','IHA'),
('14683-7','730.1100','Creatinine-Random Urine','IHA'),
('14684-5','730.1150','Albumin/Creatinine Ratio-R Ur','IHA'),
('34366-5','730.1220','Protein/Creatinine Ratio-R Ur','IHA');

create index hl7TextInfo_AccessionNum on hl7TextInfo (accessionNum);

-- 2015-08-19-OHSUPPORT-2444.sql
-- indexes for query performance
CREATE INDEX idx_eform_values_varname_fid_fdid ON eform_values (var_name, fid, fdid);

CREATE INDEX idx_measurementsExt_measurement_id_keyval ON measurementsExt (measurement_id, keyval);

CREATE INDEX idx_drugs_demographic_no_archived ON drugs (demographic_no, archived);

-- 2015-09-01-OHSUPPORT-2362.sql
-- new column for ip address where document was uploaded from
alter table ctl_document add column doc_ip_address varchar(50);

-- 2015-09-09-primary11-performance.sql
-- indexes for query performance
CREATE INDEX idx_program_programStatus ON program (programStatus);

CREATE INDEX idx_admission_program_id_admission_status ON admission (program_id, admission_status);


CREATE INDEX idx_billing_provider_no_billing_no_demographic_no ON billing (provider_no, billing_no, demographic_no);


CREATE INDEX idx_measurements_demographicNo_type_id ON measurements (demographicNo, type, id);


CREATE INDEX idx_casemgmt_tmpsave_provider_no_demographic_no_program_id ON casemgmt_tmpsave (provider_no, demographic_no, program_id);


CREATE INDEX idx_billactivity_updatedatetime_id ON billactivity (updatedatetime, id);


CREATE INDEX idx_casemgmt_note_reporter_caisi_role ON casemgmt_note (reporter_caisi_role);


CREATE INDEX idx_providerLabRouting_provider_no_status_lab_no_lab_type ON providerLabRouting (provider_no, status, lab_no, lab_type);


CREATE INDEX idx_scheduledate_sdate_available_status ON scheduledate (sdate, available, status);


CREATE INDEX idx_dsGuidelines_uuid_status_dateStart ON dsGuidelines (uuid, status, dateStart);


CREATE INDEX idx_demographic_roster_status ON demographic (roster_status);


CREATE INDEX idx_patientLabRouting_demographic_no_lab_type_lab_no ON patientLabRouting (demographic_no, lab_type, lab_no);


CREATE INDEX idx_eChart_demographicNo_eChartId ON eChart (demographicNo, eChartId);


CREATE INDEX idx_billing_demographic_no_billingtype ON billing (demographic_no, billingtype);


CREATE INDEX idx_messagelisttbl_provider_no_status_remoteLocation_message ON messagelisttbl (provider_no, status, remoteLocation, message);


CREATE INDEX idx_consultationRequests_demographicNo ON consultationRequests (demographicNo);


CREATE INDEX idx_demographic_patient_status ON demographic (patient_status);


CREATE INDEX idx_secObjPrivilege_objectName_priority ON secObjPrivilege (objectName, priority);


CREATE INDEX idx_demographicArchive_demographic_no ON demographicArchive (demographic_no);


CREATE INDEX idx_scheduledate_sdate_provider_no_status ON scheduledate (sdate, provider_no, status);


CREATE INDEX idx_log_action_provider_no_content_contentId ON log (action, provider_no, content, contentId);

-- OHSUPPORT-3588 - allow multiple routes per provider for alberta labs using new column
ALTER TABLE `provider` ADD COLUMN `e_delivery_ids` text;
ALTER TABLE `providerArchive` ADD COLUMN `e_delivery_ids` text;

-- OHSUPPORT-3682 - add a TAK number filed for alberta instances.
ALTER TABLE `provider` ADD COLUMN `tak_no` VARCHAR(20);
ALTER TABLE `providerArchive` ADD COLUMN `tak_no` VARCHAR(20);

