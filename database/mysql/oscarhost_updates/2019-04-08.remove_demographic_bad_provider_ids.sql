UPDATE demographic SET provider_no = NULL WHERE provider_no NOT IN (SELECT provider_no FROM provider);

ALTER TABLE demographic ADD FOREIGN KEY IF NOT EXISTS fk_provider(provider_no) REFERENCES provider(provider_no) ON DELETE SET NULL;