CREATE TABLE IF NOT EXISTS `roster_status` (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    roster_status VARCHAR(20) NOT NULL,
    status_description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    updated_by VARCHAR(6) NOT NULL,
    system_managed BOOLEAN NOT NULL DEFAULT FALSE,
    rostered BOOLEAN NOT NULL DEFAULT FALSE,
    is_terminated BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT unique_roster_status UNIQUE(roster_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `roster_status`(roster_status, status_description, created_at, updated_at, updated_by, system_managed, rostered, is_terminated)
VALUES
   ("TE", "Terminated", NOW(), NOW(), "999900", TRUE, FALSE, TRUE),
   ("RO", "Rostered", NOW(), NOW(), "999900", TRUE, TRUE, FALSE),
   ("NR", "Not Rostered", NOW(), NOW(), "999900", TRUE, FALSE, FALSE),
   ("FS", "Fee for Service", NOW(), NOW(), "999900", TRUE, FALSE, FALSE);

INSERT IGNORE INTO `roster_status`(roster_status, status_description, created_at, updated_at, updated_by)
SELECT DISTINCT
    roster_status,
    roster_status,
    lastUpdateDate,
    lastUpdateDate,
    lastUpdateUser
FROM demographic
WHERE LENGTH(roster_status) > 0;

INSERT IGNORE INTO `roster_status`(roster_status, status_description, created_at, updated_at, updated_by)
SELECT DISTINCT
    roster_status,
    roster_status,
    lastUpdateDate,
    lastUpdateDate,
    lastUpdateUser
FROM demographicArchive
WHERE LENGTH(roster_status) > 0;

CREATE TABLE IF NOT EXISTS `demographic_roster` (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    demographic_no INTEGER NOT NULL,
    rostered_physician VARCHAR(255) DEFAULT NULL,
    ohip_no VARCHAR(9) DEFAULT NULL,
    roster_status_id INTEGER NOT NULL,
    roster_date TIMESTAMP NULL,
    roster_termination_date TIMESTAMP NULL,
    roster_termination_reason VARCHAR(64),
    added_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT demographic_roster_demographic_no_fk FOREIGN KEY(demographic_no) REFERENCES demographic (demographic_no),
    CONSTRAINT demographic_roster_roster_status_fk FOREIGN KEY (`roster_status_id`) REFERENCES roster_status(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;