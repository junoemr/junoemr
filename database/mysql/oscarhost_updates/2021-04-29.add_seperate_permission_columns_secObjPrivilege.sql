ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_read BOOLEAN NOT NULL DEFAULT FALSE AFTER privilege;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_update BOOLEAN NOT NULL DEFAULT FALSE AFTER permission_read;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_create BOOLEAN NOT NULL DEFAULT FALSE AFTER permission_update;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_delete BOOLEAN NOT NULL DEFAULT FALSE AFTER permission_create;

UPDATE secObjPrivilege SET permission_read = TRUE WHERE privilege IN ('r', 'u', 'w', 'd', 'x');
UPDATE secObjPrivilege SET permission_update = TRUE WHERE privilege IN ('u', 'w', 'd', 'x');
UPDATE secObjPrivilege SET permission_create = TRUE WHERE privilege IN ('w', 'd', 'x');
UPDATE secObjPrivilege SET permission_delete = TRUE WHERE privilege IN ('d', 'x');