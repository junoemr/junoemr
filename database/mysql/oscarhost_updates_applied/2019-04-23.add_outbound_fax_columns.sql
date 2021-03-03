
ALTER TABLE `fax_outbound` ADD COLUMN IF NOT EXISTS `external_status` VARCHAR(32) AFTER `external_reference_id`;
ALTER TABLE `fax_outbound` ADD COLUMN IF NOT EXISTS `external_delivery_date` datetime AFTER `external_status`;

ALTER TABLE `fax_outbound` ADD COLUMN IF NOT EXISTS `notification_status` VARCHAR(16) NOT NULL DEFAULT 'NOTIFY' AFTER `sent_to`;
ALTER TABLE `fax_outbound` ADD COLUMN IF NOT EXISTS `archived` TINYINT(1) NOT NULL DEFAULT 0 AFTER `notification_status`;
