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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.misc.DateFormatTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.admin.UserRights;
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
  private User user;
  private String scenarioParameter, userIDParameter, encryptedCodeParameter;
  private ScenarioDao scenarioDao;
  private UserRights userRights;
  private List<Scenario> scenarios;

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
    this.ac = SessionObject.pullFromSession();

    if (this.ac != null) {
      this.scenario               = this.ac.getScenario();
      this.user                   = this.ac.getUser();
      this.scenarioParameter      = this.ac.getScenarioParameter();

      this.userIDParameter        = this.ac.getUserIDParameter();
      this.encryptedCodeParameter = this.ac.getSecureValueParameter();
      this.userRights             = new UserRights().initialize();

      this.scenarioDao            = new ScenarioDao();
      this.scenarios              = this.scenarioDao.findAll();

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
        boolean canBeAccessedByUser = false;
        if (scenario != null && user != null) {
          canBeAccessedByUser = this.userRights.hasViewRights(user, scenario);
        }
        if (temp != null && canBeAccessedByUser) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   *
   *
   * @return
   */
  public String getIntroductionTitle() {
    if (isValidScenario()) {
      return Cfg.inst().getText("AC.INTRO") + " - " + this.scenario.getName();
    } else {
      return "Info";
    }
  }

  /**
   *
   *
   * @return
   */
  public String getIntroductionText() {
    boolean canBeAccessedByUser = false;
    if (scenario != null && user != null) {
      canBeAccessedByUser = this.userRights.hasViewRights(user, scenario);
    }
    if (isValidScenario()) {
      return scenario.getDescription();
    } else if (scenarioParameter == null || !canBeAccessedByUser) {
      final StringBuilder listScenarios = new StringBuilder();
      final FacesContext ctx = FacesContext.getCurrentInstance();
      if (ctx != null) {
        final HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        final String contextURL = servletRequest.getRequestURL().toString().replace(servletRequest.getRequestURI().substring(0), "") +
                                  servletRequest.getContextPath();
        final User user = this.ac.getUser();

        if (scenario != null && !canBeAccessedByUser) {
          listScenarios.append("<span style='color:darkred; font-size:14px'>Das Szenario mit der ID:[" + scenario.getId()
                               + "] steht momentan nicht zur Verfügung!</span><br/><br/>");
          this.ac.setScenario(null);
        }
        listScenarios.append("<span class='scenario-overview'>");

        if (scenarios != null && !scenarios.isEmpty() && user != null) {
          listScenarios.append("Folgende Szenarien stehen zur Verfügung:<br/>");
          if (servletRequest != null) {
            if (userIDParameter != null && encryptedCodeParameter != null) {
              listScenarios.append("<ul>");
              for (Scenario sc : scenarios) {
                canBeAccessedByUser = this.userRights.hasViewRights(user, sc);
                listScenarios.append("<li>");
                if (canBeAccessedByUser) {
                  listScenarios.append("<a href='" + contextURL + "/moodle/");
                  listScenarios.append(userIDParameter + "/");
                  listScenarios.append(encryptedCodeParameter + "/");
                  listScenarios.append(sc.getId() + "'>");
                }
                listScenarios.append("[ID: " + sc.getId() + "] " + sc.getName());
                if (canBeAccessedByUser) {
                  listScenarios.append("</a>");
                }

                if (sc.getStartTime() != null) {
                  listScenarios.append("<br><span style='font-size:13px'>Verfügbar ab ");
                  listScenarios.append(DateFormatTools.getGermanFormat(sc.getStartTime()));
                  listScenarios.append(".</span>");
                }

                if (sc.getEndTime() != null) {
                  listScenarios.append("<br><span style='font-size:13px'>Verfügbar bis ");
                  listScenarios.append(DateFormatTools.getGermanFormat(sc.getEndTime()));
                  listScenarios.append(".</span>");
                }

                if (!canBeAccessedByUser) {
                  listScenarios.append("<br><span style='font-size:13px;color:darkred'>(Momentan nicht verfügbar)</span>");
                }

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
      listScenarios.append("</span>");

      return listScenarios.toString();
    }

    this.ac.setScenario(null);
    this.ac.setScenarioParameter(null);

    return error;
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
