package de.uniwue.info6.webapp.admin;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SubmissionEditor.java
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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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




import de.uniwue.info6.comparator.LevenshteinDistance;
import de.uniwue.info6.comparator.SqlExecuter;
import de.uniwue.info6.comparator.SqlQuery;
import de.uniwue.info6.comparator.SqlQueryComparator;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserResult;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserResultDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.parser.errors.Error;
import de.uniwue.info6.parser.errors.SqlError;
import de.uniwue.info6.webapp.lists.UserFeedback;
import de.uniwue.info6.webapp.session.SessionBean;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 * @author Christian
 */
@ManagedBean(name = "edit_sub")
@ViewScoped
public class SubmissionEditor implements Serializable {

  private static final long serialVersionUID = -257870928796155120L;
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmissionEditor.class);

  private static final String SUBMISSION_PARAM = "submission";
  private static final String RESULT_PARAM = "result";
  private static final String EXERCISE_PARAM = "exercise";

  private User user;
  private User student;

  private Scenario scenario;
  private ExerciseGroup exerciseGroup;
  private Exercise exercise;
  private UserEntry userEntry;
  private UserResult userResult;

  private ScenarioDao scenarioDao;
  private ExerciseGroupDao exerciseGroupDao;
  private ExerciseDao exerciseDao;
  private SolutionQueryDao solutionDao;
  private UserEntryDao userEntryDao;
  private UserResultDao userResultDao;

  private List<SolutionQuery> solutions;
  private SolutionQuery correspondingSolution;

  private int minDistToCorresponding = 9999;
  private SessionObject ac;
  private ConnectionManager connectionPool;
  private SqlExecuter executer;

  private String newComment;

  /**
   *
   *
   */
  @PostConstruct
  public void init() {

    ExternalContext ec = FacesContext.getCurrentInstance()
                         .getExternalContext();

    scenarioDao = new ScenarioDao();
    exerciseGroupDao = new ExerciseGroupDao();
    exerciseDao = new ExerciseDao();
    solutionDao = new SolutionQueryDao();
    userEntryDao = new UserEntryDao();
    userResultDao = new UserResultDao();

    try {
      Map<String, String> requestParams = ec.getRequestParameterMap();

      ac = SessionObject.pullFromSession();
      user = ac.getUser();

      solutions = new ArrayList<SolutionQuery>();

      connectionPool = ConnectionManager.instance();

      if (!requestParams.isEmpty()
          && requestParams.containsKey(EXERCISE_PARAM)
          && requestParams.containsKey(SUBMISSION_PARAM)
          && requestParams.containsKey(RESULT_PARAM)) {

        String param = requestParams.get(EXERCISE_PARAM);
        String param2 = requestParams.get(SUBMISSION_PARAM);
        String param3 = requestParams.get(RESULT_PARAM);

        final int id = Integer.parseInt(param);
        exercise = exerciseDao.getById(id);

        final int id2 = Integer.parseInt(param2);
        userEntry = userEntryDao.getById(id2);

        final int id3 = Integer.parseInt(param3);
        userResult = userResultDao.getById(id3);

        if (exercise != null && userEntry != null && userResult != null) {
          SolutionQuery example = new SolutionQuery();
          example.setExercise(exercise);
          student = userEntry.getUser();

          solutions = solutionDao.findByExample(example);
          newComment = userResult.getComment();

          if (solutions != null) {

            for (SolutionQuery tmp : solutions) {

              int curDist = LevenshteinDistance
                            .computeLevenshteinDistance(tmp.getQuery()
                                .toLowerCase(), userEntry
                                .getUserQuery().toLowerCase());

              if (curDist < minDistToCorresponding) {
                setCorrespondingSolution(tmp);
                minDistToCorresponding = curDist;
              }

            }

          }

          exerciseGroup = exerciseGroupDao.getById(exercise
                          .getExerciseGroup().getId());

          if (exerciseGroup != null) {
            scenario = scenarioDao.getById(exerciseGroup
                                           .getScenario().getId());
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("ERROR INITIALIZING SUBMISSION EDITOR", e);
    }
  }

  /**
   *
   *
   */
  public String getQuestion() {
    Exercise ex = getExercise();
    if (ex != null) {
      return StringTools.stripHtmlTags(ex.getQuestion());
    }
    return "---";
  }

  public String getEntryDate() {
    if (userEntry != null) {
      DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,
                      DateFormat.FULL, new Locale("de", "DE"));
      return df.format(userEntry.getEntryTime());
    }
    return "---";
  }

  public String getResultDate() {
    if (userResult != null) {
      DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,
                      DateFormat.FULL, new Locale("de", "DE"));
      return df.format(userResult.getLastModified());
    }
    return "---";
  }

  /**
   * Void.
   *
   * @param actionEvent
   *            the action event
   */
  public void save(ActionEvent actionEvent) {

    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;

    boolean saved = false;

    if (userResult != null) {
      userResult.setUser(user);
      userResult.setLastModified(new Date());
      userResult.setComment(newComment);

      if (exercise != null) {
        if (userResult.getCredits() > exercise.getCredits()) {
          sev = FacesMessage.SEVERITY_ERROR;
          message = Cfg.inst().getProp(DEF_LANGUAGE,
                                       "ASSERTION.MAX_CREDITS")
                    + ": " + exercise.getCredits();
        }
      }
      if (message == null) {
        saved = userResultDao.updateInstanceP(userResult);
      }
    }

    if (message == null) {
      if (saved) {
        sev = FacesMessage.SEVERITY_INFO;
        message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.SUCCESS")
                  + ".";
      } else {
        sev = FacesMessage.SEVERITY_ERROR;
        message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.FAIL") + ".";
      }
    }

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }

  /**
   * Disable credit item.
   *
   * @param i
   *            the i
   * @return true, if successful
   */
  public boolean disableCreditItem(int i) {
    if (exercise != null) {
      if (i <= exercise.getCredits()) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   *
   * @return
   */
  public String getCommentText() {
    String comment = "---";
    if (userResult != null && newComment != null
        && !newComment.trim().isEmpty()) {
      return newComment;
    }
    return comment;
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
      String qu = userEntry.getUserQuery();
      if (qu != null && !qu.trim().isEmpty()) {
        String queryText = userEntry.getUserQuery().trim();
        SqlQuery query = new SqlQuery(queryText);
        executer.execute(query);
        SqlError error = query.getError();
        if (error != null) {
          message += Cfg.inst().getProp(DEF_LANGUAGE,
                                        "ASSERTION.SYNTAX_ERROR")
                     + ": " + queryText + "<br/>" + error + "<br/>";
        }
      }
      if (!message.isEmpty()) {
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, message,
                                null);
      }
    } catch (Exception e) {
      LOGGER.error("ERROR CHECKING QUERY SYNTAX", e);
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
   *
   * @param actionEvent
   */
  public void addAsSolution(ActionEvent actionEvent) {

    SolutionQuery newQuery = new SolutionQuery(exercise,
        userEntry.getUserQuery());

    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;
    msg = syntaxCheck();
    Connection connection = null;

    try {
      connection = connectionPool.getConnection(scenario);

      if (msg != null && msg.getSummary() != null
          && !msg.getSummary().isEmpty()) {
        message = msg.getSummary();
        sev = FacesMessage.SEVERITY_ERROR;
      } else {
        newQuery.setStatus((byte) 2); // 0 initial, 1 user used, 2 user
        // unused

        boolean saved = solutionDao.insertNewInstanceP(newQuery);

        if (saved) {

          solutions.add(newQuery);
          setCorrespondingSolution(newQuery);

          minDistToCorresponding = 0;

          sev = FacesMessage.SEVERITY_INFO;
          message = Cfg.inst().getProp(DEF_LANGUAGE,
                                       "SAVED.SOLUTION_SUCCESS")
                    + ".";

        } else {

          sev = FacesMessage.SEVERITY_ERROR;
          message = Cfg.inst().getProp(DEF_LANGUAGE,
                                       "SAVED.SOLUTION_FAIL")
                    + ".";

        }

        // START
        if (saved && !saved) {

          List<UserEntry> userEntries = userEntryDao
                                        .getLastUserEntryForAllUsers(exercise);

          if (userEntries != null) {

            List<SolutionQuery> solutionsOrig = exerciseDao
                                                .getSolutions(exercise);
            LinkedList<SqlQuery> solutions = new LinkedList<SqlQuery>();

            for (SolutionQuery tmp : solutionsOrig) {
              solutions.add(new SqlQuery(tmp.getQuery()));
            }

            for (UserEntry userEntry : userEntries) {

              UserResult userResult = userResultDao
                                      .getLastUserResultFromEntry(userEntry);

              if (userResult != null
                  && userResult.getUser() == null) {

                SqlQuery userQuery = new SqlQuery(
                  userEntry.getUserQuery());

                connectionPool.resetTables(scenario, user);

                SqlExecuter executer = new SqlExecuter(
                  connection, user, scenario);

                SqlQueryComparator comparator = new SqlQueryComparator(
                  userQuery, solutions, executer);

                // get user feedback
                LinkedList<Error> errors = comparator.compare();
                ArrayList<UserFeedback> feedbackList = null;

                boolean userEntrySuccess = false;

                String feedbackSummary = "";

                if (errors != null) {

                  feedbackList = new ArrayList<UserFeedback>();

                  for (Error er : errors) {
                    feedbackList.add(new UserFeedback(er,
                                                      user));
                  }

                }

                for (UserFeedback fb : feedbackList) {

                  if (fb.isMainError()) {
                    userEntrySuccess = fb.isCorrect();
                  } else {
                    feedbackSummary += fb.getTitle() + ": "
                                       + fb.getFeedback() + "<br/>";
                  }

                }

                if (userEntrySuccess) {
                  userResult.setUser(user);
                  userResult
                  .setCredits(exercise.getCredits());
                  userResult.setLastModified(new Date());
                  userResult.setComment(feedbackSummary);
                } else {
                  userResult.setUser(user);
                  userResult.setCredits((byte) 0);
                  userResult.setLastModified(new Date());
                  userResult.setComment(null);
                }

                int index = solutions.indexOf(comparator
                                              .getSolutionQuery());
                if (index < solutions.size()) {
                  userResult.setSolutionQuery(solutionsOrig
                                              .get(index));
                }
                userResultDao.updateInstanceP(userResult);
              }
            }
          }
        }

        newQuery.setStatus((byte) 1); // 0 initial, 1 user used, 2 user
        // unused
        solutionDao.updateInstanceP(newQuery);
        userResult = userResultDao
                     .getLastUserResultFromEntry(userEntry);

      }

    } catch (Exception e) {
      LOGGER.error("ERROR ADDING NEW SOLUTION QUERY", e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    // END

    msg = new FacesMessage(sev, message, details);
    FacesContext.getCurrentInstance().addMessage(null, msg);

  }

  /**
   *
   *
   * @param str
   * @return
   */
  public boolean stringEmpty(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#SubmissionEditor()
   */
  public SubmissionEditor() {

  }

  /**
   *
   *
   * @return
   */
  public int getMinDistToCorresponding() {
    return minDistToCorresponding;
  }

  /**
   *
   *
   * @param minDistToCorresponding
   */
  public void setMinDistToCorresponding(int minDistToCorresponding) {
    this.minDistToCorresponding = minDistToCorresponding;
  }

  /**
   * @return the newComment
   */
  public String getNewComment() {
    return newComment;
  }

  /**
   * @param newComment
   *            the newComment to set
   */
  public void setNewComment(String newComment) {
    if (newComment != null) {
      if (newComment.endsWith("<br/>")) {
        newComment = newComment.substring(0, newComment.length() - 5);
      }
      this.newComment = StringTools.stripHtmlTagsForScenario(newComment);
    }
  }

  public boolean userHasRights() {
    UserRights rights = new UserRights().initialize();
    return rights.hasRatingRight(user, exercise);
  }

  public boolean userNotHasRights() {
    return !userHasRights();
  }

  public List<SolutionQuery> getSolutions() {
    return solutions;
  }

  public void setSolutions(List<SolutionQuery> solutions) {
    this.solutions = solutions;
  }

  public Exercise getExercise() {
    return exercise;
  }

  public void setExercise(Exercise exercise) {
    this.exercise = exercise;
  }

  public UserResult getUserResult() {
    return userResult;
  }

  public void setUserResult(UserResult userResult) {
    this.userResult = userResult;
  }

  /**
   * @return the student
   */
  public User getStudent() {
    return student;
  }

  /**
   * @param student
   *            the student to set
   */
  public void setStudent(User student) {
    this.student = student;
  }

  public Scenario getScenario() {
    return scenario;
  }

  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  public UserEntry getUserEntry() {
    return userEntry;
  }

  public void setUserEntry(UserEntry userEntry) {
    this.userEntry = userEntry;
  }

  public ScenarioDao getScenarioDao() {
    return scenarioDao;
  }

  public void setScenarioDao(ScenarioDao scenarioDao) {
    this.scenarioDao = scenarioDao;
  }

  public ExerciseGroupDao getExerciseGroupDao() {
    return exerciseGroupDao;
  }

  public void setExerciseGroupDao(ExerciseGroupDao exerciseGroupDao) {
    this.exerciseGroupDao = exerciseGroupDao;
  }

  public SolutionQuery getCorrespondingSolution() {
    return correspondingSolution;
  }

  public void setCorrespondingSolution(SolutionQuery correspondingSolution) {
    this.correspondingSolution = correspondingSolution;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
