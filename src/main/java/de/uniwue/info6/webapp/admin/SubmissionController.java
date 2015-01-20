package de.uniwue.info6.webapp.admin;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SubmissionController.java
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

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
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserResultDao;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.parser.errors.Error;
import de.uniwue.info6.webapp.lists.UserFeedback;
import de.uniwue.info6.webapp.session.SessionBean;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 * @author Christian
 *
 */
@ManagedBean(name = "sub")
@ViewScoped
public class SubmissionController implements Serializable {

  private static final long serialVersionUID = 3024228920485372328L;
  private static final Log LOGGER = LogFactory.getLog(SubmissionController.class);
  private static final String EMPTY = "[" + Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.EMPTY_FIELD") + "]";
  private static final String EMPTY_SC = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.NO_SCENARIO");
  private static final String EMPTY_GR = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.NO_GROUP");
  private static final String EMPTY_EX = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.NO_EX");
  private static final String EMPTY_US = Cfg.inst().getProp(DEF_LANGUAGE, "RIGHTS.USER_NOT_FOUND");

  private UserEntryDao userEntryDao;

  private TreeNode root;
  private SubmissionRow selectedNode;
  private List<SubmissionRow> filteredRows;
  private User user;
  private SessionObject ac;
  private ExerciseDao exerciseDao;
  private ScenarioDao scenarioDao;
  private ExerciseGroupDao exgroupDao;
  private UserResultDao userResultDao;
  private UserDao userDao;
  private ConnectionManager connectionPool;
  private SubmissionRow delRow;
  private SubmissionRow recalcRow;
  private UserRights rights;
  private int countEntries;
  private String errorText;

  private String userID, scenarioID, groupID, exID;
  private Integer scenarioIDInt, groupIDInt, exIDInt;

  private static final String EXERCISE_TAG = "EX-";

  /**
   * {@inheritDoc}
   *
   * @see Object#SubmissionController()
   */
  public SubmissionController() {

  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    connectionPool = ConnectionManager.instance();

    scenarioDao = new ScenarioDao();
    exgroupDao = new ExerciseGroupDao();
    exerciseDao = new ExerciseDao();
    userEntryDao = new UserEntryDao();
    userResultDao = new UserResultDao();
    userDao = new UserDao();

    ac = SessionObject.pull();
    user = ac.getUser();

    rights = new UserRights().initialize();

    initTree();
  }

  /**
   *
   *
   */
  public void reloadTree() {
    initTree();

    String msg = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.FILTER5");
    Severity sev = FacesMessage.SEVERITY_ERROR;

    if (countEntries > 0) {
      if (countEntries == 1) {
        msg = countEntries + " " + Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.FILTER3");
      } else {
        msg = countEntries + " " + Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.FILTER2");
      }
      sev = FacesMessage.SEVERITY_INFO;
    }

    FacesMessage message = new FacesMessage(sev, Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.FILTER1"), msg);
    FacesContext.getCurrentInstance().addMessage(null, message);
  }

  /**
   *
   *
   */
  private void initTree() {
    try {
      long maxTime = 45000;
      boolean filter = scenarioIDInt != null || groupIDInt != null || exIDInt != null || userID != null;

      root = new DefaultTreeNode("Root", null);
      List<Scenario> scenarios = scenarioDao.findAll();

      long starttime = 0;
      countEntries = 0;
      errorText = "";

      starttime = System.currentTimeMillis();

      if (filter) {
        TreeNode node0 = new DefaultTreeNode(new SubmissionRow(Cfg.inst().getProp(DEF_LANGUAGE, "SCENARIO")), root);
        node0.setExpanded(true);

        for (Scenario scenario : scenarios) {
          boolean scenarioFilter = (scenarioIDInt == null || scenario.getId().equals(scenarioIDInt));
          if (rights.hasRatingRight(user, scenario) && scenarioFilter) {

            TreeNode node1 = new DefaultTreeNode(new SubmissionRow(scenario), node0);
            if (groupIDInt != null || exIDInt != null || userID != null) {
              node1.setExpanded(true);
            }
            ExerciseGroup ratedExample = new ExerciseGroup();
            ratedExample.setScenario(scenario);
            ratedExample.setIsRated(true);
            List<ExerciseGroup> groups = exgroupDao.findByExample(ratedExample);

            for (ExerciseGroup group : groups) {
              boolean groupFilter = (groupIDInt == null || group.getId().equals(groupIDInt));

              if (groupFilter) {
                List<Exercise> exercises = exerciseDao.findByExGroup(group);

                if (exercises != null) {

                  TreeNode node2 = new DefaultTreeNode(new SubmissionRow(group), node1);
                  if (exIDInt != null || userID != null) {
                    node2.setExpanded(true);
                  }

                  for (Exercise ex : exercises) {
                    boolean exerciseFilter = (exIDInt == null || ex.getId().equals(exIDInt));
                    if (exerciseFilter) {
                      List<UserEntry> userEntries = null;

                      if (ex != null) {
                        if (userID == null) {
                          userEntries = userEntryDao.getLastEntriesForExercise(ex);
                        } else {
                          userEntries = new ArrayList<UserEntry>();
                          User tempUser = userDao.getById(userID);
                          if (tempUser != null) {
                            UserEntry temp = userEntryDao.getLastEntry(ex, tempUser);
                            if (temp != null) {
                              userEntries.add(temp);
                            }
                          }
                        }
                      }

                      if (System.currentTimeMillis() - starttime > maxTime) {
                        errorText = Cfg.inst().getProp(DEF_LANGUAGE, "ASSERTION.PERF_ERROR");
                        break;
                      }

                      if (userEntries != null && !userEntries.isEmpty()) {
                        SubmissionRow tmpRow = new SubmissionRow(ex, scenario);
                        double sumCredits = 0;
                        int countResults = 0;
                        TreeNode node3 = new DefaultTreeNode(tmpRow, node2);

                        if (userID != null) {
                          node3.setExpanded(true);
                        }

                        for (UserEntry userEntry : userEntries) {
                          countEntries++;
                          UserResult userResult = userResultDao
                                                  .getLastUserResultFromEntry(userEntry);

                          if (userResult != null) {
                            new DefaultTreeNode(
                              new SubmissionRow(userEntry, userResult, ex), node3);
                            countResults++;
                            sumCredits += userResult.getCredits();
                          }
                        }
                        tmpRow.setAvgCredits((double) sumCredits / (double) countResults, ex);
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // ---------------------------------------------------------
        // remove empty nodes
        // ---------------------------------------------------------
        List<TreeNode> node1toRemove = new ArrayList<TreeNode>();
        List<TreeNode> node2toRemove = new ArrayList<TreeNode>();
        List<TreeNode> node3toRemove = new ArrayList<TreeNode>();

        for (TreeNode node1 : node0.getChildren()) {
          for (TreeNode node2 : node1.getChildren()) {
            for (TreeNode node3 : node2.getChildren()) {
              if (node3.getChildCount() == 0)
                node3toRemove.add(node3);
            }
            for (TreeNode node : node3toRemove)
              node2.getChildren().remove(node);
            if (node2.getChildCount() == 0)
              node2toRemove.add(node2);
          }
          for (TreeNode node : node2toRemove)
            node1.getChildren().remove(node);
          if (node1.getChildCount() == 0)
            node1toRemove.add(node1);
        }
        for (TreeNode node : node1toRemove)
          node0.getChildren().remove(node);

        if (node0.getChildCount() == 0) {
          root.getChildren().remove(node0);
          node0 = new DefaultTreeNode(new SubmissionRow("Keine Einträge"), root);
        }
        // ---------------------------------------------------------
      }

    } catch (Exception e) {
      LOGGER.error("error initializing submission-treebean", e);
    }
  }

  /**
   *
   *
   * @param root
   */
  public void setRoot(TreeNode root) {
    this.root = root;
  }

  /**
   *
   *
   * @param node
   * @param collapse
   * @return
   */
  public String getIcon(String node, boolean collapse) {
    if (!node.startsWith("[" + EXERCISE_TAG)) {
      if (collapse) {
        return "ui-icon-folder-collapsed";
      } else {
        return "ui-icon-folder-open";
      }
    }
    return "ui-icon-document";
  }

  /**
   *
   *
   * @return
   */
  public TreeNode getRoot() {
    return root;
  }

  /**
   *
   *
   * @return
   */
  public SubmissionRow getSelectedNode() {
    return selectedNode;
  }

  /**
   *
   *
   * @param selectedNode
   */
  public void setSelectedNode(SubmissionRow selectedNode) {
    this.selectedNode = selectedNode;
  }

  /**
   *
   *
   * @param currentRow
   * @return
   */
  public String getStyle(SubmissionRow currentRow) {
    if (currentRow != null && currentRow.getIsEntry()) {
      String comment = currentRow.getUserResult().getComment();
      if (currentRow.getUserResult().getCredits() > 0 && (comment == null || comment.isEmpty())) {
        return "color:green;";
      } else if (currentRow.getUserResult().getCredits() > 0
                 && comment != null && !comment.isEmpty()) {
        return "color:orange;";
      } else {
        return "color:red;";
      }
    }
    return "";
  }

  /**
   *
   *
   * @param currentRow
   * @return
   */
  public String getStyleClass(SubmissionRow currentRow) {
    if (currentRow != null && currentRow.getIsEntry()) {
      String comment = currentRow.getUserResult().getComment();
      if (currentRow.getUserResult().getCredits() > 0 && comment != null && !comment.isEmpty()) {
        return "unknown_solution";
      }
    }
    return "";

  }

  /**
   *
   *
   * @return
   */
  public List<SubmissionRow> getFilteredRows() {
    return filteredRows;
  }

  /**
   *
   *
   * @param filteredRows
   */
  public void setFilteredRows(List<SubmissionRow> filteredRows) {
    this.filteredRows = filteredRows;
  }

  /**
   *
   *
   * @return
   */
  public UserEntryDao getUserEntryDao() {
    return userEntryDao;
  }

  /**
   *
   *
   * @param userEntryDao
   */
  public void setUserEntryDao(UserEntryDao userEntryDao) {
    this.userEntryDao = userEntryDao;
  }

  /**
   *
   *
   * @param varStr
   * @return
   */
  public String fillProperyString(String varStr) {

    String[] empty = null;
    return fillPropertyString(varStr, empty);

  }

  /**
   *
   *
   * @param varStr
   * @param data
   * @return
   */
  public String fillPropertyString(String varStr, String[] data) {
    String tmp = Cfg.inst().getProp(DEF_LANGUAGE, varStr);
    if (data != null) {
      for (String tmpStr : data) {
        tmp = tmp.replaceFirst("%", tmpStr);
      }
    }

    return tmp;

  }

  /**
   *
   *
   * @param exGroup
   * @return
   */
  public StreamedContent getExport(ExerciseGroup exGroup) {
    try {
      String delim = ";";
      String tmpTxt = "ex_id" + delim + "user_id" + delim + "points" + delim + "max_points" + "\n";
      List<Exercise> exercises = exerciseDao.findByExGroup(exGroup);
      for (Exercise ex : exercises) {

        List<UserEntry> userEntries = userEntryDao.getLastUserEntryForAllUsers(ex);

        if (userEntries != null) {

          for (UserEntry userEntry : userEntries) {

            UserResult userResult = userResultDao.getLastUserResultFromEntry(userEntry);

            tmpTxt += ex.getId() + delim + userEntry.getUser().getId() + delim + userResult.getCredits()
                      + delim + ex.getCredits() + "\n";

          }
        }
      }

      InputStream stream = new ByteArrayInputStream(tmpTxt.getBytes());
      StreamedContent file = new DefaultStreamedContent(stream, "text/csv", "results_"
          + StringTools.normalize(exGroup.getScenario().getName() + "-" + exGroup.getId()) + ".csv");
      return file;
    } catch (Exception e) {
      LOGGER.error("CSV-export for results failed", e);
    }
    return null;
  }

  /**
   *
   *
   */
  @SuppressWarnings("unused")
  public void delete() {

    UserEntry userEntry = delRow.getUserEntry();

    String message = null;
    String details = null;
    Severity sev = null;
    FacesMessage msg = null;

    boolean deleted = false;

    if (userEntry != null) {

      deleted = userEntryDao.deleteInstance(userEntry);
    }

    if (deleted) {
      sev = FacesMessage.SEVERITY_INFO;
      message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.SUCCESS");
    } else {
      sev = FacesMessage.SEVERITY_ERROR;
      message = Cfg.inst().getProp(DEF_LANGUAGE, "SAVED.FAIL");
    }
    initTree();
  }

  /**
   *
   *
   */
  public void forceExerciseAssertion() {
    SubmissionRow row = recalcRow;

    if (row == null || !row.getIsExercise()) {
      LOGGER.error("problem getting row for assertion");
      return;
    }
    Exercise ex = row.getExercise();
    Scenario sc = row.getScenario();

    if (ex != null && sc != null) {
      List<UserEntry> userEntries = userEntryDao.getLastUserEntryForAllUsers(ex);

      if (userEntries != null) {

        List<SolutionQuery> solutionsOrig = exerciseDao.getSolutions(ex);
        LinkedList<SqlQuery> solutions = new LinkedList<SqlQuery>();

        for (SolutionQuery tmp : solutionsOrig) {
          solutions.add(new SqlQuery(tmp.getQuery()));
        }
        Connection connection = null;

        try {
          connection = connectionPool.getConnection(sc);
          for (UserEntry userEntry : userEntries) {
            UserResult userResult = userResultDao.getLastUserResultFromEntry(userEntry);

            if (userResult != null) {

              SqlQuery userQuery = new SqlQuery(userEntry.getUserQuery());


              connectionPool.resetTables(sc, user);
              SqlExecuter executer = new SqlExecuter(connection, user, sc);
              SqlQueryComparator comparator = new SqlQueryComparator(userQuery, solutions, executer);

              // get user feedback
              LinkedList<Error> errors = comparator.compare();
              ArrayList<UserFeedback> feedbackList = null;

              boolean userEntrySuccess = false;
              String feedbackSummary = "";

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

              for (UserFeedback fb : feedbackList) {
                if (fb.isMainError()) {
                  userEntrySuccess = fb.isCorrect();
                } else {
                  feedbackSummary += fb.getTitle() + ": " + fb.getFeedback() + "<br/>";
                }
              }

              if (userEntrySuccess) {
                userResult.setCredits(ex.getCredits());
              } else {
                userResult.setCredits((byte) 0);
              }
              userResult.setUser(null);
              userResult.setLastModified(new Date());
              userResult.setComment(feedbackSummary);

              int index = solutions.indexOf(comparator.getSolutionQuery());
              if (index < solutions.size()) {
                userResult.setSolutionQuery(solutionsOrig.get(index));
              }

              userResultDao.updateInstance(userResult);
            }
          }
        } catch (Exception e) {
          LOGGER.error("Error forcing reset assertion", e);
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
    reloadTree();
  }

  public StreamedContent getExport2(ExerciseGroup exGroup) {
    return null;
  }

  public SubmissionRow getDelRow() {
    return delRow;
  }

  public void setDelRow(SubmissionRow delRow) {
    this.delRow = delRow;
  }

  public SubmissionRow getRecalcRow() {
    return recalcRow;
  }

  public void setRecalcRow(SubmissionRow recalcRow) {
    this.recalcRow = recalcRow;
  }

  /**
   * @param userID
   *            the userID to set
   */
  public void setUserID(String userID) {
    if (userID != null && !userID.trim().isEmpty() && !userID.equals(EMPTY) && !userID.equals(EMPTY_US)) {
      this.userID = userID;
    } else {
      this.userID = null;
    }
  }

  /**
   * @param scenarioID
   *            the scenarioID to set
   */
  public void setScenarioID(String scenarioID) {
    if (scenarioID != null && !scenarioID.trim().isEmpty() && !scenarioID.trim().equals(EMPTY)
        && !scenarioID.trim().equals(EMPTY_SC)) {
      this.scenarioID = scenarioID;
      this.scenarioIDInt = Integer.parseInt(StringTools.extractIDFromAutoComplete(scenarioID));
    } else {
      this.scenarioID = null;
      this.scenarioIDInt = null;
    }
  }

  /**
   * @param groupID
   *            the groupID to set
   */
  public void setGroupID(String groupID) {
    if (groupID != null && !groupID.trim().isEmpty() && !groupID.trim().equals(EMPTY)
        && !groupID.trim().equals(EMPTY_GR)) {
      this.groupID = groupID;
      this.groupIDInt = Integer.parseInt(StringTools.extractIDFromAutoComplete(groupID));
    } else {
      this.groupID = null;
      this.groupIDInt = null;
    }
  }

  /**
   * @param exID
   *            the exID to set
   */
  public void setExID(String exID) {
    if (exID != null && !exID.trim().isEmpty() && !exID.trim().equals(EMPTY) && !exID.trim().equals(EMPTY_EX)) {
      this.exID = exID;
      this.exIDInt = Integer.parseInt(StringTools.extractIDFromAutoComplete(exID));
    } else {
      this.exID = null;
      this.exIDInt = null;
    }
  }

  /**
   *
   *
   * @param event
   */
  public void handleScenarioSelect(SelectEvent event) {
  }

  /**
   *
   *
   * @param event
   */
  public void handleGroupSelect(SelectEvent event) {
  }

  /**
   *
   *
   * @param event
   */
  public void handleExerciseSelect(SelectEvent event) {
  }

  /**
   *
   *
   * @param event
   */
  public void handleUserSelect(SelectEvent event) {
  }

  /**
   * @return the exID
   */
  public String getExID() {
    return exID;
  }

  /**
   * @return the groupID
   */
  public String getGroupID() {
    return groupID;
  }

  /**
   * @return the errorText
   */
  public String getErrorText() {
    return errorText;
  }

  /**
   * @param errorText
   *            the errorText to set
   */
  public void setErrorText(String errorText) {
    this.errorText = errorText;
  }

  /**
   * @return the userID
   */
  public String getUserID() {
    return userID;
  }

  /**
   * @return the scenarioID
   */
  public String getScenarioID() {
    return scenarioID;
  }
}
