INSERT IGNORE INTO `property`(`name`, `value`, provider_no)
SELECT
    "cobalt",
    "yes",
    provider_no
FROM provider
WHERE provider_no != "-1";

-- provider_no="999998" does get a default value, but is set to "no" prior to this
UPDATE `property`
SET `value`="yes"
WHERE `name`="cobalt";