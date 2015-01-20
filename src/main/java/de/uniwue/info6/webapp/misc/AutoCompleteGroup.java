package de.uniwue.info6.webapp.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AutoCompleteGroup.java
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

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "group_names")
@ViewScoped
public class AutoCompleteGroup {

  private ExerciseGroupDao groupDao;
  private ScenarioDao scenarioDao;
  private List<ExerciseGroup> groups;
  private String notFound;
  private String scenarioID;
  private UserRights rights;
  private User user;

  /**
   *
   */
  public AutoCompleteGroup() {
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    this.groupDao = new ExerciseGroupDao();
    this.scenarioDao = new ScenarioDao();
    this.notFound = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.NO_GROUP");
    this.rights = new UserRights().initialize();

    SessionObject ac = SessionObject.pull();
    if (ac != null) {
      user = ac.getUser();
    }
  }

  /**
   *
   *
   * @param scenarioID
   * @return
   */
  public AutoCompleteGroup scenarioInit(String scenarioID) {
    if (user != null) {
      if (scenarioID != null && !scenarioID.isEmpty()
          && (this.scenarioID == null || !this.scenarioID.equals(scenarioID))) {
        this.scenarioID = StringTools.extractIDFromAutoComplete(scenarioID);
        if (this.scenarioID != null) {
          Scenario sc = scenarioDao.getById(Integer.parseInt(this.scenarioID));
          if (sc != null && rights.hasRatingRight(user, sc)) {
            ExerciseGroup ratedExample = new ExerciseGroup();
            ratedExample.setScenario(sc);
            ratedExample.setIsRated(true);
            groups = groupDao.findByExample(ratedExample);
          }
        }
      }

      if (groups == null) {
        ExerciseGroup ratedExample = new ExerciseGroup();
        ratedExample.setIsRated(true);
        groups = new ArrayList<ExerciseGroup>();
        List<ExerciseGroup> temp = groupDao.findByExample(ratedExample);
        if (temp != null) {
          for (ExerciseGroup gr : temp) {
            if (rights.hasRatingRight(user, gr)) {
              groups.add(gr);
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

    if (groups != null) {
      for (ExerciseGroup group : groups) {
        if (group.getName() != null) {
          String id = String.valueOf(group.getId());
          if (id.contains(query.trim())
              || group.getName().toLowerCase().contains(query.toLowerCase().trim())) {
            results.add("[" + id + "]: " + StringTools.findSnippet(group.getName(), query, 70));
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
