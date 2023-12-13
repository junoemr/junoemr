UPDATE
issue iss JOIN icd9 d9 ON (d9.icd9=iss.code AND d9.description=iss.description)
SET iss.type='ICD9'
WHERE iss.type IS NULL
;