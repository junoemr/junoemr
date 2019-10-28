create table if not exists `integration`
(
	remote_id varchar(40) not null,
	api_key varchar(255),
	site_id int null,
	integration_type varchar(20) not null,
	primary key(remote_id),
	constraint integration_site_id_fkey
		foreign key(site_id) references site(site_id)
		on delete cascade
		on update no action
);
