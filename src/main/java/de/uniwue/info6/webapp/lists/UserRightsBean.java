package de.uniwue.info6.webapp.lists;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UserRightsBean.java
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
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.webapp.admin.UserRights;
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

  private List<Scenario> scenarios;
  private SessionObject ac;
  private User loggedInUser;
  private UserRights userRights;
  private List<User> adminList;
  private List<User> lecturerList;
  private User selectedLecturer;

  // ------------------------------------------------ //

  private boolean canAssert;
  private boolean canEditGroups;
  private boolean canEditScenario;

  private boolean disableAssert;
  private boolean disableEditGroups;
  private boolean disableEditScenario;

  private boolean disableSave;

  // ------------------------------------------------ //

  private String userId;
  private String createdByUserId;
  private int scenarioId;
  private String userStatus;
  private String userStatusClass;
  private User userToSave;

  // ------------------------------------------------ //

  /**
   *
   *
   */
  @PostConstruct
  public void init() {

    this.ac = SessionObject.pullFromSession();
    this.loggedInUser = ac.getUser();

    this.userRightDao = new UserRightDao();
    this.scenarioDao = new ScenarioDao();
    this.userDao = new UserDao();
    this.userRights = new UserRights().initialize();


    if (userRights.isAdmin(loggedInUser)) {
      this.rights = userRightDao.findAll();
    } else if (userRights.isLecturer(loggedInUser)) {
      this.rights = userRightDao.getByCreator(loggedInUser);
    }

    this.lecturerList = new ArrayList<User>();
    this.adminList = userRights.getAdmins();

    this.lecturerList = userRights.getLecturers();
    this.scenarios = null;

    if (this.userRights.isAdmin()) {
      scenarios = scenarioDao.findAll();
    } else if (this.userRights.isLecturer()) {
      scenarios = this.userRights.getScenariosOfLecturer(this.ac.getUser());
    }

    if (rights != null) {
      for (UserRight userRight : rights) {
        if (userRight.getScenario() != null ) {
          userRight.setScenario(scenarioDao.getById(userRight.getScenario().getId()));
          userRight.setUser(userDao.getById(userRight.getUser().getId()));
        }
      }
      this.validateUser();
    }
  }

  /**
   *
   *
   * @return
   */
  public boolean hasUserStatus() {
    if (userId != null && userStatus != null) {
      return true;
    } else {
      return false;
    }
  }


  /**
   *
   *
   * @return
   */
  public String getUserStatus() {
    return this.userStatus;
  }

  /**
   *
   *
   * @param userStatus
   */
  public void setUserStatus(String userStatus) {
    this.userStatus = userStatus;
  }

  /**
   *
   *
   * @return
   */
  public String getUserStatusClass() {
    return this.userStatusClass;
  }

  /**
   *
   *
   * @param userStatus
   */
  public void setUserStatusClass(String userStatusClass) {
    this.userStatusClass = userStatusClass;
  }

  /**
   *
   *
   * @return
   */
  public List<User> getLecturerList() {
    return this.lecturerList;
  }

  /**
   *
   *
   * @param lecturerList
   */
  public void setLecturerList(List<User> lecturerList) {
    this.lecturerList = lecturerList;
  }

  /**
   *
   *
   * @param lecturer
   */
  public void setSelectedLecturer(User lecturer) {
    this.selectedLecturer = lecturer;
  }


  /**
   *
   *
   * @param selectedUser
   * @return
   */
  public String getUserStatusFontClass(String selectedUser) {
    if (selectedUser != null) {
      return getUserStatusFontClass(userDao.getById(selectedUser));
    }
    return "";
  }

  /**
   *
   *
   * @param selectedUser
   * @return
   */
  private String getUserStatusFontClass(User selectedUser) {
    if (selectedUser != null) {
      if (userRights.isAdmin(selectedUser)) {
        return "adminFont";
      } else if (userRights.isLecturer(selectedUser)) {
        return "lecturerFont";
      } else {
        return "studentFont";
      }
    }
    return "";
  }

  /**
   *
   *
   * @param event
   */
  public void handleDialogGroupChange() {
    if (!canEditGroups) {
      setCanEditScenario(false);
    }
  }

  /**
   *
   *
   */
  public void handleDialogScenarioChange() {
    if (canEditScenario) {
      setCanEditGroups(true);
    }
  }


  /**
   *
   *
   * @param event
   */
  public void validateUser() {
    this.userStatus = "---";
    this.userStatusClass = null;

    this.disableSave = true;
    this.disableAssert = true;
    this.disableEditGroups = true;
    this.disableEditScenario = true;

    this.canAssert = false;
    this.canEditGroups = false;
    this.canEditScenario = false;

    if (this.userId != null) {
      this.userToSave = userDao.getById(userId);
      if (userToSave != null) {
        if (userRights.isAdmin(userToSave)) {
          this.userStatus = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.ADMIN");
          this.disableSave = true;
          this.canAssert = true;
          this.canEditGroups = true;
          this.canEditScenario = true;
        } else if (userRights.isLecturer(userToSave)) {
          this.userStatus = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.LECTURER");
          if (userRights.isLecturer(loggedInUser)) {
            this.disableSave = true;
          } else {
            this.disableSave = false;
          }
          this.canAssert = true;
          this.canEditGroups = true;
          this.disableEditScenario = false;
        } else {
          this.userStatus = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.STUDENT");
          this.disableSave = false;
          this.disableAssert = false;
          this.disableEditGroups = false;
          this.disableEditScenario = false;
        }

        this.userStatusClass = getUserStatusFontClass(userToSave);
      }
    }
  }

  /**
   *
   *
   * @param lecturer
   */
  public void deleteSelectedLecturer() {
    String message = null;
    Severity sev = null;
    FacesMessage msg = null;
    boolean error = false;

    final boolean showCaseMode = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.SHOWCASE_MODE);

    if (!showCaseMode) {
      // ------------------------------------------------ //
      // delete all rights given by the lecturer
      UserRight example = new UserRight();
      example.setCreatedByUser(this.selectedLecturer);
      List<UserRight> givenRights = this.userRightDao.findByExample(example);
      for (UserRight right : givenRights) {
        this.rights.remove(right);
        this.userRightDao.deleteInstance(right);
      }
      // ------------------------------------------------ //

      if (this.selectedLecturer != null) {
        this.selectedLecturer.setIsLecturer(false);

        if (userDao.updateInstance(this.selectedLecturer)) {
          sev = FacesMessage.SEVERITY_INFO;
          message = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.LECTURER_REMOVED");
        } else {
          error = true;
        }
        this.lecturerList = userRights.getLecturers();
      }


      if (error) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "ERROR");
        sev = FacesMessage.SEVERITY_ERROR;
      }

    } else {
      sev = FacesMessage.SEVERITY_ERROR;
      message = Cfg.inst().getProp(DEF_LANGUAGE, "SHOWCASE_ERROR");
    }

    msg = new FacesMessage(sev, message, null);
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }

  /**
   *
   *
   */
  public void deleteSelectedRight() {
    String message = null;
    Severity sev = null;
    FacesMessage msg = null;
    boolean error = false;

    final boolean showCaseMode = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.SHOWCASE_MODE);
    if (!showCaseMode) {
      if (selectedRight != null) {
        if (userRightDao.deleteInstance(selectedRight)) {
          rights.remove(selectedRight);
          selectedRight = null;

          sev = FacesMessage.SEVERITY_INFO;
          message = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.RIGHTS_REMOVED");
        } else {
          error = true;
        }
      } else {
        error = true;
      }

      if (error) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "ERROR");
        sev = FacesMessage.SEVERITY_ERROR;
      }
    } else {
      sev = FacesMessage.SEVERITY_ERROR;
      message = Cfg.inst().getProp(DEF_LANGUAGE, "SHOWCASE_ERROR");
    }
    msg = new FacesMessage(sev, message, null);
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }

  /**
   *
   *
   * @return
   */
  public String getAdmins() {
    String admins = "";
    if (adminList != null && !adminList.isEmpty()) {
      for (User a : adminList) {
        admins +=  a.getId() + ", ";
      }
    }

    if (!admins.isEmpty()) {
      admins = admins.substring(0, admins.length() - 2);
    }
    return admins;
  }

  /**
   *
   *
   * @param right
   */
  public boolean disableToggleButtons(UserRight right) {
    if (right != null) {
      if (userRights.isAdmin(right.getUser()) || userRights.isLecturer(right.getUser())) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean userHasRights() {
    return userRights.isAdmin(loggedInUser);
  }

  /**
   *
   *
   */
  public void insertScenarioRights() {
    // User userToSave = userDao.getById(userId);

    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;

    if (userToSave != null) {
      Scenario selectedScenario = scenarioDao.getById(scenarioId);

      // you can't add scenario rights to an admin {{{
      if (userRights.isAdmin(userToSave)) {
        sev = FacesMessage.SEVERITY_ERROR;
        message = Cfg.inst().getProp(DEF_LANGUAGE, "ERROR");
      }
      // }}}

      if (selectedScenario == null) {
        sev = FacesMessage.SEVERITY_ERROR;
        message = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.SCENARIO_NOT_FOUND");
      }

      UserRight rightsToSave = null;
      if (message == null) {
        rightsToSave = new UserRight(userToSave, loggedInUser, selectedScenario, canAssert, canEditGroups, canEditScenario);
        // check if scenario rights already exist {{{
        List<UserRight> rightData = userRightDao.getByUser(userToSave);
        for (UserRight item : rightData) {
          if (item.getScenario().getId().equals(scenarioId)) {
            sev = FacesMessage.SEVERITY_ERROR;
            message = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.USER_EXISTS");
          }
        }
        // }}}
      }

      if (message == null) {
        if (userRightDao.insertNewInstance(rightsToSave)) {
          rights.add(rightsToSave);
          sev = FacesMessage.SEVERITY_INFO;
          message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.SUCCESS") + ".";
        } else {
          sev = FacesMessage.SEVERITY_ERROR;
          message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.FAIL") + ".";
        }
      }
    } else {
      sev = FacesMessage.SEVERITY_ERROR;
      message = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.USER_NOT_FOUND");
    }

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);

  }

  /**
   *
   *
   */
  public void insertLecturer() {
    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;

    User userToInsert = userDao.getById(userId);

    if (userToInsert != null) {

      if (userRights.isAdmin(userToInsert)) {
        sev = FacesMessage.SEVERITY_ERROR;
        // message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.FAIL") + ".";
        message = "Nutzer besitzt bereits Admin-Rechte";
      }

      if (message == null) {
        if ( userToInsert.getIsLecturer() == null || !userToInsert.getIsLecturer()) {
          userToInsert.setIsLecturer(true);

          if (userDao.updateInstance(userToInsert)) {
            sev = FacesMessage.SEVERITY_INFO;
            message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.SUCCESS") + ".";
            this.lecturerList = userRights.getLecturers();
          }
        } else {
          sev = FacesMessage.SEVERITY_ERROR;
          message = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.ALREADY_INSERTED", userToInsert.getId());
        }
      }
    }

    if (message == null) {
      sev = FacesMessage.SEVERITY_ERROR;
      message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.FAIL") + ".";
    }

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }


  /**
   *
   *
   * @param rightToModify
   */
  public void toggleGroupEditingRight(UserRight rightToModify) {
    if (rightToModify != null && !userRights.isAdmin(rightToModify.getUser()) && !userRights.isLecturer(rightToModify.getUser())) {
      boolean newValue = !rightToModify.getHasGroupEditingRights();
      rightToModify.setHasGroupEditingRights(newValue);

      if (!newValue) {
        rightToModify.setHasScenarioEditingRights(false);
      }
      userRightDao.updateInstance(rightToModify);
    }
  }

  /**
   *
   *
   * @param rightToModify
   */
  public void toggleAssertRight(UserRight rightToModify) {
    if (rightToModify != null && !userRights.isAdmin(rightToModify.getUser()) && !userRights.isLecturer(rightToModify.getUser())) {
      rightToModify.setHasRatingRights(!rightToModify.getHasRatingRights());
      userRightDao.updateInstance(rightToModify);
    }
  }

  /**
   *
   *
   * @param rightToModify
   */
  public void toggleScenarioEditingRight(UserRight rightToModify) {
    if (rightToModify != null) {
      boolean newValue = !rightToModify.getHasScenarioEditingRights();
      rightToModify.setHasScenarioEditingRights(newValue);

      if (newValue) {
        rightToModify.setHasGroupEditingRights(true);
      }

      userRightDao.updateInstance(rightToModify);
    }
  }

  /**
   *
   *
   * @param what
   */
  public void toggleSelectedRight(UserRight selectedRight, String what) {

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

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId(String createdByUserId) {
    this.createdByUserId = createdByUserId;
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

  public boolean getCanEditScenario() {
    return canEditScenario;
  }

  public void setCanEditScenario(boolean canEdit) {
    this.canEditScenario = canEdit;
  }

  public boolean getCanAssert() {
    return canAssert;
  }

  public void setCanAssert(boolean canAssert) {
    this.canAssert = canAssert;
  }




  public boolean getDisableEditGroups() {
    return disableEditGroups;
  }

  public void setDisableEditGroups(boolean disableEdit) {
    this.disableEditGroups = disableEdit;
  }

  public boolean getDisableEditScenario() {
    return disableEditScenario;
  }

  public void setDisableEditScenario(boolean disableEdit) {
    this.disableEditScenario = disableEdit;
  }

  public boolean getDisableAssert() {
    return disableAssert;
  }

  public void setDisableAssert(boolean disableAssert) {
    this.disableAssert = disableAssert;
  }

  public boolean getDisableSave() {
    return disableSave;
  }

  public void setDisableSave(boolean disableSave) {
    this.disableSave = disableSave;
  }

  public boolean userNotHasRights() {
    return !userHasRights();
  }

  public UserRightsBean() {}

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
