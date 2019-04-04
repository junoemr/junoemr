SET sql_safe_updates=0;
ALTER TABLE scheduledate ADD COLUMN site_id INT(11);
UPDATE scheduledate sd JOIN site st ON sd.reason = st.name SET sd.site_id = st.site_id WHERE sd.site_id IS NULL;