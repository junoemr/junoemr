START TRANSACTION;

-- add deleted columns for roles
ALTER TABLE secRole ADD COLUMN IF NOT EXISTS deleted_at DATETIME DEFAULT NULL;
ALTER TABLE secRole ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(6) DEFAULT NULL;
-- names can't be database unique anymore with the soft delete
DROP INDEX IF EXISTS role_name ON secRole;

-- add secObjPrivilege role id and migrate primary key
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS secRoleId INTEGER(10) after roleUserGroup;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS deleted_at DATETIME DEFAULT NULL;

-- create new roles for provider specific permissions or any that do not have a matching role name
INSERT INTO secRole (role_name, description)
    SELECT distinct p.roleUserGroup, 'system generated from legacy data'
    FROM secObjPrivilege p LEFT JOIN secRole r ON (r.role_name = p.roleUserGroup)
    WHERE r.role_no IS NULL;

-- assign the role to the provider
INSERT INTO secUserRole (provider_no, role_name, orgcd, activeyn, lastUpdateDate)
    SELECT distinct pro.provider_no, r.role_name, null, 1, NOW()
    FROM secObjPrivilege p JOIN secRole r ON (r.role_name = p.roleUserGroup)
                           JOIN provider pro ON (pro.provider_no = r.role_name)
    WHERE pro.provider_no NOT IN (SELECT role_name FROM secUserRole WHERE provider_no = pro.provider_no)
    AND p.deleted_at IS NULL -- in case it is re-run
    AND r.deleted_at IS NULL;

-- fill null id's in the secRoleId column
UPDATE secObjPrivilege p JOIN secRole r ON (r.role_name = p.roleUserGroup)
    SET p.secRoleId = r.role_no
WHERE p.secRoleId IS NULL OR p.secRoleId = 0;

ALTER TABLE secObjPrivilege MODIFY secRoleId INTEGER(10) NOT NULL;
ALTER TABLE secObjPrivilege DROP primary key, ADD primary key(secRoleId, objectName);

-- add secUserRole sec_role_id column
ALTER TABLE secUserRole ADD COLUMN IF NOT EXISTS sec_role_id INTEGER(10) after id;

UPDATE secUserRole u JOIN secRole r ON (r.role_name = u.role_name)
    SET u.sec_role_id = r.role_no
WHERE u.sec_role_id IS NULL OR u.sec_role_id = 0;
ALTER TABLE secUserRole ADD FOREIGN KEY IF NOT EXISTS secUserRole_sec_role_id_fk(sec_role_id) REFERENCES secRole(role_no);

-- add system_managed flag to secRole
ALTER TABLE secRole ADD COLUMN IF NOT EXISTS system_managed BOOLEAN NOT NULL DEFAULT FALSE AFTER description;
UPDATE secRole SET system_managed = TRUE WHERE role_name IN ('admin', 'doctor');

-- add table for blacklisting patient sets
CREATE TABLE IF NOT EXISTS secDemographicSet
(
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    provider_id    VARCHAR(6)  NOT NULL,
    set_name       VARCHAR(20) NOT NULL,
    set_type       VARCHAR(16) NOT NULL,
    created_at     DATETIME    NOT NULL,
    created_by     VARCHAR(6)  NOT NULL,
    updated_at     DATETIME    NOT NULL,
    deleted_at     DATETIME   DEFAULT NULL,
    deleted_by     VARCHAR(6) DEFAULT NULL,
    INDEX set_name_idx (set_name),
    INDEX provider_id_idx (provider_id),
    CONSTRAINT `secDemographicSet_provider_id_fk` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_no`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- migrate permissions to separated columns
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_read BOOLEAN NOT NULL DEFAULT FALSE AFTER privilege;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_update BOOLEAN NOT NULL DEFAULT FALSE AFTER permission_read;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_create BOOLEAN NOT NULL DEFAULT FALSE AFTER permission_update;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS permission_delete BOOLEAN NOT NULL DEFAULT FALSE AFTER permission_create;

UPDATE secObjPrivilege SET permission_read = TRUE WHERE privilege IN ('r', 'u', 'w', 'd', 'x');
UPDATE secObjPrivilege SET permission_update = TRUE WHERE privilege IN ('u', 'w', 'd', 'x');
UPDATE secObjPrivilege SET permission_create = TRUE WHERE privilege IN ('w', 'd', 'x');
UPDATE secObjPrivilege SET permission_delete = TRUE WHERE privilege IN ('d', 'x');

-- add columns for role inheritance
ALTER TABLE secRole ADD COLUMN IF NOT EXISTS parent_sec_role_id int(10) DEFAULT NULL AFTER system_managed;
ALTER TABLE secObjPrivilege ADD COLUMN IF NOT EXISTS inclusive BOOLEAN NOT NULL DEFAULT TRUE AFTER objectName;
UPDATE secObjPrivilege SET inclusive = FALSE WHERE privilege IN ('o');

commit;

-- add triggers to ensure data correctness in systems with out of date war file
-- these triggers can be safely removed once all systems are running the new roles code.
DROP TRIGGER IF EXISTS secUserRole_sec_role_id_insert;
DROP TRIGGER IF EXISTS secUserRole_sec_role_id_update;

DELIMITER //

CREATE TRIGGER secUserRole_sec_role_id_insert
    BEFORE INSERT ON secUserRole
    FOR EACH ROW
BEGIN
    IF NEW.sec_role_id IS NULL THEN
        SET NEW.sec_role_id = (SELECT role_no FROM secRole s WHERE s.role_name = NEW.role_name);
    END IF;
END //

CREATE TRIGGER secUserRole_sec_role_id_update
    BEFORE UPDATE ON secUserRole
    FOR EACH ROW
BEGIN
    IF NEW.sec_role_id IS NULL THEN
        SET NEW.sec_role_id = (SELECT role_no FROM secRole s WHERE s.role_name = NEW.role_name);
    END IF;
END //

DELIMITER ;
