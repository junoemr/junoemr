START TRANSACTION;

SET @care_tracker_name = "Diabetes";

SET @rule_name_never_entered = "Warn: Never Entered";
SET @rule_name_3m_6m = "Note: 3-6 months since last entry";
SET @rule_name_3m_plus = "Warn: Over 3 months since last entry";
SET @rule_name_6m_plus = "Warn: Over 6 months since last entry";
SET @rule_name_12m_plus = "Warn: Over 12 months since last entry";

CALL addCareTracker(@care_tracker_name, "Measurements for tracking Diabetes", TRUE);

-- set up the drools connection
CALL addCareTrackerDrools(@care_tracker_name, "diab.drl", "diabetes decision support");

-- set up icd9 triggers
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "250");
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "7902");

-- *** Vitals measurements group ***
SET @group_name = "Vitals";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Diabetes related vital measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "BP", "STRING", NULL, 0, "Target > 130/80");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "WT", "NUMERIC", "Weight", 1, "Weight in kg");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "HT", "NUMERIC", "Height", 1, "Height in cm");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "BMI", "NUMERIC", "BMI", 1, "Target: 18.5 - 24.9 (kg/m^2)");
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", @rule_name_6m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", "BMI low indicator");
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", "BMI high indicator");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "WAIS", "NUMERIC", "Waist Circ", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", @rule_name_6m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", "WAIS high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", "WAIS high indicator (female)");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "WHR", "NUMERIC", "Waist to Hip Ratio", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", @rule_name_6m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", "WHR high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", "WHR high indicator (female)");

-- *** Cardiovascular measurements groups ***
SET @group_name = "Cardiovascular - Lipids";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Lipids related cardio measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "HDL", "NUMERIC", "HDL", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "HDL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "HDL", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "LDL", "NUMERIC", "LDL", 1, "LDL < 2.0");
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", "Warn: Number Greater Than 2");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "TG", "NUMERIC", "Triglycerides", 1, "Target: < 2.0 mmol/L");
CALL addCareTrackerItemRule(@care_tracker_name, "TG", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "TG", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "TG", "Warn: Number Greater Than 2");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "TCHD", "NUMERIC", "TC/HDL", 1, "Ratio < 4.0");
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", "Warn: Number Greater Than 4");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "EDNL", "BOOLEAN", "Completed", 0, "Education Nutrition");
CALL addCareTrackerItemRule(@care_tracker_name, "EDNL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EDNL", @rule_name_12m_plus);

SET @group_name = "Cardiovascular - Smoking";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Smoking related cardio measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "SKST", "BOOLEAN", "Smoker", 0, NULL);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "POSK", "NUMERIC", "Packs per day", 1, NULL);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "MCCS", "BOOLEAN", "Completed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCS", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCS", @rule_name_6m_plus);

SET @group_name = "Cardiovascular - Other";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Other related cardio measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "ECG", "STRING", "ECG", 0, NULL);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "PSSC", "BOOLEAN", "Screened", 0, NULL);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "STRE", "BOOLEAN", "Stress Testing", 0, NULL);

-- *** Glycemic measurements group ***
SET @group_name = "Glycemic";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Glycemic measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "DTYP", "STRING", "type", 0, "1 or 2");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "A1C", "NUMERIC", "A1C", 1, "Target > 7.0%");
CALL addCareTrackerItemRule(@care_tracker_name, "A1C", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "A1C", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "A1C", "Warn: Number Greater Than 7");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "FBS", "NUMERIC", "Fasting Plasma Glucose", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", "Warn: Number Greater Than 4");
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", "Warn: Number Less Than 7");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "FGLC", "BOOLEAN", "Within 20%", 0, "Meter within 20% of simultaneous lab values");
CALL addCareTrackerItemRule(@care_tracker_name, "FGLC", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FGLC", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "FBPC", "NUMERIC", "2 hr PC BG", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", "Warn: Number Greater Than 10");
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", "Warn: Number Less Than 5");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "SMBG", "NUMERIC", "Self Monitoring BG", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "SMBG", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SMBG", @rule_name_3m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "HYPM", "BOOLEAN", "Reviewed", 0, "discussed");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "HYPE", "NUMERIC", "# of episodes", 1, "Since last assessed");
CALL addCareTrackerItemRule(@care_tracker_name, "HYPE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "HYPE", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "HYPE", "Warn: Number Greater Than 0");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "MCCN", "BOOLEAN", "Completed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCN", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCN", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCN", @rule_name_6m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "MCCE", "BOOLEAN", "Completed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCE", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCE", @rule_name_6m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "SMCD", "STRING", "Challenges", 0, "Self Management Challenges");
CALL addCareTrackerItemRule(@care_tracker_name, "SMCD", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SMCD", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "SMCD", @rule_name_6m_plus);

-- *** Mental Health measurements group ***
SET @group_name = "Mental Health";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Mental health check");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "DEPR", "BOOLEAN", "Depressed", 0, "Yearly or As Needed");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "LETH", "BOOLEAN", "Lethargic", 0, "Yearly or As Needed");

-- *** Nephropathy measurements group ***
SET @group_name = "Nephropathy";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Nephropathy measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "ACR", "NUMERIC", "ACR", 1, "Target: < 2.0 M : < 2.8 F");
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", "ACR high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", "ACR high indicator (female)");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "SCR", "NUMERIC", "Serum Creatinine", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "SCR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SCR", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "EGFR", "NUMERIC", "eGFR", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "AORA", "BOOLEAN", "ACE-I OR ARB", 0, "Yes No");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "CRCL", "NUMERIC", "Creatinine Clearance", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "CRCL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "CRCL", @rule_name_12m_plus);

-- *** Neuropathy measurements group ***
SET @group_name = "Neuropathy";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Neuropathy measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "AORA", "BOOLEAN", "Present", 0, "Erectile Dysfunction, gastrointestinal disturbance");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "FTLS", "BOOLEAN", "Normal", 0, "Check for peripheral anesthesia with 10g monofilament or 128 Hz tuning fork");
CALL addCareTrackerItemRule(@care_tracker_name, "FTLS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FTLS", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "PANE", "BOOLEAN", "Present", 0, NULL);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "FTE", "BOOLEAN", "Normal", 0, "Foot Care");
CALL addCareTrackerItemRule(@care_tracker_name, "FTE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FTE", @rule_name_12m_plus);

CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "EDF", "BOOLEAN", "Dysfunction", 0, "Yes No");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "BCTR", "BOOLEAN", "Birth Control", 0, "On Birth Control");

-- *** Retinopathy measurements group ***
SET @group_name = "Retinopathy";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Retinopathy measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "EYEE", "BOOLEAN", "Exam Done", 0, "Dilated Eye Exam, comment if referred");
CALL addCareTrackerItemRule(@care_tracker_name, "EYEE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EYEE", @rule_name_12m_plus);

-- *** Vaccination preventions group ***
SET @group_name = "Vaccination";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Vaccinations");
CALL careTrackerAddItemProcedure(@group_name, "PREVENTION", "Pneumovax", "BOOLEAN", "Pneumococcal vaccine", 0, NULL);
CALL careTrackerAddItemProcedure(@group_name, "PREVENTION", "Flu", "BOOLEAN", "Flu Vaccine", 0, "Annually");

-- *** Other measurements group ***
SET @group_name = "Other";
CALL careTrackerAddItemGroupProcedure(@care_tracker_name, @group_name, "Other diabetes related measurements");
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "DMME", "BOOLEAN", "Discussed", 0, "Assess and discuss self-management challenges");
CALL addCareTrackerItemRule(@care_tracker_name, "DMME", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "DMME", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "EDND", "BOOLEAN", "Completed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "EDND", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EDND", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "MCCO", "BOOLEAN", "Completed", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCO", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCO", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCO", @rule_name_6m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "CGSD", "STRING", "Goal", 0, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "CGSD", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "CGSD", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "CGSD", @rule_name_6m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "ASAU", "BOOLEAN", "Used", 0, NULL);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "AST", "NUMERIC", "AST", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "AST", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "AST", @rule_name_12m_plus);
CALL careTrackerAddItemProcedure(@group_name, "MEASUREMENT", "ALT", "NUMERIC", "ALT", 1, NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "ALT", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "ALT", @rule_name_12m_plus);



COMMIT;