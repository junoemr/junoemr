INSERT INTO secObjPrivilege VALUES ('doctor', '_dashboardDisplay', 'r', 0, 999998) ON DUPLICATE KEY UPDATE privilege = 'r';
INSERT INTO secObjPrivilege VALUES ('doctor', '_dashboardManager', 'r', 0, 999998) ON DUPLICATE KEY UPDATE privilege = 'r';
INSERT INTO secObjPrivilege VALUES ('doctor', '_dashboardDrilldown', 'r', 0, 999998) ON DUPLICATE KEY UPDATE privilege = 'r';
