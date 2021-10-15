
CREATE TABLE IF NOT EXISTS care_tracker
(
    id                      INTEGER PRIMARY KEY AUTO_INCREMENT,
    parent_care_tracker_id  INTEGER(10) DEFAULT NULL,
    owner_provider_id       VARCHAR(6) DEFAULT NULL,
    owner_demographic_id    INTEGER(10) DEFAULT NULL,
    system_managed          BOOLEAN NOT NULL DEFAULT false,
    enabled                 BOOLEAN NOT NULL DEFAULT true,
    care_tracker_name       VARCHAR(255) NOT NULL,
    description             TEXT,

    created_at              DATETIME NOT NULL,
    created_by              VARCHAR(6) DEFAULT NULL,
    updated_at              DATETIME NOT NULL,
    updated_by              VARCHAR(6) DEFAULT NULL,
    deleted_at              DATETIME,
    deleted_by              VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `care_tracker_care_tracker_parent_id_fk` FOREIGN KEY (parent_care_tracker_id) REFERENCES care_tracker (id),
    CONSTRAINT `care_tracker_owner_provider_id_fk` FOREIGN KEY (owner_provider_id) REFERENCES provider (provider_no),
    CONSTRAINT `care_tracker_owner_demographic_id_fk` FOREIGN KEY (owner_demographic_id) REFERENCES demographic (demographic_no)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS care_tracker_item_group
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    care_tracker_id INTEGER(10) NOT NULL,
    group_name      VARCHAR(255),
    description     TEXT,

    created_at      DATETIME NOT NULL,
    created_by      VARCHAR(6) DEFAULT NULL,
    updated_at      DATETIME NOT NULL,
    updated_by      VARCHAR(6) DEFAULT NULL,
    deleted_at      DATETIME,
    deleted_by      VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `care_tracker_item_group_care_tracker_id_fk` FOREIGN KEY (care_tracker_id) REFERENCES care_tracker (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS care_tracker_item
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    care_tracker_id INTEGER(10) NOT NULL,
    care_tracker_item_group_id INTEGER(10),
    item_name       VARCHAR(255) NOT NULL,
    item_type       VARCHAR(255) NOT NULL,
    item_type_code  VARCHAR(255) NOT NULL,
    value_type      VARCHAR(255) NOT NULL,
    value_label     VARCHAR(255),
    guideline       TEXT,
    description     TEXT,

    created_at      DATETIME NOT NULL,
    created_by      VARCHAR(6) DEFAULT NULL,
    updated_at      DATETIME NOT NULL,
    updated_by      VARCHAR(6) DEFAULT NULL,
    deleted_at      DATETIME,
    deleted_by      VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `care_tracker_item_care_tracker_id_fk` FOREIGN KEY (care_tracker_id) REFERENCES care_tracker (id),
    CONSTRAINT `care_tracker_item_care_tracker_item_group_id_fk` FOREIGN KEY (care_tracker_item_group_id) REFERENCES care_tracker_item_group (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS ds_rule
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    rule_name       VARCHAR(255) NOT NULL,
    description     TEXT,
    system_managed  BOOLEAN NOT NULL DEFAULT false,

    created_at      DATETIME NOT NULL,
    created_by      VARCHAR(6) DEFAULT NULL,
    updated_at      DATETIME NOT NULL,
    updated_by      VARCHAR(6) DEFAULT NULL,
    deleted_at      DATETIME,
    deleted_by      VARCHAR(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS ds_rule_condition
(
    id                  INTEGER PRIMARY KEY AUTO_INCREMENT,
    ds_rule_id          INTEGER(10) NOT NULL,
    condition_type      VARCHAR(255) NOT NULL,
    condition_value     VARCHAR(255),

    created_at          DATETIME NOT NULL,
    created_by          VARCHAR(6) DEFAULT NULL,
    updated_at          DATETIME NOT NULL,
    updated_by          VARCHAR(6) DEFAULT NULL,
    deleted_at          DATETIME,
    deleted_by          VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `ds_rule_condition_ds_rule_id_fk` FOREIGN KEY (ds_rule_id) REFERENCES ds_rule (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS ds_rule_consequence
(
    id                      INTEGER PRIMARY KEY AUTO_INCREMENT,
    ds_rule_id              INTEGER(10) NOT NULL,
    consequence_type        VARCHAR(255) NOT NULL,
    consequence_severity    VARCHAR(255) NOT NULL,
    consequence_message     TEXT,

    created_at          DATETIME NOT NULL,
    created_by          VARCHAR(6) DEFAULT NULL,
    updated_at          DATETIME NOT NULL,
    updated_by          VARCHAR(6) DEFAULT NULL,
    deleted_at          DATETIME,
    deleted_by          VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `ds_rule_consequence_ds_rule_id_fk` FOREIGN KEY (ds_rule_id) REFERENCES ds_rule (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS care_tracker_item_ds_rule
(
    care_tracker_item_id     INTEGER NOT NULL,
    ds_rule_id               INTEGER NOT NULL,
    PRIMARY KEY (care_tracker_item_id, ds_rule_id),
    CONSTRAINT `care_tracker_item_ds_rule_care_tracker_item_id_fk` FOREIGN KEY (care_tracker_item_id) REFERENCES care_tracker_item (id),
    CONSTRAINT `care_tracker_item_ds_rule_ds_rule_id_fk` FOREIGN KEY (ds_rule_id) REFERENCES ds_rule (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS drools
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    drl_file        VARCHAR(255) NOT NULL,
    description     TEXT,

    created_at      DATETIME NOT NULL,
    updated_at      DATETIME NOT NULL,
    deleted_at      DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS care_tracker_drools
(
    drools_id           INTEGER NOT NULL,
    care_tracker_id     INTEGER NOT NULL,
    PRIMARY KEY (drools_id, care_tracker_id),
    CONSTRAINT `care_tracker_drools_drools_id_fk` FOREIGN KEY (drools_id) REFERENCES drools (id),
    CONSTRAINT `care_tracker_drools_care_tracker_id_fk` FOREIGN KEY (care_tracker_id) REFERENCES care_tracker (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS care_tracker_triggers_icd9
(
    icd9_id             INTEGER NOT NULL,
    care_tracker_id     INTEGER NOT NULL,
    PRIMARY KEY (icd9_id, care_tracker_id),
    CONSTRAINT `care_tracker_triggers_icd9_icd9_id_fk` FOREIGN KEY (icd9_id) REFERENCES icd9 (id),
    CONSTRAINT `care_tracker_triggers_icd9_care_tracker_id_fk` FOREIGN KEY (care_tracker_id) REFERENCES care_tracker (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;