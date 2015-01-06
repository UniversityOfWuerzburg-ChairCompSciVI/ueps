package de.uniwue.info6.webapp.misc;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionCollector;
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
  private boolean rightsMode;

  /**
   *
   */
  public AutoCompleteUser() {
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    rightsMode = false;
    rights = new UserRights().initialize();
    dao = new UserDao();
    exerciseDao = new ExerciseDao();
    userEntryDao = new UserEntryDao();
    notFound = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.USER_NOT_FOUND");

    SessionObject ac = new SessionCollector().getSessionObject();
    if (ac != null) {
      user = ac.getUser();
    }
  }

  /**
   *
   *
   * @param exerciseID
   * @return
   */
  public AutoCompleteUser rightsInit() {
    rightsMode = true;
    List<User> temp = dao.findAll();
    users = new ArrayList<User>();

    if (temp != null) {
      for (User us : temp) {
        if (us != null && !us.getId().equals("DEBUG_USER")) {
          users.add(us);
        }
      }
    }
    return this;
  }

  /**
   *
   *
   * @param exerciseID
   * @return
   */
  public AutoCompleteUser exerciseInit(String exerciseID) {
    if (user != null) {
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
    return this;
  }

  /**
   *
   *
   * @param query
   * @return
   */
  public List<String> complete(String query) {
    List<String> results = new ArrayList<String>();
    if (!rightsMode) {
      results.add("[" + Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.EMPTY_FIELD") + "]");
    }

    if (users != null && rights != null) {
      for (User user : users) {
        if (user.getId().contains(query.trim())) {
          // System.out.println(user.getId() + " " + query.trim());
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
