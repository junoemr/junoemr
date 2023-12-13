CREATE TABLE IF NOT EXISTS log_emails(
  id bigint(20) AUTO_INCREMENT PRIMARY KEY,
  timestamp datetime NOT NULL,
  referring_provider_no varchar(6),
  loggedIn_provider_no varchar(6),
  referral_doctor_id int(10),
  demographic_no int(10),
  email_address varchar(60),
  email_success tinyint(1) NOT NULL DEFAULT 0,
  email_content text
);

ALTER TABLE consultationRequests ADD COLUMN IF NOT EXISTS notification_sent tinyint(1) NOT NULL DEFAULT 0;