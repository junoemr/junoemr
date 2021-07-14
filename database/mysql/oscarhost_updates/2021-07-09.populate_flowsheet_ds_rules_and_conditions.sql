START TRANSACTION;

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

INSERT INTO ds_rule(rule_name, description, system_managed, created_at, created_by, updated_at, updated_by)
SELECT
    "Not Entered in Over 6 months" AS rule_name,
    "Measurement hasn't been recorded in over 6 months" AS description,
    TRUE AS system_managed,
    NOW() AS created_at,
    "-1" AS created_by,
    NOW() AS updated_at,
    "-1" AS updated_by
WHERE "Not Entered in Over 6 months" NOT IN (
    SELECT rule_name
    FROM ds_rule
    WHERE rule_name = "Not Entered in Over 6 months"
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
WHERE rule_name = "Not Entered in Over 6 months"
AND system_managed IS TRUE
AND id NOT IN (
    SELECT ds_rule_id
    FROM ds_rule_condition
    WHERE condition_type = "MONTHS_SINCE_GT"
    AND condition_value = "6"
    AND created_by = "-1"
);

INSERT INTO ds_rule_consequence(ds_rule_id, consequence_type, consequence_severity, consequence_message, created_at, created_by, updated_at, updated_by)
SELECT
	id,
    "ALERT",
    "WARNING",
    "Last check was over 6 months ago",
    NOW(),
    "-1",
    NOW(),
    "-1"
FROM ds_rule
WHERE rule_name = "Not Entered in Over 6 months"
AND system_managed IS TRUE
AND id NOT IN (
    SELECT ds_rule_id
	FROM ds_rule_consequence
	WHERE consequence_type = "ALERT"
    AND consequence_severity = "WARNING"
	AND consequence_message = "Last check was over 6 months ago"
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

INSERT INTO ds_rule_consequence(ds_rule_id, consequence_type, consequence_severity, consequence_message, created_at, created_by, updated_at, updated_by)
SELECT
	id,
	"ALERT",
	"WARNING",
	"Measurement value recorded to be over 0",
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM ds_rule
WHERE rule_name = "Number Greater Than 0"
AND system_managed IS TRUE
AND id NOT IN (
	SELECT ds_rule_id
	FROM ds_rule_consequence
	WHERE consequence_type = "ALERT"
	AND consequence_severity = "WARNING"
	AND consequence_message = "Measurement value recorded to be over 0"
	AND created_by = "-1"
);

INSERT INTO ds_rule_consequence(ds_rule_id, consequence_type, consequence_severity, consequence_message, created_at, created_by, updated_at, updated_by)
SELECT
	id,
	"ALERT",
	"WARNING",
	"Measurement value recorded to be over 4",
	NOW(),
	"-1",
	NOW(),
	"-1"
FROM ds_rule
WHERE rule_name = "Number Greater Than 4"
AND system_managed IS TRUE
AND id NOT IN (
	SELECT ds_rule_id
	FROM ds_rule_consequence
	WHERE consequence_type = "ALERT"
	AND consequence_severity = "WARNING"
	AND consequence_message = "Measurement value recorded to be over 4"
	AND created_by = "-1"
);

COMMIT;
