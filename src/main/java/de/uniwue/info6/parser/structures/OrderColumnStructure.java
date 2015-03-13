package de.uniwue.info6.parser.structures;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  OrderColumnStructure.java
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
 * MySQL ORDER BY
 * 
 * @author Christian
 *
 */
public class OrderColumnStructure extends ColumnStructure {

	private boolean isAscending = true;
	private boolean explicit = true;

	public OrderColumnStructure(Structure value2, String tableName,
			boolean isAscending) {
		super(value2, tableName);
		this.isAscending = isAscending;
	}

	public OrderColumnStructure(String value, String tableName,
			boolean isAscending) {
		super(value, tableName);
		this.isAscending = isAscending;
	}
	
	public OrderColumnStructure(Structure value2, String tableName,
			boolean isAscending, boolean explicit) {
		super(value2, tableName);
		this.isAscending = isAscending;
		this.explicit = explicit;
	}

	public OrderColumnStructure(String value, String tableName,
			boolean isAscending, boolean explicit) {
		super(value, tableName);
		this.isAscending = isAscending;
		this.explicit = explicit;
	}

	public boolean isAcending() {
		return isAscending;
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof OrderColumnStructure)
			return super.equals(anotherStructure)
					&& isAscending == ((OrderColumnStructure) anotherStructure)
							.isAcending();

		return false;

	}

	@Override
	public String toString() {
		return super.toString() + (isAscending ? (explicit ? " ASC" : "") : " DESC");
	}

}
