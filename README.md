# ÜPS
**ÜPS** (**Ü**bungs-**P**rogramm für **S**QL) ist ein webbasiertes Trainingssystem zum Erlernen der Datenbanksprache SQL.

![](http://kolbasa.github.io/ueps/screenshots/ueps_neu_02.png)

## Systemanforderungen
* Tomcat 7 oder höher
  (Für diese Anleitung wird Tomcat 7 verwendet)
* Maven 3 (zum Kompilieren)
* MySQL 5

## Kurzanleitung zur Installation

1. Quellcode herunterladen:<br/>
   ``git clone --depth=1 https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps``<br/>
   (Alternativ auch als [direkter Download](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/archive/master.zip))

2. In das ``ueps``-Verzeichnis wechseln.

3. Konfigurationsdatei in einem Editor öffnen: ``src/main/resources/config.properties``

4. Den Datenbank-Server und einen Nutzer festlegen:
   ```
   MASTER_DBHOST = 127.0.0.1
   MASTER_DBPORT = 3306
   MASTER_DBNAME = ueps_master
   MASTER_DBUSER = test_user
   MASTER_DBPASS = 3ti4k4tm270kg
   ```
  <!--- ` -->
   Der unter ``MASTER_DBUSER`` festgelegte Nutzer sollte folgende Rechte besitzen:
  ```
  SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP, GRANT OPTION, LOCK TABLES
  ```
  <!--- ` -->
  Die aufgelisteten Rechte müssen für die unter ``MASTER_DBNAME`` angegebene
  Datenbank sowie für neu erstellte Datenbanken gelten. Eine Rechteskript könnte
  beispielsweise folgendermaßen aussehen:
  ```
  GRANT SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP,
  GRANT OPTION, LOCK TABLES, ON *.* TO '$MASTER_DBUSER$'@'$MASTER_DBHOST$'
  IDENTIFIED BY '$MASTER_DBPASS$';
  ```
  <!--- ` -->

5. Anschließend kann die Anwendung kompiliert werden:
   * *Unter Linux*<br/>
     Zuerst müssen einige Build-Skripte im Wurzelverzeichnis ausführbar gemacht werden:<br/>
     ``chmod +x check-dependencies.sh build-deploy.sh``<br/>
     Dann einfach foldendes Skript ausführen<br/>
     ``./build-package.sh``<br/>

   * *Unter Windows*<br/>
     Einfach folgendes Skript auführen (Doppelklick genügt):<br/>
     ``build-package.bat``

6. Die kompilierte ``ueps.war``-Datei sollte jetzt deploy-fertig im Wurzelverzeichnis zu finden sein.

7. ÜPS lässt sich alternativ auch [direkt mit Maven deployen](#DeployMaven).

<br/>

Mit der Standard-Konfiguration werden beim Start der Anwendungen folgenden Datenbanken erstellt:
* ``ueps_master`` (festgelegt durch ``MASTER_DBNAME``)
* ``ueps_slave_001`` (Datenbank für das erste Beispielszenario - Amazon Buchdatenbank)
* ``ueps_slave_002`` (Datenbank für das zweite Beispielszenario - Fussball WM 2010)

Für die ``slave``-Datenbanken werden jeweils folgende Datenbanknutzer mit beschränkten Rechten erstellt:
* ``ueps_001``
* ``ueps_002``


## Konfiguration und Deploy

### Konfiguration mit 'config.properties'
Bevor man die Anwendung startet sollte man zunächst einen Blick in die Konfigurationsdatei
[config.properties](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/src/main/resources/config.properties)
unter ``src/main/resources/``
werfen und Angaben zum Datenbank-Server überschreiben.

#### Datenbankangaben
Angaben zur [administrativen Datenbank](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/src/main/resources/config.properties#L40-L44).<br/>
In dieser Datenbank werden alle Studentenabgaben und zugehörige Aufgaben gespeichert (siehe [ER-Diagramm](http://kolbasa.github.io/ueps/screenshots/admin-db-er-diagram.png)).


```
MASTER_DBHOST = 127.0.0.1
MASTER_DBPORT = 3306
MASTER_DBNAME = ueps_master
MASTER_DBUSER = test_user
MASTER_DBPASS = 3ti4k4tm270kg
```

Der Nutzer unter [MASTER_DBUSER](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/src/main/resources/config.properties#L43)
benötigt mindestens folgende
Rechte: ``SELECT, INSERT, UPDATE, DELETE`` für die Datenbank
angegeben durch [MASTER_DBNAME](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/src/main/resources/config.properties#L42).

Beispiel MySQL-Rechte-Skript:
```
GRANT SELECT, INSERT, UPDATE, DELETE ON $MASTER_DBNAME$.*
TO '$MASTER_DBUSER$'@'$MASTER_DBHOST$' IDENTIFIED BY '$MASTER_DBPASS$';
```

Ausgefüllt mit den oberen Beispiel-Daten:
```
GRANT SELECT, INSERT, UPDATE, DELETE ON ueps_master.*
TO 'test_user'@'127.0.0.1' IDENTIFIED BY '3ti4k4tm270kg';
```

#### Automatisierung der Installation
Die Option '[IMPORT_DB_IF_EMPTY](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/src/main/resources/config.properties#L57)'
ermöglicht die Automatisierung der Datenbank-Konfiguration. Dafür benötigt der unter
[MASTER_DBUSER](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/src/main/resources/config.properties#L43)
angegebene Nutzer zusätzlich noch ``ALTER, CREATE, DROP, LOCK TABLES``-Rechte.
Die Installation wird nur gestartet, wenn keine
Datenbank mit dem Namen [MASTER_DBNAME](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/src/main/resources/config.properties#L42)
gefunden wurde.

#### Nutzer-Authentifizierung
TODO :: TODO :: TODO :: TODO

#### Pfadangaben
TODO :: TODO :: TODO :: TODO

#### Aussehen anpassen
TODO :: TODO :: TODO :: TODO

#### Verschiedenes
TODO :: TODO :: TODO :: TODO

<a name="DeployMaven"></a>
### Deploy mit Maven
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

**pom.xml** im [Root-Verzeichnis](https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps/blob/master/pom.xml#L141-L163)<br/>
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

### Quellcodedokumentation
<!--- TODO: -->
[Doxygen Dokumentation](http://kolbasa.github.io/ueps/doxygen/)

## Neues Szenario erstellen
TODO :: TODO :: TODO :: TODO
![](http://kolbasa.github.io/ueps/screenshots/ueps_10.png)

### Aufgabengruppe erstellen
TODO :: TODO :: TODO :: TODO

### Aufgabe erstellen
TODO :: TODO :: TODO :: TODO

## Benutzerrechte hinzufügen/bearbeiten
TODO :: TODO :: TODO :: TODO
### Rolle 'Admin'
TODO :: TODO :: TODO :: TODO
### Rolle 'Dozent'
TODO :: TODO :: TODO :: TODO

## Abgaben bewerten
TODO :: TODO :: TODO :: TODO
