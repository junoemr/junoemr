ALTER TABLE secRole ADD COLUMN IF NOT EXISTS deleted_at DATETIME DEFAULT NULL;
ALTER TABLE secRole ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(6) DEFAULT NULL;

-- names can't be database unique anymore with the soft delete
DROP INDEX IF EXISTS role_name ON secRole;

ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS deleted_at DATETIME DEFAULT NULL;