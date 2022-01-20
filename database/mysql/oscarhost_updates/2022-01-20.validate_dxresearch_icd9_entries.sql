-- For any entry with a coding system of icd9 but the dxresearch_code isn't in the icd9 table
-- set the coding_system to to null
UPDATE dxresearch
LEFT JOIN icd9 ON dxresearch_code = icd9.icd9
SET coding_system = icd9
WHERE dxresearch.coding_system = 'icd9'
AND icd9.icd9 IS NULL;