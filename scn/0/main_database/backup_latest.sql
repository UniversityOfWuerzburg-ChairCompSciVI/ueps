-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: dev
-- ------------------------------------------------------
-- Server version	5.5.31-0+wheezy1

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
-- Table structure for table `exercise`
--

DROP TABLE IF EXISTS `exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercise` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `copied_from_id` int(10) unsigned DEFAULT NULL,
  `exercise_group_id` int(10) unsigned DEFAULT NULL,
  `question` text COLLATE utf8_unicode_ci NOT NULL,
  `name` text COLLATE utf8_unicode_ci,
  `credits` tinyint(4) DEFAULT '0',
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `exercise_group_id` (`exercise_group_id`),
  KEY `exercise_ibfk_2` (`copied_from_id`),
  CONSTRAINT `exercise_ibfk_1` FOREIGN KEY (`exercise_group_id`) REFERENCES `exercise_group` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `exercise_ibfk_2` FOREIGN KEY (`copied_from_id`) REFERENCES `exercise` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exercise_group`
--

DROP TABLE IF EXISTS `exercise_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercise_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `copied_from_id` int(10) unsigned DEFAULT NULL,
  `scenario_id` int(10) unsigned DEFAULT NULL,
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `is_rated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `scenario_id` (`scenario_id`),
  KEY `exercise_group_ibfk_2` (`copied_from_id`),
  CONSTRAINT `exercise_group_ibfk_1` FOREIGN KEY (`scenario_id`) REFERENCES `scenario` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `exercise_group_ibfk_2` FOREIGN KEY (`copied_from_id`) REFERENCES `exercise_group` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `generated_tag`
--

DROP TABLE IF EXISTS `generated_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `generated_tag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `exercise_id` int(10) unsigned NOT NULL,
  `tag` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `gen_tag` (`exercise_id`,`tag`),
  KEY `exercise_id` (`exercise_id`),
  CONSTRAINT `generated_tag_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scenario`
--

DROP TABLE IF EXISTS `scenario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scenario` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `copied_from_id` int(10) unsigned DEFAULT NULL,
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  `create_script_path` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `image_path` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `db_host` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `db_user` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `db_pass` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `db_port` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `db_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `scenario_ibfk_1` (`copied_from_id`),
  CONSTRAINT `scenario_ibfk_1` FOREIGN KEY (`copied_from_id`) REFERENCES `scenario` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `solution_query`
--

DROP TABLE IF EXISTS `solution_query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `solution_query` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `exercise_id` int(10) unsigned NOT NULL,
  `query` text COLLATE utf8_unicode_ci NOT NULL,
  `explanation` text COLLATE utf8_unicode_ci,
  `user_entry_id` int(10) unsigned DEFAULT NULL,
  `status` tinyint(3) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `exercise_id` (`exercise_id`),
  KEY `user_entry_id` (`user_entry_id`),
  CONSTRAINT `solution_queries_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `solution_queries_ibfk_2` FOREIGN KEY (`user_entry_id`) REFERENCES `user_entry` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_admin` tinyint(1) unsigned DEFAULT '0',
  `description` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_entry`
--

DROP TABLE IF EXISTS `user_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_entry` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `exercise_id` int(10) unsigned NOT NULL,
  `user_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `user_query` text COLLATE utf8_unicode_ci NOT NULL,
  `entry_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `result_message` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `exercise_id` (`exercise_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_entry_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_entry_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_ex_tag`
--

DROP TABLE IF EXISTS `user_ex_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_ex_tag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `exercise_id` int(10) unsigned NOT NULL,
  `tag` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_tag` (`user_id`,`exercise_id`,`tag`),
  KEY `exercise_id` (`exercise_id`),
  CONSTRAINT `user_ex_tag_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_ex_tag_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_result`
--

DROP TABLE IF EXISTS `user_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_result` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_entry_id` int(10) unsigned NOT NULL,
  `corrected_by_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `solution_id` int(10) unsigned DEFAULT NULL,
  `credits` tinyint(4) NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `comment` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `corrected_by_id` (`corrected_by_id`),
  KEY `user_result_ibfk_1` (`user_entry_id`),
  KEY `user_result_ibfk_3` (`solution_id`),
  CONSTRAINT `user_result_ibfk_1` FOREIGN KEY (`user_entry_id`) REFERENCES `user_entry` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_result_ibfk_2` FOREIGN KEY (`corrected_by_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_result_ibfk_3` FOREIGN KEY (`solution_id`) REFERENCES `solution_query` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_right`
--

DROP TABLE IF EXISTS `user_right`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_right` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `scenario_id` int(10) unsigned DEFAULT NULL,
  `rating_rights` tinyint(1) unsigned DEFAULT '0',
  `group_editing_rights` tinyint(1) unsigned DEFAULT '0',
  --  new
  --  `scenario_add_delete_rights` tinyint(1) unsigned DEFAULT '0',
  --  `scenario_editing_rights` tinyint(1) unsigned DEFAULT '0',
  --  `user_management_rights` tinyint(1) unsigned DEFAULT '0',
  -- new
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `scenario_id` (`scenario_id`),
  CONSTRAINT `tutor_to_scenario_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tutor_to_scenario_ibfk_2` FOREIGN KEY (`scenario_id`) REFERENCES `scenario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

-- Dump completed on 2013-11-19 20:00:19
