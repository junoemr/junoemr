-- Add access to professional specialists

INSERT IGNORE INTO secObjPrivilege (roleUserGroup,objectName,privilege,priority,provider_no)
VALUES ('doctor', '_admin.consult', 'x', 0, '999998');