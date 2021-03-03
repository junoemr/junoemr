ALTER TABLE ctl_billingservice_age_rules
ADD CONSTRAINT unique_ctl_billingservice_age_rules_service_code UNIQUE (service_code);

INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13237', 0, 1) ON DUPLICATE KEY UPDATE minAge = 0, maxAge = 1;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13437', 2, 49) ON DUPLICATE KEY UPDATE minAge = 2, maxAge = 49;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13537', 50, 59) ON DUPLICATE KEY UPDATE minAge = 50, maxAge = 59;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13637', 60, 69) ON DUPLICATE KEY UPDATE minAge = 60, maxAge = 69;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13737', 70, 79) ON DUPLICATE KEY UPDATE minAge = 70, maxAge = 79;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13837', 80, 999) ON DUPLICATE KEY UPDATE minAge = 80, maxAge = 999;


INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13238', 0, 1) ON DUPLICATE KEY UPDATE minAge = 0, maxAge = 1;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13438', 2, 49) ON DUPLICATE KEY UPDATE minAge = 2, maxAge = 49;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13538', 50, 59) ON DUPLICATE KEY UPDATE minAge = 50, maxAge = 59;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13638', 60, 69) ON DUPLICATE KEY UPDATE minAge = 60, maxAge = 69;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13738', 70, 79) ON DUPLICATE KEY UPDATE minAge = 70, maxAge = 79;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13838', 80, 999) ON DUPLICATE KEY UPDATE minAge = 80, maxAge = 999;

INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13236', 0, 1) ON DUPLICATE KEY UPDATE minAge = 0, maxAge = 1;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13436', 2, 49) ON DUPLICATE KEY UPDATE minAge = 2, maxAge = 49;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13536', 50, 59) ON DUPLICATE KEY UPDATE minAge = 50, maxAge = 59;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13636', 60, 69) ON DUPLICATE KEY UPDATE minAge = 60, maxAge = 69;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13736', 70, 79) ON DUPLICATE KEY UPDATE minAge = 70, maxAge = 79;
INSERT INTO ctl_billingservice_age_rules (service_code, minAge, maxAge) VALUES ('13836', 80, 999) ON DUPLICATE KEY UPDATE minAge = 80, maxAge = 999;
