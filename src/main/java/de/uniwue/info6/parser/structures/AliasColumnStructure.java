package de.uniwue.info6.parser.structures;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AliasColumnStructure.java
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
 * MySQL ALIAS
 * 
 * @author Christian
 *
 */
public class AliasColumnStructure extends ColumnStructure {

	private String aliasValue;

	public AliasColumnStructure(Structure value2, String tableName,
			String aliasValue) {
		super(value2, tableName);
		this.aliasValue = aliasValue;
	}

	public AliasColumnStructure(String value, String tableName,
			String aliasValue) {
		super(value, tableName);
		this.aliasValue = aliasValue;
	}

	public String getAliasValue() {
		return aliasValue;
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof AliasColumnStructure)
			return super.equals(anotherStructure)
					&& aliasValue
							.equals(((AliasColumnStructure) anotherStructure)
									.getAliasValue());

		return false;

	}

	@Override
	public String toString() {
		return super.toString() + " AS " + aliasValue;
	}

}
