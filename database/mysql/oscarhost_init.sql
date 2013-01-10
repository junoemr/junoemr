update program set defaultServiceRestrictionDays = 0 where defaultServiceRestrictionDays is null; 

-- Makes the SQL for EDD Report 2007 faster
create index formEdited_index ON formBCAR2007(demographic_no, formEdited);

-- Takes care of "Prefs" page error
alter table eform alter patient_independent SET DEFAULT 0;

-- Enable Rich Text Letter
update eform set status = 1 where form_name = "Rich Text Letter" and fid = 2;
