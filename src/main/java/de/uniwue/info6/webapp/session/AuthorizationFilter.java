package de.uniwue.info6.webapp.session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.internal.SessionFactoryImpl;

import de.uniwue.info6.database.gen.GenerateData;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.jdbc.ConnectionTools;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.webapp.admin.UserRights;

/**
 *
 *
 * @author Michael
 */
public class AuthorizationFilter implements Filter, Serializable {

  final static Lock lock = new ReentrantLock();
  private static final long serialVersionUID = 1L;
  private static final Log LOGGER = LogFactory.getLog(AuthorizationFilter.class);

  private final static String logoutPage = "logout", permissionPage = "permission", loginPage = "index.xhtml",
      sessionPosition = "auth_controller", errorPage = "starterror";
  private final static String userID = "userID", secureValue = "secureValue", scenarioID = "scenarioID";
  private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

  private final static String SCRIPT_PATH = System.getProperty("SCENARIO_RESOURCES");
  private final static String FALLBACK_USER = System.getProperty("FALLBACK_USER");
  private final static String FALLBACK_SECUREVALUE = System.getProperty("FALLBACK_SECUREVALUE");
  private final static String FALLBACK_SCENARIO = System.getProperty("FALLBACK_SCENARIO");

  private static final String RESOURCE_PATH = "scn";
  private final static String SUB_DIR = "0";
  private final static String BROWSER_LOG_DIR = "browser_logs";
  private final static String MAIN_DB_DIR = "main_database";

  private static final String CREATE_SCRIPT_FILE = "backup_latest.sql";
  private static final String CREATE_SCRIPT_FILE_WITH_EXAMPLE = "backup_latest_with_data.sql";

  private String[] id, sv, sc;

  private User user;
  private Scenario scenario;
  private UserRights rights;
  private UserDao userDao;

  private static String browserLogFile;

  private boolean fatalError;

  public static String errorDescription;

  /**
   *
   */
  public AuthorizationFilter() {
    System.out.println("foo: " + new java.util.Date());
    System.out.println(new File("config.properties").exists());

    this.rights = new UserRights().initialize();
    this.userDao = new UserDao();
    this.fatalError = false;
    try {
      System.setProperty("file.encoding", "UTF-8");
      Field charset = Charset.class.getDeclaredField("defaultCharset");
      charset.setAccessible(true);
      charset.set(null, null);
    } catch (Exception e) {
    }

    boolean checkProperties = checkProperties();
    boolean checkMainScenarioPath = checkMainScenarioPath();
    boolean checkDBConnection = checkDBConnection();

    try {
      ConnectionManager.instance().resetAllScenarioTables();
    } catch (SQLException e) {
      System.out.println(e);
    }

    if (!checkDBConnection || !checkMainScenarioPath || !checkProperties) {
      this.fatalError = true;
      errorDescription = "<br/>";

      if (!checkDBConnection) {
        errorDescription += "[problem creating connection to main database]<br/>";
      }
      if (!checkMainScenarioPath) {
        errorDescription += "[problem finding main scenario path given by config.properties]<br/>";
      }
      if (!checkProperties) {
        errorDescription += "[problem finding config.properties]<br/>";
      }
      LOGGER.error("NOT ALL RESOURCES FOUND, GOING TO SHOW ERRORPAGE");
    } else {
      new ConnectionTools().start();
      initBrowserLog();
    }
  }

  /**
   *
   *
   */
  private void initBrowserLog() {
    if (StringTools.parseBoolean(System.getProperty("LOG_BROWSER_HISTORY"))) {
      this.browserLogFile = SCRIPT_PATH + File.separator + RESOURCE_PATH + File.separator + SUB_DIR
          + File.separator + BROWSER_LOG_DIR + File.separator + "browser_log_"
          + dateFormat.format(new Date()) + ".csv";
      logBrowser("User-ID\tUser-Agent");
    }

  }

  /**
   *
   *
   */
  private boolean checkProperties() {
    if (loginPage == null) {
      LOGGER.error("ERROR: \"config.properties\" FILE NOT FOUND!");
      return false;
    }
    return true;
  }

  /**
   *
   *
   */
  private boolean checkDBConnection() {
    try {
      if (StringTools.parseBoolean(System.getProperty("FORCE_RESET_DATABASE"))) {
        return generateNewDB(true);
      } else {
        if (StringTools.parseBoolean(System.getProperty("IMPORT_DB_IF_EMPTY"))) {
          if (!dBDataExists()) {
            if (StringTools.parseBoolean(System.getProperty("FORCE_RESET_DATABASE"))) {
              return generateNewDB(true);
            } else {
              return generateNewDB(false);
            }
          }
        }
      }

      if (!dBDataExists()) {
        String errorMessage = "COULD NOT ACCESS DB, CHECK IF LOGIN DATA IS CORRECT.";
        LOGGER.error(errorMessage);
        return false;
      }
    } catch (Exception e) {
      String errorMessage = "COULD NOT ACCESS DB, CHECK IF LOGIN DATA IS CORRECT.";
      LOGGER.error(errorMessage, e);
      return false;
    }

    return true;
  }

  /**
   *
   *
   */
  private boolean generateNewDB(boolean forceReset) {
    GenerateData gen = new GenerateData();

    if (StringTools.parseBoolean(System.getProperty("IMPORT_EXAMPLE_SCENARIO"))) {
      gen.resetDB(true, forceReset);
    } else {
      gen.resetDB(false, forceReset);
    }

    if (!dBDataExists()) {
      String errorMessage = "COULD NOT CREATE DB, CHECK IF LOGIN DATA IS CORRECT.";
      LOGGER.error(errorMessage);
      return false;
    } else {
      try {
        ConnectionManager.instance().updateScenarios();
      } catch (SQLException | IOException e) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  /**
   *
   *
   */
  private boolean checkMainScenarioPath() {
    String logMessage = null;

    try {
      File mainDir = new File(SCRIPT_PATH);
      if (SCRIPT_PATH.trim().isEmpty()) {
        logMessage = "SCENARIO RESOURCES PATH IS EMPTY!";
      } else if (!mainDir.exists()) {
        logMessage = "ERROR: \"" + mainDir.getAbsolutePath() + "\" SCENARIO RESOURCES DIRECTORY NOT FOUND!";
      } else if (!mainDir.canWrite()) {
        logMessage = "ERROR: NO WRITING-PERMISSIONS FOR SCENARIO RESOURCES: \"" + mainDir.getAbsolutePath()
            + "\"!";
      } else {
        File subFolder = new File(mainDir, RESOURCE_PATH);
        if (!subFolder.exists()) {
          subFolder.mkdir();
        }

        File subSubFolder = new File(subFolder, SUB_DIR);
        if (!subSubFolder.exists()) {
          subSubFolder.mkdir();
        }

        File browserLogFolder = new File(subSubFolder, BROWSER_LOG_DIR);
        if (StringTools.parseBoolean(System.getProperty("LOG_BROWSER_HISTORY")) && !browserLogFolder.exists()) {
          browserLogFolder.mkdir();
        }

        if (StringTools.parseBoolean(System.getProperty("FORCE_RESET_DATABASE"))
            || StringTools.parseBoolean(System.getProperty("IMPORT_DB_IF_EMPTY"))) {

          File databaseFolder = new File(subSubFolder, MAIN_DB_DIR);
          if (!databaseFolder.exists()) {
            logMessage = "ERROR: MAIN DATABASE-FOLDER NOT FOUND: \"" + databaseFolder + "\"";
          } else {
            File createScriptFile = new File(databaseFolder, CREATE_SCRIPT_FILE);
            File createScriptFileWithExample = new File(databaseFolder, CREATE_SCRIPT_FILE_WITH_EXAMPLE);
            if (!createScriptFile.exists()) {
              logMessage = "ERROR: MAIN SQL-SCRIPT NOT FOUND: \"" + createScriptFile + "\"";
            }
            if (StringTools.parseBoolean(System.getProperty("IMPORT_EXAMPLE_SCENARIO"))
                && !createScriptFileWithExample.exists()) {
              logMessage = "ERROR: SQL-SCRIPT NOT FOUND: \"" + createScriptFileWithExample + "\"";
            }
          }
        }
      }
      if (logMessage != null) {
        LOGGER.error(logMessage);
        return false;
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: problem occurred checking folder-structure", e);
      return false;
    }

    return true;
  }

  /**
   *
   *
   * @return
   */
  private boolean[] checkDBExists() {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    boolean catalogExists = false;
    boolean tableExists = false;

    try {
      String dbUser = "", dbPass = "", dbName = "";
      SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) new ScenarioDao().getSessionFactory();
      Properties props = sessionFactoryImpl.getProperties();

      String url = props.get("hibernate.connection.url").toString();
      dbUser = props.get("hibernate.connection.username").toString();
      Object optionalPass = props.get("hibernate.connection.password");
      if (optionalPass != null) {
        dbPass = optionalPass.toString();
      }
      dbName = props.get("hibernate.default_catalog").toString();

      // Class.forName("com.mysql.jdbc.Driver"); //Register JDBC Driver
      // Class.forName("org.drizzle.jdbc.DrizzleDriver"); //Register JDBC Driver
      Class.forName("org.mariadb.jdbc.Driver"); //Register JDBC Driver
      connection = DriverManager.getConnection(url, dbUser, dbPass); //Open a connection

      resultSet = connection.getMetaData().getCatalogs();
      while (resultSet.next()) {
        String databaseName = resultSet.getString(1);
        if (databaseName.equalsIgnoreCase(dbName)) {
          catalogExists = true;
          break;
        }
      }

      connection.setCatalog(dbName);
      ResultSet rs = connection.getMetaData().getTables(null, null, "%", null);
      while (rs.next()) {
        tableExists = true;
      }
    } catch (Exception e) {
      return new boolean[] { false, false };
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return new boolean[] { catalogExists, tableExists };
  }

  /**
   *
   *
   * @return
   */
  private boolean dBDataExists() {
    boolean dbExists[] = checkDBExists();
    return dbExists[0] && dbExists[1];
  }

  /**
   *
   *
   */
  private void addSessionObject(HttpSession session) {
    SessionObject ac = (SessionObject) session.getAttribute(sessionPosition);
    if (ac == null) {
      new SessionObject(session);
    }
  }

  /**
   * @param request
   * @param response
   * @param chain
   *
   * @throws ServletException
   * @throws IOException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException,
      IOException {
    try {
      HttpServletRequest req = (HttpServletRequest) request;
      HttpServletResponse res = (HttpServletResponse) response;
      response.setCharacterEncoding("UTF-8");
      request.setCharacterEncoding("UTF-8");

      // String url = req.getServletPath();
      HttpSession session = req.getSession(false);

      if (session == null) {
        res.setHeader("REFRESH", "0");
      }

      if (req != null && !isErrorURL(req)) {
        if (session != null) {
          addSessionObject(session);

          if (fatalError) {
            throwErrorPage(res, req);
          } else {
            if (getCredentials(req) != null) {
              boolean success = checkCredentials(req, session);
              if (!success) {
                SessionListener.removeSession(session);
              }
            }

            if (!SessionListener.sessionExists(session)) {
              logoutUser(res, req);
            }

            if (user != null && rights != null) {
              String path = req.getServletPath();

              if (session != null) {
                SessionObject ac = (SessionObject) session.getAttribute(sessionPosition);
                if (ac != null) {
                  User user = ac.getUser();
                  if (user != null) {
                    this.user = user;
                  }
                }
              }

              if ((path.contains("/admin.xhtml") || path.contains("/edit_ex.xhtml")
                  || path.contains("/edit_group.xhtml") || path.contains("/edit_scenario.xhtml"))) {
                if (!rights.hasEditingRight(user, scenario)) {
                  throwPermissionError(res, req);
                }
              } else if ((path.contains("/submission.xhtml") || path.contains("/edit_submission.xhtml"))
                  && !rights.hasRatingRight(user, scenario)) {
                throwPermissionError(res, req);
              } else if (path.contains("/user_rights.xhtml") && !rights.isAdmin(user)) {
                throwPermissionError(res, req);
              }

            } else {
              throwPermissionError(res, req);
            }
          }
        }
      }
      chain.doFilter(request, response);
    } catch (Exception e) {
      LOGGER.info("UNEXPECTED ERROR WITH CHAINFILTER", e);
    }
  }

  /**
   *
   *
   * @param session
   */
  private boolean checkCredentials(ServletRequest request, HttpSession session) {
    String[] id = getCredentials(request);
    try {
      if ((session != null && id != null)) {
        SessionObject sessionObject = (SessionObject) session.getAttribute(sessionPosition);

        if (sessionObject != null) {
          sessionObject = sessionObject.init(id[0], id[1], id[2], request.getRemoteAddr());
          if (sessionObject.loginSuccessfull()) {
            this.user = sessionObject.getUser();
            if (user != null) {
              HttpServletRequest req = (HttpServletRequest) request;
              String userAgent = req.getHeader("User-Agent");
              if (StringTools.parseBoolean(System.getProperty("LOG_BROWSER_HISTORY"))
                  && !SessionListener.userExists(user) && !user.getId().equals(FALLBACK_USER)) {
                logBrowser(user.getId() + "\t" + userAgent);
              }
              SessionListener.addUser(session, user, scenario);
              session.setMaxInactiveInterval(Integer.parseInt(System.getProperty("SESSION_TIMEOUT")));
            }
            return true;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("UNEXPECTED ERROR CHECKING USER CREDENTIALS", e);
    }
    return false;
  }

  /**
   *
   *
   * @param browser
   */
  private static synchronized void logBrowser(String browser) {
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(browserLogFile, true)))) {
      out.println(browser);
    } catch (Exception e) {
      LOGGER.error("COULD NOT LOG USER-AGENT.", e);
    }
  }

  /**
   *
   *
   * @param request
   * @return
   */
  private String[] getCredentials(ServletRequest request) {
    // get credentials
    try {
      Map<String, String[]> requestParams = request.getParameterMap();

      id = requestParams.get(userID);
      sv = requestParams.get(secureValue);
      sc = requestParams.get(scenarioID);

      if (StringTools.parseBoolean(System.getProperty("USE_FALLBACK_USER"))) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        if (!SessionListener.sessionExists(session)) {
          if (id == null || sv == null || sc == null || id.length == 0 || sv.length == 0 || sc.length == 0
              || id[0] == null || sv[0] == null || sc[0] == null) {
            id = new String[] { FALLBACK_USER };
            sv = new String[] { FALLBACK_SECUREVALUE };
            sc = new String[] { FALLBACK_SCENARIO };
          }
        }
      }

      UserDao userDao = new UserDao();
      ScenarioDao scenarioDao = new ScenarioDao();

      if (userDao != null && scenarioDao != null && id != null && sv != null && sc != null && id.length > 0
          && sv.length > 0 && sc.length > 0 && id[0] != null && sv[0] != null && sc[0] != null) {

        scenario = scenarioDao.getById(Integer.parseInt(sc[0]));
        return new String[] { String.valueOf(id[0]), String.valueOf(sv[0]), String.valueOf(sc[0]) };
      }
    } catch (Exception e) {
      LOGGER.info("PROBLEM OCCURRED GETTING LOGIN PARAMETERS", e);
    }
    return null;
  }

  /**
   *
   *
   * @param facesContext
   * @throws IOException
   */
  private void logoutUser(HttpServletResponse res, HttpServletRequest req) throws IOException {
    if (!isPublicURL(req)) {
      if (!res.isCommitted()) {
        res.sendRedirect(req.getContextPath() + "/" + logoutPage);
        return;
      }
    }
  }

  /**
   *
   *
   * @param res
   * @param req
   *
   * @throws IOException
   */
  private void throwPermissionError(HttpServletResponse res, HttpServletRequest req) throws IOException {
    if (!isPublicURL(req)) {
      if (!res.isCommitted()) {
        res.sendRedirect(req.getContextPath() + "/" + permissionPage);
        return;
      }
    }
  }

  /**
   *
   *
   * @param res
   * @param req
   *
   * @throws IOException
   */
  private void throwErrorPage(HttpServletResponse res, HttpServletRequest req) throws IOException {
    if (!isPublicURL(req)) {
      if (!res.isCommitted()) {
        res.sendRedirect(req.getContextPath() + "/" + errorPage);
        return;
      }
    }
  }

  /**
   *
   *
   * @param req
   * @return
   */
  private boolean isPublicURL(HttpServletRequest req) {
    String url = req.getServletPath();
    if (url.startsWith("/errorpages/") || url.startsWith("/javax.faces.resource")
        || url.startsWith("/resources/img/") || url.startsWith("/resources/js/")) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @param req
   * @return
   */
  private boolean isErrorURL(HttpServletRequest req) {
    String url = req.getServletPath();
    if (url.startsWith("/errorpages/")) {
      return true;
    }
    return false;
  }

  /**
   * @param filterConfig
   *
   * @throws ServletException
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //
  }

  /**
   */
  @Override
  public void destroy() {
    //
  }
}
