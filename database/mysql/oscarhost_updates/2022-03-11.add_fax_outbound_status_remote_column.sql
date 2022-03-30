ALTER TABLE fax_outbound ADD COLUMN IF NOT EXISTS status_remote VARCHAR(16) AFTER status;

-- migrate existing srfax data
UPDATE fax_outbound SET status_remote='SENT' WHERE status_remote IS NULL AND external_status='Sent' AND external_account_type='SRFAX';
UPDATE fax_outbound SET status_remote='ERROR' WHERE status_remote IS NULL AND external_status='Failed' AND external_account_type='SRFAX';

UPDATE fax_outbound SET status_remote='PENDING'
  WHERE status_remote IS NULL
  AND status='SENT'
  AND external_status NOT IN ('Sent', 'Failed')
  AND external_account_type='SRFAX';