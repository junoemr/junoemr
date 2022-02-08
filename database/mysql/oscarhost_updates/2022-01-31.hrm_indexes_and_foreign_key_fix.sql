begin;

alter table HRMDocumentToDemographic modify hrmDocumentId int(11) not null;
alter table HRMDocumentToDemographic modify demographicNo int(10) not null;
alter table HRMDocumentToProvider modify hrmDocumentId int(11) not null;

CREATE INDEX HRMDocumentToProvider_hrmDocumentId ON HRMDocumentToProvider (hrmDocumentId);
CREATE INDEX HRMDocumentToDemographic_hrmDocumentId ON HRMDocumentToDemographic (hrmDocumentId);

commit;
