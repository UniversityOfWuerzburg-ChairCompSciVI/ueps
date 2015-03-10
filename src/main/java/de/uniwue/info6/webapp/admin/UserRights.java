package de.uniwue.info6.webapp.admin;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UserRights.java
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "rights")
@ViewScoped
public class UserRights implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserRights.class);

  private UserRightDao userRightDao;
  private ExerciseGroupDao exerciseGroupDao;
  private ExerciseDao exerciseDao;
  private UserEntryDao userEntryDao;
  private SessionObject session;
  private UserDao userDao;
  private ScenarioDao scenarioDao;
  private List<Scenario> scenarioList;

  /**
   *
   */
  public UserRights() {
    // ---
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    this.userRightDao = new UserRightDao();
    this.exerciseGroupDao = new ExerciseGroupDao();
    this.exerciseDao = new ExerciseDao();
    this.userEntryDao = new UserEntryDao();
    this.userDao = new UserDao();
    this.scenarioDao = new ScenarioDao();
    this.scenarioList = scenarioDao.findAll();
  }

  /**
   * @return
   *
   *
   */
  private boolean validSession() {
    if (session == null) {
      session = SessionObject.pullFromSession();
    }
    if (session != null) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public UserRights initialize() {
    init();
    return this;
  }

  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   */
  public boolean hasRights(User user, Scenario scenario) {
    return hasEditingRight(user, scenario, true) || hasEditingRight(user, scenario, false) || hasRatingRight(user, scenario);
  }

  /**
   *
   *
   * @return
   */
  public boolean hasScenario() {
    try {
      if (this.validSession()) {
        Scenario scenario = session.getScenario();
        return scenario != null;
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING SCENARIO", e);
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean showExerciseLink() {
    if (this.validSession() && session.getScenario() != null && session.getUser() != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   */
  public boolean hasViewRights(User user, Scenario scenario) {
    boolean userHasRights = true;
    if (isAdmin(user)) {
      return true;
    }

    if (user != null && scenario != null) {
      if (scenario != null) {
        if (scenario.getStartTime() != null && scenario.getStartTime().after(new Date())) {
          userHasRights = false;
        }
        if (scenario.getEndTime() != null && scenario.getEndTime().before(new Date())) {
          userHasRights = false;
        }
      }
    } else {
      userHasRights = false;
    }
    return userHasRights;
  }

  /**
   *
   *
   * @param user
   * @param scenario
   * @param exerciseGroup
   * @return
   */
  public boolean hasViewRights(User user, Scenario scenario, ExerciseGroup exerciseGroup) {
    boolean userHasRights = true;
    if (isAdmin(user)) {
      return true;
    }

    if (exerciseGroup != null && scenario != null && user != null) {
      if (!hasRights(user, scenario)) {
        if (scenario.getId().equals(exerciseGroup.getScenario().getId())) {
          if (scenario.getStartTime() != null && scenario.getStartTime().after(new Date())) {
            userHasRights = false;
          }
          if (scenario.getEndTime() != null && scenario.getEndTime().before(new Date())) {
            userHasRights = false;
          }
          if (exerciseGroup.getStartTime() != null
              && exerciseGroup.getStartTime().after(new Date())) {
            userHasRights = false;
          }
        }
      }
    } else {
      userHasRights = false;
    }
    return userHasRights;
  }


  /**
   *
   *
   * @param group
   * @return
   */
  public boolean showResults(ExerciseGroup group) {
    if (group != null) {
      // if exercise group is not rated
      if (group.getIsRated() != null && !group.getIsRated()) {
        return true;
      }

      if (!entriesCanBeEdited(group) && group.getAutoReleaseRating() != null && group.getAutoReleaseRating()) {
        return true;
      }
    }
    return false;
  }


  /**
   *
   *
   * @param group
   * @return
   */
  public boolean entriesCanBeEdited(ExerciseGroup group) {
    if (group != null) {
      if (group.getIsRated() != null && !group.getIsRated()) {
        return true;
      }

      Date end = group.getEndTime();
      if (end != null) {
        if (end.before(new Date())) {
          return false;
        }
      } else {
        return true;
      }
    }
    return true;
  }


  /**
   *
   *
   * @param group
   * @return
   */
  public boolean canAcceptEntries(ExerciseGroup group) {
    if (group != null) {
      Date end = group.getEndTime();
      if (end != null) {
        if (new Date().before(end)) {
          return true;
        }
      }
    }
    return false;
  }

  // ------------------------------------------------ //
  // --
  // ------------------------------------------------ //

  // TODO: delete methods

  /**
   *
   *
   * @param sc
   * @return
   */
  public boolean checkIfScenarioCanBeEdited(Scenario sc) {
    List<ExerciseGroup> groups = exerciseGroupDao.findByScenario(sc);
    for (ExerciseGroup gr : groups) {
      if (!checkIfExerciseGroupCanBeEdited(gr)) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   *
   * @param sc
   * @return
   */
  public boolean checkIfExerciseCanBeEdited(Exercise ex) {
    List<UserEntry> entries = userEntryDao.getLastEntriesForExercise(ex);
    for (UserEntry entry : entries) {
      User user = userDao.getById(entry.getUser().getId());
      if (!hasEditingRight(user, ex) && !hasRatingRight(user, ex)) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   *
   * @param gr
   * @return
   */
  public boolean checkIfExerciseGroupCanBeEdited(ExerciseGroup gr) {
    if (gr.getIsRated()) {
      List<Exercise> exercises = exerciseDao.findByExGroup(gr);
      for (Exercise ex : exercises) {
        if (!checkIfExerciseCanBeEdited(ex)) {
          return false;
        }
      }
    }
    return true;
  }

  // ------------------------------------------------ //
  // --
  // ------------------------------------------------ //


  /**
   *
   *
   * @return
   */
  public boolean hasEditingRight() {
    if (this.validSession()) {
      return hasEditingRight(session.getUser());
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasEditingRight(User user) {
    try {
      if (user != null) {
        if (isAdmin(user)) {
          return true;
        }
        for (Scenario sc : scenarioList) {
          final List<ExerciseGroup> groups = exerciseGroupDao.findByScenario(sc);
          if (hasEditingRight(user, sc)) {
            return true;
          }
          for (ExerciseGroup group : groups) {
            if (hasEditingRight(user, group)) {
              return true;
            }
          }
        }

        if (this.validSession()) {
          Scenario scenario = session.getScenario();
          return hasEditingRight(user, scenario);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING EDITING RIGHTS", e);
    }
    return false;
  }


  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   */
  public boolean hasEditingRight(User user, Scenario scenario) {
    return hasEditingRight(user, scenario, false);
  }

  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   */
  public boolean hasEditingRight(User user, Scenario scenario, boolean groupsOnly) {
    if (user != null) {
      user.pull();
      if (isAdmin(user)) {
        return true;
      }
      List<UserRight> rights = userRightDao.getByUser(user);

      if (rights != null) {
        for (UserRight right : rights) {
          Scenario sc = right.getScenario();
          if (((groupsOnly && right.getHasGroupEditingRights()) ||
               (!groupsOnly && right.getHasScenarioEditingRights()))
              && scenario.getId().equals(sc.getId())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   *
   *
   * @param user
   * @param group
   * @return
   */
  public boolean hasEditingRight(final User user, final ExerciseGroup group) {
    if (group != null) {
      return hasEditingRight(user, group.pull().getScenario(), true);
    }
    return false;
  }


  /**
   *
   *
   * @param user
   * @param exercise
   * @return
   */
  public boolean hasEditingRight(final User user, final Exercise exercise) {
    if (user != null && exercise != null) {
      final ExerciseGroup exerciseGroup = exercise.getExerciseGroup();
      if (exerciseGroup != null) {
        return hasEditingRight(user, exerciseGroupDao.getById(exerciseGroup.getId()));
      }
    }
    return false;
  }


  // ------------------------------------------------ //
  // --
  // ------------------------------------------------ //


  /**
   *
   *
   * @return
   */
  public boolean hasRatingRight() {
    if (this.validSession()) {
      return hasRatingRight(session.getUser());
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasRatingRight(User user) {
    try {
      if (user != null) {
        if (this.scenarioList == null) {
          this.scenarioList = scenarioDao.findAll();
        }
        for (Scenario sc : scenarioList) {
          if (hasRatingRight(user, sc)) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING RATING RIGHTS", e);
    }
    // TODO: shit
    return false;
  }

  /**
   *
   *
   * @param user
   * @param exercise
   * @return
   */
  public boolean hasRatingRight(User user, Exercise exercise) {
    if (user != null && exercise != null) {
      return hasRatingRight(user, exercise.getExerciseGroup());
    }
    return false;
  }

  /**
   *
   *
   * @param user
   * @param group
   * @return
   */
  public boolean hasRatingRight(User user, ExerciseGroup group) {
    if (user != null && group != null) {
      return hasRatingRight(user, exerciseGroupDao.getById(group.getId()).getScenario());
    }
    return false;
  }

  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   */
  public boolean hasRatingRight(User user, Scenario scenario) {
    if (user != null && scenario != null) {
      if (isAdmin(user)) {
        return true;
      }
      List<UserRight> rights = userRightDao.getByUser(user);

      if (rights != null) {
        for (UserRight right : rights) {
          Scenario sc = right.getScenario();
          if (right.getHasRatingRights() && scenario.getId() == sc.getId()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  // ------------------------------------------------ //
  // --
  // ------------------------------------------------ //

  /**
   *
   *
   * @return
   */
  public boolean hasUserAddingRights() {
    if (this.validSession()) {
      return hasUserAddingRights(session.getUser());
    }
    return false;
  }

  /**
   *
   *
   * @param user
   * @return
   */
  public boolean hasUserAddingRights(User user) {
    try {
      if (user != null) {
        if (isAdmin(user)) {
          return true;
        }

        boolean hasRights = false;
        if (this.scenarioList == null) {
          this.scenarioList = scenarioDao.findAll();
        }
        for (Scenario sc : scenarioList) {
          if (hasUserAddingRights(user, sc)) {
            hasRights = true;
          }
        }
        return hasRights;
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING USER EDITING RIGHTS", e);
    }
    return false;
  }


  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   *
   * @throws Exception
   */
  public boolean hasUserAddingRights(User user, Scenario scenario) throws Exception {
    if (isAdmin(user)) {
      return true;
    }

    if (isLecturer(user)) {
      List<UserRight> rightsList = this.userRightDao.getByUser(user);
      for (UserRight rights : rightsList) {
        if (rights.getScenario().getId().equals(scenario.getId())) {
          return true;
        }
      }
    }

    return false;
  }

  // ------------------------------------------------ //
  // --
  // ------------------------------------------------ //


  /**
   *
   *
   * @return
   */
  public boolean isAdminOrLecturer() {
    try {
      if (this.validSession()) {
        User user = session.getUser();
        if (user != null) {
          return isAdminOrLecturer(user);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING ADMIN-LECTURER RIGHTS", e);
    }
    return false;
  }

  /**
   *
   *
   * @param user
   * @return
   */
  public boolean isAdminOrLecturer(User user) {
    if (isAdmin(user) || isLecturer(user)) {
      return true;
    } else {
      return false;
    }
  }

  // ------------------------------------------------ //
  // --
  // ------------------------------------------------ //

  /**
   *
   *
   * @return
   */
  public List<User> getLecturers() {
    final List<User> lecturerList = new ArrayList<User>();
    final User lecturerExample = new User();
    lecturerExample.setIsLecturer(true);
    List<User> lcList = userDao.findByExample(lecturerExample);
    if (lcList != null) {
      lecturerList.addAll(lcList);
    }
    return lecturerList;
  }

  /**
   *
   *
   * @param user
   * @return
   */
  public boolean isLecturer(User user) {
    if (user != null && user.getIsLecturer() != null && user.getIsLecturer()) {
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
  public boolean isLecturer() {
    try {
      if (this.validSession()) {
        User user = session.getUser();
        if (user != null) {
          return isLecturer(user);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING LECTURER RIGHTS", e);
    }
    return false;
  }

  /**
   *
   *
   * @param user
   * @return
   */
  public List<Scenario> getScenariosOfLecturer(User user) {
    UserRight example = new UserRight();
    example.setUser(user);
    List<UserRight> lecturerRights = this.userRightDao.findByExample(example);
    List<Scenario> scenarios = new ArrayList<Scenario>();
    for (UserRight r : lecturerRights) {
      Scenario sc = r.getScenario();
      if (!scenarios.contains(sc)) {
        scenarios.add(scenarioDao.getById(sc.getId()));
      }
    }
    return scenarios;
  }

  // ------------------------------------------------ //
  // --
  // ------------------------------------------------ //



  /**
   *
   *
   * @return
   */
  public boolean isAdmin() {
    try {
      if (this.validSession()) {
        User user = session.getUser();
        if (user != null) {
          return isAdmin(user);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING ADMIN RIGHTS", e);
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean isAdmin(User user) {
    boolean admin = false;
    if (user != null) {
      try {
        // TODO: necessary?
        user = userDao.getById(user.getId());
        String admins = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG,
                                           PropString.ADMINS);
        String[] adminArray = null;

        if (admins != null) {
          adminArray = admins.split(";");
        }

        if (user != null) {
          if (user.getIsAdmin() != null) {
            admin = user.getIsAdmin();
          }
          if (adminArray != null) {
            for (String ad : adminArray) {
              if (user.getId().equals(ad)) {
                admin = true;
              }
            }
          }
        }
      } catch (Exception e) {
        LOGGER.error("ERROR CHECKING ADMIN RIGHTS: [" + user + "]", e);
      }
    }
    return admin;
  }

  /**
   *
   *
   * @return
   */
  public List<User> getAdmins() {
    final List<User> adminList = new ArrayList<User>();

    // get admin list from 'config.properties' {{{
    String[] adminArray = getAdminsFromConfigFile();
    if (adminArray != null) {
      for (String ad : adminArray) {
        User user = userDao.getById(ad.trim());
        if (user != null) {
          if (isAdmin(user)) {
            adminList.add(user);
          }
        }
      }
    }
    // }}}

    // get admin list from database {{{
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
    // }}}
    return adminList;
  }

  /**
   * get admin list from 'config.properties'.
   *
   * @return
   */
  public String[] getAdminsFromConfigFile() {
    final String admins = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropString.ADMINS);
    String[] adminArray = null;
    if (admins != null) {
      adminArray = admins.split(";");
    }
    return adminArray;
  }
}
