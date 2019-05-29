
-- add a new colour column for the new schedule to use
ALTER TABLE `appointment_status` ADD COLUMN IF NOT EXISTS `juno_color` CHAR(7) AFTER `color`;
-- set the default status specific values;
UPDATE `appointment_status` SET `juno_color`='#75592f' WHERE `juno_color` IS NULL AND BINARY `status` = 't';
UPDATE `appointment_status` SET `juno_color`='#75592f' WHERE `juno_color` IS NULL AND BINARY `status` = 'T';
UPDATE `appointment_status` SET `juno_color`='#66a600' WHERE `juno_color` IS NULL AND BINARY `status` = 'H';
UPDATE `appointment_status` SET `juno_color`='#cc3367' WHERE `juno_color` IS NULL AND BINARY `status` = 'P';
UPDATE `appointment_status` SET `juno_color`='#e6b000' WHERE `juno_color` IS NULL AND BINARY `status` = 'E';
UPDATE `appointment_status` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL AND BINARY `status` = 'N';
UPDATE `appointment_status` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL AND BINARY `status` = 'C';
UPDATE `appointment_status` SET `juno_color`='#476bb3' WHERE `juno_color` IS NULL AND BINARY `status` = 'B';
-- set all remaining null values to the custom colour code
UPDATE `appointment_status` SET `juno_color`='#6437de' WHERE `juno_color` IS NULL;


-- add a new colour column for the new schedule to use
ALTER TABLE `scheduletemplatecode` ADD COLUMN IF NOT EXISTS `juno_color` CHAR(7) AFTER `color`;
-- set some specific values based on code duration;
UPDATE `scheduletemplatecode` SET `juno_color`='#476bb3' WHERE `juno_color` IS NULL AND duration = '5';
UPDATE `scheduletemplatecode` SET `juno_color`='#43aebf' WHERE `juno_color` IS NULL AND duration = '10';
UPDATE `scheduletemplatecode` SET `juno_color`='#ac32c7' WHERE `juno_color` IS NULL AND duration = '15';
UPDATE `scheduletemplatecode` SET `juno_color`='#cc3367' WHERE `juno_color` IS NULL AND duration = '30';
-- set all remaining null values to the custom colour code
UPDATE `scheduletemplatecode` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL;