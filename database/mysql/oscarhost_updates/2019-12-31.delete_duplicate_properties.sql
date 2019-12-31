DELETE p1 FROM
property p1 JOIN property p2
WHERE p1.name = p2.name AND p1.provider_no = p2.provider_no AND p1.id > p2.id;