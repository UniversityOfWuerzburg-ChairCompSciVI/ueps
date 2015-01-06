package de.uniwue.info6.database.jdbc;

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

import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionListener;

public class ConnectionTools extends Thread {

  private UserDao dao;
  private final static String SCRIPT_PATH = Cfg.inst().getProp(MAIN_CONFIG,
      SCENARIO_RESOURCES_PATH);
  private static final Log LOGGER = LogFactory.getLog(ConnectionTools.class);

  private static final String dummyUser = "DEBUG_USER";
  private static final String STATS_DIR = "miscellaneous";
  private static final String RESOURCE_PATH = "scn";
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
    this.dao = new UserDao();
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
        User user = dao.getById(dummyUser);
        if (user == null) {
          user = new User();
          user.setId(dummyUser);
          user.setIsAdmin(false);
          dao.insertNewInstance(user);
        }

        // add admin users if not found in database {{{
        String[] admins = new UserRights().initialize().getAdminsFromConfigFile();
        for (String adminId : admins) {
          User adminUser = dao.getById(adminId.trim());
          if (adminUser == null) {
            adminUser = new User();
            adminUser.setId(adminId);
            adminUser.setIsAdmin(true);
            dao.insertNewInstance(adminUser);
            System.err.println("INFO (ueps): Admin user with id: \"" + adminId + "\" added");
          }
        }
        // }}}


        // TODO: DEBUG
        boolean debugMode = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.DEBUG_MODE);
        if (debugMode) {
          String[] test = new String[] {"dozent_1", "dozent_2", "student_1", "student_2"};
          for (String testUserId : test) {
            User testUser = dao.getById(testUserId.trim());
            if (testUser == null) {
              testUser = new User();
              testUser.setId(testUserId);
              if (testUserId.startsWith("dozent")) {
                testUser.setIsLecturer(true);
              }
              dao.insertNewInstance(testUser);
              System.err.println("INFO (ueps): Test user with id: \"" + testUserId + "\" added");
            }
          }
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

    this.executerStatsFile = SCRIPT_PATH + File.separator + RESOURCE_PATH
                             + File.separator + SUB_DIR + File.separator + STATS_DIR
                             + File.separator + "executer_log_" + date + "_00.csv";

    date = dateFileSession.format(new Date());
    this.sessionStatsFile = SCRIPT_PATH + File.separator + RESOURCE_PATH
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
}
