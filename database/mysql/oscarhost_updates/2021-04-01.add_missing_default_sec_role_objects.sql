INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_aqs.queueConfig', 'Configure appointment queues', 0);
INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_aqs.queuedAppointments', 'Access AQS queued appointments', 0);
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('admin', '_aqs.queueConfig', 'x', 0, '-1');
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('admin', '_aqs.queuedAppointments', 'x', 0, '-1');
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('doctor', '_aqs.queueConfig', 'r', 0, '-1');
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('doctor', '_aqs.queuedAppointments', 'x', 0, '-1');

INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_fax.documents', 'view inbound and outbound faxed documents', 0);
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('admin', '_fax.documents', 'x', 0, '-1');

-- ensure these older values exist
INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_demographicExport', 'Access to the demographic export tools', 0);
INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_demographicImport', 'Access to the demographic import tools', 0);
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('admin', '_tasks', 'x', 0, '-1');
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('admin', '_admin.fax', 'x', 0, '-1');
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) VALUES ('admin', '_admin.userAdmin', 'x', 0, '-1');