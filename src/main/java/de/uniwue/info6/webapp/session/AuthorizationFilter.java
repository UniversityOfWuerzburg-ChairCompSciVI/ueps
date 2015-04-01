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
import static de.uniwue.info6.misc.properties.PropBool.SHOWCASE_MODE;
import static de.uniwue.info6.misc.properties.PropBool.USE_FALLBACK_USER;
import static de.uniwue.info6.misc.properties.PropInteger.SESSION_TIMEOUT;
import static de.uniwue.info6.misc.properties.PropString.SCENARIO_RESOURCES_PATH;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;
import static de.uniwue.info6.webapp.session.SessionObject.DEMO_ADMIN;
import static de.uniwue.info6.webapp.session.SessionObject.DEMO_STUDENT;
import static de.uniwue.info6.webapp.session.SessionObject.DEMO_LECTURER;
import static de.uniwue.info6.webapp.session.SessionObject.SESSION_POSITION;

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
import java.util.Random;
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

import org.hibernate.internal.SessionFactoryImpl;

import com.sun.faces.context.FacesFileNotFoundException;

import de.uniwue.info6.database.gen.GenerateData;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.jdbc.ConnectionTools;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.conf.HibernateUtil;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
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
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AuthorizationFilter.class);

  private final static String
  LOGOUT_PAGE        = "logout",
  PERMISSION_PAGE    = "permission",
  PAGE_NOT_FOUND     = "404",
  ERROR_PAGE         = "starterror",
  BROWSER_LOG_DIR    = "log",
  TEMP_SCENARIO_DIR  = "0",
  INDEX_PAGE         = "/index.xhtml",
  ADMIN_PAGE         = "/admin.xhtml",
  TASK_PAGE          = "/task.xhtml",
  EDIT_EXERCISE      = "/edit_ex.xhtml",
  EDIT_GROUP         = "/edit_group.xhtml",
  EDIT_SCENARIO      = "/edit_scenario.xhtml",
  SUBMISSION_PAGE    = "/submission.xhtml",
  EDIT_SUBMISSION    = "/edit_submission.xhtml",
  EDIT_RIGHTS        = "/user_rights.xhtml",
  SCENARIO_PARAM     = "scenario",
  GROUP_PARAM        = "group",
  SUBMISSION_PARAM   = "submission",
  EXERCISE_PARAM     = "exercise";

  private final static String
  NEW_SCENARIO_PAR   = "new";

  private final static String
  userID             = "userID",
  encryptedCode      = "encryptedCode",
  scenarioID         = "scenarioID";

  private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

  private final static String SCRIPT_PATH = StringTools.shortenUnixHomePathReverse(Cfg.inst()
      .getProp(MAIN_CONFIG, SCENARIO_RESOURCES_PATH));
  private final static String FALLBACK_USER = Cfg.inst().getProp(MAIN_CONFIG,
      PropString.FALLBACK_USER_ID);
  private final static String FALLBACK_ENCRYPTED_CODE = Cfg.inst().getProp(MAIN_CONFIG,
      PropString.FALLBACK_ENCRYPTED_CODE);

  private String[] id, ec, sc;

  private User user;
  private Scenario scenario;
  private UserRights rights;

  private static String browserLogFile;

  private boolean fatalError;

  public static String errorDescription;

  private String mainPathErrorMessage = null;
  private String dbErrorMessage = null;

  private ScenarioDao scenarioDao;
  private ExerciseGroupDao exerciseGroupDao;
  private UserEntryDao userEntryDao;
  private ExerciseDao exerciseDao;


  /**
   *
   */
  public AuthorizationFilter() {
    this.rights = new UserRights().initialize();
    this.scenarioDao = new ScenarioDao();
    this.exerciseGroupDao = new ExerciseGroupDao();
    this.userEntryDao = new UserEntryDao();
    this.exerciseDao = new ExerciseDao();

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
      ConnectionManager.instance().dropDatabaseTablesForUser();
    } catch (SQLException e) {
      System.out.println(e);
    }

    if (!checkDBConnection || !checkMainScenarioPath) {
      this.fatalError = true;
      errorDescription = "<br/>";

      if (!checkDBConnection) {
        errorDescription +=
          "[problem creating connection to main database]<br/>"
          + (dbErrorMessage != null ? "LOG: " + dbErrorMessage : "")
          + "<br/><br/>";
      }

      if (!checkMainScenarioPath) {
        errorDescription +=
          "[problem finding main scenario path given by config.properties]<br/>"
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
      browserLogFile = SCRIPT_PATH + File.separator + Cfg.RESOURCE_PATH + File.separator + TEMP_SCENARIO_DIR
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
        File subFolder = new File(mainDir, Cfg.RESOURCE_PATH);
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
      String dbUser = "", dbPass = "", dbName = "", url = "";
      // SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) new ScenarioDao()
      //                                         .getSessionFactory();
      SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) HibernateUtil.getSessionFactory();
      Properties props = sessionFactoryImpl.getProperties();

      url                   = props.get("hibernate.connection.url").toString();
      dbUser                = props.get("hibernate.connection.username").toString();
      dbName                = props.get("hibernate.default_catalog").toString();
      Object optionalPass   = props.get("hibernate.connection.password");

      if (optionalPass != null) {
        dbPass = optionalPass.toString();
      }

      Class.forName("org.mariadb.jdbc.Driver"); // Register JDBC Driver
      connection = DriverManager.getConnection(url, dbUser, dbPass);

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
    final SessionObject sessionObject = (SessionObject) session.getAttribute(SESSION_POSITION);
    if (sessionObject == null) {
      new SessionObject(session).pushToSession();
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
    final HttpServletRequest req = (HttpServletRequest) request;
    final HttpServletResponse res = (HttpServletResponse) response;

    final String path = req.getServletPath();
    try {
      response.setCharacterEncoding("UTF-8");
      request.setCharacterEncoding("UTF-8");

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
            // ------------------------------------------------ //
            final String[] credentials = getCredentials(req);

            final boolean relevantDocument =
              path.contains(INDEX_PAGE)
              || path.contains(TASK_PAGE)
              || path.contains(EDIT_GROUP)
              || path.contains(EDIT_SCENARIO)
              || path.contains(EDIT_RIGHTS)
              || path.contains(EDIT_EXERCISE)
              || path.contains(EDIT_SUBMISSION)
              || path.contains(SUBMISSION_PAGE);

            if (credentials != null && relevantDocument) {
              if (!checkCredentials(req, session, credentials)) {
                SessionListener.removeSession(session);
              }
            }
            // ------------------------------------------------ //

            if (!SessionListener.sessionExists(session)) {
              logoutUser(res, req);
            }

            // ------------------------------------------------ //

            if (user != null && rights != null) {
              if (session != null) {
                SessionObject ac = (SessionObject) session.getAttribute(SESSION_POSITION);
                if (ac != null) {
                  User user = ac.getUser();
                  if (user != null) {
                    this.user = user;
                  }
                }
              }

              // ------------------------------------------------ //

              final Map<String, String[]> requestParams = request.getParameterMap();
              final String[] scID = requestParams.get(SCENARIO_PARAM);
              final String[] grID = requestParams.get(GROUP_PARAM);
              final String[] sbID = requestParams.get(SUBMISSION_PARAM);
              final String[] exID = requestParams.get(EXERCISE_PARAM);

              // ------------------------------------------------ //

              // if an exercise id was given, get the scenario it belongs to
              if (isValidGetParameter(exID, true)) {
                final Exercise exercise = this.exerciseDao.getById(Integer.parseInt(exID[0]));
                if (exercise != null && exercise.getExerciseGroup() != null) {
                  final Scenario scenario = this.scenarioDao.getById(exercise.getExerciseGroup().getId());
                  if (scenario != null) {
                    this.scenario = scenario;
                  }
                }
              }

              // ------------------------------------------------ //

              boolean editPermissionError =
                (
                  path.contains(ADMIN_PAGE) && !rights.hasEditingRight(user)
                  || path.contains(EDIT_EXERCISE)
                  || path.contains(EDIT_GROUP)
                  || path.contains(EDIT_SCENARIO)
                )
                ;

              // ------------------------------------------------ //

              if (editPermissionError && path.contains(EDIT_SCENARIO) && isValidGetParameter(scID)) {
                try {
                  final Scenario scenario = this.scenarioDao.getById(Integer.parseInt(scID[0]));
                  if (scenario != null) {
                    this.scenario = scenario;
                    editPermissionError = !rights.hasEditingRight(user, scenario);
                  }
                } catch (NumberFormatException e) {
                  if (scID[0].trim().equals(NEW_SCENARIO_PAR)) {
                    editPermissionError = !rights.isAdmin(user);
                  }
                }
              }

              // ------------------------------------------------ //

              if (editPermissionError && path.contains(EDIT_GROUP)) {
                if (isValidGetParameter(grID, true)) {
                  final ExerciseGroup group = this.exerciseGroupDao.getById(Integer.parseInt(grID[0]));
                  if (group != null) {
                    editPermissionError = !rights.hasEditingRight(user, group);
                  }
                } else if (isValidGetParameter(scID, true)) {
                  final Scenario scenario = this.scenarioDao.getById(Integer.parseInt(scID[0]));
                  if (scenario != null) {
                    editPermissionError = !rights.hasEditingRight(user, scenario, true);
                  }
                }
              }

              // ------------------------------------------------ //

              if (editPermissionError && path.contains(EDIT_EXERCISE)) {
                if (isValidGetParameter(exID, true)) {
                  final Exercise exercise = this.exerciseDao.getById(Integer.parseInt(exID[0]));
                  if (exercise != null) {
                    editPermissionError = !rights.hasEditingRight(user, exercise);
                  }
                } else if (isValidGetParameter(grID, true)) {
                  final ExerciseGroup group = this.exerciseGroupDao.getById(Integer.parseInt(grID[0]));
                  if (group != null) {
                    editPermissionError = !rights.hasEditingRight(user, group);
                  }
                }
              }

              // ------------------------------------------------ //

              boolean editSubmissionError =
                (
                  path.contains(SUBMISSION_PAGE) && !rights.hasRatingRight(user)
                  || path.contains(EDIT_SUBMISSION)
                );

              if (editSubmissionError && isValidGetParameter(sbID, true)) {
                final UserEntry userEntry = this.userEntryDao.getById(Integer.parseInt(sbID[0]));
                if (userEntry != null) {
                  final Exercise exercise = exerciseDao.getById(userEntry.getExercise().getId());
                  editSubmissionError = !rights.hasRatingRight(user, exercise);
                }
              }

              // ------------------------------------------------ //

              final boolean editUserRightsError = path.contains(EDIT_RIGHTS) && !rights.hasUserAddingRights(user);

              // ------------------------------------------------ //

              final boolean viewingRightsError = path.contains(TASK_PAGE) && !rights.hasViewRights(user, scenario);

              // ------------------------------------------------ //

              // System.out.println(editPermissionError + " " + editSubmissionError + " " + editUserRightsError + " " + viewingRightsError);
              if (editPermissionError || editSubmissionError || editUserRightsError || viewingRightsError) {
                throwPermissionError(res, req);
              }

              // ------------------------------------------------ //
            } else {
              throwPermissionError(res, req);
            }
          }
        }
      }
    } catch (Exception e) {
      // TODO: logging
      e.printStackTrace();
    }


    try {
      chain.doFilter(request, response);
    } catch (IndexOutOfBoundsException e) {
      throw e;
    } catch (FacesFileNotFoundException e) {
      this.throw404Error(res, req);
    } catch (Exception e) {
      if (e.getMessage().contains("sendError()") || e.getMessage().contains("Broken pipe")) {
        LOGGER.info("UNEXPECTED ERROR WITH CHAINFILTER", e);
      } else {
        LOGGER.error("UNEXPECTED ERROR WITH CHAINFILTER", e);
      }
    }
  }

  /**
   *
   *
   * @param session
   */
  private boolean checkCredentials(HttpServletRequest request, HttpSession session, String[] credentials) {
    try {
      if (session != null && credentials != null) {

        final String sessionUserID = credentials[0];
        final String sessionEncryptedCode = credentials[1];
        final String sessionScenarioID = credentials[2];

        SessionObject sessionObject = (SessionObject) session.getAttribute(SESSION_POSITION);

        if (sessionObject != null) {
          String ipAddress = request.getRemoteAddr();
          if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
            ipAddress = "127.0.0.1";
          }

          sessionObject.init(sessionUserID, sessionEncryptedCode, sessionScenarioID, ipAddress);

          if (sessionObject.loginSuccessfull()) {
            this.user = sessionObject.getUser();
            if (user != null) {
              HttpServletRequest req = (HttpServletRequest) request;
              String userAgent = req.getHeader("User-Agent");

              if (Cfg.inst().getProp(MAIN_CONFIG, LOG_BROWSER_HISTORY)
                  && !SessionListener.userExists(user)
                  && !user.getId().equals(FALLBACK_USER)) {
                logBrowser(user.getId() + "\t" + userAgent);
              }

              SessionListener.addUser(session, this.user, scenario);

              int timeout = Cfg.inst().getProp(MAIN_CONFIG, SESSION_TIMEOUT);
              timeout = (timeout - 30 > 1) ? (timeout - 30) : timeout;
              session.setMaxInactiveInterval(timeout);
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
      ec = requestParams.get(encryptedCode);
      sc = requestParams.get(scenarioID);

      // ------------------------------------------------ //

      boolean validID = isValidGetParameter(id);
      boolean validEC = isValidGetParameter(ec);
      boolean validSC = isValidGetParameter(sc, true);

      // ------------------------------------------------ //

      String idGET = validID ? String.valueOf(id[0]) : null;
      String ecGET = validEC ? String.valueOf(ec[0]) : null;
      String scGET = validSC ? String.valueOf(sc[0]) : null;

      // ------------------------------------------------ //

      final boolean fallbackUser = Cfg.inst().getProp(MAIN_CONFIG, USE_FALLBACK_USER);
      final boolean showCaseMode = Cfg.inst().getProp(MAIN_CONFIG, SHOWCASE_MODE);

      // ------------------------------------------------ //

      final HttpServletRequest req = (HttpServletRequest) request;
      final HttpSession session = req.getSession(false);
      final User sessionUser = SessionListener.getUser(session);
      final boolean sessionExists = SessionListener.sessionExists(session);

      // ------------------------------------------------ //

      final boolean showCaseUserPar =
        validID &&
        (
          idGET.equals(DEMO_ADMIN)
          || idGET.equals(DEMO_STUDENT)
          || idGET.equals(DEMO_LECTURER)
        );

      // ------------------------------------------------ //

      final boolean showCaseUser =
        sessionUser != null &&
        (
          sessionUser.getId().startsWith(DEMO_ADMIN)
          || sessionUser.getId().startsWith(DEMO_STUDENT)
          || sessionUser.getId().startsWith(DEMO_LECTURER)
        );

      // ------------------------------------------------ //

      final boolean credentialsFound =
        validID && sessionUser != null &&
        (
          sessionUser.getId().equals(idGET) ||
          (
            showCaseUserPar && showCaseUser
            && sessionUser.getId().startsWith(idGET)
          )
        );

      // ------------------------------------------------ //

      if (showCaseUser && credentialsFound) {
        idGET = sessionUser.getId();
        validID = true;
      }

      // ------------------------------------------------ //

      if (!sessionExists || !credentialsFound || validSC) {

        // ------------------------------------------------ //
        if (fallbackUser || showCaseMode) {
          if (showCaseMode && !credentialsFound) {
            if (showCaseUserPar) {
              final Random random = new Random();
              final int maxRandom = 10000;
              String userIDPrefix = DEMO_STUDENT;

              if (validID && idGET.equals(DEMO_ADMIN)) {
                userIDPrefix = DEMO_ADMIN;
              }

              if (validID && idGET.equals(DEMO_LECTURER)) {
                userIDPrefix = DEMO_LECTURER;
              }

              String userName = userIDPrefix + "_" + random.nextInt(maxRandom);
              while (SessionListener.userExists(userName)) {
                userName = userIDPrefix + "_" + random.nextInt(maxRandom);
              }

              idGET =  userName;
              ecGET = String.valueOf(random.nextInt(maxRandom));
              validID = true;
              validEC = true;
            }
          } else if (!validID || !validEC) {
            idGET = FALLBACK_USER;
            ecGET = FALLBACK_ENCRYPTED_CODE;
            validID = true;
            validEC = true;
          }
        }

        // ------------------------------------------------ //

        if (validID && validEC
            && !idGET.equals(DEMO_ADMIN)
            && !idGET.equals(DEMO_STUDENT)
            && !idGET.equals(DEMO_LECTURER)) {
          if (validSC) {
            scenario = scenarioDao.getById(Integer.valueOf(scGET));
            return new String[] { idGET, ecGET, scGET};
          } else {
            return new String[] { idGET, ecGET, null};
          }
        }
        // ------------------------------------------------ //

      }
      // ------------------------------------------------ //

    } catch (NullPointerException e) {
      // TODO:
    } catch (Exception e) {
      LOGGER.error("PROBLEM OCCURRED GETTING LOGIN PARAMETERS", e);
    }
    return null;
  }


  /**
   *
   *
   * @param parameter
   * @return
   */
  private boolean isValidGetParameter(final String[] parameter) {
    return isValidGetParameter(parameter, false);
  }

  /**
   *
   *
   * @param parameter
   * @return
   */
  private boolean isValidGetParameter(final String[] parameter, boolean number) {
    if (parameter == null || parameter.length == 0 || parameter[0] == null || parameter[0].trim().isEmpty()) {
      return false;
    }
    if (number) {
      try {
        Integer.parseInt(parameter[0]);
      } catch (Exception e) {
        return false;
      }
    }
    return true;
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
  private void throw404Error(HttpServletResponse res, HttpServletRequest req)
  throws IOException {
    if (!isPublicURL(req)) {
      if (!res.isCommitted()) {
        res.sendRedirect(req.getContextPath() + "/" + PAGE_NOT_FOUND);
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
