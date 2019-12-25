
CREATE TABLE IF NOT EXISTS icefall_credentials(
  id INT PRIMARY KEY,
  username  VARCHAR(256),
  email     VARCHAR(256),
  password  VARCHAR(256),
  api_token VARCHAR(256)
) CHARACTER SET utf8;


CREATE TABLE IF NOT EXISTS icefall_log(
    id INT PRIMARY KEY AUTO_INCREMENT,
    status VARCHAR(64),
    message TEXT,
    form_id INT,
    is_form_instance BOOL,
    sending_provider_no VARCHAR(6),
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT `icefall_log_sending_provider_no_fk` FOREIGN KEY (sending_provider_no) REFERENCES `provider`(`provider_no`) ON DELETE SET NULL
) CHARACTER SET utf8;
