CREATE TABLE IF NOT EXISTS aqs_queued_appointment_appointment (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    aqs_queued_appointment_id VARCHAR(256) NOT NULL,
    aqs_queued_id VARCHAR(256) NOT NULL,
    appointment_no INT(12) NOT NULL,
    CONSTRAINT fk_aqs_queued_appointment_appointment FOREIGN KEY (appointment_no) REFERENCES appointment (appointment_no),
    CONSTRAINT unique_appointment_no_aqs_queued_appointment_id UNIQUE (appointment_no, aqs_queued_appointment_id),
    INDEX idx_appointment_no (appointment_no)
);