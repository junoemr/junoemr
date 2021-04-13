ALTER TABLE secUserRole ADD COLUMN IF NOT EXISTS sec_role_id INTEGER(10) after id;

UPDATE secUserRole u JOIN secRole r ON (r.role_name = u.role_name)
    SET u.sec_role_id = r.role_no
WHERE u.sec_role_id IS NULL OR u.sec_role_id = 0;
ALTER TABLE secUserRole MODIFY sec_role_id INTEGER(10) NOT NULL;
ALTER TABLE secUserRole ADD FOREIGN KEY IF NOT EXISTS secUserRole_sec_role_id_fk(sec_role_id) REFERENCES secRole(role_no);