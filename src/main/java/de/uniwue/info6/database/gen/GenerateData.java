package de.uniwue.info6.database.gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserExTag;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserExTagDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.misc.properties.Cfg;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import de.uniwue.info6.misc.properties.Cfg;

/**
 *
 *
 * @author Michael
 */
public class GenerateData {

  private String scriptPath;
  private Cfg config;

  private static final String createScriptDir = "main_database" + File.separator;
  private static final String createScriptFile = createScriptDir + "backup_latest.sql";
  private static final String createScriptFileWithExample = createScriptDir
      + "backup_latest_with_data.sql";
  private static final String oldExercises = "0" + File.separator + "miscellaneous"
      + File.separator + "example_scenario.md";
  private static final Log LOGGER = LogFactory.getLog(GenerateData.class);
  private ConnectionManager pool;

  private static final String DUMMY = "DEBUG_SCENARIO_1211243";

  /**
   *
   *
   */
  public GenerateData() {
    this.pool = ConnectionManager.offline_instance();
    this.scriptPath = pool.getResourcePath();
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
   *
   */
  public void resetDB(boolean insertExampleScenario, boolean dropAndCreateDB) {
    File scriptFile = new File(scriptPath, "0" + File.separator + createScriptFile);
    if (!scriptFile.exists()) {
      System.err.println("ERROR: MAIN SQL-SCRIPT NOT FOUND: \"" + scriptFile + "\"");
      return;
    } else {
      System.err.println("INFO: SQL-SCRIPT FOUND");
    }

    Scenario dummy = null;
    try {
      String dbHost = "", dbUser = "", dbPass = "", dbPort = "", dbName = "";

      dbHost = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBHOST);
      dbUser = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBUSER);
      dbPass = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBPASS);
      dbPort = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBPORT);
      dbName = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBNAME);

      if (insertExampleScenario) {
        dummy = new Scenario(DUMMY, new Date(), null, null, "", createScriptFileWithExample, null,
            dbHost, dbUser, dbPass, dbPort, dbName);
      } else {
        dummy = new Scenario(DUMMY, new Date(), null, null, "", createScriptFile, null, dbHost,
            dbUser, dbPass, dbPort, dbName);
      }

      dummy.setId(0);
      if (dropAndCreateDB) {
        pool.addDB(dummy, true);
      } else {
        pool.addDB(dummy, false);
      }

    } catch (Exception e) {
      LOGGER.error("COULD NOT RESET ADMIN-DATABASE", e);
      e.printStackTrace();
    } finally {
      if (dummy != null) {
        pool.removeDB(dummy);
      }
      try {
        if (insertExampleScenario) {
          ScenarioDao dao = new ScenarioDao();
          List<Scenario> scenarios = dao.findAll();

          if (scenarios != null && !scenarios.isEmpty()) {
            for (Scenario sc : scenarios) {
              if (sc != null) {
                String dbHost = "", dbUser = "", dbPass = "", dbPort = "", dbName = "";
                boolean allowCreation = this.config.getProp(PropertiesFile.MAIN_CONFIG,
                    PropBool.ALLOW_DB_CREATION);

                dbHost = this.config.getProp(PropertiesFile.MAIN_CONFIG,
                    PropString.EXAMPLE_SCENARIO_DBHOST);
                dbUser = this.config.getProp(PropertiesFile.MAIN_CONFIG,
                    PropString.EXAMPLE_SCENARIO_DBUSER);
                dbPass = this.config.getProp(PropertiesFile.MAIN_CONFIG,
                    PropString.EXAMPLE_SCENARIO_DBPASS);
                dbPort = this.config.getProp(PropertiesFile.MAIN_CONFIG,
                    PropString.EXAMPLE_SCENARIO_DBPORT);
                dbName = this.config.getProp(PropertiesFile.MAIN_CONFIG,
                    PropString.EXAMPLE_SCENARIO_DBNAME);

                boolean hasUserData = dbHost != null && dbUser != null && dbPort != null
                    && dbName != null && !dbHost.trim().isEmpty() && !dbUser.trim().isEmpty()
                    && !dbPort.trim().isEmpty() && !dbName.trim().isEmpty();

                if (!hasUserData && allowCreation) {
                  dbName = pool.addScenarioDatabase(sc);
                  dbUser = "usr_" + dbName.replace("sqltsdb_", "");
                  dbPass = StringTools.generatePassword(64, 32);

                  dbHost = config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBHOST);
                  dbPort = config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBPORT);

                  String script = getRightsScript(dbHost, dbUser, dbName, dbPass);
                  pool.editUserRights(script);
                }

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
      } catch (Exception e) {
        LOGGER.error("COULD NOT RESET ADMIN-DATABASE", e);
        e.printStackTrace();
      }
      System.err.println("INFO: DATABASE RESET");
    }
  }

  /**
   *
   *
   */
  public void insertExampleScenario(boolean insertUserData) {
    File scriptF = new File(scriptPath, oldExercises);
    if (!scriptF.exists()) {
      System.err.println("ERROR: can't find scenario file : \"" + scriptF + "\"");
      return;
    } else {
      System.err.println("INFO: SCENARIO FILE FOUND");
    }

    // **********************************************************
    // scenario 1
    // **********************************************************

    ScenarioDao scenarioDao = new ScenarioDao();
    ExerciseGroupDao exgroupDao = new ExerciseGroupDao();
    ExerciseDao exerciseDao = new ExerciseDao();
    SolutionQueryDao solutionDao = new SolutionQueryDao();
    UserExTagDao userTagDao = new UserExTagDao();
    UserDao userDao = new UserDao();
    UserRightDao userRightDao = new UserRightDao();

    String description = "<h1>Amazon Warenhaus</h1>"
        + "<p>Sie fangen einen neuen Job im Amazon Warenhaus "
        + "an und m&#252;ssen an verschiedenen Stellen "
        + "Aushilfsarbeiten leisten. Zu ihren Aufgaben geh&#246;rt "
        + "das Auffinden von B&#252;chern, deren Verwaltung" + "und andere Korrekturarbeiten.</p>"
        + "<p>Dies erledigen Sie mit der Datenbanksprache SQL, in welche"
        + " Ihnen in den folgenden Kapiteln ein"
        + "n&#228;herer Einblick gew&#228;hrt werden soll. "
        + "Dazu z&#228;hlen neben dem Einpflegen neuer Daten in die "
        + "Datenbank ebenso die Aktualisierung vorhandener Daten"
        + " sowie das L&#246;schen nicht mehr relevanter Daten.<br/>"
        + "Ebenso werden Sie kennen lernen, wie Sie gew&#252;nschte"
        + " Informationen aus verschiedenen Tabellen "
        + "selektieren und aufbereiten k&#246;nnen.</p>";

    // example of an exercise-database
    String name = "SWT2014", dbHost = this.config.getProp(PropertiesFile.MAIN_CONFIG,
        PropString.EXAMPLE_SCENARIO_DBHOST), dbUser = this.config.getProp(
        PropertiesFile.MAIN_CONFIG, PropString.EXAMPLE_SCENARIO_DBUSER), dbPass = this.config
        .getProp(PropertiesFile.MAIN_CONFIG, PropString.EXAMPLE_SCENARIO_DBPASS), dbPort = this.config
        .getProp(PropertiesFile.MAIN_CONFIG, PropString.EXAMPLE_SCENARIO_DBPORT), dbName = this.config
        .getProp(PropertiesFile.MAIN_CONFIG, PropString.EXAMPLE_SCENARIO_DBNAME);

    String createScriptFile;
    User user = null;
    Scenario scenario = null;
    ExerciseGroup exgroup = null;
    Exercise exercise = null;
    SolutionQuery solution = null;
    UserExTag user_tag = null;

    // main scenario
    createScriptFile = "scenario_01.sql";
    scenario = new Scenario(name, new Date(), null, null, description, createScriptFile,
        "er-diagram.png", dbHost, dbUser, dbPass, dbPort, dbName);
    scenarioDao.insertNewInstance(scenario);

    try {
      pool.updateScenarios();
      pool.addDB(scenario, false);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.removeDB(scenario);
    }

    // **********************************************************
    // ex_group 1
    // **********************************************************

    if (insertUserData) {
      for (int i = 0; i < 10; i++) {
        user = new User("s21354" + String.valueOf(i), new Date());

        if (i == 9) {
          // set admin as example
          user.setIsAdmin(true);
        } else {
          user.setIsAdmin(false);
        }

        userDao.insertNewInstance(user);

        if (i == 6) {
          // has rating and editing rights
          UserRight right = new UserRight(user, null, scenario, true, true, true, true, true);
          userRightDao.insertNewInstance(right);
          Set<UserRight> rights = new HashSet<UserRight>();
          rights.add(right);
          user.setUserRights(rights);
        } else if (i == 7) {
          // has rating
          UserRight right = new UserRight(user, null, scenario, true, false, false, false, false);
          userRightDao.insertNewInstance(right);
          Set<UserRight> rights = new HashSet<UserRight>();
          rights.add(right);
          user.setUserRights(rights);
        } else if (i == 8) {
          // has editing rights
          UserRight right = new UserRight(user, null, scenario, false, true, true, true, true);
          userRightDao.insertNewInstance(right);
          Set<UserRight> rights = new HashSet<UserRight>();
          rights.add(right);
          user.setUserRights(rights);
        }

      }
    }

    // **********************************************************
    // ex_group 1
    // **********************************************************

    exgroup = new ExerciseGroup(scenario, null, "Select-Aufgaben", null, new Date(), null, null,
        false, null, null);
    exgroupDao.insertNewInstance(exgroup);

    // **********************************************************
    // exercises for ex_group 1
    // **********************************************************

    String question = null, sol = null;
    question = "Suchen Sie alle Titel jener Bücher deren Autor"
        + " George Orwell ist. Ordnen Sie die Titel nach Erscheinungsjahr abwärts.";
    exercise = new Exercise(exgroup, null, question, "Select", (byte) 2, new Date(), null, null,
        null, null, null);
    exerciseDao.insertNewInstance(exercise);

    // **********************************************************
    // user tags for exercise 1
    // **********************************************************

    if (insertUserData) {
      // public UserExTag(User user, Exercise exercise, Date lastModified,
      // String tag) {
      user_tag = new UserExTag(user, exercise, "order by", new Date());
      userTagDao.insertNewInstance(user_tag);
      user_tag = new UserExTag(user, exercise, "wichtig", new Date());
      userTagDao.insertNewInstance(user_tag);
    }

    // **********************************************************
    // solutions for exercise 1
    // **********************************************************

    // public SolutionQuery(Exercise exercise, String query) {
    sol = "SELECT title FROM books WHERE author=\'George Orwell\' ORDER By year DESC";
    solution = new SolutionQuery(exercise, sol);
    solutionDao.insertNewInstance(solution);

    // sol = "SELECT title FROM books";
    // solution = new SolutionQuery(exercise, sol);
    // solutionDao.insertNewInstance(solution);

    // **********************************************************
    // same procedure
    // **********************************************************

    question = "Bestimmen Sie den Titel des Buches mit der Signatur \"PF/286-A/3\" (ohne Anführungszeichen).";
    exercise = new Exercise(exgroup, null, question, "Select", (byte) 1, new Date(), null, null,
        null, null, null);
    exerciseDao.insertNewInstance(exercise);
    sol = "SELECT title FROM books WHERE signature=\'PF/286-A/3\'";
    solution = new SolutionQuery(exercise, sol);
    solutionDao.insertNewInstance(solution);

    question = "Zeigen Sie sämtliche Datensätze der Bücher-Tabelle "
        + "an und ordnen Sie diese nach Erscheinungsjahr abwärts.";
    exercise = new Exercise(exgroup, null, question, "Select", (byte) 1, new Date(), null, null,
        null, null, null);
    exerciseDao.insertNewInstance(exercise);

    // test fuer ein update:
    // exercise.setQuestion("Zeigen Sie sämtliche ...");
    // exerciseDao.insertNewInstance(exercise);

    if (insertUserData) {
      user_tag = new UserExTag(user, exercise, "wichtig2", new Date());
      userTagDao.insertNewInstance(user_tag);
    }

    // **********************************************************
    // UserEntry
    // **********************************************************

    // user_entry = new UserEntry(user, exercise, "Select * from books", new
    // Date());
    // userEntryDao.insertNewInstance(user_entry);

    sol = "SELECT * FROM books ORDER BY year DESC";
    solution = new SolutionQuery(exercise, sol);
    solutionDao.insertNewInstance(solution);

    ExerciseGroup exgroupUpdate = new ExerciseGroup(scenario, null, "Update-Aufgaben", null,
        new Date(), null, null, false, null, null);
    exgroupDao.insertNewInstance(exgroupUpdate);
    ExerciseGroup exgroupInsert = new ExerciseGroup(scenario, null, "Insert-Aufgaben", null,
        new Date(), null, null, false, null, null);
    exgroupDao.insertNewInstance(exgroupInsert);
    ExerciseGroup exgroupDelete = new ExerciseGroup(scenario, null, "Delete-Aufgaben", null,
        new Date(), null, null, false, null, null);
    exgroupDao.insertNewInstance(exgroupDelete);
    ExerciseGroup exgroupRated = new ExerciseGroup(scenario, null, "Übungsblatt 4 - Aufgabe 1",
        null, new Date(), null, null, true, null, null);
    exgroupDao.insertNewInstance(exgroupRated);

    String importScript = scriptPath + File.separator + oldExercises;

    try {
      Scanner scan = new Scanner(new FileReader(new File(importScript)));
      int counter = 0;

      while (scan.hasNextLine()) {

        String[] line = scan.nextLine().split("','");
        String type = line[0];
        String q = line[1];
        String s = line[2];
        String c = line[3];

        if (counter++ % 7 == 0) {
          exercise = new Exercise(exgroupRated, null, q, type, Byte.parseByte(c), new Date(), null,
              null, null, null, null);
          exerciseDao.insertNewInstance(exercise);
          solution = new SolutionQuery(exercise, s);
          solutionDao.insertNewInstance(solution);
        } else if (type.toLowerCase().equals("select")) {
          exercise = new Exercise(exgroup, null, q, type, Byte.parseByte(c), new Date(), null,
              null, null, null, null);
          exerciseDao.insertNewInstance(exercise);
          solution = new SolutionQuery(exercise, s);
          solutionDao.insertNewInstance(solution);
        } else if (type.toLowerCase().equals("update")) {
          exercise = new Exercise(exgroupUpdate, null, q, type, Byte.parseByte(c), new Date(),
              null, null, null, null, null);
          exerciseDao.insertNewInstance(exercise);
          solution = new SolutionQuery(exercise, s);
          solutionDao.insertNewInstance(solution);
        } else if (type.toLowerCase().equals("delete")) {
          exercise = new Exercise(exgroupDelete, null, q, type, Byte.parseByte(c), new Date(),
              null, null, null, null, null);
          exerciseDao.insertNewInstance(exercise);
          solution = new SolutionQuery(exercise, s);
          solutionDao.insertNewInstance(solution);
        } else if (type.toLowerCase().equals("insert")) {
          exercise = new Exercise(exgroupInsert, null, q, type, Byte.parseByte(c), new Date(),
              null, null, null, null, null);
          exerciseDao.insertNewInstance(exercise);
          solution = new SolutionQuery(exercise, s);
          solutionDao.insertNewInstance(solution);
        }
      }
      scan.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // // shitty example-scenario
    // createScriptFile = "scenario_01.sql";
    // scenario = new Scenario("SWT2015", new Date(), null, null,
    // "<h1>foo</h1><p>text...text...text</p>",
    // createScriptFile, null, dbHost, dbUser, dbPass, dbPort, "dev_ex2");
    // scenarioDao.insertNewInstance(scenario);
  }
}
