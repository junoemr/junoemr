ALTER TABLE `Flowsheet` MODIFY COLUMN `name` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `Flowsheet` MODIFY COLUMN `enabled` TINYINT(1) NOT NULL DEFAULT 1;
ALTER TABLE `Flowsheet` MODIFY COLUMN `external` TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE `Flowsheet` ADD UNIQUE `Flowsheet_name`(`name`);

ALTER TABLE `FlowSheetUserCreated` MODIFY COLUMN `name` VARCHAR(255) DEFAULT NULL;

-- Note: inserting any of these prior to code changes being made available
-- will result in inaccessible flowsheets until the instance is restarted.
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("hiv", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("inrFlow", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("ASTH", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("M8", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("chf", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("hyptension", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("diab2", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("diab3", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("ckd", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("pain", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("phv", 1, 1, CURDATE());
INSERT IGNORE INTO Flowsheet(`name`, `enabled`, `external`, `createdDate`) VALUES ("tracker", 1, 1, CURDATE());