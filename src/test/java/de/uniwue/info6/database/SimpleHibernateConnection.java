package de.uniwue.info6.database;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SimpleHibernateConnection.java
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

import static de.uniwue.info6.misc.properties.PropBool.FORCE_RESET_DATABASE;
import static de.uniwue.info6.misc.properties.PropBool.IMPORT_EXAMPLE_SCENARIO;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;
import de.uniwue.info6.database.gen.GenerateData;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.jdbc.ConnectionTools;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserResultDao;
import de.uniwue.info6.misc.FileTransfer;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.admin.UserRights;

public class SimpleHibernateConnection {

  @SuppressWarnings("unused")
  public static void main(String[] args) throws Exception {
    ConnectionManager pool = ConnectionManager.offline_instance();
    boolean resetDb = true;
    if (resetDb) {
      Cfg.inst().setProp(MAIN_CONFIG, IMPORT_EXAMPLE_SCENARIO, true);
      Cfg.inst().setProp(MAIN_CONFIG, FORCE_RESET_DATABASE, true);
      new GenerateData().resetDB();
      ConnectionTools.inst().addSomeTestData();
    }

    // main daos
    ScenarioDao scenarioDao = new ScenarioDao();
    ExerciseGroupDao exerciseGroupDao = new ExerciseGroupDao();
    ExerciseDao exerciseDao = new ExerciseDao();
    SolutionQueryDao solutionDao = new SolutionQueryDao();
    UserDao userDao = new UserDao();
    UserEntryDao userEntryDao = new UserEntryDao();
    UserResultDao userResultDao = new UserResultDao();

    // misc
    UserRights userRights = new UserRights().initialize();
    FileTransfer transfer = new FileTransfer();

    User randomUser = userDao.getRandom();

    if (randomUser != null) {
      // ------------------------------------------------ //
      System.err.println("INFO (ueps): Connection established");

      // example data
      Exercise ex = exerciseDao.getById(1);

      ExerciseGroup gr1 = exerciseGroupDao.getById(1);
      ExerciseGroup gr2 = exerciseGroupDao.getById(2);

      Scenario sc1 = scenarioDao.getById(1);
      Scenario sc2 = scenarioDao.getById(2);

      User admin = userDao.getById("user_1");

      // ------------------------------------------------ //

      // System.out.println(userResultDao.getLastUserResultFromEntry(userEntryDao.getRandom()));
      // System.out.println(userEntryDao.getLastEntriesForExercise(ex));

      // pool.addDB(sc2, true);
      // pool.setResetBarrier(false);
      // pool.resetDatabaseTables(sc2, null);
      // pool.setResetBarrier(true);

      // transfer.copy(gr1, sc1, false);
      // transfer.copy(sc2);

      // transfer.copy(ex, null, true);
      // transfer.copy(ex, gr1, true);

      // has editing rights
      // User user = userDao.getById("s213548");
      // List<UserEntry> userEntries =
      // userEntryDao.getLastUserEntryForAllUsers(ex);

      // ScenarioExporter exporter = new ScenarioExporter();
      // exporter.generateScenarioXml(sc1);

      // ------------------------------------------------ //
    }

  }

}
