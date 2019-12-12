INSERT INTO property(`name`, `value`, `provider_no`)
SELECT `name`, `value`, `provider_no`
FROM (
	SELECT "schedule_count_enabled" AS `name`,
	"true" AS `value`,
	p.provider_no
	FROM provider p
	JOIN security s
	ON p.provider_no=s.provider_no
	WHERE p.status="1"
) AS login_records
WHERE `provider_no` NOT IN (
	SELECT `provider_no`
	FROM property
	WHERE name="schedule_count_enabled"
);