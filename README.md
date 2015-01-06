# ÜPS
**ÜPS** (**Ü**bungs-**P**rogramm für **S**QL) ist ein webbasiertes Trainingssystem zum Erlernen der Datenbanksprache SQL.

![](http://kolbasa.github.io/ueps/screenshots/ueps_neu_02.png)

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
  GRANT SELECT, INSERT, UPDATE, DELETE, ALTER,
  CREATE, DROP, GRANT OPTION, LOCK TABLES
  ```
  <!--- ` -->
  Die aufgelisteten Rechte müssen für die unter ``MASTER_DBNAME`` angegebene
  Datenbank sowie für neu erstellte Datenbanken gelten. Eine Rechteskript könnte
  beispielsweise folgendermaßen aussehen:
  ```
  GRANT SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP, GRANT OPTION,
  LOCK TABLES, ON *.* TO '$MASTER_DBUSER$'@'$MASTER_DBHOST$'
  IDENTIFIED BY '$MASTER_DBPASS$';
  ```
  <!--- ` -->

5. Kompilieren:
   * Unter Linux
     ``chmod +x *.sh``<br/>
     ``./build-package.sh``
   * Unter Windows
     ``./build-package.bat``

TODO :: TODO :: TODO :: TODO

## Systemanforderungen
* Tomcat 7 oder höher
* Maven (zum Kompilieren)
* MySQL

## Konfiguration und Deploy

### Deploy mit Maven
tomcat-users.xml<br/>
```
<role rolename="manager-script"/>
<user username="admin" password="testing" roles="manager-script"/>
```

<!--- ` -->

pom.xml
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

### Dokumentation
<!--- TODO: -->
[Doxygen Dokumentation](http://kolbasa.github.io/ueps/doxygen/)

## Neues Szenario erstellen
TODO :: TODO :: TODO :: TODO

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



## Screenshots
