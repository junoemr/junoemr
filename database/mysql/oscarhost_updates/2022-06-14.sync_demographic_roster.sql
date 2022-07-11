BEGIN;

INSERT INTO demographic_roster(demographic_no, rostered_physician, ohip_no, roster_status_id, roster_date, roster_termination_date, roster_termination_reason)
SELECT d.demographic_no, 
	IF(d.family_doctor_2 != '<fd></fd>' AND ExtractValue(d.family_doctor_2, '//fdname') != '', ExtractValue(d.family_doctor_2, '//fdname'), NULL), 
	IF(d.family_doctor_2 != '<fd></fd>' AND ExtractValue(d.family_doctor_2, '//fd') != '', ExtractValue(d.family_doctor_2, '//fd'), NULL), 
	(SELECT id FROM roster_status WHERE roster_status = d.roster_status), 
	IF(d.roster_date != '', d.roster_date, NULL), 
	IF(d.roster_termination_date != '' AND d.roster_status != 'RO', d.roster_termination_date, NULL), 
CASE
	WHEN d.roster_termination_reason = 12 THEN 'HEALTH_NUM'
	WHEN d.roster_termination_reason = 14 THEN 'MINISTRY_REPORTED_DECEASED'
	WHEN d.roster_termination_reason = 24 THEN 'ASSIGNED_IN_ERROR'
	WHEN d.roster_termination_reason = 30 THEN 'REGISTERED_RED_CARD'
	WHEN d.roster_termination_reason = 32 THEN 'REGISTERED_PHOTO_CARD'
	WHEN d.roster_termination_reason = 33 THEN 'CONFIDENTIAL'
	WHEN d.roster_termination_reason = 35 THEN 'TRANSFERRED'
	WHEN d.roster_termination_reason = 36 THEN 'RE_ENROLLED'
	WHEN d.roster_termination_reason = 37 THEN 'ENTERED_LONG_TERM_CARE'
	WHEN d.roster_termination_reason = 38 THEN 'LEFT_LONG_TERM_CARE'
	WHEN d.roster_termination_reason = 39 THEN 'ASSIGNMENT_ENDED'
	WHEN d.roster_termination_reason = 40 THEN 'PHYSICIAN_REPORTED_DECEASED'
	WHEN d.roster_termination_reason = 41 THEN 'NO_LONGER_MEETS_CRITERIA_REASSIGNED'
	WHEN d.roster_termination_reason = 42 THEN 'PHYSICIAN_ENDED_LONG_TERM_CARE'
	WHEN d.roster_termination_reason = 44 THEN 'PHYSICIAN_ENDED_PATIENT_ENROLMENT'
	WHEN d.roster_termination_reason = 51 THEN 'NO_LONGER_MEETS_CRITERIA'
	WHEN d.roster_termination_reason = 53 THEN 'LEFT_GEOGRAPHIC_AREA'
	WHEN d.roster_termination_reason = 54 THEN 'LEFT_PROVINCE'
	WHEN d.roster_termination_reason = 56 THEN 'PATIENT_REQUESTED_END'
	WHEN d.roster_termination_reason = 57 THEN 'PATIENT_TERMINATED_ENROLMENT'
	WHEN d.roster_termination_reason = 59 THEN 'OUT_OF_GEOGRAPHIC_AREA'
	WHEN d.roster_termination_reason = 60 THEN 'NO_CURRENT_ELIGIBILITY'
	WHEN d.roster_termination_reason = 61 THEN 'OUT_OF_GEOGRAPHIC_AREA_OVERRIDE_APPLIED'
	WHEN d.roster_termination_reason = 62 THEN 'OUT_OF_GEOGRAPHIC_AREA_OVERRIDE_REMOVED'
	WHEN d.roster_termination_reason = 73 THEN 'NO_ELIGIBILITY_73'
	WHEN d.roster_termination_reason = 74 THEN 'NO_ELIGIBILITY_74'
	WHEN d.roster_termination_reason = 82 THEN 'NO_CONSENT_FORM'
	WHEN d.roster_termination_reason = 84 THEN 'CONFIDENTIAL_84'
	WHEN d.roster_termination_reason = 90 THEN 'CONFIDENTIAL_90'
	WHEN d.roster_termination_reason = 91 THEN 'CONFIDENTIAL_91'
	ELSE NULL
END
FROM demographic d
WHERE d.roster_status IS NOT NULL 
AND d.roster_status != ""
AND d.demographic_no NOT IN(SELECT DISTINCT demographic_no FROM demographic_roster);

COMMIT;
