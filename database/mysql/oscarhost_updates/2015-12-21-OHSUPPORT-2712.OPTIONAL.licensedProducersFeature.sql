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

INSERT INTO licensed_producer (producer_name, province, phone, license_type, license_date ) 
VALUES
('Mettrum Ltd'  ,'ON', '1-844-638-8786', 'Cultivation and Sale' , '2013-11-01'),
('Tweed Inc.'  ,'ON', '1-855-558-9333', 'Cultivation and Sale' , '2013-11-18'),
('Tilray'  ,'BC', '1-844-845-7291', 'Cultivation and Sale' , '2014-03-24'),
('MedReleaf Corp.' ,'ON', '1-855-473-5323', 'Cultivation and Sale' , '2014-02-14'),
('Bedrocan Canada Inc.' ,'ON', '1-855-420-7887', 'Sale Only'  , '2013-12-16'),
('MariCann Inc.' ,'ON', '1-844-627-4226', 'Cultivation and Sale' , '2014-03-27'),
('CanniMed Ltd.' ,'SK', '1-855-787-1577', 'Sale Only'  , '2013-09-19'),
('Broken Coast Cannibis Ltd.' ,'BC', '1-888-486-7579', 'Cultivation and Sale' , '2014-03-14'),
('CannTrust Inc.' ,'ON', '1-855-794-2266', 'Cultivation and Sale' , '2014-06-12'),
('Canna Farms Ltd.' ,'BC', '1-855-882-0988', 'Cultivation and Sale' , '2014-01-08'),
('Aphria'  ,'ON', '1-844-427-4742', 'Cultivation and Sale' , '2014-03-24'),
('Delta 9 Bio-Tech Inc.' ,'MB', '1-855-245-1259', 'Cultivation and Sale' , '2014-03-18'),
('Hydropothecary' ,'QC', '1-844-406-1852', 'Cultivation and Sale' , '2014-03-14'),
('OriganiGram Inc.' ,'NB', '1-855-961-9420', 'Cultivation and Sale' , '2014-03-26'),
('RedeCan Pharm' ,'ON', '1-905-892-6788', 'Cultivation and Sale' , '2014-06-25'),
('Peace Naturals Project Inc.' ,'ON', '1-888-647-3223', 'Cultivation and Sale' , '2013-10-31'),
('Whistler Medical Marijuana Corp.','BC', '1-604-962-3440', 'Cultivation and Sale' , '2014-02-26');

CREATE TABLE licensed_producer_address (
	address_id int NOT NULL AUTO_INCREMENT,
	address varchar(64) DEFAULT NULL,
	city varchar(64) DEFAULT NULL,
	postal varchar(9) DEFAULT NULL,
	province varchar(20) DEFAULT NULL,
	display_name varchar(32) DEFAULT NULL,
	PRIMARY KEY ( address_id )
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO licensed_producer_address(address, city, province, postal, display_name)
VALUES
('329 Queenston Rd.', 'Hamilton', 'ON', 'L8K 1H7', 'Hamilton'),
('137 McKeller St. N', 'Thunder Bay', 'ON', 'P7C 3Y9', 'Thunder Bay'),
('121 Dundas St. E #106', 'Belleville', 'ON', 'K8N 1L3', 'Belleville'),
('556 Bryne Dr. Unit 16', 'Barrie', 'ON', 'L4N 9P6', 'Barrie'),
('16700 Bayview Ave. Unit 208', 'Newmarket', 'ON', 'L3X 1W1', 'Newmarket');

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
