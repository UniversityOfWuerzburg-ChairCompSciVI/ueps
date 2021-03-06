package de.uniwue.info6.parser.structures;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UpdateColumnStructure.java
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
 * MySQL UPDATE COLUMN
 * 
 * @author Christian
 *
 */
public class UpdateColumnStructure extends ColumnStructure {

	private String setValue;

	public UpdateColumnStructure(Structure value2, String tableName,
			String setValue) {

		super(value2, tableName);
		this.setValue = setValue;

	}

	public UpdateColumnStructure(String value, String tableName, String setValue) {

		super(value, tableName);
		this.setValue = setValue;

	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof UpdateColumnStructure)
			return super.equals(anotherStructure)
					&& setValue
							.equals(((UpdateColumnStructure) anotherStructure)
									.getSetValue());

		return false;

	}

	public String getSetValue() {
		return setValue;
	}

	@Override
	public String toString() {
		return super.toString() + " = " + setValue;
	}

}
