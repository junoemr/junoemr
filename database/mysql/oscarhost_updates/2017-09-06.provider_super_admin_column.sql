SET SQL_SAFE_UPDATES=0;
ALTER TABLE provider ADD COLUMN super_admin tinyint(1) DEFAULT 0;
UPDATE provider SET super_admin=1 WHERE provider_no=999900;
SET SQL_SAFE_UPDATES=1;
