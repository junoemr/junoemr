-- remove broken ovulation form from use
DELETE FROM encounterForm WHERE form_value='../form/patientEncounterWorksheet.jsp?demographic_no=';