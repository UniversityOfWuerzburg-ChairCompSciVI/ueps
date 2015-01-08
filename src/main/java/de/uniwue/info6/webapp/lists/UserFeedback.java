package de.uniwue.info6.webapp.lists;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UserFeedback.java
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

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import de.uniwue.info6.database.map.User;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.parser.errors.Error;
import de.uniwue.info6.parser.errors.JavaError;
import de.uniwue.info6.parser.errors.ResultError;
import de.uniwue.info6.parser.errors.SemanticError;
import de.uniwue.info6.parser.errors.SqlError;

/**
 *
 *
 * @author Michael
 */
@ManagedBean
@ViewScoped
public class UserFeedback implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private boolean syntaxError;
  private boolean semanticError;
  private boolean mainError;
  private boolean mainErrorEntries;
  private boolean success;
  private boolean javaError;
  private Error error;
  private String tabName;

  private String feedback;
  private String originalFeedback;
  private String title;

  /**
   *
   *
   * @param title
   * @param feedback
   */
  public UserFeedback(String title, String feedback, User user) {
    this.title = title;
    this.feedback = cleanFeedback(feedback, user);
  }

  /**
   *
   *
   * @param feedback
   * @param user
   * @return
   */
  private String cleanFeedback(String feedback, User user) {
    String temp = feedback;
    try {
      if (user != null && temp != null) {
        if (temp.trim().contains(user.getId() + "_")) {
          temp = temp.replaceAll(user.getId() + "_", "");
        }
        if (temp.trim().contains(user.getId())) {
          temp = temp.replaceAll(user.getId(), "");
        }
        if (temp.trim().contains("\"DERBYDASHPROPERTIES\" ...")) {
          temp = temp.replace("\"DERBYDASHPROPERTIES\" ...", "");
        }
      }
    } catch (Exception e) {
    }
    return temp;
  }

  /**
   *
   */
  public UserFeedback(Error er, User user) {
    this.error = er;
    this.feedback = cleanFeedback(er.getText(), user);
    this.title = er.getTitle();

    if (SqlError.class.isInstance(er)) {
      SqlError error = (SqlError) er;
      String or = error.getOrigText();
      if (or != null && !or.equals(error.getText())) {
        originalFeedback = error.getOrigText();
      }
      this.syntaxError = true;
      this.tabName = "Syntaktischer Fehler";
    } else if (SemanticError.class.isInstance(er)) {
      this.semanticError = true;
      this.tabName = "Semantischer Fehler";
    } else if (JavaError.class.isInstance(er)) {
      this.javaError = true;
      this.title = "Runtime Error";
      this.tabName = "Runtime Error";
    } else if (ResultError.class.isInstance(er)) {
      if (feedback.equals(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_ENTRY_WRONG"))) {
        this.mainErrorEntries = true;
      } else {
        this.mainError = true;
      }
      if (((ResultError) er).isCorrect()) {
        success = true;
      }
      tabName = title;
    }
  }

  /**
   * @return the syntaxError
   */
  public boolean isSyntaxError() {
    return syntaxError;
  }

  /**
   * @return the semanticError
   */
  public boolean isSemanticError() {
    return semanticError;
  }

  /**
   * @return the mainError
   */
  public boolean isMainError() {
    return mainError;
  }

  /**
   * @param mainError
   *            the mainError to set
   */
  public void setMainError(boolean mainError) {
    this.mainError = mainError;
  }

  /**
   * @return the success
   */
  public boolean isCorrect() {
    return success;
  }

  /**
   * @return the mainErrorEntries
   */
  public boolean isMainErrorEntries() {
    return mainErrorEntries;
  }

  /**
   * @param mainErrorEntries
   *            the mainErrorEntries to set
   */
  public void setMainErrorEntries(boolean mainErrorEntries) {
    this.mainErrorEntries = mainErrorEntries;
  }

  /**
   * @param success
   *            the success to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * @return the javaError
   */
  public boolean isJavaError() {
    return javaError;
  }

  /**
   * @return the error
   */
  public Error getError() {
    return error;
  }

  /**
   * @param error
   *            the error to set
   */
  public void setError(Error error) {
    this.error = error;
  }

  /**
   * @return the feedback
   */
  public String getFeedback() {
    return feedback;
  }

  /**
   * @param feedback
   *            the feedback to set
   */
  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  /**
   * @return the originalFeedback
   */
  public String getOriginalFeedback() {
    return originalFeedback;
  }

  /**
   *
   *
   * @return
   */
  public boolean hasOriginalFeedback() {
    return originalFeedback != null;
  }

  /**
   * @param originalFeedback
   *            the originalFeedback to set
   */
  public void setOriginalFeedback(String originalFeedback) {
    this.originalFeedback = originalFeedback;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *            the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the tabName
   */
  public String getTabName() {
    return tabName;
  }

}
