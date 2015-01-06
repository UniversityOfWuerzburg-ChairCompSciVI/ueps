package de.uniwue.info6.database;

import de.uniwue.info6.database.gen.GenerateData;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserResultDao;
import de.uniwue.info6.misc.FileTransfer;
import de.uniwue.info6.webapp.admin.UserRights;

public class SimpleHibernateConnection {

  @SuppressWarnings("unused")
  public static void main(String[] args) throws Exception {

    boolean resetDb = false;

    if (resetDb) {
      GenerateData gen = new GenerateData();
      gen.resetDB();
    }

    ConnectionManager pool = ConnectionManager.offline_instance();

    // main daos
    ScenarioDao scenarioDao = new ScenarioDao();
    ExerciseGroupDao exerciseGroupDao = new ExerciseGroupDao();
    ExerciseDao exerciseDao = new ExerciseDao();
    SolutionQueryDao solutionDao = new SolutionQueryDao();
    UserDao userDao = new UserDao();
    UserEntryDao userEntryDao = new UserEntryDao();
    UserResultDao userResultDao = new UserResultDao();

    // misc
    UserRights userRights = new UserRights().initialize();
    FileTransfer transfer = new FileTransfer();

    if (userDao.getRandom() != null) {
      System.err.println("INFO (ueps): Connection established");

      // example data
      // Exercise ex = exerciseDao.getById(57);

      // ExerciseGroup gr1 = exerciseGroupDao.getById(1);
      // ExerciseGroup gr2 = exerciseGroupDao.getById(2);

      // Scenario sc1 = scenarioDao.getById(1);
      // Scenario sc2 = scenarioDao.getById(2);

      // User randomUser = userDao.getRandom();
      // User admin = userDao.getById("user_1");

      // System.out.println(userResultDao.getLastUserResultFromEntry(userEntryDao.getRandom()));
      // System.out.println(userEntryDao.getLastEntriesForExercise(ex));

      // pool.addDB(sc2, true);
      // pool.setResetBarrier(false);
      // pool.resetDatabaseTables(sc2, null);
      // pool.setResetBarrier(true);

      // transfer.copy(gr1, sc1, false);
      // transfer.copy(sc2);

      // transfer.copy(ex, null, true);
      // transfer.copy(ex, gr1, true);

      // has editing rights
      // User user = userDao.getById("s213548");
      // List<UserEntry> userEntries =
      // userEntryDao.getLastUserEntryForAllUsers(ex);

      // ScenarioExporter exporter = new ScenarioExporter();
      // exporter.generateScenarioXml(sc1);
    }

  }

}
