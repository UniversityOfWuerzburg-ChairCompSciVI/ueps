package de.uniwue.info6.database.gen;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  GenerateData.java
 * ************************************************************************
 * %%
 * Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static de.uniwue.info6.misc.properties.PropString.MASTER_DBHOST;
import static de.uniwue.info6.misc.properties.PropString.MASTER_DBNAME;
import static de.uniwue.info6.misc.properties.PropString.MASTER_DBPASS;
import static de.uniwue.info6.misc.properties.PropString.MASTER_DBPORT;
import static de.uniwue.info6.misc.properties.PropString.MASTER_DBUSER;
import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import static de.uniwue.info6.misc.properties.PropBool.DEBUG_MODE;
import static de.uniwue.info6.misc.properties.PropBool.FORCE_RESET_DATABASE;
import static de.uniwue.info6.misc.properties.PropBool.IMPORT_DB_IF_EMPTY;
import static de.uniwue.info6.misc.properties.PropBool.IMPORT_EXAMPLE_SCENARIO;
import static de.uniwue.info6.misc.properties.PropBool.LOG_BROWSER_HISTORY;
import static de.uniwue.info6.misc.properties.PropBool.USE_FALLBACK_USER;
import static de.uniwue.info6.misc.properties.PropInteger.SESSION_TIMEOUT;
import static de.uniwue.info6.misc.properties.PropString.SCENARIO_RESOURCES_PATH;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.net.URL;
import java.util.Date;
import java.util.List;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;

/**
 *
 *
 * @author Michael
 */
public class GenerateData {
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GenerateData.class);

  private ConnectionManager pool;
  private Cfg config;

  private static final String
  DUMMY_NAME                                = "DEBUG_SCENARIO_1211243",
  SLAVE_DB_NAME                             = "ueps_slave",
  SLAVE_DB_USER_NAME                        = "ueps";

  public static final String
  CREATE_SCRIPT_FILE                        = "admin_db_structure.sql",
  CREATE_SCRIPT_FILE_WITH_EXAMPLE_DATA      = "admin_db_example_data.sql";

  /**
   *
   *
   */
  public GenerateData() {
    this.pool = ConnectionManager.instance();
    this.config = Cfg.inst();
  }

  /**
   *
   *
   * @param dbHost
   * @param dbUser
   * @param dbName
   * @param dbPass
   * @return
   */
  private String getRightsScript(String dbHost, String dbUser, String dbName, String dbPass) {
    // TODO: in die main-config-file uebernehmen
    String addRightsScripts = StringTools.stripHtmlTags(Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.DESCRIPTION"));

    if (dbHost != null && !dbHost.isEmpty()) {
      addRightsScripts = addRightsScripts.replace("db_host", dbHost);
    }
    if (dbUser != null && !dbUser.isEmpty()) {
      addRightsScripts = addRightsScripts.replace("user_name", dbUser);
    }
    if (dbName != null && !dbName.isEmpty()) {
      addRightsScripts = addRightsScripts.replace("db_name", dbName);
    }
    if (dbPass != null) {
      addRightsScripts = addRightsScripts.replace("password", dbPass);
    }
    return addRightsScripts;
  }

  /**
   * reset admin database
   * @param insertExampleScenario
   * @param dropAndCreateDB
   *
   */
  public void resetDB() {
    URL scriptFileURL = this.getClass().getResource("/" + CREATE_SCRIPT_FILE);

    if (scriptFileURL == null) {
      LOGGER.error("MAIN SQL-SCRIPT NOT FOUND:\n\"" + CREATE_SCRIPT_FILE + "\"");
      return;
    } else {
      System.err.println("INFO (ueps): SQL-script found");
      System.err.println("INFO (ueps): Importing database");
    }

    Scenario dummyStructure = null;
    Scenario dummyData = null;

    boolean importExampleScenarios = Cfg.inst().getProp(MAIN_CONFIG, IMPORT_EXAMPLE_SCENARIO);

    try {
      String dbHost = "", dbUser = "", dbPass = "", dbPort = "", dbName = "";

      dbHost = this.config.getProp(MAIN_CONFIG, MASTER_DBHOST);
      dbUser = this.config.getProp(MAIN_CONFIG, MASTER_DBUSER);
      dbPass = this.config.getProp(MAIN_CONFIG, MASTER_DBPASS);
      dbPort = this.config.getProp(MAIN_CONFIG, MASTER_DBPORT);
      dbName = this.config.getProp(MAIN_CONFIG, MASTER_DBNAME);

      dummyStructure = new Scenario(DUMMY_NAME, new Date(), null, null, "",
                                    CREATE_SCRIPT_FILE,
                                    null, dbHost, dbUser, dbPass, dbPort, dbName);

      if (importExampleScenarios) {
        URL scriptFileWithDataURL = this.getClass().getResource("/" +
                                    CREATE_SCRIPT_FILE_WITH_EXAMPLE_DATA);

        if (scriptFileWithDataURL != null) {
          dummyData = new Scenario(DUMMY_NAME, new Date(), null, null, "",
                                   CREATE_SCRIPT_FILE_WITH_EXAMPLE_DATA,
                                   null, dbHost, dbUser, dbPass, dbPort, dbName);
        } else {
          LOGGER.error("DATA SQL-DUMP NOT FOUND:\n\"" +
                       CREATE_SCRIPT_FILE_WITH_EXAMPLE_DATA + "\"");
        }
      }

      pool.resetMasterDatabase(dummyStructure, dummyData);

    } catch (Exception e) {
      LOGGER.error("COULD NOT RESET ADMIN-DATABASE", e);
      e.printStackTrace();
    } finally {

      if (dummyStructure != null) {
        pool.removeDB(dummyStructure);
      }
      if (dummyData != null) {
        pool.removeDB(dummyData);
      }

      try {
        if (importExampleScenarios) {
          ScenarioDao dao = new ScenarioDao();
          List<Scenario> scenarios = dao.findAll();

          if (scenarios != null && !scenarios.isEmpty()) {
            for (Scenario sc : scenarios) {
              if (sc != null) {
                String dbName = pool.addScenarioDatabase(sc);
                String dbUser = dbName.replace(SLAVE_DB_NAME, SLAVE_DB_USER_NAME);
                String dbPass = StringTools.generatePassword(64, 32);
                String dbHost = config.getProp(MAIN_CONFIG, MASTER_DBHOST);
                String dbPort = config.getProp(MAIN_CONFIG, MASTER_DBPORT);
                String script = getRightsScript(dbHost, dbUser, dbName, dbPass);

                pool.editUserRights(script);

                sc.setDbHost(dbHost);
                sc.setDbUser(dbUser);
                sc.setDbPass(dbPass);
                sc.setDbPort(dbPort);
                sc.setDbName(dbName);

                dao.updateInstance(sc);
              }
            }
          }
        }

        pool.updateScenarios();
      } catch (Exception e) {
        LOGGER.error("COULD NOT RESET ADMIN-DATABASE", e);
        e.printStackTrace();
      }
    }
  }

}
