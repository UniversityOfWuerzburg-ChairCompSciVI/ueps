package de.uniwue.info6.webapp.misc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "scenario_names")
@ViewScoped
public class AutoCompleteScenario {

	private ScenarioDao scenarioDao;
	private List<Scenario> scenarios;
	private String notFound;
	private UserRights rights;
	private User user;

	/**
	 *
	 */
	public AutoCompleteScenario() {
	}

	/**
	 *
	 *
	 */
	@PostConstruct
	public void init() {
		scenarioDao = new ScenarioDao();
		this.rights = new UserRights().initialize();
		SessionObject ac = new SessionCollector().getSessionObject();
		if (ac != null) {
			user = ac.getUser();
		}

		List<Scenario> temp = scenarioDao.findAll();
		scenarios = new ArrayList<Scenario>();
		if (temp != null && user != null) {
			for (Scenario sc: temp) {
				if (rights.hasRatingRight(user, sc)) {
					scenarios.add(sc);
				}
			}
		}
		notFound = System.getProperty("ASSERTION.NO_SCENARIO");
	}

	/**
	 *
	 *
	 * @param query
	 * @return
	 */
	public List<String> complete(String query) {
		List<String> results = new ArrayList<String>();

		if (query.startsWith("0")) {
			query = query.replaceFirst("[0]+", "");
		}
		results.add("[" + System.getProperty("ASSERTION.EMPTY_FIELD") + "]");

		if (scenarios != null) {
			for (Scenario scenario : scenarios) {
				if (scenario.getName() != null) {
					String id = String.valueOf(scenario.getId());
					if (id.contains(query.trim())
							|| scenario.getName().toLowerCase().contains(query.toLowerCase().trim())) {
						results.add("[" + id + "]: " + StringTools.findSnippet(scenario.getName(), query, 70));
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
