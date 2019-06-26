
-- add a new colour column for the new schedule to use
ALTER TABLE `appointment_status` ADD COLUMN IF NOT EXISTS `juno_color` CHAR(7) AFTER `color`;
-- set the default status specific values;
UPDATE `appointment_status` SET `juno_color`='#755e3a' WHERE `juno_color` IS NULL AND BINARY `status` = 't';
UPDATE `appointment_status` SET `juno_color`='#755e3a' WHERE `juno_color` IS NULL AND BINARY `status` = 'T';
UPDATE `appointment_status` SET `juno_color`='#86a653' WHERE `juno_color` IS NULL AND BINARY `status` = 'H';
UPDATE `appointment_status` SET `juno_color`='#cc6688' WHERE `juno_color` IS NULL AND BINARY `status` = 'P';
UPDATE `appointment_status` SET `juno_color`='#e6cb73' WHERE `juno_color` IS NULL AND BINARY `status` = 'E';
UPDATE `appointment_status` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL AND BINARY `status` = 'N';
UPDATE `appointment_status` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL AND BINARY `status` = 'C';
UPDATE `appointment_status` SET `juno_color`='#6b83b3' WHERE `juno_color` IS NULL AND BINARY `status` = 'B';
-- set all remaining null values to the custom colour code
UPDATE `appointment_status` SET `juno_color`='#8d6fde' WHERE `juno_color` IS NULL;


-- add a new colour column for the new schedule to use
ALTER TABLE `scheduletemplatecode` ADD COLUMN IF NOT EXISTS `juno_color` CHAR(7) AFTER `color`;
-- set some specific values based on code duration;
UPDATE `scheduletemplatecode` SET `juno_color`='#6b83b3' WHERE `juno_color` IS NULL AND duration = '5';
UPDATE `scheduletemplatecode` SET `juno_color`='#73b5bf' WHERE `juno_color` IS NULL AND duration = '10';
UPDATE `scheduletemplatecode` SET `juno_color`='#cc6688' WHERE `juno_color` IS NULL AND duration = '15';
UPDATE `scheduletemplatecode` SET `juno_color`='#b563c7' WHERE `juno_color` IS NULL AND duration = '30';
-- set all remaining null values to the custom colour code
UPDATE `scheduletemplatecode` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL;