package de.uniwue.info6.database.map.daos;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UserRightDao.java
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

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserRight;

/**
 *
 *
 * @author Michael
 */
@ManagedBean
@ViewScoped
public class UserRightDao extends DaoTools<UserRight> {

  private static final long serialVersionUID = 1L;
  private static final Log log = LogFactory.getLog(UserRightDao.class);

  // ******************************************************************
  // custom (not generated methods)
  // ******************************************************************

  /**
   * @param typeClass
   */
  public UserRightDao() {
    super(UserRight.class);
  }

  /**
   *
   *
   * @param id
   * @return
   */
  public List<UserRight> getByUser(User user) {
    UserRight example = new UserRight();
    example.setUser(user);
    List<UserRight> rights = findByExample(example);
    return rights;
  }

  /**
   *
   *
   * @param id
   * @return
   */
  public UserRight getById(int id) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      return findById(id, session);
    } catch (Exception e) {
      log.error("custom hibernate operation failed", e);
      return null;
    } finally {
      endTransaction(session, success);
    }
  }

  /**
   *
   *
   * @param instance
   * @return
   */
  public List<UserRight> findByExample(UserRight instance) {
    boolean success = true;

    Session session = null;
    try {
      session = startTransaction();
      return (List<UserRight>) findByExampleHbn(instance, session);
    } catch (Exception e) {
      log.error("custom hibernate operation failed", e);
      success = false;
      return null;
    } finally {
      endTransaction(session, success);
    }
  }

  // ******************************************************************
  // generated methods of hibernate
  // ******************************************************************

  public UserRight findById(java.lang.Integer id, Session session) {
    log.debug("getting UserRight instance with id: " + id);
    try {
      UserRight instance = (UserRight) session.get("de.uniwue.info6.database.map.UserRight", id);
      if (instance == null) {
        log.debug("get successful, no instance found");
      } else {
        log.debug("get successful, instance found");
      }
      return instance;
    } catch (RuntimeException re) {
      log.error("get failed", re);
      throw re;
    }
  }

  public List<UserRight> findByExampleHbn(UserRight instance, Session session) {
    log.debug("finding UserRight instance by example");
    try {
      Criteria criteria = session.createCriteria("de.uniwue.info6.database.map.UserRight");
      criteria.add(Example.create(instance));
      if (instance.getUser() != null) {
        criteria.createAlias("user", "u").add(Restrictions.eq("u.id", instance.getUser().getId()));
      }

      if (instance.getScenario() != null) {
        criteria.createAlias("scenario", "s").add(Restrictions.eq("s.id", instance.getScenario().getId()));
      }

      @SuppressWarnings("unchecked")
      List<UserRight> results = criteria.list();
      log.debug("find by example successful, result size: " + results.size());
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      throw re;
    }
  }
}
