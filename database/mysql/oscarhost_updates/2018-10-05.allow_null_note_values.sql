
-- must remove the NOT NULL clause from the column
ALTER TABLE `casemgmt_note` MODIFY `signing_provider_no` varchar(20);