

-- Makes the SQL for EDD Report 2007 faster
create index formEdited_index ON formBCAR2007(demographic_no, formEdited);

-- Index the billinghistory table in case it gets too big
create index billing_history_billingmaster_no_index ON billing_history (billingmaster_no);
