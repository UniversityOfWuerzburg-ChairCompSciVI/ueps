package de.uniwue.info6.database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.webapp.lists.ExerciseController;

public class SQLParserTest {
	public static void main(String[] args) throws Exception {

		System.out.println(StringUtils.repeat("-", 80));

		boolean resetDB = false;
		final Integer exerciseID = null;
		Connection connection = null;

		try {

			if (resetDB) {
				// // RESET DB
				// GenerateData gen = new GenerateData();
				// gen.resetDB(false, true);
				// gen.insertExampleScenario(true);
			}

			ConnectionManager pool = ConnectionManager.offline_instance();
			pool.updateScenarios();

			// main daos
			final UserDao userDao = new UserDao();

			// String userID;
			// for (int i = 2; i < 100; i++) {
			//     userID = "user_" + i;
			//     User userToInsert = new User();
			//     userToInsert.setId(userID);
			//     userToInsert.setIsAdmin(false);
			//     userDao.insertNewInstance(userToInsert);
			// }

			ArrayList<Thread> threads = new ArrayList<Thread>();

			for (int i = 0; i < 1; i++) {
				Thread thread = new Thread() {

					public void run() {

						try {
							Thread.sleep(new Random().nextInt(5));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// main daos
						final ScenarioDao scenarioDao = new ScenarioDao();
						final ExerciseDao exerciseDao = new ExerciseDao();
						final ExerciseGroupDao groupDao = new ExerciseGroupDao();

						final UserDao userDao = new UserDao();
						final List<Scenario> scenarios = scenarioDao.findAll();

						User user = userDao.getRandom();
						System.out.println("thread: " + Thread.currentThread().getId() + " started: " + user.getId());

						for (Scenario scenario : scenarios) {
							System.out.println(StringUtils.repeat("#", 90));
							System.out.println("SCENARIO: " + scenario.getId());
							for (ExerciseGroup group : groupDao.findByScenario(scenario)) {
								System.out.println(StringUtils.repeat("#", 90));
								System.out.println("GROUP: " + group.getId());
								System.out.println(StringUtils.repeat("#", 90));
								List<Exercise> exercises = exerciseDao.findByExGroup(group);
								for (Exercise exercise : exercises) {
									if (exerciseID != null && !exercise.getId().equals(exerciseID)) {
										continue;
									}

									List<SolutionQuery> solutions = new ExerciseDao().getSolutions(exercise);
									String solution = solutions.get(0).getQuery();

									ExerciseController exc = new ExerciseController().init_debug(scenario, exercise,
											user);
									exc.setUserString(solution);

									String fd = exc.getFeedbackList().get(0).getFeedback();
									if (!fd.trim().toLowerCase().equals("bestanden")) {
										System.err.println("ERROR:\n");
									}
									System.out.println("Used Query: " + solution);
									System.out.println(exercise.getId() + ": " + fd);
									System.out.println(StringUtils.repeat("-", 90));
								}
							}
						}
						System.out.println("thread: " + Thread.currentThread().getId() + " is done: " + user.getId());
					}
				};
				thread.start();
				threads.add(thread);
			}

			for (Thread thread : threads) {
				thread.join();
			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}
