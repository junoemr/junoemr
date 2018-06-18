CREATE TABLE medisproutappointment (
	`ID` int(12) NOT NULL AUTO_INCREMENT, 
	`appointment_no` int(12) NOT NULL,
	`providerUrl` varchar(1024) DEFAULT NULL,
	`attendeesUrl` varchar(1024) DEFAULT NULL,
	`code` int(12) DEFAULT NULL,
	`dowloadeddocs` tinyint(1) DEFAULT 0,
	PRIMARY KEY (`ID`)
);
