-- MySQL dump 10.13  Distrib 5.5.30, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: dev_ex 
-- ------------------------------------------------------
-- Server version	5.5.30-1.1

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


DROP TABLE IF EXISTS `publishers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `publishers` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `contact_person` varchar(100) NOT NULL,
  `address` varchar(100) NOT NULL,
  `phone` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `publishers` WRITE;
INSERT INTO `publishers` VALUES (1,'Wolters Kluwer Deutschland','Hadwig Olschewski','41747 Viersen - Nelkenstraße 98','+49 798 / 515024'),(2,'Haufe-Gruppe','Folker Schlüter','67592 Flörsheim-Dalsheim - Waisenstieg 6','+49 8592 / 724244'),(3,'Mair-Dumont','Wilhardt Linden','79599 Wittlingen - Trobischstraße 23c','+49 10370 / 898113'),(4,'Random House','Germo Priebe','67310 Hettenleidelheim - Grüner Markt 55','+49 8032 / 532845'),(5,'Westermann Verlagsgruppe','Leyla Wilk','54346 Mehring - Lina-Meyer-Straße 61','+49 1619 / 309661'),(6,'Franz Cornelsen Bildungsgruppe','Winfriede Goldmann','24582 Bordesholm - Moorende 24','+49 222 / 555735'),(7,'Carlsen','Walter Andres','53842 Troisdorf - Tacitusstraße 7c','+49 4604 / 60062'),(8,'Goldmann','Liesbeth Stingl','74259 Widdern - Seelenberger Straße 6','+49 5625 / 702537'),(9,'Penguin Books','Danuta Rosemann','25576 Brokdorf - Konrad-Duden-Weg 28','+49 3316 / 11384'),(10,'Klett-Cotta','Caterina Göhring','07751 Sulza - Polizeimeister-Kaspar-Straße 86','+49 2402 / 806341'),(11,'Rauch','Natalija Buschmann','18334 Eixen - Gustav-Voigt-Straße 34a','+49 1901 / 656275'),(12,'Springer Science','Gerthold Morgenroth','17348 Mildenitz - Hertogestraße 67a','+49 8281 / 682569');
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ratings` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `book_id` int(10) unsigned NOT NULL,
  `customer_id` int(10) unsigned NOT NULL,
  `rating` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
INSERT INTO `ratings` VALUES (1,6,1,2),(2,12,1,1),(3,18,1,2),(4,6,2,2),(5,18,2,1),(6,6,3,4),(7,12,3,5),(8,18,3,4),(9,12,5,4),(10,6,6,4),(11,18,6,1),(12,12,7,5),(13,18,7,3),(14,6,8,4),(15,12,8,3),(16,18,8,4),(17,18,9,2),(18,12,10,1),(19,18,10,4),(20,18,11,5),(21,6,12,5),(22,18,12,5),(23,6,13,3),(24,12,13,4),(25,6,14,1),(26,18,15,2),(27,12,16,3),(28,6,17,3),(29,12,17,3),(30,18,17,3),(31,6,18,3),(32,18,18,5),(33,18,20,2);
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlists`
--

DROP TABLE IF EXISTS `wishlists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wishlists` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` int(10) unsigned NOT NULL,
  `book_id` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlists`
--

LOCK TABLES `wishlists` WRITE;
/*!40000 ALTER TABLE `wishlists` DISABLE KEYS */;
INSERT INTO `wishlists` VALUES (1,5,2,'2012-09-06'),(2,4,14,'2012-09-01'),(3,6,17,'2012-09-10'),(4,12,12,'2013-04-11'),(5,6,15,'2012-11-14'),(6,10,15,'2012-10-24'),(7,12,15,'2013-01-23'),(8,13,14,'2012-10-04'),(9,6,8,'2012-12-16'),(10,5,5,'2012-08-15'),(11,7,15,'2012-09-16'),(12,7,2,'2013-04-03'),(13,11,19,'2012-07-22'),(14,3,10,'2013-04-01'),(15,1,18,'2013-01-19'),(16,11,4,'2013-02-05'),(17,5,18,'2013-01-02'),(18,7,8,'2012-07-21'),(19,12,14,'2012-05-17'),(20,13,19,'2012-08-11'),(21,15,3,'2012-08-31'),(22,6,16,'2012-12-10'),(23,13,4,'2012-05-21');
/*!40000 ALTER TABLE `wishlists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `books` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `author` varchar(100) NOT NULL,
  `year` int(4) DEFAULT NULL,
  `publisher_id` int(10) unsigned NOT NULL,
  `signature` varchar(100) NOT NULL,
  `stock` int(11) NOT NULL,
  `price` float(5,2) DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'Bis(s) zum Ende der Nacht','Stephenie Meyer',2008,7,'PF/112-X/21',75,12.90),(2,'Harry Potter und der Stein der Weisen','J. K. Rowling',1998,7,'PF/320-Y/1',75848,7.90),(3,'Harry Potter und die Kammer des Schreckens','J. K. Rowling',1998,7,'PF/630-N/2',71458,8.50),(4,'Harry Potter und der Gefangene von Askaban','J. K. Rowling',1999,7,'PF/286-A/3',63419,6.99),(5,'Harry Potter und der Feuerkelch','J. K. Rowling',2000,7,'PF/674-P/4',32888,22.90),(6,'Harry Potter und der Orden des Phönix','J. K. Rowling',2003,7,'PF/675-P/5',95686,15.60),(7,'Harry Potter und der Halbblutprinz','J. K. Rowling',2005,7,'PF/272-A/6',87923,19.99),(8,'Harry Potter und die Heiligtümer des Todes','J. K. Rowling',2007,7,'PF/588-L/7',93632,19.99),(9,'Shades of Grey - Geheimes Verlangen','E. L. James',2011,8,'PF/003-R/8',42,9.99),(10,'Shades of Grey - Gefährliche Liebe','E. L. James',2011,8,'PF/102-H/9',70629,12.99),(11,'Shades of Grey - Befreite Lust','E. L. James',2012,8,'PF/725-D/10',15282,12.99),(12,'1984','George Orwell',1949,9,'PF/777-M/11',33623,9.95),(13,'Animal Farm','George Orwell',1945,9,'PF/035-H/12',60893,7.70),(14,'Herr der Ringe: Die Gefährten','J. R. R. Tolkien',2001,10,'PF/499-E/13',30542,8.99),(15,'Herr der Ringe: Die zwei Türme','J. R. R. Tolkien',2001,10,'PF/987-W/14',6728,14.99),(16,'Herr der Ringe: Die Rückkehr des Königs','J. R. R. Tolkien',2001,10,'PF/374-V/15',56632,14.99),(17,'Der Hobbit','J. R. R. Tolkien',1997,10,'PF/666-S/16',89143,9.99),(18,'Der kleine Prinz','Antoine de Saint-Exupéry',1943,11,'PF/407-T/17',79562,4.99),(19,'Bis(s) zum Morgengrauen','Stephenie Meyer',2005,7,'PF/429-K/18',21380,22.50),(20,'Bis(s) zur Mittagsstunde','Stephenie Meyer',2006,7,'PF/510-S/19',96575,19.95),(21,'Bis(s) zum Abendrot','Stephenie Meyer',2007,7,'PF/347-Z/20',96793,19.95);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customers` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) NOT NULL,
  `family_name` varchar(100) NOT NULL,
  `birthday` date NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Ilrich','Horn','1982-11-04','ilrich_1570@aol.com','f50d5fb612cb24441aa8f6edf02ba483','06779 Marke/Rudolfstraße 37c'),(2,'Hilma','Woll','1991-03-10','hilma_1411@web.de','778b1afceb88d9960bad369e55c28442','01683 Nossen/Amselsteg 40'),(3,'Hilda','Endres','1999-07-08','hilda_1181@aol.com','6f6d73c247980acb23bf668249247279','27412 Tarmstedt/Johnsbacher Weg 17'),(4,'Sieghard','Jung','1990-04-02','sieghard_1474@yahoo.com','a7ebd00b89d15d0dd01e819e98068f24','07570 Wünschendorf/Eichelseeweg 95b'),(5,'Mechthilde','Donath','1978-11-30','mechthilde_1442@uni.de','a52612d8df6aa92874c257b1de408952','84032 Altdorf/Arnsberger Straße 39'),(6,'Thomas','Schober','1991-03-02','thomas_849@aol.com','c110f9a6c8682d61fc1b68dcf992371b','25551 Silzen/Dopplerstraße 15'),(7,'Paulina','Rohr','1974-09-24','xiong@yahoo.com','02fca1052c878ae92ca83d425bb3a8b3','15838 Kummersdorf-Gut/Mannheimer Straße 86'),(8,'Amalie','Krah','2000-08-19','amalie_1011@yahoo.com','a74b7ab833f25efcd07fae00a2805908','17168 Jördenstorf/Rubinsteinstraße 65b'),(9,'Janet','Senger','1994-01-21','janet_1157@uni.de','fcd5bc7a142980163bc44ddbdbfc9f0d','91341 Röttenbach/Heckenweg 63'),(10,'Jost','Reiter','1999-05-21','jost_645@gmx.de','234ec6baa6a8fb37105775a2cc501d49','99869 Bufleben/Gilbrechtstraße 86'),(11,'Welf','Günter','1986-07-22','xiong@yahoo.com','69e63af6270379a0fa841dbb45f945f8','49086 Osnabrück/Lochnerstraße 51'),(12,'Wilhard','Herold','1985-01-07','wilhard_1041@web.de','07be9f0e365320bb64f711f0c021a204','86859 Igling/Wilthener Straße 67'),(13,'Hansl','Appel','1993-02-26','hansl_1408@uni.de','11b317ebf0ac60767af1e878bd054b5d','73460 Hüttlingen/Ackerweg 52c'),(14,'Sylvelin','Hackbarth','1981-02-13','sylvelin_28@aol.com','8d5058e254dfaffb21de732fcc76854e','06449 Wilsleben/Postgang 53'),(15,'Pius','Nitsche','1994-07-04','pius_609@uni.de','11676e2af44c99393a74d289253aba2e','24634 Padenstedt/Gillestraße 43'),(16,'Irlanda','Giesen','1985-09-30','irlanda_1047@yahoo.com','974cdaa3a4ebcb56506b06c01ecb1d23','39319 Nielebock/Hegelstraße 92'),(17,'Liborius','Steffens','1995-05-17','liborius_1545@gmail.com','a870d738ba3a144d6ce98096f1ad823a','06295 Schmalzerode/Helma-Steinbach-Weg 62'),(18,'Christl','Seeliger','1974-03-25','christl_478@uni.de','53c7c3a279d7f169cf5f000a24840c15','57583 Mörlen/Heideweg 29a'),(19,'Dorchen','Rombach','2001-01-04','dorchen_1682@uni.de','c4584090e31b5cf80a754c010956f184','97791 Obersinn/Sechslingspforte 65'),(20,'Arne','Dyck','1976-02-10','arne_670@uni.de','fe68d57c6679634170c047f57d83e4a0','54668 Prümzurlay/Dunantring 30'),(21,'Roman','Henrich','1981-06-10','roman_757@aol.com','709c9b8a725206dda51c89ac2809ea53','23936 Roggenstorf/Hinter dem Zwinger 53a'),(22,'Antonietta','Stenger','1981-09-12','xiong@yahoo.com','3e45af4ca27ea2b03fc6183af40ea112','21514 Langenlehsten/Wallauer Straße 71'),(23,'Zacharias','Thaler','1971-10-26','zacharias_1273@gmail.com','3e45af4ca27ea2b03fc6183af40ea112','03226 Koßwig/Norderhof 56'),(24,'Jutta','Fuß','1974-07-20','jutta_1892@aol.com','beae72071a5cec21564e7a6f960464dd','24220 Techelsdorf/Josef-Eicher-Straße 93c'),(25,'Eveline','Schurr','1979-07-11','eveline_1639@gmx.de','5317d89be9d1ca2b3ffb37ee2fd6ed03','91587 Adelshofen/Siget 48b'),(26,'Gusti','Orlowski','1994-02-04','gusti_1816@aol.com','c1f5a9e158eae934b007193ca3d5aaac','04736 Waldheim/Herkulesstraße 42'),(27,'Burghild','Flaig','1973-09-17','burghild_1528@gmx.de','90a01c9f5ede5355dc48c80ee0a65db3','45699 Herten/Ostritzer Straße 9'),(28,'Xaver','Kretschmann','1994-12-05','xaver_1562@aol.com','c25bd66bde33cf5c28c2ed268be8f288','55444 Eckenroth/Henry-Budge-Straße 20'),(29,'Janin','Smith','1972-07-06','xaver_1562@aol.com','1cccca9a471b08bf5f52bf04edb334dd','70794 Filderstadt/Dubliner Straße 91'),(30,'Holk','Hack','1972-11-24','holk_901@uni.de','f341201275a479515850d78a761ba983','95448 Bayreuth/Oberer Kreutberg 38');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_positions`
--

DROP TABLE IF EXISTS `order_positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_positions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(10) unsigned NOT NULL,
  `book_id` int(10) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL,
  `price` float(5,2) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_positions`
--

LOCK TABLES `order_positions` WRITE;
/*!40000 ALTER TABLE `order_positions` DISABLE KEYS */;
INSERT INTO `order_positions` VALUES (1,1,8,4,8.50),(2,1,19,1,22.99),(3,1,19,2,8.50),(4,1,21,3,12.10),(5,1,17,3,19.99),(6,2,13,4,10.99),(7,2,20,4,14.60),(8,2,11,3,12.10),(9,2,2,4,14.60),(10,2,19,4,10.99),(11,3,9,2,19.99),(12,3,12,3,8.50),(13,4,3,3,9.99),(14,4,6,2,7.50),(15,4,21,2,9.50),(16,4,9,1,12.10),(17,4,21,1,22.99),(18,6,19,1,12.50),(19,6,8,4,22.99),(20,6,2,3,14.60),(21,7,10,2,10.99),(22,7,3,4,4.99),(23,7,12,4,19.99),(24,8,7,3,6.50),(25,9,9,2,1.99),(26,9,16,2,9.50),(27,9,5,3,34.50),(28,9,12,2,7.50),(29,9,10,2,14.60),(30,11,2,4,19.99),(31,11,3,2,9.50),(32,12,19,2,9.99),(33,12,19,3,19.99),(34,13,13,4,10.99),(35,13,9,3,8.50),(36,14,11,2,10.99),(37,14,1,3,4.99),(38,15,4,2,22.99),(39,15,2,2,6.50),(40,15,4,4,8.50),(41,15,13,4,9.99),(42,16,3,4,10.99),(43,16,7,3,7.50),(44,16,15,3,9.50),(45,16,12,4,8.50),(46,17,9,4,1.99),(47,17,12,2,12.10),(48,17,10,4,7.50),(49,18,9,4,19.99),(50,19,9,1,9.50),(51,19,20,1,10.99),(52,20,13,3,34.50),(53,20,13,1,12.50),(54,20,5,4,10.99),(55,20,5,4,10.99),(56,20,9,4,4.99),(57,21,10,2,34.50),(58,21,12,2,1.99),(59,21,16,4,7.50),(60,21,11,3,8.50),(61,22,9,2,7.50),(62,22,13,4,12.50),(63,22,9,1,12.10),(64,22,18,3,8.50),(65,22,10,4,22.99);
/*!40000 ALTER TABLE `order_positions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  `paid` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,13,'2013-02-16',1),(2,16,'2013-02-23',1),(3,17,'2013-04-11',0),(4,6,'2013-03-06',1),(5,12,'2013-02-27',1),(6,18,'2013-03-20',1),(7,13,'2013-03-11',1),(8,3,'2013-04-02',1),(9,16,'2013-04-07',1),(10,7,'2013-02-17',1),(11,13,'2013-04-10',1),(12,5,'2013-03-22',0),(13,18,'2013-04-09',1),(14,14,'2013-02-13',1),(15,3,'2013-04-19',1),(16,4,'2013-03-11',1),(17,13,'2013-03-27',1),(18,7,'2013-03-07',1),(19,19,'2013-03-19',1),(20,4,'2013-03-13',1),(21,11,'2013-04-08',1),(22,4,'2013-04-13',1);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-05-07 16:18:23
