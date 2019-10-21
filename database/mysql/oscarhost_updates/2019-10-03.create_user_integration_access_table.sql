create table if not exists `user_integration_access`
(
	id int(6) not null auto_increment,
	security_no int(6) not null,
	integration_remote_id varchar(40),
	access_email varchar(255),
	access_token TEXT,
	remote_user_id varchar(40),
	primary key(id),
	constraint user_access_integration_remote_id_fk
		foreign key(integration_remote_id) references integration(remote_id)
		on delete cascade
		on update no action,
	constraint user_access_security_no_fk
		foreign key(security_no) references security(security_no)
		on delete cascade
		on update no action
);
