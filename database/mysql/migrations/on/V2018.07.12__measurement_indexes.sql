
create or replace index keyval_idx on measurementsExt (keyval);
create or replace index val_idx on measurementsExt (val(10));
create or replace index `measurement_id_keyval_idx` on measurementsExt (`measurement_id`,`keyval`);