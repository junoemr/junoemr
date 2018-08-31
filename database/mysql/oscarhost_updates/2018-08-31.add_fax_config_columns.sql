ALTER TABLE `fax_config` MODIFY `active` tinyint(1) NOT NULL default 0;

ALTER TABLE `fax_config` ADD COLUMN `active_inbound` tinyint(1) NOT NULL default 0 AFTER `active`;
ALTER TABLE `fax_config` ADD COLUMN `active_outbound` tinyint(1) NOT NULL default 0 AFTER `active_inbound`;
ALTER TABLE `fax_config` ADD COLUMN `display_name` varchar(255);
ALTER TABLE `fax_config` ADD COLUMN `deleted_at` datetime;
ALTER TABLE `fax_config` ADD COLUMN `cover_letter_option` varchar(255);
