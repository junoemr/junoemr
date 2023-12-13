INSERT INTO property(`name`, `value`, `provider_no`)
SELECT login_records.name, login_records.value, login_records.provider_no
FROM (
	SELECT "schedule_count_enabled" AS `name`,
	"true" AS `value`,
	p.provider_no
	FROM provider p
	JOIN security s
	ON p.provider_no=s.provider_no
	WHERE p.status="1"
) AS login_records
LEFT JOIN property p
ON login_records.provider_no=p.provider_no
AND p.name="schedule_count_enabled"
WHERE p.id IS NULL;

INSERT INTO property(`name`, `value`, `provider_no`)
SELECT login_records.name, login_records.value, login_records.provider_no
FROM (
	SELECT "schedule_count_include_cancelled" AS `name`,
	"false" AS `value`,
	p.provider_no
	FROM provider p
	JOIN security s
	ON p.provider_no=s.provider_no
	WHERE p.status="1"
) AS login_records
LEFT JOIN property p
ON login_records.provider_no=p.provider_no
AND p.name="schedule_count_include_cancelled"
WHERE p.id IS NULL;

INSERT INTO property(`name`, `value`, `provider_no`)
SELECT login_records.name, login_records.value, login_records.provider_no
FROM (
	SELECT "schedule_count_include_noshow" AS `name`,
	"false" AS `value`,
	p.provider_no
	FROM provider p
	JOIN security s
	ON p.provider_no=s.provider_no
	WHERE p.status="1"
) AS login_records
LEFT JOIN property p
ON login_records.provider_no=p.provider_no
AND p.name="schedule_count_include_noshow"
WHERE p.id IS NULL;

INSERT INTO property(`name`, `value`, `provider_no`)
SELECT login_records.name, login_records.value, login_records.provider_no
FROM (
	SELECT "schedule_count_include_no_demographic" AS `name`,
	"false" AS `value`,
	p.provider_no
	FROM provider p
	JOIN security s
	ON p.provider_no=s.provider_no
	WHERE p.status="1"
) AS login_records
LEFT JOIN property p
ON login_records.provider_no=p.provider_no
AND p.name="schedule_count_include_no_demographic"
WHERE p.id IS NULL;
