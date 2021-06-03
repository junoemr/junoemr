CREATE TABLE IF NOT EXISTS flowsheet
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    flowsheet_name  VARCHAR(255) NOT NULL,
    description     TEXT,
    system_managed  BOOLEAN NOT NULL DEFAULT false,
    enabled         BOOLEAN NOT NULL DEFAULT true,

    created_at      DATETIME NOT NULL,
    created_by      VARCHAR(6) DEFAULT NULL,
    updated_at      DATETIME NOT NULL,
    updated_by      VARCHAR(6) DEFAULT NULL,
    deleted_at      DATETIME,
    deleted_by      VARCHAR(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS flowsheet_item_group
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    flowsheet_id    INTEGER(10) NOT NULL,
    group_name      VARCHAR(255) NOT NULL,
    description     TEXT,

    created_at      DATETIME NOT NULL,
    created_by      VARCHAR(6) DEFAULT NULL,
    updated_at      DATETIME NOT NULL,
    updated_by      VARCHAR(6) DEFAULT NULL,
    deleted_at      DATETIME,
    deleted_by      VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `flowsheet_item_group_flowsheet_id_fk` FOREIGN KEY (flowsheet_id) REFERENCES flowsheet (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS flowsheet_item
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    flowsheet_id    INTEGER(10) NOT NULL,
    flowsheet_item_group_id INTEGER(10),
    item_name       VARCHAR(255) NOT NULL,
    item_type       VARCHAR(255) NOT NULL,
    item_type_code  VARCHAR(255) NOT NULL,
    value_type      VARCHAR(255) NOT NULL,
    graphable       BOOLEAN NOT NULL DEFAULT false,
    guideline       TEXT,
    description     TEXT,

    created_at      DATETIME NOT NULL,
    created_by      VARCHAR(6) DEFAULT NULL,
    updated_at      DATETIME NOT NULL,
    updated_by      VARCHAR(6) DEFAULT NULL,
    deleted_at      DATETIME,
    deleted_by      VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `flowsheet_item_flowsheet_id_fk` FOREIGN KEY (flowsheet_id) REFERENCES flowsheet (id),
    CONSTRAINT `flowsheet_item_flowsheet_item_group_id_fk` FOREIGN KEY (flowsheet_item_group_id) REFERENCES flowsheet_item_group (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS flowsheet_item_validations
(
    validations_id  INTEGER NOT NULL,
    flowsheet_id    INTEGER NOT NULL,
    PRIMARY KEY (validations_id, flowsheet_id),
    CONSTRAINT `flowsheet_item_validations_validations_id_fk` FOREIGN KEY (validations_id) REFERENCES validations (id),
    CONSTRAINT `flowsheet_item_validations_flowsheet_id_fk` FOREIGN KEY (flowsheet_id) REFERENCES flowsheet (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS flowsheet_rule
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    flowsheet_id    INTEGER(10) NOT NULL,
    rule_name       VARCHAR(255) NOT NULL,
    rule_severity   VARCHAR(255) NOT NULL,
    rule_message    TEXT,
    description     TEXT,

    created_at      DATETIME NOT NULL,
    created_by      VARCHAR(6) DEFAULT NULL,
    updated_at      DATETIME NOT NULL,
    updated_by      VARCHAR(6) DEFAULT NULL,
    deleted_at      DATETIME,
    deleted_by      VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `flowsheet_rule_flowsheet_id_fk` FOREIGN KEY (flowsheet_id) REFERENCES flowsheet (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS flowsheet_rule_condition
(
    id                  INTEGER PRIMARY KEY AUTO_INCREMENT,
    flowsheet_rule_id   INTEGER(10) NOT NULL,
    condition_name      VARCHAR(255) NOT NULL,
    condition_type      VARCHAR(255) NOT NULL,
    condition_value     VARCHAR(255),

    created_at          DATETIME NOT NULL,
    created_by          VARCHAR(6) DEFAULT NULL,
    updated_at          DATETIME NOT NULL,
    updated_by          VARCHAR(6) DEFAULT NULL,
    deleted_at          DATETIME,
    deleted_by          VARCHAR(6) DEFAULT NULL,

    CONSTRAINT `flowsheet_rule_condition_flowsheet_rule_id_fk` FOREIGN KEY (flowsheet_rule_id) REFERENCES flowsheet_rule (id)
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

CREATE TABLE IF NOT EXISTS flowsheet_drools
(
    drools_id       INTEGER NOT NULL,
    flowsheet_id    INTEGER NOT NULL,
    PRIMARY KEY (drools_id, flowsheet_id),
    CONSTRAINT `flowsheet_drools_drools_id_fk` FOREIGN KEY (drools_id) REFERENCES drools (id),
    CONSTRAINT `flowsheet_drools_flowsheet_id_fk` FOREIGN KEY (flowsheet_id) REFERENCES flowsheet (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS flowsheet_triggers_icd9
(
    icd9_id         INTEGER NOT NULL,
    flowsheet_id    INTEGER NOT NULL,
    PRIMARY KEY (icd9_id, flowsheet_id),
    CONSTRAINT `flowsheet_triggers_icd9_icd9_id_fk` FOREIGN KEY (icd9_id) REFERENCES icd9 (id),
    CONSTRAINT `flowsheet_triggers_icd9_flowsheet_id_fk` FOREIGN KEY (flowsheet_id) REFERENCES flowsheet (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;