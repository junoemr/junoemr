ALTER TABLE measurements CHANGE COLUMN dataField dataField varchar(500) not null;
ALTER TABLE measurementsDeleted CHANGE COLUMN dataField dataField varchar(500) not null;
