
-- add a new colour column for the new schedule to use
ALTER TABLE `appointment_status` ADD COLUMN IF NOT EXISTS `juno_color` CHAR(7) AFTER `color`;
-- set the default status specific values;
UPDATE `appointment_status` SET `juno_color`='#8ac6e6' WHERE `juno_color` IS NULL AND BINARY `status` = 't';
UPDATE `appointment_status` SET `juno_color`='#8ac6e6' WHERE `juno_color` IS NULL AND BINARY `status` = 'T';
UPDATE `appointment_status` SET `juno_color`='#abd96c' WHERE `juno_color` IS NULL AND BINARY `status` = 'H';
UPDATE `appointment_status` SET `juno_color`='#e66588' WHERE `juno_color` IS NULL AND BINARY `status` = 'P';
UPDATE `appointment_status` SET `juno_color`='#ffea80' WHERE `juno_color` IS NULL AND BINARY `status` = 'E';
UPDATE `appointment_status` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL AND BINARY `status` = 'N';
UPDATE `appointment_status` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL AND BINARY `status` = 'C';
UPDATE `appointment_status` SET `juno_color`='#8a99e6' WHERE `juno_color` IS NULL AND BINARY `status` = 'B';
-- set all remaining null values to the custom colour code
UPDATE `appointment_status` SET `juno_color`='#a599ff' WHERE `juno_color` IS NULL;


-- add a new colour column for the new schedule to use
ALTER TABLE `scheduletemplatecode` ADD COLUMN IF NOT EXISTS `juno_color` CHAR(7) AFTER `color`;
-- set some specific values based on code duration;
UPDATE `scheduletemplatecode` SET `juno_color`='#aed9d9' WHERE `juno_color` IS NULL AND code='1';
UPDATE `scheduletemplatecode` SET `juno_color`='#aed9d9' WHERE `juno_color` IS NULL AND code='2';
UPDATE `scheduletemplatecode` SET `juno_color`='#aed9d9' WHERE `juno_color` IS NULL AND code='3';
UPDATE `scheduletemplatecode` SET `juno_color`='#aed9d9' WHERE `juno_color` IS NULL AND code='4';
UPDATE `scheduletemplatecode` SET `juno_color`='#ffc5bf' WHERE `juno_color` IS NULL AND code='9';
UPDATE `scheduletemplatecode` SET `juno_color`='#fff4bf' WHERE `juno_color` IS NULL AND code='s';
UPDATE `scheduletemplatecode` SET `juno_color`='#ffd9b3' WHERE `juno_color` IS NULL AND code='L';

UPDATE `scheduletemplatecode` SET `juno_color`='#b8c0e6' WHERE `juno_color` IS NULL AND duration = '5';
UPDATE `scheduletemplatecode` SET `juno_color`='#d2ccff' WHERE `juno_color` IS NULL AND duration = '10';
UPDATE `scheduletemplatecode` SET `juno_color`='#cab6e3' WHERE `juno_color` IS NULL AND duration = '15';
UPDATE `scheduletemplatecode` SET `juno_color`='#e693a9' WHERE `juno_color` IS NULL AND duration = '30';
-- set all remaining null values to the custom colour code
UPDATE `scheduletemplatecode` SET `juno_color`='#acb2bf' WHERE `juno_color` IS NULL;