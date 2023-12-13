ALTER TABLE `security` ADD COLUMN IF NOT EXISTS `myhealthaccess_auth_token` TEXT AFTER `pin`;
