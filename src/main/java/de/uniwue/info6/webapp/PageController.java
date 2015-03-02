package de.uniwue.info6.webapp;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  PageController.java
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
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.webapp.admin.ExerciseNode;
import de.uniwue.info6.webapp.admin.SubmissionRow;
import de.uniwue.info6.webapp.session.SessionListener;
import de.uniwue.info6.webapp.session.SessionObject;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "moveTo")
@SessionScoped
public class PageController implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final String FACES_REDIRECT = "faces-redirect=true";

  private List<Exercise> currentExercises;

  /**
   *
   */
  public PageController() {
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String exerciseModule(Exercise ex) {
    setEntry(false);
    setSolution(false);

    return "task?exercise=" + ex.getId() + FACES_REDIRECT;
  }

  /**
   *
   *
   * @param exercises
   * @return
   */
  public String exerciseModule(List<Exercise> exercises) {
    setEntry(false);
    setSolution(false);

    this.currentExercises = exercises;
    if (exercises != null && !exercises.isEmpty()) {
      return "task?exercise=" + exercises.get(0).getId() + FACES_REDIRECT;
    }
    return null;
  }

  /**
   *
   *
   * @param ex
   * @param exercises
   * @return
   */
  public String exerciseModule(Exercise ex, List<Exercise> exercises) {
    setEntry(false);
    setSolution(false);

    this.currentExercises = exercises;
    return "task?exercise=" + ex.getId() + FACES_REDIRECT;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String exerciseModuleWithEntry(Exercise ex) {
    setEntry(true);
    setSolution(false);
    return "task?exercise=" + ex.getId() + FACES_REDIRECT;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String exerciseModuleWithSolution(Exercise ex) {
    setEntry(false);
    setSolution(true);
    return "task?exercise=" + ex.getId() + FACES_REDIRECT;
  }


  /**
   *
   *
   * @return
   */
  public String editSubmission(SubmissionRow currentNode) {
    String url = null;
    if (currentNode.getIsEntry()) {
      //  url = "edit_submission?exercise="
      //      + currentNode.getExercise().getId() + "&submission="
      //      + currentNode.getUserEntry().getId() + "&result="
      //      + currentNode.getUserResult().getId()  + FACES_REDIRECT;

      url = "edit-submission-"
            + currentNode.getExercise().getId() + "-"
            + currentNode.getUserEntry().getId() + "-"
            + currentNode.getUserResult().getId();
    }
    return url;
  }


  /**
   *
   *
   * @return
   */
  public String home() {
    return "index?" + FACES_REDIRECT;
  }


  /**
   *
   *
   * @return
   */
  public String logout() {
    SessionObject sessionObject = SessionObject.pullFromSession();
    if (sessionObject != null) {
      User user = sessionObject.getUser();
      if (user != null) {
        SessionListener.removeUser(user);
      }
    }
    return "index?" + FACES_REDIRECT;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public boolean deadEndNext(Exercise ex) {
    if (nextExercise(ex) == null) {
      return false;
    }
    return true;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public boolean deadEndPrevious(Exercise ex) {
    if (previousExercise(ex) == null) {
      return false;
    }
    return true;
  }

  /**
   *
   *
   * @param linkTarget
   * @return
   */
  public String getHeaderLinkStyle(String linkTarget) {
    FacesContext ctx = FacesContext.getCurrentInstance();
    HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
    String fullURI = servletRequest.getRequestURI();
    if (fullURI.contains(linkTarget)) {
      return "selected";
    }
    return "";
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String nextExercise(Exercise ex) {
    if (currentExercises != null && !currentExercises.isEmpty()) {
      if (currentExercises.contains(ex)) {
        int index = currentExercises.indexOf(ex);
        if (currentExercises.size() > (index + 1)) {
          return exerciseModule(currentExercises.get(index + 1));
        }
      }
    }
    return null;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String previousExercise(Exercise ex) {
    if (currentExercises != null && !currentExercises.isEmpty()) {
      if (currentExercises.contains(ex)) {
        int index = currentExercises.indexOf(ex);
        if (index > 0) {
          return exerciseModule(currentExercises.get(index - 1));
        }
      }
    }
    return null;
  }

  /**
   * @return the currentExercises
   */
  public List<Exercise> getCurrentExercises() {
    return currentExercises;
  }

  /**
   * @param currentExercises
   *            the currentExercises to set
   */
  public void setCurrentExercises(List<Exercise> currentExercises) {
    this.currentExercises = currentExercises;
  }

  /**
   *
   *
   * @param setEntry
   */
  private void setEntry(boolean setEntry) {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    Map<String, Object> sessionMap = externalContext.getSessionMap();
    sessionMap.put("show_entry", setEntry);
  }

  /**
   * @param showSolution the showSolution to set
   */
  public void setSolution(boolean showSolution) {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    Map<String, Object> sessionMap = externalContext.getSessionMap();
    sessionMap.put("show_solution", showSolution);
  }

  /**
   *
   *
   * @param scenario
   * @return
   */
  public String edit(ExerciseNode node, boolean newEntry) {
    String url = null;

    if (node != null) {
      if (node.isRootNode()) {
        url = "edit_scenario?scenario=";
        url += "new";
      } else if (node.isScenario()) {
        if (!newEntry) {
          url = "edit_scenario?scenario=";
          url += node.getScenario().getId();
        } else {
          url = "edit_group?scenario=";
          url += node.getScenario().getId();
        }
      } else if (node.isExerciseGroup()) {
        if (!newEntry) {
          url = "edit_group?group=";
          url += node.getGroup().getId();
        } else {
          url = "edit_ex?group=";
          url += node.getGroup().getId();
        }
      } else if (node.isExercise()) {
        url = "edit_ex?exercise=";
        url += node.getExercise().getId();
      }

      if (url != null) {
        url += FACES_REDIRECT;
      }
    }
    return url;
  }

  // new tab action
  // public void execute() throws IOException {
  //     FacesContext.getCurrentInstance().getExternalContext().redirect(Constants.EXTERNAL_URL);
  // }
}
