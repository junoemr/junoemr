START TRANSACTION;

INSERT INTO `care_tracker` (care_tracker_name, description, system_managed, enabled, created_at, created_by, updated_at, updated_by)
SELECT
    "Asthma" AS care_tracker_name,
    "Measurements for tracking Asthma",
    1,
    1,
    NOW(),
    "-1",
    NOW(),
    "-1"
WHERE "Asthma" NOT IN (
    SELECT care_tracker_name
    FROM care_tracker
    WHERE care_tracker_name = "Asthma"
    AND system_managed IS TRUE
);

INSERT INTO care_tracker_item_group(care_tracker_id, group_name, description, created_at, created_by, updated_at, updated_by)
SELECT
    id,
    "Asthma Measurements",
    "Measurement group for Asthma related measurements",
    NOW(),
    "-1",
    NOW(),
    "-1"
FROM care_tracker f
WHERE f.care_tracker_name="Asthma"
AND f.system_managed IS TRUE
AND f.id NOT IN (SELECT care_tracker_id FROM care_tracker_item_group);

-- *** BEGIN INSERT care_tracker_item entries ***

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
JOIN measurementType type ON type.validation = "14"
WHERE type.type IN ("ANR", "FEV1", "SPIR", "ANSY", "ASYM", "PEFR")
AND item_group.group_name = "Asthma Measurements"
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
	"Yes/No" AS guideline,
	type.typeDescription AS description,
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM care_tracker sheet
JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
JOIN measurementType type ON type.validation = "7"
WHERE type.type IN ("ALPA", "ASWA", "AELV", "ARAD", "ARMA", "ARDT", "SMCS", "ASTA", "AENC", "ACOS", "AACP", "AEDR", "ASPR", "LHAD", "OUTR")
AND item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
	SELECT item_type_code
	FROM care_tracker_item
	WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
);

-- *** END INSERT care_tracker_item entries ***

INSERT INTO drools (drl_file, description, created_at, updated_at)
SELECT
    'diab.drl',
    'diabetes decision support',
    NOW(),
    NOW()
    WHERE 'diab.drl' NOT IN
(
    SELECT drl_file
    FROM drools
    WHERE drl_file = 'diab.drl'
    AND description = 'diabetes decision support'
);

INSERT INTO care_tracker_drools
SELECT
    drl.id,
    sheet.id
FROM care_tracker sheet
JOIN drools drl ON drl.drl_file = 'diab.drl'
WHERE sheet.care_tracker_name = "Asthma" AND system_managed IS TRUE
AND sheet.id NOT IN (
    SELECT care_tracker_id
    FROM care_tracker_drools
    WHERE sheet.care_tracker_name = "Asthma" AND system_managed IS TRUE
);


INSERT INTO care_tracker_item_ds_rule(care_tracker_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM care_tracker_item items
JOIN care_tracker sheet ON items.care_tracker_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Is Checked" AND rules.system_managed IS TRUE
WHERE sheet.care_tracker_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("ALPA", "ASWA", "AELV")
AND items.id NOT IN (
    SELECT care_tracker_item_id
    FROM care_tracker_item_ds_rule
);

INSERT INTO care_tracker_item_ds_rule(care_tracker_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM care_tracker_item items
JOIN care_tracker sheet ON items.care_tracker_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Number Greater Than 4" AND rules.system_managed IS TRUE
WHERE sheet.care_tracker_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("ANR", "ASYM")
AND items.id NOT IN (
    SELECT care_tracker_item_id
    FROM care_tracker_item_ds_rule
);

INSERT INTO care_tracker_item_ds_rule(care_tracker_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM care_tracker_item items
JOIN care_tracker sheet ON items.care_tracker_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Number Greater Than 0" AND rules.system_managed IS TRUE
WHERE sheet.care_tracker_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("ANSY")
AND items.id NOT IN (
    SELECT care_tracker_item_id
    FROM care_tracker_item_ds_rule
);

INSERT INTO care_tracker_item_ds_rule(care_tracker_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM care_tracker_item items
JOIN care_tracker sheet ON items.care_tracker_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Never Entered" AND rules.system_managed IS TRUE
WHERE sheet.care_tracker_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("SPIR")
AND items.id NOT IN (
    SELECT care_tracker_item_id
    FROM care_tracker_item_ds_rule
    JOIN ds_rule rules ON rules.rule_name = "Never Entered" AND rules.system_managed IS TRUE
);

INSERT INTO care_tracker_item_ds_rule(care_tracker_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM care_tracker_item items
JOIN care_tracker sheet ON items.care_tracker_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Not Entered in Over 6 mo" AND rules.system_managed IS TRUE
WHERE sheet.care_tracker_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("SPIR")
AND items.id NOT IN (
    SELECT care_tracker_item_id
    FROM care_tracker_item_ds_rule
    JOIN ds_rule rules_inner ON rules_inner.rule_name = "Not Entered in Over 6 mo" AND rules_inner.system_managed IS TRUE
    WHERE rules_inner.id != rules.id
);

INSERT INTO care_tracker_triggers_icd9(care_tracker_id, icd9_id)
SELECT
    f.id,
    i.id
FROM care_tracker f
JOIN icd9 i ON i.icd9 = "493"
WHERE f.care_tracker_name="Asthma"
AND f.system_managed IS TRUE
AND f.id NOT IN (
    SELECT care_tracker_id
    FROM care_tracker_triggers_icd9
	JOIN icd9 icd_inner ON icd_inner.icd9 = "493"
    WHERE icd9_id = icd_inner.id
);

COMMIT;
