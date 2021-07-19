START TRANSACTION;

INSERT INTO `care_tracker` (care_tracker_name, description, system_managed, enabled, created_at, created_by, updated_at, updated_by)
SELECT
	"Ocean" AS care_tracker_name,
	"Ocean Measurement Updates" AS description,
	1 AS system_managed,
	0 AS enabled,
	NOW() AS created_at,
	"-1" AS created_by,
	NOW() AS updated_at,
	"-1" AS updated_by
	WHERE "Ocean" NOT IN (
    SELECT care_tracker_name
    FROM care_tracker
    WHERE care_tracker_name = "Ocean"
    AND system_managed IS TRUE
);

INSERT INTO care_tracker_item_group(care_tracker_id, group_name, description, created_at, created_by, updated_at, updated_by)
SELECT
	id,
	"Ocean",
	"Measurement group for Ocean integration",
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker f
WHERE f.care_tracker_name="Ocean"
AND f.system_managed IS TRUE
AND f.id NOT IN (SELECT care_tracker_id FROM care_tracker_item_group);

-- groups:

-- text // STRING
INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"STRING",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Blood Pressure"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN ("BP")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"STRING",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="No Validations"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN ("HSMG", "FRAM", "HSMC", "FOBF", "MAMF", "HRMS", "SMCP", "FLUF", "SmkD", "CIMF", "CODC", "PAPF")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);


INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"NUMERIC",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Numeric Value: 0 to 10"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN ("LDL", "TCHL", "WHR", "HDL", "Kpl", "CERV", "HPCG")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"NUMERIC",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Numeric Value: 0 to 50"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN ("TEMP", "G", "A1C", "FBS", "P", "TCHD", "TRIG", "HYPE", "24UR", "MACA", "TG", "POSK")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);


INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"NUMERIC",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Numeric Value: 0 to 100"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN ("CD4P", "HEAD", "SmkS", "TSH", "BMI", "5DAA", "RESP", "EGFR", "AST", "02", "02SA")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"NUMERIC",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Numeric Value: 0 to 300"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN
      ("QDSH", "COUM", "NDIS", "Clpl", "OSWP", "ALB", "NDIP", "INR", "WAIS", "Napl",
       "DRPW", "ALT", "ACR", "ACR", "HR", "Hb", "HT", "CRCL", "LEFP", "NOSK", "OSWS")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"NUMERIC",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Integer: 1 to 4"
JOIN measurementType type ON type.validation = v.id
WHERE type.type = "NYHA"
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"NUMERIC",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Integer: 1 to 3"
JOIN measurementType type ON type.validation = v.id
WHERE type.type = "DTYP"
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"NUMERIC",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Numeric Value greater than or equal to 0"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN
      ("FEV1", "WT", "TUG", "ASYM", "ANR", "CD4", "CK", "FTST", "SCR",
       "SPIR", "PEFR", "VLOA", "HIP", "24UA", "ALP", "Exer", "VB12", "ANSY")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"BOOLEAN",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Yes/No/NA"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN
      ("DIGT", "AELV", "MedN", "EDGI", "ASAU", "HFMS", "HpAI", "RPHR", "CEDW", "IART", "ExeC", "PANE",
       "DEPR", "STRE", "PIDU", "MCCE", "HpCA", "FTLS", "DT2", "USSH", "SKST", "INSL", "HFMO", "FEET",
       "DIFB", "PVD", "AEDR", "MedG", "FTUL", "EDF", "SMCS", "COPS", "ARMA", "LcCt", "LMED", "HLA",
       "DMSM", "RETI", "CEDS", "HYPM", "EXE", "SmkF", "DARB", "ASWA", "HpBS", "FTIs", "DT1", "URBH",
       "SEXF", "Ang", "HFMH", "SSEX", "PHIN", "DIET", "PsyC", "CASA", "ACS", "MedA", "EDDD", "COPM",
       "ARDT", "OUTR", "HIVG", "DMME", "CEDM", "AIDU", "Hchl", "EPR", "CXR", "ASTA", "LHAD", "BMED",
       "MACC", "HPBP", "FTIn", "UHTP", "NtrC", "iHyp", "HFMD", "FAHS", "DIER", "SUO2", "PSPA", "MCCS",
       "FTRe", "SMCD", "COPE", "ARAD", "OthC", "JVPE", "FICO", "DMED", "TOXP", "REBG", "CEDE", "AHGM",
       "MI", "HTN", "CVD", "LETH", "BG", "HPBC", "FTEx", "DRCO", "UDUS", "RVTN", "CGSD", "ALPA", "NOVS",
       "iEx", "HFCS", "EYEE", "SOHF", "PEDE", "SUAB", "PRRF", "ACOS", "MCCO", "HPCP", "FTOt", "ECG",
       "VDRL", "SmCC", "AORA", "OTCO", "iRef", "DiaC", "FGLC", "DM", "RABG", "CDMP", "AENC", "MedR",
       "G6PD", "EDND", "SmkA", "ASPR", "HFMT", "LUCR", "HpBA", "FTE", "DpSc", "UAIP", "RPPT", "iDia",
       "HFCG", "SODI", "BCTR", "StSc", "PPD", "AACP", "MCCN", "FTNe", "SMBG", "CMVI", "iOth", "DESM")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"BOOLEAN",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Yes/No/X"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN  ("SMK", "ALC")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
SELECT
	sheet.id,
	item_group.id,
	type.typeDisplayName,
	"MEASUREMENT",
	type.type,
	"DATE",
	NULL,
	0,
	"Number >= 0" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN validations v ON v.name="Date"
JOIN measurementType type ON type.validation = v.id
WHERE type.type IN  ("LMP", "DOLE", "EDC", "SmkC")
AND item_group.group_name = "Ocean"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

COMMIT;
