package de.uniwue.info6.database.map.daos;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SolutionQueryDao.java
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.SolutionQuery;

/**
 *
 *
 * @author Michael
 */
public class SolutionQueryDao extends DaoTools<SolutionQuery> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(SolutionQuery.class);

	// ******************************************************************
	// custom (not generated methods)
	// ******************************************************************

	/**
	 * @param typeClass
	 */
	public SolutionQueryDao() {
		super(SolutionQuery.class);
	}

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public SolutionQuery getById(int id) {
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
	 * @param solution
	 * @return
	 */
	public boolean checkIfExists(SolutionQuery solution) {
		if (solution != null && solution.getId() != null && getById(solution.getId()) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 *
	 * @param exercise
	 * @return
	 */
	public List<SolutionQuery> findByExercice(Exercise exercise) {
		SolutionQuery example = new SolutionQuery();
		example.setExercise(exercise);
		return findByExample(example);
	}

	/**
	 *
	 *
	 * @param exercise
	 * @return
	 */
	public Set<SolutionQuery> findByExerciceAsSet(Exercise exercise) {
		Set<SolutionQuery> set = new HashSet<SolutionQuery>();
		set.addAll(findByExercice(exercise));
		return set;
	}

	/**
	 *
	 *
	 * @param instance
	 * @return
	 */
	public List<SolutionQuery> findByExample(SolutionQuery instance) {
		boolean success = true;
		Session session = null;
		try {
			session = startTransaction();
			return (List<SolutionQuery>) findByExampleHbn(instance, session);
		} catch (Exception e) {
			success = false;
			return null;
		} finally {
			endTransaction(session, success);
		}
	}

	// ******************************************************************
	// generated methods of hibernate
	// ******************************************************************

	private SolutionQuery findById(java.lang.Integer id, Session session) {
		log.debug("getting SolutionQuery instance with id: " + id);
		try {
			SolutionQuery instance = (SolutionQuery) session.get("de.uniwue.info6.database.map.SolutionQuery", id);
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

	private List<SolutionQuery> findByExampleHbn(SolutionQuery instance, Session session) {
		log.debug("finding SolutionQuery instance by example");
		try {
			Criteria criteria = session.createCriteria("de.uniwue.info6.database.map.SolutionQuery");
			criteria.add(Example.create(instance));
			if (instance.getExercise() != null) {
				criteria.createAlias("exercise", "e").add(Restrictions.eq("e.id", instance.getExercise().getId()));
			}

			@SuppressWarnings("unchecked")
			List<SolutionQuery> results = criteria.list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
