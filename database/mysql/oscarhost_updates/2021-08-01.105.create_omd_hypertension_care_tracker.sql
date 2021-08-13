
SET @care_tracker_name = "Hypertension Flowsheet";
SET @creatorId = "-1"; -- system provider id

SET @rule_name_never_entered = "Warn: Never Entered";
SET @rule_name_12m_plus = "Warn: Over 12 months since last entry";
SET @rule_name_24m_plus = "Warn: Over 24 months since last entry";
SET @rule_name_value_over_3.5 = "Warn: Number Greater Than 3.5";
SET @rule_name_value_over_5 = "Warn: Number Greater Than 5";
SET @rule_name_value_over_6 = "Warn: Number Greater Than 6";

START TRANSACTION;

CALL addCareTracker(@care_tracker_name, "Elements for tracking heart failure", TRUE);

-- set up the drools connection
CALL addCareTrackerDrools(@care_tracker_name, "hypertension.drl", "hypertension decision support");

-- set up icd9 triggers
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "401");

-- *** measurements group ***
SET @group_name = "Measurements";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "hypertension related measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FBS", "NUMERIC", "Value", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", @rule_name_value_over_6);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ACR", "NUMERIC", "ACR", "Target: < 2.0 M : < 2.8 F");
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", @rule_name_24m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", "ACR high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", "ACR high indicator (female)");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EGFR", "NUMERIC", "eFGR", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "TCHD", "NUMERIC", "TC/HDL", "Ratio < 4.0");
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_value_over_5);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "LDL", "NUMERIC", "LDL", "LDL < 2.5");
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_value_over_3.5);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "TG", "NUMERIC", "Triglycerides", "Target: < 2.0 mmol/L");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "BP", "STRING", NULL, "Target < 130/80");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SKST", "BOOLEAN", "Smoker", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "POSK", "NUMERIC", "Packs per day", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DIER", "BOOLEAN", "Reviewed", "Proper diet; activity 2.5 hrs/wk");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SODI", "BOOLEAN", "On Low Sodium Diet", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DRPW", "NUMERIC", "# of drinks per week", "Number of Drinks per week");
CALL addCareTrackerItemRule(@care_tracker_name, "DRPW", "DRPW high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "DRPW", "DRPW high indicator (female)");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "WAIS", "NUMERIC", "Waist Circ", "Waist Circum in cm");
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", "WAIS high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", "WAIS high indicator (female)");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "WT", "NUMERIC", "Kg", "in kg (nnn.n) Range:0-300 Interval:3mo.");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HT", "NUMERIC", "Height", "in cm (nnn) Range:0-300");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "BMI", "NUMERIC", "BMI", "Target: 18.5 - 24.9 (kg/m^2)");
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", "BMI low indicator");
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", "BMI high indicator");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HSMG", "STRING", "Goal", "Self Management Goal");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HSMC", "STRING", "Challenges", "Self Management Challenges");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HRMS", "STRING", "Reviewed", "Review med use and side effects");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FRAM", "STRING", "Risk", NULL);

-- *** preventions group ***
SET @group_name = "Preventions";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "vaccinations");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Pneumovax", "BOOLEAN", "Pneumococcal vaccine", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Flu", "BOOLEAN", "Flu Vaccine", "Annually");

COMMIT;
