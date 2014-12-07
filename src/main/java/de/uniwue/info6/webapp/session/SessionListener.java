package de.uniwue.info6.webapp.session;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;

/**
 *
 *
 * @author Michael
 */
@ManagedBean
@SessionScoped
public class SessionListener implements HttpSessionListener, Serializable {

  // main static list of all users
  private static ConcurrentHashMap<HttpSession, User> users;
  private static ConcurrentHashMap<User, Scenario> scenarios;
  private static ConcurrentHashMap<Date, String> executerStats;
  private static ConcurrentHashMap<Date, String> sessionStats;

  private static final Log LOGGER = LogFactory.getLog(SessionListener.class);
  private static final long serialVersionUID = 1L;



  /**
   *
   * @param session
   * @param user
   */
  public static void addUser(HttpSession session, User user, Scenario scenario) {
    if (user != null) {
      try {
        // lock.lock();
        if (users.containsValue(user)) {
          List<HttpSession> tmp = new ArrayList<HttpSession>();
          tmp.addAll(getKeysByValue(users, user));
          for (HttpSession ses : tmp) {
            users.remove(ses);
          }
        }
        users.put(session, user);
        if (scenario != null) {
          if (scenarios.containsKey(user)) {
            scenarios.remove(user);
          }
          scenarios.put(user, scenario);
        }

        setSessionStat(String.valueOf(getTotalActiveSession()));
      } catch (Exception e) {
        LOGGER.error("error adding user to session object", e);
      } finally {
        // lock.unlock();
      }
    }
  }

  /**
   *
   *
   * @param user
   */
  public static void removeUser(User user) {
    try {
      // lock.lock();
      if (users.containsValue(user)) {
        List<HttpSession> usersToRemove = new ArrayList<HttpSession>();
        for (HttpSession st : users.keySet()) {
          if (users.get(st).equals(user)) {
            usersToRemove.add(st);
          }
        }
        for (HttpSession st : usersToRemove) {
          users.remove(st);
          st.invalidate();
        }
        setSessionStat(String.valueOf(getTotalActiveSession()));
      }
    } catch (Exception e) {
      LOGGER.error("error removing user to session object", e);
    } finally {
      // lock.unlock();
    }
  }

  /**
   *
   *
   * @param session
   */
  public static void removeSession(HttpSession session) {
    try {
      // lock.lock();
      if (users.containsKey(session)) {
        users.remove(session);
      }
    } catch (Exception e) {
      LOGGER.error("error removing user from user list", e);
    } finally {
      // lock.unlock();
    }
  }

  /**
   *
   *
   * @param session
   */
  public static void removeUser(HttpSession session) {
    if (users.containsKey(session)) {
      User userToRemove = users.get(session);
      if (userToRemove != null) {
        try {
          LOGGER.info("USER: \"" + users.get(session).getId() + "\" KICKED OUT.");
          removeTempTables(userToRemove);
          removeUser(userToRemove);
        } catch (Exception e) {
          LOGGER.error("error removing user", e);
        }
      }
    }
  }

  /**
   *
   *
   * @return
   */
  public boolean showIEError() {
    try {
      SessionObject ac = new SessionCollector().getSessionObject();
      if (ac != null) {
        return ac.getShowIEError();
      }
    } catch (Exception e) {
      //
    }
    return false;
  }

  /**
   *
   *
   */
  public void disableIEError() {
    try {
      SessionObject ac = new SessionCollector().getSessionObject();
      if (ac != null) {
        ac.setShowIEError(false);
      }
    } catch (Exception e) {
      //
    }
  }

  /**
   *
   */
  static {
    if (users == null) {
      users = new ConcurrentHashMap<HttpSession, User>();
    }
    if (scenarios == null) {
      scenarios = new ConcurrentHashMap<User, Scenario>();
    }
  }

  /**
   *
   *
   * @return
   */
  public static int getTotalActiveSession() {
    if (users != null) {
      return users.size();
    } else {
      return 0;
    }
  }

  /**
   *
   *
   * @param session
   * @return
   */
  public static boolean sessionExists(HttpSession session) {
    if (session != null && users.containsKey(session)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   *
   * @param user
   * @return
   */
  public static boolean userExists(User user) {
    if (user != null && users.containsValue(user)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   *
   * @param map
   * @param value
   * @return
   */
  public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
    Set<T> keys = new HashSet<T>();
    for (Entry<T, E> entry : map.entrySet()) {
      if (value.equals(entry.getValue())) {
        keys.add(entry.getKey());
      }
    }
    return keys;
  }

  /**
   *
   *
   * @param user
   * @throws SQLException
   */
  public static void removeTempTables(User user) throws SQLException {
    ConnectionManager pool = ConnectionManager.instance();
    Scenario scenario = scenarios.get(user);
    if (user != null && scenario != null) {
      pool.resetDatabaseTablesForUser(scenario, user);
    }
  }

  /**
   * @return the users
   */
  public static ConcurrentHashMap<HttpSession, User> getUsers() {
    return users;
  }

  /**
   * @return the executerStats
   */
  public static ConcurrentHashMap<Date, String> getExecuterStats() {
    return executerStats;
  }

  /**
   * @return the sessionStats
   */
  public static ConcurrentHashMap<Date, String> getSessionStats() {
    return sessionStats;
  }

  @Override
  public void sessionCreated(HttpSessionEvent arg0) {
    arg0.getSession().setMaxInactiveInterval(
        Integer.parseInt(System.getProperty("SESSION_TIMEOUT")));
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent arg0) {
    removeUser(arg0.getSession());
  }

  /**
   *
   *
   * @param stat
   */
  public static void setExecuterStat(String stat) {
    try {
      if (executerStats == null) {
        executerStats = new ConcurrentHashMap<Date, String>();
      } else if (stat != null && !stat.trim().isEmpty()) {
        executerStats.put(new Date(), stat);
      }
    } catch (Exception e) {
      LOGGER.info("STAT LOG FAILED", e);
    }
  }

  /**
   *
   *
   * @param stat
   */
  public static void setSessionStat(String stat) {
    try {
      if (sessionStats == null) {
        sessionStats = new ConcurrentHashMap<Date, String>();
      } else if (stat != null && !stat.replace("\\s", "").isEmpty()) {
        sessionStats.put(new Date(), stat);
      }
    } catch (Exception e) {
      LOGGER.info("STAT LOG FAILED", e);
    }
  }

}
