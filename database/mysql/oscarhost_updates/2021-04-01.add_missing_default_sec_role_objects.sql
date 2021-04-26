
-- add some new values to existing roles to ensure a smoother transition
INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_aqs.queueConfig', 'Configure appointment queues', 0);
INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_aqs.queuedAppointments', 'Access AQS queued appointments', 0);
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'admin', '_aqs.queueConfig', 'x', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='admin' AND objectName='_aqs.queueConfig' HAVING count(*) = 0;
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'admin', '_aqs.queuedAppointments', 'x', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='admin' AND objectName='_aqs.queuedAppointments' HAVING count(*) = 0;
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'doctor', '_aqs.queueConfig', 'r', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='doctor' AND objectName='_aqs.queueConfig' HAVING count(*) = 0;
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'doctor', '_aqs.queuedAppointments', 'x', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='doctor' AND objectName='_aqs.queuedAppointments' HAVING count(*) = 0;
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'admin', '_fax.documents', 'x', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='admin' AND objectName='_fax.documents' HAVING count(*) = 0;

-- ensure these older values exist
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'admin', '_tasks', 'x', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='admin' AND objectName='_tasks' HAVING count(*) = 0;
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'admin', '_admin.fax', 'x', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='admin' AND objectName='_admin.fax' HAVING count(*) = 0;
INSERT IGNORE INTO secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) SELECT 'admin', '_admin.userAdmin', 'x', 0, '-1'
  FROM secObjPrivilege WHERE roleUserGroup='admin' AND objectName='_admin.userAdmin' HAVING count(*) = 0;

-- insert & update these values to ensure they exist
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.backup', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.billing', NULL, 0);
UPDATE secObjectName SET description = 'Access to billing configuration' WHERE objectName='_admin.billing' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.caisi', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.caisiRoles', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.consult', NULL, 0);
UPDATE secObjectName SET description = 'Access to consultation configuration' WHERE objectName='_admin.consult' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.cookieRevolver', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.document', NULL, 0);
UPDATE secObjectName SET description = 'Access to document configuration' WHERE objectName='_admin.document' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.eform', NULL, 0);
UPDATE secObjectName SET description = 'Access to eForm configuration' WHERE objectName='_admin.eform' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.eformreporttool', NULL, 0);
UPDATE secObjectName SET description = 'Access to eForm reporting' WHERE objectName='_admin.eformreporttool' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.encounter', NULL, 0);
UPDATE secObjectName SET description = 'Access to encounter configuration' WHERE objectName='_admin.encounter' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.facilityMessage', NULL, 0);
UPDATE secObjectName SET description = 'Access to facility management configuration' WHERE objectName='_admin.facilityMessage' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.fieldnote', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.invoices', NULL, 0);
UPDATE secObjectName SET description = 'Access to invoice configuration' WHERE objectName='_admin.invoices' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.issueEditor', NULL, 0);
UPDATE secObjectName SET description = 'Access to issue configuration' WHERE objectName='_admin.issueEditor' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.lookupFieldEditor', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.panelManagement', NULL, 0);
UPDATE secObjectName SET description = 'Access to BC doctors panel management' WHERE objectName='_admin.panelManagement' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.provider', NULL, 0);
UPDATE secObjectName SET description = 'Access to provider configuration' WHERE objectName='_admin.provider' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.reporting', NULL, 0);
UPDATE secObjectName SET description = 'Access to system reports' WHERE objectName='_admin.reporting' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.schedule', NULL, 0);
UPDATE secObjectName SET description = 'Access to schedule configuration' WHERE objectName='_admin.schedule' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.security', NULL, 0);
UPDATE secObjectName SET description = 'Access to security role and user access configuration' WHERE objectName='_admin.security' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.securityLogReport', NULL, 0);
UPDATE secObjectName SET description = 'Access to the security log reporting' WHERE objectName='_admin.securityLogReport' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.sharingcenter', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.systemMessage', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.traceability', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.unlockAccount', NULL, 0);
UPDATE secObjectName SET description = 'Access to account unlocking' WHERE objectName='_admin.unlockAccount' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.userAdmin', NULL, 0);
UPDATE secObjectName SET description = 'Access to user configuration' WHERE objectName='_admin.userAdmin' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.userCreatedForms', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_allergy', NULL, 0);
UPDATE secObjectName SET description = 'Access to allergies data' WHERE objectName='_allergy' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_appDefinition', NULL, 0);
UPDATE secObjectName SET description = 'Access to App Definition data (know2Act integration, etc.)' WHERE objectName='_appDefinition' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_appointment', NULL, 0);
UPDATE secObjectName SET description = 'Access to appointment data' WHERE objectName='_appointment' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_appointment.doctorLink', NULL, 0);
UPDATE secObjectName SET description = 'Access to appointment doctor link' WHERE objectName='_appointment.doctorLink' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_billing', NULL, 0);
UPDATE secObjectName SET description = 'Access to billing data' WHERE objectName='_billing' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.A1C', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.Access1AdmissionDate', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.ACR', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.Age', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.ApptsLYTD', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.BMI', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.BP', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.CashAdmissionDate', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.DisplayMode', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.Doc', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.EGFR', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.EYEE', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.HDL', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.Lab', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.LastAppt', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.LastEncounterDate', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.LastEncounterType', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.LDL', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.Msg', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.NextAppt', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.Sex', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.SMK', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.TCHD', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.Tickler', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_caseload.WT', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_casemgmt.issues', NULL, 0);
UPDATE secObjectName SET description = 'Access to case issue data' WHERE objectName='_casemgmt.issues' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_casemgmt.notes', NULL, 0);
UPDATE secObjectName SET description = 'Access to case notes data' WHERE objectName='_casemgmt.notes' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_con', NULL, 0);
UPDATE secObjectName SET description = 'Access to consultation data' WHERE objectName='_con' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_dashboardDisplay', NULL, 0);
UPDATE secObjectName SET description = 'Access to dashboard display data' WHERE objectName='_dashboardDisplay' AND description IS NULL;
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_dashboardDrilldown', NULL, 0);
UPDATE secObjectName SET description = 'Access to dashboard drilldown data' WHERE objectName='_dashboardDisplay' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_dashboardManager', NULL, 0);
UPDATE secObjectName SET description = 'Access to dashboard configuration' WHERE objectName='_dashboardManager' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_day', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_demographic', NULL, 0);
UPDATE secObjectName SET description = 'Access to demographic data' WHERE objectName='_demographic' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_demographicExport', NULL, 0);
UPDATE secObjectName SET description = 'Access to the demographic export tools' WHERE objectName='_demographicExport' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, `description`, orgapplicable) VALUES ('_demographicImport', NULL, 0);
UPDATE secObjectName SET description = 'Access to the demographic import tools' WHERE objectName='_demographicImport' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_dxresearch', NULL, 0);
UPDATE secObjectName SET description = 'Access to disease registry data' WHERE objectName='_dxresearch' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_eChart', NULL, 0);
UPDATE secObjectName SET description = 'Access to the eChart' WHERE objectName='_eChart' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_eChart.verifyButton', NULL, 0);
UPDATE secObjectName SET description = 'Access to the eChart verify button' WHERE objectName='_eChart.verifyButton' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_edoc', NULL, 0);
UPDATE secObjectName SET description = 'Access to document data' WHERE objectName='_edoc' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_eform', NULL, 0);
UPDATE secObjectName SET description = 'Access to eForm data' WHERE objectName='_eform' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_eform.doctor', NULL, 0);
UPDATE secObjectName SET description = 'Access to eForm doctor data' WHERE objectName='_eform.doctor' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_eyeform', NULL, 0);
UPDATE secObjectName SET description = 'Access to eye form data' WHERE objectName='_eyeform' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_admin.fax', NULL, 0);
UPDATE secObjectName SET description = 'Access to fax configuration' WHERE objectName='_admin.fax' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_fax.documents', NULL, 0);
UPDATE secObjectName SET description = 'Access to inbound and outbound faxed documents' WHERE objectName='_fax.documents' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_form', NULL, 0);
UPDATE secObjectName SET description = 'Access to form data' WHERE objectName='_form' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_hrm', NULL, 0);
UPDATE secObjectName SET description = 'Access to Hospital Report Management data' WHERE objectName='_hrm' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_lab', NULL, 0);
UPDATE secObjectName SET description = 'Access to lab data' WHERE objectName='_lab' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_masterLink', NULL, 0);
UPDATE secObjectName SET description = 'Access to demographic master link' WHERE objectName='_masterLink' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_measurement', NULL, 0);
UPDATE secObjectName SET description = 'Access to measurement data' WHERE objectName='_measurement' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_month', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_msg', NULL, 0);
UPDATE secObjectName SET description = 'Access to messages data' WHERE objectName='_msg' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.allergies', NULL, 0);
UPDATE secObjectName SET description = 'Access to allergies under new case-management' WHERE objectName='_newCasemgmt.allergies' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.apptHistory', NULL, 0);
UPDATE secObjectName SET description = 'Access to appointment history under new case-management' WHERE objectName='_newCasemgmt.apptHistory' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.calculators', NULL, 0);
UPDATE secObjectName SET description = 'Access to calculators under new case-management' WHERE objectName='_newCasemgmt.calculators' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.consultations', NULL, 0);
UPDATE secObjectName SET description = 'Access to consultations under new case-management' WHERE objectName='_newCasemgmt.consultations' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.cpp', NULL, 0);
UPDATE secObjectName SET description = 'Access to cpp notes under new case-management' WHERE objectName='_newCasemgmt.cpp' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.decisionSupportAlerts', NULL, 0);
UPDATE secObjectName SET description = 'Access to decision support alerts under new case-management' WHERE objectName='_newCasemgmt.decisionSupportAlerts' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.doctorName', NULL, 0);
UPDATE secObjectName SET description = 'Access to doctor name under new case-management' WHERE objectName='_newCasemgmt.doctorName' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.documents', NULL, 0);
UPDATE secObjectName SET description = 'Access to documents under new case-management' WHERE objectName='_newCasemgmt.documents' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.DxRegistry', NULL, 0);
UPDATE secObjectName SET description = 'Access to disease registry under new case-management' WHERE objectName='_newCasemgmt.DxRegistry' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.eForms', NULL, 0);
UPDATE secObjectName SET description = 'Access to eForms under new case-management' WHERE objectName='_newCasemgmt.eForms' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.episode', NULL, 0);
UPDATE secObjectName SET description = 'Access to episode under new case-management' WHERE objectName='_newCasemgmt.episode' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.familyHistory', NULL, 0);
UPDATE secObjectName SET description = 'Access to family history notes under new case-management' WHERE objectName='_newCasemgmt.familyHistory' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.forms', NULL, 0);
UPDATE secObjectName SET description = 'Access to forms under new case-management' WHERE objectName='_newCasemgmt.forms' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.labResult', NULL, 0);
UPDATE secObjectName SET description = 'Access to labs under new case-management' WHERE objectName='_newCasemgmt.labResult' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.measurements', NULL, 0);
UPDATE secObjectName SET description = 'Access to measurements under new case-management' WHERE objectName='_newCasemgmt.measurements' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.medicalHistory', NULL, 0);
UPDATE secObjectName SET description = 'Access to medical history notes under new case-management' WHERE objectName='_newCasemgmt.medicalHistory' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.oscarMsg', NULL, 0);
UPDATE secObjectName SET description = 'Access to messages under new case-management' WHERE objectName='_newCasemgmt.oscarMsg' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.otherMeds', NULL, 0);
UPDATE secObjectName SET description = 'Access to additional medications under new case-management' WHERE objectName='_newCasemgmt.otherMeds' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.photo', NULL, 0);
UPDATE secObjectName SET description = 'Access to patient photo under new case-management' WHERE objectName='_newCasemgmt.photo' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.pregnancy', NULL, 0);
UPDATE secObjectName SET description = 'Access to pregnancy data under new case-management' WHERE objectName='_newCasemgmt.pregnancy' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.prescriptions', NULL, 0);
UPDATE secObjectName SET description = 'Access to prescriptions under new case-management' WHERE objectName='_newCasemgmt.prescriptions' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.preventions', NULL, 0);
UPDATE secObjectName SET description = 'Access to preventions under new case-management' WHERE objectName='_newCasemgmt.preventions' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.riskFactors', NULL, 0);
UPDATE secObjectName SET description = 'Access to risk factor notes under new case-management' WHERE objectName='_newCasemgmt.riskFactors' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.templates', NULL, 0);
UPDATE secObjectName SET description = 'Access to templates under new case-management' WHERE objectName='_newCasemgmt.templates' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_newCasemgmt.viewTickler', NULL, 0);
UPDATE secObjectName SET description = 'Access to ticklers under new case-management' WHERE objectName='_newCasemgmt.viewTickler' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_phr', NULL, 0);
UPDATE secObjectName SET description = 'Access to PHR data' WHERE objectName='_phr' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.addProgram', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.agencyInformation', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.caisiRoles', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.caseManagement', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.clientSearch', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.editor', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.globalRoleAccess', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.manageFacilities', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.mergeRecords', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.newClient', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.programList', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm.staffList', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_agencyList', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_client.BedRoomReservation', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.access', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.bedCheck', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.clients', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.clientStatus', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.functionUser', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.general', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.queue', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.serviceRestrictions', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.staff', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.teams', NULL, 0);
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pmm_editProgram.vacancies', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_pref', NULL, 0);
UPDATE secObjectName SET description = 'Access to preferences' WHERE objectName='_pref' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_prevention', NULL, 0);
UPDATE secObjectName SET description = 'Access to prevention data' WHERE objectName='_prevention' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_report', NULL, 0);
UPDATE secObjectName SET description = 'Access to reports' WHERE objectName='_report' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_resource', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_rx', NULL, 0);
UPDATE secObjectName SET description = 'Access to rx data' WHERE objectName='_rx' AND description IS NULL;
INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_rx.dispense', NULL, 0);
UPDATE secObjectName SET description = 'Access to rx dispensing data' WHERE objectName='_rx.dispense' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_search', NULL, 0);

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_tasks', NULL, 0);
UPDATE secObjectName SET description = 'Access to scheduled tasks' WHERE objectName='_tasks' AND description IS NULL;

INSERT IGNORE INTO secObjectName (objectName, description, orgapplicable) VALUES ('_tickler', NULL, 0);
UPDATE secObjectName SET description = 'Access to tickler data' WHERE objectName='_tickler' AND description IS NULL;