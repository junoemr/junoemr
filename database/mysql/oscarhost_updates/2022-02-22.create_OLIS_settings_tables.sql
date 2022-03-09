-- same tables as olis_init.sql
CREATE TABLE IF NOT EXISTS OLISProviderPreferences
(
    providerId VARCHAR(10),
    startTime  VARCHAR(20),
    PRIMARY KEY (providerId)
) DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS OLISSystemPreferences
(
    id             INT NOT NULL AUTO_INCREMENT,
    startTime      VARCHAR(20),
    endTime        VARCHAR(20),
    pollFrequency  INT,
    lastRun        timestamp,
    filterPatients tinyint(1),
    PRIMARY KEY (id)
) DEFAULT CHARSET=utf8;