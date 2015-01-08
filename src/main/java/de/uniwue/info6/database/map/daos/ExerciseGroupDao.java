package de.uniwue.info6.database.map.daos;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ExerciseGroupDao.java
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

import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;

/**
 *
 *
 * @author Michael
 */
public class ExerciseGroupDao extends DaoTools<ExerciseGroup> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ExerciseGroupDao.class);

	// ******************************************************************
	// custom (not generated methods)
	// ******************************************************************

	/**
	 * {@inheritDoc}
	 *
	 * @see Object#ExerciseGroupDao()
	 */
	public ExerciseGroupDao() {
		super(ExerciseGroup.class);
	}

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public ExerciseGroup getById(int id) {
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
	public List<ExerciseGroup> findByScenario(Scenario scenario) {
		if (scenario != null) {
			ExerciseGroup example = new ExerciseGroup();
			example.setScenario(scenario);
			return findByExample(example);
		} else {
			return null;
		}
	}

	/**
	 *
	 *
	 * @param scenario
	 * @return
	 */
	public Set<ExerciseGroup> findByScenarioAsSet(Scenario scenario) {
		if (scenario != null) {
			List<ExerciseGroup> list = findByScenario(scenario);
			Set<ExerciseGroup> set = new HashSet<ExerciseGroup>();
			set.addAll(list);
			return set;
		} else {
			return null;
		}
	}

	/**
	 *
	 *
	 * @param instance
	 * @return
	 */
	public List<ExerciseGroup> findByExample(ExerciseGroup instance) {
		boolean success = true;
		Session session = null;
		try {
			session = startTransaction();
			return (List<ExerciseGroup>) findByExampleHbn(instance, session);
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

	private ExerciseGroup findById(java.lang.Integer id, Session session) {
		log.debug("getting ExerciseGroup instance with id: " + id);
		try {
			if (session != null) {
				ExerciseGroup instance = (ExerciseGroup) session.get("de.uniwue.info6.database.map.ExerciseGroup", id);
				if (instance == null) {
					log.debug("get successful, no instance found");
				} else {
					log.debug("get successful, instance found");
				}
				return instance;
			}
			return null;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	private List<ExerciseGroup> findByExampleHbn(ExerciseGroup instance, Session session) {
		log.debug("finding ExerciseGroup instance by example");
		try {
			if (session != null) {
				Criteria criteria = session.createCriteria("de.uniwue.info6.database.map.ExerciseGroup");
				criteria.add(Example.create(instance));
				if (instance.getScenario() != null) {
					criteria.createAlias("scenario", "sc")
							.add(Restrictions.eq("sc.id", instance.getScenario().getId()));
				}

				@SuppressWarnings("unchecked")
				List<ExerciseGroup> results = criteria.list();

				log.debug("find by example successful, result size: " + results.size());
				return results;
			}
			return null;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

}
