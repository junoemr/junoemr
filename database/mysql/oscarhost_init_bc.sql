

-- Makes the SQL for EDD Report 2007 faster
create index formEdited_index ON formBCAR2007(demographic_no, formEdited);
