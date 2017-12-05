CREATE TABLE demographic_custom_status (id int(10) AUTO_INCREMENT PRIMARY KEY, status varchar(128));

ALTER TABLE demographic ADD COLUMN custom_status_id int(10) AFTER patient_status;
