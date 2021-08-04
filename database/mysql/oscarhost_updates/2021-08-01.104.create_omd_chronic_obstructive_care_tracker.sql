
SET @care_tracker_name = "Chronic Obstructive Pulmonary";

SET @rule_name_never_entered = "Warn: Never Entered";
SET @rule_name_6m_plus = "Warn: Over 6 months since last entry";
SET @rule_name_12m_plus = "Warn: Over 12 months since last entry";

START TRANSACTION;

CALL addCareTracker(@care_tracker_name, "Elements for tracking COPD", TRUE);

-- set up the drools connection
CALL addCareTrackerDrools(@care_tracker_name, "diab.drl", "diabetes decision support");

-- set up icd9 triggers
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "416");

-- *** measurements group ***
SET @group_name = "Measurements";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "chronic obstructive pulmonary measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FEV1", "BOOLEAN", "Value", 0, "FEV 1 percent of Predicted");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CODC", "STRING", "Class", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "02SA", "NUMERIC", "Percent", 1, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "RABG", "BOOLEAN", "Recommended", 0, "Aterial Blood Gas");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SKST", "BOOLEAN", "Smoker", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SMCS", "BOOLEAN", "Discussed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SMCP", "STRING", "Program", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "BMI", "NUMERIC", "BMI", 1, "Target: 18.5 - 24.9 (kg/m^2)");
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", "BMI high indicator");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SUO2", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "SUO2", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SUO2", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "NOVS", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "NOVS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "NOVS", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "COPS", "BOOLEAN", "Referred", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "COPS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "COPS", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "RPPT", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "RPPT", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "RPPT", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "PSPA", "BOOLEAN", "Goal", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "PSPA", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "PSPA", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "COPM", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "COPM", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "COPM", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "COPE", "BOOLEAN", "Provided", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "PRRF", "BOOLEAN", "Referred", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EPR", "BOOLEAN", "Review/in place", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "EPR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EPR", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DOLE", "DATE", "Date", 0, NULL);

-- *** preventions group ***
SET @group_name = "Preventions";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "chronic obstructive pulmonary preventions");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Pneumovax", "BOOLEAN", "Pneumococcal vaccine", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Flu", "BOOLEAN", "Flu Vaccine", 0, "Annually");

COMMIT;