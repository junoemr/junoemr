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

-- Indexes
create index scheduledate_sdate on scheduledate(sdate);
create index scheduledate_provider_no on scheduledate (provider_no);
create index scheduledate_status on scheduledate (status);
create index scheduledate_key1 on scheduledate (sdate,provider_no,hour,status);
create index secObjPrivilege_objectName on secObjPrivilege (objectName);
create index appointment_date_provider on appointment ( appointment_date, provider_no);
create index appointment_status_active on appointment_status(active);
create index eform_form_name on eform(form_name);


-- New column to store the requesting client number for labs
alter table hl7TextInfo add column requesting_client_no varchar(20);
