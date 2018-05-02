ALTER TABLE `log_ws_rest`
ADD COLUMN `request_media_type` VARCHAR(255) AFTER `url`,
ADD COLUMN `method` VARCHAR(6) AFTER `request_media_type`,
ADD COLUMN `status_code` INT(3) AFTER `raw_post`,
ADD COLUMN `response_media_type` VARCHAR(255) AFTER `status_code`
;