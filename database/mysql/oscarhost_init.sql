update program set defaultServiceRestrictionDays = 0 where defaultServiceRestrictionDays is null; 

-- Enable Rich Text Letter
update eform set status = 1 where form_name = "Rich Text Letter" and fid = 2;

-- Makes Lab Uploads faster
create index measurementVal_index ON measurementsExt(val(5), keyval);
