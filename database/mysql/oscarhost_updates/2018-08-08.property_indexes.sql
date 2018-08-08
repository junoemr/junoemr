
-- some instances have indexes, remove them so we can standardize the index names
ALTER TABLE property DROP index IF EXISTS `provider_noIndex`;
ALTER TABLE property DROP index IF EXISTS `nameIndex`;

CREATE OR REPLACE INDEX `property_name_idx` ON property (`name`);
CREATE OR REPLACE INDEX `property_provider_no_idx` ON property (`provider_no`);
CREATE OR REPLACE INDEX `property_provider_no_name_idx` ON property (`provider_no`, `name`);