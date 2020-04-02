UPDATE demographic
SET month_of_birth=CONCAT("0", month_of_birth)
WHERE LENGTH(month_of_birth)=1;

UPDATE demographic
SET date_of_birth=CONCAT("0", date_of_birth)
WHERE LENGTH(date_of_birth)=1;