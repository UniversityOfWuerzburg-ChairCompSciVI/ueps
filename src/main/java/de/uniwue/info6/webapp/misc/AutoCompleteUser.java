package de.uniwue.info6.webapp.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AutoCompleteUser.java
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "user_names")
@ViewScoped
public class AutoCompleteUser {

  private UserRights rights;
  private UserDao dao;
  private ExerciseDao exerciseDao;
  private List<User> users;
  private String notFound;
  private String exerciseID;
  private UserEntryDao userEntryDao;
  private User user;

  /**
   *
   */
  public AutoCompleteUser() {
    //
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    rights = new UserRights().initialize();
    dao = new UserDao();
    exerciseDao = new ExerciseDao();
    userEntryDao = new UserEntryDao();
    notFound = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.USER_NOT_FOUND");

    SessionObject ac = SessionObject.pullFromSession();
    if (ac != null) {
      user = ac.getUser();
    }
    this.rightsInit();
  }

  /**
   *
   *
   * @param exerciseID
   * @return
   */
  public void rightsInit() {
    if (users == null && user != null) {
      List<User> temp = dao.findAll();

      // remove current user from list
      temp.remove(user);

      users = new ArrayList<User>();

      if (temp != null) {
        for (User us : temp) {
          if (us != null && !us.getId().equals("DEBUG_USER")) {
            users.add(us);
          }
        }
      }
    }
  }

  /**
   *
   *
   * @param exerciseID
   * @return
   */
  public void exerciseInit(String exerciseID) {
    if (users == null && user != null) {
      users = new ArrayList<User>();
      List<User> temp = null;

      if (exerciseID != null && !exerciseID.isEmpty()
          && (this.exerciseID == null || !this.exerciseID.equals(exerciseID))) {
        this.exerciseID = StringTools.extractIDFromAutoComplete(exerciseID);
        if (this.exerciseID != null) {
          Exercise exercise = exerciseDao.getById(Integer.parseInt(this.exerciseID));
          if (exercise != null) {
            temp = dao.getRelevantUsersByExercise(exercise);
          }
        }
      }

      if (temp == null) {
        temp = dao.findAll();
      }

      if (temp != null) {
        for (User us : temp) {
          if (us != null && userEntryDao.hasEntries(us) && !us.getId().equals("DEBUG_USER")) {
            users.add(us);
          }
        }
      }
    }
  }

  /**
   *
   *
   * @param query
   * @return
   */
  public List<String> complete(String query) {
    FacesContext context = FacesContext.getCurrentInstance();
    String exerciseID = (String) UIComponent.getCurrentComponent(context).getAttributes().get("exerciseID");
    List<String> results = new ArrayList<String>();

    if (exerciseID != null) {
      this.exerciseInit(exerciseID);
    } else {
      this.rightsInit();
      results.add("[" + Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.EMPTY_FIELD") + "]");
    }

    if (users != null && rights != null) {
      for (User user : users) {
        if (user.getId().contains(query.trim())) {
          results.add(user.getId());
        }
      }
    }

    if (results.size() < 1 && notFound != null) {
      results = new ArrayList<String>();
      results.add(notFound);
    }

    return results;
  }
}
