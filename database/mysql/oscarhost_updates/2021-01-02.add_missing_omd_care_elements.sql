INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - ABSENCES', 'Asthma - absences', 'Num Of School/Work Absence', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - COUGH', 'Asthma - cough', 'Num Of School/Work Absence', 'Per week', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - DEF. REVIEW', 'Asthma - Definition Review', 'Records whether the Asthma Definition has been reviewed', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - DYSPNEA', 'Asthma - Dyspnea', 'Dyspnea symptoms', 'Per week', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - MEDICATION  REVIEW', 'Asthma - Medication review', 'Record whether medication has been reviewed', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - NUM. EXACERBATIONS', 'Asthma - Num. Exacerbations', 'Exacerbations since last visit', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - TIGHTNESS', 'Asthma - tightness', 'Num. symptoms per week', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - WHEEZE', 'Asthma - wheeze', 'Num. symptoms per week', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('WHEEZING -yes/no', 'Wheezing yes/no', 'Wheezing yes/no', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('WHEEZING - LOCATION', 'Wheezing location', 'Indicate where', 'location', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('URINARY MICROALBUMIN SCREEN', 'Urinary Microalbumin Screen', 'Urinary Microalbumin Screen', '', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('PITTING EDEMA', 'Pitting Edema', 'Records yes/no', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('NEUROLOGICAL EXAM', 'Neurological Exam', 'Records the findings of the Neurological Examination', 'Indicate Findings', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('NYHA - CLASS I', 'NYHA - Class I', 'NYHA classification - no symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('NYHA - CLASS II', 'NYHA - Class II', 'NYHA classification - symptoms with ordinary activity', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('NYHA - CLASS III', 'NYHA - Class III', 'NYHA classification - symptoms with less than ordinary activity', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('NYHA - CLASS IV', 'NYHA - Class IV', 'NYHA classification - symptoms at rest', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('LUNG CRACKLES', 'Lung Crackles', 'Lung Crackles location', 'Indicate where', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FOOT EXAM', 'Foot Exam', 'Record Foot Exam findings', 'Indicate findings', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - COLLABORATIVE SELF-MANAGEMENT CHALLENGE', 'Challenge', 'Collaborative Self-Management Challenge', 'Indicate challenge', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('ASTHMA - COLLABORATIVE SELF-MANAGEMENT GOALS', 'Goals', 'Collaborative Self-Management Goals', 'Indicate goals', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('PEFR BEFORE', 'PEF before', 'Peak Expiratory Flow', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('PEFR AFTER ', 'PEFR after', 'Peak Expiratory Flow after puff', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FVC AFTER', 'FVC after', 'Forced Vital Capacity after puff', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FVC BEFORE', 'FVC before', 'Forced Vital Capacity before puff', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FVC PREDICTED', 'FVC predicted', 'Forced Vital Capacity in general population', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FVC AFTER/FVC PREDICTED', 'FVC after/FVC predicted', 'FVC ratio of FVC actual after puff to FVC predicted', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FVC BEFORE/FVC PREDICTED', 'FVC before/FVC predicted', 'FVC ratio of FVC actual before puff to FVC predicted', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV PREDICTED', 'FEV predicted', 'Forced Vital Capacity in the population with similar characteristics', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1 AFTER', 'FEV1 after', 'Forced Expiratory Volume after puff', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1 BEFORE', 'FEV1 before', 'Forced Expiratory Volume before puff', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1 PREDICTED', 'FEV1 predicted', 'FEV1 calculated in the population with similar characteristics', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1/FVC BEFORE', 'FEV1/FVC ratio before', 'FEV1/FVC ratio before puff', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1/FVC AFTER', 'FEV1/FVC ratio after', 'FEV1/FVC ratio before puff', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1/FVC PREDICTED', 'FEV1/FVC ratio predicted', 'ratio of FEV1 predicted to FVC predicted', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1/FVC ratio BEFORE/PREDICTED', 'FEV1/FVC ratio before/predicted ratio', 'FEV1 ratio before puff of the patient / the average FEV1 ratio predicted', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1/FEV AFTER OF FEV1/FEV PREDICTED', 'FEV1/FEV after of FEV1/FEV predicted', 'FEV1 / FVC after puff actual divided by FEV1 / FVC predicted', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('FEV1/FEV BEFORE OF FEV1/FEV PREDICTED', 'FEV1/FEV before of FEV1/FEV predicted', 'FEV1 / FVC before puff actual divided by FEV1 / FVC predicted', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('COPD - EXACERBATIONS', 'COPD - Exacerbations', 'Num since last assessment', 'Numeric value', 24, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('COPD - GOALS', 'COPD - goals', 'Collaborative Self-Management Goals', 'Indicate goals', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('COPD - CHALLENGE', 'COPD - challenge', 'Collaborative Self-Management Challenge', 'Indicate challenge', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('COPD - MILD', 'COPD - mild', 'FEV1 80 percent or more compared to predicted', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('COPD - MODERATE', 'COPD - moderate', 'FEV1 less than 80 percent and more than 50 percent compared to predicted', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('COPD - Severe', 'COPD - severe', 'FEV1 less than 50 percent and more than 30 percent compared to predicted', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('COPD - Very Severe', 'COPD - very severe', 'FEV1 less than 30 percent compared to predicted', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HT - MEDICATION', 'HT - Medication', 'Medication Review', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HT - COLLABORATIVE SELF-MANAGEMENT CHALLENGE', 'HT - Challenge', 'Collaborative Self-Management Challenge', 'Indicate challenge', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HT - GOALS', 'HT - goals', 'Collaborative Self-Management Goals', 'Indicate goals', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - GOALS', 'HF - goals', 'Collaborative Self-Management Goals', 'Indicate goals', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - CHALLENGE', 'HF - challenge', 'Collaborative Self-Management Challenge', 'Indicate challenge', 11, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - SYNCOPE', 'HF - Syncope', 'Symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - DYSPNEA AT REST', 'HF - Dyspnea at rest', 'Symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - DIZZINESS', 'HF - Dizziness', 'Symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - DYSPNEA ON EXERTION', 'HF - Dyspnea on Exertion', 'Symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - FATIGUE', 'HF - Fatigue', 'Symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - ORTHOPNEA', 'HF - Orthopnea', 'Symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - PAROXYSMAL NOCTURNAL DYSPNEA', 'HF - Paroxysmal nocturnal dyspnea', 'Symptoms', 'yes/no', 15, CURDATE());

INSERT IGNORE INTO measurementType (type, typeDisplayName, typeDescription, measuringInstruction, validation, createDate)
VALUES ('HF - PHARMACOLOGICAL INTOLERANCE ', 'HF - Pharmacological Intolerance', 'Intolerance', 'Indicate intolerance', 11, CURDATE());

