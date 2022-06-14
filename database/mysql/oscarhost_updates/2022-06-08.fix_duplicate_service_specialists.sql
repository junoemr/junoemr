BEGIN;
ALTER TABLE serviceSpecialists DROP INDEX IF EXISTS UC_service_specialists;
ALTER IGNORE TABLE serviceSpecialists ADD CONSTRAINT UC_service_specialists UNIQUE (specId, serviceId);
COMMIT;
