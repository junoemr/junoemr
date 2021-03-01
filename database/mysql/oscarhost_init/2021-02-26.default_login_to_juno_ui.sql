INSERT IGNORE INTO `property`(`name`, `value`, provider_no)
SELECT
    "cobalt",
    "yes",
    provider_no
FROM provider
WHERE provider_no != "-1";
