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

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.misc.Crypt;

import static de.uniwue.info6.misc.properties.PropBool.*;
import static de.uniwue.info6.misc.properties.PropString.*;
import static de.uniwue.info6.misc.properties.PropInteger.*;
import static de.uniwue.info6.misc.properties.PropertiesFile.*;
import de.uniwue.info6.misc.properties.Cfg;

/**
 *
 *
 * @author Michael
 */
public class SessionObject {
  private String userID, secureValue, scenarioID, userIP;
  private final static String sessionPosition = "auth_controller";
  private static final Log LOGGER = LogFactory.getLog(SessionObject.class);
  private User user;
  private HttpSession session;
  private Scenario scenario;
  private ExerciseGroup exerciseGroup;
  private Boolean validCredentials, logedIn, showIEError;
  private static final Lock lock = new ReentrantLock();

  /**
   *
   */
  public SessionObject(HttpSession session) {
    this.session = session;
    saveToSession();
  }

  /**
   *
   *
   * @param userID
   * @param secureValue
   * @param scenario
   * @param session
   */
  public SessionObject init(String userID, String secureValue, String scenario, String ip) {
    this.user = null;
    this.showIEError = true;

    this.userID = userID.trim();
    this.secureValue = secureValue.trim();
    this.scenarioID = scenario.trim();
    this.userIP = ip;

    this.validCredentials = checkCredentials();

    if (validCredentials) {
      loadUser();
    } else {
      LOGGER.info("FALSE CREDENTIALS [\"" + userID + ":" + secureValue + "\"]");
    }
    return this;
  }

  /**
   *
   *
   * @param userIP
   * @param userID
   * @param secureValue
   * @return
   */
  private static boolean isValid(String userIP, String userID, String secureValue) {
    return Crypt.md5(userIP + Cfg.inst().getProp(MAIN_CONFIG,SECRET_ID_STRING) + userID).equals(secureValue);
  }

  /**
   *
   *
   * @return
   */
  private boolean checkCredentials() {
    if (scenarioID != null && !scenarioID.isEmpty()) {
      // get scenario object from database
      try {
        this.scenario = new ScenarioDao().getById(Integer.parseInt(scenarioID));
      } catch (Exception e) {
        LOGGER.info("get scenario error: \"" + scenarioID + "\"]", e);
      }
    }

    if (userID != null && secureValue != null && userIP != null && !userID.isEmpty()
        && !secureValue.isEmpty()) {
      if (Cfg.inst().getProp(MAIN_CONFIG,USE_MOODLE_LOGIN)) {
        return isValid(userIP, userID, secureValue);
      } else {
        return true;
      }
    }
    return false;
  }

  /**
   *
   *
   * @param par
   * @return
   */
  private boolean parseBoolean(String par) {
    if (par.trim().equalsIgnoreCase("true")) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   */
  private synchronized boolean loadUser() {
    boolean userFound = false;
    try {
      // lock.lock();
      UserDao dao = new UserDao();
      this.user = dao.getById(userID);

      // if user was not found in the db, so insert him
      if (this.user == null) {
        User newUser = new User(userID, new Date());
        newUser.setIsAdmin(false);
        dao.insertNewInstance(newUser);
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // try again
      this.user = dao.getById(userID);

      if (this.user == null) {
        LOGGER.error("USER WITH ID: \"" + userID + "\" COULD NOT SAVED TO DB!");
      } else {
        LOGGER.info("USER WITH ID: \"" + userID + "\" INSERTED INTO DB!");
        userFound = true;
      }
    } catch (Exception e) {
      LOGGER.info("ERROR LOADING USER: [\"" + userID + "\"]");
      return false;
    } finally {
      // lock.unlock();
    }
    return userFound;
  }

  /**
   *
   *
   */
  private void saveToSession() {
    session.setAttribute(sessionPosition, this);
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
    return exerciseGroup;
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
   * @param validCredentials
   *          the validCredentials to set
   */
  public void setValidCredentials(Boolean validCredentials) {
    this.validCredentials = validCredentials;
  }

  /**
   * @return the showIEError
   */
  public Boolean getShowIEError() {
    return showIEError;
  }

  /**
   * @param showIEError
   *          the showIEError to set
   */
  public void setShowIEError(Boolean showIEError) {
    this.showIEError = showIEError;
  }

}
