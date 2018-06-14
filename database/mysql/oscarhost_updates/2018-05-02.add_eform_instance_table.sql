-- creates a table for instancing eForms
CREATE TABLE IF NOT EXISTS `eform_instance`  (
  id                      BIGINT UNSIGNED AUTO_INCREMENT,
  eform_id                BIGINT UNSIGNED NOT NULL,
  current_eform_data_id   BIGINT UNSIGNED,
  created_at              DATETIME        NOT NULL,
  deleted                 tinyint(1)      NOT NULL DEFAULT 0,

  PRIMARY KEY (`id`),
  INDEX `idx_eform_id` (`eform_id`),
  INDEX `idx_current_eform_data_id` (`current_eform_data_id`)
);

ALTER TABLE `eform` ADD COLUMN IF NOT EXISTS `instanced` tinyint(1) NOT NULL DEFAULT 0;

ALTER TABLE `eform_data` ADD COLUMN IF NOT EXISTS `eform_instance_id` BIGINT UNSIGNED DEFAULT NULL;