CREATE TABLE OLISResultNomenclature
(
    id          INT NOT NULL AUTO_INCREMENT,
    nameId      VARCHAR(10),
    name        TEXT,
    sortKey     VARCHAR(14),
    PRIMARY KEY (id)
);
LOAD DATA LOCAL INFILE 'OLISTestResultNomenclature.csv'
INTO TABLE OLISResultNomenclature
FIELDS TERMINATED BY ','
       OPTIONALLY ENCLOSED BY '\"' 
LINES TERMINATED BY '\n'
(nameId, name, sortKey);

CREATE TABLE OLISRequestNomenclature
(
    id       INT NOT NULL AUTO_INCREMENT,
    nameId   VARCHAR(10),
    name     TEXT,
    sortKey  VARCHAR(14),
    category VARCHAR(20),
    PRIMARY KEY (id)
);

LOAD DATA LOCAL INFILE 'OLISTestRequestNomenclature.csv'
INTO TABLE OLISRequestNomenclature
FIELDS TERMINATED BY ','
       OPTIONALLY ENCLOSED BY '\"' 
LINES TERMINATED BY '\n'
(nameId, name, sortKey, category);

UPDATE OLISResultNomenclature SET sortKey=NULL WHERE sortKey = '';
UPDATE OLISRequestNomenclature SET sortKey=NULL WHERE sortKey = '';

CREATE TABLE OLISProviderPreferences
(
    providerId VARCHAR(10),
    startTime  VARCHAR(20),
    PRIMARY KEY (providerId)
);
CREATE TABLE OLISSystemPreferences
(
    id             INT NOT NULL AUTO_INCREMENT,
    startTime      VARCHAR(20),
    endTime        VARCHAR(20),
    pollFrequency  INT,
    lastRun        timestamp,
    filterPatients tinyint(1),
    PRIMARY KEY (id)
);

update OLISSystemPreferences set filterPatients=0;
