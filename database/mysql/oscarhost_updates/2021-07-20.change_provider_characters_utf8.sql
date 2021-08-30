
-- drop inconsistent foreign keys
ALTER TABLE `ClientLink` DROP FOREIGN KEY IF EXISTS `ClientLink_ibfk_1`;
ALTER TABLE `ClientLink` DROP FOREIGN KEY IF EXISTS `ClientLink_ibfk_2`;
ALTER TABLE `ClientLink` DROP FOREIGN KEY IF EXISTS `ClientLink_ibfk_3`;
ALTER TABLE `ClientLink` DROP FOREIGN KEY IF EXISTS `ClientLink_ibfk_4`;
ALTER TABLE `DigitalSignature` DROP FOREIGN KEY IF EXISTS `DigitalSignature_ibfk_2`;
ALTER TABLE `HnrDataValidation` DROP FOREIGN KEY IF EXISTS `HnrDataValidation_ibfk_2`;
ALTER TABLE `HnrDataValidation` DROP FOREIGN KEY IF EXISTS `HnrDataValidation_ibfk_3`;
ALTER TABLE `IntegratorConsent` DROP FOREIGN KEY IF EXISTS `IntegratorConsent_ibfk_2`;
ALTER TABLE `IntegratorConsent` DROP FOREIGN KEY IF EXISTS `IntegratorConsent_ibfk_3`;
ALTER TABLE `IntegratorConsentComplexExitInterview` DROP FOREIGN KEY IF EXISTS `IntegratorConsentComplexExitInterview_ibfk_2`;
ALTER TABLE `program_client_restriction` DROP FOREIGN KEY IF EXISTS `FK_pcr_demographic`;
ALTER TABLE `program_client_restriction` DROP FOREIGN KEY IF EXISTS `FK_pcr_provider`;
ALTER TABLE `sharing_document_export` DROP FOREIGN KEY IF EXISTS `fk_sharing_document_export_demographic_no`;

-- drop known foreign keys before changing charset
ALTER TABLE `demographic` DROP FOREIGN KEY IF EXISTS `demographic_provider_no_fk`;
ALTER TABLE `demographic_roster` DROP FOREIGN KEY IF EXISTS `demographic_roster_demographic_no_fk`;
ALTER TABLE `demographic_integration` DROP FOREIGN KEY IF EXISTS `demographic_integration_demographic_no_fk`;

-- update charset & unify column types (some change with charset)
ALTER TABLE demographic CONVERT TO CHARACTER SET utf8;
ALTER TABLE `demographic_integration` CONVERT TO CHARACTER SET utf8;
ALTER TABLE `demographic_roster` CONVERT TO CHARACTER SET utf8;
ALTER TABLE provider CONVERT TO CHARACTER SET utf8;
ALTER TABLE provider MODIFY COLUMN comments mediumtext;
ALTER TABLE provider MODIFY COLUMN alberta_e_delivery_ids mediumtext;
ALTER TABLE provider MODIFY COLUMN booking_notification_numbers mediumtext;

-- rebuild known foreign keys
ALTER TABLE `demographic`ADD CONSTRAINT `demographic_provider_no_fk` FOREIGN KEY (`provider_no`) REFERENCES `provider` (`provider_no`) ON DELETE SET NULL;
ALTER TABLE `demographic_roster`ADD CONSTRAINT `demographic_roster_demographic_no_fk` FOREIGN KEY (`demographic_no`) REFERENCES `demographic` (`demographic_no`);
ALTER TABLE `demographic_integration`ADD CONSTRAINT `demographic_integration_demographic_no_fk` FOREIGN KEY (`demographic_no`) REFERENCES `demographic` (`demographic_no`) ON DELETE CASCADE ON UPDATE CASCADE;
