---
-- #%L
-- ************************************************************************
-- ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
-- PROJECT       :  UEPS - Uebungs-Programm fuer SQL
-- FILENAME      :  test_script_01.sql
-- ************************************************************************
-- %%
-- Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: dev_ex2
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
-- Table structure for table `Master`
--

DROP TABLE IF EXISTS `Master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Master` (
  `Source_ID` varchar(10) NOT NULL COMMENT 'RECNO from FDT; ID from GoU Devt/Donor Devt',
  `Source_Table` varchar(40) NOT NULL COMMENT 'Name of original table',
  `GOU Vote` varchar(7) DEFAULT NULL COMMENT 'GoU Budget Vote Code',
  `Vote Name` varchar(67) DEFAULT NULL COMMENT 'GoU Budget Vote Name (where known)',
  `Agency Name` varchar(66) NOT NULL COMMENT 'Relevant Agency, for FDT data',
  `Agency Details (where relevant)` varchar(247) NOT NULL COMMENT 'Sub-unit of Agency',
  `Project Code` varchar(15) NOT NULL COMMENT 'GoU Project Code',
  `Project Name` varchar(250) NOT NULL COMMENT 'GoU Project Name',
  `Programme Code` varchar(2) DEFAULT NULL COMMENT 'Recurrent spending Programme Code',
  `Programme Name` varchar(62) DEFAULT NULL COMMENT 'Recurrent Spending Programme Name',
  `Aid_Programme_Code` varchar(6) NOT NULL COMMENT 'From AGMTCODE (FDT)',
  `Aid_Programme_Name` varchar(192) NOT NULL COMMENT 'From Programme-AGMTTITLE',
  `DONOR COA CODE` varchar(3) NOT NULL COMMENT 'Uganda Chart of Account Donor Code (FDT data only)',
  `Donor name` varchar(50) NOT NULL COMMENT 'Name of donor (FDT)',
  `Donorref` varchar(50) NOT NULL COMMENT 'Donor''s project reference no (FDT)',
  `MTEF Sector` varchar(33) DEFAULT NULL COMMENT 'Medium-Term Expenditure Framework Sector',
  `MTEF Reference` varchar(66) DEFAULT NULL COMMENT 'Medium-Term Expenditure Framework Reference (Except FDT)',
  `Sector strategy or policy` varchar(101) NOT NULL COMMENT '(FDT only)',
  `Details of sector policy or strategy` varchar(255) NOT NULL COMMENT '(FDT only)',
  `Aid Modality` varchar(35) NOT NULL COMMENT 'Project or Budget Support (FDT only)',
  `Type of Project-Basket` varchar(59) NOT NULL COMMENT 'Type of Project (FDT only)',
  `GRANT-LOAN` varchar(5) NOT NULL COMMENT 'Grant or Loan (FDT only)',
  `On-Off Budget` varchar(10) NOT NULL COMMENT 'On/off budget (FDT only)',
  `PAF` varchar(10) DEFAULT NULL COMMENT 'Poverty Alleviation Framework (hardly exists anywhere)',
  `SWG` varchar(45) DEFAULT NULL COMMENT 'Sector Working Group (COFOG-ish classification)',
  `Sector Objective` varchar(68) DEFAULT NULL COMMENT 'Sector Objective nests perfectly under SWG',
  `PEAP Pillar` varchar(50) DEFAULT NULL COMMENT 'Poverty Eradication Action Plan (classification is now deprecated)',
  `PEAP Objective` varchar(104) DEFAULT NULL COMMENT 'Poverty Eradication Action Plan (classification is now deprecated)',
  `PEAP Area` varchar(56) DEFAULT NULL COMMENT 'Poverty Eradication Action Plan (classification is now deprecated)',
  `DP` varchar(1) NOT NULL COMMENT 'DP and GOU identified if the project is Donor or GOU funded or both, but this has not been updated for FDT data and looking at amounts (Outturn) is therefore much more accurate',
  `GOU` varchar(1) NOT NULL COMMENT 'DP and GOU identified if the project is Donor or GOU funded or both, but this has not been updated for FDT data and looking at amounts (Outturn) is therefore much more accurate',
  `Outturn` varchar(20) DEFAULT NULL COMMENT 'Amount disbursed in this year in Billions of Ugandan Shillings. Calculated for FDT data using the following conversion rates (based on Donor Development ratios for each year): 2003/4: 1 USD = 1.847 UGX; 2004/5: 1 USD = 1.804 UGX; 2005/6: 1 USD = 1.779 UGX',
  `Planned` varchar(20) DEFAULT NULL COMMENT 'Amount planned in this year in Billions of Ugandan Shillings',
  `Outturn_Dollars` varchar(20) NOT NULL COMMENT 'Amount disbursed in this year in Millions of US Dollars. Not calculated for all data.',
  `Planned_Dollars` varchar(20) NOT NULL COMMENT 'Amount planned in this year in Millions of US Dollars. Not calculated for all data.',
  `Outturn_Donor` varchar(20) NOT NULL COMMENT 'Amount disbursed this year in the donor''s currency (only for FDT data)',
  `Planned_Donor` varchar(20) NOT NULL COMMENT 'Amount planned for this year in the donor currency (only for FDT data)',
  `donorcurrency` varchar(26) NOT NULL COMMENT 'Donor''s currency',
  `orderofmagnitude` varchar(7) NOT NULL COMMENT 'Order of magnitude of donor''s currency. Multiply Outturn_Donor by this to get the actual amount in the donor''s currency',
  `Year` varchar(10) DEFAULT NULL COMMENT 'The financial year that this money was disbursed or planned for',
  `SIGNDATE` varchar(10) NOT NULL COMMENT 'Only for FDT',
  `START DATE` varchar(10) NOT NULL COMMENT 'Only for FDT',
  `CLOSE DATE` varchar(8) NOT NULL COMMENT 'Only for FDT',
  `Financecomment` varchar(250) NOT NULL COMMENT 'Largely financial comments about the project; only for FDT',
  `Duplicate` int(1) NOT NULL COMMENT 'A 1 in this column marks this project for this year as a duplicate. This occurs where the project existed in both the Donor_Development data and the FDT data. Only Donor_Development data has been marked, so it can easily be removed, and should be removed '
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Master`
--

LOCK TABLES `Master` WRITE;
/*!40000 ALTER TABLE `Master` DISABLE KEYS */;
INSERT INTO `Master` VALUES ('','Wage_Recurrent','1','President\'s Office','','','','','1','Finance and Administration','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','NON PAF','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.1 Strengthened Political Governance','4.1a Democracy,  Political Parties, and Civic Education','','','','','','','','','','','2003','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','1','Finance and Administration','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','NON PAF','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.1 Strengthened Political Governance','4.1a Democracy,  Political Parties, and Civic Education','','','4.217486','','','','','','','','2004','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','1','Finance and Administration','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','NON PAF','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.1 Strengthened Political Governance','4.1a Democracy,  Political Parties, and Civic Education','','','','','','','','','','','2005','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','1','Finance and Administration','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','NON PAF','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.1 Strengthened Political Governance','4.1a Democracy,  Political Parties, and Civic Education','','','','4.851758','','','','','','','2006','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','2','Ethics and Good Governance','','','','','','Accountability','001 Ethics and Integrity (Office of the President)','','','','','','','PAF','Accountability','Increased Integrity and reduced Corruption','4. Good Governance','4.4 Strengthened Public Sector Management & Accountability','4.4d Anti-Corruption','','','','','','','','','','','2003','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','2','Ethics and Good Governance','','','','','','Accountability','001 Ethics and Integrity (Office of the President)','','','','','','','PAF','Accountability','Increased Integrity and reduced Corruption','4. Good Governance','4.4 Strengthened Public Sector Management & Accountability','4.4d Anti-Corruption','','','0.11','','','','','','','','2004','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','2','Ethics and Good Governance','','','','','','Accountability','001 Ethics and Integrity (Office of the President)','','','','','','','PAF','Accountability','Increased Integrity and reduced Corruption','4. Good Governance','4.4 Strengthened Public Sector Management & Accountability','4.4d Anti-Corruption','','','0.11','','','','','','','','2005','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','2','Ethics and Good Governance','','','','','','Accountability','001 Ethics and Integrity (Office of the President)','','','','','','','PAF','Accountability','Increased Integrity and reduced Corruption','4. Good Governance','4.4 Strengthened Public Sector Management & Accountability','4.4d Anti-Corruption','','','','0.11','','','','','','','2006','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','3','Department of Monitoring and Evaluation','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','','','','','','','','','2003','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','3','Department of Monitoring and Evaluation','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','0.038483','','','','','','','','2004','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','3','Department of Monitoring and Evaluation','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','0.038483','','','','','','','','2005','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','3','Department of Monitoring and Evaluation','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','','0.038483','','','','','','','2006','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','4','Department of Monitoring and Inspection','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','','','','','','','','','2003','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','4','Department of Monitoring and Inspection','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','0.039599','','','','','','','','2004','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','4','Department of Monitoring and Inspection','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','0.039599','','','','','','','','2005','','','','',0),('','Wage_Recurrent','1','President\'s Office','','','','','4','Department of Monitoring and Inspection','','','','','','Public Administration','001 Office of the President (excluding Ethics & Integrity)','','','','','','','','Public Administration','A strong Presidency and Cabinet','4. Good Governance','4.0 Support to all or multiple objectives under Pillar 4','Support to all or multiple objectives under Pillar','','','','0.039599','','','','','','','2006','','','','',0);
/*!40000 ALTER TABLE `Master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-11-10 17:06:54
