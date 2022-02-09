UPDATE security SET forcePasswordReset = 0 WHERE forcePasswordReset IS NULL;
ALTER TABLE security MODIFY forcePasswordReset TINYINT(1) DEFAULT 0 NOT NULL;

