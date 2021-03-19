INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "B", "Community Health Centre", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "B"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "J", "First Nations Primary Health Care Clinic", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "J"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "K", "Hybrid Primary Care Practice (part-time longitudinal practice, part-time walk-in clinic)", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "K"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "L", "Hybrid Primary Care Practice (part-time longitudinal practice, part-time walk-in clinic)", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "L"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "N", "Health Care Practitioner Office (non-physician)", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "N"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "Q", "Specialist Physician Office", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "Q"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "U", "Urgent and Primary Care Centre", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "U"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "V", "Virtual Care Clinic", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "V"
) LIMIT 1;

INSERT INTO billingvisit (`visittype`, `visit_desc`, `region`)
SELECT * FROM (SELECT "W", "Walk-In Clinic", "BC") AS tmp
WHERE NOT EXISTS (
    SELECT visittype FROM billingvisit WHERE visittype = "W"
) LIMIT 1;
