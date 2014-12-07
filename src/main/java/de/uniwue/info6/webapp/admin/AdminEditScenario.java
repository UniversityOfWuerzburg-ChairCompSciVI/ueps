package de.uniwue.info6.webapp.admin;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.misc.DateFormatTools;
import de.uniwue.info6.misc.FileTransfer;
import de.uniwue.info6.misc.Password;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.webapp.session.SessionCollector;
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
  private static final Log LOGGER = LogFactory.getLog(AdminEditScenario.class);

  private static final String SCENARIO_PARAM = "scenario";
  private static final String EMPTY_FIELD = "---";

  private static String scriptSystemPath = System.getProperty("SCENARIO_RESOURCES");
  private static final String RESOURCE_PATH = "scn";

  private ScenarioDao scenarioDao;
  private Scenario scenario;

  private Integer originalScenarioId;
  private ExerciseGroupDao exerciseGroupDao;

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

  /**
   *
   *
   * @return
   */
  public String autoCreationText() {
    String allowCreation = System.getProperty("ALLOW_DB_CREATION");
    boolean hasCreateRights = (allowCreation != null && StringTools.parseBoolean(allowCreation));
    if (hasHost() || hasPort() || hasDBName() || hasUser() || !hasCreateRights) {
      return "";
    }

    return " --- " + System.getProperty("EDIT_SC.AUTO_GEN");
  }

  /**
   *
   *
   */
  private void updateRightScripts() {
    String color = "green";
    addRightsScripts = System.getProperty("EDIT_SC.DESCRIPTION");
    removeRightsScripts = System.getProperty("EDIT_SC.DROP_USER");
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
    if (dbPass != null) {
      addRightsScripts = addRightsScripts.replace("password", "<span style=\"color:" + color
          + "\">" + dbPass + "</span>");
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
    exerciseGroupDao = new ExerciseGroupDao();
    Map<String, String> requestParams = ec.getRequestParameterMap();
    ac = new SessionCollector().getSessionObject();
    user = ac.getUser();
    connectionPool = ConnectionManager.instance();
    uploader = new FileTransfer();
    rights = new UserRights().initialize();

    if (!requestParams.isEmpty()) {
      try {
        // exercise get-parameter found
        if (requestParams.containsKey(SCENARIO_PARAM)) {
          String param = requestParams.get(SCENARIO_PARAM);
          final int id = Integer.parseInt(param);
          scenario = scenarioDao.getById(id);

          if (scenario == null) {
            if (rights.isAdmin(user)) {
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
              dbPass = scenario.getDbPass();
              dbPort = scenario.getDbPort();
              dbName = scenario.getDbName();
              startDate = scenario.getStartTime();
              endDate = scenario.getEndTime();

              if (scenario.getScenario() != null) {
                originalScenarioId = scenario.getScenario().getId();
              }

              if (scriptPath != null && !scriptPath.isEmpty()) {
                File absPath = getSourceFile(scriptPath);
                scriptFile = absPath;
                scriptStream = uploader.getFileToDownload(absPath);
              }

              if (imagePath != null) {
                File absPath = getSourceFile(imagePath);
                imageFile = absPath;
                imageStream = uploader.getFileToDownload(absPath);
              }
            }

          }
        }
      } catch (NumberFormatException e) {
        if (rights.isAdmin(user)) {
          userHasRights = true;
        }
      } catch (Exception e) {
        LOGGER.error("ERROR GETTING SCENARIO FIELDS FROM DATABASE", e);
      }
    }

    // scenarioName = "testname";
    // description = "testbeschreibung";
    // scriptPath = "scenario_01.sql";
    //
    // dbHost = "localhost";
    // dbUser = "blabla";
    // dbPass = "testpassword";
    // dbPort = "3306";
    // dbName = "dev_ex";

    updateRightScripts();
  }

  /**
   *
   *
   * @param fileName
   * @return
   */
  public File getSourceFile(String fileName) {
    File file = new File(scriptSystemPath + File.separator + RESOURCE_PATH + File.separator
        + scenario.getId() + File.separator + fileName);
    return file;
  }

  /**
   *
   *
   * @return
   */
  public String getAvailableTables() {
    if (scenario != null && connectionPool != null) {
      ArrayList<String> tables = connectionPool.getScenarioTableNamesWithHash(scenario);
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
    File absPath = getSourceFile(scriptPath);
    scriptStream = uploader.getFileToDownload(absPath);
  }

  /**
   *
   *
   */
  public void resetImageFile() {
    File absPath = getSourceFile(imagePath);
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

    String allowCreation = System.getProperty("ALLOW_DB_CREATION");
    boolean hasCreateRights = (allowCreation != null && StringTools.parseBoolean(allowCreation));

    try {
      if (!hasHost()) {
        message = System.getProperty("EDIT_SC.EMPTY_DBHOST");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasPort()) {
        message = System.getProperty("EDIT_SC.EMPTY_DBPORT");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasDBName()) {
        message = System.getProperty("EDIT_SC.EMPTY_DBNAME");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasUser()) {
        message = System.getProperty("EDIT_SC.EMPTY_DBUSER");
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
        message = System.getProperty("EDIT_SC.EMPTY_NAME");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasDescription()) {
        message = System.getProperty("EDIT_SC.EMPTY_DESCRIPTION");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (!hasScriptPath()) {
        message = System.getProperty("EDIT_SC.EMPTY_DBSCRIPT");
        sev = FacesMessage.SEVERITY_ERROR;
      }

      if (startDate != null && endDate != null) {
        if (startDate.after(endDate)) {
          message = System.getProperty("EDIT_SC.TIME_CONFLICT1");
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
              message = System.getProperty("EDIT_SC.TIME_CONFLICT2") + ": ID[" + group.getId()
                  + "]";
              sev = FacesMessage.SEVERITY_ERROR;
            }
          }

          if (groupEnd != null && endDate != null) {
            if (groupEnd.after(endDate)) {
              message = System.getProperty("EDIT_SC.TIME_CONFLICT3") + ": ID[" + group.getId()
                  + "]";
              sev = FacesMessage.SEVERITY_ERROR;
            }
          }
        }
      }

      if (scriptPath != null) {
        if (!scriptPath.trim().toLowerCase().endsWith(".sql")) {
          message = System.getProperty("EDIT_SC.FALSE_ENDING") + ": \".sql\"!";
          sev = FacesMessage.SEVERITY_ERROR;
        }
      }

      if (imagePath != null) {
        String img = imagePath.trim().toLowerCase();
        if (!img.endsWith(".jpg") && !img.endsWith(".jpeg") && !img.endsWith(".gif")
            && !img.endsWith(".png")) {
          message = System.getProperty("EDIT_SC.NO_DIAGRAM");
          sev = FacesMessage.SEVERITY_ERROR;
        }
      }

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
            dbHost = System.getProperty("MAIN_DBHOST");
            dbPort = System.getProperty("MAIN_DBPORT");

            if (!hasUser()) {
              dbUser = "usr_" + dbName.replace("sqltsdb_", "");
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
                message = System.getProperty("EDIT_SC.RIGHTS_ERROR");
                importSyntaxError = "check permission for user-management";
                sev = FacesMessage.SEVERITY_ERROR;
              } else {
                databaseChanged = true;
              }
            }

          } catch (Exception e) {
            message = System.getProperty("EDIT_SC.RIGHTS_ERROR");
            sev = FacesMessage.SEVERITY_ERROR;
            importSyntaxError = StringTools.trimToLengthIndicator(ExceptionUtils.getStackTrace(e),
                600);
          }
        }

        if (newScenario) {
          if (message == null) {
            if (tempScenario != null) {
              scenarioDao.insertNewInstance(tempScenario);
            }
            List<Scenario> tempScenarios = scenarioDao.findByExample(tempScenario);
            Scenario temp = null;
            if (tempScenarios != null && !tempScenarios.isEmpty()) {
              temp = tempScenarios.get(0);
            }

            if (temp != null) {
              tempScenario = temp;
              LOGGER.info("going to copy: \n" + imageFile + "\n" + scriptFile);
              if (!uploader.copy(scriptFile, imageFile, String.valueOf(temp.getId()))) {
                message = System.getProperty("EDIT_SC.COPY_ERROR");
                sev = FacesMessage.SEVERITY_ERROR;
              }
            } else {
              message = System.getProperty("ERROR") + ". \n"
                  + System.getProperty("EDIT_SC.SC_NOT_FOUND");
              sev = FacesMessage.SEVERITY_ERROR;
            }
          }
        } else {
          if (scenario != null) {
            connectionPool.removeScenario(scenario, databaseChanged);
            scenarioDao.updateInstance(tempScenario);
          }
        }

        connectionPool.updateScenarios();

        String error = connectionPool.getError(tempScenario);

        if (error != null) {
          importSyntaxError = error;
          message = System.getProperty("EDIT_SC.SYNTAX_ERROR");
          sev = FacesMessage.SEVERITY_ERROR;
          scenario = tempScenario;
          if (scenario != null) {
            scenarioDao.updateInstance(scenario);
          }
        } else {
          sev = FacesMessage.SEVERITY_INFO;
          DateFormat df = new SimpleDateFormat("HH:mm:ss");
          message = System.getProperty("SUCCESS") + " (" + df.format(new Date()) + ")";
          scenario = tempScenario;
          if (scenario != null) {
            ac.setScenario(scenario);
          }
          updateRightScripts();
        }

      }
    } catch (Exception e) {
      message = System.getProperty("ERROR");
      sev = FacesMessage.SEVERITY_ERROR;
      importSyntaxError = StringTools.trimToLengthIndicator(ExceptionUtils.getStackTrace(e), 600);
      LOGGER.error("failed saving scenario:" + scenario, e);

      scenario = tempScenario;
      if (scenario != null) {
        scenarioDao.updateInstance(scenario);
      }
    }

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);
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
    FacesMessage msg = new FacesMessage(sev, System.getProperty("EDIT_SC.UPLOAD_SUCCESS"), System
        .getProperty("EDIT_SC.NEW_NAME")
        + ": " + scriptPath);
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
    FacesMessage msg = new FacesMessage(sev, System.getProperty("EDIT_SC.UPLOAD_SUCCESS"), System
        .getProperty("EDIT_SC.NEW_NAME")
        + ": " + scriptPath);
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
