package de.uniwue.info6.database.map;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SolutionQuery.java
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

// Generated Oct 30, 2013 2:01:27 PM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import de.uniwue.info6.database.map.daos.SolutionQueryDao;

/**
 * SolutionQuery generated by hbm2java
 */
public class SolutionQuery implements java.io.Serializable {
  // ******************************************************************
  // custom (not generated methods)
  // ******************************************************************

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((exercise == null) ? 0 : exercise.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((query == null) ? 0 : query.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof SolutionQuery)) {
      return false;
    }
    SolutionQuery other = (SolutionQuery) obj;
    if (exercise == null) {
      if (other.exercise != null) {
        return false;
      }
    } else if (!exercise.equals(other.exercise)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (query == null) {
      if (other.query != null) {
        return false;
      }
    } else if (!query.equals(other.query)) {
      return false;
    }
    return true;
  }

  // ******************************************************************
  // generated methods of hibernate
  // ******************************************************************

  private Integer id;
  private UserEntry userEntry;
  private Exercise exercise;
  private String query;
  private String explanation;
  private Byte status;
  private Set userResults = new HashSet(0);

  public SolutionQuery() {
  }

  public SolutionQuery(Exercise exercise, String query) {
    this.exercise = exercise;
    this.query = query;
  }

  public SolutionQuery(UserEntry userEntry, Exercise exercise, String query, String explanation, Byte status,
                       Set userResults) {
    this.userEntry = userEntry;
    this.exercise = exercise;
    this.query = query;
    this.explanation = explanation;
    this.status = status;
    this.userResults = userResults;
  }

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UserEntry getUserEntry() {
    return this.userEntry;
  }

  @XmlTransient
  public void setUserEntry(UserEntry userEntry) {
    this.userEntry = userEntry;
  }

  public Exercise getExercise() {
    return this.exercise;
  }

  @XmlTransient
  public void setExercise(Exercise exercise) {
    this.exercise = exercise;
  }

  public String getQuery() {
    return this.query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getExplanation() {
    return this.explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public Byte getStatus() {
    return this.status;
  }

  public void setStatus(Byte status) {
    this.status = status;
  }

  public Set getUserResults() {
    return this.userResults;
  }

  @XmlTransient
  public void setUserResults(Set userResults) {
    this.userResults = userResults;
  }

  public SolutionQuery pull() {
    return new SolutionQueryDao().getById(getId());
  }
}
