package de.uniwue.info6.parser.structures;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  JoinTableStructure.java
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


/**
 * MySQL JOIN
 * 
 * @author Christian
 *
 */
public class JoinTableStructure extends TableStructure {

	private TableStructure leftTable;
	private String joinType;
	private TableStructure rightTable;
	private Structure joinClause;

	public JoinTableStructure(TableStructure leftTable,
			TableStructure rightTable, Structure joinClause, String joinType) {

		super("");
		this.leftTable = leftTable;
		this.rightTable = rightTable;
		this.joinClause = joinClause;
		this.joinType = joinType;

	}

	@Override
	public String toString() {
		return joinType + "(" + leftTable.toString() + ", "
				+ rightTable.toString() + ") ON " + joinClause.toString();
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof JoinTableStructure) {
			return leftTable.equals(((JoinTableStructure) anotherStructure)
					.getLeftTable())
					&& rightTable
							.equals(((JoinTableStructure) anotherStructure)
									.getRightTable())
					&& joinClause
							.equals(((JoinTableStructure) anotherStructure)
									.getJoinClause())
					&& joinType.equals(((JoinTableStructure) anotherStructure)
							.getJoinType());
		}

		return false;

	}

	private String getJoinType() {
		return joinType;
	}

	public TableStructure getLeftTable() {
		return leftTable;
	}

	public TableStructure getRightTable() {
		return rightTable;
	}

	public Structure getJoinClause() {
		return joinClause;
	}

}
