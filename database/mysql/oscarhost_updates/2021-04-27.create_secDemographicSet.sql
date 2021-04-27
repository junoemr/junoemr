CREATE TABLE IF NOT EXISTS secDemographicSet
(
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    provider_id    VARCHAR(6)  NOT NULL,
    set_name       VARCHAR(20) NOT NULL,
    deleted_at     DATETIME   DEFAULT NULL,
    deleted_by     VARCHAR(6) DEFAULT NULL,
    INDEX set_name_idx (set_name),
    INDEX provider_id_idx (provider_id),
    CONSTRAINT `secDemographicSet_provider_id_fk` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;