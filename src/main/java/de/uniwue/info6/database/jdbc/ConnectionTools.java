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
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.lists.ExerciseController;

public class ConnectionTools extends Thread {

  private UserDao userDao;
  private final static String SCRIPT_PATH = Cfg.inst().getProp(MAIN_CONFIG,
      SCENARIO_RESOURCES_PATH);
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ConnectionTools.class);

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

    // ------------------------------------------------ //
    // --
    // ------------------------------------------------ //

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
    boolean showcaseMode = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.SHOWCASE_MODE);

    if ((debugMode || showcaseMode) && (new UserEntryDao().findAll().size() < 20)) {
      this.addSomeTestData();
    }

    // ------------------------------------------------ //
    // --
    // ------------------------------------------------ //

    boolean logPerformance = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.LOG_PERFORMANCE);
    while (!currentThread.isInterrupted()) {
      try {
        userDao.getById(dummyUser);
        LOGGER.info("AUTO RECONNECT TO DATABASE: " + new Date());


        // TODO: log performance stats
        // quick and dirty log, do not use in a productive environment
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
        Thread.sleep(1000 * 60 * 60);
      } catch (NullPointerException | InterruptedException e) {
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
  public void addSomeTestStudents() {
    final ScenarioDao scenarioDao = new ScenarioDao();
    final ExerciseGroupDao groupDao = new ExerciseGroupDao();
    final ExerciseDao exerciseDao = new ExerciseDao();

    final List<Scenario> scenarios = scenarioDao.findAll();
    // ------------------------------------------------ //

    for (int i = 0; i < 20; i++) {
      String userID = "student_" + new Random().nextInt(10000);
      User userToInsert = new User();
      userToInsert.setId(userID);
      userToInsert.setIsAdmin(false);
      if (userDao.getById(userID) == null) {
        userDao.insertNewInstance(userToInsert);
      }
    }

    final Random random = new Random();
    for (Scenario scenario : scenarios) {
      // ------------------------------------------------ //
      for (ExerciseGroup group : groupDao.findByScenario(scenario)) {
        if (group.getIsRated()) {
          List<Exercise> exercises = exerciseDao.findByExGroup(group);

          // ------------------------------------------------ //
          for (Exercise exercise : exercises) {
            for (int i = 0; i < random.nextInt(20); i++) {
              User user = userDao.getRandom();
              if (!user.getId().equals(dummyUser)) {
                try {
                  List<SolutionQuery> solutions = new ExerciseDao().getSolutions(exercise);
                  String solution = solutions.get(0).getQuery();

                  if (random.nextInt(3) == 2) {
                    solution = StringTools.forgetOneWord(solution);
                  }
                  ConnectionManager.instance();
                  ExerciseController exc = new ExerciseController().init_debug(scenario, exercise,
                      user);
                  exc.setUserString(solution);
                } catch (Exception e) {
                } finally {
                }
              }
            }
          }
          // ------------------------------------------------ //
        }
      }
    }

  }



  /**
   *
   *
   */
  public void addSomeTestData() {
    final UserRightDao userRightDao = new UserRightDao();

    // ------------------------------------------------ //

    String adminId1 = Cfg.DEMO_ADMIN + "1", adminId2 = Cfg.DEMO_ADMIN + "2";
    User admin1 = userDao.getById(adminId1);
    User admin2 = userDao.getById(adminId2);

    if (admin1 == null) {
      admin1 = new User();
      admin1.setId(adminId1);
      admin1.setIsAdmin(true);
      this.userDao.insertNewInstance(admin1);
    }
    if (admin2 == null) {
      admin2 = new User();
      admin2.setId(adminId2);
      admin2.setIsAdmin(true);
      this.userDao.insertNewInstance(admin2);
    }

    // ------------------------------------------------ //

    String lecturerId1 = Cfg.DEMO_LECTURER + "1", lecturerId2 = Cfg.DEMO_LECTURER + "2";
    User lecturer1 = userDao.getById(lecturerId1);
    User lecturer2 = userDao.getById(lecturerId2);

    if (lecturer1 == null) {
      lecturer1 = new User();
      lecturer1.setId(lecturerId1);
      lecturer1.setIsLecturer(true);
      this.userDao.insertNewInstance(lecturer1);

      Scenario scenario = new ScenarioDao().getById(1);
      if (scenario != null && admin1 != null) {
        UserRight right = new UserRight(lecturer1, admin1, scenario, true, true, false);
        userRightDao.insertNewInstance(right);
      }
    }

    if (lecturer2 == null) {
      lecturer2 = new User();
      lecturer2.setId(lecturerId2);
      lecturer2.setIsLecturer(true);
      this.userDao.insertNewInstance(lecturer2);
    }

    // ------------------------------------------------ //

    String studentId1 = Cfg.DEMO_STUDENT + "1", studentId2 = Cfg.DEMO_STUDENT + "2";
    User student1 = userDao.getById(studentId1);
    User student2 = userDao.getById(studentId2);

    if (student1 == null) {
      student1 = new User();
      student1.setId(studentId1);
      this.userDao.insertNewInstance(student1);

      Scenario scenario = new ScenarioDao().getById(1);
      if (scenario != null && student1 != null && lecturer1 != null) {
        UserRight right = new UserRight(student1, lecturer1, scenario, true, true, true);
        userRightDao.insertNewInstance(right);
      }
    }
    if (student2 == null) {
      student2 = new User();
      student2.setId(studentId2);
      this.userDao.insertNewInstance(student2);

      Scenario scenario = new ScenarioDao().getById(2);
      if (scenario != null && student2 != null && admin1 != null) {
        UserRight right = new UserRight(student2, admin1, scenario, true, true, true);
        userRightDao.insertNewInstance(right);
      }
    }

    // ------------------------------------------------ //

    this.addSomeTestStudents();
  }
}
