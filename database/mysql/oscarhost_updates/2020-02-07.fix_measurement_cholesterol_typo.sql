UPDATE measurementMap
SET name="Total Cholesterol"
WHERE name="Total Cholestorol";

UPDATE measurementType
SET typeDisplayName="Total Cholesterol", typeDescription="Total Cholesterol"
WHERE type='TCHL' AND typeDisplayName="Total Cholestorol" AND typeDescription="Total Cholestorol";