UPDATE measurementMap
SET name="Total Cholesterol"
WHERE name="Total Cholestorol"
AND loinc_code="14647-2"
AND ident_code="CHO";

UPDATE measurementType
SET typeDisplayName="Total Cholesterol", typeDescription="Total Cholesterol"
WHERE type='TCHL' AND typeDisplayName="Total Cholestorol" AND typeDescription="Total Cholestorol";