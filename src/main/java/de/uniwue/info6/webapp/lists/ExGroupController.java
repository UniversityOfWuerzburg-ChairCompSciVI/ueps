package de.uniwue.info6.webapp.lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 *
 * @author Michael
 */
@ManagedBean
@ViewScoped
public class ExGroupController implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Log LOGGER = LogFactory.getLog(ExGroupController.class);

	private Scenario scenario;
	private List<ExerciseGroup> exGroups;

	// daos
	private ExerciseGroupDao exGroupDao;
	private ExerciseGroup example, exampleRated;
	private UserRights rights;
	private User user;

	/**
	 *
	 */
	public ExGroupController() {
		// get current scenario
		SessionObject so = new SessionCollector().getSessionObject();
		scenario = so.getScenario();
		user = so.getUser();

		exGroupDao = new ExerciseGroupDao();
		exGroups = new ArrayList<ExerciseGroup>();

		// search criteria for unrated exercises
		example = new ExerciseGroup();
		example.setScenario(scenario);
		example.setIsRated(false);

		// search criteria for rated exercises
		exampleRated = new ExerciseGroup();
		exampleRated.setScenario(scenario);
		exampleRated.setIsRated(true);

		rights = new UserRights();
		rights.initialize();
	}

	/**
	 * @return the exGroups
	 */
	public List<ExerciseGroup> getExGroups() {
		try {
		List<ExerciseGroup> temp = new ArrayList<ExerciseGroup>();
		if (exGroups.isEmpty() && scenario != null && rights != null && user != null) {
			temp = exGroupDao.findByExample(example);
			List<ExerciseGroup> exGroupsRated =  exGroupDao.findByExample(exampleRated);

			if (exGroupsRated != null) {
				temp.addAll(exGroupsRated);
			}
			for (ExerciseGroup group: temp) {
				if (rights.hasViewRights(user, scenario, group)) {
					exGroups.add(group);
				}
			}
		}
		}
		catch (Exception e) {
			LOGGER.error("ERROR CREATING EXERCISE-GROUP LIST", e);
		}
		return exGroups;
	}

	/**
	 * @param exGroups
	 *            the exGroups to set
	 */
	public void setExGroups(List<ExerciseGroup> exGroups) {
		this.exGroups = exGroups;
	}
}
