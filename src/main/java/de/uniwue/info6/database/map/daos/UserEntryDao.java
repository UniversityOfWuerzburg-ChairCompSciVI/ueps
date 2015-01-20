package de.uniwue.info6.database.map.daos;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UserEntryDao.java
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
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.webapp.session.SessionBean;

/**
 *
 *
 * @author Michael
 */
public class UserEntryDao extends DaoTools<UserEntry> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private User currentUser;
	private static final Log log = LogFactory.getLog(UserEntryDao.class);

	// ******************************************************************
	// custom (not generated methods)
	// ******************************************************************

	/**
	 *
	 */
	public UserEntryDao() {
		super(UserEntry.class);
		if (FacesContext.getCurrentInstance() != null) {
			currentUser = new SessionBean().getUser();
		}
	}

	/**
	 *
	 *
	 * @param ex
	 * @return
	 */
	public List<UserEntry> getByExercise(Exercise ex, boolean lastEntry) {
		return getByExercise(ex, currentUser, lastEntry);
	}

	/**
	 *
	 *
	 * @param ex
	 * @param user
	 * @return
	 */
	public UserEntry getLastEntry(Exercise ex, User user) {
		List<UserEntry> entries = getByExercise(ex, user, true);
		if (entries != null && !entries.isEmpty()) {
			return entries.get(0);
		}
		return null;
	}

	/**
	 *
	 *
	 * @param user
	 * @return
	 */
	public boolean hasEntries(User user) {
		UserEntry example = new UserEntry();
		example.setUser(user);
		List<UserEntry> entries = findByExample(example, true);
		if (entries != null && !entries.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<UserEntry> getByExercise(Exercise ex, User user, boolean lastEntry) {
		// entries to search for
		UserEntry entry = new UserEntry();
		entry.setUser(user);
		entry.setExercise(ex);

		List<UserEntry> entries = this.findByExample(entry, lastEntry);

		if (entries != null && !entries.isEmpty()) {
			return entries;
		}

		return null;
	}

	/**
	 *
	 *
	 * @param ex
	 * @param user
	 * @return
	 */
	public List<UserEntry> getLastUserEntryForAllUsers(Exercise ex) {
		UserDao userDao = new UserDao();
		List<UserEntry> userEntries = new ArrayList<UserEntry>();

		List<User> users = userDao.getRelevantUsersByExercise(ex);

		if (users != null) {
			for (User user : users) {
				UserEntry en = getLastUserEntry(ex, user);
				if (en != null) {
					userEntries.add(en);
				}
			}

			if (!userEntries.isEmpty()) {
				return userEntries;
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param ex
	 * @param user
	 * @return
	 */
	public UserEntry getLastUserEntry(Exercise ex, User user) {
		List<UserEntry> entries = null;
		entries = getByExercise(ex, user, true);

		if (entries != null && !entries.isEmpty()) {
			return entries.get(0);
		}

		return null;
	}

	/**
	 *
	 *
	 * @param ex
	 * @return
	 */
	public UserEntry getLastUserEntry(Exercise ex) {
		return getLastUserEntry(ex, currentUser);
	}

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public UserEntry getById(int id) {
		boolean success = true;
		Session session = null;
		try {
			session = startTransaction();
			return findByIdHbn(id, session);
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
	 * @param instance
	 * @return
	 */
	public List<UserEntry> findByExample(UserEntry instance, boolean lastEntry) {
		boolean success = true;
		Session session = null;
		try {
			session = startTransaction();
			if (lastEntry) {
				return (List<UserEntry>) findByExampleHbn(instance, true, session);
			} else {
				return (List<UserEntry>) findByExampleHbn(instance, false, session);
			}
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
	 * @param instance
	 * @return
	 */
	public List<UserEntry> getLastEntriesForExercise(Exercise exercise) {
		boolean success = true;
		Session session = null;
		try {
			session = startTransaction();
			return (List<UserEntry>) getLastEntriesForExerciseHbn(exercise, session);
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
	 * @param UserEntry
	 * @return
	 */
	public List<SolutionQuery> getSolutions(UserEntry UserEntry) {
		boolean success = true;
		Session session = null;
		try {
			session = startTransaction();
			pull(UserEntry, session);
			@SuppressWarnings("unchecked")
			Set<SolutionQuery> solutions = (Set<SolutionQuery>) UserEntry.getSolutionQueries();
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
	 * @param id
	 * @return
	 */
	private UserEntry findByIdHbn(java.lang.Integer id, Session session) {
		log.debug("getting UserEntry instance with id: " + id);
		try {
			UserEntry instance = (UserEntry) session.get("de.uniwue.info6.database.map.UserEntry", id);
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

	/**
	 *
	 *
	 * @param instance
	 * @param getLast
	 * @return
	 */
	private List<UserEntry> findByExampleHbn(UserEntry instance, boolean getLast, Session session) {
		log.debug("finding UserEntry instance by example");
		try {
			Criteria criteria = session.createCriteria("de.uniwue.info6.database.map.UserEntry");
			criteria.add(Example.create(instance));
			if (instance.getExercise() != null) {
				criteria.createAlias("exercise", "ex").add(Restrictions.eq("ex.id", instance.getExercise().getId()));
			}

			if (instance.getUser() != null) {
				criteria.createAlias("user", "u").add(Restrictions.eq("u.id", instance.getUser().getId()));
			}
			criteria.addOrder(Order.desc("entryTime"));

			if (getLast) {
				criteria.setMaxResults(1);
			}
			@SuppressWarnings("unchecked")
			List<UserEntry> results = criteria.list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		} finally {
		}

	}

	/**
	 *
	 *
	 * @param exercise
	 * @return
	 */
	private List<UserEntry> getLastEntriesForExerciseHbn(Exercise exercise, Session session) {
		log.debug("finding UserEntry instance by example");
		try {
			//String query = "from UserEntry as entry where exists (from UserEntry order by entryTime DESC) group by exercise, user";
			String query = "from UserEntry as entry where exists (from UserEntry order by entryTime DESC) AND exercise="
					+ exercise.getId() + " group by user";
			Query result = session.createQuery(query.toString());
			@SuppressWarnings("unchecked")
			List<UserEntry> results = result.list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		} finally {
		}
	}

}
