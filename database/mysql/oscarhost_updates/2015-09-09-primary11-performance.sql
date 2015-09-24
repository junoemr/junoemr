
CREATE INDEX idx_program_programStatus ON program (programStatus);

CREATE INDEX idx_admission_program_id_admission_status ON admission (program_id, admission_status);


CREATE INDEX idx_billing_provider_no_billing_no_demographic_no ON billing (provider_no, billing_no, demographic_no);


CREATE INDEX idx_measurements_demographicNo_type_id ON measurements (demographicNo, type, id);


CREATE INDEX idx_casemgmt_tmpsave_provider_no_demographic_no_program_id ON casemgmt_tmpsave (provider_no, demographic_no, program_id);


CREATE INDEX idx_billactivity_updatedatetime_id ON billactivity (updatedatetime, id);


CREATE INDEX idx_casemgmt_note_reporter_caisi_role ON casemgmt_note (reporter_caisi_role);


CREATE INDEX idx_providerLabRouting_provider_no_status_lab_no_lab_type ON providerLabRouting (provider_no, status, lab_no, lab_type);


CREATE INDEX idx_scheduledate_sdate_available_status ON scheduledate (sdate, available, status);


CREATE INDEX idx_dsGuidelines_uuid_status_dateStart ON dsGuidelines (uuid, status, dateStart);


CREATE INDEX idx_demographic_roster_status ON demographic (roster_status);


CREATE INDEX idx_patientLabRouting_demographic_no_lab_type_lab_no ON patientLabRouting (demographic_no, lab_type, lab_no);


CREATE INDEX idx_eChart_demographicNo_eChartId ON eChart (demographicNo, eChartId);


CREATE INDEX idx_billing_demographic_no_billingtype ON billing (demographic_no, billingtype);


CREATE INDEX idx_messagelisttbl_provider_no_status_remoteLocation_message ON messagelisttbl (provider_no, status, remoteLocation, message);


CREATE INDEX idx_consultationRequests_demographicNo ON consultationRequests (demographicNo);


CREATE INDEX idx_demographic_patient_status ON demographic (patient_status);


CREATE INDEX idx_secObjPrivilege_objectName_priority ON secObjPrivilege (objectName, priority);


CREATE INDEX idx_demographicArchive_demographic_no ON demographicArchive (demographic_no);


CREATE INDEX idx_scheduledate_sdate_provider_no_status ON scheduledate (sdate, provider_no, status);


CREATE INDEX idx_log_action_provider_no_content_contentId ON log (action, provider_no, content, contentId);



