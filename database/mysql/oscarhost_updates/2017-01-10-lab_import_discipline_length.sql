-- The lifelabs parser makes long disciplines by concatenating them.

alter table hl7TextInfo change column discipline discipline varchar(200);
