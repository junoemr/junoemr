START TRANSACTION;

ALTER TABLE `provider_billing` ADD COLUMN `tmp_provider_no` VARCHAR(6) DEFAULT NULL;

INSERT IGNORE INTO provider_billing(bc_bcp_eligible, tmp_provider_no)
SELECT FALSE, provider_no
FROM provider
WHERE provider_billing_id IS NULL;

UPDATE provider prov
JOIN provider_billing bill
ON prov.provider_no=bill.tmp_provider_no
SET prov.provider_billing_id = bill.id
WHERE prov.provider_billing_id IS NULL;

ALTER TABLE `provider_billing` DROP COLUMN `tmp_provider_no`;

COMMIT;
