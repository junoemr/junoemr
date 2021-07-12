START TRANSACTION;

INSERT INTO `flowsheet` (flowsheet_name, description, system_managed, enabled, created_at, created_by, updated_at, updated_by)
SELECT
    "Asthma" AS flowsheet_name,
    "Measurements for tracking Asthma",
    1,
    1,
    NOW(),
    "-1",
    NOW(),
    "-1"
WHERE "Asthma" NOT IN (
    SELECT flowsheet_name
    FROM flowsheet
    WHERE flowsheet_name = "Asthma"
    AND system_managed IS TRUE
);

INSERT INTO flowsheet_item_group(flowsheet_id, group_name, description, created_at, created_by, updated_at, updated_by)
SELECT
    id,
    "Asthma Measurements",
    "Measurement group for Asthma related measurements",
    NOW(),
    "-1",
    NOW(),
    "-1"
FROM flowsheet f
WHERE f.flowsheet_name="Asthma"
AND f.system_managed IS TRUE
AND f.id NOT IN (SELECT flowsheet_id FROM flowsheet_item_group);

-- *** BEGIN INSERT flowsheet_item entries ***

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ALPA"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ANR"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ASYM"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ASWA"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ANSY"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "FEV1"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "AELV"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "SPIR"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "PEFR"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);


INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ARAD"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ARMA"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ARDT"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "SMCS"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ASTA"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "AENC"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ACOS"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "AACP"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "AEDR"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "ASPR"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "LHAD"
WHERE item_group.group_name = "Asthma Measurements"
AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

INSERT INTO `flowsheet_item`(flowsheet_id, flowsheet_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
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
FROM flowsheet sheet
JOIN flowsheet_item_group item_group ON sheet.id = item_group.flowsheet_id
JOIN measurementType type ON type.type = "OUTR"
WHERE item_group.group_name = "Asthma Measurements"
  AND type.type NOT IN (
    SELECT item_type_code
    FROM flowsheet_item
    WHERE flowsheet_id=sheet.id AND flowsheet_item_group_id=item_group.id
);

-- *** END INSERT flowsheet_item entries ***

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

INSERT INTO flowsheet_drools
SELECT
    drl.id,
    sheet.id
FROM flowsheet sheet
JOIN drools drl ON drl.drl_file = 'diab.drl'
WHERE sheet.flowsheet_name = "Asthma" AND system_managed IS TRUE
AND sheet.id NOT IN (
    SELECT flowsheet_id
    FROM flowsheet_drools
    WHERE sheet.flowsheet_name = "Asthma" AND system_managed IS TRUE
);

INSERT INTO ds_rule(rule_name, description, system_managed, created_at, created_by, updated_at, updated_by)
SELECT
    "Is Checked" AS rule_name,
    "Indicates whether a possibly problematic measurement has been checked off" AS description,
    TRUE AS system_managed,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
WHERE "Is Checked" NOT IN (
    SELECT rule_name
    FROM ds_rule
    WHERE rule_name = "Is Checked"
    AND system_managed IS TRUE
);

INSERT INTO ds_rule(rule_name, description, system_managed, created_at, created_by, updated_at, updated_by)
SELECT
    "Number Greater Than 0" AS rule_name,
    "Any numeric value greater than 0" AS description,
    TRUE AS system_managed,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
WHERE "Number Greater Than 0" NOT IN (
    SELECT rule_name
    FROM ds_rule
    WHERE rule_name = "Number Greater Than 0"
    AND system_managed IS TRUE
);

INSERT INTO ds_rule(rule_name, description, system_managed, created_at, created_by, updated_at, updated_by)
SELECT
    "Number Greater Than 4" AS rule_name,
    "Any numeric value greater than 4" AS description,
    TRUE AS system_managed,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
WHERE "Number Greater Than 4" NOT IN (
    SELECT rule_name
    FROM ds_rule
    WHERE rule_name = "Number Greater Than 4"
    AND system_managed IS TRUE
);

INSERT INTO ds_rule(rule_name, description, system_managed, created_at, created_by, updated_at, updated_by)
SELECT
    "Never Entered" AS rule_name,
    "Measurement has never been recorded" AS description,
    TRUE AS system_managed,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
WHERE "Never Entered" NOT IN (
    SELECT rule_name
    FROM ds_rule
    WHERE rule_name = "Never Entered"
    AND system_managed IS TRUE
);

-- rule for "haven't entered in more than 6 months"
INSERT INTO ds_rule(rule_name, description, system_managed, created_at, created_by, updated_at, updated_by)
SELECT
    "Not Entered in Over 6 mo" AS rule_name,
    "Measurement hasn't been recorded in over 6 months" AS description,
    TRUE AS system_managed,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
WHERE "Not Entered in Over 6 mo" NOT IN (
    SELECT rule_name
    FROM ds_rule
    WHERE rule_name = "Not Entered in Over 6 mo"
    AND system_managed IS TRUE
);

INSERT INTO ds_rule_condition(ds_rule_id, condition_type, condition_value, created_at, created_by, updated_at, updated_by)
SELECT
    id AS ds_rule_id,
    "VALUE_EQ" AS condition_type,
    "Yes/No" AS condition_value,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
FROM ds_rule
WHERE rule_name = "Is Checked"
AND system_managed IS TRUE
AND id NOT IN (
    SELECT ds_rule_id
    FROM ds_rule_condition
    WHERE condition_type = "VALUE_EQ"
      AND condition_value = "Yes/No"
      AND created_by = "-1"
);

INSERT INTO ds_rule_condition(ds_rule_id, condition_type, condition_value, created_at, created_by, updated_at, updated_by)
SELECT
    id AS ds_rule_id,
    "NEVER_GIVEN" AS condition_type,
    NULL AS condition_value,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
FROM ds_rule
WHERE rule_name = "Never Entered"
AND system_managed IS TRUE
AND id NOT IN (
    SELECT ds_rule_id
    FROM ds_rule_condition
    WHERE condition_type = "NEVER_GIVEN"
      AND condition_value IS NULL
      AND created_by = "-1"
);

INSERT INTO ds_rule_condition(ds_rule_id, condition_type, condition_value, created_at, created_by, updated_at, updated_by)
SELECT
    id AS ds_rule_id,
    "MONTHS_SINCE_GT" AS condition_type,
    "6" AS condition_value,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
FROM ds_rule
WHERE rule_name = "Not Entered in Over 6 mo"
AND system_managed IS TRUE
AND id NOT IN (
    SELECT ds_rule_id
    FROM ds_rule_condition
    WHERE condition_type = "MONTHS_SINCE_GT"
      AND condition_value = "6"
      AND created_by = "-1"
);

INSERT INTO ds_rule_condition(ds_rule_id, condition_type, condition_value, created_at, created_by, updated_at, updated_by)
SELECT
    id AS ds_rule_id,
    "VALUE_GT" AS condition_type,
    "0" AS condition_value,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
FROM ds_rule
WHERE rule_name = "Number Greater Than 0"
AND system_managed IS TRUE
AND id NOT IN (
    SELECT ds_rule_id
    FROM ds_rule_condition
    WHERE condition_type = "VALUE_GT"
      AND condition_value = "0"
      AND created_by = "-1"
);

INSERT INTO ds_rule_condition(ds_rule_id, condition_type, condition_value, created_at, created_by, updated_at, updated_by)
SELECT
    id AS ds_rule_id,
    "VALUE_GT" AS condition_type,
    "4" AS condition_value,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
FROM ds_rule
WHERE rule_name = "Number Greater Than 4"
AND system_managed IS TRUE
AND id NOT IN (
    SELECT ds_rule_id
    FROM ds_rule_condition
    WHERE condition_type = "VALUE_GT"
      AND condition_value = "4"
      AND created_by = "-1"
);

INSERT INTO flowsheet_item_ds_rule(flowsheet_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id,
    items.item_type
FROM flowsheet_item items
JOIN flowsheet sheet ON items.flowsheet_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Is Checked" AND rules.system_managed IS TRUE
WHERE sheet.flowsheet_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("ALPA", "ASWA", "AELV")
AND items.id NOT IN (
    SELECT flowsheet_item_id
    FROM flowsheet_item_ds_rule
);

INSERT INTO flowsheet_item_ds_rule(flowsheet_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM flowsheet_item items
JOIN flowsheet sheet ON items.flowsheet_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Number Greater Than 4" AND rules.system_managed IS TRUE
WHERE sheet.flowsheet_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("ANR", "ASYM")
AND items.id NOT IN (
    SELECT flowsheet_item_id
    FROM flowsheet_item_ds_rule
);

INSERT INTO flowsheet_item_ds_rule(flowsheet_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM flowsheet_item items
JOIN flowsheet sheet ON items.flowsheet_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Number Greater Than 0" AND rules.system_managed IS TRUE
WHERE sheet.flowsheet_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("ANSY")
AND items.id NOT IN (
    SELECT flowsheet_item_id
    FROM flowsheet_item_ds_rule
);

INSERT INTO flowsheet_item_ds_rule(flowsheet_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM flowsheet_item items
JOIN flowsheet sheet ON items.flowsheet_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Never Entered" AND rules.system_managed IS TRUE
WHERE sheet.flowsheet_name = "Asthma"
AND sheet.system_managed IS TRUE
AND items.item_type_code IN ("SPIR")
AND items.id NOT IN (
    SELECT flowsheet_item_id
    FROM flowsheet_item_ds_rule
    JOIN ds_rule rules ON rules.rule_name = "Never Entered" AND rules.system_managed IS TRUE
);

INSERT INTO flowsheet_item_ds_rule(flowsheet_item_id, ds_rule_id)
SELECT
    items.id,
    rules.id
FROM flowsheet_item items
JOIN flowsheet sheet ON items.flowsheet_id = sheet.id
JOIN ds_rule rules ON rules.rule_name = "Not Entered in Over 6 mo" AND rules.system_managed IS TRUE
WHERE sheet.flowsheet_name = "Asthma"
  AND sheet.system_managed IS TRUE
  AND items.item_type_code IN ("SPIR")
  AND items.id NOT IN (
    SELECT flowsheet_item_id
    FROM flowsheet_item_ds_rule
    JOIN ds_rule rules_inner ON rules_inner.rule_name = "Not Entered in Over 6 mo" AND rules_inner.system_managed IS TRUE
    WHERE rules_inner.id != rules.id
);

INSERT INTO flowsheet_triggers_icd9(flowsheet_id, icd9_id)
SELECT
    f.id,
    i.id
FROM flowsheet f
JOIN icd9 i ON i.icd9 = "493"
WHERE f.flowsheet_name="Asthma"
AND f.system_managed IS TRUE
AND f.id NOT IN (
    SELECT flowsheet_id
    FROM flowsheet_triggers_icd9
    WHERE icd9_id = 5522
);

COMMIT;
