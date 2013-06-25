update program set defaultServiceRestrictionDays = 0 where defaultServiceRestrictionDays is null; 

-- Enable Rich Text Letter
update eform set status = 1 where form_name = "Rich Text Letter" and fid = 2;

-- Makes Lab Uploads faster
create index measurementVal_index ON measurementsExt(val(5), keyval);

-- Make the demographic_merged_full table for doing full demographic merges
create table demographic_merged_full (
	provider_no int,
	to_demographic_no int,
	from_demographic_no int,
	created_tstamp timestamp
);
