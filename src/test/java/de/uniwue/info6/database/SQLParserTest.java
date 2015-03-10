package de.uniwue.info6.database;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SQLParserTest.java
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

import static de.uniwue.info6.misc.properties.PropBool.FORCE_RESET_DATABASE;
import static de.uniwue.info6.misc.properties.PropBool.IMPORT_EXAMPLE_SCENARIO;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import de.uniwue.info6.database.gen.GenerateData;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.jdbc.ConnectionTools;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.misc.EquivalenceLock;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.lists.ExerciseController;

public class SQLParserTest {
  public static void main(String[] args) throws Exception {
    String test = "Dies ist ein einfacher Test";
    System.out.println(StringTools.forgetOneWord(test));


    System.exit(0);
    // SimpleTupel<String, Integer> test1 =  new SimpleTupel<String, Integer>("test1", 1);
    // SimpleTupel<String, Integer> test2 =  new SimpleTupel<String, Integer>("test1", 12);

    // ArrayList<SimpleTupel<String, Integer>> test = new ArrayList<SimpleTupel<String, Integer>>();
    // test.add(test1);
    // System.out.println(test1.equals(test2));
    // System.exit(0);

    final boolean resetDb = true;
    // Falls nur nach einer bestimmten Aufgabe gesucht wird
    final Integer exerciseID = 39;
    final Integer scenarioID = null;
    final int threadSize = 1;

    final EquivalenceLock <Long[]> equivalenceLock = new EquivalenceLock<Long[]>();
    final Long[] performance = new Long[] {0L, 0L};

    // ------------------------------------------------ //
    final ScenarioDao scenarioDao = new ScenarioDao();
    final ExerciseDao exerciseDao = new ExerciseDao();
    final ExerciseGroupDao groupDao = new ExerciseGroupDao();
    final UserDao userDao = new UserDao();
    final ArrayList<Thread> threads = new ArrayList<Thread>();

    // ------------------------------------------------ //
    try {
      ConnectionManager.offline_instance();
      if (resetDb) {
        Cfg.inst().setProp(MAIN_CONFIG, IMPORT_EXAMPLE_SCENARIO, true);
        Cfg.inst().setProp(MAIN_CONFIG, FORCE_RESET_DATABASE, true);
        new GenerateData().resetDB();
        ConnectionTools.inst().addSomeTestData();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // ------------------------------------------------ //

    final List<Scenario> scenarios = scenarioDao.findAll();

    try {
      // ------------------------------------------------ //
      String userID;
      for (int i = 2; i < 100; i++) {
        userID = "user_" + i;
        User userToInsert = new User();
        userToInsert.setId(userID);
        userToInsert.setIsAdmin(false);
        userDao.insertNewInstance(userToInsert);
      }
      // ------------------------------------------------ //

      for (int i = 0; i < threadSize; i++) {
        Thread thread = new Thread() {

          public void run() {
            // ------------------------------------------------ //
            try {
              Thread.sleep(new Random().nextInt(30));
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            // ------------------------------------------------ //

            User user = userDao.getRandom();
            Thread.currentThread().setName(user.getId());
            System.err.println("\n\nINFO (ueps): Thread '" + Thread.currentThread().getName() + "' started\n");

            // ------------------------------------------------ //

            for (Scenario scenario : scenarios) {
              if (scenarioID != null && !scenario.getId().equals(scenarioID)) {
                continue;
              }




              System.out.println(StringUtils.repeat("#", 90));
              System.out.println("SCENARIO: " + scenario.getId());

              // ------------------------------------------------ //
              for (ExerciseGroup group : groupDao.findByScenario(scenario)) {
                System.out.println(StringUtils.repeat("#", 90));
                System.out.println("GROUP: " + group.getId());
                System.out.println(StringUtils.repeat("#", 90));
                List<Exercise> exercises = exerciseDao.findByExGroup(group);

                // ------------------------------------------------ //
                for (Exercise exercise : exercises) {
                  if (exerciseID != null && !exercise.getId().equals(exerciseID)) {
                    continue;
                  }
                  long startTime = System.currentTimeMillis();

                  for (int i = 0; i < 100; i++) {
                    String userID = "user_" + new Random().nextInt(100000);
                    User userToInsert = new User();
                    userToInsert.setId(userID);
                    userToInsert.setIsAdmin(false);
                    userDao.insertNewInstance(userToInsert);
                    user = userDao.getById(userID);


                    List<SolutionQuery> solutions = new ExerciseDao().getSolutions(exercise);
                    String solution = solutions.get(0).getQuery();
                    ExerciseController exc = new ExerciseController().init_debug(scenario, exercise,
                        user);
                    exc.setUserString(solution);

                    String fd = exc.getFeedbackList().get(0).getFeedback();
                    System.out.println("Used Query: " + solution);
                    if (fd.trim().toLowerCase().equals("bestanden")) {
                      System.out.println(exercise.getId() + ": " + fd);
                    } else {
                      System.err.println(exercise.getId() + ": " + fd + "\n");
                    }
                    System.out.println(StringUtils.repeat("-", 90));
                  }

                  long elapsedTime = System.currentTimeMillis() - startTime;

                  // if (i > 5) {
                  //   try {
                  //     equivalenceLock.lock(performance);
                  //     performance[0] += elapsedTime;
                  //     performance[1]++;
                  //   } catch (Exception e) {
                  //   } finally {
                  //     equivalenceLock.release(performance);
                  //   }
                  // }
                }
              }
            }

            System.err.println("INFO (ueps): Thread '" + Thread.currentThread().getName() + "' stopped");
          }
        };
        thread.start();
        threads.add(thread);
      }

      for (Thread thread : threads) {
        thread.join();
      }

      // try {
      //   equivalenceLock.lock(performance);

      //   long elapsedTime = (performance[0] / performance[1]);
      //   System.out.println("\n" + String.format("perf : %d.%03dsec", elapsedTime / 1000, elapsedTime % 1000));
      // } catch (Exception e) {
      // } finally {
      //   equivalenceLock.release(performance);
      // }

    } finally {
    }
  }
}
