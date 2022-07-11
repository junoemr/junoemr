-- Keep this file in oscarhost_updates and don't move to applied, as it will be needed when moving instances
ALTER TABLE providerLabRouting add COLUMN IF NOT EXISTS obr_date DATETIME;

-- These statements can be run repeatedly without affecting the integrity of the data.

UPDATE providerLabRouting plr
JOIN document doc
  ON doc.document_no = plr.lab_no
    AND plr.lab_type='DOC'
SET plr.obr_date = doc.observationdate
  WHERE plr.lab_type='DOC';


-- We want to match each hl7 lab_no with its minimum obr_date in order to match the logic
-- of the original (uncached) inbox search.

UPDATE providerLabRouting plr
JOIN hl7TextInfo hl7
  ON hl7.lab_no = plr.lab_no
  AND plr.lab_type='HL7'
SET plr.obr_date = hl7.obr_date
  WHERE plr.lab_type='HL7';

-- Use triggers to cache the obr_date/Observation date in providerLabRouting table when any of
-- providerLabRouting, document, or hl7TextInfo are created or updated

DROP PROCEDURE IF EXISTS getObrDate_plr;
DROP PROCEDURE IF EXISTS cacheObrDate_doc;
DROP PROCEDURE IF EXISTS cacheObrDate_hl7;

DROP TRIGGER IF EXISTS cacheObrDate_plr_insert;
DROP TRIGGER IF EXISTS cacheObrDate_plr_update;
DROP TRIGGER IF EXISTS cacheObrDate_doc_insert;
DROP TRIGGER IF EXISTS cacheObrDate_doc_update;
DROP TRIGGER IF EXISTS cacheObrDate_hl7_insert;
DROP TRIGGER IF EXISTS cacheObrDate_hl7_update;

DELIMITER //

CREATE PROCEDURE cacheObrDate_hl7(IN in_hl7_no INT(20), IN in_obr_date DATE)
SQL SECURITY INVOKER
BEGIN
UPDATE providerLabRouting plr
SET plr.obr_date = in_obr_date
WHERE plr.lab_no = in_hl7_no
AND plr.lab_type = 'HL7';
END //

CREATE PROCEDURE cacheObrDate_doc(IN in_doc_no INT(20), IN in_obr_date DATE)
SQL SECURITY INVOKER
BEGIN
UPDATE providerLabRouting plr
SET plr.obr_date = in_obr_date
WHERE plr.lab_no = in_doc_no
  AND plr.lab_type = 'DOC';
END //

CREATE PROCEDURE getObrDate_plr(IN in_lab_no INT(10), IN in_lab_type VARCHAR(3), OUT out_obr_date DATETIME)
SQL SECURITY INVOKER
BEGIN
IF in_lab_type = 'HL7' THEN
  SET out_obr_date = (SELECT hl7.obr_date FROM hl7TextInfo hl7 WHERE hl7.lab_no = in_lab_no);
ELSEIF in_lab_type = 'DOC' THEN
  SET out_obr_date = (SELECT doc.observationdate FROM document doc WHERE doc.document_no = in_lab_no);
ELSE
  SET out_obr_date = NULL;
END IF;

END //

-- Can't update a table in a procedure if it is the same one called by the invoking trigger.
-- In this case, let the trigger set the value as the row is created.
CREATE TRIGGER cacheObrDate_plr_insert
BEFORE INSERT ON providerLabRouting
FOR EACH ROW
BEGIN
IF NEW.lab_type IN ('DOC', 'HL7') THEN
  CALL getObrDate_plr(NEW.lab_no, NEW.lab_type, @obr_date);
  SET NEW.obr_date = @obr_date;
END IF;
END //

CREATE TRIGGER cacheObrDate_plr_update
BEFORE UPDATE ON providerLabRouting
FOR EACH ROW
BEGIN
IF NEW.lab_type IN ('DOC', 'HL7') THEN
  CALL getObrDate_plr(NEW.lab_no, NEW.lab_type, @obr_date);
  SET NEW.obr_date = @obr_date;
END IF;
END //

CREATE TRIGGER cacheObrDate_doc_insert
AFTER INSERT ON document
FOR EACH ROW
BEGIN
CALL cacheObrDate_doc(NEW.document_no, NEW.observationdate);
END //

CREATE TRIGGER cacheObrDate_doc_update
AFTER UPDATE ON document
FOR EACH ROW
BEGIN
CALL cacheObrDate_doc(NEW.document_no, NEW.observationdate);
END //

CREATE TRIGGER cacheObrDate_hl7_insert
AFTER INSERT ON hl7TextInfo
FOR EACH ROW
BEGIN
CALL cacheObrDate_hl7(NEW.lab_no, NEW.obr_date);
END //

CREATE TRIGGER cacheObrDate_hl7_update
AFTER UPDATE ON hl7TextInfo
FOR EACH ROW
BEGIN
CALL cacheObrDate_hl7(NEW.lab_no, NEW.obr_date);
END //
DELIMITER ;