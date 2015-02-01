package de.uniwue.info6.comparator;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SqlExecuter.java
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.parser.errors.SqlError;
import de.uniwue.info6.parser.structures.JoinTableStructure;
import de.uniwue.info6.parser.structures.TableStructure;
import de.uniwue.info6.parser.visitors.RootVisitor;
import de.uniwue.info6.webapp.session.SessionListener;

/* Howto create restricted user

GRANT USAGE ON *.* TO 'restricted_user'@'localhost' IDENTIFIED BY 'passwort' WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP, ALTER, LOCK TABLES ON  `dev\_ex` . * TO  'restricted_user'@'localhost';

 *
 *
 */
/**
 * Kapselt SQL-Connection und ermöglicht die Ausführung von SQL-Befehlen auf temporäre Tabelle. Verhindert non-secure Queries. 99%
 * Sicherheit ist allerdings nur mit der Einrichtung eines restricted Users gegeben.
 *
 * @author Christian
 *
 */
public class SqlExecuter {

  private Connection connection;
  private User user;
  private Scenario scenario;

  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SqlExecuter.class);

  /**
   *
   *
   * @param connection
   * @param user
   * @param scenario
   */
  public SqlExecuter(Connection connection, User user, Scenario scenario) {
    this.connection = connection;
    this.user = user;
    this.scenario = scenario;
  }

  /**
   *
   *
   * @param query
   */
  public void execute(SqlQuery query) {

    double startTime = System.nanoTime();

    if (query.getError() != null || query.getResult() != null) {
      LOGGER.warn("Query has already been executed");
      return;
    }

    if (query.getPlainContent() == null || query.getPlainContent().equals("")) {
      query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), System
                                  .getProperty("EXECUTER.EMPTY_QUERY"), Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.EMPTY_QUERY"), user.getId()));
      return;
    }

    if (query.getPlainContent().toLowerCase().contains("drop")
        || query.getPlainContent().toLowerCase().contains("truncate")
        || query.getPlainContent().toLowerCase().contains("create")
        || query.getPlainContent().toLowerCase().contains("alter table")
        || (!query.getPlainContent().toLowerCase().contains("update") && query.getPlainContent().toLowerCase()
            .contains("set"))) { // restricted user hat drop, truncate und alter rechte

      query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), System
                                  .getProperty("EXECUTER.SECURITY_ISSUE"), Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SECURITY_ISSUE"), user
                                  .getId()));
      return;

    }

    ResultSet result = null;
    Statement statement = null;
    ResultSetMetaData resultMetaData = null;

    RootVisitor parsedContent = null;

    boolean byPassParser = false;

    String queryText = SqlQuery.dejustPlainString(query.getPlainContent());

    try {
      parsedContent = query.getParsedContent();

      if (parsedContent.getMainKeyWord().equals("UPDATE") || parsedContent.getMainKeyWord().equals("DELETE")
          || parsedContent.getMainKeyWord().equals("INSERT")
          || parsedContent.getMainKeyWord().equals("SELECT")) {

        LinkedList<String> tables = tableParser(parsedContent.getTables());

        for (String tab : tables) {

          Pattern p = Pattern.compile("([^\\.\\s]([\\s]*))" + tab + "([\\.\\s\\,\\;\\)\\`]|$)");
          Matcher m = p.matcher(queryText); // get a matcher object

          String tmpQuery = queryText;


          while (m.find()) {

            String tmp = queryText.substring(m.start(), m.end());
            String newTmp = tmp.replaceAll(tab, user.getId() + "_" + tab);

            try {
              tmpQuery = tmpQuery.replace(tmp, newTmp);
            } catch (Exception e) {
              LOGGER.warn("TABLE REPLACE ISSUE ", e);
            }

          }

          queryText = tmpQuery;

        }


      } else {
        query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), System
                                    .getProperty("EXECUTER.SECURITY_ISSUE"), Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SECURITY_ISSUE"), user
                                    .getId()));
        return;
      }

    } catch (Exception e) {

      if (!queryText.contains("&&") && queryText.contains("&") || !queryText.contains("||")
          && queryText.contains("|")) {

        // if somebody tries to use | or & to escape a query, we stop him
        query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), System
                                    .getProperty("EXECUTER.SECURITY_ISSUE"), Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SECURITY_ISSUE"), user
                                    .getId()));
        return;

      }

      try { // provoke a sql error //TODO blind Tabellen ersetzen

        LOGGER.warn("PARSER HAS BEEN KILLED ", e);

        ConnectionManager pool = ConnectionManager.instance();

        ArrayList<String> tables = pool.getScenarioTableNames(scenario);

        for (String tab : tables) {

          Pattern p = Pattern.compile("([^\\.\\s]([\\s]*))" + tab + "([\\.\\s\\,\\;\\)\\(\\`]|$)");
          Matcher m = p.matcher(queryText); // get a matcher object

          String tmpQuery = queryText;


          while (m.find()) {

            String tmp = queryText.substring(m.start(), m.end());
            String newTmp = tmp.replaceAll(tab, user.getId() + "_" + tab);

            try {
              tmpQuery = tmpQuery.replace(tmp, newTmp);
            } catch (Exception e2) {
              LOGGER.warn("TABLE REPLACE ISSUE ", e2);
            }

          }

          queryText = tmpQuery;

        }

        statement = connection.createStatement();

        if (queryText.toLowerCase().contains("select")) {
          statement.executeQuery(queryText);
        } else {
          statement.executeUpdate(queryText);
        }

        pool.resetTables(scenario, user);


      } catch (Exception e2) {
        query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), e2.getMessage(), e2.getMessage(),
                                    user.getId()));
        return;

      } finally {

        try {

          if (result != null)
            result.close();

          if (statement != null)
            statement.close();

        } catch (SQLException ex) {
          ex.printStackTrace();
        }

      }

      // to make sure that the executer will stop here
      //query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), System
      //  .getProperty("EXECUTER.CANNOT_PARSE"), Cfg.inst().getProp(DEF_LANGUAGE, "SQL.EXECUTER.CANNOT_PARSE"), user.getId()));
      //return;
      LOGGER.warn("Query cannot be parsed but executed: " + queryText);
      byPassParser = true; // query cannot be parsed, but work suprisingly

    }

    if (parsedContent != null || byPassParser) {

      try {

        statement = connection.createStatement();
        statement.setQueryTimeout(5);

        if ((byPassParser && (queryText.toLowerCase().contains("select")))
            || (parsedContent != null && parsedContent.getMainKeyWord().equals("SELECT"))) {
          result = statement.executeQuery(queryText);
          resultMetaData = result.getMetaData();

        } else {
          statement.executeUpdate(queryText);

        }

      } catch (Exception e) {
        query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), e.getMessage(), e.getMessage(),
                                    user.getId()));
        return;
      }

      if ((byPassParser && !(queryText.toLowerCase().contains("select")))
          || (parsedContent != null && !parsedContent.getMainKeyWord().equals("SELECT"))) {

        try {

          if (result != null)
            result.close();

          if (statement != null)
            statement.close();

        } catch (SQLException ex) {
          ex.printStackTrace();
        }

        try {

          statement = connection.createStatement();

          if (byPassParser || parsedContent == null) {

            ConnectionManager pool = ConnectionManager.instance();
            ArrayList<String> tables = pool.getScenarioTableNames(scenario);
            String tmp2 = "";

            for (String tab : tables) { // q&d fix
              if (queryText.toLowerCase().contains(tab.toLowerCase()))
                tmp2 = "SELECT * FROM " + user.getId() + "_" + tab;
            }

            queryText = tmp2;

          } else {

            for (TableStructure table : parsedContent.getTables()) {
              queryText = "SELECT * FROM " + user.getId() + "_" + table.getValue();
            }

          }

          result = statement.executeQuery(queryText);
          resultMetaData = result.getMetaData();

          ConnectionManager pool = ConnectionManager.instance();
          pool.resetTables(scenario, user);

        } catch (Exception e) {
          query.setError(new SqlError(Cfg.inst().getProp(DEF_LANGUAGE, "EXECUTER.SQL_ERROR"), e.getMessage(), e
                                      .getMessage(), user.getId()));

          try {
            return;
          } finally {
            try {

              if (result != null)
                result.close();

              if (statement != null)
                statement.close();

            } catch (SQLException ex) {
              ex.printStackTrace();
            }
          }
        }
      }

      SqlResult tmpResult = new SqlResult(result, resultMetaData);
      query.setResult(tmpResult);

    }

    try {

      if (result != null)
        result.close();

      if (statement != null)
        statement.close();

    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    double endTime = System.nanoTime();

    String statsString = "executer_exec\t" + ((endTime - startTime) / 1000000);

    SessionListener.setExecuterStat(statsString);

  }

  private LinkedList<String> tableParser(LinkedList<TableStructure> tables) {

    LinkedList<String> tmpTables = new LinkedList<String>();

    //System.out.println("in: " + tables);

    for (TableStructure table : tables) {

      if (table instanceof JoinTableStructure) {

        LinkedList<TableStructure> tmp1 = new LinkedList<TableStructure>();
        tmp1.add(((JoinTableStructure) table).getLeftTable());

        LinkedList<TableStructure> tmp2 = new LinkedList<TableStructure>();
        tmp2.add(((JoinTableStructure) table).getRightTable());

        LinkedList<String> tablesNew = tableParser(tmp1);
        tablesNew.addAll(tableParser(tmp2));

        for (String tab : tablesNew) {

          if (!tmpTables.contains(tab)) {
            tmpTables.add(tab);
          }

        }

      } else {

        if (!tmpTables.contains(table.toString())) {
          tmpTables.add(table.getValue());
        }

      }

    }

    //System.out.println("out: " + tmpTables);

    return tmpTables;

  }

}
