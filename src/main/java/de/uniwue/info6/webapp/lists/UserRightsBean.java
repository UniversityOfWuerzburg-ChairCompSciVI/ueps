package de.uniwue.info6.webapp.lists;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 * @author Christian
 *
 */
@ManagedBean(name = "rights_bean")
@ViewScoped
public class UserRightsBean implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private UserRightDao userRightDao;

  private List<UserRight> rights;
  private List<UserRight> filteredRights;

  private UserRight selectedRight;
  private ScenarioDao scenarioDao;
  private UserDao userDao;

  private String userId;
  private int scenarioId;
  private boolean canEditGroups;
  private boolean canAssert;

  private List<Scenario> scenarios;
  private SessionObject ac;
  private User user;
  private UserRights userRights;
  private List<User> adminList;

  @PostConstruct
  public void init() {

    ac = new SessionCollector().getSessionObject();
    user = ac.getUser();

    userRightDao = new UserRightDao();
    scenarioDao = new ScenarioDao();
    userDao = new UserDao();

    rights = userRightDao.findAll();

    this.adminList = new ArrayList<User>();
    String admins = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropString.ADMINS);

    String[] adminArray = null;
    userRights = new UserRights().initialize();
    scenarios = scenarioDao.findAll();

    if (admins != null) {
      adminArray = admins.split(";");
    }

    if (adminArray != null) {
      for (String ad : adminArray) {
        User user = userDao.getById(ad);
        if (user != null) {
          if (userRights.isAdmin(user)) {
            adminList.add(user);
          }
        }
      }
    }

    User adminExample = new User();
    adminExample.setIsAdmin(true);
    List<User> adList = userDao.findByExample(adminExample);
    if (adList != null) {
      for (User us : adList) {
        if (!adminList.contains(us)) {
          adminList.add(us);
        }
      }
    }

    for (UserRight ur : rights) {
      ur.setScenario(scenarioDao.getById(ur.getScenario().getId()));
      ur.setUser(userDao.getById(ur.getUser().getId()));
    }
  }

  public void deleteSelectedRight() {
    userRightDao.deleteInstance(selectedRight);
    rights.remove(selectedRight);
    selectedRight = null;
  }

  public String getAdmins() {
    String admins = "";
    if (adminList != null && !adminList.isEmpty()) {
      for (User a : adminList) {
        admins += "[" + a.getId() + "] ";
      }
    }
    return admins;
  }

  public boolean userHasRights() {
    return userRights.isAdmin(user);
  }

  public void insert() {

    User tmpUser = userDao.getById(userId);

    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;
    boolean saved = false;
    boolean noneed = false;

    if (tmpUser != null) {

      Scenario tmpSce = scenarioDao.getById(scenarioId);
      UserRight tmp = new UserRight(tmpUser, null, tmpSce, canAssert, canEditGroups, false, false, false);
      List<UserRight> rightData = userRightDao.getByUser(tmpUser);
      for (UserRight item : rightData) {
        if (item.getScenario().getId().equals(scenarioId)) {
          sev = FacesMessage.SEVERITY_ERROR;
          message = "Schon vorhanden.";
          noneed = true;
        }
      }

      if (!noneed)
        saved = userRightDao.insertNewInstance(tmp);

      if (saved) {
        rights.add(tmp);
        sev = FacesMessage.SEVERITY_INFO;
        message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.SUCCESS") + ".";
      } else if (!noneed) {
        sev = FacesMessage.SEVERITY_ERROR;
        message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.FAIL") + ".";
      }

    } else {
      sev = FacesMessage.SEVERITY_ERROR;
      message = "User nicht gefunden";
    }

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);

  }

  public void toggleSelectedRight(String what) {

    if (what.equals("edit")) {
      selectedRight.setHasGroupEditingRights(!selectedRight.getHasGroupEditingRights());
    } else if (what.equals("assert")) {
      selectedRight.setHasRatingRights(!selectedRight.getHasRatingRights());
    }

    userRightDao.updateInstance(selectedRight);
    selectedRight = null;

  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getScenarioId() {
    return scenarioId;
  }

  public void setScenarioId(int scenarioId) {
    this.scenarioId = scenarioId;
  }

  public boolean getCanEditGroups() {
    return canEditGroups;
  }

  public void setCanEditGroups(boolean canEdit) {
    this.canEditGroups = canEdit;
  }

  public boolean getCanAssert() {
    return canAssert;
  }

  public boolean userNotHasRights() {
    return !userHasRights();
  }

  public void setCanAssert(boolean canAssert) {
    this.canAssert = canAssert;
  }

  public UserRightsBean() {
  }

  /**
   * @return the rights
   */
  public List<UserRight> getRights() {
    return rights;
  }

  /**
   * @param rights
   *          the rights to set
   */
  public void setRights(List<UserRight> rights) {
    this.rights = rights;
  }

  /**
   * @return the filteredRights
   */
  public List<UserRight> getFilteredRights() {
    return filteredRights;
  }

  /**
   * @param filteredRights
   *          the filteredRights to set
   */
  public void setFilteredRights(List<UserRight> filteredRights) {
    this.filteredRights = filteredRights;
  }

  public UserRight getSelectedRight() {
    return selectedRight;
  }

  public void setSelectedRight(UserRight selectedRight) {
    this.selectedRight = selectedRight;
  }

  public List<Scenario> getScenarios() {
    return scenarios;
  }

  public void setScenarios(List<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

}
