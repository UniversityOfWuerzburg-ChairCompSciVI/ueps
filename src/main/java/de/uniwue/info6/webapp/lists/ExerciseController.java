package de.uniwue.info6.webapp.lists;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.comparator.RefLink;
import de.uniwue.info6.comparator.SqlExecuter;
import de.uniwue.info6.comparator.SqlQuery;
import de.uniwue.info6.comparator.SqlQueryComparator;
import de.uniwue.info6.comparator.SqlResult;
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
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.parser.errors.Error;
import de.uniwue.info6.webapp.admin.UserRights;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "e")
@ViewScoped
public class ExerciseController implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final Log LOGGER = LogFactory.getLog(ExerciseController.class);

  @SuppressWarnings("unused")
  private static final String error = "ERROR: Scenario not found", exerciseParam = "exercise";
  private static final String RESOURCE_PATH_IMAGES = "scn_images";

  private SessionObject ac;
  private Scenario scenario;
  private Exercise exercise;
  private ExerciseGroup exerciseGroup;
  private List<SolutionQuery> solutions;
  private int usedSolutionIndex;
  private LinkedList<SqlQuery> solutionQueries;

  // daos
  private ExerciseGroupDao exGroupDao;
  private ScenarioDao scenarioDao;
  private ExerciseDao exerciseDao;
  private UserEntryDao userEntryDao;
  private UserResultDao userResultDao;
  private SolutionQueryDao solutionQueryDao;

  private User user;

  private String userString, feedback, solutionTable, userTable, diagramImage;
  private SqlQuery userQuery, usedSolutionQuery;
  private SqlExecuter executer;
  private ConnectionManager connectionPool;
  @SuppressWarnings("unused")
  private HashMap<String, String> relevantTables;

  private boolean resultVisible, userResultVisible, feedbackVisible;
  private boolean syntaxError;

  private ArrayList<String> availableTables;

  private HashMap<String, String> tableContent;
  private ArrayList<UserFeedback> feedbackList;

  // mysql-reflinks
  private String mainRefLink;
  private LinkedList<RefLink> refLinks;
  private boolean userHasRights, debug = false, showFeedback, querySaved;
  private UserRights rights;
  private String importScriptError;

  // user result table resources
  private List<String> userQueryColumns;
  private List<TableEntry> userQueryValues;
  private List<TableEntry> filteredUserQueryValues;
  private List<ColumnModel> userQueryMColumns = new ArrayList<ColumnModel>();
  private String userQueryFilter;

  // solution table resources
  private List<String> solutionQueryColumns;
  private List<TableEntry> solutionQueryValues;
  private List<TableEntry> filteredSolutionQueryValues;
  private List<ColumnModel> solutionQueryMColumns = new ArrayList<ColumnModel>();
  private String solutionQueryFilter;

  // relevant tables
  private HashMap<String, List<String>> tableColumns;
  private HashMap<String, List<TableEntry>> tableValues;
  private HashMap<String, List<TableEntry>> tableValuesOriginal;
  private String[] currentTableFilter;

  /**
   * @param currentTable
   *          the currentTable to set
   */
  public void setCurrentTable(String currentTable, Integer index) {
    currentTable = currentTable.trim();
    String filter = currentTableFilter[index];
    List<TableEntry> currentValues = tableValuesOriginal.get(currentTable);
    if (filter != null && !filter.trim().isEmpty()) {
      List<String> columns = tableColumns.get(currentTable);
      ArrayList<TableEntry> filteredValues = setQueryFilter(filter.trim(), columns, currentValues);
      tableValues.put(currentTable, filteredValues);
    } else {
      tableValues.put(currentTable, currentValues);
    }
  }

  /**
   *
   *
   * @param currentTable
   * @param index
   */
  public void resetCurrentTable(String currentTable, Integer index) {
    currentTable = currentTable.trim();
    List<TableEntry> currentValues = tableValuesOriginal.get(currentTable);
    tableValues.put(currentTable, currentValues);
    this.currentTableFilter[index] = null;
  }

  /**
   *
   *
   * @return
   */
  public MethodExpression getSortByModel() {
    FacesContext context = FacesContext.getCurrentInstance();
    return context.getApplication().getExpressionFactory().createMethodExpression(
             context.getELContext(), "#{e.sortByModel}", Integer.class,
             new Class[] { Object.class, Object.class });
  }

  /**
   *
   *
   * @param obj1
   * @param obj2
   * @return
   */
  public int sortByModel(String obj1, String obj2) {
    try {
      int int1 = Integer.parseInt(obj1);
      int int2 = Integer.parseInt(obj2);
      if (int1 > int2) {
        return 1;
      }
      if (int1 < int2) {
        return -1;
      }
      if (int1 == int2) {
        return 0;
      }
    } catch (NumberFormatException e) {
    }

    try {
      double double1 = Double.parseDouble(obj1);
      double double2 = Double.parseDouble(obj2);
      if (double1 > double2) {
        return 1;
      }
      if (double1 < double2) {
        return -1;
      }
      if (double1 == double2) {
        return 0;
      }
    } catch (NumberFormatException e) {
    }
    return obj1.compareTo(obj2);
  }

  /**
   *
   *
   */
  public void createUserQueryColumns() {
    userQueryMColumns.clear();
    for (int i = 0; i < userQueryColumns.size(); i++) {
      userQueryMColumns.add(new ColumnModel(userQueryColumns.get(i), StringTools.zeroPad(i, 2)));
    }
  }

  /**
   *
   *
   */
  public void createSolutionQueryColumns() {
    solutionQueryMColumns.clear();
    for (int i = 0; i < solutionQueryColumns.size(); i++) {
      solutionQueryMColumns.add(new ColumnModel(solutionQueryColumns.get(i), StringTools.zeroPad(i,
                                2)));
    }
  }

  /**
   *
   *
   * @param scenario
   * @param user
   * @return
   */
  public ExerciseController init_debug(Scenario scenario, Exercise exercise, User user) {
    this.scenario = scenario;
    this.user = user;
    this.exercise = exercise;
    this.connectionPool = ConnectionManager.offline_instance();

    debug = true;
    init();
    return this;
  }

  /**
   *
   */
  public ExerciseController() {
  }

  @PostConstruct
  public void init() {
    if (connectionPool == null || debug) {
      importScriptError = "";
      userHasRights = false;
      showFeedback = false;
      querySaved = false;
      usedSolutionIndex = 0;
      Boolean setEntry = null;
      Boolean setSolution = null;
      this.rights = new UserRights();
      this.rights.initialize();

      if (!debug) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        setEntry = (Boolean) sessionMap.get("show_entry");
        setSolution = (Boolean) sessionMap.get("show_solution");
        // get current scenario
        ac = new SessionCollector().getSessionObject();
        user = ac.getUser();
        scenario = ac.getScenario();
        // set up connection pool
        connectionPool = ConnectionManager.instance();
      }

      this.resultVisible = false;
      this.userResultVisible = false;
      this.feedbackVisible = false;
      this.syntaxError = false;

      // init hibernate daos
      exGroupDao = new ExerciseGroupDao();
      scenarioDao = new ScenarioDao();
      exerciseDao = new ExerciseDao();
      userEntryDao = new UserEntryDao();
      userResultDao = new UserResultDao();
      solutionQueryDao = new SolutionQueryDao();

      if (scenario != null) {
        exerciseDao.updateObject(scenario);

        // get exercise id
        if (!debug) {
          ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
          Map<String, String> requestParams = ec.getRequestParameterMap();
          final int id = Integer.parseInt(requestParams.get(exerciseParam));
          exercise = exerciseDao.getById(id);
        }

        if (exercise != null) {
          solutionQueries = new LinkedList<SqlQuery>();
          solutions = exerciseDao.getSolutions(exercise);
          exerciseGroup = exGroupDao.getById(exercise.getExerciseGroup().getId());

          if (exerciseGroup != null) {

            if (exerciseGroup.getDescription() == null
                || exerciseGroup.getDescription().trim().isEmpty()) {
              showFeedback = true;
            } else if (exerciseGroup.getDescription().trim().equals("[NO_FEEDBACK]")) {
              showFeedback = false;
            }

            if (!exerciseGroup.getScenario().getId().equals(scenario.getId())) {
              scenario = scenarioDao.getById(exerciseGroup.getScenario().getId());
              userHasRights = rights.hasViewRights(user, scenario, exerciseGroup);
              if (!debug && userHasRights) {
                ac.setScenario(scenario);
              }
            }
            userHasRights = rights.hasViewRights(user, scenario, exerciseGroup);

            diagramImage = scenario.getImagePath();
            if (diagramImage != null) {
              diagramImage = RESOURCE_PATH_IMAGES + "/" + scenario.getId() + "/" + diagramImage;
            }

            // get list of sql tables
            availableTables = connectionPool.getScenarioTableNamesWithHash(scenario);
            if (availableTables == null || availableTables.isEmpty()) {
              importScriptError = connectionPool.checkIfImportScriptExists(scenario);
            }

            tableContent = new HashMap<String, String>();

            // relevant tables
            tableColumns = new HashMap<String, List<String>>();
            tableValues = new HashMap<String, List<TableEntry>>();

            if (userHasRights) {
              for (SolutionQuery qu : solutions) {
                SqlQuery sqlQuery = new SqlQuery(qu.getQuery());
                try {
                  if (mainRefLink == null && sqlQuery != null) {
                    RefLink ref = sqlQuery.getRefLink();
                    mainRefLink = ref.getUrl();
                  }
                } catch (Exception e) {
                }
                solutionQueries.add(sqlQuery);
              }

              try {
                connectionPool.resetTables(scenario, user);
              } catch (Exception e) {
                LOGGER.error("COULD NOT RESET TABLES", e);
              }

              UserEntry entry = userEntryDao.getLastUserEntry(exercise, user);

              if (entry != null && setEntry != null && setEntry) {
                userString = entry.getUserQuery();
              } else if (setSolution != null && setSolution) {
                if (entry != null) {
                  UserResult result = userResultDao.getLastUserResultFromEntry(entry);
                  SolutionQuery query = result.getSolutionQuery();
                  query = solutionQueryDao.getById(query.getId());
                  if (result != null && query != null) {
                    String query_sol = query.getQuery();
                    if (query_sol != null) {
                      userString = query_sol;
                    }
                  }
                } else {
                  SolutionQuery example = new SolutionQuery();
                  example.setExercise(exercise);
                  List<SolutionQuery> solutions = solutionQueryDao.findByExample(example);
                  if (solutions != null && !solutions.isEmpty()) {
                    userString = solutions.get(0).getQuery();
                  }
                }
              }

              Connection connection = null;
              try {
                relevantTables = new HashMap<String, String>();
                connection = connectionPool.getConnection(scenario);
                executer = new SqlExecuter(connection, user, scenario);

                SqlQuery selectTable = null;

                if (availableTables != null) {
                  for (String table : availableTables) {
                    String showTableCommand = "SELECT * FROM " + table + ";";
                    selectTable = new SqlQuery(showTableCommand);
                    executer.execute(selectTable);
                    try {
                      SqlResult result = selectTable.getResult();
                      if (result != null) {
                        String[][] data = result.getData();
                        ResultSetMetaData metaData = result.getResultMetaData();
                        List<String> clNames = new ArrayList<String>();
                        if (metaData != null) {
                          for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            String name = metaData.getColumnName(i);
                            if (name != null && !name.trim().isEmpty()) {
                              clNames.add(name);
                            }
                          }
                          tableColumns.put(table, clNames);
                        }
                        if (data != null) {
                          List<TableEntry> el = new ArrayList<TableEntry>();
                          for (int i = 0; i < data.length; i++) {
                            TableEntry en = new TableEntry(clNames);
                            for (int z = 0; z < data[i].length; z++) {
                              en.addValue(data[i][z], z);
                            }
                            el.add(en);
                          }
                          tableValues.put(table, el);
                        }

                        this.tableValuesOriginal = new HashMap<String, List<TableEntry>>(
                          tableValues);
                        currentTableFilter = new String[tableValues.size() + 2];
                      }
                    } catch (Exception e) {
                      LOGGER.error("ERROR BUILDING RELEVANT TABLES", e);
                    }
                  }
                }
              } catch (SQLException e) {
                String er = ExceptionUtils.getStackTrace(e);
                if (er.length() > 500) {
                  importScriptError = er.substring(0, 500) + " [...]";
                } else {
                  importScriptError = er;
                }
              } catch (Exception e) {
                LOGGER.error("ERROR GETTING RELEVANT TABLES", e);
              } finally {
                if (connection != null) {
                  try {
                    connection.close();
                  } catch (SQLException e) {
                    e.printStackTrace();
                  }
                }
              }
            }
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
  public String getSQLError() {
    return "<span style='color:red'>" + "Import-Skript nicht gefunden! Siehe Details:"
           + "<br/><br/></span><span style='color:red;font-size:10px' class='monospace'>"
           + importScriptError + "</span>";
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public boolean showResults() {
    if (exercise != null) {
      if (!showFeedback) {
        return false;
      }
      if (!isRated()) {
        return true;
      }
      Date end = exerciseGroup.getEndTime();
      if (end != null) {
        if (end.before(new Date())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   *
   */
  public String getSolution() {
    return solutions.get(0).getQuery();
  }

  /**
   *
   *
   * @return
   */
  public boolean hasEditingRights() {
    if (user != null && scenario != null) {
      return rights.hasRights(user, scenario);
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean adminSolutionVisible() {
    Boolean prop = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG,
                                      PropBool.SHOW_SOL_TO_PRIVELEGED);
    if (prop && hasEditingRights()) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   */
  public void compareResults() {

    if (scenario == null || user == null) {
      return;
    }

    Connection connection = null;
    try {
      this.feedbackVisible = false;
      this.resultVisible = false;
      this.syntaxError = false;

      connection = connectionPool.getConnection(scenario);
      executer = new SqlExecuter(connection, user, scenario);
      SqlQueryComparator comparator = new SqlQueryComparator(userQuery, solutionQueries, executer);

      // get user feedback
      LinkedList<Error> errors = comparator.compare();
      usedSolutionQuery = comparator.getSolutionQuery();
      if (solutionQueries != null && !solutionQueries.isEmpty()) {
        usedSolutionIndex = solutionQueries.indexOf(usedSolutionQuery);
      }

      if (errors != null) {
        feedbackList = new ArrayList<UserFeedback>();
        for (Error er : errors) {
          feedbackList.add(new UserFeedback(er, user));
        }
      }

      for (UserFeedback fd : feedbackList) {
        if (fd.isJavaError()) {
          feedbackList.remove(fd);
          break;
        }
      }

      boolean userEntrySuccess = false;

      String feedbackSummaryDB = "";

      for (UserFeedback fb : feedbackList) {
        if (fb.isSyntaxError()) {
          syntaxError = true;
        }
        if (fb.isMainError()) {
          userEntrySuccess = fb.isCorrect();
        } else {
          String fd = fb.getFeedback();
          if (!userEntrySuccess) {
            feedbackSummaryDB += fb.getTitle() + ": " + fd + "<br/>";
          }
        }
      }

      if (usedSolutionQuery == null || usedSolutionQuery.getResult() == null) {
        ArrayList<UserFeedback> newFeedbackList = new ArrayList<UserFeedback>();
        newFeedbackList.add(new UserFeedback(Cfg.inst().getProp(DEF_LANGUAGE, "QUE.UNEXPECTED_ERROR"),
                                             Cfg.inst().getProp(DEF_LANGUAGE, "QUE.UNEXPECTED_ERROR2"), user));
        newFeedbackList.addAll(feedbackList);
        feedbackList = newFeedbackList;
        this.feedbackVisible = true;
        LOGGER.error("EMPTY SOLUTION RESULT, FAULTY SOLUTION-QUERY?\n" + feedbackSummaryDB);
      }

      if (usedSolutionQuery != null && usedSolutionQuery.getResult() != null) {
        /*
         * **************************************************************
         * get column-names and values from db solution-query
         * **************************************************************
         */

        if (solutionQueryValues == null || solutionQueryColumns == null
            || solutionQueryValues.isEmpty() || solutionQueryColumns.isEmpty()) {
          // setting correct solution
          SqlResult sol_result = usedSolutionQuery.getResult();
          String[][] data = sol_result.getData();
          solutionQueryColumns = new ArrayList<String>();
          solutionQueryValues = new ArrayList<TableEntry>();

          ResultSetMetaData metaData = sol_result.getResultMetaData();
          if (metaData != null) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
              String name = metaData.getColumnLabel(i);

              if (user != null && name != null && name.contains(user.getId() + "_")) {
                name = name.replaceAll(user.getId() + "_", "");
              }

              if (name != null && !name.trim().isEmpty()) {
                solutionQueryColumns.add(name);
              }
            }
          }
          if (data != null) {
            for (int i = 0; i < data.length; i++) {
              TableEntry en = new TableEntry(solutionQueryColumns);
              for (int z = 0; z < data[i].length; z++) {
                en.addValue(data[i][z], z);
              }
              solutionQueryValues.add(en);
            }
          }
          filteredSolutionQueryValues = solutionQueryValues;
          createSolutionQueryColumns();
        }

        if (showResults()) {
          this.resultVisible = true;
          this.feedbackVisible = true;
        }

        UserEntry entry = null;
        UserResult result = null;
        boolean userEntryAvailable = false;
        byte reachedCredits = userEntrySuccess ? exercise.getCredits() : 0;
        SolutionQuery usedQuery = null;

        int index = solutionQueries.indexOf(usedSolutionQuery);
        if (index < solutions.size()) {
          usedQuery = solutions.get(index);
        }

        if (!userString.trim().isEmpty() && (!isRated() || !showResults())) {

          entry = userEntryDao.getLastEntry(exercise, user);
          String msg = "Ihre Abgabe wurde erfolgreich gespeichert.";
          if (entry != null) {
            msg = "Ihre Abgabe wurde erfolgreich überschrieben.";
          }

          if (Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.ONLY_SAVE_LAST_USER_QUERY)) {
            // String msg = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.FILTER5");
            // TODO:
            if (entry != null) {
              entry.setUserQuery(userString);
              entry.setEntryTime(new Date());
              entry.setResultMessage(feedbackSummaryDB);
              userEntryAvailable = userEntryDao.updateInstance(entry);
              result = userResultDao.getLastUserResultFromEntry(entry);

              if (result != null) {
                result.setCredits(reachedCredits);
                result.setLastModified(new Date());
                result.setSolutionQuery(usedQuery);
                result.setComment(feedbackSummaryDB);
                userResultDao.updateInstance(result);
              }
            } else {
              entry = new UserEntry(user, exercise, userString, new Date());
              entry.setResultMessage(feedbackSummaryDB);
              userEntryAvailable = userEntryDao.insertNewInstance(entry);
            }
          } else {
            entry = new UserEntry(user, exercise, userString, new Date());
            entry.setResultMessage(feedbackSummaryDB);
            userEntryAvailable = userEntryDao.insertNewInstance(entry);
          }

          if (isRated() && !debug) {
            // show feedback message to user
            Severity sev = FacesMessage.SEVERITY_INFO;
            FacesMessage message1 = new FacesMessage(sev, "Server-Meldung:", msg);
            FacesContext.getCurrentInstance().addMessage(null, message1);
            // FacesMessage message2 = new FacesMessage("Gespeicherte Query:",
            // StringTools.trimToLengthIndicator(userString, 100));
            // FacesContext.getCurrentInstance().addMessage(null, message2);
          }

          if (!showFeedback) {
            querySaved = true;
          }

          if (result == null && userEntryAvailable) {
            result = new UserResult(entry, reachedCredits, new Date());
            result.setSolutionQuery(usedQuery);
            result.setComment(feedbackSummaryDB);
            userResultDao.insertNewInstance(result);
          }
        }

        if (feedbackVisible) {
          refLinks = comparator.getRefLinks();
        }

        if (!syntaxError) {
          SqlResult sol_result = userQuery.getResult();
          String[][] data = sol_result.getData();
          userQueryColumns = new ArrayList<String>();
          userQueryValues = new ArrayList<TableEntry>();

          ResultSetMetaData metaData = sol_result.getResultMetaData();
          if (metaData != null) {
            if (!debug) {
              for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnLabel(i);

                if (user != null && name != null && name.contains(user.getId() + "_")) {
                  name = name.replaceAll(user.getId() + "_", "");
                }

                if (name != null && !name.trim().isEmpty()) {
                  userQueryColumns.add(name);
                }
              }
            }
          }
          if (data != null) {
            for (int i = 0; i < data.length; i++) {
              TableEntry en = new TableEntry(userQueryColumns);
              for (int z = 0; z < data[i].length; z++) {
                if (data[i][z] != null) {
                  en.addValue(data[i][z], z);
                } else {
                  en.addValue("NULL", z);
                }
              }
              userQueryValues.add(en);
            }
          }
          filteredUserQueryValues = userQueryValues;
          createUserQueryColumns();

          if (showFeedback) {
            this.userResultVisible = true;
          }
        } else {
          if (showFeedback) {
            this.feedbackVisible = true;
            this.userResultVisible = false;
          }
        }

        // ------------------------------------------------ //
        // --
        // ------------------------------------------------ //

        if (userEntrySuccess) {
          feedbackList.clear();
          UserFeedback feedback = new UserFeedback(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT"),
              Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT.SUC"), user);
          feedback.setSuccess(true);
          feedback.setMainError(true);
          feedbackList.add(feedback);
        }

      }

    } catch (SQLException e) {
      LOGGER.error("PARSER-SQL-ERROR", e);
    } catch (Exception e) {
      LOGGER.error("PARSER-ERROR", e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *
   *
   * @param table
   * @return
   */
  public List<TableEntry> getEntries(String table) {
    return tableValues.get(table);
  }

  /**
   *
   *
   * @param table
   * @return
   */
  public List<String> getColumns(String table) {
    return tableColumns.get(table);
  }

  /**
   *
   *
   * @return
   */
  public boolean isRated() {
    if (exerciseGroup != null) {
      return exerciseGroup.getIsRated();
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public String getResultStrings() {
    String solString = "";
    for (SolutionQuery sol : solutions) {
      solString += sol.getQuery() + "<br/><br/>";
    }
    return solString;
  }

  /**
   *
   *
   * @return
   */
  public boolean isSolutionCorrect() {
    for (UserFeedback fb : feedbackList) {
      if (fb.isMainError()) {
        return fb.isCorrect();
      }
    }
    return false;
  }

  /**
   *
   *
   * @param table
   * @return
   */
  public String getTableContent(String table) {
    return tableContent.get(table);
  }

  /**
   *
   *
   * @return
   */
  public String getFistSolution() {
    return solutions.get(0).getQuery();
  }

  /**
   * @return the exercise
   */
  public Exercise getExercise() {
    return exercise;
  }

  /**
   * @param exercise
   *          the exercise to set
   */
  public void setExercise(Exercise exercise) {
    this.exercise = exercise;
  }

  /**
   * @return the solutions
   */
  public List<SolutionQuery> getSolutions() {
    return solutions;
  }

  /**
   * @param solutions
   *          the solutions to set
   */
  public void setSolutions(List<SolutionQuery> solutions) {
    this.solutions = solutions;
  }

  /**
   * @return the usedSolutionIndex
   */
  public int getUsedSolutionIndex() {
    return usedSolutionIndex;
  }

  /**
   * @param usedSolutionIndex
   *          the usedSolutionIndex to set
   */
  public void setUsedSolutionIndex(int usedSolutionIndex) {
    this.usedSolutionIndex = usedSolutionIndex;
  }

  /**
   * @return the userString
   */
  public String getUserString() {
    return userString;
  }

  /**
   * @param userString
   *          the userString to set
   */
  public void setUserString(String userString) {
    if (connectionPool != null) {
      userString = userString.trim();

      resetUserFilter();
      resetSolutionFilter();

      if (userString != null) {
        this.userString = userString;
        // TODO: quick fix
        // if (userString.toLowerCase().contains("insert into") &&
        // userString.toLowerCase().contains("value ")) {
        // this.userString = new
        // StringBuffer(userString).insert(userString.toLowerCase().indexOf("value")
        // + 5, "s").toString();
        // }
        this.userQuery = new SqlQuery(this.userString);
        compareResults();
      }

    }
  }

  /**
   * @return the feedback
   */
  public String getFeedback() {
    return this.feedback;
  }

  /**
   * @param feedback
   *          the feedback to set
   */
  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  /**
   * @return the solutionTable
   */
  public String getSolutionTable() {
    return solutionTable;
  }

  /**
   * @param solutionTable
   *          the solutionTable to set
   */
  public void setSolutionTable(String solutionTable) {
    this.solutionTable = solutionTable;
  }

  /**
   * @return the userTable
   */
  public String getUserTable() {
    return userTable;
  }

  /**
   * @param userTable
   *          the userTable to set
   */
  public void setUserTable(String userTable) {
    this.userTable = userTable;
  }

  /**
   * @return the diagramImage
   */
  public String getDiagramImage() {
    return diagramImage;
  }

  /**
   * @param diagramImage
   *          the diagramImage to set
   */
  public void setDiagramImage(String diagramImage) {
    this.diagramImage = diagramImage;
  }

  /**
   * @return the resultVisible
   */
  public boolean isResultVisible() {
    return resultVisible;
  }

  /**
   * @param resultVisible
   *          the resultVisible to set
   */
  public void setResultVisible(boolean resultVisible) {
    this.resultVisible = resultVisible;
  }

  /**
   * @return the userResultVisible
   */
  public boolean isUserResultVisible() {
    return userResultVisible;
  }

  /**
   * @param userResultVisible
   *          the userResultVisible to set
   */
  public void setUserResultVisible(boolean userResultVisible) {
    this.userResultVisible = userResultVisible;
  }

  /**
   * @return the feedbackVisible
   */
  public boolean isFeedbackVisible() {
    return feedbackVisible;
  }

  /**
   * @param feedbackVisible
   *          the feedbackVisible to set
   */
  public void setFeedbackVisible(boolean feedbackVisible) {
    this.feedbackVisible = feedbackVisible;
  }

  /**
   * @return the availableTables
   */
  public ArrayList<String> getAvailableTables() {
    return availableTables;
  }

  /**
   * @param availableTables
   *          the availableTables to set
   */
  public void setAvailableTables(ArrayList<String> availableTables) {
    this.availableTables = availableTables;
  }

  /**
   * @return the tableColumns
   */
  public HashMap<String, List<String>> getTableColumns() {
    return tableColumns;
  }

  /**
   * @param tableColumns
   *          the tableColumns to set
   */
  public void setTableColumns(HashMap<String, List<String>> tableColumns) {
    this.tableColumns = tableColumns;
  }

  /**
   * @return the columnValues
   */
  public HashMap<String, List<TableEntry>> getTableValues() {
    return tableValues;
  }

  /**
   * @param columnValues
   *          the columnValues to set
   */
  public void setTableValues(HashMap<String, List<TableEntry>> columnValues) {
    this.tableValues = columnValues;
  }

  /**
   * @return the currentTableFilter
   */
  public String[] getCurrentTableFilter() {
    return currentTableFilter;
  }

  /**
   * @param currentTableFilter
   *          the currentTableFilter to set
   */
  public void setCurrentTableFilter(String[] currentTableFilter) {
    this.currentTableFilter = currentTableFilter;
  }

  /**
   * @return the userQueryFilter
   */
  public String getUserQueryFilter() {
    return userQueryFilter;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderUserQueryFilter() {
    if (userQueryValues != null && userQueryValues.size() > 5) {
      return true;
    }
    return false;
  }

  /**
   * @param userQueryFilter
   *          the userQueryFilter to set
   */
  public void setUserQueryFilter(String userQueryFilter) {
    this.userQueryFilter = userQueryFilter;
    filteredUserQueryValues = setQueryFilter(userQueryFilter, userQueryColumns, userQueryValues);
  }

  /**
   *
   *
   * @return
   */
  public boolean renderSolutionQueryFilter() {
    if (solutionQueryValues != null && solutionQueryValues.size() > 5) {
      return true;
    }
    return false;
  }

  /**
   * @param solutionQueryFilter
   *          the solutionQueryFilter to set
   */
  public void setSolutionQueryFilter(String solutionQueryFilter) {
    this.solutionQueryFilter = solutionQueryFilter;
    filteredSolutionQueryValues = setQueryFilter(solutionQueryFilter, solutionQueryColumns,
                                  solutionQueryValues);
  }

  /**
   * @param userQueryFilter
   *          the userQueryFilter to set
   */
  public ArrayList<TableEntry> setQueryFilter(String userFilter, List<String> columns,
      List<TableEntry> values) {
    try {

      LinkedList<Integer> filtered = new LinkedList<Integer>();
      ArrayList<TableEntry> filteredValues = new ArrayList<TableEntry>();

      if (userFilter != null && !userFilter.trim().isEmpty()) {
        userFilter = userFilter.trim().toLowerCase();
        String column = null;

        if (userFilter.contains("=")) {
          String[] parts = userFilter.split("=");
          column = parts[0];
          userFilter = parts[1];
        }

        for (int z = 0; z < values.size(); z++) {
          for (int i = 0; i < columns.size(); i++) {
            if (column == null || columns.get(i).toLowerCase().equals(column)) {
              String value = values.get(z).getValue(i);
              if (value != null && value.toLowerCase().contains(userFilter)) {
                if (!filtered.contains(z)) {
                  filtered.add(z);
                }
              }
            }
          }
        }

        filteredValues = new ArrayList<TableEntry>();

        for (Integer filter : filtered) {
          filteredValues.add(values.get(filter));
        }
        return filteredValues;
      }
    } catch (Exception e) {
      filteredUserQueryValues = null;
    }

    return null;
  }

  /**
   *
   *
   */
  public void resetUserFilter() {
    userQueryFilter = null;
    filteredUserQueryValues = null;
  }

  /**
   *
   *
   */
  public void resetSolutionFilter() {
    solutionQueryFilter = null;
    filteredSolutionQueryValues = null;
  }

  /**
   * @return the solutionColumns
   */
  public List<String> getSolutionQueryColumns() {
    return solutionQueryColumns;
  }

  /**
   * @param solutionColumns
   *          the solutionColumns to set
   */
  public void setSolutionQueryColumns(List<String> solutionColumns) {
    this.solutionQueryColumns = solutionColumns;
  }

  /**
   * @return the solutionValues
   */
  public List<TableEntry> getSolutionQueryValues() {
    return solutionQueryValues;
  }

  /**
   * @param solutionValues
   *          the solutionValues to set
   */
  public void setSolutionQueryValues(List<TableEntry> solutionValues) {
    this.solutionQueryValues = solutionValues;
  }

  /**
   * @return the filteredSolutionQueryValues
   */
  public List<TableEntry> getFilteredSolutionQueryValues() {
    return filteredSolutionQueryValues;
  }

  /**
   * @param filteredSolutionQueryValues
   *          the filteredSolutionQueryValues to set
   */
  public void setFilteredSolutionQueryValues(List<TableEntry> filteredSolutionQueryValues) {
    this.filteredSolutionQueryValues = filteredSolutionQueryValues;
  }

  /**
   * @return the solutionQueryMColumns
   */
  public List<ColumnModel> getSolutionQueryMColumns() {
    return solutionQueryMColumns;
  }

  /**
   * @param solutionQueryMColumns
   *          the solutionQueryMColumns to set
   */
  public void setSolutionQueryMColumns(List<ColumnModel> solutionQueryMColumns) {
    this.solutionQueryMColumns = solutionQueryMColumns;
  }

  /**
   * @return the solutionQueryFilter
   */
  public String getSolutionQueryFilter() {
    return solutionQueryFilter;
  }

  /**
   * @return the userQueryColumns
   */
  public List<String> getUserQueryColumns() {
    return userQueryColumns;
  }

  /**
   * @return the userQueryValues
   */
  public List<TableEntry> getUserQueryValues() {
    return userQueryValues;
  }

  /**
   * @return the filteredUserQueryValues
   */
  public List<TableEntry> getFilteredUserQueryValues() {
    return filteredUserQueryValues;
  }

  /**
   * @param filteredUserQueryValues
   *          the filteredUserQueryValues to set
   */
  public void setFilteredUserQueryValues(List<TableEntry> filteredUserQueryValues) {
    this.filteredUserQueryValues = filteredUserQueryValues;
  }

  /**
   * @return the columns
   */
  public List<ColumnModel> getUserQueryMColumns() {
    return userQueryMColumns;
  }

  /**
   * @param columns
   *          the columns to set
   */
  public void setUserQueryMColumns(List<ColumnModel> columns) {
    this.userQueryMColumns = columns;
  }

  /**
   * @return the feedbackList
   */
  public ArrayList<UserFeedback> getFeedbackList() {
    return feedbackList;
  }

  /**
   * @param feedbackList
   *          the feedbackList to set
   */
  public void setFeedbackList(ArrayList<UserFeedback> feedbackList) {
    this.feedbackList = feedbackList;
  }

  /**
   * @return the mainRefLink
   */
  public String getMainRefLink() {
    return mainRefLink;
  }

  /**
   * @param mainRefLink
   *          the mainRefLink to set
   */
  public void setMainRefLink(String mainRefLink) {
    this.mainRefLink = mainRefLink;
  }

  /**
   * @return the refLinks
   */
  public LinkedList<RefLink> getRefLinks() {
    return refLinks;
  }

  /**
   * @return the userHasRights
   */
  public boolean isUserHasRights() {
    return userHasRights;
  }

  /**
   * @param userHasRights
   *          the userHasRights to set
   */
  public void setUserHasRights(boolean userHasRights) {
    this.userHasRights = userHasRights;
  }

  /**
   * @return the debug
   */
  public boolean isDebug() {
    return debug;
  }

  /**
   * @param debug
   *          the debug to set
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * @return the querySaved
   */
  public boolean isQuerySaved() {
    return querySaved;
  }

  /**
   * @param querySaved
   *          the querySaved to set
   */
  public void setQuerySaved(boolean querySaved) {
    this.querySaved = querySaved;
  }

  /**
   * @param refLinks
   *          the refLinks to set
   */
  public void setRefLinks(LinkedList<RefLink> refLinks) {
    this.refLinks = refLinks;
  }
}
