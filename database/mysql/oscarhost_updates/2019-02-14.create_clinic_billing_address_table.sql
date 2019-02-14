CREATE TABLE IF NOT EXISTS `clinic_billing_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `address` varchar(60) DEFAULT NULL,
  `city` varchar(40) DEFAULT NULL,
  `province` varchar(40) DEFAULT NULL,
  `postal` varchar(15) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `fax` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

ALTER TABLE clinic
  ADD COLUMN IF NOT EXISTS clinic_billing_address_id INT DEFAULT NULL,
  ADD FOREIGN KEY IF NOT EXISTS billing_address_fk(clinic_billing_address_id) REFERENCES clinic_billing_address(id);
