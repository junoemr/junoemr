INSERT INTO `property`(`name`, `value`, provider_no)
SELECT
    "cobalt",
    "yes",
    provider_no
FROM provider;