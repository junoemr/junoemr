
CREATE INDEX idx_billingmaster_demographic_no ON billingmaster (demographic_no);

CREATE INDEX idx_billing_history_billingmaster_no ON billing_history (billingmaster_no);

CREATE INDEX idx_formBCAR2007_demographic_no ON formBCAR2007 (demographic_no);
