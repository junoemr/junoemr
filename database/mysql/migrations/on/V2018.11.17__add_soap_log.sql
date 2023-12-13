CREATE TABLE IF NOT EXISTS log_ws_soap (
	id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	created_at DATETIME,
	duration_ms MEDIUMINT(9),
	ip VARCHAR(20),
	url VARCHAR(2048),
	http_method VARCHAR(6),
	soap_method VARCHAR(255),
	provider_no VARCHAR(6),
	soap_input TEXT,
	soap_output TEXT,
	error_message TEXT
);