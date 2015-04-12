package de.uniwue.info6.webapp.admin;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AdminEditScenario.java
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

import static de.uniwue.info6.misc.properties.PropBool.CREATE_EXAMPLE_EXERCISE;
import static de.uniwue.info6.misc.properties.PropBool.DEBUG_MODE;
import static de.uniwue.info6.misc.properties.PropBool.SHOWCASE_MODE;
import static de.uniwue.info6.misc.properties.PropString.MASTER_DBHOST;
import static de.uniwue.info6.misc.properties.PropString.MASTER_DBPORT;
import static de.uniwue.info6.misc.properties.PropString.SCENARIO_RESOURCES_PATH;
import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.DateFormatTools;
import de.uniwue.info6.misc.FileTransfer;
import de.uniwue.info6.misc.Password;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropInteger;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "edit_sc")
@ViewScoped
public class AdminEditScenario implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AdminEditScenario.class);

  private static final String SCENARIO_PARAM = "scenario";
  private static final String EMPTY_FIELD = "---";

  private static String scriptSystemPath = Cfg.inst().getProp(MAIN_CONFIG, SCENARIO_RESOURCES_PATH);

  private Scenario scenario;

  private Integer originalScenarioId;

  private User user;
  private SessionObject ac;
  private ConnectionManager connectionPool;
  private FileTransfer uploader;

  private String scenarioName;
  private String description;
  private String scriptPath;
  private String imagePath;
  private String dbHost;
  private String dbUser;
  private String dbPass;
  private String dbPort;
  private String dbName;
  private Date startDate;
  private Date endDate;
  private Date lastModified;
  private File scriptFile;
  private File imageFile;
  private StreamedContent scriptStream;
  private StreamedContent imageStream;
  private String importSyntaxError;
  private String addRightsScripts;
  private String removeRightsScripts;
  private boolean userHasRights;
  private UserRights rights;
  private boolean databaseChanged;
  private boolean isAdmin, isLecturer;
  private boolean debugMode;

  // ------------------------------------------------ //
  private ScenarioDao scenarioDao;
  private ExerciseDao exerciseDao;
  private ExerciseGroupDao exerciseGroupDao;
  private SolutionQueryDao solutionQueryDao;
  private UserRightDao userRightsDao;
  // ------------------------------------------------ //

  /**
   *
   *
   * @return
   */
  public boolean renderDBFields() {
    if (isAdmin || isLecturer) {
      return true;
    }
    return false;
  }


  /**
   *
   *
   * @return
   */
  public String autoCreationText() {
    if (hasHost() || hasPort() || hasDBName() || hasUser() || hasPass()) {
      return "";
    }

    return " --- " + Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.AUTO_GEN");
  }

  /**
   *
   *
   */
  private void updateRightScripts() {
    String color = "green";
    addRightsScripts = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.DESCRIPTION");
    removeRightsScripts = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.DROP_USER");

    // TODO: rechteskript aus config-datei auslesen
    if (dbHost != null && !dbHost.isEmpty()) {
      addRightsScripts = addRightsScripts.replace("db_host", "<span style=\"color:" + color + "\">"
                         + dbHost + "</span>");
      removeRightsScripts = removeRightsScripts.replace("db_host", "<span style=\"color:" + color
                            + "\">" + dbHost + "</span>");
    }
    if (dbUser != null && !dbUser.isEmpty()) {
      addRightsScripts = addRightsScripts.replace("user_name", "<span style=\"color:" + color
                         + "\">" + dbUser + "</span>");
      removeRightsScripts = removeRightsScripts.replace("user_name", "<span style=\"color:" + color
                            + "\">" + dbUser + "</span>");
    }
    if (dbName != null && !dbName.isEmpty()) {
      addRightsScripts = addRightsScripts.replace("db_name", "<span style=\"color:" + color + "\">"
                         + dbName + "</span>");
      removeRightsScripts = removeRightsScripts.replace("db_name", "<span style=\"color:" + color
                            + "\">" + dbName + "</span>");
    }
    if (dbPass != null && !dbPass.isEmpty()) {
      addRightsScripts = addRightsScripts.replace("password", "<span class=\"hidden_password\" " +
                         "style=\"background-color:green;color:green;cursor:help\">" + dbPass + "</span>");
      removeRightsScripts = removeRightsScripts.replace("password", "<span style=\"color:" + color
                            + "\">" + dbPass + "</span>");
    }
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    userHasRights = false;
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    scenarioDao = new ScenarioDao();
    exerciseDao = new ExerciseDao();
    userRightsDao = new UserRightDao();
    exerciseGroupDao = new ExerciseGroupDao();
    solutionQueryDao = new SolutionQueryDao();
    Map<String, String> requestParams = ec.getRequestParameterMap();
    ac = SessionObject.pullFromSession();
    user = ac.getUser();
    connectionPool = ConnectionManager.instance();
    uploader = new FileTransfer();
    rights = new UserRights().initialize();
    debugMode = Cfg.inst().getProp(MAIN_CONFIG, DEBUG_MODE);

    if (user != null) {
      this.isAdmin = rights.isAdmin(user);
      this.isLecturer = rights.isLecturer(user);
    }

    if (!requestParams.isEmpty()) {
      try {
        // exercise get-parameter found
        if (requestParams.containsKey(SCENARIO_PARAM)) {
          String param = requestParams.get(SCENARIO_PARAM);
          final int id = Integer.parseInt(param);
          scenario = scenarioDao.getById(id);

          if (scenario == null) {
            if (rights.isAdmin(user) || rights.isLecturer(user)) {
              userHasRights = true;
            }
          } else {
            if (rights.hasEditingRight(user, scenario)) {
              userHasRights = true;
              lastModified = scenario.getLastModified();
              scenarioName = scenario.getName();
              description = scenario.getDescription();
              scriptPath = scenario.getCreateScriptPath();
              imagePath = scenario.getImagePath();
              dbHost = scenario.getDbHost();
              dbUser = scenario.getDbUser();

              // ------------------------------------------------ //
              final boolean showCaseMode = Cfg.inst().getProp(MAIN_CONFIG, SHOWCASE_MODE);
              if (showCaseMode) {
                // don't show the actual password
                dbPass = StringTools.generatePassword(64, 32);
              } else {
                dbPass = scenario.getDbPass();
              }
              // ------------------------------------------------ //

              dbPort = scenario.getDbPort();
              dbName = scenario.getDbName();
              startDate = scenario.getStartTime();
              endDate = scenario.getEndTime();

              if (scenario.getScenario() != null) {
                originalScenarioId = scenario.getScenario().getId();
              }

              if (scriptPath != null && !scriptPath.isEmpty()) {
                File absPath = getSourceFile(scriptPath, false);
                scriptFile = absPath;
                scriptStream = uploader.getFileToDownload(absPath);
              }

              if (imagePath != null) {
                File absPath = getSourceFile(imagePath, false);
                imageFile = absPath;
                imageStream = uploader.getFileToDownload(absPath);
              }
            }

          }
        }
      } catch (NumberFormatException e) {
        if (rights.isAdmin(user) || rights.isLecturer(user)) {
          userHasRights = true;
        }
      } catch (Exception e) {
        LOGGER.error("ERROR GETTING SCENARIO FIELDS FROM DATABASE", e);
      }
    }

    if (debugMode) {
      final Random random = new Random();
      scenarioName = "debug-name-" + random.nextInt(1000);
      description = StringUtils.repeat("debug-description ", 50);
    }

    updateRightScripts();
  }

  /**
   *
   *
   * @param fileName
   * @return
   */
  public File getSourceFile(String fileName, boolean origin) {
    String scenarioID = origin ? "0" : scenario.getId().toString();
    File file = new File(scriptSystemPath + File.separator + Cfg.RESOURCE_PATH + File.separator
                         + scenarioID + File.separator + fileName);
    return file;
  }

  /**
   *
   *
   * @return
   */
  public String getAvailableTables() {
    if (scenario != null && connectionPool != null) {
      ArrayList<String> tables = connectionPool.getScenarioTableNames(scenario);
      StringBuffer returnString = new StringBuffer();
      if (tables != null) {
        for (String table : tables) {
          returnString.append("[" + table + "] ");
        }
        if (returnString.length() > 0) {
          return returnString.toString();
        }
      }

    }
    return EMPTY_FIELD;
  }

  /**
   *
   *
   */
  public void resetScriptFile() {
    File absPath = getSourceFile(scriptPath, false);
    scriptStream = uploader.getFileToDownload(absPath);
  }

  /**
   *
   *
   */
  public void resetImageFile() {
    File absPath = getSourceFile(imagePath, false);
    imageStream = uploader.getFileToDownload(absPath);
  }

  /**
   *
   *
   */
  public void deleteImageFile() {
    imageStream = null;
    imagePath = null;
  }

  /**
   *
   *
   */
  public void deleteScriptFile() {
    scriptStream = null;
    scriptPath = null;
  }

  /**
   *
   */
  public boolean hasScenarioName() {
    return scenarioName != null && !scenarioName.isEmpty();
  }

  /**
   *
   */
  public boolean hasOriginalScenario() {
    return originalScenarioId != null;
  }

  /**
   *
   */
  public boolean hasDescription() {
    return description != null && !StringTools.stripHtmlTags(description).trim().isEmpty();
  }

  /**
   *
   */
  public boolean hasHost() {
    return dbHost != null && !dbHost.isEmpty();
  }

  /**
   *
   */
  public boolean hasPort() {
    return dbPort != null && !dbPort.isEmpty();
  }

  /**
   *
   */
  public boolean hasUser() {
    return dbUser != null && !dbUser.isEmpty();
  }

  /**
   *
   */
  public boolean hasPass() {
    return dbPass != null && !dbPass.isEmpty();
  }

  /**
   *
   */
  public boolean hasDBName() {
    return dbName != null && !dbName.isEmpty();
  }

  /**
   *
   */
  public boolean hasImagePath() {
    return imagePath != null && !imagePath.isEmpty();
  }

  /**
   *
   */
  public boolean hasScriptPath() {
    return scriptPath != null && !scriptPath.isEmpty();
  }

  /**
   *
   *
   * @param actionEvent
   */
  public void save(ActionEvent actionEvent) {
    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;
    importSyntaxError = null;
    Scenario tempScenario = null;
    databaseChanged = false;


    boolean hasCreateRights = true;

    try {
      if (!hasHost()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.EMPTY_DBHOST");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasPort()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.EMPTY_DBPORT");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasDBName()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.EMPTY_DBNAME");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasUser()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.EMPTY_DBUSER");
        sev = FacesMessage.SEVERITY_ERROR;
      }

      if (hasHost() || hasPort() || hasDBName() || hasUser()) {
        hasCreateRights = false;
      }

      if (hasCreateRights) {
        message = null;
        sev = null;
      }

      if (!hasScenarioName()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.EMPTY_NAME");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasDescription()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.EMPTY_DESCRIPTION");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasScriptPath()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.EMPTY_DBSCRIPT");
        sev = FacesMessage.SEVERITY_ERROR;
      }

      if (startDate != null && endDate != null) {
        if (startDate.after(endDate)) {
          message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.TIME_CONFLICT1");
          sev = FacesMessage.SEVERITY_ERROR;
        }
      }

      if (scenario != null) {
        List<ExerciseGroup> groups = exerciseGroupDao.findByScenario(scenario);
        for (ExerciseGroup group : groups) {
          Date groupStart = group.getStartTime();
          Date groupEnd = group.getEndTime();

          if (groupStart != null && startDate != null) {
            if (startDate.after(groupStart)) {
              message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.TIME_CONFLICT2") + ": ID[" + group.getId()
                        + "]";
              sev = FacesMessage.SEVERITY_ERROR;
            }
          }

          if (groupEnd != null && endDate != null) {
            if (groupEnd.after(endDate)) {
              message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.TIME_CONFLICT3") + ": ID[" + group.getId()
                        + "]";
              sev = FacesMessage.SEVERITY_ERROR;
            }
          }
        }
      }

      // ------------------------------------------------ //
      File scriptFile = null;
      if (scriptPath != null) {
        scriptFile = getSourceFile(scriptPath, true);
        if (!scriptPath.trim().toLowerCase().endsWith(".sql")) {
          message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.FALSE_ENDING") + ": \".sql\"!";
          sev = FacesMessage.SEVERITY_ERROR;
        }

        Integer maxFileSize = Cfg.inst().getProp(
                                PropertiesFile.MAIN_CONFIG, PropInteger.MAX_SQL_SCRIPT_SIZE);
        final Integer maxFileSizeStudent = Cfg.inst().getProp(
                                             PropertiesFile.MAIN_CONFIG, PropInteger.MAX_SQL_SCRIPT_SIZE_STUDENT);

        if (maxFileSize != null || (maxFileSizeStudent != null && !isAdmin && !isLecturer)) {
          if (maxFileSizeStudent != null) {
            maxFileSize = maxFileSizeStudent;
          }
          if (scriptFile.exists() && scriptFile.isFile() && scriptFile.canRead()) {
            if (!validSize(maxFileSize, scriptFile)) {
              // TODO:
              message = "Die maximale Größe für das SQL-Skript ist " + maxFileSize + "KB<br>"
                        + "(Festgelegt in der 'config.properties')";
              sev = FacesMessage.SEVERITY_ERROR;
            }
          } else {
            message = "Unerwarteter Fehler, das SQL-Skript konnte intern nicht gefunden werden<br>" + scriptFile;
            sev = FacesMessage.SEVERITY_ERROR;
          }
        }
      }
      // ------------------------------------------------ //

      if (imagePath != null) {
        final File imageFile = getSourceFile(imagePath, true);
        String img = imagePath.trim().toLowerCase();

        if (!img.endsWith(".jpg") && !img.endsWith(".jpeg") && !img.endsWith(".png")) {
          message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.NO_DIAGRAM");
          sev = FacesMessage.SEVERITY_ERROR;
        }

        final Integer maxFileSize = Cfg.inst().getProp(
                                      PropertiesFile.MAIN_CONFIG, PropInteger.MAX_ER_DIAGRAM_SIZE);

        if (maxFileSize != null) {
          if (imageFile.exists() && imageFile.isFile() && imageFile.canRead()) {
            if (!validSize(maxFileSize, imageFile)) {
              // TODO:
              message = "Die maximale Größe für das ER-Diagramm ist " + maxFileSize + "KB<br>"
                        + "(Festgelegt in der 'config.properties')";
              sev = FacesMessage.SEVERITY_ERROR;
            }
          } else {
            message = "Unerwarteter Fehler, das ER-Diagramm konnte intern nicht gefunden werden<br>" + imageFile;
            sev = FacesMessage.SEVERITY_ERROR;
          }
        }
      }

      // ------------------------------------------------ //
      final boolean showCaseMode = Cfg.inst().getProp(MAIN_CONFIG, SHOWCASE_MODE);
      if (message == null && showCaseMode) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "SHOWCASE_ERROR");
        sev = FacesMessage.SEVERITY_ERROR;
      }
      // ------------------------------------------------ //

      if (message == null) {
        sev = FacesMessage.SEVERITY_INFO;
        boolean newScenario = false;
        tempScenario = new Scenario();

        if (scenario == null) {
          newScenario = true;
        } else {
          tempScenario.setId(scenario.getId());
        }

        tempScenario.setName(scenarioName);
        tempScenario.setDescription(description);
        tempScenario.setDbHost(dbHost);
        tempScenario.setDbPort(dbPort);
        tempScenario.setDbUser(dbUser);
        tempScenario.setDbName(dbName);
        tempScenario.setLastModified(new Date());

        tempScenario.setEndTime(endDate);
        tempScenario.setStartTime(startDate);

        if (hasImagePath()) {
          tempScenario.setImagePath(imagePath);
        } else {
          tempScenario.setImagePath(null);
        }

        if (hasScriptPath()) {
          tempScenario.setCreateScriptPath(scriptPath);
        } else {
          tempScenario.setCreateScriptPath(null);
        }

        if (hasPass()) {
          tempScenario.setDbPass(dbPass);
        } else {
          tempScenario.setDbPass("");
        }

        if (hasCreateRights) {
          try {
            // creating new database
            dbName = connectionPool.addScenarioDatabase(tempScenario);

            Cfg config = Cfg.inst();
            dbHost = config.getProp(MAIN_CONFIG, MASTER_DBHOST);
            dbPort = config.getProp(MAIN_CONFIG, MASTER_DBPORT);

            if (!hasUser()) {
              // TODO: Nutzernamen-Generierung ueberarbeiten
              dbUser = dbName.replace("slave_", "");
              dbPass = StringTools.generatePassword(64, 32);
              tempScenario.setDbPass(dbPass);
              tempScenario.setDbUser(dbUser);
            }

            tempScenario.setDbName(dbName);
            tempScenario.setDbHost(dbHost);
            tempScenario.setDbPort(dbPort);

            if (dbName != null) {
              updateRightScripts();
              // boolean success1 = connectionPool.editUserRights(StringTools
              // .stripHtmlTags(removeRightsScripts));
              boolean success2 = connectionPool.editUserRights(StringTools
                                 .stripHtmlTags(addRightsScripts));

              if (!success2) {
                message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.RIGHTS_ERROR");
                importSyntaxError = "check permission for user-management";
                sev = FacesMessage.SEVERITY_ERROR;
              } else {
                databaseChanged = true;
              }
            }

          } catch (Exception e) {
            message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.RIGHTS_ERROR");
            sev = FacesMessage.SEVERITY_ERROR;
            importSyntaxError = StringTools.trimToLengthIndicator(ExceptionUtils.getStackTrace(e),
                                600);
          }
        }

        if (newScenario) {
          if (message == null) {
            if (tempScenario != null) {
              scenarioDao.insertNewInstanceP(tempScenario);
            }

            connectionPool.updateScenarios();

            if (tempScenario.getId() != null) {
              LOGGER.info("going to copy: \n" + imageFile + "\n" + scriptFile);
              if (!uploader.copy(scriptFile, imageFile, String.valueOf(tempScenario.getId()))) {
                message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.COPY_ERROR");
                sev = FacesMessage.SEVERITY_ERROR;
              }
            } else {
              message = Cfg.inst().getProp(DEF_LANGUAGE, "ERROR") + ". \n"
                        + Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.SC_NOT_FOUND");
              sev = FacesMessage.SEVERITY_ERROR;
            }

            // ------------------------------------------------ //
            if (tempScenario.getId() != null) {
              try {

                if (this.rights.isLecturer(user)) {
                  final UserRight rightsToSave = new UserRight(user, user, tempScenario, true, true, true);
                  this.userRightsDao.insertNewInstance(rightsToSave);
                }

                final boolean generateExampleExercise = Cfg.inst().getProp(MAIN_CONFIG, CREATE_EXAMPLE_EXERCISE);
                if (generateExampleExercise && tempScenario.getId() != null) {
                  final Random random = new Random();
                  final ExerciseGroup testGroup = new ExerciseGroup();
                  testGroup.setName("Test Übungsblatt - " + random.nextInt(100000) + " (lösch mich)");
                  testGroup.setIsRated(false);
                  testGroup.setScenario(tempScenario);
                  exerciseGroupDao.insertNewInstanceP(testGroup);
                  final ArrayList<String> tables = connectionPool.getScenarioTableNames(tempScenario);
                  if (testGroup.getId() != null && tables != null && !tables.isEmpty()) {
                    final Exercise testExercise = new Exercise();
                    testExercise.setExerciseGroup(testGroup);
                    testExercise.setQuestion("Test Übungsaufgabe - " + random.nextInt(100000) +  " (lösch mich)");
                    testExercise.setCredits((byte) 1);
                    this.exerciseDao.insertNewInstanceP(testExercise);
                    final SolutionQuery solutionQuery = new SolutionQuery();
                    solutionQuery.setExercise(testExercise);
                    solutionQuery.setQuery("SELECT * FROM " + tables.get(0));
                    this.solutionQueryDao.insertNewInstanceP(solutionQuery);
                  }
                }
              } catch (Exception e) {
                LOGGER.info("CAN NOT GENERATE EXAMPLE SCENARIO", e);
              }
            }


            // ------------------------------------------------ //
          }
        } else {
          if (scenario != null) {
            connectionPool.removeScenario(scenario, databaseChanged);
            scenarioDao.updateInstanceP(tempScenario);
          }
          connectionPool.updateScenarios();
        }


        String error = connectionPool.getError(tempScenario);

        if (error != null) {
          importSyntaxError = error;
          message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.SYNTAX_ERROR");
          sev = FacesMessage.SEVERITY_ERROR;
          scenario = tempScenario;
          if (scenario != null) {
            scenarioDao.updateInstanceP(scenario);
          }
        } else {
          sev = FacesMessage.SEVERITY_INFO;
          DateFormat df = new SimpleDateFormat("HH:mm:ss");
          message = Cfg.inst().getProp(DEF_LANGUAGE, "SUCCESS") + " (" + df.format(new Date()) + ")";
          scenario = tempScenario;
          if (scenario != null) {
            ac.setScenario(scenario);
          }
          updateRightScripts();
        }

      }
    } catch (Exception e) {
      message = Cfg.inst().getProp(DEF_LANGUAGE, "ERROR");
      sev = FacesMessage.SEVERITY_ERROR;
      importSyntaxError = StringTools.trimToLengthIndicator(ExceptionUtils.getStackTrace(e), 600);
      LOGGER.error("failed saving scenario:" + scenario, e);

      scenario = tempScenario;
      if (scenario != null) {
        scenarioDao.updateInstanceP(scenario);
      }
    }

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }


  /**
   *
   *
   * @param maxSizeInKB
   * @return
   */
  private boolean validSize(final int maxSizeInKB, final File file) {
    try {
      long fileSizeInKB = file.length() / 1024;
      if (fileSizeInKB < maxSizeInKB)
        return true;
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getLastModifiedTime() {
    if (scenario != null) {
      Date lastMod = scenario.getLastModified();
      if (lastMod != null) {
        return DateFormatTools.getGermanFormat(lastMod);
      }
    }
    return EMPTY_FIELD;
  }

  /**
   *
   *
   * @param actionEvent
   */
  public void resetFields(ActionEvent actionEvent) {
    // optional:

    // question = questionOriginal;
    // credits = creditsOriginal;
    //
    // SolutionQuery example = new SolutionQuery();
    // example.setExercise(exercise);
    // solutions = solutionDao.findByExample(example);
  }

  /**
   *
   *
   * @param event
   */
  public void handleSQLScriptUpload(FileUploadEvent event) {
    String folder = "0";
    if (scenario != null) {
      folder = String.valueOf(scenario.getId());
    }
    scriptFile = uploader.handleUpload(event, folder);
    scriptPath = scriptFile.getName();

    Severity sev = FacesMessage.SEVERITY_INFO;

    FacesMessage msg = new FacesMessage(sev, Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.UPLOAD_SUCCESS"),
                                        scriptPath != null ? Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.NEW_NAME")
                                        + ": " + scriptPath : "");
    FacesContext.getCurrentInstance().addMessage(null, msg);
    scriptStream = uploader.getFileToDownload(scriptFile);
  }

  /**
   *
   *
   * @param event
   */
  public void handleImageUpload(FileUploadEvent event) {
    String folder = "0";
    if (scenario != null) {
      folder = String.valueOf(scenario.getId());
    }

    imageFile = uploader.handleUpload(event, folder);
    imagePath = imageFile.getName();

    Severity sev = FacesMessage.SEVERITY_INFO;
    FacesMessage msg = new FacesMessage(sev, Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.UPLOAD_SUCCESS"),
                                        imagePath != null ? Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_SC.NEW_NAME")
                                        + ": " + imagePath : "");
    FacesContext.getCurrentInstance().addMessage(null, msg);
    imageStream = uploader.getFileToDownload(imageFile);
  }

  /**
   *
   *
   * @return
   */
  public String getPasswortString() {
    if (dbPass == null || dbPass.isEmpty()) {
      return EMPTY_FIELD;
    } else {
      return new Password(dbPass).toString();
    }
  }

  /**
   * @return the originalScenarioId
   */
  public Integer getOriginalScenarioId() {
    return originalScenarioId;
  }

  /**
   * @param originalScenarioId
   *          the originalScenarioId to set
   */
  public void setOriginalScenarioId(Integer originalScenarioId) {
    this.originalScenarioId = originalScenarioId;
  }

  /**
   * @return the scenarioName
   */
  public String getScenarioName() {
    return scenarioName;
  }

  /**
   * @param scenarioName
   *          the scenarioName to set
   */
  public void setScenarioName(String scenarioName) {
    this.scenarioName = scenarioName;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the createScript
   */
  public String getScriptPath() {
    return scriptPath;
  }

  /**
   * @param createScript
   *          the createScript to set
   */
  public void setScriptPath(String createScript) {
    this.scriptPath = createScript;
  }

  /**
   * @return the imagePath
   */
  public String getImagePath() {
    return imagePath;
  }

  /**
   * @param imagePath
   *          the imagePath to set
   */
  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  /**
   * @return the dbHost
   */
  public String getDbHost() {
    return dbHost;
  }

  /**
   * @param dbHost
   *          the dbHost to set
   */
  public void setDbHost(String dbHost) {
    this.dbHost = dbHost;
    updateRightScripts();
  }

  /**
   * @return the dbUser
   */
  public String getDbUser() {
    return dbUser;
  }

  /**
   * @param dbUser
   *          the dbUser to set
   */
  public void setDbUser(String dbUser) {
    this.dbUser = dbUser;
    updateRightScripts();
  }

  /**
   * @return the dbPass
   */
  public String getDbPass() {
    return dbPass;
  }

  /**
   * @param dbPass
   *          the dbPass to set
   */
  public void setDbPass(String dbPass) {
    this.dbPass = dbPass;
    updateRightScripts();
  }

  /**
   * @return the dbPort
   */
  public String getDbPort() {
    return dbPort;
  }

  /**
   * @param dbPort
   *          the dbPort to set
   */
  public void setDbPort(String dbPort) {
    this.dbPort = dbPort;
  }

  /**
   * @return the dbName
   */
  public String getDbName() {
    return dbName;
  }

  /**
   * @param dbName
   *          the dbName to set
   */
  public void setDbName(String dbName) {
    this.dbName = dbName;
    updateRightScripts();
  }

  /**
   * @return the startDate
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * @param startDate
   *          the startDate to set
   */
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  /**
   * @return the endDate
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  /**
   * @return the lastModified
   */
  public Date getLastModified() {
    return lastModified;
  }

  /**
   * @param lastModified
   *          the lastModified to set
   */
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /**
   * @return the scriptFile
   */
  public File getScriptFile() {
    return scriptFile;
  }

  /**
   * @param scriptFile
   *          the scriptFile to set
   */
  public void setScriptFile(File scriptFile) {
    this.scriptFile = scriptFile;
  }

  /**
   * @return the scriptStream
   */
  public StreamedContent getScriptStream() {
    return scriptStream;
  }

  /**
   * @param scriptStream
   *          the scriptStream to set
   */
  public void setScriptStream(StreamedContent scriptStream) {
    this.scriptStream = scriptStream;
  }

  /**
   * @return the imageStream
   */
  public StreamedContent getImageStream() {
    return imageStream;
  }

  /**
   * @param imageStream
   *          the imageStream to set
   */
  public void setImageStream(StreamedContent imageStream) {
    this.imageStream = imageStream;
  }

  /**
   * @return the importSyntaxError
   */
  public String getImportSyntaxError() {
    return importSyntaxError;
  }

  /**
   * @param importSyntaxError
   *          the importSyntaxError to set
   */
  public void setImportSyntaxError(String importSyntaxError) {
    this.importSyntaxError = importSyntaxError;
  }

  /**
   * @return the addRightsScripts
   */
  public String getAddRightsScripts() {
    return addRightsScripts;
  }

  /**
   * @param addRightsScripts
   *          the addRightsScripts to set
   */
  public void setAddRightsScripts(String addRightsScripts) {
    this.addRightsScripts = addRightsScripts;
  }

  /**
   * @return the removeRightsScripts
   */
  public String getRemoveRightsScripts() {
    return removeRightsScripts;
  }

  /**
   * @param removeRightsScripts
   *          the removeRightsScripts to set
   */
  public void setRemoveRightsScripts(String removeRightsScripts) {
    this.removeRightsScripts = removeRightsScripts;
  }

  /**
   * @return the userHasRights
   */
  public boolean isUserHasRights() {
    return userHasRights;
  }

  /**
   * @param userHasRights
   *          the userHasRights to set
   */
  public void setUserHasRights(boolean userHasRights) {
    this.userHasRights = userHasRights;
  }

  /**
   * @return the isAdmin
   */
  public boolean getIsAdmin() {
    return isAdmin;
  }

  /**
   * @param isAdmin the isAdmin to set
   */
  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  /**
   *
   *
   * @return
   */
  public boolean syntaxError() {
    if (getImportSyntaxError() != null && !getImportSyntaxError().isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * @return the scriptFilePath
   */
  public String getScriptFilePath() {
    if (scriptPath != null && !scriptPath.isEmpty()) {
      return scriptPath;
    }
    return EMPTY_FIELD;
  }

  /**
   * @return the scriptFilePath
   */
  public String getImageFilePath() {
    if (imagePath != null && !imagePath.isEmpty()) {
      return imagePath;
    }
    return EMPTY_FIELD;
  }

  /**
   *
   *
   * @return
   */
  public String getStartDateString() {
    if (startDate != null) {
      return DateFormatTools.getGermanFormat(startDate);
    } else {
      return EMPTY_FIELD;
    }
  }

  /**
   *
   *
   * @return
   */
  public String getEndDateString() {
    if (endDate != null) {
      return DateFormatTools.getGermanFormat(endDate);
    } else {
      return EMPTY_FIELD;
    }
  }

  /**
   *
   *
   * @return
   */
  public String openScenario() {
    if (ac != null && scenario != null) {
      ac.setScenario(scenario);
    }
    if (scenario != null) {
      return ".";
    }
    return null;
  }

  /**
   *
   *
   * @return
   */
  public boolean disableScenarioLink() {
    return openScenario() == null;
  }
}
