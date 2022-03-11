ALTER TABLE fax_outbound ADD COLUMN IF NOT EXISTS status_remote VARCHAR(16) AFTER status;

-- migrate existing srfax data
UPDATE fax_outbound SET status_remote='SENT' WHERE status_remote IS NULL AND external_status='Sent';
UPDATE fax_outbound SET status_remote='ERROR' WHERE status_remote IS NULL AND external_status='Failed';