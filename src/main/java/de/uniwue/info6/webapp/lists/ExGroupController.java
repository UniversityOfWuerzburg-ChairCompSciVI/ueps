package de.uniwue.info6.webapp.lists;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ExGroupController.java
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean
@ViewScoped
public class ExGroupController implements Serializable {
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
    SessionObject so = SessionObject.pull();
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
        for (ExerciseGroup group : temp) {
          if (rights.hasViewRights(user, scenario, group)) {
            exGroups.add(group);
          }
        }
      }
    } catch (Exception e) {
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
