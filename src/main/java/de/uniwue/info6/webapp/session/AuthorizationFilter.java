package de.uniwue.info6.webapp.session;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AuthorizationFilter.java
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
import static de.uniwue.info6.misc.properties.PropBool.IMPORT_DB_IF_EMPTY;
import static de.uniwue.info6.misc.properties.PropBool.IMPORT_EXAMPLE_SCENARIO;
import static de.uniwue.info6.misc.properties.PropBool.LOG_BROWSER_HISTORY;
import static de.uniwue.info6.misc.properties.PropBool.USE_FALLBACK_USER;
import static de.uniwue.info6.misc.properties.PropInteger.SESSION_TIMEOUT;
import static de.uniwue.info6.misc.properties.PropString.SCENARIO_RESOURCES_PATH;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
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
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropString;
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

  private final static String
  LOGOUT_PAGE        = "logout",
  PERMISSION_PAGE    = "permission",
  SESSION_POSITION   = "auth_controller",
  ERROR_PAGE         = "starterror",
  BROWSER_LOG_DIR    = "log",
  SCENARIO_RES_PATH  = "scn",
  TEMP_SCENARIO_DIR  = "0";

  private final static String userID = "userID", secureValue = "secureValue",
                              scenarioID = "scenarioID";
  private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

  private final static String SCRIPT_PATH = StringTools.shortenUnixHomePathReverse(Cfg.inst()
      .getProp(MAIN_CONFIG, SCENARIO_RESOURCES_PATH));
  private final static String FALLBACK_USER = Cfg.inst().getProp(MAIN_CONFIG,
      PropString.FALLBACK_USER);
  private final static String FALLBACK_SECUREVALUE = Cfg.inst().getProp(MAIN_CONFIG,
      PropString.FALLBACK_SECUREVALUE);
  private final static String FALLBACK_SCENARIO = Cfg.inst().getProp(MAIN_CONFIG,
      PropString.FALLBACK_SCENARIO);

  private String[] id, sv, sc;

  private User user;
  private Scenario scenario;
  private UserRights rights;

  private static String browserLogFile;

  private boolean fatalError;

  public static String errorDescription;

  private String mainPathErrorMessage = null;
  private String dbErrorMessage = null;

  /**
   *
   */
  public AuthorizationFilter() {
    this.rights = new UserRights().initialize();
    this.fatalError = false;
    try {
      System.setProperty("file.encoding", "UTF-8");
      Field charset = Charset.class.getDeclaredField("defaultCharset");
      charset.setAccessible(true);
      charset.set(null, null);
    } catch (Exception e) {
    }

    boolean checkMainScenarioPath = checkMainScenarioPath();
    boolean checkDBConnection = checkDBConnection();

    try {
      ConnectionManager.instance().resetAllScenarioTables();
    } catch (SQLException e) {
      System.out.println(e);
    }

    if (!checkDBConnection || !checkMainScenarioPath) {
      this.fatalError = true;
      errorDescription = "<br/>";

      if (!checkDBConnection) {
        errorDescription += "[problem creating connection to main database]<br/>"
                            + (dbErrorMessage != null ? "LOG: " + dbErrorMessage : "")
                            + "<br/><br/>";
      }

      if (!checkMainScenarioPath) {
        errorDescription += "[problem finding main scenario path given by config.properties]<br/>"
                            + (mainPathErrorMessage != null ? "LOG: " + mainPathErrorMessage : "")
                            + "<br/>";
      }

      LOGGER.error("NOT ALL RESOURCES FOUND, GOING TO SHOW ERRORPAGE");
    } else {
      ConnectionTools.inst().start();
      initBrowserLog();
    }
  }

  /**
   *
   *
   */
  private void initBrowserLog() {
    if (Cfg.inst().getProp(MAIN_CONFIG, LOG_BROWSER_HISTORY)) {
      browserLogFile = SCRIPT_PATH + File.separator + SCENARIO_RES_PATH + File.separator + TEMP_SCENARIO_DIR
                       + File.separator + BROWSER_LOG_DIR + File.separator + "browser_log_"
                       + dateFormat.format(new Date()) + ".csv";
      logBrowser("User-ID\tUser-Agent");
    }
  }



  /**
   *
   *
   */
  private boolean checkDBConnection() {
    try {

      // ------------------------------------------------ //

      boolean masterDBFound = dBDataExists();
      boolean forceReset = Cfg.inst().getProp(MAIN_CONFIG, FORCE_RESET_DATABASE);
      boolean importIfNoDBFound = Cfg.inst().getProp(MAIN_CONFIG, IMPORT_DB_IF_EMPTY);

      if (!masterDBFound) {
        System.err.println("INFO (ueps): Master database not found");
      }

      if (forceReset || (!masterDBFound && importIfNoDBFound)) {
        new GenerateData().resetDB();
      }

      // ------------------------------------------------ //

      if (!dBDataExists()) {
        dbErrorMessage = "COULD NOT ACCESS DB, CHECK IF CREDENTIALS ARE CORRECT.";
        LOGGER.error(dbErrorMessage);
        return false;
      } else {
        try {
          ConnectionManager.instance().updateScenarios();
        } catch (Exception e) {
          LOGGER.error("UPDATING SCENARIOS FAILED", e);
          return false;
        }
      }

      // ------------------------------------------------ //

    } catch (Exception e) {
      dbErrorMessage = "COULD NOT ACCESS DB, CHECK IF CREDENTIALS ARE CORRECT.";
      LOGGER.error(dbErrorMessage, e);
      return false;
    }

    return true;
  }


  /**
   *
   *
   */
  private boolean checkMainScenarioPath() {

    try {
      File mainDir = new File(SCRIPT_PATH);

      if (SCRIPT_PATH.trim().isEmpty()) {
        mainPathErrorMessage = "SCENARIO RESOURCES PATH IS EMPTY!";
      } else if (!mainDir.exists() || !mainDir.isDirectory()) {
        mainPathErrorMessage =  "SCENARIO RESOURCES DIRECTORY NOT FOUND:\n\"" +
                                mainDir.getAbsolutePath() + "\"";
      } else if (!mainDir.canWrite()) {
        mainPathErrorMessage = "NO WRITING - PERMISSIONS FOR SCENARIO RESOURCES:\n\""
                               + mainDir.getAbsolutePath() + "\"";
      } else {

        // create needed path structure in resource directory {{{
        File subFolder = new File(mainDir, SCENARIO_RES_PATH);
        if (!subFolder.exists()) {
          subFolder.mkdir();
        }
        File subSubFolder = new File(subFolder, TEMP_SCENARIO_DIR);
        if (!subSubFolder.exists()) {
          subSubFolder.mkdir();
        }
        // }}}

        if (Cfg.inst().getProp(MAIN_CONFIG, FORCE_RESET_DATABASE)
            || Cfg.inst().getProp(MAIN_CONFIG, IMPORT_DB_IF_EMPTY)) {

          final URL scriptFileURL = this.getClass().getResource("/" +
                                    GenerateData.CREATE_SCRIPT_FILE);

          final URL scriptFileWithDataURL = this.getClass().getResource("/" +
                                            GenerateData.CREATE_SCRIPT_FILE_WITH_EXAMPLE_DATA);

          if (scriptFileURL == null) {
            mainPathErrorMessage = "MAIN SQL-SCRIPT NOT FOUND:\n\"" +
                                   GenerateData.CREATE_SCRIPT_FILE + "\"";
          }

          if (Cfg.inst().getProp(MAIN_CONFIG, IMPORT_EXAMPLE_SCENARIO)
              && scriptFileWithDataURL == null) {
            mainPathErrorMessage = "SQL-SCRIPT NOT FOUND:\n\"" +
                                   GenerateData.CREATE_SCRIPT_FILE_WITH_EXAMPLE_DATA + "\"";
          }
        }
      }
      if (mainPathErrorMessage != null) {
        LOGGER.error(mainPathErrorMessage);
        return false;
      }
    } catch (Exception e) {
      LOGGER.error("problem occurred checking folder-structure", e);
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
      SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) new ScenarioDao()
                                              .getSessionFactory();
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
      Class.forName("org.mariadb.jdbc.Driver"); // Register JDBC Driver
      connection = DriverManager.getConnection(url, dbUser, dbPass); // Open a
      // connection

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
    SessionObject ac = (SessionObject) session.getAttribute(SESSION_POSITION);
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
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
  throws ServletException, IOException {
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
                SessionObject ac = (SessionObject) session.getAttribute(SESSION_POSITION);
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
              } else if ((path.contains("/submission.xhtml") || path
                          .contains("/edit_submission.xhtml"))
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
        SessionObject sessionObject = (SessionObject) session.getAttribute(SESSION_POSITION);

        if (sessionObject != null) {
          sessionObject = sessionObject.init(id[0], id[1], id[2], request.getRemoteAddr());
          if (sessionObject.loginSuccessfull()) {
            this.user = sessionObject.getUser();
            if (user != null) {
              HttpServletRequest req = (HttpServletRequest) request;
              String userAgent = req.getHeader("User-Agent");

              if (Cfg.inst().getProp(MAIN_CONFIG, LOG_BROWSER_HISTORY)
                  && !SessionListener.userExists(user) && !user.getId().equals(FALLBACK_USER)) {
                logBrowser(user.getId() + "\t" + userAgent);
              }

              SessionListener.addUser(session, user, scenario);
              session.setMaxInactiveInterval(Cfg.inst().getProp(MAIN_CONFIG, SESSION_TIMEOUT));
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
    // TODO: browser logging - dirty implementation
    // try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(browserLogFile, true)))) {
    //   out.println(browser);
    // } catch (Exception e) {
    //   LOGGER.error("COULD NOT LOG USER-AGENT.", e);
    // }
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

      if (Cfg.inst().getProp(MAIN_CONFIG, USE_FALLBACK_USER)) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        if (!SessionListener.sessionExists(session)) {
          if (id == null || sv == null || sc == null || id.length == 0 || sv.length == 0
              || sc.length == 0 || id[0] == null || sv[0] == null || sc[0] == null) {
            id = new String[] { FALLBACK_USER };
            sv = new String[] { FALLBACK_SECUREVALUE };
            sc = new String[] { FALLBACK_SCENARIO };
          }
        }
      }

      UserDao userDao = new UserDao();
      ScenarioDao scenarioDao = new ScenarioDao();

      if (userDao != null && scenarioDao != null && id != null && sv != null && sc != null
          && id.length > 0 && sv.length > 0 && sc.length > 0 && id[0] != null && sv[0] != null
          && sc[0] != null) {

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
        res.sendRedirect(req.getContextPath() + "/" + LOGOUT_PAGE);
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
  private void throwPermissionError(HttpServletResponse res, HttpServletRequest req)
  throws IOException {
    if (!isPublicURL(req)) {
      if (!res.isCommitted()) {
        res.sendRedirect(req.getContextPath() + "/" + PERMISSION_PAGE);
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
        res.sendRedirect(req.getContextPath() + "/" + ERROR_PAGE);
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
