package de.uniwue.info6.database.jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import de.uniwue.info6.database.gen.ScriptRunner;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.misc.OldPropertiesManager;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.misc.properties.PropertiesManager;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
public class ConnectionManager implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final Log LOGGER = LogFactory.getLog(ConnectionManager.class);

  // @formatter:off
  private static final String
    RESOURCE_PATH     = "/scn/",
    DRIVER            = "org.mariadb.jdbc.Driver",
    URL_PREFIX        = "jdbc:mariadb://";
  // @formatter:on

  private String scriptPath;

  private HashMap<Scenario, ArrayList<String>> scenarioScripts, scenarioTables,
      scenarioTablesWithHash;
  private HashMap<Scenario, JdbcTemplate> pools;
  private HashMap<Scenario, String> errors;
  private ArrayList<Scenario> hasForeignKeys, originalTableDeleted;

  private String resourcePath;
  private SessionObject ac;
  private ScenarioDao scenarioDao;

  private DriverManagerDataSource adminDataSource;

  private PropertiesManager config;

  private static ConnectionManager instance;

  // -----------------------------------------------------------------------
  // initialize
  // -----------------------------------------------------------------------

  /**
   *
   *
   * @return
   *
   * @throws FileNotFoundException
   * @throws SQLException
   * @throws IOException
   */
  public static synchronized ConnectionManager instance() {
    if (instance == null) {
      instance = new ConnectionManager();
      try {
        instance.updateScenarios();
      } catch (Exception e) {
        LOGGER.error("UPDATING SCENARIOS FAILED", e);
      }
    }
    return instance;
  }

  /**
   *
   *
   * @return
   *
   * @throws FileNotFoundException
   * @throws SQLException
   * @throws IOException
   */
  public static synchronized ConnectionManager offline_instance() {
    if (instance == null) {
      // if (scriptPath == null) {
      // System.out.println("OFFLINE ZUGRIFF AUF DIE DATENBANK FUER TEMPORAERE TABELLEN...\n\n");
      OldPropertiesManager pr = OldPropertiesManager.instance();
      pr.loadProperties("config.properties").loadProperties("text_de.properties");
      // }
      instance = new ConnectionManager();
    }
    return instance;
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#SystemProperties()
   */
  private ConnectionManager() {

    this.config = PropertiesManager.inst();
    this.scriptPath = this.config
        .getProp(PropertiesFile.MAIN_CONFIG, PropString.SCENARIO_RESOURCES);
    this.scriptPath = StringTools.shortenUnixHomePathReverse(this.scriptPath);

    this.pools = new HashMap<Scenario, JdbcTemplate>();
    this.errors = new HashMap<Scenario, String>();
    this.ac = new SessionCollector().getSessionObject();

    this.scenarioScripts = new HashMap<Scenario, ArrayList<String>>();
    this.scenarioTablesWithHash = new HashMap<Scenario, ArrayList<String>>();
    this.scenarioTables = new HashMap<Scenario, ArrayList<String>>();
    this.hasForeignKeys = new ArrayList<Scenario>();
    this.originalTableDeleted = new ArrayList<Scenario>();
    this.scenarioDao = new ScenarioDao();

    // getting resource path
    File rootPath = new File(scriptPath);
    if (rootPath.exists() && rootPath.isDirectory() && rootPath.canWrite()) {
      File resource = new File(scriptPath + File.separator + RESOURCE_PATH);
      if (!resource.exists()) {
        resource.mkdir();
      }
      resourcePath = resource.getAbsolutePath();
    } else {
      try {
        throw new FileNotFoundException("RESOURCE PATH NOT FOUND (OR INSUFFICIENT RW-PERMISSIONS)!");
      } catch (FileNotFoundException e) {
        LOGGER.error(e);
        e.printStackTrace();
      }
    }
  }

  // -----------------------------------------------------------------------
  // deleting tables from the database
  // -----------------------------------------------------------------------

  /**
   * @throws SQLException
   *
   *
   */
  public void resetAllScenarioTables() throws SQLException {
    List<Scenario> scenarios = scenarioDao.findAll();
    if (scenarios != null) {
      for (Scenario scenario : scenarios) {
        resetDatabaseTablesForUser(scenario, null, null);
      }
    }
  }

  /**
   *
   *
   * @param scenario
   * @param user
   *
   * @throws SQLException
   */
  public void resetDatabaseTablesForUser(Scenario scenario, User user) throws SQLException {
    resetDatabaseTablesForUser(scenario, user, null);
  }

  /**
   *
   *
   * @param scenario
   *
   * @throws SQLException
   */
  public void resetDatabaseTablesForUser(Scenario scenario, User user, List<String> tablesToDelete)
      throws SQLException {
    Connection connection = null;

    if (scenario != null) {
      try {
        JdbcTemplate template = instance.getTemplate(scenario);
        String dbName = scenario.getDbName();

        List<String> tables = new LinkedList<String>();

        connection = template.getDataSource().getConnection();
        connection.setCatalog(dbName);

        ResultSet result = null;
        Statement statement = null;
        try {
          statement = connection.createStatement();
          String showTables = "SHOW TABLES";
          if (user != null) {
            showTables += " LIKE '" + user.getId() + "_%'";
          }
          showTables += ";";
          result = statement.executeQuery(showTables);

          result.beforeFirst();
          while (result.next()) {
            tables.add(result.getString(1));
          }
        } catch (Exception ex) {
          LOGGER.error("PROBLEMS EXECUTING 'SHOW TABLES'", ex);
        } finally {
          if (result != null) {
            result.close();
            result = null;
          }
          if (statement != null) {
            statement.close();
            statement = null;
          }
        }

        try {
          statement = connection.createStatement();
          statement.execute("SET FOREIGN_KEY_CHECKS = 0;");

          for (String table : tables) {
            statement = connection.createStatement();

            if (tablesToDelete != null && !tablesToDelete.isEmpty()
                && !hasForeignKeys.contains(scenario)) {
              for (String tableToDelete : tablesToDelete) {
                if ((user != null && table.equalsIgnoreCase(user.getId() + "_" + tableToDelete))
                    || table.equalsIgnoreCase(tableToDelete)) {
                  // droping changed table
                  statement.execute("UNLOCK TABLES;");
                  statement.execute("DROP TABLE IF EXISTS `" + table + "`;");
                }
              }
            } else {
              statement.execute("UNLOCK TABLES;");
              statement.execute("DROP TABLE IF EXISTS `" + table + "`;");
              // System.err.println("INFO: DROPPING TABLE AT STARTUP: \"" +
              // table + "\"");
            }
          }
          statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (Exception ex) {
          LOGGER.error("PROBLEMS EXECUTING 'DROP TABLE'", ex);
        } finally {
          if (statement != null) {
            statement.close();
          }
          if (result != null) {
            result.close();
          }
        }
      } catch (Exception e) {
        errors.put(scenario, e.getMessage());
      } finally {
        if (connection != null) {
          connection.close();
        }
      }
    }
  }

  // -----------------------------------------------------------------------
  // create scenario databases and user rights
  // -----------------------------------------------------------------------

  /**
   *
   *
   * @param scenario
   * @param script
   * @return
   * @throws SQLException
   */
  public synchronized boolean editUserRights(String script) throws SQLException {
    if (script != null && !script.isEmpty()) {
      try {
        if (adminDataSource == null) {
          createAdminDataSource();
        }

        if (adminDataSource != null) {
          JdbcTemplate template = new JdbcTemplate(adminDataSource);
          String[] statements = script.split(";");
          for (int i = 0; i < statements.length; i++) {
            if (!statements[i].trim().isEmpty()) {
              template.execute(statements[i].trim());
            }
          }
          return true;
        } else {
          throw new SQLException("CAN'T GRANT USER RIGHTS, DATABASE NOT FOUND");
        }
      } catch (Exception e) {
        throw new SQLException("COULD NOT CONNECT TO ADMIN DATABASE: \n" + "[" + script + "]"
            + "\n\n" + ExceptionUtils.getStackTrace(e));
      }
    }
    return false;
  }

  /**
   *
   *
   */
  public boolean createAdminDataSource() throws Exception {
    String dbHost = "", dbUser = "", dbPass = "", dbPort = "", url = "";

    dbHost = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBHOST);
    dbUser = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBUSER);
    dbPass = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBPASS);
    dbPort = this.config.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBPORT);

    Boolean allowCreation = this.config.getProp(PropertiesFile.MAIN_CONFIG,
        PropBool.ALLOW_DB_CREATION);

    if (!allowCreation) {
      return false;
    }

    // admin-database
    adminDataSource = new DriverManagerDataSource();

    url = url + URL_PREFIX + dbHost + ":" + dbPort
        + "?useUnicode=true&characterEncoding=UTF8&autoReconnect=true";

    adminDataSource.setDriverClassName(DRIVER);
    adminDataSource.setUrl(url);
    adminDataSource.setUsername(dbUser);

    if (dbPass != null && !dbPass.isEmpty()) {
      adminDataSource.setPassword(dbPass);
    }
    return true;
  }

  /**
   *
   *
   * @param scenario
   * @return
   * @throws SQLException
   */
  public synchronized String addScenarioDatabase(Scenario scenario) throws SQLException {
    if (scenario != null) {
      String dbHost = "", dbUser = "", dbPass = "", dbPort = "", url = "", dbName = "";
      Connection connection = null;
      ResultSet resultSet = null;
      try {
        JdbcTemplate template = null;

        if (adminDataSource == null) {
          createAdminDataSource();
        }

        if (adminDataSource != null) {
          template = new JdbcTemplate(adminDataSource);

          if (template != null) {
            try {
              connection = template.getDataSource().getConnection();
            } catch (Exception e) {
              throw new SQLException(e);
            }
          }

          dbName = "sqltsdb_"
              + StringTools.normalize(StringTools.trimToLength(scenario.getName(), 4) + "_"
                  + StringTools.zeroPad(1, 3));

          resultSet = connection.getMetaData().getCatalogs();
          ArrayList<String> dbNames = new ArrayList<String>();

          while (resultSet.next()) {
            dbNames.add(resultSet.getString(1).toLowerCase().trim());
          }

          int count = 1;
          while (dbNames.contains(dbName)) {
            try {
              count = Integer.parseInt(dbName.substring(dbName.length() - 3, dbName.length()));
              dbName = dbName.substring(0, dbName.length() - 3) + StringTools.zeroPad(++count, 3);
            } catch (NumberFormatException e) {
              dbName = dbName + "_" + new Random().nextInt(10000);
            }
          }
          template.execute("CREATE DATABASE IF NOT EXISTS `" + dbName + "`;");
          return dbName;
        } else {
          throw new SQLException();
        }
      } catch (Exception e) {
        throw new SQLException("could not connect to admin database: " + "[" + dbHost + "]" + "["
            + dbUser + "]" + "[" + dbPass + "]" + "[" + dbPort + "]" + "[" + url + "]", e);
      } finally {
        try {
          if (resultSet != null) {
            resultSet.close();
          }
          if (connection != null) {
            connection.close();
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  /**
   *
   *
   * @param scenario
   *
   * @throws SQLException
   */
  private void removeScenarioDatabase(Scenario scenario) throws SQLException {
    if (scenario != null) {
      try {
        if (adminDataSource == null) {
          createAdminDataSource();
        }
        if (adminDataSource != null) {
          LOGGER.info("INFO: FORCE DROP AND CREATE SCENARIO DATABASE");

          JdbcTemplate template = new JdbcTemplate(adminDataSource);
          if (template != null) {
            String dbName = scenario.getDbName();
            if (dbName != null) {
              template.execute("DROP DATABASE IF EXISTS `" + dbName + "`;");
            }
          }
        }
      } catch (Exception e) {
        errors.put(scenario, e.getMessage());
      }
    }
  }

  // -----------------------------------------------------------------------
  // fill scenario databases
  // -----------------------------------------------------------------------

  /**
   *
   *
   * @param scenario
   * @return
   */
  public String checkIfImportScriptExists(Scenario scenario) {
    String dbScript = scenario.getCreateScriptPath();
    if (dbScript != null) {
      File sqlScript = new File(resourcePath + File.separator + scenario.getId(), dbScript);
      if (!sqlScript.exists()) {
        String er = ExceptionUtils.getStackTrace(new FileNotFoundException(sqlScript
            .getAbsolutePath()));

        if (er.length() > 500) {
          return er.substring(0, 500) + " [...]";
        } else {
          return er;
        }

      }
    }
    return null;
  }

  /**
   *
   *
   * @param scenario
   * @param db
   * @throws SQLException
   * @throws IOException
   * @throws FileNotFoundException
   */
  public synchronized String addDB(Scenario scenario, boolean resetDB) throws SQLException,
      FileNotFoundException, IOException {
    if (scenario == null) {
      LOGGER.error("ADDED SCENARIO IS NULL");
    } else {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName(DRIVER);

      String url = URL_PREFIX + scenario.getDbHost();
      String port = scenario.getDbPort();
      String password = scenario.getDbPass();

      // port is optional
      if (port != null && !port.isEmpty()) {
        url = url + ":" + port;
      }
      url = url + "?useUnicode=true&characterEncoding=UTF8&autoReconnect=true";
      // url = url + "?useUnicode=true&characterEncoding=UTF8";

      dataSource.setUrl(url);
      dataSource.setUsername(scenario.getDbUser());

      if (password != null && !password.isEmpty()) {
        dataSource.setPassword(password);
      }

      JdbcTemplate template = new JdbcTemplate(dataSource);
      if (template != null) {
        pools.put(scenario, template);
      }

      if (resetDB) {
        resetDatabaseRoot(scenario);
      }

      // parse import-scripts
      String error = "";
      if (scenario != null) {
        Connection connection = null;
        StringWriter swError = new StringWriter();
        StringWriter swLog = new StringWriter();

        try {

          connection = instance.getConnection(scenario);
          String dbScript = scenario.getCreateScriptPath();
          ScriptRunner sc = new ScriptRunner(connection, false, true);

          PrintWriter pwLog = new PrintWriter(swLog);
          sc.setLogWriter(pwLog);

          PrintWriter pwError = new PrintWriter(swError);
          sc.setErrorLogWriter(pwError);

          File sqlScript = new File(resourcePath + File.separator + scenario.getId(), dbScript);
          File sqlScriptOriginal = new File(resourcePath + File.separator + "0", dbScript);

          if (!sqlScript.exists() && sqlScriptOriginal.exists()) {
            if (!sqlScript.getParentFile().exists()) {
              sqlScript.getParentFile().mkdirs();
            }
            FileUtils.copyFile(sqlScriptOriginal, sqlScript);
          }

          if (sqlScript.exists()) {
            sc.runScript(new FileReader(sqlScript), true);
            ArrayList<String> commands = sc.getCommands();

            if (!commands.isEmpty()) {
              scenarioScripts.put(scenario, commands);
            }
            for (String command : commands) {
              addUserPrefix(command, scenario, null);
            }
          }
        } catch (Exception e) {
          error = swError.toString();
          if (error.isEmpty()) {
            String er = ExceptionUtils.getStackTrace(e);
            if (er.length() > 500) {
              error = System.getProperty("ERROR.UNEXPECTED") + ": \n" + er.substring(0, 500)
                  + " [...]";
            } else {
              error = System.getProperty("ERROR.UNEXPECTED") + ": \n" + er;
            }
          }
          LOGGER.error(error, e);
          if (!errors.containsKey(scenario)) {
            errors.put(scenario, error);
          }
          return error;
        } finally {
          if (connection != null) {
            try {
              connection.close();
            } catch (SQLException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * @throws IOException
   * @throws SQLException
   * @throws FileNotFoundException
   */
  public synchronized void updateScenarios() throws FileNotFoundException, SQLException,
      IOException {
    List<Scenario> scenarios = scenarioDao.findAll();
    errors = new HashMap<Scenario, String>();

    if (scenarios != null) {
      // add scenarios if necessary
      for (Scenario sc : scenarios) {
        // get import-script
        File scDir = new File(resourcePath, String.valueOf(sc.getId()));
        if (!scDir.exists()) {
          scDir.mkdir();
        }

        if (!pools.containsKey(sc)) {
          instance.addDB(sc, false);
        }
      }

      List<Scenario> scenariosToRemove = new ArrayList<Scenario>();

      // remove scenarios if necessary
      for (Scenario sc : pools.keySet()) {
        if (!scenarios.contains(sc)) {
          scenariosToRemove.add(sc);
        }
      }
      for (Scenario sc : scenariosToRemove) {
        removeScenario(sc, true);
      }
    }
  }

  /**
   *
   *
   * @param scenario
   */
  public void removeScenario(Scenario scenario, boolean deleteDatabase) {
    if (scenario != null && pools != null) {
      if (ac != null) {
        Scenario currentScenario = ac.getScenario();
        if (scenario.equals(currentScenario)) {
          ac.setScenario(null);
        }
      }

      if (pools.containsKey(scenario)) {
        pools.remove(scenario);
      }

      if (scenarioScripts.containsKey(scenario)) {
        scenarioScripts.remove(scenario);
      }

      if (scenarioTables.containsKey(scenario)) {
        scenarioTables.remove(scenario);
      }

      if (scenarioTablesWithHash.containsKey(scenario)) {
        scenarioTablesWithHash.remove(scenario);
      }

      if (hasForeignKeys.contains(scenario)) {
        hasForeignKeys.remove(scenario);
      }

      if (originalTableDeleted.contains(scenario)) {
        originalTableDeleted.remove(scenario);
      }

      if (errors.containsKey(scenario)) {
        errors.remove(scenario);
      }

      if (deleteDatabase) {
        try {
          removeScenarioDatabase(scenario);
        } catch (Exception e) {
        }
      }
    }
  }

  /**
   *
   *
   * @param scenario
   * @return
   *
   * @throws SQLException
   */
  public synchronized Connection getConnection(Scenario scenario) throws SQLException {
    Connection connection = null;
    if (scenario != null) {
      if (pools.containsKey(scenario)) {
        try {
          JdbcTemplate templ = pools.get(scenario);
          connection = templ.getDataSource().getConnection();
          connection.setCatalog(scenario.getDbName());
        } catch (Exception exception) {
          errors.put(scenario, exception.getMessage());
          throw exception;
        }
      }
    }
    return connection;
  }

  /**
   *
   *
   * @param scenario
   * @return
   *
   * @throws SQLException
   */
  public synchronized JdbcTemplate getTemplate(Scenario scenario) throws SQLException {
    JdbcTemplate templ = null;
    if (scenario != null) {
      if (pools.containsKey(scenario)) {
        templ = pools.get(scenario);
        templ.setDatabaseProductName(scenario.getDbName());
      }
    }
    return templ;
  }

  /**
   *
   *
   * @param scenario
   */
  public synchronized void removeDB(Scenario scenario) {
    if (pools.containsKey(scenario)) {
      JdbcTemplate temp = pools.get(scenario);
      temp.setDataSource(null);
      pools.remove(scenario);
    }
  }

  /**
   *
   *
   * @param scenario
   * @param user
   * @param table
   * @return
   *
   * @throws SQLException
   */
  public synchronized String getTableChecksum(Scenario scenario, User user, String table)
      throws SQLException {
    if (scenario != null) {
      Connection connection = null;
      ResultSet resultSet = null;
      Statement statement = null;
      try {
        connection = instance.getConnection(scenario);
        statement = connection.createStatement();

        if (user == null) {
          statement.execute("CHECKSUM TABLE " + table);
        } else {
          statement.execute("CHECKSUM TABLE `" + user.getId().toLowerCase().trim() + "_" + table
              + "`");
        }
        resultSet = statement.getResultSet();

        if (resultSet.next()) {
          return resultSet.getString(2);
        }

      } catch (Exception e) {
        LOGGER.error("PROBLEM GETTING TABLE CHECKSUM", e);
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          try {
            connection.close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return null;
  }

  /**
   * @throws SQLException
   * @throws IOException
   * @throws FileNotFoundException
   *
   *
   */
  public synchronized void resetTables(Scenario scenario, User user) throws SQLException,
      FileNotFoundException, IOException {
    long starttime = System.currentTimeMillis();

    if (!originalTableDeleted.contains(scenario)) {
      resetDatabaseTablesForUser(scenario, null, getScenarioTableNames(scenario));
      originalTableDeleted.add(scenario);
    }

    if (scenario != null && user != null) {
      Connection connection = null;
      Statement statement = null;
      try {
        List<String> changedTables = checkSumChanged(scenario, user);
        List<String> temp = getScenarioTableNames(scenario);
        List<String> unchangedTables = new ArrayList<String>();

        if (changedTables != null) {

          if (!scenarioTablesWithHash.isEmpty()) {
            resetDatabaseTablesForUser(scenario, user, changedTables);
          }

          if (temp != null) {
            for (String table : temp) {
              if (!changedTables.contains(table)) {
                unchangedTables.add(table);
              }
            }
          }
          connection = this.getConnection(scenario);
          if (connection != null) {
            statement = connection.createStatement();
            // LOGGER.error("main-url: " + connection.getMetaData());
            ArrayList<String> commands = scenarioScripts.get(scenario);
            if (commands != null) {
              for (String command : commands) {
                command = command.trim();
                String commandWithUserPrefix = addUserPrefix(command, scenario, user);

                if (commandWithUserPrefix != null) {
                  statement.execute("SET FOREIGN_KEY_CHECKS = 0;");
                  if (changedTables.isEmpty() || hasForeignKeys.contains(scenario)) {
                    statement.execute(commandWithUserPrefix);
                  } else {
                    boolean containsUnchangedTable = false;
                    boolean containsChangedTable = false;

                    for (String unchangedTable : unchangedTables) {
                      if (commandWithUserPrefix.contains(unchangedTable)) {
                        containsUnchangedTable = true;
                      }
                    }
                    for (String changedTable : changedTables) {
                      if (commandWithUserPrefix.contains(changedTable)) {
                        containsChangedTable = true;
                      }
                    }
                    // if (containsChangedTable || !containsUnchangedTable) {
                    if (containsChangedTable || !containsUnchangedTable) {
                      try {
                        statement.execute(commandWithUserPrefix);
                      } catch (Exception e) {

                      }
                    }
                  }
                  statement.execute("SET FOREIGN_KEY_CHECKS = 1;");
                }
              }
            } else {
              String fileNotFound = "\n" + checkIfImportScriptExists(scenario);
              if (fileNotFound.trim().length() < 5) {
                fileNotFound = "";
              }
              LOGGER.error("SQL COMMAND LIST IS EMPTY. THERE IS"
                  + " SOMETHING WRONG WITH THE IMPORT SCRIPT" + fileNotFound);
            }
          } else {
            LOGGER.error("CAN'T ESTABLISH CONNECTION FROM SELECTED SCENARIO!");
          }
        }
      } catch (Exception e) {
        LOGGER.error("PROBLEM WITH RESETTING USER TABLES", e);
      } finally {
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          try {
            connection.close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
    }

    // long elapsedTime = System.currentTimeMillis() - starttime;
    // 150-190ms
    // System.out.println("Import-Script: " + elapsedTime + " ms");
  }

  /**
   *
   *
   * @param scenario
   * @param user
   * @return
   *
   * @throws SQLException
   */
  private List<String> checkSumChanged(Scenario scenario, User user) throws SQLException {
    List<String> list = new ArrayList<String>();
    List<String> tables = scenarioTablesWithHash.get(scenario);

    if (!scenarioTablesWithHash.containsKey(scenario)) {
      return list;
    }

    if (tables != null && tables.isEmpty()) {
      return list;
    }

    for (String table : tables) {
      String name = table.split("::")[0];
      String sum = table.split("::")[1];
      String currentSum = getTableChecksum(scenario, user, name);
      if (!sum.equals(currentSum)) {
        list.add(name);
      }
    }

    if (!list.isEmpty()) {
      return list;
    }

    return null;
  }

  /**
   *
   *
   * @param query
   * @param user
   * @return
   * @throws SQLException
   */
  private String addUserPrefix(String query, Scenario scenario, User user) throws SQLException {
    // query = "create table dept_emp, test";
    // String regex_table = "[\\`\\'\"\\s]+([a-zA-Z0-9-_]+?)[\\`\\'\"\\s]+";
    // String regex_table = "[\\s]+([a-zA-Z0-9-_]+)[\\s]*";

    String regex_table = "[\\`\\'\"\\s]+([a-zA-Z0-9-_]+)[\\`\\'\"\\s]*[,]?";
    // String REGEX_FIELD =
    // "(?:create|drop|lock|alter)[\\s]+table[s]?(?:[\\s]*if[\\s]*exists)?" +
    // regex_table;
    String REGEX_FIELD = "(?:create|drop|lock|alter)[\\s]+table[s]?(?:[\\s]*if[\\s]*not?[\\s]*exists)?"
        + regex_table;
    Matcher matcher = Pattern.compile(REGEX_FIELD, Pattern.CASE_INSENSITIVE).matcher(query);

    ArrayList<String> exclusions = new ArrayList<String>() {
      private static final long serialVersionUID = 1L;
      {
        add("if");
        add("select");
        add("table");
        add("exists");
        add("not exists");
      }
    };

    List<String> tablesToReplace = new ArrayList<String>();
    List<String> tablesToUse = new ArrayList<String>();
    List<Integer> stringStart = new ArrayList<Integer>();
    List<Integer> stringEnd = new ArrayList<Integer>();

    while (matcher.find()) {
      String table = matcher.group(1).trim().toLowerCase();
      if (!exclusions.contains(table)) {
        tablesToReplace.add(table);
        tablesToUse.add(table);
        stringStart.add(matcher.start(1));
        stringEnd.add(matcher.end(1));
      }
    }

    REGEX_FIELD = "(?:insert[\\s]+into|references|constraint)" + regex_table;
    matcher = Pattern.compile(REGEX_FIELD, Pattern.CASE_INSENSITIVE).matcher(query);
    while (matcher.find()) {
      String table = matcher.group(1).trim().toLowerCase();
      if (!exclusions.contains(table)) {
        tablesToReplace.add(table);
        stringStart.add(matcher.start(1));
        stringEnd.add(matcher.end(1));
      }
    }

    REGEX_FIELD = "(?:insert[\\s]+into|references)" + regex_table;
    matcher = Pattern.compile(REGEX_FIELD, Pattern.CASE_INSENSITIVE).matcher(query);
    if (matcher.find()) {
      String table = matcher.group(1).trim().toLowerCase();
      if (!exclusions.contains(table)) {
        tablesToUse.add(table);
        stringStart.add(matcher.start(1));
        stringEnd.add(matcher.end(1));
      }
    }

    // String tempQuery = query.replaceAll("\\s+", "").toLowerCase();
    // if (tempQuery.contains("foreignkey") &&
    // tempQuery.contains("createtable")) {
    // if (!hasForeignKeys.contains(scenario)) {
    // hasForeignKeys.add(scenario);
    // }
    // }

    if (!tablesToReplace.isEmpty()) {
      // System.out.println(tablesToReplace);
      ArrayList<String> tables = null;
      ArrayList<String> tablesWithHash = null;

      if (scenarioTables.containsKey(scenario)) {
        tables = scenarioTables.get(scenario);
        tablesWithHash = scenarioTablesWithHash.get(scenario);
      } else {
        tables = new ArrayList<String>();
        scenarioTables.put(scenario, tables);
        tablesWithHash = new ArrayList<String>();
        scenarioTablesWithHash.put(scenario, tablesWithHash);
      }

      for (int i = tablesToReplace.size() - 1; i >= 0; i--) {
        String foundTable = tablesToReplace.get(i);
        if (user != null) {
          query = query.substring(0, stringStart.get(i)) + user.getId() + "_" + foundTable
              + query.substring(stringEnd.get(i), query.length());
        }
      }

      for (String tableToUse : tablesToUse) {
        if (!tables.contains(tableToUse)) {
          tables.add(tableToUse);
        }
        boolean saveTableCheckum = true;
        for (String table : tablesWithHash) {
          if (table.startsWith(tableToUse + "::")) {
            saveTableCheckum = false;
          }
        }
        if (saveTableCheckum) {
          String checkSum = getTableChecksum(scenario, user, tableToUse);
          tablesWithHash.add(tableToUse + "::" + checkSum);
        }
      }
    }
    return query;
  }

  /**
   *
   *
   * @param scenario
   *
   * @throws SQLException
   */
  private void resetDatabaseRoot(Scenario scenario) throws SQLException {
    if (scenario != null) {
      try {
        System.err.println("INFO: FORCE DROP AND CREATE DATABASE");
        JdbcTemplate template = instance.getTemplate(scenario);
        String dbName = scenario.getDbName();
        template.execute("DROP DATABASE IF EXISTS `" + dbName + "`;");
        Thread.sleep(100);
        template.execute("CREATE DATABASE IF NOT EXISTS`" + dbName + "` CHARACTER SET utf8;");
        Thread.sleep(100);
      } catch (Exception e) {
        errors.put(scenario, e.getMessage());
      }
    }
  }

  /**
   *
   *
   * @param scenario
   * @return
   */
  public ArrayList<String> getScenarioTableNamesWithHash(Scenario scenario) {
    if (scenario != null && scenarioTables != null && scenarioTables.containsKey(scenario)) {
      return scenarioTables.get(scenario);
    }
    return null;
  }

  /**
   *
   *
   * @param scenario
   * @return
   */
  public ArrayList<String> getScenarioTableNames(Scenario scenario) {
    ArrayList<String> temp = getScenarioTableNamesWithHash(scenario);
    if (temp != null) {
      ArrayList<String> tables = new ArrayList<String>();
      for (String table : temp) {
        tables.add(table.split("::")[0]);
      }
      return tables;
    }
    return null;
  }

  /**
   *
   *
   * @param scenario
   * @return
   */
  public String getError(Scenario scenario) {
    if (errors.containsKey(scenario)) {
      return errors.get(scenario);
    }
    return null;
  }

  /**
   * @return the resourcePath
   */
  public String getResourcePath() {
    return resourcePath;
  }

  /**
   * @param resourcePath
   *          the resourcePath to set
   */
  public void setResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  /**
   * @return the originalTableDeleted
   */
  public ArrayList<Scenario> getOriginalTableDeleted() {
    return originalTableDeleted;
  }
}
