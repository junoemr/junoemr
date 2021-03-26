-- make all provider records either 1 or 0 status
UPDATE provider SET status='0' WHERE status != '1';