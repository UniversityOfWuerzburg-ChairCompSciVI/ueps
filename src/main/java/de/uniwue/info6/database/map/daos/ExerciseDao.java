package de.uniwue.info6.database.map.daos;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ExerciseDao.java
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.SolutionQuery;

/**
 *
 *
 * @author Michael
 */
public class ExerciseDao extends DaoTools<Exercise> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExerciseDao.class);

  // ******************************************************************
  // custom (not generated methods)
  // ******************************************************************

  /**
   * @param typeClass
   */
  public ExerciseDao() {
    super(Exercise.class);
  }

  /**
   *
   *
   * @param id
   * @return
   */
  public Exercise getById(int id) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      return getByIdHbn(id, session);
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
   * @param group
   * @return
   */
  public List<Exercise> findByExGroup(ExerciseGroup group) {
    try {
      if (group != null) {
        Exercise exercise = new Exercise();
        exercise.setExerciseGroup(group);
        return findByExample(exercise);
      }
    } catch (Exception e) {
      log.error("custom hibernate operation failed", e);
    }
    return null;
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public Set<Exercise> findByExGroupAsSet(ExerciseGroup group) {
    List<Exercise> list = findByExGroup(group);
    Set<Exercise> set = new HashSet<Exercise>();
    set.addAll(list);
    return set;
  }

  /**
   *
   *
   * @param instance
   * @return
   */
  public List<Exercise> findByExample(Exercise instance) {
    boolean success = true;
    Session session = null;
    try {
      session = startTransaction();
      return (List<Exercise>) findByExampleHbn(instance, session);
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
   * @param exercise
   * @return
   */
  public List<SolutionQuery> getSolutions(Exercise exercise) {
    boolean success = true;
    Session session = null;

    try {
      session = startTransaction();
      refresh(exercise, session);
      @SuppressWarnings("unchecked")
      Set<SolutionQuery> solutions = exercise.getSolutionQueries();
      List<SolutionQuery> queries = new ArrayList<SolutionQuery>();
      queries.addAll(solutions);
      return queries;
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
   * @param exercise
   * @return
   */
  public Exercise pull(Exercise exercise) {
    return this.getById(exercise.getId());
  }

  // ******************************************************************
  // generated methods of hibernate
  // ******************************************************************

  /**
   *
   *
   * @param instance
   * @return
   */
  private List<Exercise> findByExampleHbn(Exercise instance, Session session) {
    log.debug("finding Exercise instance by example");
    try {
      @SuppressWarnings("unchecked")
      List<Exercise> results = session.createCriteria("de.uniwue.info6.database.map.Exercise")
                               .add(Example.create(instance)).createAlias("exerciseGroup", "gr")
                               .add(Restrictions.eq("gr.id", instance.getExerciseGroup().getId())).list();
      log.debug("find by example successful, result size: " + results.size());
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      throw re;
    }
  }

  /**
   *
   *
   * @param id
   * @return
   */
  private Exercise getByIdHbn(java.lang.Integer id, Session session) {
    log.debug("getting Exercise instance with id: " + id);
    try {
      Exercise instance = (Exercise) session.get("de.uniwue.info6.database.map.Exercise", id);
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

}
