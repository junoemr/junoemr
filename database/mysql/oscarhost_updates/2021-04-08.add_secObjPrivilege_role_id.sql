ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS roleId INTEGER(10) after roleUserGroup;

UPDATE secObjPrivilege p JOIN secRole r ON (r.role_name = p.roleUserGroup)
    SET p.roleId = r.role_no
WHERE p.roleId IS NULL;

ALTER TABLE secObjPrivilege MODIFY roleId INTEGER(10) NOT NULL;
ALTER TABLE secObjPrivilege DROP primary key, ADD primary key(roleId, objectName);