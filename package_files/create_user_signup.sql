-- MySQL dump 10.16  Distrib 10.1.21-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: localhost
-- ------------------------------------------------------
-- Server version	10.1.21-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `user_signup`
--

DROP TABLE IF EXISTS `user_signup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_signup` (
  `practice_name` varchar(100) NOT NULL,
  `practice_id` varchar(100) NOT NULL,
  `login_name` varchar(20) NOT NULL,
  `password` varchar(200) DEFAULT NULL,
  `password_hash` varchar(200) DEFAULT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(500) NOT NULL,
  `phone_number` varchar(10) NOT NULL,
  `phone_number_ext` varchar(10) DEFAULT NULL,
  `country` varchar(100) NOT NULL DEFAULT 'Canada',
  `province` varchar(50) NOT NULL,
  `city` varchar(50) DEFAULT NULL,
  `address_1` varchar(200) DEFAULT NULL,
  `address_2` varchar(200) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT '0',
  `specialty` varchar(100) DEFAULT NULL,
  `hearaboutus` varchar(30) NOT NULL DEFAULT '0',
  `referred_by` varchar(255) NOT NULL DEFAULT '0',
  `signup_timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `signup_ip` varchar(20) NOT NULL,
  `terms_agreed` tinyint(1) NOT NULL,
  `terms_agreed_timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `chargify_subscription_id` int(11) NOT NULL,
  `id_user_signup_status` int(11) NOT NULL DEFAULT '1',
  `id_user_signup_plan` int(11) NOT NULL DEFAULT '1',
  `last_update_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_backup_size` varchar(20) NOT NULL DEFAULT '0',
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `encrypted_oscarhost_password` varchar(200) DEFAULT NULL,
  `current_server_url` text,
  `pin` text,
  PRIMARY KEY (`practice_id`),
  UNIQUE KEY `practice_id` (`practice_id`),
  KEY `id_user_signup_status` (`id_user_signup_status`),
  KEY `id_user_signup_plan` (`id_user_signup_plan`),
  CONSTRAINT `user_signup_ibfk_1` FOREIGN KEY (`id_user_signup_status`) REFERENCES `user_signup_status` (`id`),
  CONSTRAINT `user_signup_ibfk_2` FOREIGN KEY (`id_user_signup_plan`) REFERENCES `user_signup_plan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_signup`
--

LOCK TABLES `user_signup` WRITE;
/*!40000 ALTER TABLE `user_signup` DISABLE KEYS */;
INSERT INTO `user_signup` VALUES ('Oscar Test Practice','oscar_test','oscar_test','SeemaGanesh7887','-123-35-512331108-126-50-606010948-4520-26980-119-724','Test','User','stephen.mcgrath@oscarhost.ca','1234567890','0','Canada','BC','Victoria','1-415 Dunedin St','','0','Family Medicine','0','0','2012-11-26 07:17:17','96.49.165.233',1,'2012-11-26 07:17:17',0,8,1,'2012-11-26 07:19:09','0',0,0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `user_signup` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-02 14:22:13
