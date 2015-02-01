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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;




import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ScenarioDao;
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

  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ScenarioController.class);

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private SessionObject ac;
  private final String error = Cfg.inst().getText("SCENARIO_NOT_FOUND");
  private Scenario scenario;
  private String scenarioParameter, userIDParameter, secureValueParameter;
  private ScenarioDao scenarioDao;
  private List<Scenario> scenarios;
  public static String NO_SCENARIO_SELECTED_PARAMETER = "NO_SCENARIO";

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

    this.scenario             = this.ac.getScenario();
    this.scenarioParameter    = this.ac.getScenarioParameter();
    this.userIDParameter      = this.ac.getUserIDParameter();
    this.secureValueParameter = this.ac.getSecureValueParameter();

    this.scenarioDao = new ScenarioDao();
    this.scenarios = this.scenarioDao.findAll();

    final ConnectionManager pool = ConnectionManager.instance();
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
  public boolean isValidScenario() {
    if (ac != null) {
      if (scenario != null) {
        String temp = scenario.getDescription();
        if (temp != null) {
          return true;
        }
      } else if (scenarioParameter != null &&
                 scenarioParameter.equals(NO_SCENARIO_SELECTED_PARAMETER)) {
        return false;
      }
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public String getIntroductionText() {
    if (isValidScenario()) {
      return scenario.getDescription();
    } else if (scenarioParameter != null && scenarioParameter.equals(NO_SCENARIO_SELECTED_PARAMETER)) {
      final StringBuilder listScenarios = new StringBuilder();


      final FacesContext ctx = FacesContext.getCurrentInstance();
      if (ctx != null) {
        final HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        final String contextURL = servletRequest.getRequestURL().toString().replace(servletRequest.getRequestURI().substring(0), "") +
                                  servletRequest.getContextPath();

        if (scenarios != null && !scenarios.isEmpty()) {
          listScenarios.append("Es wurde kein Szenario gewählt. Folgende Szenarien stehen zur Verfügung:<br/>");
          if (servletRequest != null) {

            if (userIDParameter != null && secureValueParameter != null) {
              listScenarios.append("<ul>");
              for (Scenario sc : scenarios) {
                listScenarios.append("<li>");
                listScenarios.append("<a href='" + contextURL + "/moodle/");
                listScenarios.append(userIDParameter + "/");
                listScenarios.append(secureValueParameter + "/");
                listScenarios.append(sc.getId() + "'>[ID: " + sc.getId() + "] " + sc.getName() + "</a>");
                listScenarios.append("</li>");
              }
              listScenarios.append("</ul>");
            }
          }
        } else {
          listScenarios.append("Es wurde kein Szenario gefunden. Ein neues Szenario kann im '");
          listScenarios.append("<a href='" + contextURL + "/admin/" + "'>");
          listScenarios.append("Editieren");
          listScenarios.append("</a>");
          listScenarios.append("'-Bereich erstellt werden.");
        }
      }

      return listScenarios.toString();
    }
    return error;
  }


// public String openScenario() {
//   if (ac != null && scenario != null) {
//     ac.setScenario(scenario);
//   }
//   if (scenario != null) {
//     return ".";
//   }
//   return null;
// }

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
