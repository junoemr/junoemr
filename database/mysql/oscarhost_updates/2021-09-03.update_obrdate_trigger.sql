DROP TRIGGER IF EXISTS cacheObrDate_plr_insert;
DROP TRIGGER IF EXISTS cacheObrDate_plr_update;

DROP PROCEDURE IF EXISTS getObrDate_plr;

DELIMITER //

-- Can't update a table in a procedure if it is the same one called by the invoking trigger.
-- In this case, let the trigger set the value as the row is created.

-- These triggers now only affect provider lab routing entries where the lab type is DOC or HL7

CREATE PROCEDURE getObrDate_plr(IN in_lab_no INT(10), IN in_lab_type VARCHAR(3), OUT out_obr_date DATETIME)
    SQL SECURITY INVOKER
BEGIN
IF in_lab_type = 'HL7' THEN
  SET out_obr_date = (SELECT hl7.obr_date FROM hl7TextInfo hl7 WHERE hl7.lab_no = in_lab_no);
ELSEIF in_lab_type = 'DOC' THEN
  SET out_obr_date = (SELECT doc.observationdate FROM document doc WHERE doc.document_no = in_lab_no);
END IF;
END //

CREATE TRIGGER cacheObrDate_plr_insert
    BEFORE INSERT ON providerLabRouting
    FOR EACH ROW
BEGIN
IF (NEW.lab_type = 'HL7' OR NEW.lab_type = 'DOC') THEN
  CALL getObrDate_plr(NEW.lab_no, NEW.lab_type, @obr_date);
  SET NEW.obr_date = @obr_date;
END IF;
END //

CREATE TRIGGER cacheObrDate_plr_update
    BEFORE UPDATE ON providerLabRouting
    FOR EACH ROW
BEGIN
IF (NEW.lab_type = 'HL7' OR NEW.lab_type = 'DOC') THEN
  CALL getObrDate_plr(NEW.lab_no, NEW.lab_type, @obr_date);
  SET NEW.obr_date = @obr_date;
END IF;
END //

DELIMITER ;
