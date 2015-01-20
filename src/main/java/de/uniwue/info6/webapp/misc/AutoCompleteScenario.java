package de.uniwue.info6.webapp.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AutoCompleteScenario.java
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

import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
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
    SessionObject ac = SessionObject.pull();
    if (ac != null) {
      user = ac.getUser();
    }

    if (user != null) {
      List<Scenario> temp = scenarioDao.findAll();

      scenarios = new ArrayList<Scenario>();
      if (temp != null && user != null) {
        for (Scenario sc : temp) {
          if (rights.hasRatingRight(user, sc)) {
            scenarios.add(sc);
          }
        }
      }
      notFound = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.NO_SCENARIO");
    }
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
    results.add("[" + Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.EMPTY_FIELD") + "]");

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
