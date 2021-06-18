START TRANSACTION;

SELECT COUNT(*) INTO @provider_billing_count FROM provider_billing;

-- This is to get X number of entries linked up
-- i.e. if we have 10 providers but we only need 3 entries, 7 will fail and 3 will insert
INSERT IGNORE INTO provider_billing(bc_bcp_eligible)
SELECT FALSE
FROM provider;

UPDATE provider
SET provider_billing_id = (@provider_billing_count := @provider_billing_count + 1)
WHERE provider_billing_id IS NULL;

COMMIT;
