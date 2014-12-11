package de.uniwue.info6.webapp.admin;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.misc.DateFormatTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "edit_gr")
@ViewScoped
public class AdminEditGroup implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final Log LOGGER = LogFactory.getLog(AdminEditGroup.class);

  private static final String GROUP_PARAM = "group";
  private static final String SCENARIO_PARAM = "scenario";
  private static final String EMPTY_FIELD = "---";

  private ScenarioDao scenarioDao;
  private ExerciseGroupDao exerciseGroupDao;

  private Scenario scenario;
  private ExerciseGroup exerciseGroup;

  private User user;
  private SessionObject ac;

  private Integer originalGroupId;

  private String groupName;
  private String groupNameOriginal;

  private Integer rated;
  private Integer ratedOriginal;

  private Date startDate;
  private Date startDateOriginal;

  private Date endDate;
  private Date endDateOriginal;

  private Date lastModified;
  private boolean isNewGroup;
  private boolean userHasRights;

  private UserRights rights;

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

    if (scenario != null) {
      if (rated == null) {
        rated = 1;
      }

      if (groupName == null || groupName.trim().isEmpty()) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_GROUP.EMPTY_GROUP");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (startDate != null && endDate != null && startDate.after(endDate)) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_GROUP.TIME_CONFLICT1");
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (scenario.getStartTime() != null && startDate != null
          && startDate.before(scenario.getStartTime())) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_GROUP.TIME_CONFLICT2") + ": ["
            + DateFormatTools.getGermanFormat(scenario.getStartTime()) + "]";
        sev = FacesMessage.SEVERITY_ERROR;
      } else if (scenario.getEndTime() != null && endDate != null
          && endDate.after(scenario.getEndTime())) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_GROUP.TIME_CONFLICT3") + ": ["
            + DateFormatTools.getGermanFormat(scenario.getEndTime()) + "]";
        sev = FacesMessage.SEVERITY_ERROR;
      }

      if (message == null) {
        try {
          if (isNewGroup) {
            exerciseGroup = new ExerciseGroup();
            exerciseGroup.setScenario(scenario);
          }
          exerciseGroup.setLastModified(new Date());
          exerciseGroup.setStartTime(startDate);
          exerciseGroup.setEndTime(endDate);
          exerciseGroup.setName(groupName);

          if (rated == 1) {
            exerciseGroup.setIsRated(false);
          } else if (rated == 2 || rated == 3) {
            exerciseGroup.setIsRated(true);
          }

          if (rated == 3) {
            exerciseGroup.setDescription("[NO_FEEDBACK]");
          } else {
            exerciseGroup.setDescription(null);
          }

          if (isNewGroup) {
            exerciseGroupDao.insertNewInstance(exerciseGroup);
            isNewGroup = false;
          } else {
            exerciseGroupDao.updateInstance(exerciseGroup);
          }

          sev = FacesMessage.SEVERITY_INFO;
          DateFormat df = new SimpleDateFormat("HH:mm:ss");

          message = Cfg.inst().getProp(DEF_LANGUAGE, "SUCCESS") + " (" + df.format(new Date()) + ")";
        } catch (Exception e) {
          LOGGER.error("failed saving exercise-group:" + exerciseGroup, e);
          sev = FacesMessage.SEVERITY_ERROR;
          message = Cfg.inst().getProp(DEF_LANGUAGE, "ERROR");
        }
      }
      msg = new FacesMessage(sev, message, details);
      FacesContext.getCurrentInstance().addMessage(null, msg);
    }
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    scenarioDao = new ScenarioDao();
    exerciseGroupDao = new ExerciseGroupDao();
    Map<String, String> requestParams = ec.getRequestParameterMap();
    ac = new SessionCollector().getSessionObject();
    user = ac.getUser();
    rights = new UserRights().initialize();
    userHasRights = false;

    if (!requestParams.isEmpty()) {
      try {
        // exercise get-parameter found
        if (requestParams.containsKey(GROUP_PARAM)) {
          String param = requestParams.get(GROUP_PARAM);
          final int id = Integer.parseInt(param);
          exerciseGroup = exerciseGroupDao.getById(id);

          if (exerciseGroup != null) {
            if (rights.hasEditingRight(user, exerciseGroup)) {
              userHasRights = true;
              scenario = scenarioDao.getById(exerciseGroup.getScenario().getId());
              groupName = exerciseGroup.getName();
              groupNameOriginal = groupName;
              Boolean temp = exerciseGroup.getIsRated();
              if (temp == null || !temp) {
                rated = 1;
              } else {
                if (exerciseGroup.getDescription() == null
                    || exerciseGroup.getDescription().trim().isEmpty()) {
                  rated = 2;
                } else if (exerciseGroup.getDescription().trim().equals("[NO_FEEDBACK]")) {
                  rated = 3;
                }
              }
              ratedOriginal = rated;
              startDate = exerciseGroup.getStartTime();
              startDateOriginal = startDate;
              endDate = exerciseGroup.getEndTime();
              endDateOriginal = endDate;
              lastModified = exerciseGroup.getLastModified();
              if (exerciseGroup.getExerciseGroup() != null) {
                originalGroupId = exerciseGroup.getExerciseGroup().getId();
              }
            }
          }
        } else if (requestParams.containsKey(SCENARIO_PARAM)) {
          String param = requestParams.get(SCENARIO_PARAM);
          final int id = Integer.parseInt(param);
          scenario = scenarioDao.getById(id);
          if (scenario != null && rights.hasEditingRight(user, scenario)) {
            isNewGroup = true;
            userHasRights = true;
          }
        }
      } catch (NumberFormatException e) {
        // new Exercise-Group
      } catch (Exception e) {
        LOGGER.error("ERROR GETTING EXERCISE GROUP FIELDS FROM DATABASE", e);
      }
    }
  }

  /**
   *
   *
   * @return
   */
  public String getType() {
    if (rated == null || rated == 1) {
      return Cfg.inst().getProp(DEF_LANGUAGE, "UNRATED");
    }
    if (rated == 2) {
      return Cfg.inst().getProp(DEF_LANGUAGE, "RATED");
    }
    return Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_GROUP.RATED_NO_FEED");
  }

  /**
   *
   *
   * @return
   */
  public boolean hasEditingRights() {
    if (exerciseGroup != null) {
      return rights.checkIfExerciseGroupCanBeEdited(exerciseGroup);
    }
    return true;
  }

  /**
   *
   *
   * @return
   */
  public boolean disableEditingFields() {
    return !hasEditingRights();
  }

  /**
   *
   *
   * @return
   */
  public boolean hasGroupName() {
    return groupName != null && !groupName.isEmpty();
  }

  /**
   *
   *
   * @return
   */
  public boolean hasOriginalGroup() {
    return originalGroupId != null;
  }

  /**
   *
   *
   * @return
   */
  public String getScenarioId() {
    if (scenario != null) {
      return scenario.getName() + " [ID: " + scenario.getId() + "]";
    }
    return null;
  }

  /**
   * @return the scenario
   */
  public Scenario getScenario() {
    return scenario;
  }

  /**
   * @param scenario
   *          the scenario to set
   */
  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * @return the originalGroupId
   */
  public Integer getOriginalGroupId() {
    return originalGroupId;
  }

  /**
   * @param originalGroupId
   *          the originalGroupId to set
   */
  public void setOriginalGroupId(Integer originalGroupId) {
    this.originalGroupId = originalGroupId;
  }

  /**
   * @return the groupName
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * @param groupName
   *          the groupName to set
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  /**
   * @return the rated
   */
  public Integer getRated() {
    return rated;
  }

  /**
   * @param rated
   *          the rated to set
   */
  public void setRated(Integer rated) {
    this.rated = rated;
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
   * @param ex
   * @return
   */
  public String getLastModifiedTime() {
    if (exerciseGroup != null) {
      Date lastMod = exerciseGroup.getLastModified();
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
    groupName = groupNameOriginal;
    rated = ratedOriginal;
    startDate = startDateOriginal;
    endDate = endDateOriginal;
  }
}
