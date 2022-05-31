BEGIN;

INSERT INTO demographic_roster(demographic_no, rostered_physician, ohip_no, roster_status_id, roster_date, roster_termination_date, roster_termination_reason)

SELECT d.demographic_no, 
	CONCAT(p.last_name, ", ", p.first_name), 
	p.ohip_no, 
	(SELECT id FROM roster_status WHERE roster_status = d.roster_status), 
	d.roster_date, 
	d.roster_termination_date, 
	d.roster_termination_reason
FROM demographic d
JOIN provider p ON d.provider_no = p.provider_no
WHERE d.roster_status IS NOT NULL 
AND d.roster_status != ""
AND d.demographic_no NOT IN(SELECT demographic_no FROM demographic_roster);

COMMIT;
