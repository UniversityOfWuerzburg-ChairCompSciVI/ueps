package de.uniwue.info6.database.map.daos;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ScenarioDao.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import de.uniwue.info6.database.map.Scenario;

/**
 *
 *
 * @author Michael
 */
public class ScenarioDao extends DaoTools<Scenario> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final Log log = LogFactory.getLog(ScenarioDao.class);

  // ******************************************************************
  // custom (not generated methods)
  // ******************************************************************

  /**
   * @param typeClass
   */
  public ScenarioDao() {
    super(Scenario.class);
  }

  /**
   *
   *
   * @param name
   * @return
   */
  public synchronized Scenario getByName(String name) {
    Scenario obj = new Scenario();
    obj.setName(name);
    List<Scenario> objs = findByExample(obj);

    if (objs != null && !objs.isEmpty()) {
      obj = (Scenario) objs.get(0);
      return obj;
    }
    return null;
  }

  /**
   *
   *
   * @param id
   * @return
   */
  public Scenario getById(int id) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      return findById(id, session);
    } catch (Exception e) {
      log.error("custom hibernate operation failed", e);
      success = false;
      return null;
    } finally {
      endTransaction(session, success);
    }
  }

  /**
   *
   *
   * @param scenario
   * @return
   */
  public boolean checkIfExists(Scenario scenario) {
    if (scenario != null && scenario.getId() != null && getById(scenario.getId()) != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   *
   * @param instance
   * @return
   */
  public List<Scenario> findByExample(Scenario instance) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      return findByExampleHbn(instance, session);
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

  public Scenario findById(java.lang.Integer id, Session session) {
    log.debug("getting Scenario instance with id: " + id);
    try {
      Scenario instance = (Scenario) session.get("de.uniwue.info6.database.map.Scenario", id);
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

  public List<Scenario> findByExampleHbn(Scenario instance, Session session) {
    log.debug("finding Scenario instance by example");
    try {
      @SuppressWarnings("unchecked")
      List<Scenario> results = session.createCriteria("de.uniwue.info6.database.map.Scenario")
                               .add(Example.create(instance)).list();
      log.debug("find by example successful, result size: " + results.size());
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      throw re;
    }
  }
}
