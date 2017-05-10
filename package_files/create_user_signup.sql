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
-- Table structure for table `schema_migrations`
--

DROP TABLE IF EXISTS `schema_migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_migrations` (
  `version` varchar(255) NOT NULL,
  UNIQUE KEY `unique_schema_migrations` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_migrations`
--

LOCK TABLES `schema_migrations` WRITE;
/*!40000 ALTER TABLE `schema_migrations` DISABLE KEYS */;
INSERT INTO `schema_migrations` VALUES ('20170214222606'),('20170214225313'),('20170216011045'),('20170216192618'),('20170217002621'),('20170310184223');
/*!40000 ALTER TABLE `schema_migrations` ENABLE KEYS */;
UNLOCK TABLES;

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
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`practice_id`),
  UNIQUE KEY `practice_id` (`practice_id`),
  KEY `id_user_signup_status` (`id_user_signup_status`),
  KEY `id_user_signup_plan` (`id_user_signup_plan`),
  CONSTRAINT `user_signup_ibfk_1` FOREIGN KEY (`id_user_signup_status`) REFERENCES `user_signup_status` (`id`),
  CONSTRAINT `user_signup_ibfk_2` FOREIGN KEY (`id_user_signup_plan`) REFERENCES `user_signup_plan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_signup_plan`
--

DROP TABLE IF EXISTS `user_signup_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_signup_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_signup_plan`
--

LOCK TABLES `user_signup_plan` WRITE;
/*!40000 ALTER TABLE `user_signup_plan` DISABLE KEYS */;
INSERT INTO `user_signup_plan` VALUES (1,'basic','$199/month, up to 6 users',1),(2,'plus','$499/month, up to 15 users',1),(3,'premium','$999/month, up to 40 users',1);
/*!40000 ALTER TABLE `user_signup_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_signup_status`
--

DROP TABLE IF EXISTS `user_signup_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_signup_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_signup_status`
--

LOCK TABLES `user_signup_status` WRITE;
/*!40000 ALTER TABLE `user_signup_status` DISABLE KEYS */;
INSERT INTO `user_signup_status` VALUES (1,'new_user','New unprocessed user',1),(2,'user_db_created','Initial oscar database created',1),(3,'oscar_account_created','Initial oscar user created',1),(4,'case_management_imported','Case Management has been imported',1),(5,'oscar_instance_crawled','Oscar Instance has been crawled and cached',1),(6,'set_account_password','User \'s password has been setup',1),(7,'sent_user_email','User has been sent a welcome email with their login credentials',1),(8,'setup_done','Setup process is done',1),(9,'marked_for_deletion','Marked & ready to be deleted',1);
/*!40000 ALTER TABLE `user_signup_status` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-15 12:45:39
