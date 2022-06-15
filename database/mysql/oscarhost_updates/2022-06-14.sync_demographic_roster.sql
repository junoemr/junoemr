BEGIN;

ALTER TABLE demographic_roster MODIFY roster_termination_reason VARCHAR(128);

INSERT INTO demographic_roster(demographic_no, rostered_physician, ohip_no, roster_status_id, roster_date, roster_termination_date, roster_termination_reason)

SELECT d.demographic_no, 
	CONCAT(p.last_name, ", ", p.first_name), 
	IF(p.ohip_no != '', p.ohip_no, NULL), 
	(SELECT id FROM roster_status WHERE roster_status = d.roster_status), 
	IF(d.roster_date != '', d.roster_date, NULL), 
	IF(d.roster_termination_date != '', d.roster_termination_date, NULL), 
CASE
	WHEN d.roster_termination_reason = '' THEN NULL
	WHEN d.roster_termination_reason = 12 THEN 'Health Number error'
	WHEN d.roster_termination_reason = 14 THEN 'Patient identified as deceased on ministry database'
	WHEN d.roster_termination_reason = 24 THEN 'Patient added to roster in error'
	WHEN d.roster_termination_reason = 30 THEN 'Pre-member/ Assigned member ended; now enrolled or registered with red and white health card'
	WHEN d.roster_termination_reason = 32 THEN 'Pre-member/ Assigned member ended; now enrolled or registered with photo health card'
	WHEN d.roster_termination_reason = 33 THEN 'Termination reason cannot be released (due to patient confidentiality)'
	WHEN d.roster_termination_reason = 35 THEN 'Patient transferred from roster per physician request'
	WHEN d.roster_termination_reason = 36 THEN 'Original enrolment ended; patient now re-enroled'
	WHEN d.roster_termination_reason = 37 THEN 'Original enrolment ended; patient now enrolled as Long Term Care'
	WHEN d.roster_termination_reason = 38 THEN 'Long Term Care enrolment ended; patient has left Long Term Care'
	WHEN d.roster_termination_reason = 39 THEN 'Assigned member status ended; roster transferred per physician request'
	WHEN d.roster_termination_reason = 40 THEN 'Physician reported member as deceased'
	WHEN d.roster_termination_reason = 41 THEN 'Patient no longer meets selection criteria for your roster - assigned to another physician'
	WHEN d.roster_termination_reason = 42 THEN 'Physician ended enrolment; patient entered Long Term Care facility'
	WHEN d.roster_termination_reason = 44 THEN 'Physician ended patient enrolment'
	WHEN d.roster_termination_reason = 51 THEN 'Patient no longer meets selection criteria for your roster'
	WHEN d.roster_termination_reason = 53 THEN 'Physician ended enrolment; patient moved out of geographic area'
	WHEN d.roster_termination_reason = 54 THEN 'Physician ended enrolment; patient left province'
	WHEN d.roster_termination_reason = 56 THEN 'Physician ended enrolment; per patient request'
	WHEN d.roster_termination_reason = 57 THEN 'Enrolment terminated by patient'
	WHEN d.roster_termination_reason = 59 THEN 'Enrolment ended; patient out of geographic area'
	WHEN d.roster_termination_reason = 60 THEN 'No current eligibility'
	WHEN d.roster_termination_reason = 61 THEN 'Patient out of geographic area; address over-ride applied'
	WHEN d.roster_termination_reason = 62 THEN 'Patient out of geographic area; address over-ride removed'
	WHEN d.roster_termination_reason = 73 THEN 'No current eligibility'
	WHEN d.roster_termination_reason = 74 THEN 'No current eligibility'
	WHEN d.roster_termination_reason = 82 THEN 'Ministry has not received enrolment/ Consent form'
	WHEN d.roster_termination_reason = 84 THEN 'Termination reason cannot be released (due to patient confidentiality)'
	WHEN d.roster_termination_reason = 90 THEN 'Termination reason cannot be released (due to patient confidentiality)'
	WHEN d.roster_termination_reason = 91 THEN 'Termination reason cannot be released (due to patient confidentiality)'
END
FROM demographic d
JOIN provider p ON d.provider_no = p.provider_no
WHERE d.roster_status IS NOT NULL 
AND d.roster_status != ""
AND d.demographic_no NOT IN(SELECT demographic_no FROM demographic_roster);

COMMIT;
