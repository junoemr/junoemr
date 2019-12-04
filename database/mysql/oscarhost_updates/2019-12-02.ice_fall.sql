
CREATE TABLE IF NOT EXISTS icefall_credentials(
  id INT PRIMARY KEY,
  username  VARCHAR(256),
  email     VARCHAR(256),
  password  VARCHAR(256),
  api_token VARCHAR(256)
);