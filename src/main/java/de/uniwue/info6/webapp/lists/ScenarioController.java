package de.uniwue.info6.webapp.lists;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ScenarioController.java
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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean
@ViewScoped
public class ScenarioController implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private SessionObject ac;
  private final String error = Cfg.inst().getText("SCENARIO_NOT_FOUND");
  private Scenario scenario;
  private static final Log LOGGER = LogFactory.getLog(ScenarioController.class);

  /**
   *
   */
  public ScenarioController() {
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    this.ac = SessionObject.pull();
    this.scenario = ac.getScenario();
    ConnectionManager pool = ConnectionManager.instance();

    if (!pool.getOriginalTableDeleted().contains(scenario)) {
      User user = ac.getUser();
      if (pool != null && scenario != null && user != null) {
        try {
          pool.resetTables(scenario, user);
        } catch (Exception e) {
          LOGGER.error("ERROR RESETTING TABLES", e);
        }
      }
    }
  }

  /**
   *
   *
   * @return
   */
  public String getIntroductionText() {
    String msg = error;
    if (scenario != null) {
      if (ac != null) {
        String temp = scenario.getDescription();
        if (temp != null) {
          msg = temp;
        }
      }
    }
    return msg;
  }

  /**
   *
   *
   * @return
   */
  public String getName() {
    String name = error;
    if (scenario != null) {
      if (ac != null) {
        String temp = scenario.getName();
        if (temp != null) {
          name = temp;
        }
      }
    }
    return name;
  }

  /**
   * @return the scenario
   */
  public Scenario getScenario() {
    return scenario;
  }
}
