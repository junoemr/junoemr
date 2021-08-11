
ALTER TABLE provider CONVERT TO CHARACTER SET utf8;
ALTER TABLE provider MODIFY COLUMN comments mediumtext;
ALTER TABLE provider MODIFY COLUMN alberta_e_delivery_ids mediumtext;
ALTER TABLE provider MODIFY COLUMN booking_notification_numbers mediumtext;