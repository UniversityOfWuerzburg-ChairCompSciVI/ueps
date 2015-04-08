package de.uniwue.info6.comparator;

import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SqlDocLinker.java
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
 * Klasse wandelt MySQL-Keywords in Urls um.
 * 
 * @author Christian
 * 
 */
public class SqlDocLinker {

	public static RefLink getUrlByKeyword(String keyword) {

		String url = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG,
				PropString.MYSQL_DOC_URL);

		if (keyword.equals("SELECT")) {
			url += "select.html";
		} else if (keyword.equals("UPDATE")) {
			url += "select.html";
		} else if (keyword.equals("INSERT")) {
			url += "insert.html";
		} else if (keyword.equals("DELETE")) {
			url += "delete.html";
		} else if (keyword.equals("JOIN")) {
			url += "join.html";
		} else if (keyword.equals("WHERE")) {
			url += "functions.html";
		} else if (keyword.equals("GROUPBY")) {
			url += "group-by-functions-and-modifiers.html";
		} else {
			return null;
		}

		return new RefLink(keyword, url);

	}

}
