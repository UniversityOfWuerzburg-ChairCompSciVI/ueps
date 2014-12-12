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
-- Table structure for table `departments`
--

DROP TABE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `departments` (
  `dept_no` char(4) NOT NULL,
  `dept_name` varchar(40) NOT NULL,
  PRIMARY KEY (`dept_no`),
  UNIQUE KEY `dept_name` (`dept_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES ('LKBE','AiUMJodGhryTrDwROiGnDvxsoxTHRkrxPhLSAaxd'),('sWMd','AMUCCAxxkLUJOwDmClrirNBgWdvQDsPDkuNdBLtN'),('GQpj','ariCcZfDlrBdXHliiKwFZtDqbqGZrkkXPHCOXbjt'),('ZVsH','bmkEmlqpHGOAOrqWzlOkMDGoBauvucaXFWUnSJKc'),('MtQl','CUKHVaTWXIcQnWUKNjSnCiLKKyCAShwgZzNNRPpN'),('YKDo','dYexoxDUGPeExvezGvCEwsaIKFwVuFnezwWzXtmG'),('VWvu','eFbmLWISkLBBSHUTRALKQxQrVSnQeZiKAhoaGITR'),('WwlV','FyRAsWGwKjAvphobniXxFZUPGTNHhifBfVpFcDIQ'),('QkbO','GqLTvLYgvHJAPHeYzMskZLvPHgfbyZRsWXWrGLSM'),('FHwh','gwRIxcrTZdVPuqUsxEnpfKxHLFTutEYbjDkwLPqd'),('GahL','HRVrjmdFyFvbYHRbtvLRxNwQLkYQTapfKdlJLKXy'),('oIeT','IdrVDGuHvgEBIBKEyhrXcMDfHzAOINnfMrjzhcdt'),('yzyN','isfhIjYFWQGRiGRHhAoPJrSslkmFXXATbkVfWDsG'),('zyQu','jynlHHmlIDNoMDBleLVYVnPYpficdhNuuhldAhYW'),('CRQx','mWqspWTVbdmbVUyitxhckjuLoKFMdQqIPGEHYfea'),('sydD','MZSRpsvkPIbtKGSvfVwyqxsnNJUotTVsewRpjgeE'),('XFnK','njcCJzsaNpmKIonbGeFzNOCpFgLNABkoyBiQMXlC'),('Afex','NYKOVpayLzefwamWyorbWUHzAocONqhXfHmIkOfX'),('TSOq','oDZQRspilmOtvrrZdnrIsBdtyqveaPWTvlTtJAPh'),('jtqu','OMGjklGirvAZTpkzIUYrEzxVJOHMevEpkDGgIHPF'),('uLce','QShmvkIAaSRaoPSEniZetdEVfbVdITYtnozLEIKa'),('fXwk','qwoQKGYQXzKyKgoHKreZlgjALnWqQwpEtYXkOvUI'),('vPwV','RIhAoNuPxDaZvuOZKJHduJSQZXAWyKCdHDuYrxIv'),('OCsm','rYpuDGIQAvWSshceIlFSbZWcsUQLOxXdjjkrWKdV'),('epHe','ssjuotLisYOsIgRHdMkVaFZFNpTnAWqdvtbztPnj'),('esIn','UcmYTdvAuwsXxwVJAoBBSLWYexGBSlgMCGjKrUmF'),('WGth','uWcfwqwGiVaZSzjFOlhyjIkdIaNDDQkXFMKVIivt'),('PMiq','WAUsjRFhrNfftXpvdlzqGztYECdhtedCyTLYrLty'),('WOBb','xCIyPAyytwSXXeOvGHunAkyrRHSNePPYXiCrhjEt'),('ZAYP','zlhivxTQfwyFvSemESAEsnYMQVwlnTMuYOKlFTEn');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dept_emp`
--

DROP TABLE IF EXISTS `dept_emp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dept_emp` (
  `emp_no` int(11) NOT NULL,
  `dept_no` char(4) NOT NULL,
  `from_date` date NOT NULL,
  `to_date` date NOT NULL,
  PRIMARY KEY (`emp_no`,`dept_no`),
  KEY `emp_no` (`emp_no`),
  KEY `dept_no` (`dept_no`),
  CONSTRAINT `dept_emp_ibfk_1` FOREIGN KEY (`emp_no`) REFERENCES `employees` (`emp_no`) ON DELETE CASCADE,
  CONSTRAINT `dept_emp_ibfk_2` FOREIGN KEY (`dept_no`) REFERENCES `departments` (`dept_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dept_emp`
--

LOCK TABLES `dept_emp` WRITE;
/*!40000 ALTER TABLE `dept_emp` DISABLE KEYS */;
INSERT INTO `dept_emp` VALUES (6628380,'LKBE','2014-02-19','2014-02-18'),(27073554,'sWMd','2014-02-15','2014-02-14'),(104527126,'GQpj','2014-02-14','2014-02-17'),(171937344,'ZVsH','2014-02-17','2014-02-17'),(264910784,'MtQl','2014-02-16','2014-02-15'),(298598619,'YKDo','2014-02-15','2014-02-18'),(662706111,'VWvu','2014-02-15','2014-02-18'),(818376330,'WwlV','2014-02-14','2014-02-18'),(819551357,'QkbO','2014-02-18','2014-02-15'),(940920466,'FHwh','2014-02-14','2014-02-16'),(959347320,'GahL','2014-02-15','2014-02-15'),(961643216,'oIeT','2014-02-17','2014-02-19'),(963486822,'yzyN','2014-02-14','2014-02-17'),(992010218,'zyQu','2014-02-19','2014-02-15'),(1203775697,'CRQx','2014-02-18','2014-02-15'),(1265768560,'sydD','2014-02-16','2014-02-14'),(1267032372,'XFnK','2014-02-17','2014-02-14'),(1286257613,'Afex','2014-02-15','2014-02-18'),(1553215577,'TSOq','2014-02-17','2014-02-17'),(1638212754,'jtqu','2014-02-14','2014-02-17'),(1644452175,'uLce','2014-02-16','2014-02-14'),(1701621128,'fXwk','2014-02-14','2014-02-16'),(1711627671,'vPwV','2014-02-15','2014-02-14'),(1748908678,'OCsm','2014-02-18','2014-02-14'),(1771430100,'epHe','2014-02-15','2014-02-15'),(1908678418,'esIn','2014-02-18','2014-02-16'),(1947917684,'WGth','2014-02-18','2014-02-18'),(1975854901,'PMiq','2014-02-16','2014-02-14'),(2077016275,'WOBb','2014-02-17','2014-02-19'),(2140565110,'ZAYP','2014-02-14','2014-02-15');
/*!40000 ALTER TABLE `dept_emp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dept_manager`
--

DROP TABLE IF EXISTS `dept_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dept_manager` (
  `dept_no` char(4) NOT NULL,
  `emp_no` int(11) NOT NULL,
  `from_date` date NOT NULL,
  `to_date` date NOT NULL,
  PRIMARY KEY (`emp_no`,`dept_no`),
  KEY `emp_no` (`emp_no`),
  KEY `dept_no` (`dept_no`),
  CONSTRAINT `dept_manager_ibfk_1` FOREIGN KEY (`emp_no`) REFERENCES `employees` (`emp_no`) ON DELETE CASCADE,
  CONSTRAINT `dept_manager_ibfk_2` FOREIGN KEY (`dept_no`) REFERENCES `departments` (`dept_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dept_manager`
--

LOCK TABLES `dept_manager` WRITE;
/*!40000 ALTER TABLE `dept_manager` DISABLE KEYS */;
INSERT INTO `dept_manager` VALUES ('LKBE',6628380,'2014-02-14','2014-02-15'),('sWMd',27073554,'2014-02-15','2014-02-16'),('GQpj',104527126,'2014-02-17','2014-02-19'),('ZVsH',171937344,'2014-02-14','2014-02-19'),('MtQl',264910784,'2014-02-17','2014-02-16'),('YKDo',298598619,'2014-02-17','2014-02-16'),('VWvu',662706111,'2014-02-16','2014-02-17'),('WwlV',818376330,'2014-02-19','2014-02-16'),('QkbO',819551357,'2014-02-17','2014-02-14'),('FHwh',940920466,'2014-02-15','2014-02-14'),('GahL',959347320,'2014-02-17','2014-02-19'),('oIeT',961643216,'2014-02-16','2014-02-14'),('yzyN',963486822,'2014-02-18','2014-02-18'),('zyQu',992010218,'2014-02-17','2014-02-16'),('CRQx',1203775697,'2014-02-18','2014-02-19'),('sydD',1265768560,'2014-02-14','2014-02-15'),('XFnK',1267032372,'2014-02-16','2014-02-19'),('Afex',1286257613,'2014-02-18','2014-02-19'),('TSOq',1553215577,'2014-02-16','2014-02-19'),('jtqu',1638212754,'2014-02-17','2014-02-14'),('uLce',1644452175,'2014-02-17','2014-02-15'),('fXwk',1701621128,'2014-02-17','2014-02-18'),('vPwV',1711627671,'2014-02-14','2014-02-15'),('OCsm',1748908678,'2014-02-17','2014-02-17'),('epHe',1771430100,'2014-02-15','2014-02-19'),('esIn',1908678418,'2014-02-18','2014-02-16'),('WGth',1947917684,'2014-02-19','2014-02-17'),('PMiq',1975854901,'2014-02-14','2014-02-15'),('WOBb',2077016275,'2014-02-19','2014-02-14'),('ZAYP',2140565110,'2014-02-15','2014-02-19');
/*!40000 ALTER TABLE `dept_manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employees` (
  `emp_no` int(11) NOT NULL,
  `birth_date` date NOT NULL,
  `first_name` varchar(14) NOT NULL,
  `last_name` varchar(16) NOT NULL,
  `gender` enum('M','F') NOT NULL,
  `hire_date` date NOT NULL,
  PRIMARY KEY (`emp_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (6628380,'2014-02-17','tVcfpIxRxGytAK','fcHQcQAbUxwUJeKT','F','2014-02-16'),(27073554,'2014-02-14','toJhNdhIVXhFOH','bSAaxFJeSOONVLvu','F','2014-02-18'),(104527126,'2014-02-15','RzPMpFCGdUpYbK','eWdaeUBisujTXiBg','M','2014-02-14'),(171937344,'2014-02-14','ECLrhaYWqRjhYe','LpyIKaqiLkeAIGBo','M','2014-02-17'),(264910784,'2014-02-17','YLEJDtGSHewjPZ','KBaNqYxZVjzfMkUa','F','2014-02-19'),(298598619,'2014-02-15','HIGXFAeqRWanTO','qgjJEXGJmgrzxSpF','F','2014-02-18'),(662706111,'2014-02-16','jBfbNBtiThRjZH','kvlbPLEGpARvzTvn','M','2014-02-17'),(818376330,'2014-02-19','UumsWAfjnZNBfm','PVZehXLDYKutIdwW','F','2014-02-14'),(819551357,'2014-02-19','YGQbnBHXmrNqfD','ioZpvUzFKFOANmmE','M','2014-02-16'),(940920466,'2014-02-14','vjRWkChDnOZUAM','zjeVAidVHKNiIKWg','F','2014-02-17'),(959347320,'2014-02-15','RWHvPhkyYoBRWN','ChIsKjdFwnBRStOD','F','2014-02-17'),(961643216,'2014-02-14','eNRLSkzkkuNSwk','dwKRCFzkxuOWAEBG','F','2014-02-16'),(963486822,'2014-02-19','bmhbMuPoILtDhY','gTghOvRPzwlnrpLr','F','2014-02-18'),(992010218,'2014-02-14','cjDzOAIOivxAEM','MBIrtLlhMKiJmdHc','M','2014-02-15'),(1203775697,'2014-02-19','KQoQBYazlhyoYP','hRYzFwabhlfYUJfF','F','2014-02-18'),(1265768560,'2014-02-14','RsazaRhvVEcidh','jiHxiKBEmUMTQdsC','F','2014-02-15'),(1267032372,'2014-02-14','zCkmGXumsOyZyb','YTWlAXDfCwEwnLWy','F','2014-02-15'),(1286257613,'2014-02-19','RpYtCvpOZhdkLQ','tHeUJmnGhQeXKTAY','M','2014-02-17'),(1553215577,'2014-02-18','ftkwyfIaTnbSOn','roQDswcuMasuQiZv','F','2014-02-18'),(1638212754,'2014-02-18','pyCSBadNwCApMk','facNVYFwyOnFRDLY','M','2014-02-14'),(1644452175,'2014-02-15','CoYwUwYGyeHqlk','VJhXWhtRyNmjyctY','M','2014-02-16'),(1701621128,'2014-02-15','CAgrHEYnYBKXvb','vVEhvcPnLNeSgbvA','F','2014-02-14'),(1711627671,'2014-02-19','IClwBKauZYheBA','SGNRhVXWbovKcvEI','M','2014-02-16'),(1748908678,'2014-02-15','dCGFVNUttFwANk','MCYUkuIVxpibjolW','F','2014-02-17'),(1771430100,'2014-02-14','eWutoafzsyGxJF','bOYnICFzpLELyUKG','F','2014-02-14'),(1908678418,'2014-02-17','mWkNtHiyviteAD','ChhoVXVhhxlDiIKN','F','2014-02-19'),(1947917684,'2014-02-18','nySdilGHhOVFRU','ayzluqMZXlIMCjdN','F','2014-02-16'),(1975854901,'2014-02-16','YvjRuDGlISqBwR','WPjIVQEfJqcnrMzV','M','2014-02-17'),(2077016275,'2014-02-17','TaoUOJGTlIBytf','KAekagfPWGpxQOgH','M','2014-02-16'),(2140565110,'2014-02-17','FQJXIFYfJGyMhD','IZpCcEfwtxlEYAoB','F','2014-02-14');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salaries`
--

DROP TABLE IF EXISTS `salaries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `salaries` (
  `emp_no` int(11) NOT NULL,
  `salary` int(11) NOT NULL,
  `from_date` date NOT NULL,
  `to_date` date NOT NULL,
  PRIMARY KEY (`emp_no`,`from_date`),
  KEY `emp_no` (`emp_no`),
  CONSTRAINT `salaries_ibfk_1` FOREIGN KEY (`emp_no`) REFERENCES `employees` (`emp_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salaries`
--

LOCK TABLES `salaries` WRITE;
/*!40000 ALTER TABLE `salaries` DISABLE KEYS */;
INSERT INTO `salaries` VALUES (6628380,2146652728,'2014-02-16','2014-02-17'),(27073554,875905560,'2014-02-14','2014-02-19'),(104527126,238150755,'2014-02-18','2014-02-16'),(171937344,2049787755,'2014-02-16','2014-02-14'),(264910784,1889310178,'2014-02-18','2014-02-14'),(298598619,60988818,'2014-02-14','2014-02-19'),(662706111,1369799089,'2014-02-19','2014-02-19'),(818376330,1448043632,'2014-02-18','2014-02-15'),(819551357,1576860153,'2014-02-17','2014-02-14'),(940920466,389906306,'2014-02-16','2014-02-18'),(959347320,1582618026,'2014-02-17','2014-02-19'),(961643216,127849579,'2014-02-15','2014-02-16'),(963486822,518528741,'2014-02-19','2014-02-16'),(992010218,1855875180,'2014-02-15','2014-02-16'),(1203775697,1850253076,'2014-02-17','2014-02-15'),(1265768560,1353329118,'2014-02-17','2014-02-17'),(1267032372,42723897,'2014-02-19','2014-02-16'),(1286257613,2116242142,'2014-02-18','2014-02-16'),(1553215577,520056990,'2014-02-16','2014-02-16'),(1638212754,1518090116,'2014-02-18','2014-02-19'),(1644452175,2065329305,'2014-02-19','2014-02-15'),(1701621128,1569631254,'2014-02-18','2014-02-15'),(1711627671,1406683612,'2014-02-19','2014-02-16'),(1748908678,803653318,'2014-02-17','2014-02-14'),(1771430100,1234959430,'2014-02-17','2014-02-18'),(1908678418,1468853440,'2014-02-19','2014-02-16'),(1947917684,1611191776,'2014-02-18','2014-02-16'),(1975854901,12911430,'2014-02-19','2014-02-17'),(2077016275,343158344,'2014-02-14','2014-02-14'),(2140565110,384179164,'2014-02-18','2014-02-18');
/*!40000 ALTER TABLE `salaries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `titles`
--

DROP TABLE IF EXISTS `titles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `titles` (
  `emp_no` int(11) NOT NULL,
  `title` varchar(50) NOT NULL,
  `from_date` date NOT NULL,
  `to_date` date DEFAULT NULL,
  PRIMARY KEY (`emp_no`,`title`,`from_date`),
  KEY `emp_no` (`emp_no`),
  CONSTRAINT `titles_ibfk_1` FOREIGN KEY (`emp_no`) REFERENCES `employees` (`emp_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `titles`
--

LOCK TABLES `titles` WRITE;
/*!40000 ALTER TABLE `titles` DISABLE KEYS */;
INSERT INTO `titles` VALUES (6628380,'fZOMZpeDRHInaXeuHINFFXqgXeMuvcUYLMJqnHkqDfkXaxbhfe','2014-02-19','2014-02-14'),(27073554,'pWXEBvLJMceKcUXgXXiYufZNZckCUVTcffKGdZSfMdaMtyNpKA','2014-02-16','2014-02-14'),(104527126,'BGExPviJKeSBxzHMBcAGLDoolnDUSZFQWEtGcxsnWxIvWSmhHt','2014-02-15','2014-02-15'),(171937344,'DpJMZCixrjRnxjMyCmbIrrrjCNaHTWPFjduBhPntiyCcrrxSCl','2014-02-19','2014-02-18'),(264910784,'WTOXtNkpgbdrcWjGsLupRMgQiICqmcamyLGzBINgvOBzfDBQpN','2014-02-16','2014-02-14'),(298598619,'sVnZDWNhuNwAJxKJyQUXVwyADncJFSLnLpwIHaoraxHGwwlTyX','2014-02-19','2014-02-18'),(662706111,'bpIXioAgVEwnSDGFcwMVUTAkHVHpuVCLJYdZefkPzfKEWMSOvf','2014-02-15','2014-02-14'),(818376330,'TJueahleapIroqFsCrLWWKscjfakGpipYGlMPkAYwUiqJVUUYE','2014-02-16','2014-02-17'),(819551357,'aSFJGgkffTIVNiuVriuZFJQFrDsOONECyMZmQIOuPypzghpNXy','2014-02-18','2014-02-17'),(940920466,'hVzTuRBuBJYCadBNgyZFbINvhTLqKSfjRrQwLLSPaQtWmqfwVn','2014-02-18','2014-02-15'),(959347320,'GObSUeorAAcLwKWmtWAGZAbZrkvzoescvtljsvMlmRKdMlYvfT','2014-02-19','2014-02-15'),(961643216,'IttonihccUbIdwtUOyjhwivLNbVPzomkAZowbJZKmuSBLlLVnG','2014-02-16','2014-02-17'),(963486822,'JBjZIVKdfxJkoCRxELvsuRaMnFPioILKHQRnkeuqsIWnCsHNUo','2014-02-15','2014-02-16'),(992010218,'YmhMSkOSRHMAEMlchNNletMjmkcmKDrbjLkjoPaGBPwcOFPXFq','2014-02-18','2014-02-19'),(1203775697,'fXSzyIRbfHzpKyMTardsWIVtGFzNJOANCJGJzJdLBfVThiglki','2014-02-14','2014-02-17'),(1265768560,'GwmntmuVJAsaTmIzobRksMasrLGNSkcjjlJgCjfEosRLcSxoQW','2014-02-19','2014-02-17'),(1267032372,'ItcYQjyBPyCwSKrDItdbvBkSprmCjaivsMoMARCjBWnISSdwPZ','2014-02-19','2014-02-14'),(1286257613,'dxFbyyYuRnogrJqiUvLcyTKZYFuHOIeHHqHGQYJJjBVitkCjNR','2014-02-17','2014-02-17'),(1553215577,'kxNPdZQziVecjKXuQFLBfQQQTKiINtVElAzFINRYlLVfAGGlkg','2014-02-17','2014-02-16'),(1638212754,'DXEHqLQjmTCVMjhbdfxccroREWdHoXzJrdDBaFmavexGDeQDsA','2014-02-17','2014-02-15'),(1644452175,'SVQOjtUpnZgWRYDfXqvEEhbyiilJtDOxxbqVakeSHYuVpSIXec','2014-02-18','2014-02-19'),(1701621128,'DHDXFvAEEupMAPcfeXewsBRRfwdXApHqXFKKcKDsPlGEpIroby','2014-02-14','2014-02-17'),(1711627671,'AayYYPndjSvoVDhcjIXADXQSfPDxJqqUPmKtDwlRLzscgBUqqG','2014-02-18','2014-02-15'),(1748908678,'ySTOqxlwKufxJhgoVKBWUZBYnVcKqfXjsyBpjNLxFefQzTuRWi','2014-02-16','2014-02-15'),(1771430100,'TfCjTVXWUmmiypCMBLLNoAznQEeRXbIqkAxLNLNfNaLrvdRGLx','2014-02-19','2014-02-15'),(1908678418,'VhgrcNMKXgSsmuvJHtfUqBVyWXUuFSomgNIgUgjlrlbQqghmjV','2014-02-14','2014-02-14'),(1947917684,'WaIHDBxBHFicZncjtofNCTaZpomWQmKtfVcJtCuCVzxDRUhQsz','2014-02-17','2014-02-14'),(1975854901,'XJlBNrVhzKhjotCCPFykTIRMHIeudDpVVxlExHoLDdMrzoiHVm','2014-02-18','2014-02-16'),(2077016275,'ysWaMcpWEZGHZvmFpZMtrceJJAAhhfjwBCEhItSnLSVjzGlEWv','2014-02-14','2014-02-14'),(2140565110,'MVlkJDVwWoMIxwmVRerPRxCxAiNtkJezGNiJCDPZgyQZvBJiDE','2014-02-15','2014-02-18');
/*!40000 ALTER TABLE `titles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-14 23:32:42
