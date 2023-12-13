CREATE TABLE IF NOT EXISTS integration_push_update(
  id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  integration_type VARCHAR(64) NOT NULL,
  integration_id INT,
  security_no INT,
  update_type VARCHAR(256) NOT NULL,

  created_at datetime NOT NULL DEFAULT NOW(),
  updated_at datetime NOT NULL DEFAULT NOW(),
  deleted_at datetime,

  status VARCHAR (32) NOT NULL,
  send_count int NOT NULL DEFAULT 0,
  sent_at datetime,
  json_data text NOT NULL,

  CHECK (JSON_VALID(json_data)),
  INDEX idx_status (status));