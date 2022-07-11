UPDATE providerLabRouting plr JOIN HRMDocument hrm ON (hrm.id = plr.lab_no AND plr.lab_type='HRM')
SET plr.obr_date = hrm.reportDate
WHERE plr.obr_date IS NULL;