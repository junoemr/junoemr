ALTER TABLE `appointment` ADD COLUMN IF NOT EXISTS `isVirtual` TINYINT DEFAULT false AFTER `urgency`;
