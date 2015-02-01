package de.uniwue.info6.parser.errors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SqlError.java
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
import de.uniwue.info6.misc.properties.Cfg;

/**
 *
 * @author Christian
 *
 */
public class SqlError extends Error {

  private String origText;

  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SqlError.class);

  public SqlError(String title, String text, String origText, String user) {

    super(title, translateSqlOutput(text, user));

    try {

      if (origText.matches("Table '[\\S]*' doesn't exist")) {
        String tmp = origText.substring(origText.indexOf("'") + 1,
                                        origText.indexOf("'", origText.indexOf("'") + 1));
        origText = origText.replace(tmp, cleanTableFromPrefix(tmp, user));
      } else if (origText.matches("Unknown column '[\\S]*'[\\S\\s]*")) {

        String tmp = origText.substring(origText.indexOf("'") + 1,
                                        origText.indexOf("'", origText.indexOf("'") + 1));

        if (tmp.contains(user + "_")) {
          origText = origText.replace(user + "_", "");
        }

      } else {
        if (origText.contains(user + "_")) {
          origText = origText.replace(user + "_", "");
        }
      }
    } catch (Exception e) {
      LOGGER.error("PROBLEM WITH TRANSLATING SQL ERROR MESSAGE:\n" + title + "\n" + text + "\n" + origText + "\n"
                   + user, e);
    }

    this.origText = origText;
  }

  public String getOrigText() {
    return origText;
  }

  public void setOrigText(String origText) {
    this.origText = origText;
  }

  public static String translateSqlOutput(String origText, String user) {

    if (origText == null) {
      return "";
    }

    if (origText.matches("Table '[\\S]*' doesn't exist")) {
      String tmp = origText
                   .substring(origText.indexOf("'") + 1, origText.indexOf("'", origText.indexOf("'") + 1));
      return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.TABLE_NOTFOUND"),
                                new String[] { cleanTableFromPrefix(tmp, user) });
    } else if (origText.matches("Unknown column '[\\S]*'[\\S\\s]*")) {

      String tmp = origText
                   .substring(origText.indexOf("'") + 1, origText.indexOf("'", origText.indexOf("'") + 1));

      if (tmp.contains(user + "_")) {
        tmp = tmp.replace(user + "_", "");
      }

      return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.COLUMN_NOTFOUND"), new String[] { tmp });

    } else if (origText.contains("to use near")) {
      String tmp = origText
                   .substring(origText.indexOf("'") + 1, origText.indexOf("'", origText.indexOf("'") + 1));

      if (tmp.contains(user + "_")) {
        tmp = tmp.replace(user + "_", "");
      }

      return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SYNTAX_ERROR"), new String[] { tmp });

    } else if (origText.contains("Access denied")) {
      return Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SECURITY_ISSUE");

    } else {
      if (origText.contains(user + "_")) {
        origText = origText.replace(user + "_", "");
      }
    }

    return origText;

  }

  private static String cleanTableFromPrefix(String tmp, String user) {

    if (tmp.contains(".")) {
      tmp = tmp.substring(tmp.indexOf(".") + 1, tmp.length());
    }

    if (tmp.contains(user + "_")) {
      tmp = tmp.replace(user + "_", "");
    }

    return tmp;

  }

  public static String fillPropertyString(String varStr, String[] data) {

    String tmp = /*System.getProperty(*/varStr/*)*/;

    if (data != null) {
      for (String tmpStr : data) {
        tmp = tmp.replaceFirst("%", tmpStr);
      }
    }

    return tmp;

  }

}

