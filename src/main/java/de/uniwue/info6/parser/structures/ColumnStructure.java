package de.uniwue.info6.parser.structures;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ColumnStructure.java
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
 * MySQL COLUMNS
 * 
 * @author Christian
 *
 */
public class ColumnStructure extends Structure {

	protected Structure value2;
	protected String tableName;

	public ColumnStructure(Structure value2, String tableName) {
		super("");
		this.value2 = value2;
		this.tableName = tableName;
	}

	public ColumnStructure(String value, String tableName) {
		super(value);
		this.tableName = tableName;
	}

	public Structure getValue2() {
		return value2;
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (value2 != null && anotherStructure instanceof ColumnStructure)
			return value2.equals(((ColumnStructure) anotherStructure)
					.getValue2());
		else
			return value.equals(anotherStructure.getValue());

	}

	@Override
	public String toString() {

		if (value2 != null)
			return (tableName != null && tableName != "" ? tableName + "." : "")
					+ value2.toString();
		else
			return (tableName != null  && tableName != "" ? tableName + "." : "")
					+ super.toString();

	}

}
