package de.uniwue.info6.database.map.daos;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserResult;

/**
 *
 *
 * @author Michael
 */
public class UserResultDao extends DaoTools<UserResult> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(UserResultDao.class);

	// ******************************************************************
	// custom (not generated methods)
	// ******************************************************************

	/**
	 * @param typeClass
	 */
	public UserResultDao() {
		super(UserResult.class);
	}

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public UserResult getById(int id) {
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
	public List<UserResult> findByExample(UserResult instance) {
		boolean success = true;

		Session session = null;
		try {
			session = startTransaction();
			return (List<UserResult>) findByExampleHbn(instance, session);
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
	 * @param entry
	 * @return
	 */
	public UserResult getLastUserResultFromEntry(UserEntry entry) {
		Session session = null;
		if (entry != null) {
			boolean success = true;
			try {
				session = startTransaction();
				List<UserResult> results = getLastUserResultFromEntryHbn(entry, session);

				if (results != null && !results.isEmpty()) {
					return results.get(0);
				}
			} catch (Exception e) {
				log.error("custom hibernate operation failed", e);
				success = false;
				return null;
			} finally {
				endTransaction(session, success);
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param exercise
	 * @return
	 */
	private List<UserResult> getLastUserResultFromEntryHbn(UserEntry entry, Session session) {
		log.debug("finding UserEntry instance by example");
		try {
			//String query = "from UserEntry as entry where exists (from UserEntry order by entryTime DESC) group by exercise, user";
			String query = "from UserResult as result where exists (from UserResult order by lastModified DESC) AND userEntry="
					+ entry.getId() + " group by user";
			Query result = session.createQuery(query.toString());
			@SuppressWarnings("unchecked")
			List<UserResult> results = result.list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	// ******************************************************************
	// generated methods of hibernate
	// ******************************************************************

	private UserResult findById(java.lang.Integer id, Session session) {
		log.debug("getting UserResult instance with id: " + id);
		try {
			UserResult instance = (UserResult) session.get("de.uniwue.info6.database.map.UserResult", id);
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

	private List<UserResult> findByExampleHbn(UserResult instance, Session session) {
		log.debug("finding UserResult instance by example");
		try {
			Criteria criteria = session.createCriteria("de.uniwue.info6.database.map.UserResult");
			criteria.add(Example.create(instance));
			if (instance.getUser() != null) {
				criteria.createAlias("user", "u").add(Restrictions.eq("u.id", instance.getUser().getId()));
			}

			if (instance.getUserEntry() != null) {
				criteria.createAlias("userEntry", "e").add(Restrictions.eq("e.id", instance.getUserEntry().getId()));
			}

			if (instance.getSolutionQuery() != null) {
				criteria.createAlias("solutionQuery", "s").add(
						Restrictions.eq("s.id", instance.getSolutionQuery().getId()));
			}

			@SuppressWarnings("unchecked")
			List<UserResult> results = criteria.list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

}
