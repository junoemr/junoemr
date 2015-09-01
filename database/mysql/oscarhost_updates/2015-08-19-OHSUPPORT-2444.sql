
CREATE INDEX idx_eform_values_varname_fid_fdid ON eform_values (var_name, fid, fdid);

CREATE INDEX idx_measurementsExt_measurement_id_keyval ON measurementsExt (measurement_id, keyval);

CREATE INDEX idx_drugs_demographic_no_archived ON drugs (demographic_no, archived);