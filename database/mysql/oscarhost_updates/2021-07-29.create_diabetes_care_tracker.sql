START TRANSACTION;

-- add the care tracker
INSERT INTO `care_tracker` (care_tracker_name, description, system_managed, enabled, created_at, created_by, updated_at, updated_by)
SELECT
    "Diabetes" AS care_tracker_name,
    "Measurements for tracking Diabetes",
    1,
    1,
    NOW(),
    "-1",
    NOW(),
    "-1"
WHERE "Diabetes" NOT IN (
    SELECT care_tracker_name
    FROM care_tracker
    WHERE care_tracker_name = "Diabetes"
      AND system_managed IS TRUE
);

-- set up the drools connection
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
      );

INSERT INTO care_tracker_drools
SELECT
    drl.id,
    sheet.id
FROM care_tracker sheet
         JOIN drools drl ON drl.drl_file = 'diab.drl'
WHERE sheet.care_tracker_name = "Diabetes" AND system_managed IS TRUE
  AND sheet.id NOT IN (
    SELECT care_tracker_id
    FROM care_tracker_drools
    WHERE sheet.care_tracker_name = "Diabetes" AND system_managed IS TRUE
);

-- set up icd9 triggers
INSERT INTO care_tracker_triggers_icd9(care_tracker_id, icd9_id)
SELECT
    f.id,
    i.id
FROM care_tracker f
         JOIN icd9 i ON i.icd9 = "250"
WHERE f.care_tracker_name="Diabetes"
  AND f.system_managed IS TRUE
  AND f.id NOT IN (
    SELECT care_tracker_id
    FROM care_tracker_triggers_icd9
             JOIN icd9 icd_inner ON icd_inner.icd9 = "250"
    WHERE icd9_id = icd_inner.id
);
INSERT INTO care_tracker_triggers_icd9(care_tracker_id, icd9_id)
SELECT
    f.id,
    i.id
FROM care_tracker f
         JOIN icd9 i ON i.icd9 = "7902"
WHERE f.care_tracker_name="Diabetes"
  AND f.system_managed IS TRUE
  AND f.id NOT IN (
    SELECT care_tracker_id
    FROM care_tracker_triggers_icd9
             JOIN icd9 icd_inner ON icd_inner.icd9 = "7902"
    WHERE icd9_id = icd_inner.id
);


DROP PROCEDURE IF EXISTS careTrackerAddItemGroupProcedure;
DROP PROCEDURE IF EXISTS careTrackerAddItemProcedure;

DELIMITER //

CREATE PROCEDURE careTrackerAddItemGroupProcedure(
    IN in_care_tracker_name varchar(255), IN in_group_name varchar(255), IN in_description text)
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO care_tracker_item_group(care_tracker_id, group_name, description, created_at, created_by, updated_at, updated_by)
    SELECT
        id,
        in_group_name,
        in_description,
        NOW() AS created_at,
        "-1" AS created_by,
        NOW() AS updated_at,
        "-1" AS updated_by
    FROM care_tracker f
    WHERE f.care_tracker_name=in_care_tracker_name
      AND f.system_managed IS TRUE
      AND f.id NOT IN (SELECT care_tracker_id FROM care_tracker_item_group g WHERE g.group_name = in_group_name);
END //


CREATE PROCEDURE careTrackerAddItemProcedure(
  IN in_group_name varchar(255), IN in_item_type varchar(255), IN in_item_code varchar(255), IN in_value_type varchar(255), IN in_value_label varchar(255), IN in_graphable tinyint(1), IN in_guideline text)
    SQL SECURITY INVOKER
BEGIN
    IF in_item_type = 'PREVENTION' THEN
        INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
        SELECT
            sheet.id,
            item_group.id,
            in_item_code,
            in_item_type,
            in_item_code,
            in_value_type,
            in_value_label,
            in_graphable,
            in_guideline AS guideline,
            NULL AS description,
            NOW() AS created_at,
            "-1" AS created_by,
            NOW() AS updated_at,
            "-1" AS updated_by
        FROM care_tracker sheet
                 JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
        WHERE item_group.group_name = in_group_name
          AND in_item_code NOT IN (
            SELECT item_type_code
            FROM care_tracker_item
            WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
        ) limit 1;
    ELSE
        INSERT INTO `care_tracker_item`(care_tracker_id, care_tracker_item_group_id, item_name, item_type, item_type_code, value_type, value_label, graphable, guideline, description, created_at, created_by, updated_at, updated_by)
        SELECT
            sheet.id,
            item_group.id,
            type.typeDisplayName,
            in_item_type,
            type.type,
            in_value_type,
            in_value_label,
            in_graphable,
            in_guideline AS guideline,
            type.typeDescription AS description,
            NOW() AS created_at,
            "-1" AS created_by,
            NOW() AS updated_at,
            "-1" AS updated_by
        FROM care_tracker sheet
                 JOIN care_tracker_item_group item_group ON sheet.id = item_group.care_tracker_id
                 JOIN measurementType type
        WHERE type.type IN (in_item_code)
          AND item_group.group_name = in_group_name
          AND type.type NOT IN (
            SELECT item_type_code
            FROM care_tracker_item
            WHERE care_tracker_id=sheet.id AND care_tracker_item_group_id=item_group.id
        ) limit 1;
    END IF;
END //

DELIMITER ;

-- *** Vitals measurements group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Vitals", "Diabetes related vital measurements");
CALL careTrackerAddItemProcedure("Vitals", "MEASUREMENT", "BP", "STRING", NULL, 0, "Target > 130/80");
CALL careTrackerAddItemProcedure("Vitals", "MEASUREMENT", "WT", "NUMERIC", "Weight", 1, "Weight in kg");
CALL careTrackerAddItemProcedure("Vitals", "MEASUREMENT", "HT", "NUMERIC", "Height", 1, "Height in cm");
CALL careTrackerAddItemProcedure("Vitals", "MEASUREMENT", "BMI", "NUMERIC", "BMI", 1, "Target: 18.5 - 24.9 (kg/m^2)");
CALL careTrackerAddItemProcedure("Vitals", "MEASUREMENT", "WAIS", "NUMERIC", "Waist Circ", 1, NULL);
CALL careTrackerAddItemProcedure("Vitals", "MEASUREMENT", "WHR", "NUMERIC", "Waist to Hip Ratio", 1, NULL);


-- *** Cardiovascular measurements groups ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Cardiovascular - Lipids", "Lipids related cardio measurements");
CALL careTrackerAddItemProcedure("Cardiovascular - Lipids", "MEASUREMENT", "HDL", "NUMERIC", "HDL", 1, NULL);
CALL careTrackerAddItemProcedure("Cardiovascular - Lipids", "MEASUREMENT", "LDL", "NUMERIC", "LDL", 1, "LDL < 2.0");
CALL careTrackerAddItemProcedure("Cardiovascular - Lipids", "MEASUREMENT", "TG", "NUMERIC", "Triglycerides", 1, "Target: < 2.0 mmol/L");
CALL careTrackerAddItemProcedure("Cardiovascular - Lipids", "MEASUREMENT", "TCHD", "NUMERIC", "TC/HDL", 1, "Ratio < 4.0");
CALL careTrackerAddItemProcedure("Cardiovascular - Lipids", "MEASUREMENT", "EDNL", "BOOLEAN", "Completed", 0, "Education Nutrition");

CALL careTrackerAddItemGroupProcedure("Diabetes", "Cardiovascular - Smoking", "Smoking related cardio measurements");
CALL careTrackerAddItemProcedure("Cardiovascular - Smoking", "MEASUREMENT", "SKST", "BOOLEAN", "Smoker", 0, NULL);
CALL careTrackerAddItemProcedure("Cardiovascular - Smoking", "MEASUREMENT", "POSK", "NUMERIC", "Packs per day", 1, NULL);
CALL careTrackerAddItemProcedure("Cardiovascular - Smoking", "MEASUREMENT", "MCCS", "BOOLEAN", "Completed", 0, NULL);

CALL careTrackerAddItemGroupProcedure("Diabetes", "Cardiovascular - Other", "Other related cardio measurements");
CALL careTrackerAddItemProcedure("Cardiovascular - Other", "MEASUREMENT", "ECG", "STRING", "ECG", 0, NULL);
CALL careTrackerAddItemProcedure("Cardiovascular - Other", "MEASUREMENT", "PSSC", "BOOLEAN", "Screened", 0, NULL);
CALL careTrackerAddItemProcedure("Cardiovascular - Other", "MEASUREMENT", "STRE", "BOOLEAN", "Stress Testing", 0, NULL);

-- *** Glycemic measurements group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Glycemic", "Glycemic measurements");
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "DTYP", "STRING", "type", 0, "1 or 2");
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "A1C", "NUMERIC", "A1C", 1, "Target > 7.0%");
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "FBS", "NUMERIC", "Fasting Plasma Glucose", 1, NULL);
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "FGLC", "BOOLEAN", "Within 20%", 0, "Meter within 20% of simultaneous lab values");
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "FBPC", "NUMERIC", "2 hr PC BG", 1, NULL);
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "SMBG", "NUMERIC", "Self Monitoring BG", 1, NULL);
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "HYPM", "BOOLEAN", "Reviewed", 0, "discussed");
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "HYPE", "NUMERIC", "# of episodes", 1, "Since last assessed");
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "MCCN", "BOOLEAN", "Completed", 0, NULL);
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "MCCE", "BOOLEAN", "Completed", 0, NULL);
CALL careTrackerAddItemProcedure("Glycemic", "MEASUREMENT", "SMCD", "STRING", "Challenges", 0, "Self Management Challenges");

-- *** Mental Health measurements group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Mental Health", "Mental health check");
CALL careTrackerAddItemProcedure("Mental Health", "MEASUREMENT", "DEPR", "BOOLEAN", "Depressed", 0, "Yearly or As Needed");
CALL careTrackerAddItemProcedure("Mental Health", "MEASUREMENT", "LETH", "BOOLEAN", "Lethargic", 0, "Yearly or As Needed");

-- *** Nephropathy measurements group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Nephropathy", "Nephropathy measurements");
CALL careTrackerAddItemProcedure("Nephropathy", "MEASUREMENT", "ACR", "NUMERIC", "ACR", 1, "Target: < 2.0 M : < 2.8 F");
CALL careTrackerAddItemProcedure("Nephropathy", "MEASUREMENT", "SCR", "NUMERIC", "Serum Creatinine", 1, NULL);
CALL careTrackerAddItemProcedure("Nephropathy", "MEASUREMENT", "EGFR", "NUMERIC", "eGFR", 1, NULL);
CALL careTrackerAddItemProcedure("Nephropathy", "MEASUREMENT", "AORA", "BOOLEAN", "ACE-I OR ARB", 0, "Yes No");
CALL careTrackerAddItemProcedure("Nephropathy", "MEASUREMENT", "CRCL", "NUMERIC", "Creatinine Clearance", 1, NULL);

-- *** Neuropathy measurements group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Neuropathy", "Neuropathy measurements");
CALL careTrackerAddItemProcedure("Neuropathy", "MEASUREMENT", "AORA", "BOOLEAN", "Present", 0, "Erectile Dysfunction, gastrointestinal disturbance");
CALL careTrackerAddItemProcedure("Neuropathy", "MEASUREMENT", "FTLS", "BOOLEAN", "Normal", 0, "Check for peripheral anesthesia with 10g monofilament or 128 Hz tuning fork");
CALL careTrackerAddItemProcedure("Neuropathy", "MEASUREMENT", "PANE", "BOOLEAN", "Present", 0, NULL);
CALL careTrackerAddItemProcedure("Neuropathy", "MEASUREMENT", "FTE", "BOOLEAN", "Normal", 0, "Foot Care");

CALL careTrackerAddItemProcedure("Neuropathy", "MEASUREMENT", "EDF", "BOOLEAN", "Dysfunction", 0, "Yes No");
CALL careTrackerAddItemProcedure("Neuropathy", "MEASUREMENT", "BCTR", "BOOLEAN", "Birth Control", 0, "On Birth Control");

-- *** Retinopathy measurements group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Retinopathy", "Retinopathy measurements");
CALL careTrackerAddItemProcedure("Retinopathy", "MEASUREMENT", "EYEE", "BOOLEAN", "Exam Done", 0, "Dilated Eye Exam, comment if referred");

-- *** Vaccination preventions group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Vaccination", "Vaccinations");
CALL careTrackerAddItemProcedure("Vaccination", "PREVENTION", "Pneumovax", "BOOLEAN", "Pneumococcal vaccine", 0, NULL);
CALL careTrackerAddItemProcedure("Vaccination", "PREVENTION", "Flu", "BOOLEAN", "Flu Vaccine", 0, "Annually");

-- *** Other measurements group ***
CALL careTrackerAddItemGroupProcedure("Diabetes", "Other", "Other diabetes related measurements");
CALL careTrackerAddItemProcedure("Other", "MEASUREMENT", "DMME", "BOOLEAN", "Discussed", 0, "Assess and discuss self-management challenges");
CALL careTrackerAddItemProcedure("Other", "MEASUREMENT", "EDND", "BOOLEAN", "Completed", 0, NULL);
CALL careTrackerAddItemProcedure("Other", "MEASUREMENT", "MCCO", "BOOLEAN", "Completed", 0, NULL);
CALL careTrackerAddItemProcedure("Other", "MEASUREMENT", "CGSD", "STRING", "Goal", 0, NULL);
CALL careTrackerAddItemProcedure("Other", "MEASUREMENT", "ASAU", "BOOLEAN", "Used", 0, NULL);
CALL careTrackerAddItemProcedure("Other", "MEASUREMENT", "AST", "NUMERIC", "AST", 1, NULL);
CALL careTrackerAddItemProcedure("Other", "MEASUREMENT", "ALT", "NUMERIC", "ALT", 1, NULL);



COMMIT;