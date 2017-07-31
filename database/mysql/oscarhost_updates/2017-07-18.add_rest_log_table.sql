-- creates a table for logging rest WS calls
CREATE TABLE `log_ws_rest` (
  id                BIGINT UNSIGNED AUTO_INCREMENT,
  created_at        DATETIME       NOT NULL,
  duration_ms       MEDIUMINT      NOT NULL DEFAULT 0,
  provider_no       VARCHAR(6),
  ip                VARCHAR(20),
  user_agent        text,
  url               VARCHAR(2048),
  raw_query_string  text,
  raw_post          text,
  raw_output        text,
  error_message     text,

  PRIMARY KEY (`id`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_provider_no` (`provider_no`)
);