UPDATE provider set last_name = "_" WHERE last_name IS NULL OR TRIM(last_name) = "";
UPDATE provider set first_name = "_" WHERE first_name IS NULL OR TRIM(first_name) = "";
