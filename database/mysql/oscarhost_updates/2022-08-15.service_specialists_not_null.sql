BEGIN;

DELETE FROM serviceSpecialists WHERE serviceId IS NULL OR specId IS NULL;

ALTER TABLE serviceSpecialists MODIFY serviceID int(10) NOT NULL;
ALTER TABLE serviceSpecialists MODIFY specId int(10) NOT NULL;

COMMIT;