package de.uniwue.info6.database.map.daos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
  private static final Log log = LogFactory.getLog(ExerciseDao.class);

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
      update(exercise, session);
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
