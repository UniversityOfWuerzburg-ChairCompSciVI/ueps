package de.uniwue.info6.webapp.admin;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.session.SessionCollector;
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
  private static final Log LOGGER = LogFactory.getLog(UserRights.class);

  private UserRightDao userRightDao;
  private ExerciseGroupDao exerciseGroupDao;
  private ExerciseDao exerciseDao;
  private UserEntryDao userEntryDao;
  private SessionObject ac;
  private UserDao userDao;

  /**
   *
   */
  public UserRights() {
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

  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   */
  public boolean hasRights(User user, Scenario scenario) {
    return hasEditingRight(user, scenario) || hasRatingRight(user, scenario);
  }

  /**
   *
   *
   * @param user
   * @param scenario
   * @return
   */
  public boolean hasEditingRight(User user, Scenario scenario) {
    if (user != null) {
      user = userDao.getById(user.getId());

      if (isAdmin(user)) {
        return true;
      }

      List<UserRight> rights = userRightDao.getByUser(user);

      if (rights != null) {
        for (UserRight right : rights) {
          Scenario sc = right.getScenario();
          if (right.getHasEditingRights() && scenario.getId() == sc.getId()) {
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
   * @param scenario
   * @return
   */
  public boolean hasRatingRight(User user, Scenario scenario) {
    if (user != null) {
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

  /**
   *
   *
   * @return
   */
  public boolean isAdmin() {
    try {
      if (ac == null) {
        ac = new SessionCollector().getSessionObject();
      }
      if (ac != null) {
        User user = ac.getUser();
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
    try {
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
    return admin;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasRatingRight() {
    try {
      if (ac == null) {
        ac = new SessionCollector().getSessionObject();
      }
      if (ac != null) {
        User user = ac.getUser();
        Scenario scenario = ac.getScenario();
        return hasRatingRight(user, scenario);
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING RATING RIGHTS", e);
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasScenario() {
    try {
      if (ac == null) {
        ac = new SessionCollector().getSessionObject();
      }
      if (ac != null) {
        Scenario scenario = ac.getScenario();
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
    if (ac == null) {
      ac = new SessionCollector().getSessionObject();
    }
    if (ac != null) {
      Scenario scenario = ac.getScenario();
      if (scenario != null) {
        if (ac.getUser() != null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasEditingRight() {
    try {
      if (ac == null) {
        ac = new SessionCollector().getSessionObject();
      }
      if (ac != null) {
        User user = ac.getUser();
        Scenario scenario = ac.getScenario();
        return hasEditingRight(user, scenario);
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
   * @param group
   * @return
   */
  public boolean hasEditingRight(User user, ExerciseGroup group) {
    return hasEditingRight(user, exerciseGroupDao.getById(group.getId()).getScenario());
  }

  /**
   *
   *
   * @param user
   * @param group
   * @return
   */
  public boolean hasRatingRight(User user, ExerciseGroup group) {
    return hasRatingRight(user, exerciseGroupDao.getById(group.getId()).getScenario());
  }

  /**
   *
   *
   * @param user
   * @param exercise
   * @return
   */
  public boolean hasEditingRight(User user, Exercise exercise) {
    if (user != null && exercise != null) {
      return hasEditingRight(user, exercise.getExerciseGroup());
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
   * @param scenario
   * @param exerciseGroup
   * @return
   */
  public boolean hasViewRights(User user, Scenario scenario, ExerciseGroup exerciseGroup) {
    boolean userHasRights = true;
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
}
