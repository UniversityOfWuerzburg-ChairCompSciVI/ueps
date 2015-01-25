package de.uniwue.info6.database.jdbc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ConnectionTools.java
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

import static de.uniwue.info6.misc.properties.PropString.SCENARIO_RESOURCES_PATH;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserResult;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserResultDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.webapp.admin.UserRights;

public class ConnectionTools extends Thread {

  private UserDao userDao;
  private final static String SCRIPT_PATH = Cfg.inst().getProp(MAIN_CONFIG,
      SCENARIO_RESOURCES_PATH);
  private static final Log LOGGER = LogFactory.getLog(ConnectionTools.class);

  private static final String dummyUser = "DEBUG_USER";
  private static final String STATS_DIR = "miscellaneous";
  private static final String SUB_DIR = "0";

  private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
    "HH:mm:ss");
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
    "dd.MM.yyyy");

  private static final SimpleDateFormat dateFileSession = new SimpleDateFormat(
    "yyyy_MM_dd");
  private static final SimpleDateFormat dateFileExecuter = new SimpleDateFormat(
    "yyyy_MM_dd-HH");

  private String executerStatsFile;
  private String sessionStatsFile;
  private String oldvalue = "";

  private static ConnectionTools instance;

  private Thread currentThread;

  /**
   *
   *
   * @return
   */
  public static ConnectionTools inst() {
    if (instance == null) {
      instance = new ConnectionTools();
    }
    return instance;
  }

  /**
   *
   */
  private ConnectionTools() {
    super();
    this.userDao = new UserDao();
  }


  /**
   * {@inheritDoc}
   *
   * @see Runnable#run()
   */
  @Override
  public void run() {
    this.currentThread = Thread.currentThread();
    while (!currentThread.isInterrupted()) {
      try {
        User user = userDao.getById(dummyUser);
        if (user == null) {
          user = new User();
          user.setId(dummyUser);
          user.setIsAdmin(false);
          userDao.insertNewInstance(user);
        }

        // add admin users if not found in database {{{
        String[] admins = new UserRights().initialize().getAdminsFromConfigFile();
        User lastAdminUser = null;
        for (String adminId : admins) {
          lastAdminUser = userDao.getById(adminId.trim());
          if (lastAdminUser == null) {
            lastAdminUser = new User();
            lastAdminUser.setId(adminId);
            lastAdminUser.setIsAdmin(true);
            userDao.insertNewInstance(lastAdminUser);
            System.err.println("INFO (ueps): Admin user with id: \"" + adminId + "\" added");
          }
        }
        // }}}

        // ------------------------------------------------ //
        // --
        // ------------------------------------------------ //

        // TODO: DEBUG
        boolean debugMode = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.DEBUG_MODE);

        if (debugMode) {
          this.addSomeTestData();
        }

        // log performance stats
        // quick and dirty log, do not use in a productive environment
        boolean logPerformance = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.LOG_PERFORMANCE);
        if (logPerformance) {
          // initStatsLog();

          // Map<Date, String> executerMap = SessionListener
          //                                 .getExecuterStats();
          // if (executerMap != null) {
          //   String executerOutput = hashMapToOutput(executerMap);
          //   logExecuterStats(executerOutput);
          //   executerMap.clear();
          // }

          // Map<Date, String> sessionMap = SessionListener
          //                                .getSessionStats();
          // if (sessionMap != null) {
          //   String sessionOutput = hashMapToOutput(sessionMap);
          //   logSessionStats(sessionOutput);
          //   sessionMap.clear();
          // }
        }

        LOGGER.info("AUTO RECONNECT TO DATABASE: " + new Date());

        Thread.sleep(1000 * 60 * 60);
      } catch (InterruptedException e) {
        break;
      } catch (NullPointerException e) {
        break;
      } catch (Exception e) {
        LOGGER.error("PROBLEM WITH RECONNECTING TO DATABASE", e);
      }
    }
  }

  /**
   *
   *
   */
  private void initStatsLog() {

    String date = dateFileExecuter.format(new Date());

    this.executerStatsFile = SCRIPT_PATH + File.separator + Cfg.RESOURCE_PATH
                             + File.separator + SUB_DIR + File.separator + STATS_DIR
                             + File.separator + "executer_log_" + date + "_00.csv";

    date = dateFileSession.format(new Date());
    this.sessionStatsFile = SCRIPT_PATH + File.separator + Cfg.RESOURCE_PATH
                            + File.separator + SUB_DIR + File.separator + STATS_DIR
                            + File.separator + "session_log_" + date + ".csv";

    File parent = new File(this.executerStatsFile).getParentFile();

    if (!parent.exists()) {
      parent.mkdir();
    }

    if (parent.exists() && !(new File(executerStatsFile).exists())) {
      logExecuterStats("date\ttime\ttype\tduration");
    }

    if (parent.exists() && !(new File(sessionStatsFile).exists())) {
      logSessionStats("date\ttime\tusercount");
    }
  }

  /**
   *
   *
   * @param string
   */
  private synchronized void logExecuterStats(String string) {
    if (string != null && !string.replaceAll("\\s", "").isEmpty()) {
      try (PrintWriter out = new PrintWriter(new BufferedWriter(
            new FileWriter(executerStatsFile, true)))) {
        out.println(string);
      } catch (Exception e) {
        LOGGER.error("COULD NOT LOG EXECUTER STATS.", e);
      }
    }
  }

  /**
   *
   *
   * @param string
   */
  private synchronized void logSessionStats(String string) {
    if (string != null && !string.replaceAll("\\s", "").isEmpty()) {
      try (PrintWriter out = new PrintWriter(new BufferedWriter(
            new FileWriter(sessionStatsFile, true)))) {
        out.println(string);
      } catch (Exception e) {
        LOGGER.error("COULD NOT LOG SESSION STATS.", e);
      }
    }
  }

  /**
   *
   *
   * @return
   */
  private String hashMapToOutput(Map<Date, String> map) {

    StringBuffer buffer = new StringBuffer();
    if (map != null) {
      for (Date date : map.keySet()) {
        String value = map.get(date).trim();
        if (!value.equals(oldvalue)) {
          buffer.append(dateFormat.format(date) + "\t"
                        + timeFormat.format(date) + "\t" + value + "\n");
          oldvalue = value;
        }
      }
    }
    if (buffer.length() > 0) {
      buffer.replace(buffer.length() - 1, buffer.length(), "");
    }
    return buffer.toString();
  }

  /**
   *
   *
   */
  public void cleanUp() {
    if (this.currentThread != null)
      this.currentThread.interrupt();
  }


  /**
   *
   *
   */
  public void addSomeTestData() {
    UserRightDao userRightDao = new UserRightDao();
    String[] testUsers = new String[] {"dozent_1", "dozent_2", "student_1", "student_2"};
    User exampleLecturer = null;
    User lastAdminUser = userDao.getRandom();
    for (String testUserId : testUsers) {
      User testUser = userDao.getById(testUserId.trim());
      if (testUser == null) {
        testUser = new User();
        testUser.setId(testUserId);

        // ------------------------------------------------ //
        if (testUserId.startsWith("dozent_")) {
          testUser.setIsLecturer(true);
        }
        userDao.insertNewInstance(testUser);
        // ------------------------------------------------ //
        if (testUserId.equals("dozent_1")) {
          exampleLecturer = testUser;
          Scenario scenario = new ScenarioDao().getById(1);
          if (scenario != null && testUser != null && lastAdminUser != null) {
            UserRight right = new UserRight(testUser, lastAdminUser, scenario, true, true, false);
            userRightDao.insertNewInstance(right);
          }
        }
        // ------------------------------------------------ //
        if (testUserId.equals("student_1")) {
          Scenario scenario = new ScenarioDao().getById(1);
          if (scenario != null && testUser != null && exampleLecturer != null) {
            UserRight right = new UserRight(testUser, exampleLecturer, scenario, true, true, true);
            userRightDao.insertNewInstance(right);
          }
        }
        // ------------------------------------------------ //
        if (testUserId.equals("student_2")) {
          Scenario scenario = new ScenarioDao().getById(1);
          if (scenario != null && testUser != null && exampleLecturer != null) {
            UserRight right = new UserRight(testUser, exampleLecturer, scenario, true, false, false);
            userRightDao.insertNewInstance(right);
          }
        }

        // ------------------------------------------------ //
        System.err.println("INFO (ueps): Test user with id: \"" + testUserId + "\" added");
      }
    }

    UserEntryDao userEntryDao = new UserEntryDao();
    UserResultDao userResultDao = new UserResultDao();
    ExerciseDao exerciseDao = new ExerciseDao();
    SolutionQueryDao solutionQueryDao = new SolutionQueryDao();

    for (int i = 1; i < 100; i++) {
      try {
        Exercise testExercise = exerciseDao.getById(i);
        if (testExercise != null) {
          UserEntry testEntry = new UserEntry();
          // testEntry.setExercise(exerciseDao.getById(4));
          testEntry.setExercise(testExercise);
          testEntry.setUser(userDao.getById("student_2"));
          testEntry.setUserQuery("SELECT title, author, price FROM books JOIN publishers ON publisher_id=publishers.id WHERE publishers.name=\"Carlsen\"");
          testEntry.setEntryTime(new Date());
          testEntry.setResultMessage("");
          userEntryDao.insertNewInstance(testEntry);

          UserResult testResult = new UserResult();
          testResult.setUserEntry(testEntry);
          testResult.setSolutionQuery(solutionQueryDao.getById(4));
          testResult.setCredits((byte) 0);
          testResult.setComment("");
          testResult.setLastModified(new Date());
          userResultDao.insertNewInstance(testResult);
        }
      } catch (Exception e) {
        // TODO: logging
        e.printStackTrace();
      }
    }
  }
}
