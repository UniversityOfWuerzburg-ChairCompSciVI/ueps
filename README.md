# ÜPS
**ÜPS** (**Ü**bungs-**P**rogramm für **S**QL) ist ein webbasiertes Trainingssystem zum Erlernen der Datenbanksprache SQL.

![](res/screenshots/ueps_neu_02.png)
![](res/screenshots/ueps_neu_03.png)

## Kurzanleitung zur Installation

``git clone --depth=1 https://github.com/UniversityOfWuerzburg-ChairCompSciVI/ueps``


TODO :: TODO :: TODO :: TODO

## Systemanforderungen
* Tomcat 7 oder höher
* Maven (zum Kompilieren)
* MySQL

    ## Konfiguration und Deploy

    ### Deploy mit Maven
    tomcat-users.xml<br/>
    ``<role rolename="manager-script"/>``<br/>
    ``<user username="admin" password="testing" roles="manager-script"/>``

    pom.xml<br/>
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
