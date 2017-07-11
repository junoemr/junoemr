ALTER TABLE `waitingList` ADD INDEX(`demographic_no`); 
ALTER TABLE `waitingList` ADD INDEX `demographic_on_waitlist`(`demographic_no`, `listID`, `is_history`);