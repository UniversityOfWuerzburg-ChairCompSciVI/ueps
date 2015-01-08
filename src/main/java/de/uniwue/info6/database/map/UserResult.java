package de.uniwue.info6.database.map;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UserResult.java
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

import java.util.Date;

/**
 * UserResult generated by hbm2java
 */
public class UserResult implements java.io.Serializable {
	// ******************************************************************
	// custom (not generated methods)
	// ******************************************************************
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (!(obj instanceof UserResult)) {
			return false;
		}
		UserResult other = (UserResult) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	// ******************************************************************
	// generated methods of hibernate
	// ******************************************************************

	private Integer id;
	private UserEntry userEntry;
	private User user;
	private SolutionQuery solutionQuery;
	private byte credits;
	private Date lastModified;
	private String comment;

	public UserResult() {
	}

	public UserResult(UserEntry userEntry, byte credits, Date lastModified) {
		this.userEntry = userEntry;
		this.credits = credits;
		this.lastModified = lastModified;
	}

	public UserResult(UserEntry userEntry, User user, SolutionQuery solutionQuery, byte credits, Date lastModified,
			String comment) {
		this.userEntry = userEntry;
		this.user = user;
		this.solutionQuery = solutionQuery;
		this.credits = credits;
		this.lastModified = lastModified;
		this.comment = comment;
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

	public void setUserEntry(UserEntry userEntry) {
		this.userEntry = userEntry;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SolutionQuery getSolutionQuery() {
		return this.solutionQuery;
	}

	public void setSolutionQuery(SolutionQuery solutionQuery) {
		this.solutionQuery = solutionQuery;
	}

	public byte getCredits() {
		return this.credits;
	}

	public void setCredits(byte credits) {
		this.credits = credits;
	}

	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
