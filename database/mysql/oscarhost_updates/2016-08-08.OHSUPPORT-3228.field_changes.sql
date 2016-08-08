ALTER TABLE `demographic` CHANGE COLUMN `parent_lname` `name_of_mother` VARCHAR(32);
ALTER TABLE `demographic` CHANGE COLUMN `parent_fname` `name_of_father` VARCHAR(32);

ALTER TABLE `demographicArchive` CHANGE COLUMN `parent_lname` `name_of_mother` VARCHAR(32);
ALTER TABLE `demographicArchive` CHANGE COLUMN `parent_fname` `name_of_father` VARCHAR(32);