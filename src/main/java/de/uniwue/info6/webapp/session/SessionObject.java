package de.uniwue.info6.webapp.session;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SessionObject.java
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

import static de.uniwue.info6.misc.properties.PropBool.SHOWCASE_MODE;
import static de.uniwue.info6.misc.properties.PropBool.USE_MOODLE_LOGIN;
import static de.uniwue.info6.misc.properties.PropString.SECRET_PHRASE;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.Crypt;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.admin.UserRights;

/**
 *
 *
 * @author Michael
 */
public class SessionObject  implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = -1080188490640321456L;
  public final static String SESSION_POSITION = "SESS_USER";

  public final static String DEMO_STUDENT = "demo_student";
  public final static String DEMO_ADMIN = "demo_admin";
  public final static String DEMO_LECTURER = "demo_lecturer";

  private String userID, encryptedCode, scenarioID, userIP, secretPhrase;
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SessionObject.class);
  private User user;
  private HttpSession session;
  private Scenario scenario;
  private ExerciseGroup exerciseGroup;
  private boolean validCredentials, showInternetExplorerWarning, showInfoMessage;
  private UserDao userDao;
  private UserRightDao userRightDao;
  private ScenarioDao scenarioDao;
  private UserRights userRights;
  private boolean useMoodleLogin;

  /**
   *
   */
  public SessionObject(HttpSession session) {
    this.session = session;
    this.pushToSession();
  }

  /**
   *
   *
   * @param userID
   * @param encryptedCode
   * @param scenario
   * @param session
   */
  public boolean init(String userID, String encryptedCode, String scenarioID, String userIP) {
    this.scenarioID = scenarioID;
    this.useMoodleLogin = Cfg.inst().getProp(MAIN_CONFIG, USE_MOODLE_LOGIN);

    if (((userID == null || encryptedCode == null || userIP == null) ||
         (userID.equals(this.userID) && encryptedCode.equals(this.encryptedCode)
          && userIP.equals(this.userIP))) && this.useMoodleLogin) {
      return this.validCredentials;
    }

    this.userID           = userID;
    this.encryptedCode    = encryptedCode;
    this.userIP           = userIP;

    this.user = null;
    this.scenario = null;
    this.showInternetExplorerWarning = true;
    this.showInfoMessage = true;
    this.userDao = new UserDao();
    this.userRightDao = new UserRightDao();
    this.scenarioDao = new ScenarioDao();
    this.userRights = new UserRights().initialize();
    this.secretPhrase = Cfg.inst().getProp(MAIN_CONFIG, SECRET_PHRASE);
    this.validCredentials = checkCredentials();

    if (validCredentials) {
      loadUser();
    } else {
      LOGGER.info("FALSE CREDENTIALS [\"" + userID + ":" + encryptedCode + "\"]");
    }
    return this.validCredentials;
  }

  /**
   *
   *
   * @return
   */
  private boolean checkCredentials() {
    final boolean hasScenarioParameter = scenarioID != null && !scenarioID.trim().isEmpty();
    final boolean hasUserIDParameter = userID != null && !userID.trim().isEmpty();
    final boolean hasEncryptedCodeParameter = encryptedCode != null && !encryptedCode.trim().isEmpty();

    // ------------------------------------------------ //
    if (hasScenarioParameter) {
      try {
        // pull scenario from database
        this.setScenario(new ScenarioDao().getById(Integer.parseInt(scenarioID)));
      } catch (Exception e) {}
      if (this.scenario == null) {
        LOGGER.info("CAN'T FIND SCENARIO WITH ID: [" + scenarioID + "]");
      }
    }

    // ------------------------------------------------ //
    if (hasUserIDParameter && hasEncryptedCodeParameter) {
      if (Cfg.inst().getProp(MAIN_CONFIG, USE_MOODLE_LOGIN)) {

        final String calculatedEncryptedCode = Crypt.md5(this.userIP + this.secretPhrase + this.userID);
        final boolean validCredentials = calculatedEncryptedCode.equals(this.encryptedCode);

        LOGGER.debug(
          "\nUser Login:\n" +
          "User IP:\t\t\t"                + this.userIP + "\n" +
          "Secret phrase:\t\t\t"          + this.secretPhrase + "\n" +
          "User id:\t\t\t"                + this.userID + "\n" +
          "Calculated encrypted code:\t"  + calculatedEncryptedCode + "\n" +
          "Valid credentials:\t\t"        + validCredentials
        );

        return validCredentials;
      } else {
        return true;
      }
    }
    // ------------------------------------------------ //
    return false;
  }


  /**
   *
   *
   */
  private synchronized boolean loadUser() {
    boolean userFound = false;
    try {
      this.user = userDao.getById(userID);

      // if user was not found in the db, so insert him
      if (this.user == null) {
        final User newUser = new User(userID, new Date());
        final boolean showCaseMode = Cfg.inst().getProp(MAIN_CONFIG, SHOWCASE_MODE);

        User exampleAdmin = null;

        if (showCaseMode) {
          if (userID.startsWith(DEMO_ADMIN)) {
            newUser.setIsAdmin(true);
          } else if (userID.startsWith(DEMO_LECTURER)) {
            newUser.setIsLecturer(true);
            final User adminUser = new User();
            adminUser.setIsAdmin(true);
            final List<User> admins = userDao.findByExample(adminUser);

            if (admins != null && !admins.isEmpty()) {
              exampleAdmin = admins.get(0);
            }
          }
        }
        userDao.insertNewInstance(newUser);

        if (exampleAdmin != null) {
          final Scenario firstScenario = this.scenarioDao.getById(1);
          final UserRight lecturerRight = new UserRight(newUser, exampleAdmin, firstScenario, true, true, true);
          this.userRightDao.insertNewInstance(lecturerRight);
        }
      }

      // try again
      this.user = userDao.getById(userID);

      if (this.user == null) {
        LOGGER.error("USER WITH ID: \"" + userID + "\" COULD NOT SAVED TO DB!");
      } else {

        if (scenario != null) {
          ConnectionManager connectionPool = ConnectionManager.instance();
          try {
            connectionPool.resetTables(scenario, user, true);
          } catch (Exception e) {
            LOGGER.error("COULD NOT RESET TABLES", e);
          }
        }

        LOGGER.info("USER WITH ID: \"" + userID + "\" INSERTED INTO DB!");
        userFound = true;
      }

    } catch (Exception e) {
      LOGGER.info("ERROR LOADING USER: [\"" + userID + "\"]");
      return false;
    }
    return userFound;
  }

  /**
   *
   *
   * @return
   */
  public static SessionObject pullFromSession() {
    try {
      final FacesContext facesContext = FacesContext.getCurrentInstance();
      if (facesContext != null) {
        final HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        SessionObject sessionObject = null;
        if (session != null) {
          sessionObject = (SessionObject) session.getAttribute(SESSION_POSITION);
        }
        return sessionObject;
      }
    } catch (Exception e) {
      LOGGER.error("PROBLEM GETTING SAVED SESSION USER OBJECT", e);
    }
    return null;
  }

  /**
   *
   *
   */
  public void pushToSession() {
    this.session.setAttribute(SESSION_POSITION, this);
  }

  /**
  *
  */
  public void removeFromSession() {
    this.session.removeAttribute(SESSION_POSITION);
  }

  /**
   *
   *
   * @return
   */
  public boolean loginSuccessfull() {
    return validCredentials;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @return the scenario
   */
  public Scenario getScenario() {
    return scenario;
  }

  /**
   *
   *
   * @return
   */
  public String getUserIP() {
    return this.userIP;

  }

  /**
   *
   *
   * @return
   */
  public String getUserIDParameter() {
    return this.userID;
  }

  /**
   *
   *
   * @return
   */
  public String getSecureValueParameter() {
    return this.encryptedCode;
  }

  /**
   *
   *
   * @return
   */
  public String getScenarioParameter() {
    return this.scenarioID;
  }

  /**
   *
   *
   * @param scenarioParameter
   * @return
   */
  public String setScenarioParameter(String scenarioParameter) {
    return this.scenarioID = scenarioParameter;
  }

  /**
   * @param scenario
   *          the scenario to set
   */
  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * @return the exerciseGroup
   */
  public ExerciseGroup getExerciseGroup() {
    return this.exerciseGroup;
  }

  /**
   * @param exerciseGroup
   *          the exerciseGroup to set
   */
  public void setExerciseGroup(ExerciseGroup exerciseGroup) {
    this.exerciseGroup = exerciseGroup;
  }

  /**
   * @return the validCredentials
   */
  public Boolean getValidCredentials() {
    return validCredentials;
  }

  /**
   * @return the validCredentials
   */
  public boolean isValidCredentials() {
    return validCredentials;
  }

  /**
   * @param validCredentials the validCredentials to set
   */
  public void setValidCredentials(boolean validCredentials) {
    this.validCredentials = validCredentials;
  }

  /**
   * @param validCredentials
   *          the validCredentials to set
   */
  public void setValidCredentials(Boolean validCredentials) {
    this.validCredentials = validCredentials;
  }

  /**
   * @return the showInternetExplorerWarning
   */
  public boolean showInternetExplorerWarning() {
    return showInternetExplorerWarning;
  }

  /**
   * @param showInternetExplorerWarning the showInternetExplorerWarning to set
   */
  public void setShowInternetExplorerWarning(
    boolean showInternetExplorerWarning) {
    this.showInternetExplorerWarning = showInternetExplorerWarning;
  }

  /**
   * @return the showInfoMessage
   */
  public boolean showInfoMessage() {
    return showInfoMessage;
  }

  /**
   * @param showInfoMessage the showInfoMessage to set
   */
  public void setShowInfoMessage(boolean showInfoMessage) {
    this.showInfoMessage = showInfoMessage;
  }

  /**
   * @param showIEError
   *          the showIEError to set
   */
  public void setShowIEError(Boolean showIEError) {
    this.showInternetExplorerWarning = showIEError;
  }

}
