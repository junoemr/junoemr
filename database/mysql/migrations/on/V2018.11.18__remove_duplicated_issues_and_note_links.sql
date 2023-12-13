begin;

-- update the casemgmt_issues to use max issue ids
UPDATE casemgmt_issue cmi, (
SELECT i.issue_id, i.type, i.code, maxes.max_id
FROM issue i
JOIN
(SELECT type, code, MAX(issue_id) max_id
FROM issue
GROUP BY type, code HAVING count(*) > 1) AS maxes ON (i.type = maxes.type AND i.code = maxes.code)
WHERE i.issue_id != maxes.max_id
) AS datas
SET cmi.issue_id = datas.max_id
WHERE cmi.issue_id = datas.issue_id
;

-- delete duplicated issues
DELETE FROM issue WHERE issue_id IN (
SELECT issue_id FROM (
SELECT i.issue_id
FROM issue i
JOIN
(SELECT type, code, MAX(issue_id) max_id
FROM issue
GROUP BY type, code HAVING count(*) > 1) AS maxes ON (i.type = maxes.type AND i.code = maxes.code)
WHERE i.issue_id != maxes.max_id
) AS to_delete
);

-- update note links
UPDATE casemgmt_issue_notes, (
SELECT ci.demographic_no, ci.issue_id, ci.id, maxes.max_id
FROM casemgmt_issue ci JOIN(
SELECT demographic_no, issue_id, MAX(id) max_id
FROM casemgmt_issue
GROUP BY 1, 2
HAVING count(*) > 1) AS maxes ON ci.demographic_no = maxes.demographic_no AND ci.issue_id = maxes.issue_id
WHERE maxes.demographic_no IS NOT NULL
AND ci.id != maxes.max_id
) AS datas
SET  casemgmt_issue_notes.id = datas.max_id
WHERE casemgmt_issue_notes.id = datas.id;

-- delete duplicated casemgmt_issues
DELETE FROM casemgmt_issue WHERE id IN (
SELECT id FROM (
SELECT ci.id
FROM casemgmt_issue ci JOIN(
SELECT demographic_no, issue_id, MAX(id) max_id
FROM casemgmt_issue
GROUP BY 1, 2
HAVING count(*) > 1) AS maxes ON ci.demographic_no = maxes.demographic_no AND ci.issue_id = maxes.issue_id
WHERE maxes.demographic_no IS NOT NULL
AND ci.id != maxes.max_id
) AS c
);

commit;