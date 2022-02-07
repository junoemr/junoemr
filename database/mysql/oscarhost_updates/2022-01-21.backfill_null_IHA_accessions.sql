-- shouldn't have null accession numbers in lab entries
UPDATE hl7TextInfo ti JOIN hl7TextMessage tm ON(ti.lab_no = tm.lab_id)
  SET ti.accessionNum = CONCAT(tm.type, '-', ti.lab_no)
  WHERE ti.accessionNum IS NULL
  AND tm.type = 'IHA'