
SET @care_tracker_name = "Diabetes";
SET @creatorId = "-1"; -- system provider id

SET @rule_name_never_entered = "Warn: Never Entered";
SET @rule_name_3m_6m = "Note: 3-6 months since last entry";
SET @rule_name_3m_plus = "Warn: Over 3 months since last entry";
SET @rule_name_6m_plus = "Warn: Over 6 months since last entry";
SET @rule_name_12m_plus = "Warn: Over 12 months since last entry";

START TRANSACTION;

CALL addCareTracker(@care_tracker_name, "Measurements for tracking Diabetes", TRUE);

-- set up the drools connection
CALL addCareTrackerDrools(@care_tracker_name, "diab.drl", "diabetes decision support");

-- set up icd9 triggers
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "250");
CALL addCareTrackerIcd9Trigger(@care_tracker_name, "7902");

-- *** Vitals measurements group ***
SET @group_name = "Vitals";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Diabetes related vital measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "BP", "STRING", NULL, "Target > 130/80");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "WT", "NUMERIC", "Weight", "Weight in kg");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HT", "NUMERIC", "Height", "Height in cm");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "BMI", "NUMERIC", "BMI", "Target: 18.5 - 24.9 (kg/m^2)");
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", @rule_name_6m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", "BMI low indicator");
CALL addCareTrackerItemRule(@care_tracker_name, "BMI", "BMI high indicator");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "WAIS", "NUMERIC", "Waist Circ", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", @rule_name_6m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", "WAIS high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "WAIS", "WAIS high indicator (female)");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "WHR", "NUMERIC", "Waist to Hip Ratio", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", @rule_name_6m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", "WHR high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "WHR", "WHR high indicator (female)");

-- *** Cardiovascular measurements groups ***
SET @group_name = "Cardiovascular - Lipids";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Lipids related cardio measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HDL", "NUMERIC", "HDL", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "HDL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "HDL", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "LDL", "NUMERIC", "LDL", "LDL < 2.0");
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "LDL", "Warn: Number Greater Than 2");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "TG", "NUMERIC", "Triglycerides", "Target: < 2.0 mmol/L");
CALL addCareTrackerItemRule(@care_tracker_name, "TG", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "TG", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "TG", "Warn: Number Greater Than 2");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "TCHD", "NUMERIC", "TC/HDL", "Ratio < 4.0");
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "TCHD", "Warn: Number Greater Than 4");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EDNL", "BOOLEAN", "Completed", "Education Nutrition");
CALL addCareTrackerItemRule(@care_tracker_name, "EDNL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EDNL", @rule_name_12m_plus);

SET @group_name = "Cardiovascular - Smoking";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Smoking related cardio measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SKST", "BOOLEAN", "Smoker", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "POSK", "NUMERIC", "Packs per day", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "MCCS", "BOOLEAN", "Completed", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCS", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCS", @rule_name_6m_plus);

SET @group_name = "Cardiovascular - Other";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Other related cardio measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ECG", "STRING", "ECG", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "PSSC", "BOOLEAN", "Screened", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "STRE", "BOOLEAN", "Stress Testing", NULL);

-- *** Glycemic measurements group ***
SET @group_name = "Glycemic";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Glycemic measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DTYP", "STRING", "type", "1 or 2");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "A1C", "NUMERIC", "A1C", "Target > 7.0%");
CALL addCareTrackerItemRule(@care_tracker_name, "A1C", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "A1C", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "A1C", "Warn: Number Greater Than 7");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FBS", "NUMERIC", "Fasting Plasma Glucose", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", "Warn: Number Greater Than 4");
CALL addCareTrackerItemRule(@care_tracker_name, "FBS", "Warn: Number Less Than 7");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FGLC", "BOOLEAN", "Within 20%", "Meter within 20% of simultaneous lab values");
CALL addCareTrackerItemRule(@care_tracker_name, "FGLC", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FGLC", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FBPC", "NUMERIC", "2 hr PC BG", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", "Warn: Number Greater Than 10");
CALL addCareTrackerItemRule(@care_tracker_name, "FBPC", "Warn: Number Less Than 5");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SMBG", "BOOLEAN", "Self Monitoring BG", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "SMBG", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SMBG", @rule_name_3m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HYPM", "BOOLEAN", "Reviewed", "discussed");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "HYPE", "NUMERIC", "# of episodes", "Since last assessed");
CALL addCareTrackerItemRule(@care_tracker_name, "HYPE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "HYPE", @rule_name_3m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "HYPE", "Warn: Number Greater Than 0");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "MCCN", "BOOLEAN", "Completed", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCN", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCN", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCN", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "MCCE", "BOOLEAN", "Completed", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCE", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCE", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SMCD", "STRING", "Challenges", "Self Management Challenges");
CALL addCareTrackerItemRule(@care_tracker_name, "SMCD", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SMCD", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "SMCD", @rule_name_6m_plus);

-- *** Mental Health measurements group ***
SET @group_name = "Mental Health";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Mental health check");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DEPR", "BOOLEAN", "Depressed", "Yearly or As Needed");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "LETH", "BOOLEAN", "Lethargic", "Yearly or As Needed");

-- *** Nephropathy measurements group ***
SET @group_name = "Nephropathy";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Nephropathy measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ACR", "NUMERIC", "ACR", "Target: < 2.0 M : < 2.8 F");
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", @rule_name_12m_plus);
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", "ACR high indicator (male)");
CALL addCareTrackerItemRule(@care_tracker_name, "ACR", "ACR high indicator (female)");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "SCR", "NUMERIC", "Serum Creatinine", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "SCR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "SCR", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EGFR", "NUMERIC", "eGFR", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EGFR", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "AORA", "BOOLEAN", "ACE-I OR ARB", "Yes No");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CRCL", "NUMERIC", "Creatinine Clearance", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "CRCL", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "CRCL", @rule_name_12m_plus);

-- *** Neuropathy measurements group ***
SET @group_name = "Neuropathy";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Neuropathy measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "AORA", "BOOLEAN", "Present", "Erectile Dysfunction, gastrointestinal disturbance");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FTLS", "BOOLEAN", "Normal", "Check for peripheral anesthesia with 10g monofilament or 128 Hz tuning fork");
CALL addCareTrackerItemRule(@care_tracker_name, "FTLS", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FTLS", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "PANE", "BOOLEAN", "Present", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "FTE", "BOOLEAN", "Normal", "Foot Care");
CALL addCareTrackerItemRule(@care_tracker_name, "FTE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "FTE", @rule_name_12m_plus);

CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EDF", "BOOLEAN", "Dysfunction", "Yes No");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "BCTR", "BOOLEAN", "Birth Control", "On Birth Control");

-- *** Retinopathy measurements group ***
SET @group_name = "Retinopathy";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Retinopathy measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EYEE", "BOOLEAN", "Exam Done", "Dilated Eye Exam, comment if referred");
CALL addCareTrackerItemRule(@care_tracker_name, "EYEE", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EYEE", @rule_name_12m_plus);

-- *** Vaccination preventions group ***
SET @group_name = "Vaccination";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Vaccinations");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Pneumovax", "BOOLEAN", "Pneumococcal vaccine", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "PREVENTION", "Flu", "BOOLEAN", "Flu Vaccine", "Annually");

-- *** Other measurements group ***
SET @group_name = "Other";
CALL addCareTrackerItemGroup(@care_tracker_name, @group_name, "Other diabetes related measurements");
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "DMME", "BOOLEAN", "Discussed", "Assess and discuss self-management challenges");
CALL addCareTrackerItemRule(@care_tracker_name, "DMME", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "DMME", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "EDND", "BOOLEAN", "Completed", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "EDND", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "EDND", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "MCCO", "BOOLEAN", "Completed", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCO", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCO", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "MCCO", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "CGSD", "BOOLEAN", "Goal", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "CGSD", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "CGSD", @rule_name_3m_6m);
CALL addCareTrackerItemRule(@care_tracker_name, "CGSD", @rule_name_6m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ASAU", "BOOLEAN", "Used", NULL);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "AST", "NUMERIC", "AST", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "AST", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "AST", @rule_name_12m_plus);
CALL addCareTrackerItem(@care_tracker_name, @group_name, "MEASUREMENT", "ALT", "NUMERIC", "ALT", NULL);
CALL addCareTrackerItemRule(@care_tracker_name, "ALT", @rule_name_never_entered);
CALL addCareTrackerItemRule(@care_tracker_name, "ALT", @rule_name_12m_plus);



COMMIT;