ALTER TABLE `favorites` MODIFY `freqcode` VARCHAR(8);
ALTER TABLE `drugs` MODIFY `freqcode` VARCHAR(8);
UPDATE `drugs` set freqcode='Q3Month' where freqcode='Q3Mont';
UPDATE `drugs` set freqcode='Q1Month' where freqcode='Q1Mont';
UPDATE `favorites` set freqcode='Q3Month' where freqcode='Q3Mont';
UPDATE `favorites` set freqcode='Q1Month' where freqcode='Q1Mont';