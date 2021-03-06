package de.uniwue.info6.webapp.admin;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AdminEditExercise.java
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

import static de.uniwue.info6.misc.properties.PropBool.SHOWCASE_MODE;
import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;

import de.uniwue.info6.comparator.SqlExecuter;
import de.uniwue.info6.comparator.SqlQuery;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.parser.errors.SqlError;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "edit_ex")
@ViewScoped
public class AdminEditExercise implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AdminEditExercise.class);

  private Integer id;
  private Integer originalExerciseId;
  private ExerciseGroup exerciseGroup;
  private Date lastModified;
  private static final String EXERCISE_PARAM = "exercise";
  private static final String GROUP_PARAM = "group";
  private Exercise exercise;
  private ExerciseDao exerciseDao;
  private ScenarioDao scenarioDao;
  private ExerciseGroupDao exerciseGroupDao;
  private Scenario scenario;
  private ArrayList<String> availableTables;
  private SqlExecuter executer;
  private User user;
  private SessionObject ac;

  private ConnectionManager connectionPool;
  private SolutionQueryDao solutionDao;

  private String question;
  private String questionOriginal;

  private Byte credits;
  private Byte creditsOriginal;

  private String name;
  private String nameOriginal;

  private List<SolutionQuery> solutions;
  private List<SolutionQuery> solutionsToDelete;

  private boolean isNewExercise;
  private boolean userHasRights;
  private UserRights rights;

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    userHasRights = false;
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    exerciseDao = new ExerciseDao();
    solutionDao = new SolutionQueryDao();
    scenarioDao = new ScenarioDao();
    exerciseGroupDao = new ExerciseGroupDao();
    Map<String, String> requestParams = ec.getRequestParameterMap();
    ac = SessionObject.pullFromSession();
    user = ac.getUser();
    solutions = new ArrayList<SolutionQuery>();
    solutionsToDelete = new ArrayList<SolutionQuery>();
    connectionPool = ConnectionManager.instance();
    rights = new UserRights().initialize();

    if (!requestParams.isEmpty()) {
      try {
        // exercise get-parameter found
        if (requestParams.containsKey(EXERCISE_PARAM)) {
          String param = requestParams.get(EXERCISE_PARAM);
          final int id = Integer.parseInt(param);
          exercise = exerciseDao.getById(id);
          if (exercise != null) {
            exerciseGroup = exerciseGroupDao.getById(exercise.getExerciseGroup().getId());
            name = exercise.getName();
            nameOriginal = name;
            question = exercise.getQuestion();
            questionOriginal = question;
            credits = exercise.getCredits();
            creditsOriginal = credits;

            SolutionQuery example = new SolutionQuery();
            example.setExercise(exercise);
            solutions = solutionDao.findByExample(example);

            if (exercise.getExercise() != null) {
              originalExerciseId = exercise.getExercise().getId();
            }
          }
        } else if (requestParams.containsKey(GROUP_PARAM)) {
          String param = requestParams.get(GROUP_PARAM);
          final int id = Integer.parseInt(param);
          exerciseGroup = exerciseGroupDao.getById(id);
          isNewExercise = true;
        }

        if (exerciseGroup != null) {
          scenario = scenarioDao.getById(exerciseGroup.getScenario().getId());
          if (scenario != null) {
            if (rights.hasEditingRight(user, scenario, true)) {
              userHasRights = true;
              availableTables = connectionPool.getScenarioTableNames(scenario);
              connectionPool.resetTables(scenario, user);
            }
          }
        }
      } catch (NumberFormatException e) {
        // new exercise window
      } catch (Exception e) {
        LOGGER.error("ERROR GETTING EXERCISE FIELDS FROM DATABASE", e);
      }
    }
  }

  /**
   *
   *
   * @param actionEvent
   */
  public void save(ActionEvent actionEvent) {
    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;

    if (question == null || StringTools.stripHtmlTags(question.trim()).isEmpty()) {
      message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_EX.EMPTY_QUESTION");
      sev = FacesMessage.SEVERITY_ERROR;
    } else if (credits == null || credits == 0) {
      message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_EX.EMPTY_CREDITS");
      sev = FacesMessage.SEVERITY_ERROR;
    } else if (solutions == null || solutions.isEmpty()) {
      message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_EX.EMPTY_SOLUTION");
      sev = FacesMessage.SEVERITY_ERROR;
    } else if (!solutions.isEmpty()) {
      boolean textfound = false;
      for (SolutionQuery sol : solutions) {
        if (sol.getQuery() != null && !sol.getQuery().trim().isEmpty()) {
          textfound = true;
          break;
        }
      }
      if (!textfound) {
        message = Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_EX.EMPTY_SOLUTION");
        sev = FacesMessage.SEVERITY_ERROR;
      } else {
        FacesMessage m = syntaxCheck();
        if (m != null && m.getSummary() != null && !m.getSummary().isEmpty()) {
          message = m.getSummary();
          sev = FacesMessage.SEVERITY_ERROR;
        }
      }
    }

    // ------------------------------------------------ //
    final boolean showCaseMode = Cfg.inst().getProp(MAIN_CONFIG, SHOWCASE_MODE);
    if (message == null && showCaseMode) {
      message = Cfg.inst().getProp(DEF_LANGUAGE, "SHOWCASE_ERROR");
      sev = FacesMessage.SEVERITY_ERROR;
    }
    // ------------------------------------------------ //

    if (message == null) {
      try {
        sev = FacesMessage.SEVERITY_INFO;
        if (isNewExercise) {
          exercise = new Exercise();
          exercise.setExerciseGroup(exerciseGroup);
        }
        while (question.endsWith("<br/>")) {
          question = question.substring(0, question.length() - 5);
        }
        while (question.endsWith("<br>")) {
          question = question.substring(0, question.length() - 4);
        }

        exercise.setQuestion(question);
        exercise.setCredits(credits);
        exercise.setName(name);
        exercise.setLastModified(new Date());
        if (isNewExercise) {
          exerciseDao.insertNewInstanceP(exercise);
          isNewExercise = false;
        } else {
          exerciseDao.updateInstanceP(exercise);
        }

        for (SolutionQuery sol : solutions) {
          boolean exists = solutionDao.checkIfExists(sol);
          String qu = sol.getQuery();
          if (qu != null && !qu.trim().isEmpty()) {
            if (exists) {
              solutionDao.updateInstanceP(sol);
            } else {
              sol.setExercise(exercise);
              solutionDao.insertNewInstanceP(sol);
            }
          }
        }

        for (SolutionQuery sol : solutionsToDelete) {
          boolean exists = solutionDao.checkIfExists(sol);
          if (exists) {
            solutionDao.deleteInstanceP(sol);
          }
        }
        solutionsToDelete.clear();

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        message = Cfg.inst().getProp(DEF_LANGUAGE, "SUCCESS") + " (" + df.format(new Date()) + ")";
      } catch (Exception e) {
        LOGGER.error("error saving exercise: " + exercise, e);
        sev = FacesMessage.SEVERITY_ERROR;
        message = Cfg.inst().getProp(DEF_LANGUAGE, "ERROR");
      }
    }

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }


  /**
   *
   *
   * @return
   */
  public String openExercise() {
    if (ac != null && scenario != null) {
      ac.setScenario(scenario);
    }
    if (exercise != null) {
      return "ex-" + exercise.getId();
    }
    return null;
  }


  /**
   *
   *
   * @return
   */
  public boolean disableExerciseLink() {
    return openExercise() == null;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasCredits() {
    return credits != null && credits != 0;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasOriginalExercise() {
    return originalExerciseId != null;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasName() {
    return name != null && !name.isEmpty();
  }

  /**
   *
   *
   * @return
   */
  public boolean hasQuestion() {
    return question != null && !StringTools.stripHtmlTags(question).trim().isEmpty();
  }

  /**
   *
   *
   * @return
   */
  public boolean hasModifiedTime() {
    if (exercise != null && exercise.getLastModified() != null) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getLastModifiedTime() {
    if (exercise != null) {
      DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, new Locale("de", "DE"));
      return df.format(exercise.getLastModified());
    }
    return null;
  }

  /**
   *
   *
   * @return
   */
  public String getExerciseGroupName() {
    if (exerciseGroup != null) {
      String group = exerciseGroup.getName() + ", ID: " + exerciseGroup.getId() + ", ";
      group += exerciseGroup.getIsRated() == true ? Cfg.inst().getProp(DEF_LANGUAGE, "RATED") + "." : Cfg.inst().getProp(DEF_LANGUAGE,
               "UNRATED") + ".";
      return group;
    }
    return null;
  }

  /**
   *
   *
   * @param actionEvent
   */
  public void resetFields(ActionEvent actionEvent) {
    question = questionOriginal;
    credits = creditsOriginal;
    name = nameOriginal;

    SolutionQuery example = new SolutionQuery();
    example.setExercise(exercise);
    solutions = solutionDao.findByExample(example);
  }

  /**
   *
   *
   * @param actionEvent
   */
  public void getSyntaxMessage(ActionEvent actionEvent) {
    FacesMessage msg = syntaxCheck();
    if (msg != null) {
      FacesContext.getCurrentInstance().addMessage(null, msg);
    } else {
      DateFormat df = new SimpleDateFormat("HH:mm:ss");
      FacesContext.getCurrentInstance()
      .addMessage(
        null,
        new FacesMessage(Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_EX.NO_SYNTAX_ERROR") + " ("
                         + df.format(new Date()) + ")"));
    }
  }

  /**
   *
   *
   * @return
   */
  private FacesMessage syntaxCheck() {
    String message = "";
    Connection connection = null;

    try {
      connection = connectionPool.getConnection(scenario);
      executer = new SqlExecuter(connection, user, scenario);
      for (SolutionQuery sol : solutions) {
        String qu = sol.getQuery();
        if (qu != null && !qu.trim().isEmpty()) {
          String queryText = sol.getQuery().trim();
          SqlQuery query = new SqlQuery(queryText);
          executer.execute(query);
          SqlError error = query.getError();
          if (error != null) {
            message += Cfg.inst().getProp(DEF_LANGUAGE, "EDIT_EX.SYNTAX_ERROR") + ": " + queryText + "<br/>" + error
                       + "<br/>";
          }
        }
      }
      if (!message.isEmpty()) {
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
      }
    } catch (SQLException e) {
      //
    } catch (Exception e) {
      LOGGER.error("SYNTAX CHECK FAILED", e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  /**
   *
   */
  public AdminEditExercise() {
  }

  /**
   *
   *
   * @param solution
   */
  public void delete(SolutionQuery solution) {
    if (solution != null) {
      solutions.remove(solution);
      solutionsToDelete.add(solution);
    }
  }

  /**
   *
   *
   * @param actionEvent
   */
  public void addSolution(ActionEvent actionEvent) {
    solutions.add(new SolutionQuery());
  }

  /**
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id
   *            the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return the originalExerciseId
   */
  public Integer getOriginalExerciseId() {
    return originalExerciseId;
  }

  /**
   * @param originalExerciseId
   *            the originalExerciseId to set
   */
  public void setOriginalExerciseId(Integer originalExerciseId) {
    this.originalExerciseId = originalExerciseId;
  }

  /**
   * @return the exerciseGroup
   */
  public ExerciseGroup getExerciseGroup() {
    return exerciseGroup;
  }

  /**
   *
   *
   * @param event
   */
  public void onCancel(RowEditEvent event) {
    //
  }

  /**
   *
   *
   * @param event
   */
  public void onEdit(RowEditEvent event) {
    //
  }

  /**
   * @param exerciseGroup
   *            the exerciseGroup to set
   */
  public void setExerciseGroup(ExerciseGroup exerciseGroup) {
    this.exerciseGroup = exerciseGroup;
  }

  /**
   * @return the question
   */
  public String getQuestion() {
    return question;
  }

  /**
   * @param question
   *            the question to set
   */
  public void setQuestion(String question) {
    this.question = StringTools.stripHtmlTagsForQuestion(question);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *            the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the credits
   */
  public Byte getCredits() {
    return credits;
  }

  /**
   * @param credits
   *            the credits to set
   */
  public void setCredits(Byte credits) {
    this.credits = credits;
  }

  /**
   * @return the lastModified
   */
  public Date getLastModified() {
    return lastModified;
  }

  /**
   * @param lastModified
   *            the lastModified to set
   */
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /**
   * @return the availableTables
   */
  public ArrayList<String> getAvailableTables() {
    return availableTables;
  }

  /**
   * @param availableTables
   *            the availableTables to set
   */
  public void setAvailableTables(ArrayList<String> availableTables) {
    this.availableTables = availableTables;
  }

  /**
   * @return the solutions
   */
  public List<SolutionQuery> getSolutions() {
    return solutions;
  }

  /**
   * @param solutions
   *            the solutions to set
   */
  public void setSolutions(List<SolutionQuery> solutions) {
    this.solutions = solutions;
  }

  /**
   * @return the userHasRights
   */
  public boolean isUserHasRights() {
    return userHasRights;
  }

  /**
   * @param userHasRights
   *            the userHasRights to set
   */
  public void setUserHasRights(boolean userHasRights) {
    this.userHasRights = userHasRights;
  }
}
