START TRANSACTION;

DROP PROCEDURE IF EXISTS addDsRule;
DROP PROCEDURE IF EXISTS addDsRuleCondition;
DROP PROCEDURE IF EXISTS addDsRuleConsequence;

SET @creatorId = "-1"; -- system provider id

DELIMITER //

CREATE PROCEDURE addDsRule(IN in_rule_name varchar(255), IN in_rule_description text)
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO ds_rule(rule_name, description, system_managed, created_at, created_by, updated_at, updated_by)
    SELECT
        in_rule_name AS rule_name,
        in_rule_description AS description,
        TRUE AS system_managed,
        NOW() AS created_at,
        @creatorId AS created_by,
        NOW() AS updated_at,
        @creatorId AS updated_by
    WHERE in_rule_name NOT IN (
        SELECT rule_name
        FROM ds_rule
        WHERE rule_name = in_rule_name
          AND system_managed IS TRUE
    );
END //

CREATE PROCEDURE addDsRuleCondition(IN in_rule_name varchar(255), IN in_condition_type varchar(255), IN in_condition_value varchar(255))
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO ds_rule_condition(ds_rule_id, condition_type, condition_value, created_at, created_by, updated_at, updated_by)
    SELECT
        id AS ds_rule_id,
        in_condition_type AS condition_type,
        in_condition_value AS condition_value,
        NOW() AS created_at,
        @creatorId AS created_by,
        NOW() AS updated_at,
        @creatorId AS updated_by
    FROM ds_rule
    WHERE rule_name = in_rule_name
      AND system_managed IS TRUE
      AND id NOT IN (
        SELECT ds_rule_id
        FROM ds_rule_condition
        WHERE condition_type = in_condition_type
          AND condition_value = in_condition_value
          AND created_by = @creatorId
    );
END //

CREATE PROCEDURE addDsRuleConsequence(IN in_rule_name varchar(255), IN in_consequence_type varchar(255), IN in_consequence_severity varchar(255), IN in_consequence_message text)
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO ds_rule_consequence(ds_rule_id, consequence_type, consequence_severity, consequence_message, created_at, created_by, updated_at, updated_by)
    SELECT
        id,
        in_consequence_type AS consequence_type,
        in_consequence_severity AS consequence_severity,
        in_consequence_message AS consequence_message,
        NOW() AS created_at,
        @creatorId AS created_by,
        NOW() AS updated_at,
        @creatorId AS updated_by
    FROM ds_rule
    WHERE rule_name = in_rule_name
      AND system_managed IS TRUE
      AND id NOT IN (
        SELECT ds_rule_id
        FROM ds_rule_consequence
        WHERE consequence_type = in_consequence_type
          AND consequence_severity = in_consequence_severity
          AND consequence_message = in_consequence_message
          AND created_by = @creatorId
    );
END //

DELIMITER ;

SET @rule_name = "Problem indicator checked";
CALL addDsRule(@rule_name, "Indicates whether a possibly problematic measurement has been checked off");
CALL addDsRuleCondition(@rule_name, "VALUE_EQ", "Yes");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Problem indicator checked");

SET @rule_name = "Number Greater Than 0";
CALL addDsRule(@rule_name, "Any numeric value greater than 0");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "0");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING",  "Measurement value recorded to be over 0");

SET @rule_name = "Number Greater Than 4";
CALL addDsRule(@rule_name, "Any numeric value greater than 4");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "4");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING",  "Measurement value recorded to be over 4");

SET @rule_name = "Never Entered";
CALL addDsRule(@rule_name, "Measurement has never been recorded");
CALL addDsRuleCondition(@rule_name, "NEVER_GIVEN", NULL);
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Never Entered");

SET @rule_name = "Not Entered in Over 6 months";
CALL addDsRule(@rule_name, "Measurement hasn't been recorded in over 6 months");
CALL addDsRuleCondition(@rule_name, "MONTHS_SINCE_GT", "6");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Last check was over 6 months ago");

COMMIT;
