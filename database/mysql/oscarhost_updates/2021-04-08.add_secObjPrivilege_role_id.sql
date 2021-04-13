ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS secRoleId INTEGER(10) after roleUserGroup;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS deleted_at DATETIME DEFAULT NULL;

UPDATE secObjPrivilege p JOIN secRole r ON (r.role_name = p.roleUserGroup)
    SET p.secRoleId = r.role_no
WHERE p.secRoleId IS NULL OR p.secRoleId = 0;

ALTER TABLE secObjPrivilege MODIFY secRoleId INTEGER(10) NOT NULL;
ALTER TABLE secObjPrivilege DROP primary key, ADD primary key(secRoleId, objectName);