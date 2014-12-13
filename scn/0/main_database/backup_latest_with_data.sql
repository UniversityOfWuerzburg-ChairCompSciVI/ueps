-- MySQL dump 10.13  Distrib 5.5.33, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: admin_db
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
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise`
--

LOCK TABLES `exercise` WRITE;
/*!40000 ALTER TABLE `exercise` DISABLE KEYS */;
INSERT INTO `exercise` VALUES (1,NULL,1,'Suchen Sie alle Titel jener Bücher deren Autor George Orwell ist. Ordnen Sie die Titel nach Erscheinungsjahr abwärts.','Select',2,'2014-02-14 23:25:37'),(2,NULL,1,'Bestimmen Sie den Titel des Buches mit der Signatur \"PF/286-A/3\" (ohne Anführungszeichen).','Select',1,'2014-02-14 23:25:37'),(3,NULL,1,'Zeigen Sie sämtliche Datensätze der Bücher-Tabelle an und ordnen Sie diese nach Erscheinungsjahr abwärts.','Select',1,'2014-02-14 23:25:37'),(4,NULL,5,'Zeigen Sie Titel, Autor und Preis aller Bücher, die im Carlsen-Verlag erschienen sind an. Hinweis: Die Zuordnung von Verlag-Id zu Verlag-Name befindet sich in der Tabelle \"publishers\".','Select',3,'2014-02-14 23:25:37'),(5,NULL,1,'Wählen Sie Titel und Preis aller Bücher aus, deren Preis unter 10€ liegt. Ordnen Sie die Einträge zusätzlich preislich aufwärts.','Select',2,'2014-02-14 23:25:37'),(6,NULL,1,'Bestimmen Sie die Signatur des Buches \"Shades of Grey - Geheimes Verlangen\" (ohne Anführungszeichen).','Select',1,'2014-02-14 23:25:37'),(7,NULL,1,'Zeigen Sie Titel, Verlag-Name und Lagerbestand sämtlicher Bücher an deren Lagerbestand geringer als 40000 ist. Hinweis: Die Zuordnung von Verlag-Id zu Verlag-Name befindet sich in der Tabelle \"publishers\".','Select',3,'2014-02-14 23:25:37'),(8,NULL,1,'Zeigen sie Titel und Autor der drei teuersten Bücher an. Sortieren Sie die Einträge nach Preis abwärts.','Select',2,'2014-02-14 23:25:37'),(9,NULL,1,'Bestimmen Sie Titel und Signatur sämtlicher Bücher des Autorin \"J. K. Rowling\" (ohne Anführungszeichen). ','Select',1,'2014-02-14 23:25:37'),(10,NULL,1,'Bestimmen Sie den Autor des Buches \"Der kleine Prinz\" (ohne Anführungszeichen).','Select',1,'2014-02-14 23:25:37'),(11,NULL,5,'Bestimmen Sie Titel und Preis aller Bücher des Autors \"J. R. R. Tolkien\" (ohne Anführungszeichen), deren Preis unter 10€ liegt. Ordnen Sie die Einträge nach Erscheinungsjahr abwärts.','Select',2,'2014-02-14 23:25:37'),(12,NULL,1,'Zeigen Sie Titel, Autor und Peis aller Bücher an. Ordnen Sie die Einträge zusätzlich preislich aufwärts.','Select',1,'2014-02-14 23:25:37'),(13,NULL,1,'Zeigen Sie sätmliche Autoren an deren Bücher über den Carlsen-Verlag publiziert wurden. Hinweis: Die Zuordnung von Verlag-Id zu Verlag-Name befindet sich in der Tabelle \"publishers\".','Select',3,'2014-02-14 23:25:37'),(14,NULL,1,'Erfassen Sie alle Autoren und den kumulierten Bestand ihrer Bücher. Ordnen Sie die Einträge nach kumulierten Bestand abwärts. Benennen Sie die Spalte des  kumulierten Bestandes \"stock_sum\" (ohne Anführungszeichen). Hinweise: Verwenden Sie den Sum-Operator und das Schlüsselwort AS.','Select',3,'2014-02-14 23:25:37'),(15,NULL,1,'Bestimmen Sie den potentiellen Umsatz des Buches \"Animal Farm\" (ohne Anführungszeichen). Hinweis: Als potentieller Umsatz wird hier das Produkt von Bestand und Preis bezeichnet. Nennen Sie diese Spalte \"revenue\" (ohne Anführungszeichen).','Select',2,'2014-02-14 23:25:37'),(16,NULL,1,'Bestimmen Sie den Preis des Buches mit der Signatur \"PF/510-S/19\" (ohne Anführungszeichen).','Select',1,'2014-02-14 23:25:37'),(17,NULL,1,'Wählen Sie alle Kunden aus, die im Februar Geburtstag haben und geben Sie den Vornamen, Nachnamen und das Geburtsdatum aus. Hinweis: Verwenden Sie für die Eingrenzung des Geburtsdatums den \"LIKE\"-Operator.','Select',2,'2014-02-14 23:25:37'),(18,NULL,5,'Geben Sie alle Email-Adressen der Kunden aus, die mit \"gmx.de\" enden. Hinweis: Verwenden Sie für die Eingrenzung der Mailadresse den \"LIKE\"-Operator.','Select',2,'2014-02-14 23:25:37'),(19,NULL,1,'Geben Sie alle Kunden aus, die vor dem Jahr 2000 geboren sind.','Select',2,'2014-02-14 23:25:37'),(20,NULL,1,'Geben Sie den Vornamen und Nachnamen des jüngsten Kunden aus. Achten Sie darauf, dass sich die Spaltennamen nicht verändern.','Select',1,'2014-02-14 23:25:37'),(21,NULL,1,'Sie möchten ihre Kunden dazu motivieren sichere Passwörter zu verwenden. Alle Kunden, die als Kennwort \"Passwort\" verwenden, sollen beim nächsten Login über eine Meldung aufgefordert werden sich was neues auszudenken.<br><br>Die Passwörter der Kunden sind als MD5-Hash-String in der Tabelle \"customers\" gespeichert. Der MD5-Hashstring zu \"Passwort\" lautet \"3e45af4ca27ea2b03fc6183af40ea112\". Geben Sie die ID und die Email-Adresse der faulen Kunden aus.','Select',1,'2014-02-14 23:25:37'),(22,NULL,1,'Berechnen Sie das Alter der jeweiligen Kunden. Die Ergebnistabelle soll zu jedem Kunden den Geburtstag und das Alter (in einer neuen Spalte \"age\") beinhalten. Hinweis: Verwenden Sie zum Abrunden den FLOOR-Operator.','Select',3,'2014-02-14 23:25:37'),(23,NULL,1,'Finden Sie alle Email-Adressen, die mehrfach in der Tabelle \"customers\" vorkommen. Geben Sie zusätzlich die Häufigkeit in einer neuen Spalte mit dem Namen \"occurences\" an.','Select',3,'2014-02-14 23:25:37'),(24,NULL,1,'Wählen Sie alle Einträge zu Kunden aus, die im Jahr 1981 Geburtstag haben. Hinweis: Verwenden Sie hierfür den \"LIKE\"-Operator','Select',1,'2014-02-14 23:25:37'),(25,NULL,5,'Wählen Sie alle Kunden aus, deren Nachname nur aus vier Buchstaben besteht. Geben Sie nur den Vornamen und Nachnamen aus. Die Datensätze sollen nach dem Nachnamen alphabetisch sortiert werden.','Select',2,'2014-02-14 23:25:37'),(26,NULL,1,'Sie möchten zu jeder Bestellung (aus der Tabelle \"orders\") den passenden Kunden herausfinden. Geben Sie hierfür das Bestelldatum, die Bestell-ID (Spaltenname: \"order_id\"), den Nachnamen des Kunden und seine zugehörige Kunden-ID (Spaltenname: \"customer_id\") aus. Ordnen Sie die resultierenden Datensätze absteigend nach dem Bestelldatum.','Select',3,'2014-02-14 23:25:37'),(27,NULL,1,'Finden Sie alle Kunden, die ihre Bestellung noch nicht bezahlt haben. Geben sie die Bestell-ID (neuer Spaltenname: \"order_id\"), den Nachnamen des Kunden und seine zugehörige Email-Adresse aus.','Select',2,'2014-02-14 23:25:37'),(28,NULL,1,'Geben Sie die Kunden aus, die mehr als eine Bestellung getätigt haben. Geben sie die Kunden-ID, den Nachnamen und die Anzahl seiner Bestellungen (neuer Spaltenname: \"quantity\") aus.','Select',3,'2014-02-14 23:25:37'),(29,NULL,1,'Geben Sie alle Email-Adressen der Kunden aus, die mit \"gmx.de\" oder mit \"gmail.com\" enden.','Select',2,'2014-02-14 23:25:37'),(30,NULL,1,'Wählen Sie alle Bestellungen aus, die das Buch \"Harry Potter und der Halbblutprinz\" enthalten. Geben Sie hierfür die Bestell-ID (Tabelle: \"orders\") und die Bestellmenge (Tabelle: \"order_positions\") aus.','Select',3,'2014-02-14 23:25:37'),(31,NULL,1,'Berechnen Sie für jede Bestellung den Gesamtpreis, den der Kunde zahlen muss. Geben Sie nur die Bestell-ID und den Gesamtpreis (neuer Spaltenname \"sum\") aus. Beachten Sie, dass die Kunden mehrere Exemplare eines Buches bestellen können (Spalte: \"amount\").','Select',3,'2014-02-14 23:25:37'),(32,NULL,5,'Berechnen Sie die Anzahl der bestellten Bücher, die zum Zeitpunkt des Kaufes weniger als fünf Euro gekostet haben. Geben Sie das Ergebnis unter dem Spaltenname \"sum_books\" aus.','Select',1,'2014-02-14 23:25:37'),(33,NULL,1,'Finden Sie alle Bücher, die zu einem reduziertem Preis erworben wurden. Geben Sie hierfür die ID der Bestellposition (Tabelle: \"order_positions\"), den Buchtitel, den aktuellen Preis (neue Spalte: \"current_price\") und den reduzierten Preis (neue Spalte: \"order_price\") aus.','Select',3,'2014-02-14 23:25:37'),(34,NULL,1,'Listen Sie die Bücher auf, die unter der Bestellnummer: \"9\" zusammengefasst sind. Sortieren Sie das Ergebnis aufsteigend nach dem Erscheinungsjahr.','Select',2,'2014-02-14 23:25:37'),(35,NULL,1,'Geben Sie für jeden Kunden aus, wie oft diese bereits Bestellungen abgegeben haben. Listen Sie dafür den Vornamen, Nachnamen und die Anzahl der Bestellungen (neue Spalte: \"order_count\"). Sortieren Sie die Liste anschließend absteigend nach der errechneten Anzahl.','Select',2,'2014-02-14 23:25:38'),(36,NULL,3,'Fügen Sie ein neues Buch mit dem Titel \"Applied Statistical Genetics with R\" in die Datenbank ein (Autor: \"Andrea S. Foulkes\", Erscheinungsjahr: 2010, Publisher-ID: \"12\" , Signatur \"PF/520-Y/2\", Lagerbestand \"289\", Preis: \"24.99\").','Insert',2,'2014-02-14 23:25:38'),(37,NULL,4,'Löschen Sie alle Bücher, die vom Autor \"J. R. R. Tolkien\" geschrieben wurden.','Delete',1,'2014-02-14 23:25:38'),(38,NULL,4,'Löschen Sie alle Kunden, die nach dem Jahr 1995 geboren wurden\" geschrieben wurden.','Delete',1,'2014-02-14 23:25:38'),(39,NULL,5,'Löschen Sie alle Bücher, die mehr als 8,50 Euro kosten und die Verlags-ID: \"7\" besitzen.','Delete',1,'2014-02-14 23:25:38'),(40,NULL,4,'Löschen Sie alle Kunden, die noch keine Bestellung abgegeben haben.','Delete',1,'2014-02-14 23:25:38'),(41,NULL,2,'Aktualisieren Sie die Email-Adresse von Paulina Rohl. Die neue Adresse soll \"paulina.r@yahoo.com\" lauten.','Update',2,'2014-02-14 23:25:38'),(42,NULL,1,'Berechnen Sie den durchschnittlichen Preis eines bestellten Buches aus der Tabelle \"order_positions\".	Das Ergebnis soll in einer Spalte namens: \"average_price\" angezeigt werden.','Select',2,'2014-02-14 23:25:38'),(43,NULL,1,'Wählen Sie alle Bücher aus, die in den Jahren 1998, 2001 oder 2011 veröffentlicht wurden.','Select',2,'2014-02-14 23:25:38'),(44,NULL,1,'Fügen Sie einen neuen Kunden in die Datenbank ein. Name \"Ferdinandus Merkle\", Email: \"ferdinandus_1856@yahoo.com\", Geburtstag: \"1990-11-24\". Das Passwort und die Adresse soll vorerst leer gelassen werden.','Select',2,'2014-02-14 23:25:38'),(45,NULL,1,'Geben Sie zu jedem Kunden Vorname, Nachname und Anzahl seiner Bewertungen (Spalte rating_count). Die Bewertungen befinden sich in der Tabelle \"ratings\". Ordnen Sie die Einträge nach Anzahl der Bewertungen abwärts.','Select',3,'2014-02-14 23:25:38'),(46,NULL,5,'Ermitteln Sie den Kunden, welcher die höchste durchschnittliche Bewertung abgegeben hat. Geben Sie dazu Vorname, Nachname und seine durchschnittliche Bewertung (Spaltenbezeichnung avg_rating).','Select',3,'2014-02-14 23:25:38'),(47,NULL,1,'Bestimmen Sie jene drei Bücher mit der höchsten durchschnittlichen Bewertung. Geben Sie dabei Buchname und durchschnittliche Bewertung (Spaltenbezeichnung \"avg_rating\") an. Ordnen Sie die Einträge nach durchschnittlicher Bewertung abwärts.','Select',3,'2014-02-14 23:25:38'),(48,NULL,1,'Geben Sie durchschnittliche Bewertung und Anzahl der Bewertungen zum Buch \"Harry Potter und der Orden des Phönix\" (ohne Anführungszeichen) an. Benennen Sie die Spalten \"avg_rating\" und \"rating_count\".','Select',3,'2014-02-14 23:25:38'),(49,NULL,1,'Suchen Sie alle Bewertungen der Kundin \"Christl Seeliger\" an. Die Einträge sollen Buchnamen sowie die Bewertung enthalten. Ordnen Sie die Einträge zusätzlich nach Bewertung abwärts.','Select',3,'2014-02-14 23:25:38'),(50,NULL,1,'Stellen Sie alle Buchtitel, welche auf der Wunschliste von Wilhard Herold befinden dar.','Select',3,'2014-02-14 23:25:38'),(51,NULL,1,'Ermitteln Sie das meist gewünschteste Buch. Geben Sie dabei Buchtitel sowie Anzahl des Auftretens auf den Wunschzetteln (Spaltenbezeichnung wish_count) an.','Select',3,'2014-02-14 23:25:38'),(52,NULL,1,'Ermitteln sie alle Buchtitel jener Büche, welche sich auf keiner Wunschliste befinden.','Select',3,'2014-02-14 23:25:38'),(53,NULL,5,'Ermitteln Sie den Einkaufswert der Wunschlisten für jeden Kunden. Listen Sie dabei Vorname, Nachname und Einkaufswert (Spaltenbezeichnung \"value\") auf. Sortieren Sie die Einträge aufwärts nach Einkaufswert.','Select',3,'2014-02-14 23:25:38'),(54,NULL,1,'Ermitteln Sie Titel und durchschnittliche Bewertung aller bewerteten Bücher. Ordnen Sie die Einträge aufsteigend nach durchschnittlicher Bewertung. Bezeichen Sie die Spalte der durchschnittlichen Bewertung mit \"avg_rating\".','Select',3,'2014-02-14 23:25:38'),(55,NULL,2,'Aktualisieren Sie den Lagerbestand des Buches \"Shades of Grey - Geheimes Verlangen\" auf den neuene Wert \"2042\".','Update',1,'2014-02-14 23:25:38'),
	(62, NULL, 8, '<span><span>Wie heißt der nordkoreanische Fussballverband?</span></span>\r\n        ', NULL, 1, '2014-05-14 02:31:26'),
	(63, NULL, 8, '<span>Wieviele verschiedene Konföderationen gibt es? Bennen Sie die Ergebnisspalte number.</span>', NULL, 1, '2014-05-15 11:17:02'),
	(64, NULL, 13, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"In welcher Konf\\u00f6deration ist Neuseeland?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">In welcher Konföderation ist Neuseeland?</span>', NULL, 1, '2014-05-13 19:10:45'),
	(65, NULL, 8, '<span>Wieviele Mannschaften spielen bei dem Turnier mit? Benennen Sie die Ergebnisspalte number.</span>', NULL, 1, '2014-05-15 11:18:40'),
	(66, NULL, 8, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"Wie kann man den s\\u00fcdafrikanischen Fussballverband erreichen (Adresse, Telefon / Fax, Homepage)?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">Wie kann man den südafrikanischen Fußballverband erreichen (Adresse, Telefon / Fax, Homepage)?</span>', NULL, 1, '2014-05-13 19:12:50'),
	(67, NULL, 8, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"Wie hei\\u00dft der brasilianische Fussballverband und wie ist seine Internetadresse?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">Wie heißt der brasilianische Fußballverband und wie ist seine Internetadresse?</span>', NULL, 1, '2014-05-13 19:13:37'),
	(68, NULL, 13, '<span>Wie viele Mitglieder hatte eine Assoziation durchschnittlich? Benennen Sie die Ergebnisspalte average.</span>', NULL, 1, '2014-05-15 11:20:16'),
	(69, NULL, 8, '<span>Wann wurde der jüngste Verband gegründet? Benennen Sie die Ergebnisspalte max_founded.</span>', NULL, 1, '2014-05-15 11:22:33'),
	(70, NULL, 8, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"Welche Mannschaften (name) geh\\u00f6ren zur Konf\\u00f6deration \\"CAF\\"?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">Welche Mannschaften (Name) gehören zur Konföderation "CAF"?</span>', NULL, 1, '2014-05-13 19:16:16'),
	(71, NULL, 8, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2," in="" welchen="" farben="" spielen="" griechenland="" und="" italien?"]"="" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">In welchen Farben spielen Griechenland und die Elfenbeinküste?</span>', NULL, 1, '2014-05-13 19:18:25'),
	(72, NULL, 8, '<span>Wie viele Torhütertrainer ("Goaly-Coach") fuhren zur WM? Benennen Sie die Ergebnisspalte number.</span>', NULL, 1, '2014-05-15 11:43:05'),
	(73, NULL, 8, '<span>Wieviele Tore fielen insgesamt im Verlaufe des Turniers? Benennen Sie die Ergebnisspalte number.</span>', NULL, 2, '2014-05-15 11:44:27'),
	(74, NULL, 13, '<span>Wie viele Tore fielen durchschnittlich in den Gruppenspielen? Benennen Sie die Ergebnisspalte number.</span>', NULL, 2, '2014-05-15 11:46:06'),
	(75, NULL, 8, '<span>Wo wurde der Spieler Carlos Tévez geboren?</span>', NULL, 1, '2014-05-16 11:00:42'),
	(76, NULL, 13, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"Wie gro\\u00df ist Lionel Messi?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">Wie groß ist Lionel Messi?</span>', NULL, 1, '2014-05-13 19:24:29'),
	(77, NULL, 8, '<span>Wie alt ist die jüngste Person? Benennen Sie die Ergebnisspalte min_age.</span>', NULL, 1, '2014-05-16 11:04:22'),
	(78, NULL, 8, '<span>Wie groß ist eine Person durchschnittlich? Benennen Sie die Ergebnisspalte average.</span>', NULL, 1, '2014-05-15 12:03:52'),
	(79, NULL, 8, '<span>Wie viel hat Südafrika für das Bauen aller Stadien insgesamt ausgegeben? </span>Benennen Sie die Ergebnisspalte sum_costs.', NULL, 1, '2014-05-16 11:06:18'),
	(80, NULL, 8, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"In welchem Jahr wurde das \\"Loftus Versveld\\" - Stadion erbaut?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">In welchem Jahr wurde das "Loftus Versveld" - Stadion erbaut?</span>', NULL, 1, '2014-05-13 19:27:00'),
	(81, NULL, 13, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"Wie hei\\u00dft das Stadion in Bloemfontein?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">Wie heißt das Stadion in Bloemfontein?</span>', NULL, 1, '2014-05-13 19:27:34'),
	(82, NULL, 8, '<span>Wie alt waren die Stadien im Jahr 2010 durchschnittlich? </span><span>Benennen Sie die Ergebnisspalte average.</span><span><br></span>', NULL, 2, '2014-05-15 12:06:36'),
	(83, NULL, 8, '<span style="font-family: arial, sans, sans-serif; text-align: center;" data-sheets-value="[null,2,"In welches Stadion passen 40911 Zuschauer?"]" data-sheets-userformat="[null,null,961,[null,0],null,null,null,null,null,1,1,1,0]">In welches Stadion passen 40911 Zuschauer?</span>', NULL, 1, '2014-05-13 19:28:50'),
	(84, NULL, 8, '<span>Wie viele Mitglieder hat die Assoziation mit den meisten Mitgliedern mehr als die mit den wenigsten? Benennen Sie die Ergebnisspalte difference.</span>', NULL, 2, '2014-05-16 15:13:40'),
	(85, NULL, 8, '<span>Welcher Schiedsrichter leitete das Eröffnungsspiel (id = 1)? </span><span> Hinweis: Das Eröffnungsspiel hat die id 1.</span>', NULL, 2, '2014-05-16 16:55:07'),
	(86, NULL, 8, '<span>Wie viele Personen haben eine zweite Staatsbürgerschaft in Deutschland? Benennen Sie die Ergebnisspalte number.</span>', NULL, 1, '2014-05-15 12:09:16'),
	(90, NULL, 13, '<span>Welche Schiedsrichter pfiffen das Finale?</span>', NULL, 3, '2014-05-14 08:28:41'),
	(91, NULL, 13, '<span>Wie viele Spiele leitete Howard Melton Webb (als Haupt- oder Assistenzschiedsrichter)? Benennen Sie die Ergebnisspalte number.</span>', NULL, 2, '2014-05-19 17:33:41'),
	(92, NULL, 8, '<span>Welche Spiele fanden am 21.6.2010 statt (Ein Datum wird im Format "YYYY-MM-DD HH-..." angegeben)? Geben Sie alle Spalten aus.</span>', NULL, 2, '2014-05-15 12:11:51'),
	(93, NULL, 8, '<span>Welche Personen sind 25 Jahre alt? </span>Geben Sie alle Spalten aus.', NULL, 2, '2014-05-15 12:13:43'),
	(94, NULL, 8, '<span>Welche Personen wurden nicht in Algerien geboren, haben aber einen algerischen (Haupt -) Pass?</span>', NULL, 1, '2014-05-14 08:39:32'),
	(95, NULL, 13, '<span>Geben sie alle Stadien nach den Baukosten aufsteigend geordnet aus.</span>', NULL, 1, '2014-05-14 08:40:33'),
	(96, NULL, 10, '<span>Die Person mit der ID 600 hat eine neue zweite Staatsbürgerschaft in Polen. Fügen sie den Wert in die Tabelle ein.</span>', NULL, 1, '2014-05-14 14:06:32'),
	(97, NULL, 13, '<span>Der nordkoreanische Fußballverband hat eine Homepage bekommen: "www.northkoreanfootball.kp". Fügen sie diese in die Tabelle ein.</span>', NULL, 1, '2014-05-14 14:10:54'),
	(98, NULL, 12, '<span>Neuseeland hat neue blaue Trikots bekommen. Aktualisieren sie die Trikotfarbe.</span>', NULL, 1, '2014-05-14 14:14:23'),
	(99, NULL, 13, '<span>Um Stärke zu demonstrieren, hat Nordkorea alle Bewohner des Landes in den Verband eingegliedert. Ändern sie also die Mitgliederzahl auf 24,76 Millionen.</span>', NULL, 1, '2014-05-14 14:15:10'),
	(100, NULL, 12, '<span>Die Person mit der ID 350 hat eine andere zweite Staatsbürgerschaft. Ändern sie sie auf Algerien.</span>', NULL, 1, '2014-05-14 14:16:00'),
	(101, NULL, 11, 'Zum Spiel um den dritten Platz (match_id = 63) sind die Schiedsrichter nicht erschienen. Löschen sie das Match aus der Tabelle "Match_has_refs".', NULL, 1, '2014-05-14 14:22:33'),
	(102, NULL, 11, 'Die Person mit der ID 3 hat ihren zweiten Pass abgegeben. Löschen sie ihn aus der Tabelle "Dual_citizenship".', NULL, 1, '2014-05-16 17:02:41'),
	(103, NULL, 12, 'Die Personen, die in Paraguay geboren wurden, sind um 2 cm gewachsen. Passen sie die Größe an.', NULL, 1, '2014-05-14 15:11:44'),
	(104, NULL, 13, 'Der Spieler Thomas Enevoldsen ist krank und reist ab. Löschen sie ihn aus der Personentabelle.', NULL, 1, '2014-05-14 15:14:09'),
	(105, NULL, 8, 'Welcher ist Philipp Lahms starker Fuß?', NULL, 1, '2014-05-16 16:53:59'),
	(106, NULL, 8, '<span>In welcher Vorrundengruppe spielte Mexiko?</span>', NULL, 2, '2014-05-14 17:09:38'),
	(107, NULL, 13, '<span>Wieviele cm ist Thomas Müller größer als Javier Mascherano?</span><span>Bennen Sie die Ergebnisspalte difference.</span>', NULL, 3, '2014-05-15 12:21:59'),
	(108, NULL, 8, '<span>Wie schwer war das US - amerikanische Team insgesamt? </span>Benennen Sie die Ergebnisspalte total_weight.', NULL, 2, '2014-05-15 12:24:27'),
	(109, NULL, 13, '<span>Bestimmen sie den Kader (Spielernamen) der USA.</span>', NULL, 2, '2014-05-14 17:19:22'),
	(110, NULL, 13, '<span>Wieviele Linksfüßer (footer = "links") hatte Deutschland dabei?</span>', NULL, 2, '2014-05-16 16:56:08'),
	(111, NULL, 8, '<span>Mit wievielen Spielern fuhr Deutschland zur WM?</span>', NULL, 2, '2014-05-14 17:24:33'),
	(112, NULL, 8, '<span>Wer trainiert die ghanaische Nationalmannschaft?</span>', NULL, 2, '2014-05-14 17:27:14'),
	(113, NULL, 8, '<span>Welche Mannschaft spielt in welcher Vorrundengruppe? </span><span>Geben Sie die Teamnamen und die Gruppennamen aus. </span>', NULL, 3, '2014-05-14 17:29:25'),
	(114, NULL, 8, '<span>Bestimmen sie das Alter des jüngsten Trainers. Benennen Sie die Ergebnisspalte result.</span>', NULL, 2, '2014-05-19 09:43:08'),
	(115, NULL, 13, '<span>Welche Staatsbürgerschaften hat Tommy Smith?</span>', NULL, 2, '2014-05-14 17:31:48'),
	(116, NULL, 8, '<span>Welche zweite Staatsbürgschaft hat Samuel Eto\'o? Hinweis: Schreiben Sie den Namen in Anführungszeichen. (Nicht in einfachen Hochkommas.)</span>', NULL, 2, '2014-05-14 17:37:17'),
	(117, NULL, 8, '<span>Welche Mannschaften müssten bei Spielen gegeneinander die Trikots abstimmen, weil sie die gleichen Farben haben? Geben Sie dabei die Teamnamen der beiden Mannschaften aus und benennen Sie dabei die erste Spalte team1 und die zweite team2.</span>', NULL, 2, '2014-05-17 12:38:50'),
	(118, NULL, 13, '<span>Wie hoch ist das Durchschnittsalter der Mannschaft aus Neuseeland? </span>Benennen Sie die Ergebnisspalte average.', NULL, 2, '2014-05-15 14:04:24'),
	(119, NULL, 13, '<span>Wie viele Mitglieder hat der deutsche Verband mehr als der französische Verband? </span>Benennen Sie die Ergebnisspalte difference.', NULL, 3, '2014-05-16 15:14:50'),
	(120, NULL, 8, '<span>Wie groß ist ein Innenverteidiger durchschnittlich? </span><span>Benennen Sie die Ergebnisspalte average.</span><span><br></span>', NULL, 2, '2014-05-15 14:07:38'),
	(121, NULL, 13, '<span>Wie groß ist der kleinste Torwart? </span>Benennen Sie die Ergebnisspalte min_size.', NULL, 2, '2014-05-15 14:08:35'),
	(122, NULL, 8, '<span>Welche Stürmer hatte das spanische Team dabei? Geben Sie die Namen aus.</span>', NULL, 3, '2014-05-14 18:02:44'),
	(123, NULL, 13, 'Geben Sie eine Liste aller Kapitäne mit deren Marktwert und das Land, für das sie spielen (association.team_name), aus. Sortieren Sie dabei nach dem Marktwert absteigend.', NULL, 3, '2014-05-14 18:19:27'),
	(124, NULL, 11, '<span>Das Stadion "Loftus Versveld" wurde nicht rechtzeitig fertig. Löschen sie es aus der Tabelle.</span>', NULL, 1, '2014-05-14 18:28:05'),
	(125, NULL, 13, '<span>Der Schiedsrichter aus den Seychellen (ID 800) hat seinen Flug verpasst. Löschen sie ihn aus der Referee-Tabelle.</span>', NULL, 1, '2014-05-14 18:30:17'),
	(126, NULL, 13, 'Mbombela wird zum Austragungsort ernannt. Fügen Sie das Stadion in die Tabelle ein.Name: Mbombela-Stadion, Stadt: Mbombela,Kapazität: 46000 Zuschauer, Baukosten 1 Mio Euro, Bahujahr: 2009. (Hinweis: Die ID wird automatisch generiert <span>und muss deshalb nicht explizit eingetragen werden.</span>) ', NULL, 1, '2014-05-16 16:58:49'),
	(127, NULL, 13, '<span>Wolfgang Stark (ID=795) wird beim Eröffnungspiel (match_id=1) als vierter Offizieller (role=assistant) hinzugezogen. Fügen Sie ihn in die match_has_refs-Tabelle ein.</span>', NULL, 1, '2014-05-14 19:33:12'),
	(128, NULL, 10, 'Fügen Sie in die Tabelle Person Chuck Norris ein. Alter: 70, Geburtsort: Ryan, Geburtsstaat: USA, Staatsbürgerschaft: USA, Größe 178 cm, Gewicht: 80 kg. (Hinweis: Die ID wird automatisch generiert und muss deshalb nicht explizit angegeben werden.)', NULL, 1, '2014-05-16 17:00:29'),
	(129, NULL, 13, '<span>Bei welchen Personen ist das Geburtsland und die erste Staatsbürgerschaft (person.citizenship) nicht gleich? Geben Sie den Namen, das Geburtsland und die erste Staatsbürgerschaft aus.</span>', NULL, 1, '2014-05-14 19:52:39'),
	(130, NULL, 13, '<span>Welche verschiedenen Spielerpositionen gibt es? </span>Benennen Sie die Ergebnisspalte positions.', NULL, 1, '2014-05-15 14:09:58');
/*!40000 ALTER TABLE `exercise` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_group`
--

LOCK TABLES `exercise_group` WRITE;
/*!40000 ALTER TABLE `exercise_group` DISABLE KEYS */;
INSERT INTO `exercise_group` VALUES (1,NULL,1,'Select-Aufgaben',NULL,'2014-02-14 23:25:37',NULL,NULL,0),(2,NULL,1,'Update-Aufgaben',NULL,'2014-02-14 23:25:37',NULL,NULL,0),(3,NULL,1,'Insert-Aufgaben',NULL,'2014-02-14 23:25:37',NULL,NULL,0),(4,NULL,1,'Delete-Aufgaben',NULL,'2014-02-14 23:25:37',NULL,NULL,0),(5,NULL,1,'Übungsblatt 4 - Aufgabe 1',NULL,'2014-02-14 23:25:37',NULL,NULL,1),(8, NULL, 2, 'SELECT Aufgaben', NULL, '2014-05-13 18:35:40', NULL, NULL, 0),(10, NULL, 2, 'INSERT Aufgaben', NULL, '2014-05-14 14:09:23', NULL, NULL, 0),(11, NULL, 2, 'DELETE Aufgaben', NULL, '2014-05-14 14:09:37', NULL, NULL, 0),(12, NULL, 2, 'UPDATE Aufgaben', NULL, '2014-05-14 14:10:00', NULL, NULL, 0),(13, NULL, 2, 'Übungsblatt 05', NULL, '2014-05-16 00:00:00', '2014-05-16 06:00:00', '2014-05-26 06:00:00', 1);
/*!40000 ALTER TABLE `exercise_group` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `generated_tag`
--

LOCK TABLES `generated_tag` WRITE;
/*!40000 ALTER TABLE `generated_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `generated_tag` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenario`
--

LOCK TABLES `scenario` WRITE;
/*!40000 ALTER TABLE `scenario` DISABLE KEYS */;
INSERT INTO `scenario` (`id`, `copied_from_id`, `name`, `last_modified`, `start_time`, `end_time`, `description`, `create_script_path`, `image_path`, `db_host`, `db_user`, `db_pass`, `db_port`, `db_name`) VALUES
	(1, NULL, 'Beispiel-Szenario', '2014-05-14 02:26:40', NULL, NULL, '<font size="5"><span style="font-weight: bold;">Amazon Warenhaus</span></font><br><br>Sie fangen einen neuen Job im Amazon Warenhaus an und müssen an verschiedenen Stellen Aushilfsarbeiten leisten. Zu ihren Aufgaben gehört das Auffinden von Büchern, deren Verwaltungund andere Korrekturarbeiten.<br><br>Dies erledigen Sie mit der Datenbanksprache SQL, in welche Ihnen in den folgenden Kapiteln ein näherer Einblick gewährt werden soll. Dazu zählen neben dem Einpflegen neuer Daten in die Datenbank ebenso die Aktualisierung vorhandener Daten sowie das Löschen nicht mehr relevanter Daten.<br>Ebenso werden Sie kennen lernen, wie Sie gewünschte Informationen aus verschiedenen Tabellen selektieren und aufbereiten können.', 'scenario_01.sql', 'er-diagram.png', 'ERROR', 'ERROR', 'ERROR', 'ERROR', 'ERROR'),
	(2, NULL, 'WM 2010', '2014-05-16 20:21:50', '2014-05-15 00:00:00', '2014-05-29 00:00:00', '<font size="5">Fußball Weltmeisterschaft 2010 in Südafrika</font><br><br>Sie bewerben sich um einen Job als Datenanalyst bei dem vom Fußball-Weltverband FIFA offiziell anerkannten Wettbüro "FiddleBet", welches bei der kommenden WM in Brasilien den wettbegeisterten Fußballfans auf der ganzen Welt helfen will, den Sommer finanziell lohnend zu gestalten. Die Aufgabe des Datenanalysten besteht darin, Informationen zu sammeln, um die Wettquoten möglichst "fair" gestalten zu können.<br>Im Rahmen eine Auswahlverfahrens für ein Praktikum erhalten Sie Zugriff auf die Datenbank der letzten WM in Südafrika und sollen dort einige Anfragen stellen und Manipulationen vornehmen. Zeigen Sie Ihr Können im Umgang mit der SQL und steigen Sie so ins goldene Wettgeschäft ein!<br><br><br>', 'wm_2014_05_16-19_18_32.sql', 'wm_2014_05_14-12_00_37.png', 'ERROR', 'ERROR', 'ERROR', 'ERROR', 'ERROR');
UNLOCK TABLES;
/*!40000 ALTER TABLE `scenario` ENABLE KEYS */;


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
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution_query`
--

LOCK TABLES `solution_query` WRITE;
/*!40000 ALTER TABLE `solution_query` DISABLE KEYS */;
INSERT INTO `solution_query` VALUES (1,1,'SELECT title FROM books WHERE author=\'George Orwell\' ORDER By year DESC',NULL,NULL,NULL),(2,2,'SELECT title FROM books WHERE signature=\'PF/286-A/3\'',NULL,NULL,NULL),(3,3,'SELECT * FROM books ORDER BY year DESC',NULL,NULL,NULL),(4,4,'SELECT title, author, price FROM books JOIN publishers ON publisher_id=publishers.id WHERE publishers.name=\"Carlsen\"',NULL,NULL,NULL),(5,5,'SELECT title, price FROM books WHERE price < 10 ORDER BY price ASC',NULL,NULL,NULL),(6,6,'SELECT signature FROM books WHERE title=\'Shades of Grey - Geheimes Verlangen\'',NULL,NULL,NULL),(7,7,'SELECT title, publishers.name, stock FROM books JOIN publishers ON publisher_id=publishers.id WHERE stock < 40000',NULL,NULL,NULL),(8,8,'SELECT title, author FROM books ORDER BY price DESC LIMIT 3',NULL,NULL,NULL),(9,9,'SELECT title, signature FROM books WHERE author=\'J. K. Rowling\'',NULL,NULL,NULL),(10,10,'SELECT author FROM books WHERE title=\'Der kleine Prinz\'',NULL,NULL,NULL),(11,11,'SELECT title, price FROM books WHERE author=\'J. R. R. Tolkien\' AND price<10 ORDER BY year DESC',NULL,NULL,NULL),(12,12,'SELECT title, author, price FROM books ORDER BY price ASC',NULL,NULL,NULL),(13,13,'SELECT author FROM books JOIN publishers ON publisher_id=publishers.id WHERE publishers.name =\'Carlsen\' GROUP BY author',NULL,NULL,NULL),(14,14,'SELECT author, sum(stock) AS stock_sum FROM books GROUP BY author ORDER BY stock_sum DESC',NULL,NULL,NULL),(15,15,'SELECT stock*price AS revenue FROM books WHERE title=\'Animal Farm\'',NULL,NULL,NULL),(16,16,'SELECT price FROM books WHERE signature=\'PF/510-S/19\'',NULL,NULL,NULL),(17,17,'SELECT first_name, family_name, birthday FROM customers WHERE birthday LIKE \'%-02-%\'',NULL,NULL,NULL),(18,18,'SELECT email FROM customers WHERE email LIKE \'%gmx.de\'',NULL,NULL,NULL),(19,19,'SELECT * FROM customers WHERE birthday < \'2000-01-01\'',NULL,NULL,NULL),(20,20,'SELECT first_name, family_name, birthday FROM customers ORDER BY birthday DESC LIMIT 1',NULL,NULL,NULL),(21,21,'SELECT id, email FROM customers WHERE password = \'3e45af4ca27ea2b03fc6183af40ea112\'',NULL,NULL,NULL),(22,22,'SELECT birthday, FLOOR(DATEDIFF(NOW(), birthday)/365) AS age FROM customers',NULL,NULL,NULL),(23,23,'SELECT email, COUNT(email) AS occurrences FROM customers GROUP BY email HAVING ( COUNT(email) > 1 )',NULL,NULL,NULL),(24,24,'SELECT * FROM customers WHERE birthday LIKE \'1981-%\'',NULL,NULL,NULL),(25,25,'SELECT first_name, family_name FROM customers WHERE LENGTH(family_name) = 4 ORDER BY family_name ASC',NULL,NULL,NULL),(26,26,'SELECT date, orders.id AS order_id, family_name, customers.id AS customer_id FROM customers JOIN orders ON customers.id = customer_id ORDER BY date DESC',NULL,NULL,NULL),(27,27,'SELECT orders.id AS order_id, family_name, email FROM customers JOIN orders ON customers.id = customer_id WHERE paid=0',NULL,NULL,NULL),(28,28,'SELECT customers.id, family_name, COUNT(customer_id) AS quantity FROM customers JOIN orders ON customers.id = customer_id GROUP BY customer_id HAVING ( COUNT(customer_id) > 1 )',NULL,NULL,NULL),(29,29,'SELECT email FROM customers WHERE email LIKE \'%gmx.de\' OR  email LIKE \'%gmail.com\'',NULL,NULL,NULL),(30,30,'SELECT order_id, amount FROM orders JOIN order_positions ON orders.id = order_id JOIN books ON books.id = book_id WHERE title = \"Harry Potter und der Halbblutprinz\"',NULL,NULL,NULL),(31,31,'SELECT id, SUM(price*amount) AS \"sum\" FROM order_positions GROUP BY order_id',NULL,NULL,NULL),(32,32,'SELECT SUM(amount) AS sum_books FROM order_positions WHERE price < 5',NULL,NULL,NULL),(33,33,'SELECT order_positions.id, title, books.price AS current_price, order_positions.price AS order_price FROM order_positions JOIN books ON books.id = book_id WHERE (books.price - order_positions.price) > 0',NULL,NULL,NULL),(34,34,'SELECT books.* FROM books JOIN order_positions ON books.id = book_id WHERE order_id = 9 ORDER BY year ASC',NULL,NULL,NULL),(35,35,'SELECT first_name, family_name, COUNT(orders.id) AS order_count FROM customers JOIN orders ON customers.id = customer_id GROUP BY customers.id ORDER BY order_count DESC',NULL,NULL,NULL),(36,36,'INSERT INTO books (title,author,year, publisher_id,signature,stock,price) VALUES (\"Applied Statistical Genetics with R\",\"Andrea S. Foulkes\", 2010, 12 ,\"PF/520-Y/2\",289, 24.99)',NULL,NULL,NULL),(37,37,'DELETE FROM books WHERE author = \"J. R. R. Tolkien\"',NULL,NULL,NULL),(38,38,'DELETE FROM customers WHERE birthday > \'1995-01-01\'',NULL,NULL,NULL),(39,39,'DELETE FROM books WHERE price > 8.50 AND publisher_id = 7',NULL,NULL,NULL),(40,40,'DELETE FROM customers WHERE id NOT IN (SELECT customer_id FROM orders)',NULL,NULL,NULL),(41,41,'UPDATE customers SET email=\"paulina.r@yahoo.com\" WHERE first_name=\"Paulina\" AND family_name=\"Rohr\"',NULL,NULL,NULL),(42,42,'SELECT AVG(price) AS \"average_price\" FROM order_positions',NULL,NULL,NULL),(43,43,'SELECT * FROM books WHERE year IN (1998, 2001, 2011)',NULL,NULL,NULL),(44,44,'INSERT INTO customers (first_name, family_name, birthday, email) VALUES (\"Ferdinandus\", \"Merkle\", \"1990-11-24\", \"ferdinandus_1856@yahoo.com\");',NULL,NULL,NULL),(45,45,'SELECT customers.first_name, customers.family_name, count(*) AS rating_count FROM ratings LEFT JOIN customers ON customers.id=customer_id GROUP BY customer_id ORDER BY rating_count DESC',NULL,NULL,NULL),(46,46,'SELECT customers.first_name, customers.family_name, avg(rating) AS avg_rating FROM ratings LEFT JOIN customers ON customers.id=customer_id GROUP BY customer_id ORDER BY avg_rating DESC LIMIT 1',NULL,NULL,NULL),(47,47,'SELECT books.title, avg(rating) AS avg_rating FROM ratings JOIN books ON books.id=book_id GROUP BY book_id ORDER BY avg_rating DESC LIMIT 3',NULL,NULL,NULL),(48,48,'SELECT avg(rating) AS avg_rating, count(*) AS rating_count FROM ratings JOIN books ON books.id=book_id WHERE books.title=\"Harry Potter und der Orden des Phönix\" GROUP BY book_id',NULL,NULL,NULL),(49,49,'SELECT books.title, rating FROM ratings LEFT JOIN books ON books.id=book_id LEFT JOIN customers ON customers.id=customer_id WHERE customers.first_name = \"Christl\" AND customers.family_name = \"Seeliger\" ORDER BY rating DESC',NULL,NULL,NULL),(50,50,'SELECT books.title FROM wishlists LEFT JOIN books ON books.id=book_id LEFT JOIN customers ON customers.id=customer_id WHERE customers.first_name = \"Wilhard\" AND customers.family_name = \"Herold\"',NULL,NULL,NULL),(51,51,'SELECT books.title, count(*) AS wish_count FROM wishlists LEFT JOIN books ON books.id=book_id GROUP BY book_id ORDER BY wish_count DESC LIMIT 1',NULL,NULL,NULL),(52,52,'SELECT books.title FROM books WHERE id NOT IN (SELECT book_id FROM wishlists)',NULL,NULL,NULL),(53,53,'SELECT customers.first_name, customers.family_name, sum(price) AS value FROM wishlists LEFT JOIN books ON books.id=book_id LEFT JOIN customers ON customers.id=customer_id GROUP BY customer_id ORDER BY value ASC',NULL,NULL,NULL),(54,54,'SELECT books.title, avg(rating) AS avg_rating FROM ratings JOIN books ON books.id=book_id GROUP BY book_id ORDER BY avg_rating ASC',NULL,NULL,NULL),(55,55,'UPDATE books SET stock=2042 WHERE title=\"Shades of Grey - Geheimes Verlangen\"',NULL,NULL,NULL),
	(63, 62, 'select name from association where team_name = "Nordkorea";', NULL, NULL, NULL),
	(64, 63, 'select count(distinct (confederation)) as number from association;', NULL, NULL, NULL),
	(65, 64, 'select confederation from association where team_name = "Neuseeland";', NULL, NULL, NULL),
	(66, 65, 'select count(*) as number from association;', NULL, NULL, NULL),
	(67, 66, 'select address, phone, fax, homepage from association where team_name = "Südafrika";', NULL, NULL, NULL),
	(68, 67, 'select name, homepage from association where team_name = "Brasilien";', NULL, NULL, NULL),
	(69, 68, 'select avg(members) as average from association;', NULL, NULL, NULL),
	(70, 69, 'select max(founded) as max_founded from association;', NULL, NULL, NULL),
	(71, 70, 'select name from association where confederation = "CAF";', NULL, NULL, NULL),
	(72, 71, 'select trikot_color from association where team_name = "Griechenland" or team_name = "Elfenbeinküste";', NULL, NULL, NULL),
	(73, 72, 'select count(*) as number from coach where type = "Goaly-Coach";', NULL, NULL, NULL),
	(74, 73, 'select sum(goals_away + goals_home) as number from matches;', NULL, NULL, NULL),
	(75, 74, 'select avg(goals_home + goals_away) as number from matches where type = "group";', NULL, NULL, NULL),
	(76, 75, 'select birthplace from person where name = "Carlos Tévez";', NULL, NULL, NULL),
	(77, 76, 'select size from person where name = "Lionel Messi";', NULL, NULL, NULL),
	(78, 77, 'select min(age) as min_age from person;', NULL, NULL, NULL),
	(79, 78, 'select avg(size) as average from person;', NULL, NULL, NULL),
	(80, 79, 'select sum(building_costs) as sum_costs from stadium;', NULL, NULL, NULL),
	(81, 80, 'select building_year from stadium where name = "Loftus Versveld";', NULL, NULL, NULL),
	(82, 81, 'select name from stadium where city = "Bloemfontein";', NULL, NULL, NULL),
	(83, 82, 'select avg(2010 - building_year) as average from stadium;', NULL, NULL, NULL),
	(84, 83, 'select name from stadium where capacity = 40911;', NULL, NULL, NULL),
	(85, 84, 'select max(members) - min(members) as difference from association;', NULL, NULL, NULL),
	(86, 85, 'select name from match_has_refs, person where person.id = match_has_refs.referee_id and match_has_refs.role = "referee" and match_has_refs.match_id = 1;', NULL, NULL, NULL),
	(87, 86, 'select count(*) as number from dual_citizenship where citizenship2 = "Deutschland";', NULL, NULL, NULL),
	(91, 90, 'select person.name from matches, match_has_refs, person where matches.type = "final" and matches.id = match_has_refs.match_id and match_has_refs.referee_id = person.id;', NULL, NULL, NULL),
	(92, 91, 'select count(*) as number from match_has_refs, person where match_has_refs.referee_id = person.id and person.name = "Howard Melton Webb";', NULL, NULL, NULL),
	(93, 92, 'select * from matches where kickoff like "2010-06-21%";', NULL, NULL, NULL),
	(94, 93, 'select * from person where age=25', NULL, NULL, NULL),
	(95, 94, 'select * from person where not native_country = citizenship and citizenship = "Algerien";', NULL, NULL, NULL),
	(96, 95, 'select * from stadium order by building_costs asc;', NULL, NULL, NULL),
	(97, 96, 'insert into dual_citizenship values (600, "Polen");', NULL, NULL, NULL),
	(98, 97, 'update association set homepage = "www.northkoreanfootball.kp" where team_name = "Nordkorea";', NULL, NULL, NULL),
	(99, 98, 'update association set trikot_color = "blau" where team_name = "Neuseeland";', NULL, NULL, NULL),
	(100, 99, 'update association set members = 24760000 where team_name = "Nordkorea";', NULL, NULL, NULL),
	(101, 100, 'update dual_citizenship set citizenship2 = "Algerien" where person_id = 350;', NULL, NULL, NULL),
	(102, 101, 'delete from match_has_refs where match_id = 63;', NULL, NULL, NULL),
	(103, 102, 'delete from dual_citizenship where person_id = 3;', NULL, NULL, NULL),
	(104, 103, 'update person set size = size + 2 where native_country = "Paraguay";', NULL, NULL, NULL),
	(105, 104, 'delete from person where name = "Thomas Enevoldsen";', NULL, NULL, NULL),
	(106, 105, 'select footer from player, person where person.id = player.person_id and person.name = "Philipp Lahm"', NULL, NULL, NULL),
	(107, 106, 'select preliminarygroup.name from association, team_in_group, preliminarygroup where association.id = team_in_group.association and preliminarygroup.id = team_in_group.group and association.team_name = "Mexiko"', NULL, NULL, NULL),
	(108, 107, 'select p2.size - p1.size as difference from person as p1, person as p2 where p1.name = "Javier Mascherano" and p2.name = "Thomas Müller"', NULL, NULL, NULL),
	(109, 108, 'select sum(weight) as total_weight from person, player, association where player.person_id = person.id and player.association = association.id and association.team_name = "USA";', NULL, NULL, NULL),
	(110, 109, 'select person.name from person, player, association where association.team_name = "USA" and association.id = player.association and player.person_id = person.id', NULL, NULL, NULL),
	(111, 110, 'select count(*) from player, association  where player.association = association.id and association.team_name = "Deutschland" and player.footer = "links"', NULL, NULL, NULL),
	(112, 111, 'select count(*) from player, association where association.team_name = "Deutschland" and association.id = player.association;', NULL, NULL, NULL),
	(113, 112, 'select person.name from coach, person, association where person.id = coach.person_id and association.id = coach.association and association.team_name = "Ghana"', NULL, NULL, NULL),
	(114, 113, 'select association.team_name, preliminarygroup.name  from team_in_group, preliminarygroup, association where team_in_group.group = preliminarygroup.id and association.id = team_in_group.association;', NULL, NULL, NULL),
	(115, 114, 'select min(age) as result from person, coach where person.id = coach.person_id', NULL, NULL, NULL),
	(116, 115, 'select citizenship, citizenship2 from person, dual_citizenship where name = "Tommy Smith" and person.id = dual_citizenship.person_id;', NULL, NULL, NULL),
	(117, 116, 'select citizenship2 from person, dual_citizenship where name = "Samuel Eto\'o" and person.id = dual_citizenship.person_id;', NULL, NULL, NULL),
	(118, 117, 'select a1.team_name as team1, a2.team_name as team2 from association a1, association a2 where a1.trikot_color = a2.trikot_color and a1.id!=a2.id', NULL, NULL, NULL),
	(119, 118, 'select avg(age) as average from person, association, player where person.id = player.person_id and player.association = association.id and association.team_name = "Neuseeland";', NULL, NULL, NULL),
	(120, 119, 'select a1.members - a2.members as difference from association a1, association a2 where a1.team_name = "Deutschland" and a2.team_name = "Frankreich";', NULL, NULL, NULL),
	(121, 120, 'select avg(size) as average from person, player where person.id=player.person_id and position="Innenverteidiger"', NULL, NULL, NULL),
	(122, 121, 'select min(size) as min_size from person, player where person.id=player.person_id and position="Torwart"', NULL, NULL, NULL),
	(123, 122, 'select person.name from person, player, association where person.id=player.person_id and player.association=association.id and position="Stürmer" and association.team_name="Spanien"', NULL, NULL, NULL),
	(124, 123, 'select   person.name, player.market_value , association.team_name from person, player, association where person.id=player.person_id and player.person_id=association.team_captain order by market_value desc', NULL, NULL, NULL),
	(125, 124, 'delete from stadium where name = "Loftus Versveld";', NULL, NULL, NULL),
	(126, 125, 'delete from referee where person_id = 800;', NULL, NULL, NULL),
	(127, 126, 'INSERT INTO stadium (name, city, capacity, building_costs, building_year) values (\'Mbombela-Stadion\', \'Mbombela\', 46000, 1000000, 2009)', NULL, NULL, NULL),
	(128, 127, 'INSERT INTO match_has_refs (match_id, referee_id, role) values (1,795,\'assistant\')', NULL, NULL, NULL),
	(129, 128, 'INSERT INTO person (name, age, birthplace, native_country, citizenship, size, weight) values (\'Chuck Norris\', 70, \'Ryan\', \'USA\',\'USA\', 178, 80)', NULL, NULL, NULL),
	(130, 129, 'select name,native_country, citizenship from person where native_country!=citizenship', NULL, NULL, NULL),
	(131, 130, 'select distinct(position) as positions from player', NULL, NULL, NULL);
/*!40000 ALTER TABLE `solution_query` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_entry`
--

LOCK TABLES `user_entry` WRITE;
/*!40000 ALTER TABLE `user_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_entry` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_ex_tag`
--

LOCK TABLES `user_ex_tag` WRITE;
/*!40000 ALTER TABLE `user_ex_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_ex_tag` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_result`
--

LOCK TABLES `user_result` WRITE;
/*!40000 ALTER TABLE `user_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_right`
--

DROP TABLE IF EXISTS `user_right`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_right` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `created_by_user_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `scenario_id` int(10) unsigned DEFAULT NULL,
  `rating_rights` tinyint(1) unsigned DEFAULT '0',
  `group_editing_rights` tinyint(1) unsigned DEFAULT '0',
  `scenario_editing_rights` tinyint(1) unsigned DEFAULT '0',
  `scenario_add_delete_rights` tinyint(1) unsigned DEFAULT '0',
  `user_management_rights` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `scenario_id` (`scenario_id`),
  CONSTRAINT `tutor_to_scenario_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tutor_to_scenario_ibfk_2` FOREIGN KEY (`created_by_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tutor_to_scenario_ibfk_3` FOREIGN KEY (`scenario_id`) REFERENCES `scenario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_right`
--

LOCK TABLES `user_right` WRITE;
/*!40000 ALTER TABLE `user_right` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_right` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-15  0:26:12
