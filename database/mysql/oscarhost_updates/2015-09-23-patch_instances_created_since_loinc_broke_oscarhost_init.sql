
-- NOTE: This can be run on all servers and should fail on the first line
-- if the server has already run this code
-- new column for ip address where document was uploaded from

create index hl7TextInfo_AccessionNum on hl7TextInfo (accessionNum);

CREATE INDEX idx_eform_values_varname_fid_fdid ON eform_values (var_name, fid, fdid);

CREATE INDEX idx_measurementsExt_measurement_id_keyval ON measurementsExt (measurement_id, keyval);

CREATE INDEX idx_drugs_demographic_no_archived ON drugs (demographic_no, archived);

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

