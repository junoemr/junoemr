
-- remove invalid data
DELETE FROM demographicSets WHERE demographic_no IS NULL OR set_name IS NULL;

-- delete duplicates within the same set. keep lowest id
DELETE s1 FROM demographicSets s1
  INNER JOIN demographicSets s2 ON (s1.demographic_no = s2.demographic_no AND s1.set_name = s2.set_name)
  WHERE s1.id > s2.id;

-- add column constraints
ALTER TABLE demographicSets MODIFY COLUMN demographic_no INTEGER(10) NOT NULL;
ALTER TABLE demographicSets MODIFY COLUMN set_name VARCHAR(32) NOT NULL;

-- table must use same charset as fk linked table. need to drop fk first to change charset
ALTER TABLE demographicSets DROP FOREIGN KEY IF EXISTS `demographicSets_demographic_no_fk`;
ALTER TABLE demographicSets DROP INDEX IF EXISTS `demographicSets_demographic_no_set_name_unique`;

-- add foreign key constraint,
ALTER TABLE demographicSets CONVERT TO CHARACTER SET utf8;
ALTER TABLE demographicSets ADD CONSTRAINT `demographicSets_demographic_no_fk` FOREIGN KEY IF NOT EXISTS(`demographic_no`) REFERENCES `demographic` (`demographic_no`) ON DELETE CASCADE;

-- add other constraints and indexes
ALTER TABLE demographicSets ADD CONSTRAINT `demographicSets_demographic_no_set_name_unique` UNIQUE (demographic_no, set_name);
CREATE OR REPLACE INDEX demographicSets_set_name_idx ON demographicSets (set_name);