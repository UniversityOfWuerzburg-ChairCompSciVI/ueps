package de.uniwue.info6.database.map.daos;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import de.uniwue.info6.database.map.UserExTag;

/**
 *
 *
 * @author Michael
 */
public class UserExTagDao extends DaoTools<UserExTag> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(UserExTagDao.class);

	// ******************************************************************
	// custom (not generated methods)
	// ******************************************************************

	/**
	 * @param typeClass
	 */
	public UserExTagDao() {
		super(UserExTag.class);
	}

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public UserExTag getById(int id) {
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
	 * @return
	 */
	public UserExTag getRandom() {
		List<UserExTag> allEntries = findAll();
		if (allEntries != null && !allEntries.isEmpty()) {
			return allEntries.get((int) (Math.random() * allEntries.size()));
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
	public List<UserExTag> findByExample(UserExTag instance) {
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

	public UserExTag findById(java.lang.Integer id, Session session) {
		log.debug("getting UserExTag instance with id: " + id);
		try {
			UserExTag instance = (UserExTag) session.get("de.uniwue.info6.database.map.UserExTag", id);
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

	public List<UserExTag> findByExampleHbn(UserExTag instance, Session session) {
		log.debug("finding UserExTag instance by example");
		try {
			@SuppressWarnings("unchecked")
			List<UserExTag> results = session.createCriteria("de.uniwue.info6.database.map.UserExTag")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

}
