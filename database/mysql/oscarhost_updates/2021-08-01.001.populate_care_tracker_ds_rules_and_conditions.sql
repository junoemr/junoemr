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

-- ** time since last check rules **

SET @rule_name = "Warn: Never Entered";
CALL addDsRule(@rule_name, "Measurement has never been recorded");
CALL addDsRuleCondition(@rule_name, "NEVER_GIVEN", NULL);
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Never Entered");

SET @rule_name = "Warn: Over 3 months since last entry";
CALL addDsRule(@rule_name, "Measurement hasn't been recorded in over 3 months");
CALL addDsRuleCondition(@rule_name, "MONTHS_SINCE_GT", "3");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Last check was over 3 months ago");

SET @rule_name = "Warn: Over 6 months since last entry";
CALL addDsRule(@rule_name, "Measurement hasn't been recorded in over 6 months");
CALL addDsRuleCondition(@rule_name, "MONTHS_SINCE_GT", "6");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Last check was over 6 months ago");

SET @rule_name = "Warn: Over 12 months since last entry";
CALL addDsRule(@rule_name, "Measurement hasn't been recorded in over 12 months");
CALL addDsRuleCondition(@rule_name, "MONTHS_SINCE_GT", "12");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Last check was over 12 months ago");

SET @rule_name = "Note: 3-6 months since last entry";
CALL addDsRule(@rule_name, "Measurement hasn't been recorded in over 3 months");
CALL addDsRuleCondition(@rule_name, "MONTHS_SINCE_GT", "3");
CALL addDsRuleCondition(@rule_name, "MONTHS_SINCE_LT", "6");
CALL addDsRuleConsequence(@rule_name, "ALERT", "RECOMMENDATION", "Last check was over 3 months ago");

-- ** patient data based rules **

SET @rule_name = "Visible for Female Patients Only";
CALL addDsRule(@rule_name, "Visible for Female Patients Only");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_NE", "F");
CALL addDsRuleConsequence(@rule_name, "HIDDEN", "RECOMMENDATION", NULL);

SET @rule_name = "Visible for Male Patients Only";
CALL addDsRule(@rule_name, "Visible for Male Patients Only");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_NE", "M");
CALL addDsRuleConsequence(@rule_name, "HIDDEN", "RECOMMENDATION", NULL);

-- ** generic value entered rules & indicators **

SET @rule_name = "Warn: Number Greater Than 0";
CALL addDsRule(@rule_name, "Any numeric value greater than 0");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "0");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be over 0");

SET @rule_name = "Warn: Number Greater Than 2";
CALL addDsRule(@rule_name, "Any numeric value greater than 2");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "2");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be over 2");

SET @rule_name = "Warn: Number Greater Than 4";
CALL addDsRule(@rule_name, "Any numeric value greater than 4");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "4");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be over 4");

SET @rule_name = "Warn: Number Greater Than 5";
CALL addDsRule(@rule_name, "Any numeric value greater than 5");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "5");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be over 5");

SET @rule_name = "Warn: Number Less Than 5";
CALL addDsRule(@rule_name, "Any numeric value less than 5");
CALL addDsRuleCondition(@rule_name, "VALUE_LT", "5");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be under 5");

SET @rule_name = "Warn: Number Greater Than 7";
CALL addDsRule(@rule_name, "Any numeric value greater than 7");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "7");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be over 7");

SET @rule_name = "Warn: Number Less Than 7";
CALL addDsRule(@rule_name, "Any numeric value less than 7");
CALL addDsRuleCondition(@rule_name, "VALUE_LT", "7");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be under 7");

SET @rule_name = "Warn: Number Greater Than 10";
CALL addDsRule(@rule_name, "Any numeric value greater than 10");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "10");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Measurement value recorded to be over 10");


 -- ** Measurement specific rules **

SET @rule_name = "Problem indicator checked";
CALL addDsRule(@rule_name, "Indicates whether a possibly problematic measurement has been checked off");
CALL addDsRuleCondition(@rule_name, "VALUE_EQ", "Yes");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Problem indicator checked");

SET @rule_name = "BMI low indicator";
CALL addDsRule(@rule_name, "Indicates an abnormally low BMI value");
CALL addDsRuleCondition(@rule_name, "VALUE_LT", "18.5");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Body Mass Index is low (under 18.5)");

SET @rule_name = "BMI high indicator";
CALL addDsRule(@rule_name, "Indicates an abnormally high BMI value");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "24.9");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Body Mass Index is high (over 24.9)");

SET @rule_name = "WAIS high indicator (male)";
CALL addDsRule(@rule_name, "Indicates an abnormally high male Waist Circumference value");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "102");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_EQ", "M");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Waist Circumference is high (over 102)");
SET @rule_name = "WAIS high indicator (female)";
CALL addDsRule(@rule_name, "Indicates an abnormally high female Waist Circumference value");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "88");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_EQ", "F");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Waist Circumference is high (over 88)");

SET @rule_name = "WHR high indicator (male)";
CALL addDsRule(@rule_name, "Indicates an abnormally high male Waist Hip Ratio value");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "0.9");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_EQ", "M");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Waist Hip Ratio is high (over 0.9)");
SET @rule_name = "WHR high indicator (female)";
CALL addDsRule(@rule_name, "Indicates an abnormally high female Waist Hip Ratio value");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "0.85");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_EQ", "F");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Waist Hip Ratio is high (over 0.85)");

SET @rule_name = "ACR high indicator (male)";
CALL addDsRule(@rule_name, "Indicates an abnormally high male Alb creat ratio value");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "2.0");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_EQ", "M");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Alb creat ratio is high (over 2.0)");
SET @rule_name = "ACR high indicator (female)";
CALL addDsRule(@rule_name, "Indicates an abnormally high female Alb creat ratio value");
CALL addDsRuleCondition(@rule_name, "VALUE_GT", "2.8");
CALL addDsRuleCondition(@rule_name, "PATIENT_GENDER_EQ", "F");
CALL addDsRuleConsequence(@rule_name, "ALERT", "WARNING", "Alb creat ratio is high (over 2.8)");

COMMIT;
