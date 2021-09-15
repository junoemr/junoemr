SET GLOBAL innodb_buffer_pool_size = 268435456;
ALTER TABLE log_ws_soap MODIFY soap_input TEXT COMPRESSED NULL;
ALTER TABLE log_ws_soap MODIFY soap_output TEXT COMPRESSED NULL;
ALTER TABLE log_ws_rest MODIFY raw_output TEXT COMPRESSED NULL;
ALTER TABLE log_ws_rest MODIFY raw_post TEXT COMPRESSED NULL;
ALTER TABLE casemgmt_note MODIFY history mediumtext COMPRESSED NOT NULL;
ALTER TABLE eform_data MODIFY form_data mediumtext COMPRESSED NULL;
ALTER TABLE eChart MODIFY encounter text COMPRESSED NULL;
SET GLOBAL innodb_buffer_pool_size = 134217728;
