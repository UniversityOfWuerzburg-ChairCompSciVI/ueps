package de.uniwue.info6.webapp.misc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "exercise_names")
@ViewScoped
public class AutoCompleteExercise {

	private ExerciseDao exerciseDao;
	private ExerciseGroupDao groupDao;
	private List<Exercise> exercises;
	private String notFound;
	private String groupID;
	private UserRights rights;
	private User user;

	/**
	 *
	 */
	public AutoCompleteExercise() {
	}

	/**
	 *
	 *
	 */
	@PostConstruct
	public void init() {
		this.exerciseDao = new ExerciseDao();
		this.groupDao = new ExerciseGroupDao();
		this.notFound = System.getProperty("ASSERTION.NO_EX");
		this.rights = new UserRights().initialize();

		SessionObject ac = new SessionCollector().getSessionObject();
		if (ac != null) {
			user = ac.getUser();
		}
	}

	/**
	 *
	 *
	 * @param groupID
	 * @return
	 */
	public AutoCompleteExercise groupInit(String groupID) {
		if (user != null) {
			if (groupID != null && !groupID.isEmpty() && (this.groupID == null || !this.groupID.equals(groupID))) {
				this.groupID = StringTools.extractIDFromAutoComplete(groupID);
				if (this.groupID != null) {
					ExerciseGroup gr = groupDao.getById(Integer.parseInt(this.groupID));
					if (gr != null && rights.hasRatingRight(user, gr)) {
						exercises = exerciseDao.findByExGroup(gr);
					}
				}
			}

			if (exercises == null) {
				ExerciseGroup ratedExample = new ExerciseGroup();
				ratedExample.setIsRated(true);
				List<ExerciseGroup> groups = groupDao.findByExample(ratedExample);
				exercises = new ArrayList<Exercise>();
				for (ExerciseGroup group : groups) {
					if (rights.hasRatingRight(user, group)) {
						List<Exercise> temp = exerciseDao.findByExGroup(group);
						if (temp != null) {
							exercises.addAll(temp);
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 *
	 *
	 * @param query
	 * @return
	 */
	public List<String> complete(String query) {
		if (query.startsWith("0")) {
			query = query.replaceFirst("[0]+", "");
		}

		List<String> results = new ArrayList<String>();
		results.add("[" + System.getProperty("ASSERTION.EMPTY_FIELD") + "]");

		if (exercises != null) {
			for (Exercise exercise : exercises) {
				String question = StringTools.stripHtmlTags(exercise.getQuestion());

				if (question != null) {
					String id = String.valueOf(exercise.getId());
					if (id.contains(query.trim())
							|| question.toLowerCase().contains(query.toLowerCase().trim())) {
						results.add("[" + id + "]: " + StringTools.findSnippet(question, query, 70));
					}
				}
			}
		}

		if (results.size() <= 1 && notFound != null) {
			results = new ArrayList<String>();
			results.add(notFound);
		}

		return results;
	}
}
