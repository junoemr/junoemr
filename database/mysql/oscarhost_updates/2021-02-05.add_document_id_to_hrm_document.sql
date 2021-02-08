ALTER TABLE HRMDocument ADD COLUMN IF NOT EXISTS documentId INTEGER(20);
ALTER TABLE HRMDocument ADD FOREIGN KEY IF NOT EXISTS HRMDocument_documentId_fk(documentId) REFERENCES document(document_no);