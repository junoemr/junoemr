CREATE TABLE IF NOT EXISTS casemgmt_note_residual_info
(
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    note_id        INTEGER(10) NOT NULL,
    content_key    VARCHAR(64) NOT NULL,
    content_type   VARCHAR(64) NOT NULL,
    content_value  TEXT,
    INDEX idx_note_id (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;