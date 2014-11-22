package de.uniwue.info6.database;

import de.uniwue.info6.database.gen.GenerateData;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserExTagDao;
import de.uniwue.info6.database.map.daos.UserResultDao;
import de.uniwue.info6.misc.FileTransfer;
import de.uniwue.info6.webapp.admin.UserRights;

public class SimpleHibernateConnection {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		// String queryText = "`hallo`.hallo hallo";

		// String REGEX_FIELD = "hallo";
		// Matcher matcher = Pattern.compile(REGEX_FIELD, Pattern.CASE_INSENSITIVE).matcher(queryText);
		// StringBuffer s = new StringBuffer();
		// while (matcher.find()) {
		//     int start = matcher.start(0), end = matcher.end(0);
		//     String group = matcher.group();

		//     boolean leftCharacterValid = StringTools.trailingCharacter(queryText, start, true);
		//     boolean rightCharacterValid =  StringTools.trailingCharacter(queryText, end, false);
		//     if (leftCharacterValid && rightCharacterValid)  {
		//         matcher.appendReplacement(s, "user_" + group);
		//     }
		// }

		boolean resetDb = false;

		if (resetDb) {
			GenerateData gen = new GenerateData();
			gen.resetDB(false, true);
			gen.insertExampleScenario(true);
		}

		ConnectionManager pool = ConnectionManager.offline_instance();

		// main daos
		ScenarioDao scenarioDao = new ScenarioDao();
		ExerciseGroupDao exerciseGroupDao = new ExerciseGroupDao();
		ExerciseDao exerciseDao = new ExerciseDao();
		SolutionQueryDao solutionDao = new SolutionQueryDao();
		UserExTagDao userTagDao = new UserExTagDao();
		UserDao userDao = new UserDao();
		UserEntryDao userEntryDao = new UserEntryDao();
		UserResultDao userResultDao = new UserResultDao();

		// misc
		UserRights userRights = new UserRights().initialize();
		FileTransfer transfer = new FileTransfer();

		// example data
		Exercise ex = exerciseDao.getById(57);

		ExerciseGroup gr1 = exerciseGroupDao.getById(1);
		ExerciseGroup gr2 = exerciseGroupDao.getById(2);

		Scenario sc1 = scenarioDao.getById(1);
		Scenario sc2 = scenarioDao.getById(2);

		User randomUser = userDao.getRandom();
		User admin = userDao.getById("user_1");

		// System.out.println(userResultDao.getLastUserResultFromEntry(userEntryDao.getRandom()));
		//	System.out.println(userEntryDao.getLastEntriesForExercise(ex));

		//	pool.addDB(sc2, true);
		//	pool.setResetBarrier(false);
		//	pool.resetDatabaseTables(sc2, null);
		//	pool.setResetBarrier(true);

		// transfer.copy(gr1, sc1, false);
		// transfer.copy(sc2);

		// transfer.copy(ex, null, true);
		// transfer.copy(ex, gr1, true);

		// has editing rights
		// User user = userDao.getById("s213548");
		// List<UserEntry> userEntries = userEntryDao.getLastUserEntryForAllUsers(ex);

		//	ScenarioExporter exporter = new ScenarioExporter();
		//	exporter.generateScenarioXml(sc1);

	}

}
