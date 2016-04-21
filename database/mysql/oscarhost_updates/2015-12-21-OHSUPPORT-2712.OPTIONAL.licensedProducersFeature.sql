-- Optional update. Only required if licensed producer dropdown is being enabled for demographic master file
-- No impact on instances not using the custom work

CREATE TABLE licensed_producer (
	producer_id int(10) NOT NULL AUTO_INCREMENT,
	producer_name varchar(32) DEFAULT NULL,
	province varchar(20) DEFAULT NULL,
	phone varchar(20) DEFAULT NULL,
	license_type varchar(64) DEFAULT NULL,
	license_date date DEFAULT NULL,
	PRIMARY KEY ( producer_id )
);

CREATE TABLE licensed_producer_address (
	address_id int NOT NULL AUTO_INCREMENT,
	address varchar(64) DEFAULT NULL,
	city varchar(64) DEFAULT NULL,
	postal varchar(9) DEFAULT NULL,
	province varchar(20) DEFAULT NULL,
	display_name varchar(32) DEFAULT NULL,
	PRIMARY KEY ( address_id )
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


CREATE TABLE demographic_licensed_producer (
	id int NOT NULL AUTO_INCREMENT,
	demographic_no int(10) UNIQUE NOT NULL,
	producer_id int(10) NOT NULL DEFAULT 0,
	address_id int(10) NOT NULL DEFAULT 0,
	PRIMARY KEY ( id ),
	FOREIGN KEY (demographic_no) REFERENCES demographic(demographic_no) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (producer_id) REFERENCES licensed_producer(producer_id) ON UPDATE CASCADE ON DELETE SET DEFAULT,
	FOREIGN KEY (address_id) REFERENCES licensed_producer_address(address_id) ON UPDATE CASCADE ON DELETE SET DEFAULT
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
