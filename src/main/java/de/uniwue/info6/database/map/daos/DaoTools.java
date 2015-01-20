package de.uniwue.info6.database.map.daos;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  DaoTools.java
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
import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

import de.uniwue.info6.database.map.conf.HibernateUtil;

/**
 *
 *
 * @author Michael
 */
public class DaoTools<T> implements Serializable {

  // ******************************************************************
  // methods concerning session-transactions
  // ******************************************************************

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static boolean STARTED = false;
  private static final Log log = LogFactory.getLog(ExerciseDao.class);
  private static final String FACT_ERROR = "Could not locate SessionFactory in JNDI";
  private final Class<?> typeClass;

  // ******************************************************************
  // session tools
  // ******************************************************************

  /**
   *
   */
  public DaoTools(Class<T> typeClass) {
    this.typeClass = typeClass;
  }

  /**
   *
   *
   *
   * @throws Exception
   */
  protected synchronized Session startTransaction() throws Exception {
    return startTransaction(null);
  }

  /**
   *
   *
   */
  protected synchronized void endTransaction(Session session) {
    endTransaction(session, true);
  }

  /**
   *
   *
   * @return
   */
  protected synchronized Session getSession() {
    Session session = HibernateUtil.getSessionFactory().openSession();
    if (!session.isOpen()) {
      session = HibernateUtil.getSessionFactory().openSession();
    }

    session.setFlushMode(FlushMode.MANUAL);
    ManagedSessionContext.bind(session);
    return session;
  }

  /**
   *
   *
   * @param obj
   *
   * @throws Exception
   */
  protected synchronized Session startTransaction(Object obj)
  throws Exception {
    try {
      Session session = getSession();
      int countRetries = 0;

      while (!session.isOpen()) {
        session = getSession();
        log.error("session is closed. trying to revive connection.");
        if (countRetries++ > 50) {
          break;
        }
        Thread.sleep(200);
      }

      if (session.isOpen()) {
        session.beginTransaction();
        if (obj != null) {
          session.save(obj);
        }
      }
      return session;
    } catch (Exception e) {
      log.info("custom hibernate operation failed", e);
      return null;
    }
  }

  /**
   *
   *
   */
  protected synchronized void endTransaction(Session session, boolean success) {
    if (session != null) {
      Transaction transaction = session.getTransaction();
      try {
        if (session.isOpen()) {
          if (transaction != null && transaction.isActive()) {
            ManagedSessionContext.unbind(HibernateUtil
                                         .getSessionFactory());
            session.flush();
            transaction.commit();
          } else if (!success) {
            if (transaction != null) {
              transaction.rollback();
            }
          }
        }
      } catch (Exception e) {
        if (transaction != null) {
          transaction.rollback();
        }
        log.error("custom hibernate operation failed", e);
      } finally {
        if (session != null && session.isConnected()) {
          session.close();
        }
      }
    }
  }

  /**
   *
   *
   * @param obj
   */
  protected synchronized void pull(Object obj, Session session) {
    if (obj != null) {
      try {
        session.refresh(obj);
        session.update(obj);
      } catch (Exception e) {
        log.error("custom hibernate operation failed", e);
      }
    }
  }

  /**
   *
   *
   */
  public synchronized void pullObject(Object obj) {
    Session session = null;
    if (obj != null) {
      boolean success = true;
      try {
        session = startTransaction();
        pull(obj, session);
      } catch (Exception e) {
        success = false;
        log.error("custom hibernate operation failed", e);
      } finally {
        endTransaction(session, success);
      }
    }
  }

  /**
   *
   *
   * @return
   */
  public synchronized SessionFactory getSessionFactory() {
    SessionFactory fac = null;
    try {
      fac = (SessionFactory) new InitialContext()
            .lookup("SessionFactory");
      if (fac == null) {
        throw new NullPointerException(FACT_ERROR + " [hibernate]");
      }
    } catch (Exception e) {
      try {
        if (!DaoTools.STARTED) {
          System.err
          .println("INFO (ueps): Starting hibernate session");
        }
        fac = HibernateUtil.getSessionFactory();
        if (fac == null) {
          throw new NullPointerException(FACT_ERROR + " [manual]");
        } else {
          DaoTools.STARTED = true;
        }
      } catch (Exception x) {
        log.error(FACT_ERROR, x);
        throw new IllegalStateException(FACT_ERROR);
      }
    }
    return fac;
  }

  // ******************************************************************
  // select-methods
  // ******************************************************************

  /**
   *
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<T> findAll() {
    Session session = null;
    boolean success = true;
    try {
      session = startTransaction();
      if (session != null) {
        return session.createCriteria(typeClass).list();
      } else {
        return null;
      }
    } catch (Exception e) {
      log.info("custom hibernate operation failed", e);
      success = false;
      return null;
    } finally {
      endTransaction(session, success);
    }
  }

  /**
   *
   *
   * @return
   */
  public T getRandom() {
    List<T> allEntries = findAll();
    if (allEntries != null && !allEntries.isEmpty()) {
      return allEntries.get((int) (Math.random() * allEntries.size()));
    } else {
      return null;
    }
  }

  // ******************************************************************
  // modification-methods
  // ******************************************************************

  /**
   *
   *
   * @return
   */
  public boolean insertNewInstance(T obj) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      persist(obj, session);
    } catch (Exception e) {
      log.error("custom hibernate operation failed", e);
      success = false;
    } finally {
      endTransaction(session, success);
    }
    return success;
  }

  /**
   *
   *
   * @param obj
   * @return
   */
  public boolean updateInstance(T obj) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      merge(obj, session);
    } catch (Exception e) {
      log.error("custom hibernate operation failed", e);
      success = false;
    } finally {
      endTransaction(session, success);
    }
    return success;
  }

  /**
   *
   *
   * @param obj
   * @return
   */
  public boolean deleteInstance(T obj) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      delete(obj, session);
    } catch (Exception e) {
      log.error("custom hibernate operation failed", e);
      success = false;
    } finally {
      endTransaction(session, success);
    }
    return success;
  }

  // ******************************************************************
  // generated methods of hibernate
  // ******************************************************************

  public void save(T transientInstance, Session session) {
    log.debug("saving T instance");
    try {
      session.save(transientInstance);
      log.debug("save successful");
    } catch (RuntimeException re) {
      log.error("save failed", re);
      throw re;
    }
  }

  public void persist(T transientInstance, Session session) {
    log.debug("persisting T instance");
    try {
      session.persist(transientInstance);
      log.debug("persist successful");
    } catch (RuntimeException re) {
      log.error("persist failed", re);
      throw re;
    }
  }

  public void attachDirty(T instance, Session session) {
    log.debug("attaching dirty T instance");
    try {
      session.saveOrUpdate(instance);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  @SuppressWarnings("deprecation")
  public void attachClean(T instance, Session session) {
    log.debug("attaching clean T instance");
    try {
      session.lock(instance, LockMode.NONE);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void delete(T persistentInstance, Session session) {
    log.debug("deleting T instance");
    try {
      session.delete(persistentInstance);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      throw re;
    }
  }

  public T merge(T detachedInstance, Session session) {
    log.debug("merging T instance");
    try {
      @SuppressWarnings("unchecked")
      T result = (T) session.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    } catch (RuntimeException re) {
      log.error("merge failed", re);
      throw re;
    }
  }

}
