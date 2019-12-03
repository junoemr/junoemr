SET FOREIGN_KEY_CHECKS=0;
DROP PROCEDURE IF EXISTS DropAndCreateTables;

DELIMITER $$
CREATE PROCEDURE DropAndCreateTables()
BEGIN
	DROP TABLE integration;
	DROP TABLE user_integration_access;

	CREATE TABLE `integration` (
		`id` int(6) NOT NULL AUTO_INCREMENT,
		`integration_type` varchar(40) NOT NULL,
		`site_id` int(11) DEFAULT NULL,
		`remote_id` varchar(40) NOT NULL,
		`api_key` varchar(255) DEFAULT NULL,
		PRIMARY KEY (`id`),
		KEY `integration_site_id_fkey` (`site_id`),
		CONSTRAINT `integration_site_id_fkey` FOREIGN KEY (`site_id`) REFERENCES `site` (`site_id`) ON DELETE CASCADE ON UPDATE NO ACTION
	);

	CREATE TABLE `user_integration_access` (
		`id` int(6) NOT NULL AUTO_INCREMENT,
		`integration_id` int(6) NOT NULL,
		`security_no` int(6) NOT NULL,
		`remote_user_id` varchar(40) NOT NULL,
		`api_key` varchar(255) NOT NULL,
		PRIMARY KEY (`id`),
		KEY `user_access_integration_id_fk` (`integration_id`),
		KEY `user_access_security_no_fk` (`security_no`),
		CONSTRAINT `user_access_integration_id_fk` FOREIGN KEY (`integration_id`) REFERENCES `integration` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
		CONSTRAINT `user_access_security_no_fk` FOREIGN KEY (`security_no`) REFERENCES `security` (`security_no`) ON DELETE CASCADE ON UPDATE NO ACTION
  );
END $$
DELIMITER ;

DROP PROCEDURE IF EXISTS DropCreateIfOutdated;

DELIMITER $$
CREATE PROCEDURE DropCreateIfOutdated()
BEGIN
	DECLARE pri_col_i VARCHAR(20) DEFAULT NULL;

	SELECT database() INTO @db;

	SELECT COLUMN_NAME INTO pri_col_i
		FROM information_schema.COLUMNS
		WHERE TABLE_SCHEMA = @db
		AND TABLE_NAME='integration' AND COLUMN_KEY='PRI';

	IF pri_col_i = 'remote_id' THEN
		# Table is outdated. Drop & Create
        CALL DropAndCreateTables();
	END IF;
END $$
DELIMITER ;

CALL DropCreateIfOutdated();

DROP PROCEDURE DropCreateIfOutdated;
DROP PROCEDURE DropAndCreateTables;

SET FOREIGN_KEY_CHECKS=1;