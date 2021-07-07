INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - ABSENCES' AS type, 'Asthma - absences' AS typeDisplayName, 'Num Of School/Work Absence' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - ABSENCES') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - COUGH' AS type, 'Asthma - cough' AS typeDisplayName, 'Num Of School/Work Absence' AS typeDescription, 'Per week' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - COUGH') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - DEF. REVIEW' AS type, 'Asthma - Definition Review' AS typeDisplayName, 'Records whether the Asthma Definition has been reviewed' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - DEF. REVIEW') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - DYSPNEA' AS type, 'Asthma - Dyspnea', 'Dyspnea symptoms' AS typeDisplayName, 'Per week' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - DYSPNEA') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - MEDICATION  REVIEW' AS type, 'Asthma - Medication review' AS typeDisplayName, 'Record whether medication has been reviewed' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - DYSPNEA') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - NUM. EXACERBATIONS' AS type, 'Asthma - Num. Exacerbations' AS typeDisplayName, 'Exacerbations since last visit' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - NUM. EXACERBATIONS') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - TIGHTNESS' AS type, 'Asthma - tightness' AS typeDisplayName, 'Num. symptoms per week' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE()  AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - TIGHTNESS') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - WHEEZE' AS type, 'Asthma - wheeze' AS typeDisplayName, 'Num. symptoms per week' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - WHEEZE') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'WHEEZING - yes/no' AS type, 'Wheezing yes/no' AS typeDisplayName, 'Wheezing yes/no' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'WHEEZING - yes/no') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'WHEEZING - LOCATION' AS type, 'Wheezing location' AS typeDisplayName, 'Indicate where' AS typeDescription, 'location' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'WHEEZING - LOCATION') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'URINARY MICROALBUMIN SCREEN' AS type, 'Urinary Microalbumin Screen' AS typeDisplayName, 'Urinary Microalbumin Screen' AS typeDescription, '' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'URINARY MICROALBUMIN SCREEN') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'PITTING EDEMA' AS type, 'Pitting Edema' AS typeDisplayName, 'Records yes/no' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'PITTING EDEMA') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'PITTING EDEMA - location' AS type, 'Pitting Edema - location' AS typeDisplayName, 'Records location' AS typeDescription, 'Indicate location' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'PITTING EDEMA') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'NEUROLOGICAL EXAM' AS type, 'Neurological Exam' AS typeDisplayName, 'Records the findings of the Neurological Examination' AS typeDescription, 'Indicate Findings' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'NEUROLOGICAL EXAM') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'NYHA - CLASS I' AS type, 'NYHA - Class I' AS typeDisplayName, 'NYHA classification - no symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'NYHA - CLASS I') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'NYHA - CLASS II' AS type, 'NYHA - Class II' AS typeDisplayName, 'NYHA classification - symptoms with ordinary activity' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'NYHA - CLASS II') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'NYHA - CLASS III' AS type, 'NYHA - Class III' AS typeDisplayName, 'NYHA classification - symptoms with less than ordinary activity' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'NYHA - CLASS III') LIMIT 1;


INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'NYHA - CLASS IV' AS type, 'NYHA - Class IV' AS typeDisplayName, 'NYHA classification - symptoms at rest' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'NYHA - CLASS IV') LIMIT 1;


INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'LUNG CRACKLES' AS type, 'Lung Crackles' AS typeDisplayName, 'Lung Crackles location' AS typeDescription, 'Indicate location' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'LUNG CRACKLES'  AND typeDescription = 'Lung Crackles location') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FOOT EXAM' AS type, 'Foot Exam' AS typeDisplayName, 'Record Foot Exam findings' AS typeDescription, 'Indicate findings' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FOOT EXAM') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - COLLABORATIVE SELF-MANAGEMENT CHALLENGE' AS type, 'Challenge' AS typeDisplayName, 'Collaborative Self-Management Challenge'  AS typeDescription, 'Indicate challenge' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - COLLABORATIVE SELF-MANAGEMENT CHALLENGE') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'ASTHMA - COLLABORATIVE SELF-MANAGEMENT GOALS' AS type, 'Goals' AS typeDisplayName, 'Collaborative Self-Management Goals' AS typeDescription, 'Indicate goals' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'ASTHMA - COLLABORATIVE SELF-MANAGEMENT GOALS') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'PEFR BEFORE' AS type, 'PEF before' AS typeDisplayName, 'Peak Expiratory Flow' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'PEFR BEFORE') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'PEFR AFTER' AS type, 'PEFR after' AS typeDisplayName, 'Peak Expiratory Flow after puff' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'PEFR AFTER') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FVC AFTER' AS type, 'FVC after' AS typeDisplayName, 'Forced Vital Capacity after puff' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FVC AFTER') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FVC BEFORE' AS type, 'FVC before' AS typeDisplayName, 'Forced Vital Capacity before puff' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FVC BEFORE') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FVC PREDICTED' AS type, 'FVC predicted' AS typeDisplayName, 'Forced Vital Capacity in general population' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FVC PREDICTED') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FVC AFTER/FVC PREDICTED' AS type, 'FVC after/FVC predicted' AS typeDisplayName, 'FVC ratio of FVC actual after puff to FVC predicted' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FVC AFTER/FVC PREDICTED') LIMIT 1;

INSERT INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FVC BEFORE/FVC PREDICTED' AS type, 'FVC before/FVC predicted' AS typeDisplayName, 'FVC ratio of FVC actual before puff to FVC predicted' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FVC BEFORE/FVC PREDICTED') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV PREDICTED' AS type, 'FEV predicted' AS typeDisplayName, 'Forced Vital Capacity in the population with similar characteristics' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV PREDICTED') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1 AFTER' AS type, 'FEV1 after' AS typeDisplayName, 'Forced Expiratory Volume after puff' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1 AFTER') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1 BEFORE' AS type, 'FEV1 before' AS typeDisplayName, 'Forced Expiratory Volume before puff' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1 BEFORE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1 PREDICTED' AS type, 'FEV1 predicted' AS typeDisplayName, 'FEV1 calculated in the population with similar characteristics' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1 PREDICTED') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1/FVC BEFORE' AS type, 'FEV1/FVC ratio before' AS typeDisplayName, 'FEV1/FVC ratio before puff' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1/FVC BEFORE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1/FVC AFTER' AS type, 'FEV1/FVC ratio after' AS typeDisplayName, 'FEV1/FVC ratio before puff' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1/FVC AFTER') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1/FVC PREDICTED' AS type, 'FEV1/FVC ratio predicted' AS typeDisplayName, 'ratio of FEV1 predicted to FVC predicted' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1/FVC PREDICTED') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1/FVC ratio BEFORE/PREDICTED' AS type, 'FEV1/FVC ratio before/predicted ratio' AS typeDisplayName, 'FEV1 ratio before puff of the patient / the average FEV1 ratio predicted' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1/FVC ratio BEFORE/PREDICTED') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1/FEV AFTER OF FEV1/FEV PREDICTED' AS type, 'FEV1/FEV after of FEV1/FEV predicted' AS typeDisplayName, 'FEV1 / FVC after puff actual divided by FEV1 / FVC predicted' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1/FEV AFTER OF FEV1/FEV PREDICTED') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'FEV1/FEV BEFORE OF FEV1/FEV PREDICTED' AS type, 'FEV1/FEV before of FEV1/FEV predicted' AS typeDisplayName, 'FEV1 / FVC before puff actual divided by FEV1 / FVC predicted' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'FEV1/FEV BEFORE OF FEV1/FEV PREDICTED') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - EXACERBATIONS' AS type, 'COPD - Exacerbations' AS typeDisplayName, 'Num since last assessment' AS typeDescription, 'Numeric value' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Numeric Value greater than or equal to 0') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - EXACERBATIONS') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - GOALS' AS type, 'COPD - goals' AS typeDisplayName, 'Collaborative Self-Management Goals' AS typeDescription, 'Indicate goals' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - GOALS') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - CHALLENGE' AS type, 'COPD - challenge' AS typeDisplayName, 'Collaborative Self-Management Challenge' AS typeDescription, 'Indicate challenge' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - CHALLENGE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - MILD' AS type, 'COPD - mild' AS typeDisplayName, 'FEV1 80 percent or more compared to predicted' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - MILD') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - MODERATE' AS type, 'COPD - moderate' AS typeDisplayName, 'FEV1 less than 80 percent and more than 50 percent compared to predicted' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - MODERATE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - Severe' AS type, 'COPD - severe' AS typeDisplayName, 'FEV1 less than 50 percent and more than 30 percent compared to predicted' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - Severe') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - Very Severe' AS type, 'COPD - very severe' AS typeDisplayName, 'FEV1 less than 30 percent compared to predicted' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - Very Severe') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HT - MEDICATION' AS type, 'HT - Medication' AS typeDisplayName, 'Medication Review' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HT - MEDICATION') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HT - COLLABORATIVE SELF-MANAGEMENT CHALLENGE' AS type, 'HT - Challenge' AS typeDisplayName, 'Collaborative Self-Management Challenge' AS typeDescription, 'Indicate challenge' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HT - COLLABORATIVE SELF-MANAGEMENT CHALLENGE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HT - GOALS' AS type, 'HT - goals' AS typeDisplayName, 'Collaborative Self-Management Goals' AS typeDescription, 'Indicate goals' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HT - GOALS') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - GOALS' AS type, 'HF - goals' AS typeDisplayName, 'Collaborative Self-Management Goals' AS typeDescription, 'Indicate goals' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - GOALS') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - CHALLENGE' AS type, 'HF - challenge' AS typeDisplayName, 'Collaborative Self-Management Challenge' AS typeDescription, 'Indicate challenge' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - CHALLENGE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - SYNCOPE' AS type, 'HF - Syncope', 'Symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - SYNCOPE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - DYSPNEA AT REST' AS type, 'HF - Dyspnea at rest' AS typeDisplayName, 'Symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - DYSPNEA AT REST') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - DIZZINESS' AS type, 'HF - Dizziness' AS typeDisplayName, 'Symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - DIZZINESS') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - DYSPNEA ON EXERTION' AS type, 'HF - Dyspnea on Exertion' AS typeDisplayName, 'Symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - DYSPNEA ON EXERTION') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - FATIGUE' AS type, 'HF - Fatigue' AS typeDisplayName, 'Symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - FATIGUE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - ORTHOPNEA' AS type, 'HF - Orthopnea' AS typeDisplayName, 'Symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - ORTHOPNEA') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - PAROXYSMAL NOCTURNAL DYSPNEA' AS type, 'HF - Paroxysmal nocturnal dyspnea' AS typeDisplayName, 'Symptoms' AS typeDescription, 'yes/no' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - PAROXYSMAL NOCTURNAL DYSPNEA') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HF - PHARMACOLOGICAL INTOLERANCE' AS type, 'HF - Pharmacological Intolerance' AS typeDisplayName, 'Intolerance' AS typeDescription, 'Indicate intolerance' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HF - PHARMACOLOGICAL INTOLERANCE') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HT - Exercise reviewed' AS type, 'Exercise reviewed' AS typeDisplayName, 'Exercise reviewed' AS typeDescription, 'Yes/No' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HT - Exercise reviewed') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'HT - Medication reviewed' AS type, 'Medication reviewed' AS typeDisplayName, 'Medication reviewed' AS typeDescription, 'Yes/No' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'HT - Medication reviewed') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - Exacerbation plan' AS type, 'Exacerbation plan' AS typeDisplayName, 'Exacerbation plan' AS typeDescription, 'Provided/Revised/Reviewed' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - Exacerbation plan') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'Asthma - Action plan' AS type, 'Action plan' AS typeDisplayName, 'Action plan' AS typeDescription, 'Provided/Revised/Reviewed' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'No Validations') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'Asthma - Action plan') LIMIT 1;

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
SELECT * FROM
(SELECT 'COPD - Patient Education' AS type, 'Patient Education' AS typeDisplayName, 'Patient education materials provided ' AS typeDescription, 'Yes/No' AS measuringInstruction, v.id AS validation, CURDATE() AS createDate
FROM validations v WHERE v.name = 'Yes/No') AS temp
WHERE NOT EXISTS (SELECT type FROM measurementType WHERE type = 'COPD - Patient Education') LIMIT 1;