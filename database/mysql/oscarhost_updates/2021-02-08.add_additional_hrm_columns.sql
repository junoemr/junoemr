ALTER TABLE HRMDocument ADD COLUMN IF NOT EXISTS reportFileSchemaVersion VARCHAR(32) after reportFile;
ALTER TABLE HRMDocument ADD COLUMN IF NOT EXISTS sendingFacilityId VARCHAR(255) after sourceFacility;
ALTER TABLE HRMDocument ADD COLUMN IF NOT EXISTS sendingFacilityReportId VARCHAR(255) after sendingFacilityId;
ALTER TABLE HRMDocument ADD COLUMN IF NOT EXISTS messageUniqueId VARCHAR(255);
ALTER TABLE HRMDocument ADD COLUMN IF NOT EXISTS deliverToUserId VARCHAR(255);