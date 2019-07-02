CREATE TABLE IF NOT EXISTS hl7_embedded_document_link(
    id int(10) AUTO_INCREMENT,
    lab_no int(10) NOT NULL,
    document_no int(20) NOT NULL,
    PRIMARY KEY(`id`),
    FOREIGN KEY lab_no_hl7TextInfo_fk(lab_no) REFERENCES hl7TextInfo(lab_no),
    FOREIGN KEY document_no_document_fk(document_no) REFERENCES document(document_no)
) ENGINE=InnoDb;