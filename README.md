# ÜPS
**ÜPS** (**Ü**bungs-**P**rogramm für **S**QL) ist ein webbasiertes Trainingssystem zum Erlernen der Datenbanksprache SQL ([**Screenshots**](#screenshots)).

<!--- http://ueps.ddnss.de:82/ueps/task.xhtml?userID=demo_admin&encryptedCode=showcase&scenarioID=1&exercise=1 -->

## Showcase

Link          | Rolle          | Szenario
------------- | -------------  | -------------
**<a href="http://ueps.scienceontheweb.net?index=xhtml&userID=demo_student&encryptedCode=showcase&scenarioID=1" target="_blank">Demo 1</a>** | Student | Amazon Buchdatenbank
**<a href="http://ueps.scienceontheweb.net?index=xhtml&userID=demo_admin&encryptedCode=showcase&scenarioID=1" target="_blank">Demo 2</a>** | Admin | Amazon Buchdatenbank
**<a href="http://ueps.scienceontheweb.net?index=xhtml&userID=demo_student&encryptedCode=showcase&scenarioID=2" target="_blank">Demo 3</a>** | Student | Fussball WM 2010 
**<a href="http://ueps.scienceontheweb.net?index=xhtml&userID=demo_admin&encryptedCode=showcase&scenarioID=2" target="_blank">Demo 4</a>** | Admin | Fussball WM 2010

# Inhaltsverzeichnis
1. [Systemanforderungen](#systemanforderungen)
2. [Kurzanleitung zur Installation](#kurzanleitung-zur-installation)
3. [Konfiguration mit 'config.properties'](#konfiguration-mit-configproperties)
  1. [Datenbankangaben](#datenbankangaben)
  2. [Automatischer Import der Datenbank](#automatischer-import-der-datenbank)
  3. [Nutzer-Authentifizierung](#nutzer-authentifizierung)
  4. [Pfadangaben](#pfadangaben)
  5. [Aussehen/Text anpassen](#aussehentext-anpassen)
4. [Deploy mit Maven](#deploy-mit-maven)
5. [Quellcodedokumentation](#quellcodedokumentation)
6. [Screenshots](#screenshots)

------

## Systemanforderungen
* Tomcat 7 oder höher
  (Für diese Anleitung wird Tomcat 7 verwendet)
* Maven 3 (zum Kompilieren)
* MySQL 5

------

## Kurzanleitung zur Installation

1. Quellcode herunterladen:<br/>
   ``git clone --depth=1 https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps``<br/>
   (Alternativ auch als [direkter Download](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/archive/master.zip))

2. In das ``ueps``-Verzeichnis wechseln.

3. Konfigurationsdatei in einem Editor öffnen: [``src/main/resources/config.properties``](src/main/resources/config.properties)

4. Den Datenbank-Server und einen Nutzer festlegen:
   ```properties
   MASTER_DBHOST = 127.0.0.1
   MASTER_DBPORT = 3306
   MASTER_DBNAME = ueps_master
   MASTER_DBUSER = test_user
   MASTER_DBPASS = 3ti4k4tm270kg
   ```
  <!--- ` -->
   Der unter [``MASTER_DBUSER``](src/main/resources/config.properties#L43) festgelegte Nutzer sollte folgende Rechte besitzen:
  ```
  SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP, GRANT OPTION, LOCK TABLES
  ```
  <!--- ` -->
  Die aufgelisteten Rechte müssen für die unter [``MASTER_DBNAME``](src/main/resources/config.properties#L42) angegebene Datenbank sowie für neu erstellte Datenbanken gelten. Eine Rechteskript könnte beispielsweise folgendermaßen aussehen:
  ```
  GRANT SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP,
  GRANT OPTION, LOCK TABLES, ON *.* TO '%MASTER_DBUSER%'@'%MASTER_DBHOST%'
  IDENTIFIED BY '%MASTER_DBPASS%';
  ```
  <!--- ` -->

6. Anschließend kann die Anwendung kompiliert werden:
   * *Unter Linux*<br/>
     Zuerst müssen einige Build-Skripte im Wurzelverzeichnis ausführbar gemacht werden:<br/>
     ``chmod +x check-dependencies.sh build-deploy.sh build-package.sh``<br/>
     Dann einfach foldendes Skript ausführen<br/>
     [``./build-package.sh``](build-deploy.sh)<br/>

   * *Unter Windows*<br/>
     Einfach folgendes Skript auführen (Doppelklick genügt):<br/>
     [``build-deploy.bat``](build-deploy.bat)

7. Die kompilierte ``ueps.war``-Datei sollte jetzt deploy-fertig im Wurzelverzeichnis zu finden sein.
   (ÜPS lässt sich alternativ auch [direkt mit Maven deployen](#DeployMaven)).

8. [Die Startseite](http://kolbasa.github.io/ueps/screenshots/startpage.png) lässt sich jetzt über die eingestellte Tomcat-URL aufrufen (siehe auch [Nutzer-Authentifizierung](#nutzer-authentifizierung)). ``catalina.out`` sollte folgende Zeilen zeigen:

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

  <!--- ` -->

--

Mit der gezeigten Konfiguration werden beim Start der Anwendungen folgenden Datenbanken erstellt:
* ``ueps_master`` ([Administrative Datenbank](http://kolbasa.github.io/ueps/screenshots/admin-db-er-diagram.png) - festgelegt durch ``MASTER_DBNAME``)
* ``ueps_slave_001`` (Datenbank für das erste Beispielszenario - [Amazon Buchdatenbank](http://kolbasa.github.io/ueps/screenshots/er-diagram-amazon.png))
* ``ueps_slave_002`` (Datenbank für das zweite Beispielszenario - [Fussball WM 2010](http://kolbasa.github.io/ueps/screenshots/er-diagram-wm.png))

Für die ``slave``-Datenbanken werden jeweils folgende Datenbanknutzer mit beschränkten Rechten erstellt:
* ``ueps_001``
* ``ueps_002``

------

## Konfiguration mit 'config.properties'
Bevor man die Anwendung startet sollte man zunächst einen Blick in die Konfigurationsdatei
[``config.properties``](src/main/resources/config.properties#L40-L44)
unter ``src/main/resources/``
werfen und Angaben zum Datenbank-Server überschreiben.

#### Datenbankangaben
In der [administrativen Datenbank](src/main/resources/config.properties#L40-L44) werden alle Studentenabgaben und zugehörige
Aufgaben gespeichert (siehe [ER-Diagramm](http://kolbasa.github.io/ueps/screenshots/admin-db-er-diagram.png)).

```properties
MASTER_DBHOST = 127.0.0.1
MASTER_DBPORT = 3306
MASTER_DBNAME = ueps_master
MASTER_DBUSER = test_user
MASTER_DBPASS = 3ti4k4tm270kg
```

Der Nutzer unter [``MASTER_DBUSER``](src/main/resources/config.properties#L43)
benötigt mindestens folgende
Rechte: ``SELECT, INSERT, UPDATE, DELETE`` für die Datenbank
angegeben durch [``MASTER_DBNAME``](src/main/resources/config.properties#L42).

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
Die Option [``IMPORT_DB_IF_EMPTY``](src/main/resources/config.properties#L57) ermöglicht die Automatisierung des Datenbank-Imports. Dafür benötigt der unter [``MASTER_DBUSER``](src/main/resources/config.properties#L43) angegebene Nutzer zusätzlich noch ``ALTER, CREATE, DROP, LOCK TABLES``-Rechte.

Die Installation wird nur gestartet, wenn keine Datenbank mit dem Namen [``MASTER_DBNAME``](src/main/resources/config.properties#L42) gefunden wurde.

Möchte man die zusätzlichen Rechte nicht vergeben, so kann man die Datenbank auch manuell importieren. Das zugehörige MySQL-Skript findet sich unter [``src/main/resources/admin_db_structure.sql``](src/main/resources/admin_db_structure.sql).

Möchte man die Datenbank zurücksetzen, so lässt sich dies mit der Option [``FORCE_RESET_DATABASE``](src/main/resources/config.properties#L84) bewerkstelligen. Diese Option wird nach einem erfolgreichen Reset von der Anwendung selbst auf ``false`` gesetzt, sodass ein Server-Neustart die Datenbank nicht erneut zurücksetzt.

Möchte man noch zusätzlich zwei Beispielszenarien importieren, so kann man das über die Option [``IMPORT_EXAMPLE_SCENARIO``](src/main/resources/config.properties#L75) aktivieren. Es wird für jedes Szenario eine neue Datenbank unter [``MASTER_DBHOST``](src/main/resources/config.properties#L40) erstellt.
Für jede Szenario-Datenbank wird ebenfalls ein Datenbank-Nutzer mit beschränkten Rechten erstellt. Hierfür benötigt der [``MASTER_DBUSER``](src/main/resources/config.properties#L43) jedoch zusätzlich ``GRANT OPTION``-Rechte.

Ein vollständiges Rechte-Skript würde also folgendermaßen aussehen:
```
GRANT SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP, GRANT OPTION,
LOCK TABLES, ON *.* TO '%MASTER_DBUSER%'@'%MASTER_DBHOST%'
IDENTIFIED BY '%MASTER_DBPASS%';
```

Wenn eine automatische Installation nicht erwünscht ist, so benötigt die Anwendung im laufenden Betrieb nur ``SELECT, INSERT, UPDATE, DELETE`` Rechte.

#### Nutzer-Authentifizierung
In der Konfigurationsdatei sollte mindestens ein [Adminstrator-Nutzer](src/main/resources/config.properties#L96) festgelegt werden. Mehrere Nutzer sollten mit einem Semikolon getrennt werden (z.B. ``admin_id1;admin_id2;``).
Admins können auch zur Laufzeit hinzugefügt werden, hierfür muss man jedoch den Datenbankeintrag für den Nutzer abändern ([Tabelle: "user", Spalte: "is_admin"](http://kolbasa.github.io/ueps/screenshots/admin-db-er-diagram.png)).

ÜPS besitzt keine eigene Nutzerverwaltung. Die Nutzer-Authentifizierung erfolgt über die Open-Source Lernplattform [Moodle](https://moodle.org/) mithilfe der "[Externe URL](https://docs.moodle.org/27/de/Link/URL_konfigurieren)"-Funktion.

Für die Anmeldung werden zwei GET-Paramater übergeben: die **Nutzer-ID** (``userID``) und ein **verschlüsseltes Kennwort** (``encryptedCode``).
In der config.properties wird zusätzlich ein [``SECRET_PHRASE``](src/main/resources/config.properties#L112) festgelegt. Dieses ``SECRET_PHRASE`` wirds benutzt, um den ``encryptedCode`` zu generieren, der als Parameter an den ÜPS-Server übermittelt wird. 

Der verschlüsselte Code wird über einen md5-Wert der aktuellen Client-IP-Adresse in Verbindung mit dem ``SECRET_PHRASE`` und der Nutzer-ID erzeugt, d.h. 
```
encryptedCode = md5(userIP + secretPhrase + userID)
```

Die Implementierung in ÜPS findet man [hier](src/main/java/de/uniwue/info6/webapp/session/SessionObject.java#L141-L166).

Die URL zusammen mit den Anmeldeparametern sieht folgendermaßen aus:

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

1. Der Pfad für Logdateien unter [``LOG_PATH``](src/main/resources/config.properties#L124).

2. Der Pfad für die Szenario-Dateien unter [``SCENARIO_RESOURCES_PATH``](src/main/resources/config.properties#L121). Unter diesem Pfad werden alle hochgeladenen Szenario-MySQL-Skripte und ER-Diagramme abgelegt.

#### Aussehen/Text anpassen

Alle Texte sind in der Properties-Datei [``text_de.properties``](src/main/resources/text_de.properties) zusammengefasst und lassen sich so komfortabel abändern.

Alle Bilder, das Favicon und die Hintergrundfarbe können in der [``config.properties``](src/main/resources/config.properties) geändert werden. Für größere Änderungen gibt es das Stylesheet [``style.css``](src/main/webapp/resources/css/styles.css). Das Aussehen der Tooltips lässt sich mit [``tipsy.css``](src/main/webapp/resources/css/tipsy.css) verändern.

------


<a name="DeployMaven"></a>
## Deploy mit Maven
Die Anwendung lässt sich direkt mit Maven und dem [Tomcat-Manager](http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html) deployen.
Hierzu müssen zwei Dateien abgeändert werden.

**tomcat-users.xml**<br/>
Linux-Pfad:<br/>
``/etc/tomcat7/tomcat-users.xml``<br/>
Windows-Pfad:<br/>
``%TOMCAT_PATH%\conf\tomcat-users.xml``<br/><br/>
Hier muss einem Nutzer (hier: 'admin') die Rolle 'manager-script' zugewiesen werden:<br/>
```
<role rolename="manager-script"/>
<user username="admin" password="testing" roles="manager-script"/>
```

<!--- ` -->

**pom.xml** im [Root-Verzeichnis](pom.xml#L141-L163)<br/>
Hier dann den entsprechenden Nutzer eintragen und die [Tomcat-URL anpassen](http://tomcat.apache.org/maven-plugin-trunk/tomcat7-maven-plugin/usage.html):
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

<!--- ` -->

Anschließend kann die Anwendung mit folgendem Skript deployed werden:<br/>
[``./build-deploy.sh``](build-deploy.sh) unter Linux bzw. [``build-deploy.bat``](build-deploy.bat) unter Windows.

------

## Quellcodedokumentation
<!--- TODO: -->
[Doxygen Dokumentation](http://kolbasa.github.io/ueps/doxygen/)

------

## Szenarien erstellen/bearbeiten
TODO :: TODO :: TODO :: TODO

### Aufgabengruppe erstellen
TODO :: TODO :: TODO :: TODO

### Aufgabe erstellen
TODO :: TODO :: TODO :: TODO

------

## Rollen und Rechte
Um den Zugriff auf die Funktionen von ÜPS zu kontrollen, wurden drei verschiedene Rollen eingeführt: Student, Dozent und Admin.

### Rolle 'Admin'
Der 'Admin' verfügt über umfassende Rechte im ganzen System:
- globale Benutzerverwaltung z.B. Bestimmung von Dozenten und deren Zuweisung zu Szenarien
- globale Verwaltung der Szenarien, sowie Rolle 'Dozent' in allen Szenarien

### Rolle 'Dozent'
Die Rolle 'Dozent' priviligiert einen Benutzer dazu folgende Funktionalitäten innerhalb eines Szenarios zu nutzen:
- Zuweisung von Rechten für Benutzer innerhalb des Szenarios
- Änderung des Szenarion
- Erstellung und Änderung von Übungsblättern und Übungsaufgaben des Szenarios
- Einsicht und Bewertung von abgebenen Lösungen innerhalb des Szenarios

### Rolle 'Student'
- Durchführung von Übungsaufgaben
- Einsicht in die Korrektur der eigenen Abgaben

------

## Abgaben bewerten
TODO :: TODO :: TODO :: TODO

------

## Screenshots

Startseite    | &nbsp;
:-----------: | :-----------:
Beschreibung und Auflistung <br/>der Übungsaufgaben | [![](http://kolbasa.github.io/ueps/screenshots/index-01-small.png)](http://kolbasa.github.io/ueps/screenshots/index-01.png)
Auflistung der Szenarien <br/>falls keine ``scenarioID`` per <br/>GET-Paramater übergeben wurde | [![](http://kolbasa.github.io/ueps/screenshots/index-02-small.png)](http://kolbasa.github.io/ueps/screenshots/index-02.png)
**Übungsbereich** | &nbsp;
Übungsbereich für die Studenten | [![](http://kolbasa.github.io/ueps/screenshots/task-01-small.png)](http://kolbasa.github.io/ueps/screenshots/task-01.png)
Anzeige des ER-Diagramms | [![Index](http://kolbasa.github.io/ueps/screenshots/task-02-small.png)](http://kolbasa.github.io/ueps/screenshots/task-02.png)
Anzeige von Tabellen | [![Index](http://kolbasa.github.io/ueps/screenshots/task-03-small.png)](http://kolbasa.github.io/ueps/screenshots/task-03.png)

#### Übungsbereich

[![Index](http://kolbasa.github.io/ueps/screenshots/task-03-small.png)](http://kolbasa.github.io/ueps/screenshots/task-03.png) [![Index](http://kolbasa.github.io/ueps/screenshots/task-04-small.png)](http://kolbasa.github.io/ueps/screenshots/task-04.png)

-

#### Rechteverwaltung
[![Index](http://kolbasa.github.io/ueps/screenshots/rights-01-small.png)](http://kolbasa.github.io/ueps/screenshots/rights-01.png) [![Index](http://kolbasa.github.io/ueps/screenshots/rights-02-small.png)](http://kolbasa.github.io/ueps/screenshots/rights-02.png)

-

#### Szenarien erstellen/bearbeiten
[![Index](http://kolbasa.github.io/ueps/screenshots/edit-scenario-01-small.png)](http://kolbasa.github.io/ueps/screenshots/edit-scenario-01.png) [![Index](http://kolbasa.github.io/ueps/screenshots/edit-scenario-02-small.png)](http://kolbasa.github.io/ueps/screenshots/edit-scenario-02.png)

-

#### Abgaben bewerten
[![Index](http://kolbasa.github.io/ueps/screenshots/submission-01-small.png)](http://kolbasa.github.io/ueps/screenshots/submission-01.png) [![Index](http://kolbasa.github.io/ueps/screenshots/submission-02-small.png)](http://kolbasa.github.io/ueps/screenshots/submission-02.png)

[![Index](http://kolbasa.github.io/ueps/screenshots/submission-03-small.png)](http://kolbasa.github.io/ueps/screenshots/submission-03.png) [![Index](http://kolbasa.github.io/ueps/screenshots/submission-04-small.png)](http://kolbasa.github.io/ueps/screenshots/submission-04.png)


