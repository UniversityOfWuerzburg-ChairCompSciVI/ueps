package de.uniwue.info6.database.map.daos;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;

/**
 *
 *
 * @author Michael
 */
public class UserDao extends DaoTools<User> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(UserDao.class);

	// ******************************************************************
	// custom (not generated methods)
	// ******************************************************************

	/**
	 *
	 */
	public UserDao() {
		super(User.class);
	}

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public synchronized User getById(String id) {
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
	 * @param instance
	 * @return
	 */
	public List<User> findByExample(User instance) {
		boolean success = true;
		Session session = null;
		try {
			session = startTransaction();
			return (List<User>) findByExampleHbn(instance, session);
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
	 * @return
	 */
	public List<User> getRelevantUsersByExercise(Exercise ex) {
		boolean success = true;
		UserEntryDao userEntryDao = new UserEntryDao();

		List<User> users = new LinkedList<User>();
		List<UserEntry> entries = userEntryDao.getLastEntriesForExercise(ex);
		Session session = null;
		try {
			session = startTransaction();
			if (entries != null) {
				for (UserEntry entry : entries) {
					users.add(entry.getUser());
				}
				if (!users.isEmpty()) {
					return users;
				}
			}
			return null;
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
	private User findById(java.lang.String id, Session session) {
		log.debug("getting User instance with id: " + id);
		try {
			User instance = (User) session.get("de.uniwue.info6.database.map.User", id);
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
	 * @return
	 */
	private List<User> findByExampleHbn(User instance, Session session) {
		log.debug("finding User instance by example");
		try {
			@SuppressWarnings("unchecked")
			List<User> results = session.createCriteria("de.uniwue.info6.database.map.User")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

}
