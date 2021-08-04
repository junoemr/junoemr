
SET @care_tracker_name = "Heart Failure Flowsheet";
SET @creatorId = "-1"; -- system provider id

SET @rule_name_never_entered = "Warn: Never Entered";
SET @rule_name_6m_plus = "Warn: Over 6 months since last entry";
SET @rule_name_12m_plus = "Warn: Over 12 months since last entry";
SET @rule_name_3m_6m = "Note: 3-6 months since last entry";
SET @rule_name_10m_12m = "Note: 10-12 months since last entry";
SET @rule_name_value_over_0 = "Warn: Number Greater Than 0";
SET @rule_name_value_over_2 = "Warn: Number Greater Than 2";
SET @rule_name_value_over_4 = "Warn: Number Greater Than 4";

START TRANSACTION;

CALL addCareTracker(@care_tracker_name, "Elements for tracking heart failure", TRUE);

-- set up icd9 triggers
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "428");

-- *** measurements group ***
SET @group_name = "Measurements";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "heart failure measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "NYHA", "NUMERIC", "Class", 0, "Record class 1 - 4");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "Kpl", "NUMERIC", "Value", 1, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "Napl", "NUMERIC", "Value", 1, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SCR", "NUMERIC", "Value", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "SCR", "SCR high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "SCR", "SCR high indicator (female)");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EGFR", "NUMERIC", "EGFR", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_10m_12m);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SOHF", "BOOLEAN",
    "Reviewed", 0, "list signs in comments: Fatigue, Dizziness and/or syncope, Dyspnea on exertion, Dyspnea at rest, Orthopnea, Paroxysmal nocturnal dyspnea");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "WT", "NUMERIC", "Value", 1, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HR", "NUMERIC", "Heart Rate", 1, "in bpm (nnn) Range:40-180");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "BP", "STRING", NULL, 0, "Target < 130/80");
CALL addCareTrackerItemRule(@care_tracker_name, "BP", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "BP", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "BP", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "JVPE", "BOOLEAN", "Changed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "PEDE", "BOOLEAN", "Has", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "LUCR", "BOOLEAN", "Has", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "PHIN", "BOOLEAN", "Shows signs", 0, "Signs of Pharmacological Intolerance");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CERV", "NUMERIC", "# of visits", 1, "since last assessment");
CALL addCareTrackerItemRule(@care_tracker_name, "CERV", @rule_name_value_over_0);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DEPR", "BOOLEAN", "Depressed", 0, "Yearly or As Needed");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DRPW", "NUMERIC", "# of drinks per week", 1, "Number of Drinks per week");
CALL addCareTrackerItemRule(@care_tracker_name, "DRPW", "DRPW high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "DRPW", "DRPW high indicator (female)");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SKST", "BOOLEAN", "Smoker", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HDL", "NUMERIC", "HDL", 1, "in mmol/L (n.n) Range:0.4-4.0");
CALL addCareTrackerItemRule(@care_tracker_name, "HDL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "HDL", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "LDL", "NUMERIC", "LDL", 1, "LDL < 2.0");
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_value_over_2);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "TG", "NUMERIC", "Triglycerides", 1, "Target: < 2.0 mmol/L");
CALL addCareTrackerItemRule(@care_tracker_name, "TG", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "TG", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "TG", @rule_name_value_over_2);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "TCHD", "NUMERIC", "TC/HDL", 1, "Ratio < 4.0");
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_value_over_4);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ECG", "BOOLEAN", "ECG", 0, NULL);

CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CEDM", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CEDS", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CEDW", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CEDE", "BOOLEAN", "Reviewed", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HFMS", "BOOLEAN", "Discussed", 0, "Target Modifiable Risk Factor");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HFMD", "BOOLEAN", "Discussed", 0, "Target Modifiable Risk Factor");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HFMO", "BOOLEAN", "Discussed", 0, "Target Modifiable Risk Factor");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HFMH", "BOOLEAN", "Discussed", 0, "Target Modifiable Risk Factor");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HFMT", "BOOLEAN", "Discussed", 0, "Target Modifiable Risk Factor");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HFCG", "STRING", "Goal", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HFCS", "STRING", "Challenges", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CASA", "BOOLEAN", "Considered", 0, NULL);

-- *** preventions group ***
SET @group_name = "Preventions";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "vaccinations");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Pneumovax", "BOOLEAN", "Pneumococcal vaccine", 0, NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Flu", "BOOLEAN", "Flu Vaccine", 0, "Annually");

COMMIT;