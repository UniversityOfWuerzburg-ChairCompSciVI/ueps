package de.uniwue.info6.webapp.session;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SessionListener.java
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

import static de.uniwue.info6.misc.properties.PropInteger.SESSION_TIMEOUT;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

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




import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.misc.properties.Cfg;

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

  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SessionListener.class);
  private static final long serialVersionUID = 1L;

  /**
   *
   * @param session
   * @param user
   */
  public static void addUser(HttpSession session, User user, Scenario scenario) {
    if (user != null) {
      try {
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
    }
  }

  /**
   *
   *
   * @param session
   */
  public static void removeSession(HttpSession session) {
    try {
      if (users.containsKey(session)) {
        users.remove(session);
      }
    } catch (Exception e) {
      LOGGER.error("error removing user from user list", e);
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
      SessionObject ac = SessionObject.pullFromSession();
      if (ac != null) {
        return ac.getShowInternetExplorerWarning();
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
      SessionObject ac = SessionObject.pullFromSession();
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
   * @param user
   * @return
   */
  public static boolean userExists(String userID) {
    if (users != null) {
      for (User user : users.values()) {
        return user.getId().equals(userID);
      }
    }
    return false;
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
      pool.dropDatabaseTablesForUser(scenario, user);
    }
  }

  /**
   *
   *
   * @param session
   * @return
   */
  public static User getUser(final HttpSession session) {
    if (session != null && users != null && users.containsKey(session)) {
      return users.get(session);
    }
    return null;
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
    int timeout = Cfg.inst().getProp(MAIN_CONFIG, SESSION_TIMEOUT);
    timeout = (timeout - 30 > 1) ? (timeout - 30) : timeout;
    arg0.getSession().setMaxInactiveInterval(timeout);
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
