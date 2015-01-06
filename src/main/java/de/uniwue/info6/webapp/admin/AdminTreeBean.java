package de.uniwue.info6.webapp.admin;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AdminTreeBean.java
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

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import de.uniwue.info6.database.jaxb.ScenarioExporter;
import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.misc.FileTransfer;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.webapp.session.SessionCollector;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "t")
@ViewScoped
public class AdminTreeBean implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final Log LOGGER = LogFactory.getLog(AdminTreeBean.class);

  private TreeNode root, selectedNode;
  private ExerciseNode exerciseNode;
  private String currentTag;
  private int currentID;
  private ExerciseGroupDao exgroupDao;
  private ExerciseDao exerciseDao;
  private SolutionQueryDao solutionDao;
  private UserDao userDao;
  private UserEntryDao userEntryDao;
  private List<Scenario> scenarios;
  private ScenarioDao scenarioDao;
  private UserRights rights;
  private User user;
  private FileTransfer transferController;
  private ConnectionManager connectionPool;

  private Exercise copiedExercise;
  private ExerciseGroup copiedExerciseGroup;
  private TreeNode copiedNode;
  private FileTransfer transfer;
  private ScenarioExporter exporter;

  private boolean moveFlag;

  private StreamedContent exportStream;

  public AdminTreeBean() {
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    scenarioDao = new ScenarioDao();
    exgroupDao = new ExerciseGroupDao();
    exerciseDao = new ExerciseDao();
    solutionDao = new SolutionQueryDao();
    userDao = new UserDao();
    userEntryDao = new UserEntryDao();
    rights = new UserRights().initialize();
    transferController = new FileTransfer();
    connectionPool = ConnectionManager.instance();

    SessionObject ac = new SessionCollector().getSessionObject();
    user = ac.getUser();
    updateTree(null);
  }

  /**
   *
   *
   */
  public void updateTree(ActionEvent event) {
    root = new DefaultTreeNode("Root", null);
    TreeNode node0 = new DefaultTreeNode(new ExerciseNode(), root);
    scenarios = scenarioDao.findAll();
    node0.setExpanded(true);

    try {
      if (scenarios != null) {
        for (Scenario scenario : scenarios) {
          if (user != null) {
            if (rights.hasEditingRight(user, scenario)) {
              TreeNode node1 = new DefaultTreeNode(new ExerciseNode(scenario), node0);
              // node1.setExpanded(true);
              List<ExerciseGroup> groups = exgroupDao.findByScenario(scenario);
              for (ExerciseGroup group : groups) {
                TreeNode node2 = new DefaultTreeNode(new ExerciseNode(group), node1);
                List<Exercise> exercises = exerciseDao.findByExGroup(group);
                for (Exercise ex : exercises) {
                  new DefaultTreeNode(new ExerciseNode(ex), node2);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("FAILED BUILDING TREEBEAN", e);
    }
  }

  /**
   * manages icons. a folder icon is used for scenarios and exercise-groups a document icon is used for exercises
   *
   * @param node
   * @param collapse
   * @return
   */
  public String getIcon(ExerciseNode node, boolean collapse) {
    if (!node.isExercise()) {
      if (collapse) {
        return "ui-icon-folder-collapsed";
      } else {
        return "ui-icon-folder-open";
      }
    }
    return "ui-icon-arrowthick-1-e";
  }

  /**
   *
   *
   * @return
   */
  public boolean pasteDisabled() {
    if (exerciseNode != null) {
      if (exerciseNode.isScenario() && copiedExerciseGroup != null) {
        return false;
      } else if (exerciseNode.isExerciseGroup() && copiedExercise != null) {
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
  public TreeNode getRoot() {
    return root;
  }

  /**
   *
   *
   * @return
   */
  public TreeNode getSelectedNode() {
    return selectedNode;
  }

  /**
   *
   *
   * @param selectedNode
   */
  public void setSelectedNode(TreeNode selectedNode) {
    this.selectedNode = selectedNode;
  }

  /**
   *
   *
   * @param event
   */
  public void onNodeCollapse(NodeCollapseEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void onNodeExpand(NodeExpandEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void setSelection(NodeSelectEvent event) {
    exerciseNode = (ExerciseNode) event.getTreeNode().getData();
  }

  /**
   *
   *
   * @return
   */
  public String getDate() {
    return new Date().toString();
  }

  /**
   * @return the exerciseNode
   */
  public ExerciseNode getSelection() {
    return exerciseNode;
  }

  /**
   * @return the currentTag
   */
  public String getCurrentTag() {
    return currentTag;
  }

  /**
   * @param currentTag
   *            the currentTag to set
   */
  public void setCurrentTag(String currentTag) {
    this.currentTag = currentTag;
  }

  /**
   * @return the currentID
   */
  public int getCurrentID() {
    return currentID;
  }

  /**
   *
   *
   * @return
   */
  public String getNodeName() {
    if (exerciseNode != null) {
      if (exerciseNode.isScenario()) {
        return Cfg.inst().getProp(DEF_LANGUAGE, "SCENARIO") + " ";
      } else if (exerciseNode.isExerciseGroup()) {
        return Cfg.inst().getProp(DEF_LANGUAGE, "EXERCISE_GROUP") + " ";
      } else if (exerciseNode.isExercise()) {
        return Cfg.inst().getProp(DEF_LANGUAGE, "EXERCISE") + " ";
      }
    }
    return "---" + " ";
  }

  /**
   *
   *
   * @return
   */
  public String getChildName() {
    if (exerciseNode != null) {
      if (exerciseNode.isRootNode()) {
        return Cfg.inst().getProp(DEF_LANGUAGE, "SCENARIO") + " ";
      } else if (exerciseNode.isScenario()) {
        return Cfg.inst().getProp(DEF_LANGUAGE, "EXERCISE_GROUP") + " ";
      } else if (exerciseNode.isExerciseGroup()) {
        return Cfg.inst().getProp(DEF_LANGUAGE, "EXERCISE") + " ";
      }
    }
    return "";
  }

  /**
   * @param currentID
   *            the currentID to set
   */
  public void setCurrentID(int currentID) {
    this.currentID = currentID;
  }

  /**
   * @return the exportStream
   */
  public StreamedContent getExportStream() {
    return exportStream;
  }

  /**
   * @param exportStream
   *            the exportStream to set
   */
  public void setExportStream(StreamedContent exportStream) {
    this.exportStream = exportStream;
  }

  /**
   *
   *
   */
  public void displaySelectedSingle() {
    if (selectedNode != null) {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData()
                                              .toString());

      FacesContext.getCurrentInstance().addMessage(null, message);
    }
  }

  /**
   *
   *
   * @param move
   */
  public void setNodeToCopy(boolean move) {
    this.moveFlag = move;
    copiedExercise = null;
    copiedExerciseGroup = null;
    copiedNode = null;

    FacesContext context = FacesContext.getCurrentInstance();
    if (exerciseNode != null) {
      copiedNode = selectedNode;
      if (exerciseNode.isExercise()) {
        copiedExercise = exerciseNode.getExercise();
        context.addMessage(
          null,
          new FacesMessage(Cfg.inst().getProp(DEF_LANGUAGE, "ADMINTREE.CLIPBOARD") + ":", Cfg.inst().getProp(DEF_LANGUAGE,
                           "EXERCISE_GROUP") + ": [" + copiedExercise.getId() + "]"));
      } else if (exerciseNode.isExerciseGroup()) {
        copiedExerciseGroup = exerciseNode.getGroup();
        context.addMessage(
          null,
          new FacesMessage(Cfg.inst().getProp(DEF_LANGUAGE, "ADMINTREE.CLIPBOARD") + ":", Cfg.inst().getProp(DEF_LANGUAGE,
                           "EXERCISE")
                           + ": ["
                           + copiedExerciseGroup.getId()
                           + "] "
                           + copiedExerciseGroup.getName()));
      }
    }
  }

  /**
   *
   *
   */
  public void copyNode() {
    if (exerciseNode != null) {
      if (exerciseNode.isExerciseGroup()) {
        copyExercise(true);
      } else if (exerciseNode.isScenario()) {
        copyGroup(true);
      }
    }
  }

  /**
   *
   *
   */
  public void exportNode() {
    if (exerciseNode != null) {
      if (exerciseNode.isScenario()) {
        Scenario scenario = exerciseNode.getScenario();
        if (this.exporter == null) {
          exporter = new ScenarioExporter();
        }
        File exportFile = exporter.generateScenarioXml(scenario);
        if (exportFile != null && exportFile.exists()) {
          if (transfer == null) {
            transfer = new FileTransfer();
          }
          exportStream = transfer.getFileToDownload(exportFile);
        }
      }
    }
  }

  /**
   *
   *
   */
  public void deleteNode() {
    try {
      if (exerciseNode != null) {
        if (exerciseNode.isExercise()) {
          exerciseDao.deleteInstance(exerciseNode.getExercise());
        } else if (exerciseNode.isExerciseGroup()) {
          for (Exercise exercise : exerciseDao.findByExGroup(exerciseNode.getGroup())) {
            exerciseDao.deleteInstance(exercise);
          }
          exgroupDao.deleteInstance(exerciseNode.getGroup());
        } else if (exerciseNode.isScenario()) {
          for (ExerciseGroup group : exgroupDao.findByScenario(exerciseNode.getScenario())) {
            for (Exercise exercise : exerciseDao.findByExGroup(group)) {
              exerciseDao.deleteInstance(exercise);
            }
            exgroupDao.deleteInstance(group);
          }
          scenarioDao.deleteInstance(exerciseNode.getScenario());
          connectionPool.updateScenarios();
        }
        hideSelectedNode(selectedNode);
        updateSelectedNode();
      }
    } catch (Exception e) {
      LOGGER.error("FAILED UPDATING SCENARIOS IN ADMIN TREEBEAN", e);
    }
  }

  /**
   *
   *
   */
  public void duplicateNode() {
    try {
      if (exerciseNode != null) {
        if (exerciseNode.isExercise()) {
          copyExercise(false);
        } else if (exerciseNode.isExerciseGroup()) {
          copyGroup(false);
        } else if (exerciseNode.isScenario()) {
          Scenario scenario = exerciseNode.getScenario();
          Scenario newScenario = transferController.copy(scenario);

          TreeNode scenarioNode = new DefaultTreeNode(new ExerciseNode(newScenario), selectedNode.getParent());
          List<ExerciseGroup> groups = exgroupDao.findByScenario(newScenario);
          for (ExerciseGroup group : groups) {
            TreeNode groupNode = new DefaultTreeNode(new ExerciseNode(group), scenarioNode);
            List<Exercise> exercises = exerciseDao.findByExGroup(group);
            for (Exercise ex : exercises) {
              new DefaultTreeNode(new ExerciseNode(ex), groupNode);
            }
          }
        }
      }

    } catch (Exception e) {
      LOGGER.error("FAILED DUPLICATING NODE IN ADMIN TREEBEAN", e);
    }
  }

  /**
   *
   *
   * @param paste
   */
  private void copyExercise(boolean paste) {
    Exercise exercise = null;
    ExerciseGroup gr = null;

    try {
      if (paste && copiedExercise != null) {
        exercise = copiedExercise;
        if (exerciseNode.isExerciseGroup()) {
          gr = exerciseNode.getGroup();
        }
      } else {
        exercise = exerciseNode.getExercise();
        if (exercise != null) {
          gr = exercise.getExerciseGroup();
        }
      }
      if (exercise != null) {
        Exercise newExercise = null;
        if (moveFlag) {
          newExercise = transferController.copy(exercise, gr, true);
          hideSelectedNode(copiedNode);
        } else {
          newExercise = transferController.copy(exercise, gr, false);
        }

        if (newExercise != null) {
          TreeNode nodeToUpdate = null;
          if (paste) {
            nodeToUpdate = selectedNode;
          } else {
            nodeToUpdate = selectedNode.getParent();
          }
          new DefaultTreeNode(new ExerciseNode(newExercise), nodeToUpdate);
        }
      }
    } catch (Exception e) {
      LOGGER.error("FAILED COPYING EXCERCISE IN ADMIN TREEBEAN", e);
    }
  }

  /**
   *
   *
   * @param paste
   */
  private void copyGroup(boolean paste) {
    ExerciseGroup exerciseGroup = null;
    Scenario sc = null;

    try {
      if (paste && copiedExerciseGroup != null) {
        exerciseGroup = copiedExerciseGroup;
        if (exerciseNode.isScenario()) {
          sc = exerciseNode.getScenario();
        }
      } else {
        exerciseGroup = exerciseNode.getGroup();
        if (exerciseGroup != null) {
          sc = exerciseGroup.getScenario();
        }
      }

      if (exerciseGroup != null) {
        ExerciseGroup newExerciseGroup = null;
        if (moveFlag) {
          newExerciseGroup = transferController.copy(exerciseGroup, sc, true);
          hideSelectedNode(copiedNode);
        } else {
          newExerciseGroup = transferController.copy(exerciseGroup, sc, false);
        }

        if (newExerciseGroup != null) {
          TreeNode nodeToUpdate = null;

          if (paste) {
            nodeToUpdate = selectedNode;
          } else {
            nodeToUpdate = selectedNode.getParent();
          }

          TreeNode node = new DefaultTreeNode(new ExerciseNode(newExerciseGroup), nodeToUpdate);
          List<Exercise> exercises = exerciseDao.findByExGroup(newExerciseGroup);
          for (Exercise ex : exercises) {
            new DefaultTreeNode(new ExerciseNode(ex), node);
          }
        }
      }

    } catch (Exception e) {
      LOGGER.error("FAILED COPYING EXCERCISE GROUP IN ADMIN TREEBEAN", e);
    }
  }

  /**
   *
   *
   * @param node
   */
  private void hideSelectedNode(TreeNode node) {
    if (node != null) {
      node.setExpanded(true);
      if (node.getParent() != null) {
        node.getParent().setExpanded(true);
        node.getParent().getChildren().remove(node);
        node.setParent(null);
      }
      if (node.getChildren() != null) {
        node.getChildren().clear();
      }
      node = null;
    }
  }

  /**
   *
   *
   */
  private void updateSelectedNode() {
    if (selectedNode != null) {
      selectedNode.setExpanded(true);
    }
  }

  /**
   *
   *
   * @return
   */
  public boolean renderExportMenu() {
    // if (exerciseNode != null && (exerciseNode.isRootNode() || exerciseNode.isScenario())) {
    if (exerciseNode != null && exerciseNode.isScenario()) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderResultMenu() {
    if (exerciseNode != null && exerciseNode.isExerciseGroup()) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderRootNode() {
    if (exerciseNode != null && exerciseNode.isRootNode()) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderEditMenu() {
    if (exerciseNode != null && !exerciseNode.isRootNode()) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderDuplicateMenu() {
    if (exerciseNode != null) {
      if (exerciseNode.isScenario()) {
        if (rights.isAdmin(user)) {
          return true;
        }
      } else if (!exerciseNode.isRootNode()) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderDeleteMenu() {
    if (exerciseNode != null && !exerciseNode.isRootNode()) {
      if (exerciseNode.isScenario()) {
        if (rights.isAdmin()) {
          return rights.checkIfScenarioCanBeEdited(exerciseNode.getScenario());
        }
      } else if (exerciseNode.isExerciseGroup()) {
        return rights.checkIfExerciseGroupCanBeEdited(exerciseNode.getGroup());
      } else if (exerciseNode.isExercise()) {
        return rights.checkIfExerciseCanBeEdited(exerciseNode.getExercise());
      } else {
        return true;
      }
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderAddMenu() {
    if (exerciseNode != null) {
      if (exerciseNode.isRootNode()) {
        if (rights.isAdmin(user)) {
          return true;
        }
      } else if (!exerciseNode.isExercise()) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderPasteMenu() {
    if (exerciseNode != null && !exerciseNode.isExercise() && !exerciseNode.isRootNode()) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderCopyMenu() {
    if (exerciseNode != null && !exerciseNode.isRootNode() && !exerciseNode.isScenario()) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public boolean renderCutMenu() {
    return renderCopyMenu() && renderDeleteMenu();
  }
}
