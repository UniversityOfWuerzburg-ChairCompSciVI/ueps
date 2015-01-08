package de.uniwue.info6.webapp.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AutoCompleteExercise.java
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

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import de.uniwue.info6.misc.properties.Cfg;

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
    this.notFound = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.NO_EX");
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
    results.add("[" + Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.EMPTY_FIELD") + "]");

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
