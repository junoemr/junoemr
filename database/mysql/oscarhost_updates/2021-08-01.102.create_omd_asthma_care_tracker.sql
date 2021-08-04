
SET @care_tracker_name = "Asthma";
SET @creatorId = "-1"; -- system provider id

SET @rule_name_never_entered = "Warn: Never Entered";
SET @rule_name_6m_plus = "Warn: Over 6 months since last entry";
# SET @rule_name_12m_plus = "Warn: Over 12 months since last entry";
# SET @rule_name_3m_6m = "Note: 3-6 months since last entry";
# SET @rule_name_10m_12m = "Note: 10-12 months since last entry";
SET @rule_name_value_over_0 = "Warn: Number Greater Than 0";
# SET @rule_name_value_over_2 = "Warn: Number Greater Than 2";
SET @rule_name_value_over_4 = "Warn: Number Greater Than 4";
SET @rule_name_problem_indicator_checked = "Problem indicator checked";

START TRANSACTION;

CALL addCareTracker(@care_tracker_name, "Measurements for tracking Asthma", TRUE);

-- set up icd9 triggers
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "493");

-- set up the drools connection
CALL addCareTrackerDrools(@care_tracker_name, "diab.drl", "diabetes decision support");

-- *** measurements group ***
SET @group_name = "Asthma Measurements";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Measurement group for Asthma related measurements");

CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ALPA", "BOOLEAN", "Asthma Limits Physical Activity", 0, "Yes/No");
CALL addCareTrackerItemRule(@care_tracker_name, "ALPA", @rule_name_problem_indicator_checked);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ANR", "NUMERIC", "Frequency/Week", 1, "Number >= 0");
CALL addCareTrackerItemRule(@care_tracker_name, "ANR", @rule_name_value_over_4);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ASYM", "NUMERIC", "Frequency/Week", 1, "dyspnea, cough, wheeze, chest tightness");
CALL addCareTrackerItemRule(@care_tracker_name, "ASYM", @rule_name_value_over_4);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ASWA", "BOOLEAN", "Has Absence", 0, "School/Work");
CALL addCareTrackerItemRule(@care_tracker_name, "ASWA", @rule_name_problem_indicator_checked);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ANSY", "NUMERIC", "Frequency/Week", 1, "frequency per week");
CALL addCareTrackerItemRule(@care_tracker_name, "ANSY", @rule_name_value_over_0);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FEV1", "BOOLEAN", "90% personal or predicted best", 0, "greater than 90 percent personal or predicted or personal best");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "AELV", "BOOLEAN", "Has", 0, "Requiring clinical evaluations");
CALL addCareTrackerItemRule(@care_tracker_name, "AELV", @rule_name_problem_indicator_checked);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SPIR", "NUMERIC", "Spirometry", 1, "Number >= 0");
CALL addCareTrackerItemRule(@care_tracker_name, "SPIR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SPIR", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "PEFR", "NUMERIC", "PEFR value", 1, "best of 3");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ARAD", "BOOLEAN", "Reviewed", 0, "Review Asthma Definition");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ARMA", "BOOLEAN", "Reviewed", 0, "Asthma Review Med Adherence");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ARDT", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SMCS", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ASTA", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "AENC", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ACOS", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "AACP", "BOOLEAN", "Provided/Revised/Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "AEDR", "BOOLEAN", "Referred", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ASPR", "BOOLEAN", "Referred", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "LHAD", "BOOLEAN", "Admission", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "OUTR", "BOOLEAN", "Referred", 0, NULL);

COMMIT;
