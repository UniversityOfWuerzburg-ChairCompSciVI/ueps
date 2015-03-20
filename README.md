# ÜPS
**ÜPS** (**Ü**bungs-**P**rogramm für **S**QL) ist ein webbasiertes 
Trainingssystem zum Erlernen der Datenbanksprache SQL.

([**Screenshots**](#screenshots)).

## Showcase

Link                                                        | Rolle
----------------------------------------------------------- | -------------
[**Amazon Buchdatenbank**][d1] / [**Fussball WM 2010**][d2] | Student
[**Amazon Buchdatenbank**][d3] / [**Fussball WM 2010**][d4] | Admin 
[**Amazon Buchdatenbank**][d4] / [**Fussball WM 2010**][d5] | Dozent 

-

## Inhaltsverzeichnis
1. [Systemanforderungen](#systemanforderungen)
2. [Kurzanleitung zur Installation](#kurzanleitung-zur-installation)
3. [Konfiguration mit 'config.properties'](#konfiguration-mit-configproperties)
  1. [Datenbankangaben](#datenbankangaben)
  2. [Automatischer Import der Datenbank](#automatischer-import-der-datenbank)
  3. [Nutzer-Authentifizierung](#nutzer-authentifizierung)
  4. [Pfadangaben](#pfadangaben)
  5. [Aussehen/Text anpassen](#aussehentext-anpassen)
4. [Deploy mit Maven](#deploy-mit-maven)
5. [Bedienung](#bedienung)
  1. [Übungsmaterial erstellen/bearbeiten](#erstellen-bearbeiten)
  2. [Rollen und Rechte](#rollen-und-rechte)
  3. [Abgaben bewerten](#abgaben-bewerten)
8. [Screenshots](#screenshots)
9. [Implementierung](#implementierung)
  1. [Verwendete Technologien](#verwendete-technologien)
  2. [Wartung](#wartung)
  3. [Quellcodedokumentation](#quellcodedokumentation)

-

## Systemanforderungen
**Serverseitig**
* Tomcat 7 oder höher
  (Für diese Anleitung wird Tomcat 7 verwendet)
* Maven 3 (zum Kompilieren)
* MySQL 5-Server

**Clientseitig**
* Firefox, Chrome, Safari (ab Ver.7) oder Internet Explorer (ab Ver.9)
* Aktiviertes JavaScript wird vorausgesetzt

Mobile Geräte werden zwar unterstützt, es wird jedoch keine angepasste 
Darstellung angeboten

-

## Kurzanleitung zur Installation

1. Quellcode herunterladen:<br/>
   ``git clone --depth=1 
   https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps``<br/>
   (Alternativ auch als [direkter Download][master-archive])

2. In das ``ueps``-Verzeichnis wechseln.

3. Konfigurationsdatei in einem Editor 
   öffnen: [``src/main/resources/config.properties``][cfg]

4. Den Datenbank-Server und einen Nutzer festlegen:
   ```properties
   MASTER_DBHOST = 127.0.0.1
   MASTER_DBPORT = 3306
   MASTER_DBNAME = ueps_master
   MASTER_DBUSER = test_user
   MASTER_DBPASS = 3ti4k4tm270kg
   ```

   Der unter [``MASTER_DBUSER``][MASTER_DBUSER] festgelegte Nutzer sollte 
   folgende Rechte besitzen:

  ```
  SELECT, INSERT, UPDATE, DELETE, ALTER, 
  CREATE, DROP, GRANT OPTION, LOCK TABLES
  ```

  Die aufgelisteten Rechte müssen für die unter 
  [``MASTER_DBNAME``][MASTER_DBNAME] angegebene Datenbank sowie für neu 
  erstellte Datenbanken gelten. Ein Rechte-Skript könnte beispielsweise 
  folgendermaßen aussehen:

  ```
  GRANT SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP,
  GRANT OPTION, LOCK TABLES, ON *.* TO '%MASTER_DBUSER%'@'%MASTER_DBHOST%'
  IDENTIFIED BY '%MASTER_DBPASS%';
  ```

6. Anschließend kann die Anwendung kompiliert werden:
   * *Unter Linux*<br/>
     Zuerst müssen einige Build-Skripte im Wurzelverzeichnis ausführbar 
     gemacht werden:<br/>
     ``chmod +x check-dependencies.sh build-deploy.sh build-package.sh``<br/>
     Dann einfach folgendes Skript ausführen<br/>
     [``./build-package.sh``](build-deploy.sh)<br/>

   * *Unter Windows*<br/>
     Einfach folgendes Skript ausführen (Doppelklick genügt):<br/>
     [``build-package.bat``](build-package.bat)

7. Die kompilierte ``ueps.war``-Datei sollte jetzt deploy-fertig im 
   Wurzelverzeichnis zu finden sein. (ÜPS lässt sich alternativ auch 
   [direkt mit Maven deployen](#deploy-mit-maven)).

8. [Die Startseite](#startseite) lässt sich jetzt über die eingestellte 
   Tomcat-URL aufrufen (siehe auch 
   [Nutzer-Authentifizierung](#nutzer-authentifizierung)). 
   ``catalina.out`` sollte folgende Zeilen zeigen:

  ```
    INFO (ueps): Load 'config.properties' from
         'webapps/ueps/WEB-INF/classes'
    INFO (ueps): Load 'text_de.properties' from
         'webapps/ueps/WEB-INF/classes'
    INFO (ueps): Init logger with filepath
         'webapps/ueps/WEB-INF/classes/log/ueps-webapp.log'
    INFO (ueps): Starting hibernate session
    INFO (ueps): Master database not found
    INFO (ueps): SQL-script found
    INFO (ueps): Importing database
    INFO (ueps): Creating database 'ueps_master'
    INFO (ueps): Admin user with id: 'user_1' added
  ```

-

Mit der gezeigten Konfiguration werden beim Start der Anwendungen folgenden 
Datenbanken erstellt:
* ``ueps_master`` ([Administrative Datenbank][er-admin] 
  - festgelegt durch ``MASTER_DBNAME``)
* ``ueps_slave_001`` (Datenbank für das erste Beispielszenario 
  - [Amazon Buchdatenbank][er-amazon])
* ``ueps_slave_002`` (Datenbank für das zweite Beispielszenario 
  - [Fussball WM 2010][er-wm2010])

Für die ``slave``-Datenbanken werden jeweils folgende Datenbanknutzer mit 
beschränkten Rechten erstellt:
* ``ueps_001``
* ``ueps_002``

-

## Konfiguration mit 'config.properties'

Bevor man die Anwendung startet sollte man zunächst einen Blick in die 
Konfigurationsdatei [``config.properties``][db-fields] unter 
[``src/main/resources/``][res] werfen und Angaben zum Datenbank-Server 
überschreiben.

#### Datenbankangaben

In der [administrativen Datenbank][db-fields] werden alle Studentenabgaben und 
zugehörige Aufgaben gespeichert (siehe [ER-Diagramm][er-admin]).

```properties
MASTER_DBHOST = 127.0.0.1
MASTER_DBPORT = 3306
MASTER_DBNAME = ueps_master
MASTER_DBUSER = test_user
MASTER_DBPASS = 3ti4k4tm270kg
```

Der Nutzer unter [``MASTER_DBUSER``][MASTER_DBUSER] benötigt mindestens 
folgende Rechte: ``SELECT, INSERT, UPDATE, DELETE`` für die Datenbank 
angegeben durch [``MASTER_DBNAME``][MASTER_DBNAME].

Beispiel MySQL-Rechte-Skript:
```
GRANT SELECT, INSERT, UPDATE, DELETE ON %MASTER_DBNAME%.*
TO '%MASTER_DBUSER%'@'%MASTER_DBHOST%' IDENTIFIED BY '%MASTER_DBPASS%';
```

Ausgefüllt mit den oberen Beispiel-Daten:
```
GRANT SELECT, INSERT, UPDATE, DELETE ON ueps_master.*
TO 'test_user'@'127.0.0.1' IDENTIFIED BY '3ti4k4tm270kg';
```

#### Automatischer Import der Datenbank
Die Option [``IMPORT_DB_IF_EMPTY``][IMPORT_DB_IF_EMPTY] ermöglicht die 
Automatisierung des Datenbank-Imports. Dafür benötigt der unter 
[``MASTER_DBUSER``][MASTER_DBUSER] angegebene Nutzer zusätzlich noch 
``ALTER, CREATE, DROP, LOCK TABLES``-Rechte.

Die Installation wird nur gestartet, wenn keine Datenbank mit dem Namen 
[``MASTER_DBNAME``][MASTER_DBNAME] gefunden wurde.

Möchte man die zusätzlichen Rechte nicht vergeben, so kann man die Datenbank 
auch manuell importieren. Das zugehörige MySQL-Skript findet sich unter 
[``src/main/resources/admin_db_structure.sql``][admin-sql].

Möchte man die Datenbank zurücksetzen, so lässt sich dies mit der Option 
[``FORCE_RESET_DATABASE``][FORCE_RESET_DATABASE] bewerkstelligen. Diese Option 
wird nach einem erfolgreichen Reset von der Anwendung selbst auf ``false`` 
gesetzt, sodass ein Server-Neustart die Datenbank nicht erneut zurücksetzt.

Möchte man noch zusätzlich zwei Beispielszenarios importieren, so kann man das 
über die Option [``IMPORT_EXAMPLE_SCENARIO``][IMPORT_EXAMPLE_SCENARIO] 
aktivieren. Es wird für jedes Szenario eine neue Datenbank unter 
[``MASTER_DBHOST``][MASTER_DBHOST] erstellt.
Für jede Szenario-Datenbank wird ebenfalls ein Datenbank-Nutzer mit 
beschränkten Rechten erstellt. Hierfür benötigt der 
[``MASTER_DBUSER``][MASTER_DBUSER] jedoch zusätzlich ``GRANT OPTION``-Rechte.

Ein vollständiges Rechte-Skript würde also folgendermaßen aussehen:
```
GRANT SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP, GRANT OPTION,
LOCK TABLES, ON *.* TO '%MASTER_DBUSER%'@'%MASTER_DBHOST%'
IDENTIFIED BY '%MASTER_DBPASS%';
```

Wenn eine automatische Installation nicht erwünscht ist, so benötigt die 
Anwendung im laufenden Betrieb nur ``SELECT, INSERT, UPDATE, DELETE`` Rechte.

#### Nutzer-Authentifizierung
In der Konfigurationsdatei sollte mindestens ein [Administrator-Nutzer][ADMINS] 
festgelegt werden. Mehrere Nutzer sollten mit einem Semikolon getrennt werden 
(z.B. ``admin_id1;admin_id2;``).
Admins können auch zur Laufzeit hinzugefügt werden, hierfür muss man jedoch den 
Datenbankeintrag für den Nutzer manuell abändern 
([Tabelle: ``user``, Spalte: ``is_admin``][er-admin].

ÜPS besitzt keine eigene Nutzerverwaltung. Die Nutzer-Authentifizierung erfolgt 
über die Open-Source Lernplattform [Moodle][moodle] mithilfe der 
"[Externe URL][moodle-url]"-Funktion. Diese Form der Anmeldung setzt voraus, 
dass die Option [``USE_MOODLE_LOGIN``][USE_MOODLE_LOGIN] auf ``true`` steht.

Für die Anmeldung werden zwei HTTP GET Paramater an ÜPS übergeben: die 
**Nutzer-ID** (``userID``) von Moodle und ein **verschlüsseltes Kennwort** 
(``encryptedCode``).<br>
In der config.properties wird zusätzlich ein [``SECRET_PHRASE``][SECRET_PHRASE] 
festgelegt. Dieses ``SECRET_PHRASE`` wird von Moodle dazu benutzt, um den 
``encryptedCode`` zu generieren, der als Parameter an den ÜPS-Server 
übermittelt wird.

Das verschlüsselte Kennwort wird über einen md5-Wert der aktuellen 
Client-IP-Adresse in Verbindung mit dem ``SECRET_PHRASE`` und der Nutzer-ID 
erzeugt, d.h. 

```java
encryptedCode = md5(userIP + secretPhrase + userID)
```


Bei einer Benutzeranmeldung [berechnet ÜPS][enc-code] ebenfalls einen solchen 
``encryptedCode`` und vergleicht ihn mit dem übermittelten. Sind beide 
Kennwörter identisch, so ist der Benutzer authentifiziert, andernfalls bekommt 
er eine Fehlermeldung zu sehen.

**WICHTIG:** ``SECRET_PHRASE`` sollte aufgrund seiner Rolle **nicht** den 
[voreingestellten][SECRET_PHRASE] Wert beibehalten.

Zusätzlich zu den Anmeldeparametern sollte man noch die Kennung des zu 
bearbeitenden Szenarios ``scenarioID`` angeben 
([weitere Infos zu Szenarios](#erstellen-bearbeiten)).
Die fertige URL sieht dann folgendermaßen aus:

```
http://%HOST_URL%/index.xhtml?
userID=%USER_ID%&encryptedCode=%ENCRYPTED_CODE%&scenarioID=%SCENARIO_ID%
```

Ausgefülltes Beispiel:<br/>
```
http://ueps.ddnss.de:82/ueps/index.xhtml
?userID=demo_admin&encryptedCode=showcase&scenarioID=1
```

#### Pfadangaben
Es lassen sich zwei Pfade konfigurieren (optional).

1. Der Pfad für Logdateien unter [``LOG_PATH``][LOG_PATH].

2. Der Pfad für die Szenario-Dateien unter 
   [``SCENARIO_RESOURCES_PATH``][SCENARIO_RESOURCES_PATH]. Unter diesem Pfad 
   werden alle hochgeladenen Szenario-MySQL-Skripte und ER-Diagramme abgelegt.

#### Aussehen/Text anpassen

Alle Texte sind in der Properties-Datei [``text_de.properties``][txt] 
zusammengefasst und lassen sich so komfortabel abändern.

Alle Bilder, das Favicon und die Hintergrundfarbe können in der 
[``config.properties``][design] geändert werden. Für größere Änderungen gibt 
es das Stylesheet [``main.css``][main-css]. Das Aussehen der Tooltips lässt 
sich mit [``tipsy.css``][tipsy-css] verändern.

-

## Deploy mit Maven
Die Anwendung lässt sich direkt mit Maven und dem [Tomcat-Manager][tomcat] 
deployen. Hierzu müssen zwei Dateien abgeändert werden.

**tomcat-users.xml**<br/>
Linux-Pfad:<br/>
``/etc/tomcat7/tomcat-users.xml``<br/>
Windows-Pfad:<br/>
``%TOMCAT_PATH%\conf\tomcat-users.xml``<br/><br/>
Hier muss einem Nutzer (hier: 'admin') die Rolle 'manager-script' zugewiesen 
werden:<br/>
```
<role rolename="manager-script"/>
<user username="admin" password="testing" roles="manager-script"/>
```

**pom.xml** im [Root-Verzeichnis](pom.xml#L141-L163)<br/>
Hier dann den entsprechenden Nutzer eintragen und die 
[Tomcat-URL anpassen][maven-plugin]:
```
<plugin>
<groupId>org.apache.tomcat.maven</groupId>
<artifactId>tomcat7-maven-plugin</artifactId>
...
<configuration>
  <url>http://127.0.0.1:8080/manager/text</url>
  <mode>war</mode>
  <warFile>ueps.war</warFile>
  <server>TomcatServer</server>
  <username>admin</username>
  <password>testing</password>
  <path>/ueps</path>
</configuration>
</plugin>
```

Anschließend kann die Anwendung mit folgendem Skript deployed werden:<br/>
[``./build-deploy.sh``](build-deploy.sh) unter Linux bzw. 
[``build-deploy.bat``](build-deploy.bat) unter Windows.

-

## Bedienung

<a name="erstellen-bearbeiten">
#### Übungsmaterial erstellen/bearbeiten ([Screenshots](#szenarios-erstellenbearbeiten))
ÜPS fasst einzelne SQL-Übungsaufgaben zusammen zu Aufgabengruppen und 
Aufgabengruppen zu "Szenarios":

```
├── Szenario 01
│   ├── Aufgabengruppe 01
│   │   ├── Aufgabe 01
│   │   └── Aufgabe 02
│   ├── Aufgabengruppe 02
│   │   └── ...
│   └── ...
├── Szenario 02
│   └── ...
└── ...
```

Besitzt der angemeldete Nutzer **Editierrechte**, so kann er Aufgaben 
komfortabel über die *Editieren*-Seite bearbeiten. Hierfür muss der Nutzer 
einen gewünschten Knoten im Baum auswählen, das Kontextmenü mit einem 
Rechtsklick öffnen und auf *Bearbeiten* klicken 

([Screenshot](#szenarios-erstellenbearbeiten)).

#### Szenarios erstellen ([Screenshot](#edit-scenario))
Ein einzelnes Szenario stellt eine SQL Datenbank dar. Auf dieser Datenbank werden
alle zugehörigen SQL-Queries der Studenten ausgeführt.<br>
Für das Erstellen eines neuen Szenarios muss der Autor ein MySQL-Datenbank-Skript
hochladen. Hierfür muss ein eigenes externes Tool verwendet werden (z.B. 
[HeidiSQL][heidi], [SQuirrel][squ], [phpMyAdmin][php]  ...).

Man sollte darauf achten, dass dieses Skript nicht zu groß wird (am besten unter 
200KB). Eine Tabelle braucht für Lehrzwecke keine 10.000 Einträge.
``DROP``-und ``CREATE``-Statements benötigen viel Laufzeit, um ein Vielfaches mehr 
als die üblichen und optimierten ``SELECT, UPDATE, DELETE, INSERT``-Statements.
Je kompakter das Skript, desto performanter die Anwendung.

Für jeden Studenten wird das komplette Skript beim Login einmal ausgeführt.
Es wird also von jeder Tabelle eine Kopie für jeden individuellen Nutzer erstellt
(hierfür wird einfach bei jeder Tabelle die Moodle-``userID`` als Präfix angehängt).
So wird verhindert, dass sich die Nutzer gegenseitig stören, wenn sie die Tabellen
modifizieren.<br>
Von den originalen Tabellen wird jeweils ein Hashwert gespeichert. Modifiziert
ein Student eins seiner Tabellen, so wird **ausschließlich** diese mithilfe des 
SQL-Skripts wieder auf den Ursprungszustand zurückgesetzt. Es werden also nur 
die Inserts für diese Tabelle aus dem Skript verwendet.

Wenn das Szenario gespeichert wurde, so sollte man unbedingt überprüfen, ob 
alle Tabellen [richtig erkannt][e5] wurden. Wenn ein Skript syntaktisch korrekt 
ist aber nicht richtig geparst werden konnte, dann verfassen Sie bitte ein 
Bug Report [hier][bug].

Aus Sicherheitsgründen werden alle MySQL-Anfragen der Studenten mit einem
MySQL-Nutzer mit beschränkten Zugriffsrechten ausgeführt.

Wenn der unter [``MASTER_DBUSER``](MASTER_DB_USER) ``ALTER, CREATE, DROP, 
GRANT OPTION, LOCK TABLES``-Rechte besitzt, so übernimmt ÜPS automatisch das
Anlegen von neuen Datenbanken und beschränkten Datenbanknutzern.

Möchte man dies vermeiden, so müssen die Datenbanken und Nutzer manuell erstellt 
und eingetragen werden. Die unter "Datenbank-Host/IP" eingetragene Datenbank
muss hierfür leer sein und der Nutzer unter "Datenbank-User" muss 
``SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, LOCK TABLES``-Rechte
**ausschließlich** für die neue Datenbank besitzen. Als Hilfe wird ein 
Rechteskript am Anfang der Seite generiert ([Screenshot](#edit-scenario)).

Optional kann auch ein Entity-Relationship-Diagramm (im JPEG/PNG-Format) 
bereitgestellt werden.

#### Aufgabengruppe erstellen ([Screenshot](#edit-group))
Es gibt zwei Arten von Aufgabengruppen, **bewertete** und **unbewertete**. 
Unbewertete Aufgabengruppen dienen dem freien Üben und bieten uneingeschränkten 
Zugang zu den Musterlösungen. Bewertete Aufgabengruppen sind zeitbeschränkt und 
dienen zur Feststellung der Fähigkeiten der Studenten.

Zusätzlich bekommt der Student bei bewerteten Aufgaben eine 
[Server-Meldung](#server-info) für jede Abgabe als Bestätigung.

Ergebnisse können nach Ablauf der Abgabezeit **automatisch** oder **manuell** 
freigegeben werden. Möchte man nach einer manuellen Bearbeitungszeit die 
Ergebnisse freigeben, so muss man nur die Option '*nicht automatisch freigeben*'
wieder auf '*automatisch freigeben*' setzen.

Bewertete Aufgaben (und dessen Elternknoten) können nicht mehr gelöscht werden, 
wenn Nutzer (ohne weitere Rechte) diese bereits bearbeitet haben. Dies soll 
versehentliches Löschen von prüfungsrelevanten Nutzerabgaben verhindern. Möchte
man diese tatsächlich löschen, so muss zuerst die zugehörige Übungsgruppe auf 
"unbewertet" gestellt werden.

#### Aufgabe erstellen ([Screenshot](#edit-exercise))

Eine Aufgabe besteht aus einem Aufgabentext und einer oder mehrerer Musterlösungen.
Die Aufgaben sind auf ``SELECT``, ``UPDATE``, ``INSERT`` und ``DELETE`` beschränkt.
``CREATE``, ``DROP``-Anfragen werden nicht unterstüzt.

Zusätzlich sollte ein Schwierigkeitsgrad (1-5 Punkte) festgelegt werden. Man sollte
dabei beachten, dass die Anwendung Abgaben nur als *richtig* oder *falsch*
einstufen kann, d.h. bei einer maximalen Punktzahl von fünf kann nur bei einer 
[manuellen Nachkorrektur](#abgaben-bewerten) eine Punktzahl zwischen 1-4 erreicht 
werden.

#### Szenarios exportieren

Szenarios lassen sich als XSD + XML exportieren (*Editieren-Menü* &#8594; 
*Rechtklick auf beliebiges Szenario*  &#8594; *Szenario exportieren*).

Ein Beispiel zum herunterladen: [Amazon-Szenario][export1]

-

#### Rollen und Rechte
Um den Zugriff auf die Funktionen von ÜPS zu kontrollieren, wurden drei 
verschiedene Rollen eingeführt: **Student**, **Dozent** und **Admin**.


Der **Admin** verfügt über umfassende Rechte im ganzen System:
- globale Benutzerverwaltung z.B. Bestimmung von Dozenten und deren Zuweisung 
  zu Szenarios
- globale Verwaltung der Szenarios, sowie Rolle 'Dozent' in allen Szenarios<br>
  (Admins sind die einzigen Nutzer, die neue Szenarios erstellen können)

Die Rolle **Dozent** privilegiert den Nutzer dazu folgende Funktionalitäten 
*innerhalb eines Szenarios* (festgelegt von einem Admin) zu nutzen:
- Zuweisung von Rechten für Nutzer innerhalb des Szenarios
- Änderung des Szenarios (wobei die Optionen zum Datenbank-Server und 
  Datenbank-Nutzer nur von einem Admin geändert werden können)
- Erstellung und Änderung von Übungsblättern und Übungsaufgaben des Szenarios
- Einsicht und Bewertung von abgegebenen Lösungen innerhalb des Szenarios

Ein **Student** kann folgende Funktionen des Systems nutzen:
- Durchführung von Übungsaufgaben
- Einsicht in die Korrektur der eigenen Abgaben
- ggf. Korrektur- und/oder Bearbeitungsrechte für bestimmte Szenarien 
  (für Tutoren)

#### Abgaben bewerten

Nutzer mit Bewertungsrechten können Studentenabgaben aufrufen und gegebenenfalls 
korrigieren. Wenn manuelle Korrekturen vorgenommen wurden, so wird die 
``userID`` des Korrektors gespeichert.
Einzelne Studentenabgaben können ebenfalls kommentiert werden 
([Screenshots](#abgaben-bewerten-1)) und nach Freigabe der Ergebnisse von den 
Nutzern eingesehen werden.

Um endgültige Bewertungen an Moodle zu übertragen, können sich Nutzer mit 
Bewertungsrechten zu den einzelnen Aufgabengruppen CSV-Dateien generieren.

**Anleitung:**<br>
*Bewerten* &#8594; *Nach gesuchtem Übungsblatt filtern* &#8594; 
*Export-Symbol klicken*<br>
[Screenshot 1][csv1] | [Screenshot 2][csv2]

Die Werte sind mit Semikolon getrennt:
```
ex_id ; user_id    ; points ; max_points
64    ; student_45 ; 1      ; 1
64    ; student_69 ; 1      ; 1
64    ; student_75 ; 0      ; 1
68    ; student_48 ; 1      ; 1
68    ; student_85 ; 1      ; 1
74    ; student_69 ; 2      ; 2
76    ; student_12 ; 0      ; 1
76    ; student_55 ; 0      ; 1
76    ; student_59 ; 1      ; 1
81    ; student_38 ; 1      ; 1
90    ; student_38 ; 0      ; 3
90    ; student_55 ; 3      ; 3
90    ; student_57 ; 0      ; 3
```

-

## Screenshots

#### Startseite

&nbsp;         | &nbsp;
-------------- | --------------
[![][i1t]][i1] | Beschreibung und Auflistung der <br>Übungsaufgaben zu einem Szenario <br>[Showcase][d7]
[![][i6t]][i6] | Eine **unbewertete** Übungsgruppe aufgeklappt
[![][i5t]][i5] | Eine **bewertete** und noch <br>**aktive** Übungsgruppe aufgeklappt
[![][i3t]][i3] | Eine **bewertete** und bereits <br>**abgelaufene** Übungsgruppe aufgeklappt
[![][i4t]][i4] | Eine einzelne Bewertung mit <br>Kommentar vom Korrektor aufgeklappt
[![][i2t]][i2] | Auflistung der Szenarios falls keine <br>``scenarioID`` per GET-Paramater übergeben <br>wurde

-

#### Übungsseite

&nbsp;         | &nbsp;
-------------- | --------------
[![][t1t]][t1] | Übungsbereich für die Studenten <br>[Showcase][d8]
[![][t2t]][t2] | Anzeige des ER-Diagramms <br>(diese lassen sich auch in einem <br>neuen Tab öffnen)
[![][t3t]][t3] | Anzeige von Tabellen (die Fenster lassen sich <br>ziehen und vergrößern/verkleinern)
[![][t4t]][t4] | Feedback-Generierung <a name="server-info">
[![][t5t]][t5] | Servermeldung bei einer Studentenabgabe <br>für eine **bewertete** Aufgabe [Showcase][d9]

-

#### Rechteverwaltung

&nbsp;         | &nbsp;
-------------- | --------------
[![][r1t]][r1] | Auflistung der vergebenen Rechte <br>[Showcase][d10]
[![][r2t]][r2] | Hinzufügen von Rechten

-

#### Szenarios erstellen/bearbeiten

&nbsp;         | &nbsp;
-------------- | --------------
[![][e0t]][e0] | Aufgabenliste als Baumstruktur <br>[Showcase][d11]
[![][e1t]][e1] | Kontextmenü-Einträge (wird durch einen <br>Rechtsklick auf einen Knoten geöffnet)<a name="edit-scenario">
[![][e2t]][e2] | Szenario bearbeiten <br>[Showcase][d12]<a name="edit-group">
[![][e3t]][e3] | Übungsblatt bearbeiten <br>[Showcase][d13]<a name="edit-exercise">
[![][e4t]][e4] | Übungsaufgabe bearbeiten <br>[Showcase][d14]

-

#### Abgaben bewerten

&nbsp;         | &nbsp;
-------------- | --------------
[![][s1t]][s1] | Abgaben lassen sich nach dem Szenario, <br>dem Übungsblatt, der Übungsaufgabe <br>oder nach der User-ID sortieren <br>(oder nach einer Kombination dieser <br>Parameter) <br>[Showcase][d15]
[![][s2t]][s2] | Auflistung der Nutzereinträge in einer <br>Baumstruktur
[![][s3t]][s3] | Nutzerabgabe bearbeiten <br>[Showcase][d16]
[![][s4t]][s4] | Kommentar abgeben

-

## Implementierung

#### Verwendete Technologien

**Clientseitig**
* HTML5, [jQuery][jquery], CSS3

**Serverseitig**
* Tomcat7/8
* [PrimeFaces 4.0][prime] (JSF-Framework)
    * Leicht abgeändert, Quellcode (mit der Dokumentation der Änderungen) 
      findet sich [hier](res/primefaces/)
* MySQL5 mit Hibernate (für die administrative Datenbank)
* MySQL5 mit [MariaDB-Connector][mariadb] für die Übungsdatenbanken
* [Akiban SQL-parser][akiban] für die Feedback-Generierung


#### Wartung

Möchte man die Anwendung warten, so kann man im laufenden Betrieb eine 
[Wartungsmeldung][info] einblenden. Hierzu muss man nur 
[folgende Stelle][info-section] anpassen.

#### Quellcodedokumentation

[Doxygen Dokumentation][doxygen]

-

<!--- {{{ -->
[csv1]:   http://kolbasa.github.io/ueps/screenshots/csv-export-01.png
[csv2]:   http://kolbasa.github.io/ueps/screenshots/csv-export-02.png

[s1]:     http://kolbasa.github.io/ueps/screenshots/submission-01.png
[s1t]:    http://kolbasa.github.io/ueps/screenshots/submission-01-small.png
[s2]:     http://kolbasa.github.io/ueps/screenshots/submission-02.png
[s2t]:    http://kolbasa.github.io/ueps/screenshots/submission-02-small.png
[s3]:     http://kolbasa.github.io/ueps/screenshots/submission-03.png
[s3t]:    http://kolbasa.github.io/ueps/screenshots/submission-03-small.png
[s4]:     http://kolbasa.github.io/ueps/screenshots/submission-04.png
[s4t]:    http://kolbasa.github.io/ueps/screenshots/submission-04-small.png

[e0]:     http://kolbasa.github.io/ueps/screenshots/admin-01.png
[e0t]:    http://kolbasa.github.io/ueps/screenshots/admin-01-small.png
[e1]:     http://kolbasa.github.io/ueps/screenshots/edit-scenario-01.png
[e1t]:    http://kolbasa.github.io/ueps/screenshots/edit-scenario-01-small.png
[e2]:     http://kolbasa.github.io/ueps/screenshots/edit-scenario-02.png
[e2t]:    http://kolbasa.github.io/ueps/screenshots/edit-scenario-02-small.png
[e3]:     http://kolbasa.github.io/ueps/screenshots/edit-group-02.png
[e3t]:    http://kolbasa.github.io/ueps/screenshots/edit-group-02-small.png
[e4]:     http://kolbasa.github.io/ueps/screenshots/edit-ex-02.png
[e4t]:    http://kolbasa.github.io/ueps/screenshots/edit-ex-02-small.png
[e5]:     http://kolbasa.github.io/ueps/screenshots/edit-scenario-03.png

[t1]:     http://kolbasa.github.io/ueps/screenshots/task-01.png
[t1t]:    http://kolbasa.github.io/ueps/screenshots/task-01-small.png
[t2]:     http://kolbasa.github.io/ueps/screenshots/task-02.png
[t2t]:    http://kolbasa.github.io/ueps/screenshots/task-02-small.png
[t3]:     http://kolbasa.github.io/ueps/screenshots/task-03.png
[t3t]:    http://kolbasa.github.io/ueps/screenshots/task-03-small.png
[t4]:     http://kolbasa.github.io/ueps/screenshots/task-04.png
[t4t]:    http://kolbasa.github.io/ueps/screenshots/task-04-small.png
[t5]:     http://kolbasa.github.io/ueps/screenshots/task-05.png
[t5t]:    http://kolbasa.github.io/ueps/screenshots/task-05-small.png

[i1]:     http://kolbasa.github.io/ueps/screenshots/index-01.png
[i1t]:    http://kolbasa.github.io/ueps/screenshots/index-01-small.png
[i2]:     http://kolbasa.github.io/ueps/screenshots/index-02.png
[i2t]:    http://kolbasa.github.io/ueps/screenshots/index-02-small.png
[i3]:     http://kolbasa.github.io/ueps/screenshots/index-03.png
[i3t]:    http://kolbasa.github.io/ueps/screenshots/index-03-small.png
[i4]:     http://kolbasa.github.io/ueps/screenshots/index-04.png
[i4t]:    http://kolbasa.github.io/ueps/screenshots/index-04-small.png
[i5]:     http://kolbasa.github.io/ueps/screenshots/index-05.png
[i5t]:    http://kolbasa.github.io/ueps/screenshots/index-05-small.png
[i6]:     http://kolbasa.github.io/ueps/screenshots/index-06.png
[i6t]:    http://kolbasa.github.io/ueps/screenshots/index-06-small.png

[r1]:     http://kolbasa.github.io/ueps/screenshots/rights-01.png
[r1t]:    http://kolbasa.github.io/ueps/screenshots/rights-01-small.png
[r2]:     http://kolbasa.github.io/ueps/screenshots/rights-02.png
[r2t]:    http://kolbasa.github.io/ueps/screenshots/rights-02-small.png

[info]:   http://kolbasa.github.io/ueps/screenshots/info-screen.png



[heidi]:                      http://www.heidisql.com/
[squ]:                        http://squirrel-sql.sourceforge.net/
[php]:                        http://www.phpmyadmin.net/

[bug]:                        https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/issues
[master-archive]:             https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/archive/master.zip
[moodle]:                     https://moodle.org/
[moodle-url]:                 https://docs.moodle.org/27/de/Link/URL_konfigurieren
[tomcat]:                     http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html
[maven-plugin]:               http://tomcat.apache.org/maven-plugin-trunk/tomcat7-maven-plugin/usage.html
[jquery]:                     http://jquery.com/
[prime]:                      http://primefaces.org/
[mariadb]:                    https://mariadb.com/kb/en/mariadb/about-the-mariadb-java-client/
[akiban]:                     http://blog.akiban.com/akibans-open-source-sql-parser/


[enc-code]:                   src/main/java/de/uniwue/info6/webapp/session/SessionObject.java#L151-L166
[info-section]:               src/main/webapp/templates/common/commonLayout.xhtml#L71-L74
[txt]:                        src/main/resources/text_de.properties
[main-css]:                   src/main/webapp/resources/css/main.css
[tipsy-css]:                  src/main/webapp/resources/css/tipsy.css



[res]:                        src/main/resources/
[cfg]:                        src/main/resources/config.properties
[design]:                     src/main/resources/config.properties#L180-L262
[db-fields]:                  src/main/resources/config.properties#L40-L44
[MASTER_DBHOST]:              src/main/resources/config.properties#L40
[MASTER_DBNAME]:              src/main/resources/config.properties#L42
[MASTER_DBUSER]:              src/main/resources/config.properties#L43
[IMPORT_DB_IF_EMPTY]:         src/main/resources/config.properties#L57
[IMPORT_EXAMPLE_SCENARIO]:    src/main/resources/config.properties#L75
[FORCE_RESET_DATABASE]:       src/main/resources/config.properties#L84
[ADMINS]:                     src/main/resources/config.properties#L96
[USE_MOODLE_LOGIN]:           src/main/resources/config.properties#L111
[SECRET_PHRASE]:              src/main/resources/config.properties#L112
[SCENARIO_RESOURCES_PATH]:    src/main/resources/config.properties#L121
[LOG_PATH]:                   src/main/resources/config.properties#L124

[er-admin]:                   http://kolbasa.github.io/ueps/screenshots/admin-db-er-diagram.png
[er-amazon]:                  src/main/resources/sql/1/er-diagram.png
[er-wm2010]:                  src/main/resources/sql/2/wm_2014_05_14-12_00_37.png
[admin-sql]:                  src/main/resources/admin_db_structure.sql
[export1]:                    http://kolbasa.github.io/ueps/exports/scenario_1_export.zip
[doxygen]:                    http://kolbasa.github.io/ueps/doxygen/


[d1]:  http://ueps.scienceontheweb.net?index=xhtml&userID=demo_student&encryptedCode=showcase&scenarioID=1
[d2]:  http://ueps.scienceontheweb.net?index=xhtml&userID=demo_student&encryptedCode=showcase&scenarioID=2
[d3]:  http://ueps.scienceontheweb.net?index=xhtml&userID=demo_admin&encryptedCode=showcase&scenarioID=1
[d4]:  http://ueps.scienceontheweb.net?index=xhtml&userID=demo_admin&encryptedCode=showcase&scenarioID=2
[d5]:  http://ueps.scienceontheweb.net?index=xhtml&userID=demo_lecturer&encryptedCode=showcase&scenarioID=1
[d6]:  http://ueps.scienceontheweb.net?index=xhtml&userID=demo_lecturer&encryptedCode=showcase&scenarioID=2

[d7]:  http://ueps.scienceontheweb.net?index=xhtml&userID=demo_student&encryptedCode=showcase&scenarioID=1
[d8]:  http://ueps.scienceontheweb.net?task=xhtml&userID=demo_student&encryptedCode=showcase&scenarioID=1&exercise=1
[d9]:  http://ueps.scienceontheweb.net?task=xhtml&userID=demo_student&encryptedCode=showcase&scenarioID=1&exercise=4
[d10]: http://ueps.scienceontheweb.net?user_rights=xhtml&userID=demo_admin&encryptedCode=showcase
[d11]: http://ueps.scienceontheweb.net?admin=xhtml&userID=demo_admin&encryptedCode=showcase&scenarioID=1
[d12]: http://ueps.scienceontheweb.net?edit_scenario=xhtml&userID=demo_admin&encryptedCode=showcase&scenario=1&scenarioID=1
[d13]: http://ueps.scienceontheweb.net?edit_group=xhtml&userID=demo_admin&encryptedCode=showcase&group=1&scenarioID=1
[d14]: http://ueps.scienceontheweb.net?edit_ex=xhtml&userID=demo_admin&encryptedCode=showcase&exercise=62&scenarioID=1
[d15]: http://ueps.scienceontheweb.net?submission=xhtml&userID=demo_admin&encryptedCode=showcase&scenarioID=1
[d16]: http://ueps.scienceontheweb.net?edit_submission=xhtml&userID=demo_admin&encryptedCode=showcase&submission=1
<!--- }}} -->

