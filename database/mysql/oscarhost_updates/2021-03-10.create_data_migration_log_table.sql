CREATE TABLE IF NOT EXISTS log_data_migration
(
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    uuid           VARCHAR(36) NOT NULL,
    type           VARCHAR(32) NOT NULL,
    start_datetime datetime    NOT NULL,
    end_datetime   datetime,
    data           json,
    CONSTRAINT unique_uuid UNIQUE (uuid),
    INDEX idx_uuid (uuid)
);