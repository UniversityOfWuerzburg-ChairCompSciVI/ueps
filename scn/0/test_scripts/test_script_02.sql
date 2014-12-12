-- MySQL dump 10.13  Distrib 5.5.33, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: dev_ex
-- ------------------------------------------------------
-- Server version	5.5.33-0+wheezy1

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
-- Table structure for table `branch`
--

DROP TABLE IF EXISTS `branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `branch` (
  `id` int(12) NOT NULL,
  `name` varchar(128) COLLATE latin1_german2_ci NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch`
--

LOCK TABLES `branch` WRITE;
/*!40000 ALTER TABLE `branch` DISABLE KEYS */;
INSERT INTO `branch` VALUES (52849367,'HXnJsyGBogHBbGEZjTJfncMfcGbiZVHwpebLZkAzRdmkzydvHgWzMWdCKPnvXbNOMhEvyfDoiGLjbosdXrwTAsILBiqnErMIwYHFToYXXENnSJWTpKVZjCkTOtgVMOHa','2014-02-15 15:13:02'),(84313974,'PvsQfssZtUDfNeXbSfqbQVsuQVrytTKLGsEucAsPMyDbExjvJOpnFnkgBfeeBhIxxHHuPNeWOqGkryKYVusAfqTwhZfrYXEfysbHLMqXVRWrLSrfQyoGzstIKwVAGyHe','2014-02-15 15:25:46'),(151035218,'odunMghCEhCnMrkTbjnmnZxGYGObqvdmUFaBcbmNHSmRRpbTtLNJBTXhSWgkKOEDWlFCQZcUQFLNXhzFEvEdJvacmeiPhnovRZQzvJnPXALEJkzgmGsOFMdloLZaSZix','2014-02-15 15:25:20'),(331339033,'TmcmehHDNiGwHCtXciJTPzknZpDXHzOkVAkFqzBDDpWSQYHFJvLKcRDCwTqpDJHfcOEEcHTCdyqFlqhExXMejTHiLokbUtfwUBzJnVYdLPyiczsjWLwbWyXztfmtAJnJ','2014-02-15 15:14:09'),(394076700,'zHVQWBEKksKkrxwEDIASocPELXpYwXpnoOeiKkRxBIIkeAWjrDcvLlOByMhoflYEITzflSOimISekVonIpFnpiRiUrobSSUQLXqzmBeRUqgePdcYawupJKNJvPgGAVbu','2014-02-15 15:23:33'),(452818824,'QPfRQyCYyONmQVeOCFTODyIMOmXhfVlKZeaQQWAnWrqOZkHHZAMQTHajscLlLiGmmLLqqyjuaZPRxVEKTscpYbzAqcxfECgetwoNKNDockEiczMPsNJHnmmllTyrCbVc','2014-02-15 15:02:18'),(571480765,'RXzFEpunNVJqXlyYJSImLwmKUZGRMQbvjhUCVMxQoqSZTOaXeZVfNZeAKxSksOVEFRXRBRPsYcTOOCVUNaIsknSeRithsCsJFEeDywrCIiYiSMUbjwPXGFBAbuUnJBDu','2014-02-15 14:56:40'),(1130257184,'KjdGCkNTKcaFcftuAOoqcvXkyvvkBnEkUJlnxzwoYIbFZNWJzOrSpTVlHiPUdEIpEQcDnRLXvwshIJMDxxGmBEVGwnoOZgwLvRqWMBxMKgJAgVmBEsHWuPYsUivusNlH','2014-02-15 15:22:38'),(1230278687,'oaJQXFtjtJxhpsKHKqysbaCywZyEfzjaitUbHGeXyBhhWIVladINGVaFsrWQoMedWcWgxIXvCImpfFNVDMsLPCxeKIhVrNxVwFmGqPCAPolbObpPddBQjZaYYNQFctDy','2014-02-15 14:58:04'),(1276320045,'yleIRPBHcGJpYFHaMwDHhOSxjzasiAqrBmCxNkzwXhhIPTpprgHQWolfeoRJbZJXGFTsfDPlvYqOqSbmgjnNppTIBRgFdvUNwEhEQAUdqJZOGsZVpbOUMmFjwTCOcUBS','2014-02-15 15:16:20'),(1398674085,'kwypeOQbQcWEcEQPMCpKBXtvpsEkgvnTBBMctaAzXZasQaleogmqbtQdPFNCfpJnLUruWYZZMflpehErPCprvvnTAmVWCCHxRkFcQheXlImryHjlyFgThWuOphuSVSVX','2014-02-15 14:58:09'),(1659072878,'aQzgIZndyqmGMUEGoEAkLZSGDkmJgKQTgSMhPSdvNGHpGmzHdXgaWfddWXOBomXNhIYZKgXlChPkgifvgGhceUnycNjsOoqEYScpyPDiWXWeVuisVSbhFqLgBUBZgToT','2014-02-15 15:10:44'),(1962483260,'jwaxbKttqEqGGlrHVBAXyJnLwxCTTDeUxbEaJkOjmqUggBlDVfMIfTMzwgUAArEUsclPYFeJNqzddKAPZZsQKsDYXmksuJMfIOYArMuiOAozHyJFdNUipOEuzUCXHpeG','2014-02-15 15:00:16');
/*!40000 ALTER TABLE `branch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` int(12) NOT NULL,
  `name` varchar(60) COLLATE latin1_german2_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (2379624,'zCXPffbFyFOigWxfrDfZqVjqrLlamVMRXORWSsfnDMdyhozOcvmLkrRlpdDU'),(145499738,'UgcejaZUOkSkgqjyDUTooSncWEPQhsxuLXmXeFroJosnkZvOSsBZPsGScBaJ'),(210169642,'ARmaxBQzVuoOPPSurbzIrLGxTOmxQNyAewTcdesvWlGIuBwxQFhWWHhxsFPL'),(484672837,'cyLlkexXeYwPallCEGkrkJdcMwJgkqFJqiZfwbCXestggzRcxeVzHOnHuhlA'),(666906782,'akyZvnKmVgbDMXFOeVUGLkMUExGtQIiGLbcCJFhijrELcKfuUBdWtNztgAbY'),(791388845,'sooQaGrHYSrUWCoCpqxVDLruynAnzkHaHqPdRSUucyEnaqftLWMwmCjbvBlu'),(828471927,'AUTrDAtuxDVVlDsHouBvIJiaOGpMcfhMqYtVTkNWtdkpLjPOQAFkBzvyVJgJ'),(1035905590,'CWuGZfVIuiaRdHjQCxfNvNhjmULsryXXVRywMYrcYGZNhoAFxOoXwytRVsok'),(1165768430,'yPNcewwtCBikrMXiIMSnWzVywPfUDZodYLnRDgWQoiqVlBQhQfCwuraxPQrc'),(1228200079,'HsTeynqBGtoPjnumNxKtFeuDyWvQavVGcTUXyeHtnNbqdPVJpxeinOpfuWjv'),(1295313566,'qcQtowhIBdRjwhIyCekSGZcumbSkflBaxZyyGxSaQmGXgZnguFBwmbMHqBKy'),(1658901947,'WJVVMfCYemlviPWOewDRpvUysbiMNTbFLzATbtubZllbjfRXfJHgmavwXRSl'),(1938862048,'RyKWBSuEKABVhcJhtdJsjjTIKqKzavBdQfnpsvsFFGJZNjOJlXwexXyBDkKL');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` int(12) NOT NULL,
  `country_id` int(12) NOT NULL,
  `name` varchar(128) COLLATE latin1_german2_ci NOT NULL,
  `postal_code` int(12) NOT NULL,
  `last_modified` timestamp,
  `priority` tinyint(3) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `country_id` (`country_id`),
  CONSTRAINT `city_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (42998829,30428035,'pWSIeUAWPAREPMBrLNDJIuDStmGRALuVKmyfqfQMRdwCuiXHPhpQQTViINXfoxRQNpRnzxaVXfLgNPxvZFPbBtYdNmgyXsZyeGhcpbKNyVfgawjmqHFvnnZMcnVCRXrK',1769997597,'2014-02-15 15:14:15',21),(148479840,1777373033,'STTXBibaBZXTniOwvuTwXfxBOwBkRwqJGYzSnzVnxWWVvncmyCflwWqzPruJgKEFMDbbRThDMsFwxTiPlWvEfipDQhbXvIqiyccAEWIswqzCjMrCaYvUyfgxwjGJBblw',290709436,'2014-02-15 15:00:45',85),(428815379,709869909,'PXoRIRquvducakRsqNtuyYuFfTSnBPhgtNualtgGstBLogKKxyaRYDLXWZdvaClQfYcLFdoDvnoYfcMHsHYvRCTtnJSNJUxlGfiEUerNPElotvaSIbldKUcMpUPUtPEC',614651268,'2014-02-15 15:14:12',119),(500398679,1251318556,'IXCHfbWcgIQVckJOymHCMApylskqtbAuWKwKnGLbueVVtVqpAeKaUGIswJVvcyFkTyumuZCLtqrFbiJzQbbNYoJaMCCtqxOQagpVUjApXQTbjfzzAppYbpidtZVdYdij',2109201214,'2014-02-15 15:03:20',109),(568491873,1034532250,'ZlXsvwRYZxrvbFYmoiQlIlEmWNBfntOWocHDHQlHvwLyXwGbRShSpFJEiPKnsLiWSXHNEFCYQfhvqyEampqSXRWHCEvTaqDGNOBnfUPaFfgDhHDBdRATnYHdzvPvMpxD',471006030,'2014-02-15 15:06:42',12),(637383782,375990109,'XXDJkTsDoQOGFGDIISNJxRhAMRsbxAsHbqxuNNuLyHnWalgsHYNWZHnugUyHpbHpOeSddjNlgAwMUfuyQmThNKIJzTgcbSwBudBLwClEfEcHzQCaHLzbmmuXASEjbAfD',702202428,'2014-02-15 14:53:27',44),(746763196,2005542176,'zmsrEZRnlNDdNfSZFRnDkmynDKcKJOyvSuLgnsWPSAeyqcGhbIFnKhKJkESSIHlnGwEbMkRAuleiVRpWRfRRuUKQNiceZPNTlJnaJffwbFkadLBsZXCHPcWtpUzhtrRk',1730373011,'2014-02-15 15:17:33',26),(856572054,1447808401,'BxZnAPTmkCOSxwnIFUGJsnmasxILzKtyIRzLvLvWGGIBDbUFKNUGkMaQbqhnyruOzIwyQFAiLtLlDsaLjjoVrOuCQmrAsIiIjVEoLYrtaVpKWqnUXkuWQsYEEWcNkvMk',1322061904,'2014-02-15 15:15:41',93),(1204179574,1669180855,'DnSuHsiMptxiBsPysSUReStHaUOtWEczRerRIQMyCZZZuOavKgQHLrGxGrKUFmrDqRzpbVRwHHzQbeFcUGQDbUDuegSaqPycmwxVirgsweGnkjvAKbcTjgbSQatlWSrl',1901499136,'2014-02-15 15:09:00',70),(1323350965,87783595,'fJhyiyzApkcBsjcGNfldwGKfQUyiXUfPxSqsgLiqMAIObprYcQhLcFlInLMhQouamgoouBOSRvTHPcchqGlHsKXCcZTQXGscIiWimQHMADwcRkOEgofBOfRFthUGjmZo',588528223,'2014-02-15 14:52:58',45),(1357024184,1555057832,'HHtccNVHHqgmzOWWMcbNDYANDIeBDkPCwgsvtofbimneccrmfcxSiDjPUkJJioJzWnMdhvqwzUQYFHoTGPukXJFwCqLbPfaRcYXBLWizXfOpuBrWVbJdlDDRARUxDhgN',700757938,'2014-02-15 15:25:00',24),(1483422338,2026111565,'YofIDQkVBekEGmhBpeUbPyyeazYqplhXOUtwLaNptmdmsXfVSwPtvmxKKsYlFpyuYYPPrigNIVCSiapNHmxizCsuQvuShSdsXLkJZvZMJyoqLpbJlvuVbTruEmlVeKyt',1460665216,'2014-02-15 15:06:41',38),(1959807088,820486766,'tQCObQruGsCKTchQLTnYusjTjYJtAftMwnxerfUCkssBqlaHSqNpKQouFAWSCiaDngXoezzlgMytHNtWwJNXpOLAJoJmBIXJJKNtRQLiDMMlAUGqmTDLzPRUjWSzKAby',1635459614,'2014-02-15 15:22:56',115);
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `id` int(12) NOT NULL,
  `name` varchar(128) COLLATE latin1_german2_ci NOT NULL,
  `code` varchar(10) COLLATE latin1_german2_ci NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES (30428035,'gMqXOCNUoyiSvkgoDETNiZpEVjQIbFJGfoFCsPBszHpGBOkVkhfDxeIhwEdzBxwmuxkrHuDABpxQYlmcLVToWBcgnodKnnKRiizrlqRmJGOJfUnQDzsjDPGrdVbUieRe','fqVEyuqKqs','2014-02-15 15:04:53'),(87783595,'wfXcqRljoTzsFQzNQHaQCBCiGJFesWRFPAOiVahKCAhzpFcWmOKiofYhnOdRUYZSHwwrbHOsElJvMFBNzRJycXcfQWiTjABahSfaOxQEywmDNgiFTKAxildoeDraLteA','GoCKjYuMNO','2014-02-15 15:03:40'),(375990109,'ZoSRtTTKmhQtNbjZtXumgswBrCOghIxSeYCvuuPWMvougqSnsdqsotKnkKEpAhULHGMdXcmtvqYwAqlIsgpLvRzEdssBltQUbUqkTUQAdPtYihmsoBSRuOuykAkQrqKL','jnlninFotC','2014-02-15 15:23:03'),(709869909,'LtVSOIFdsgSeGeOYbjnAdlNkOtLBiimlUUFnqFMRDEedDubMCEjIeUVXACQUGOfrPNNoOgahCQKFtbvXuFqVZbTnErFChvxpmRTacidhuCKbeumbTyHJxJKFnGzfpKqP','WoIOSBHeDD','2014-02-15 15:16:24'),(820486766,'RANSLxhhufmfAUGYBQncWGVMNrgAdWWhXeZkonHDYSZFtocpDPMgEoBkUKGKCfBjoOHHJQlZzxfrSGbrJEkgPHvbQjCuWNHXzaoCgszShvaYwVYEfcDxfxAnYKSPmDpo','VLELnVNJXq','2014-02-15 14:57:10'),(1034532250,'UbyrLUxeqiWyvjweJOgTAapgTnbKuoMudYKirmXSKFjvdcCGrnnfbLKTMdXSkfKTAQoTfUTjSTvIywRbeVMGkzmgAHfKasAcZKhygmbNczcEVnrLWlaXqEyBRjeBfBkB','zOgRyZWxEm','2014-02-15 15:24:30'),(1251318556,'NkMsDluFxvYOoVvRgtCrKviezIalesBjyFuSGGJNevHLkRoOsPdcXqnLDDsJWPCbmeJWDQMyGuKuXGUUmQvHExmnpwZytkdQVsbQHSQLMEKsFiyvkqCtXNSvPvrovwcA','FcffAxkOoA','2014-02-15 15:20:50'),(1447808401,'pUKXnehbnzchQyfiZAXfGGGVJljjvjUqtFbsGWdAXeFJvfsIdjPOBvmEtrIKxDwQfQmQCYDUiSKVtOrIpqvIAxLrFNdMFIhcjxMsvIFDXOuKEGOiwkgBRiDaSxiDfwNP','epigsqYOpo','2014-02-15 15:10:01'),(1555057832,'gAEDoEqhZGAkNpzAqICiBxiPSLskXktwPdbnbSTtMTxDunIQWGqtnDklNmuToyZaNUCZHrACvAHwcAgUeDvUabHhBscfrUkFAePBztPJGGbHUBzWFxtRFEBtrNLbyrLJ','bTcYppUOvx','2014-02-15 15:25:02'),(1669180855,'OuUVXNeYdqdmgrxAYnXYATxQgQVLTraNDhDDeBSHiyCsFEggRMupxBoziiZtPtSnOwMJfJVVHzMRUHOTMfvDaNqILUrQnOlfRFlJmNWabstXeujIDqParlbYzdjwetZV','WEVrMybbgh','2014-02-15 15:19:19'),(1777373033,'JclWLWLIKGSjWkvTuSFvaRbnpXPEbfEHCnQvdqBxdhzBesVQMcBNsTvengVWpjYRzrzjCSSZuTOIcszHdFErdDyhvNaIWKQLeeVCwTAtMUKYXFEhfxvVecvqnmGIeLam','iHPtSboeZU','2014-02-15 15:08:00'),(2005542176,'EJxKnvmlirQYWyFtXzSKQONVBxIxxkDBcjzRfOyAriVdhXPBYHtbHysgQyXKzTqThWxfTZHeAebvKXJiVqsbAxUZhvPuCSonQmhKhOLTBxzFoEJcZsviAjbVOQYIIDgM','FNDiwKBaVi','2014-02-15 15:24:41'),(2026111565,'qbkTxwoyFZZmgBaGQlSHWLGidrLDHqlAGrLlvBPLvZfuTaFySPOUCSMjHTfhMhRUzjgAJuWbgBtNXYddKQclOYFDuOujBozUMvYxggTlokjpIniukFKNuiCFcxRGUXhO','TsuqhpoCWx','2014-02-15 14:58:42');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `place`
--

DROP TABLE IF EXISTS `place`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `place` (
  `id` int(12) NOT NULL,
  `branch_id` int(12) NOT NULL,
  `city_id` int(12) NOT NULL,
  `lat` float(10,6) NOT NULL,
  `lng` float(10,6) NOT NULL,
  `name` varchar(128) COLLATE latin1_german2_ci NOT NULL,
  `address` varchar(128) COLLATE latin1_german2_ci NOT NULL,
  `phone_number` varchar(30) COLLATE latin1_german2_ci NOT NULL,
  `origin` varchar(30) COLLATE latin1_german2_ci NOT NULL,
  `website` varchar(255) COLLATE latin1_german2_ci NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `branch_id` (`branch_id`),
  KEY `city_id` (`city_id`),
  CONSTRAINT `place_ibfk_1` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `place_ibfk_2` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `place`
--

LOCK TABLES `place` WRITE;
/*!40000 ALTER TABLE `place` DISABLE KEYS */;
INSERT INTO `place` VALUES (89786274,1659072878,746763196,7.400000,9.000000,'jCdotadPNkeMfbbuYxvYaDAbwkfgHFYNzkdeaskJzqPxYtprRwNOJxwAGxfCEaPcodpHGrdDmehAlLyawlDZZllDtgftzbAIQOCiRniZNcVLnitHQQJXTDkjLNDWQslZ','klNXixeuKRBhLRarIYxdKfhhXUdMwhGkRRDmCtwGyCCBxJgaJzUyAzXdLFhVpBpNbJygSYqVNJXrGDSzwQJxYymxWAFAfIIhZUwLazGYvbjgwpQNRHIguuXAmRUCfvkA','zkrpMxNskGyvCoaieLBpAxRibuKzgq','WbKLNWZjuxqekadkAzHqomymPudewe','qNEbdRrVHkgbBnSNzBGGYhmDpTqDJyQPOSpfvfVagIBMuRESgTUZPcGRfiixADrZhqRCTALgrUqKHwVShMxWimpNnLNUzPpOXWdcTWHUPZApkBqIDXpoukGDKuuXAvdWRomKJaDsxIGRHlqcPdtcjzrYwhkeCREdmAwgqxSHHltRVpMxdQTUUKnbiazdRMwtaryYVdvDpgcCoHgFIHhapecyKDxCidWNWBVIBgMggrwAJazxwruWijZVJCnYtxE','2014-02-15 15:00:53'),(100960801,1130257184,856572054,8.100000,6.500000,'OcInLVioePWNniOCQBWEoNgdVJXhiopoMNeXeZBiYhDUHIMoRQqLdYkmXUdzqutbJAZglPYCADnmDrCwKTOHAzaTTLdstXlrOUiBIdzdCcmasnUMPcXPLgrXxqXmqHjS','FqQKuuWgQGcMdEZfdhpomQGulkosaknxjtoMJiIlBjXnoTfBdlIISArSyHtqhyPTDHSYXnhBMTPCGpRCjQdcnfChRhLoarAsnEqhXmtMnGFVtwkWAYEAQEXBSUQvpwCw','pCfHpKhTqHjrZnXzdlqJnKtqobTbKH','nPwLbOJTCWBxabZPltspVPLkBLdPcL','LJrLAiulpTpMUQMPFdyNYazKnZxOSeNMrHYiUifiiAMmeBXYvVRsdwVekhRZEqvOWvTbuRdHNBrOsdZDoXlxDKYGBYtMUoqpCSWCbtiVxkkpAdUCfGHyMSBcIHYuYOOzCDTsmzWLByagktJwTpJHJVQSHVgXNkqBBlbjTPzdWcsRCZztrpUQgnSbhqgIPnkjnejgHGlzUOuALPwiBHLxYtNMHHSRrcLEhgJEuVvwhxXwJmOunugxdEnPrIxfCcn','2014-02-15 15:15:07'),(400411620,52849367,42998829,1.100000,3.400000,'gtCxFEMODefeBkDzVOmnMOILsCCbddBRKFhZbNVEOfMMDnJlSRSVQVUWJwatmtqvyMdtXvPDWXOkmagIAqPQxXhLgPNOclMmpnFepUPxHpvWptFmSXyYpjSKHoFgkmLB','KLraCiVHsoBQmPxkkCipTRWZYaCVrvFpJIVVgaAsJZzmBANemRPsjjRSwEjUneoYKRgoMyIrRwZtsFWjYipZYvHUpNKcQmAcyAEopodUAUeyomxbsvUpMPXHqpumoTPN','KRUjorEVSMlIChTytSJzGjrpIvPski','jRxeLDWlAtRunkOJhsgJMVxtBcumdk','zPaeLjLxmMUGhRPADGKjVnEEgGzCPReyOaZcBrYrdIrlqhXYfSRrpChFJxPJpanPMLcoXIuyZymITcSBcntecQVOkuxbClRnrupIDeReWxERBHGnFJGhRYRAZRkCqXXEFYCWGSwlQdgVgDqKTENmdfhhQcGsVJFVveHfNGILPAvhTRKQMxZUuWutHROkOnnTrgOctJnJwkepBaQVCcqIntYEGBTDFyZsKCMloyFbVOogCUQpHtLjMNkmRAWAnkM','2014-02-15 14:56:39'),(606061474,1230278687,1357024184,3.500000,1.500000,'bwfteaGSAEuYytgyHXFUIALzHdbFjQtkBOEvQVeuZZaQhaQFKabYuunteExMkAuSMJIaNcrOvqjIIBvuXvvpSntjoaNMQejhfYqZrDpaDaYcssTLKdqErbiRIAhNlKNa','CmeAcUMYVCTkYybDCcFYTRQwmJDOcgUCvrKsvICRshdtEUSJTaMWiqDVgDixdstqodWaggnhEjonRfNTioyOTgwuaiLWwAcrBBHIVGOVFBtLuRQvIhFzBoldTgZbOPEv','yiXSMHUZWfXpcpRLYYRddOBjXwyFVo','dizCnDYJvbYdVhvWvKmRAcRpNRufIo','hFtYpBJCOKydCxXOTjTNzYURsQatsCcUjpOvColzFNWXfQVgyTAsJXxZuShPRCNVXjrfEdpftaCNTUGnnZFAYqwaIwSOfeilIjJAyFdAkLwAKvQLxbAjiowWniNwdHaNnUVqZZpIGWplyvEnQRWGidGbBexLKpLTNeOTueRMoVDcDBIniVcaqIYcBOLaEhVPYecrqREDYqgBmLpVJupDzDccCcxcalBuWddRttZFXxQVYMDMkIeyxmjBwwvZkPu','2014-02-15 15:22:46'),(667249101,1962483260,1483422338,1.500000,4.000000,'SDnieFgArhCnNoeZGjYzAiPQXNUaUTImiQCVouwXFPOprbWbrcrCPDIrNMEvbusXOBlMKPfXNreJRRcSRXycGzivkuURNVyOlaCqweXaCuzqSLtvVoBrvvCLhOsYEdfb','gibWxPSFyIZTpiHOZhDWPTLKeAtoNdDYiIHjSjmHkGOSmsbshbGaGnoNmlUxHKHZAWSQWMcoSrLGVKGPFeFkfeUEIZvDHqHNYORkFTErxjmkqUtEJtLYKHulpAdAuGtV','ztEihmgmYzAPNVnxDkSddUaoVhXHBX','GEBdgdBmQVUoGJWWtDjpGPposhLtHp','dUZvEsBriyezQTMjAmwtggFRXbvzwfoifwjVjwnKJBmyRxTWskhRUWJrvMBPWswHmrfohPgobxLbMcEifQkPXkiuYVLGUmMECpJjPEqAtOyTDFQJMUmlycuztUjjGimVMWorLEaKUUfTxBaFESJYqFVAljnUbsdYAnEdCaNDDuZyCsQNEyXanYvldLrJGCEzRjLrDMqKKCgbYtpKMQqjomgvAylgrtRgEVrWtGoAgAvGkhsMApOZWxnWEybudBv','2014-02-15 15:13:44'),(926025763,1398674085,148479840,0.000000,8.100000,'KuFEjeXdciFdcbVFLoNBKAevBJbsakzplmUyANLXDQABrXtvJEHqilwXMJaqxbKzIbeNLPvJzoNzDTDxcEZuXbPsTDMVsfPxoHIYwtCNCxMGGIGgyUxARdqxmwESiXWa','OjLJOcvLUQUslGlEMqQphSkpXfFoLysyKUbeCIOGDnVcHAjdqRpPAjaHkrucfNBrTCGnVXmoYPhXazUmDvMApeVhNKlLuJSIFzVkfguRnVJiKRbuNnvsdpBQfmRunJiz','HpHAXeKxkWxAKEGvSKSBBHupFoNFJd','luDTtKkxUJVKXxrBwzDwNofdqRdmXg','bZCtnoWdbgDEMULKgHTbCiFUszDFgUZDUeMJUOUiyzISbpqZZfQtTjKHoYNSyIJWNDccDTTCevppmbxAduGZMlUmbkgpqoymzlgnuEhkFuCvvIrCbYXyMRtCkBpbaGVLVegeDxSIUyNyLZYDOySfMfPRvrDQEJqusxYIImVplerMrukcGNJDZzRluiEryUnEImYxwRJigEkIPPFjSOlfNAEJPWgqriJbznSHrKygbULjFXyQNtfzvRnqsWTXqiL','2014-02-15 15:04:04'),(1006976200,571480765,500398679,2.400000,0.100000,'idXusDwsxcXPLMiMiFDLzCuDSaumGoofmrYttjzqocOXsdateYBiwsKEaxddJeykEhNeTnFmGFnTVuFPXuMMryjShrrwcyUUTKnkbHibJZCjUqDJoBCUiyKsBECuuHxE','eBBVVGVXlpJSSRVnJqlYgtBXfGzRmhYaJewvzrIJsltZKGMBleMxXZzvsfvovStYwQwcaOYdjULKzbGSZVEDRCmNHtRWqWRjPLFUbXumdUeUswiBpUfhmwnXQRsOZeZL','xYKXykXVyhHctgCmQSPDkiRKCunKrU','nLmAlrxPIIksHShLjZjjQseIeIMbmL','xCTNTpCuRCkozckXFEsHhEojMtftRpnByplhmQpqXRVrilKcDgHTQILwDSXrHgPdFfXZVRBzkSwVvYnFQLENgmgMNwVgPxOxAYlOtREWbtSzoQiziUbrmlQrbUzYqRQmbYTKbegyyVNrldFXRZIOebkDcfGAFQaLOkLEGnDGhpLQUfcGsAUIJoJDmltvTUAdjXNiKuCzrPZGQqKhWrHpGFIIpFFTbWTNFPESapqgrdqbjfqjbeWmRthLBpSWKTZ','2014-02-15 15:10:28'),(1305574863,331339033,428815379,3.000000,2.100000,'aNCCKlmJQpTloHrThuDlHgQRBmuuJgGptIZbjQmInwDxvUDsKQLjhUYOAkvAkqisZKUUwBNWlcmxDDfXWpIOEgHDHYxGdsTtRuaIbyBmVrYAsESpBrgmNBCpDmPaFmrp','DyoidJBDqIzHBYYfYWVsZRoaBAvyJqBcoYXAiDSzBVAQXlJURzEeapGhLmsXQRYmulkqmbWrwODVIkKDnRRCdlhwOaxfsfmCEzFchkKRETTStszbauTRCHyCCRoxovmp','rGsqAGuRzFZYhsvOkcKZcoxMlopiFk','wXePlkSCBvSOFjTFobvxiqwvfVovIU','yiWXeuOKyqPbaOaSLXlJLfttblKSwfEykSLjvJKnQJbEOmpqSNErEFuYFrcnaRBVPvyBvUPCiaXyplGnBybyTFwVYdxdOheeeVWMVnPzUcQfUEPpTjOuMWGiZyaoFMcWZTUGesswMuLnhIwLlcVpjwxhqjRGJgYtIAnRACUycHNKXUCEiEkcFisAIbaxWbGYAvToZKejtTPwquPqfnytsyIsHmIVhhpWGkqrHkljNiQmdseUAgRYFmADatAPzMS','2014-02-15 14:53:48'),(1347565620,452818824,568491873,5.200000,8.500000,'iXwBBaCmXSIibzjtotpxfgmmJbvrJRqzSrVAfztXeDbpvzbiUewRkviUsCnndvHMmOXMtYIBzcmvAnZJlIPEVUYNCTSPuPIXFhdIZuJGCQatcJkorkVAICjsrZoFvqZr','gXHwnlYuitgDJomdbClkcyxfTAbFkvrTDPvhKgbnimWMCNrBxOBSYLuXNSaeWfGGpDoRvrCaXSSbKNVdYxCwhbMaXPSADfQhsFwqGFLpwOVAhkkKyXGVkHwMZEooCUjA','IQAaQESwSCrRRJNYUHJLdXjUSRZgwm','MnyJedjnLsPlpRDmGDSXXBTJtuKoxj','BAzLwmOFpfwCMphryVTqkyznklvQvPGbXLwFqdHpRaBKPJdTMFDDMJnpzdnVYePHJQqfitTYlvaHUaLQQuiMRoPaaGAyMOApJTYQMaBidfEcvwjGBBUOOSOTYWGmpbckMaZvqmQFQhiWdDdDxkTESbkEDcJXcfGPUuCMhAnLfXzLaTJMhmYhxDTTmvfXecakoYLcCCQRKMBykHkXraQlCOXlBkSodHEpDJooXMDvJRjNSAXQzOZnEvOONeVCYPx','2014-02-15 15:14:26'),(1647197740,1276320045,1204179574,7.200000,0.500000,'fiMSDKMkYZIbTkmePZSeTizPriKXjqCuZhgicPZWKLLZfQYdOTSNbGiyOLTfGhIvaSJMBxHxvGujvAdznZoOHRFYxwwjyluplxDDntbIKXpEwYnbGDYyYVarHXGBbdmw','KEWjZtOYSoUoBNwpIkMCBNZckXCRvHehYrPtyAIEBFuNygvswtIYuWUBDoZwruMDiWBzHXxHIjPLhbxuVnropPyyHbJFmZepkrlvUTqdtiBiuVoQUwlcGHjBMhTJIXJC','KzjslWuCchwZSDNUmFIHCpMapmwfgk','IwBMBkGrHaoZCrpMqAmhEnfkvEIljN','mSgGNyVTYJNoUPFpjfXTVfdUkpNDxGuBGgWpevHxowXhhSdtChmetEhZXiwKfykVsuhIsQjhfYHpMnsqFOQmuaFeUFcGIRJjNgXllaywoAuszDJjnXlvgWtLgRczBDwjFFWOsAiJHcgdSlKzvGicCudAzHENhRJafOTwMLSULLygwbBCXOMRZSRvoNRqHpkCzZxTxgsZQjjCTFwhzfLLguQQdxVVrhffbrmvZTtNwjWufllFOcPoMAaJEIHTFvH','2014-02-15 15:01:04'),(1790874628,84313974,1323350965,6.000000,4.000000,'JbXMeNCVCpGfdhMteaUNbvvXKevlMgoDPikPObWFjGxiuiMkLnPxKeoJtNrhDpSJMOhGwlUKvXmNJZvAKGtSPwVnzQUctosQyNriAWHwyYEGnnXkGEMXBAiaWkCvEIfH','uoTquGSITJyBbsTlSRfSWzAvPwBSPmvgjqlxzBrmkYaJeVexfQjelyfBgqKwZiZEABDYTQRjBxLrWsDCQLIYlgRiAjJobvfeijTVTcOjtgrHtFTSwVfiyRviHWbHrVUJ','mCcDaiXtYRQCljBKSAdPMiUVKSWdBX','RufdyTtvWvoYMLjUTosWMCiJwyCsJg','lppkcylpYWpUvCDagJLFJnxKbvJHqNQfdugCPuCAxjvojYHAGhqXZaGssePtOukmkkcGBpNZsTylvZYaJhbOkOFKTlNVOLZMNyCQPEVewPkkPjserQjcBntWCmGtgrPiKmryvitaxFOisyPZMenpZkedUoruMjGOAejUjnMAAkOXbPrXUZGeHgeVFoeGPlFdVDYrLodSlcBgbVgJLjqMnwHBnhwRFjyuPkaDPMDiLClFyOzhgktUcQyanoafOFD','2014-02-15 14:53:24'),(1798987669,151035218,637383782,0.000000,3.000000,'SmQfhfhBdaPgBfofsJnPCZTVUbOgrGXEVnOAXleNLkjdSeFjvpOczUoZhFUJFhdzGVwGzpPZuCFlSnKJeRJYWBtUhYMCiqAwHWEvyMmBCLfcbpwfRKqGGRzMpzrxSYEM','XnwcRasTTBBTcmMBvANHsueEKRYjCqrOvMJxFQxGCDRWIcMTcfYHzGCFsewlmIEjxfjpKQmnhHkgPJPLfimjfXMTIKnAnYwTeiLxSiLNAKrKSgYEzzXDFqhozVgWYyWf','fHPwSpcGoFbxEENchcTKsPEmBaCpyc','ffZfVwWGFODiAJJPwYNckpzphRGkhp','GcVCqCiMGVRvbzkGwRAxiRDQkVhrdqHfrGGBvqhimZJpAWHEpLhISMqyTYJgzufsAIIPdWmenxqtAMJGXoHXfsQDThRuyGFrjYhelBBMJpuqdASrtwUkeHKShwGHJYWACjyVtQwSbAnvMwaZlcdVUQQcXQLQzoSDLtnDfJBvjGQqdxWzBJrNgAEzHABVWjQHKicVUfxJjikUbgciXNNBgqWNLcEcClbFOFZHqYIUCzJJJdiUnLavgVJoukULqGL','2014-02-15 15:04:17'),(2098590354,394076700,1959807088,4.100000,7.000000,'AxXtjRffGKHHSaDrinbHRItnEDSivqprqsHclGBEelBROtiWLrJEiQcDIdtubIxMZBgPbzvPTUZEEIywhnhhhQaYwoJYCjYIsGOXMDmFMDLybZZAGlkrmSchPgLWRHQN','THyZVwtliVFfCkPbqiOMQByjrXLbflOUoZehNuHQQzceuviaAOpMigAItYeHAQyOTjwkNFurUxSfdKQczbkmhrripBNPXwqYkDgvcIBcfaqGrDZSCusJnKsRGgVGgVAG','bJEIAvdXPmBXbededEXUDlRRYgIMdx','BnzeObpXvvUAyCXkMCIUdNnsgFaHvQ','GvMHxQqXQnzpDqPHlhiipqzHSEbyXzLzrPZiDCTAamvzQxGcjwIVLPTvmxSzAhgFpMpkKNiaevlKXKcaYtpdoMnEGxPyAcFFWywosCNDjsTwdktKaLXSIbLGzLTYnQFdlrgqkoQNGvQUpBqHXNfqSwvXLOsZgThhtqwOGJDmQsVekXVvIxnCKEnSGLlenIMaVFIfKKZDiRoffUdlfysRhlSMPsPuOqFGnqMYRjpeSBUXYMICaHhWxtxmjeAYddX','2014-02-15 14:53:08');
/*!40000 ALTER TABLE `place` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rating`
--

DROP TABLE IF EXISTS `rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rating` (
  `id` int(12) NOT NULL,
  `user_id` int(12) NOT NULL,
  `place_id` int(12) NOT NULL,
  `category_id` int(12) NOT NULL,
  `score` tinyint(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `place_id` (`place_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `rating_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rating_ibfk_2` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rating_ibfk_3` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rating`
--

LOCK TABLES `rating` WRITE;
/*!40000 ALTER TABLE `rating` DISABLE KEYS */;
INSERT INTO `rating` VALUES (138271782,1911024254,89786274,1658901947,120),(170736642,406329612,400411620,2379624,77),(233912728,787637385,1305574863,484672837,92),(469911067,492854512,1798987669,210169642,4),(935478540,410198961,1790874628,145499738,75),(955537452,1722957969,926025763,1295313566,13),(1006149713,1302833652,1006976200,828471927,107),(1036564081,911164219,1347565620,791388845,21),(1117639279,1428694217,606061474,1165768430,94),(1323161540,2036115546,667249101,1938862048,59),(1416045206,884625027,2098590354,666906782,79),(1731128213,1552975527,1647197740,1228200079,53),(1972253623,1421185891,100960801,1035905590,13);
/*!40000 ALTER TABLE `rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(12) NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_login` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `password` varchar(128) COLLATE latin1_german2_ci NOT NULL,
  `email` varchar(255) COLLATE latin1_german2_ci NOT NULL,
  `is_admin` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (406329612,'2014-02-15 14:53:04','2014-02-15 15:10:25','EwraAEYIwZQNGLhkeaRMxJacjQexUGbVOOalcIaphmLtBgIZCLRXnZFpYZgyVcpINiGvdgiyHrQUThvXGqmqucYXqRjLzCwdYjFVXPoGEUWEikTLCFwDQZaiFyFGZqvN','ySoNEjiSZcGXrsbFRrxNlCxQmuoApdmFkhgxdgGdeZyvkGTzrElYkwkvwRTrwPUnpGdDvhbZpiSJMKFKTOTQBfIxCjqqustcxCrIbFvBuXDdSfRLKOzscbGqOfFhWNYJiqmCSlZjffTbRxtWPwrcURSgxhfaOMrOQmWHTQTgqVZACGgFSRcGNqnBgQNBjZVecTtgrZrruvIjfZnBZtJCkNDBcCrbpyRuUcwnGGddhVDCBjrMIytXZnMRpVumRvd',109),(410198961,'2014-02-15 15:23:06','2014-02-15 14:54:30','EOKBVRKLfeOsKBmAGWqAGDYmqxprkFeuCdZbVNftxYSRjublbdvjsEoTwZTePvwiovwuoaheEMKUIDQBsKiZAspFOPwmqdPGPfZjxOawNtuxmYusypZESAbWZMIIaxaz','LKkyPdXAReBaHmkzbtsWnzkWXjBCUPtpKetMSeGnGtniUFUfPpvqGcqEinVtXjljpjfGfTYSVcDpvloFqLeFWoWdFnsmNsHFNLHxjrVJkmLbqepkmnwQCFEeZLsWQAgIfuuQXfHMNtoWdTsAsKaDyoqcYdRAJZZZnBlzjwyjNqekmxvPYScknWgZpsgKCeMtCklFiHlEJzZcyrGXEKNlSfqZaodsQFFXotzzlNGoeCRerIalrRkEhSJJqYtLMHc',94),(492854512,'2014-02-15 15:02:08','2014-02-15 15:04:02','mINNnzIOurxPJiVRVoudfHUzSGlgjAdYbPZqMpKqCcHHdNCUnPpjDyAhJPTAjbvzECfNicyVCdAFtMdPzmiBdNxfViLmZdpnVzgdUPBOOagupKdkTrAGNCORzUbcVZjy','frodKMMqvzIyeLVILhceGFNEzBmHEMmWBAKhZqDQqyuUjqcTYWZBBbNBMUBmEFdqIBsOvWmImxBDlfddqPdglRvePRnfSujDOVNndVZpcGpFvskYNwujlHxIdNBhAxpeLUxCAwamBhoMPiBeTUKIWWMHQtEcFydcxoDySquRnnRIlAGfwsKRIDFfJYpWAusViLXBtRnaWhFdXpCHULfOzSebuCTxXAydDlAUNPEmFHurUHMTaMHagskEIpffuwT',62),(787637385,'2014-02-15 15:22:48','2014-02-15 14:55:02','MyPHaKeKDCuWQGGgvbAcsPqTFogNGqOMNSDDACTyTbuaSYjjXYjIWOGXgtuoNEYSJdAXilXsMRdtEiUeawchUjtUXdJrGnxhZVmgWztxjAoFRUmonyPkvlQzOLnjUImY','CnoQJopUCgycuolELxhhRfWjqWbzRxTAGPllIjBWCNoQpJLWwnfrAgkWVEEJHTHDHWaoryFtjNeHQmmESSmcNZEkhyDoGHkfuTvqOgmQFsUhGRUrtQBIXWBxyKGuuKnlhzTYLfcojmHeiFFaQzhqcUwwHxoaUNFykWALJlVLzOskYzvPOompttjvctJdWrOnUUcXeQYnHLgFtmkqOesBdDpuKXNaSiTAaOLJuqeUZgzjnzvwpfEHqNZCYsqGslj',89),(884625027,'2014-02-15 15:22:39','2014-02-15 15:03:45','SaZVuWJvENCYjzETQjwtVQHiEvLSBdZurYKlStmaWDfALewjJoAgNCXGTdPUuxmTYQfqBBUvwVPmjzUWUQUVHZHwcOIyXdCiyOJTGIzlLdUmAcVHHzhgufIitkXMMSDI','WyJnUUBOXfaJcCJUnylBWCIzpZZadrhGIiElFdYhsAlBxVPmBVxupeOHjLuycvKXQnFCCZhFDZATvMPhWmjzkAwXulDlIQWYniJnKDxFVMffRskgzdcmaGPUGxpITDqTyAgHeSoyfWCxzabEfcnMjCOKURXVYdiEKZfntFvBPhHyEboFDrIiaplGlHltmuglGhBGzNWjsCjtCjAcQXBOIYAQxwJpeXZJHLzNfSROzURjyrcTzOXWLOawqfWwXoh',86),(911164219,'2014-02-15 15:11:20','2014-02-15 15:03:43','nMcVKYzyzJouUNpqtRlibgWtIHzKcbriFdTMujlgzjyCyaHybhMfXJNzuhujEVUBXIOdcZFvkxyZJZAeORRpiakpLbipJYYRmwJQpBifhKBDBVsqQwqLnquHqPtfdsWj','rzWnEnqwDtXnaODzncIPDFpTLJEpYCYzPWosgXurYBVVXmfyvWlaeQuDENsyunTmnElQrZeqboOnMXyNFvvQUNNrmTonFCuYGyASqKXFajuqMpkQnaJTQQazKGuMTfSYENRldkQPEEtvDoRTVqnLWgCUNnDBGwskWPnUZkRxqwwAlaNWDHBllgVaxxlELQdzszDLkrXgyoPjfQAdoeINacDngFupryrtpUhSsHYVupGYuYSGhmsItFmvCdAmOCB',19),(1302833652,'2014-02-15 14:58:04','2014-02-15 15:04:52','kldZMeBjVkNNafWAcBODmFZWVEpCTQDAAULnPmdhCWyYICmymzlymEhhqjTeJZqtApgFedeJKQOqXjUnSsRVaKoyCjFCsQQWgInelcOJcPhwuJLtwnvTHLuUIteUlWHl','ypgToKAaNtAqQPNvqFKVCqEuiboNqEAxSJnpfpjdtWJJBuKkAhODYKkioKDKEAipUbPiNzwQshHjRSJSlfBiFNWJmimDyrPJbKdVPbORhcebufMuXDfwcVmxHrTqGaHBrmKkormlcGVJaXRJWCIzcnSXdGggxrKfZOuWyEObSjMuiLyMfHCqWcQFdaWYVuIBsqEnpIyWDmrhNbfMMvkYBkUoVFMpvxxQJeaFMBixLBypPLxhlakFKUDchrSrgui',2),(1421185891,'2014-02-15 14:55:55','2014-02-15 15:10:42','mpXQSIayhBIHAlgEJVajXCtkflqxUSXdGeqEkxQPMfatFQsyekfAwZrhWaTDoZmIVwBwLnjAgGZQsRIFngzybqtnpwZgKQFZdhrMXlWcIWVaikSybubYCDeMrBXgzQxh','WaEabTDSmTEYECZsCVoQLWEVltFVhwXzotLRErEIdaxMTThIqETuReylOxtkCpcQLsVcpDRcyMJeWVbcAoeyGqBmhYijRHUyKaEfEPVMyHUWmkDFIzTGphTXCCPBVhUvoRFgbjrsbuwOmhBpVopHqoMUqheAAkOdVPUkjpJzAJSuXZgNKBpAxxCJXcpyHHCOEbJkktOLFKZoHlxSWnpxzCsemWNEuTeHEWTzvQZiyFxBxUcUiVwnJwfEJeNhMyi',104),(1428694217,'2014-02-15 15:13:12','2014-02-15 15:04:33','fswvLcxFTYtJseovLklJEtcJSfLioZhhnhTCtAevSlFUPMsZPOZvKacDCvYmQwaMNQSiOPlAggKcnqwfIZuYSHXBERVtXLgTxgHjhjOiPVhyVCKkDTevmdLnZzwiRtDq','BgJqpmUmPJdRSgQCbalBqrZBxWZkigLzatmxAzCRhQDlzoDCKkQssgAeFxUZFNjeFQFikvsjoTXTAGCOHXVneWZzZzOFyZdbOJeNsnDqEZIJPCYkcGhIPRvpgxpKdFHYOcxgGNRiquFpgZUkSCrECGZJddHzUFCbOdUDqoYrBNrOcBQMxizXYeCyEWHJMFixWKYBfNIIGvfRiAiXOVjaXIaiiCKVVAvCIJwzyprzRwPthNvKpBFTMbmlvRISUFD',72),(1552975527,'2014-02-15 15:20:43','2014-02-15 15:03:41','uvsgSPouwthFIGMHEYKfBZnMMyfxdLaWdnXYmxxaxNBOqXKgxeeMCeCVUjSNNPxkndanjaxQrrukMLNjwJGAhPaHvVpgspJcufTlZBBNAQvubOYoFdFWUlFdItrPzNCS','BWLEtcUPHcbAzDGihfpfhyjOCUPOSdXpdzkwyLmPaYHQPqTsqnjoYvhkpaRLCapJpbNWOABOmEhfiCTgjNuCrwhxulsWOrRjbChNXywIxkyhxLYWpkKnhhpnEWUSJKTOHBOnArpObKEXXbUVJrvNUbymKCPoneRKPcuBPgPqjdbwQISGylJeRPFerXVNlbCRPcvCYEoXpezQOcTxSAkkSqZfdFlrpnmsDVucKnWLMmEujjhRnQhAzIWhcMThGOJ',117),(1722957969,'2014-02-15 15:14:47','2014-02-15 15:00:02','aMNRTTpSZGfiQvBDdSKcjsJjQFJPlHNYMUobHziSabQFeDDSxKZAFvFUiHuKFEAstIXKOXWJrTUScNwTqNbwFHGgduAyWJzzoeMdHyfGcjTiHwUwKGAKJHWmqwesCdjZ','URiPnUiXZIywIjauCAqNjhsSzqhBgshkymydrZScjfOAtBtrCbXFCKnWEkEjdbCRPSACnGSpfMTHrCILAlJIlLgCSmMqKVQXvkdULWnewoihOLjMKMRyiubSVjdUWgkImBgWyQFgSOmrCwwutpHfIjAJlvqKWivdGcARvvOpnkLiAgCDsVRSbTOcSfbZHkLYQzLtklzhwWbZVgFoZxGpfViqkXZCefNoSITkTrYIaNxuSBsELGweLyqXdSYBmIi',111),(1911024254,'2014-02-15 14:53:21','2014-02-15 15:09:06','QEETmNUOVgjyUgkEkDmxSMSDqoJqAIkCFeZjjGLVGprLfmMwdcOzdaYhiongemqcBDcoRYMHuVQLiYmEcbumxGopTUmSAyKiljQZKkSeIqyGmMeNeNkSnPLsQrNXqrNA','ApALfCljmjSkyDYxcvFQVgZlBpwKmlPPAqrvcQBemkGWYSKNSkjaEHkUwOIEPKORXVXPVPjEscQfuyKrMMMLZLUJrpPYixgrvwvFHBOXxjArrYauADnxueGJToXntYXxSngUkPgMusQNTgMMVXzIZIoKAqHqqtpsxiHPCyLmyeKWwjNrPWjJgOpEPpRMHOwldydekYbIlUqhoXdODDaFAsOcIiCHRtroPBlcbMukvRuRqVCBCkxKgWcUDkuvJyb',4),(2036115546,'2014-02-15 15:20:54','2014-02-15 15:00:49','VvpBRWBpdWCBTDaIaoyxJofOJnMvdSiHhumHUxptdMlEEoqhXypPnVCiKIrhFJReijfPnmPvOjVVbsfqFWOoiGskdlPlfoKgtSlQYSYpiOLgbyowubYfwMNmUhXwEQzM','RhIaIoWjzUPfwskzEchFBZAmjXfVzPvwUQtwzMIrHtDNNRDCANecqlgTRHjyHXlnGinicxnUvTDewxmhwKOvyjXZFgVEavShRnTVqgpIpRrIcKKyLmvjBWxvQiWOSUeNBHzHPjaLajNomBdJDtsgRLeCTloYyUzpRqPluQikJUvojDPCHvTKlfqDaXLYccXImJOJMDfqnsMsjYkGTCWMIXLkUUUKwRURBGYBITUAWPBBxbTIPZmouxVWhEzVZEw',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-15 15:53:30
