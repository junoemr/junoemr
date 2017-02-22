BEGIN;

ALTER TABLE hl7TextInfo CHANGE discipline discipline varchar(200);

COMMIT;
