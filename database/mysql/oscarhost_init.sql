update program set defaultServiceRestrictionDays = 0 where defaultServiceRestrictionDays is null; 

-- Makes the SQL for EDD Report 2007 faster
create index formEdited_index ON formBCAR2007(demographic_no, formEdited);

-- Enable Rich Text Letter
update eform set status = 1 where form_name = "Rich Text Letter" and fid = 2;

-- Makes Lab Uploads faster
create index measurementVal_index ON measurementsExt(val(5), keyval);
