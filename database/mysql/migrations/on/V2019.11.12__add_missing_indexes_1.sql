CREATE INDEX IF NOT EXISTS accession_index ON hl7TextInfo(accessionNum);
CREATE INDEX IF NOT EXISTS appointment_no_idx ON appointmentArchive(appointment_no);

CREATE INDEX IF NOT EXISTS archive_id_idx ON demographicExtArchive(archiveId);
CREATE INDEX IF NOT EXISTS datesIndex ON raheader(paymentdate, readdate);
CREATE INDEX IF NOT EXISTS dem_inde_stat_date_time_Index ON eform_data(demographic_no, patient_independent, status, form_date, form_time);
CREATE INDEX IF NOT EXISTS demographicNoIdIndex ON eChart(demographicNo, eChartId);
CREATE INDEX IF NOT EXISTS demographicNoIndex ON consultationRequests(demographicNo);
CREATE INDEX IF NOT EXISTS demographic_noIndex ON dxresearch(demographic_no);
CREATE INDEX IF NOT EXISTS demographic_noIndex ON formRourke2009(demographic_no);
CREATE INDEX IF NOT EXISTS demographic_provider_no_fk ON demographic(provider_no);
CREATE INDEX IF NOT EXISTS demo_last_first_hin_sex_Index ON demographic(demographic_no, last_name, first_name, hin, sex);

CREATE INDEX IF NOT EXISTS demo_script_pos_rxdate_Index ON drugs(demographic_no, script_no, position, rx_date, drugid);
CREATE INDEX IF NOT EXISTS demo_status_dateIndex ON tickler(demographic_no, status, service_date);
CREATE INDEX IF NOT EXISTS drugs_demographic_no ON drugs(demographic_no);
CREATE INDEX IF NOT EXISTS eform_values_varname_varvalue ON eform_values(var_name, var_value (30));
CREATE INDEX IF NOT EXISTS icd9Index ON icd9(icd9);
CREATE INDEX IF NOT EXISTS id_by_subject_Index ON messagetbl(messageid, sentby, thesubject);
CREATE INDEX IF NOT EXISTS id_que_doc_status_Index ON queue_document_link(id, queue_id, document_id, status);

CREATE INDEX IF NOT EXISTS md5Index ON fileUploadCheck(md5sum);
CREATE INDEX IF NOT EXISTS module ON ctl_document(module);
CREATE INDEX IF NOT EXISTS myOscarUserName ON demographic(myOscarUserName);
CREATE INDEX IF NOT EXISTS note_idIndex ON casemgmt_note_ext(note_id);
CREATE INDEX IF NOT EXISTS note_idIndex ON casemgmt_note_link(note_id);
CREATE INDEX IF NOT EXISTS objectNameIndex ON secObjPrivilege(objectName);
CREATE INDEX IF NOT EXISTS patient_independentIndex ON eform_data(patient_independent);
CREATE INDEX IF NOT EXISTS property_provider_no_name_idx ON property(provider_no, name);

CREATE INDEX IF NOT EXISTS provider_demoIndex ON casemgmt_tmpsave(provider_no, demographic_no);
CREATE INDEX IF NOT EXISTS provider_lab_status_index ON providerLabRouting(provider_no, status);
CREATE INDEX IF NOT EXISTS provider_noIndex ON log(provider_no);
CREATE INDEX IF NOT EXISTS providerohip_noIndex ON radetail(providerohip_no);
CREATE INDEX IF NOT EXISTS regionalIndex ON drugs(regional_identifier);
CREATE INDEX IF NOT EXISTS sdate_2 ON scheduledate(sdate, provider_no, hour, status);
CREATE INDEX IF NOT EXISTS special_instructionIndex ON drugs(special_instruction (5));
CREATE INDEX IF NOT EXISTS status_type_Index ON document(status, doctype);
CREATE INDEX IF NOT EXISTS table_id_idx ON casemgmt_note_link(table_id);
