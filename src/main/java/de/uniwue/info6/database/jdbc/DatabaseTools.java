package de.uniwue.info6.database.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.uniwue.info6.database.map.User;

/**
 *
 *
 * @author
 */
public class DatabaseTools {
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DatabaseTools.class);

  /**
   *
   *
   * @param connection
   * @param user
   * @return
   *
   * @throws SQLException
   */
  public static List<String> getTablesOfUser(Connection connection, User user)
  throws SQLException {
    ResultSet result = null;
    Statement statement = null;
    List<String> tables = new LinkedList<String>();
    try {
      statement = connection.createStatement();
      String showTables = "SHOW TABLES";
      if (user != null) {
        showTables += " LIKE '" + user.getId() + "_%'";
      }
      showTables += ";";
      result = statement.executeQuery(showTables);

      result.beforeFirst();
      while (result.next()) {
        tables.add(result.getString(1));
      }
    } catch (Exception ex) {
      LOGGER.error("PROBLEMS EXECUTING 'SHOW TABLES'", ex);
    } finally {
      if (result != null) {
        result.close();
        result = null;
      }
      if (statement != null) {
        statement.close();
        statement = null;
      }
    }
    return tables;
  }


  /**
   *
   *
   * @param connection
   * @param tables
   * @param user
   *
   * @throws SQLException
   */
  public static void dropTable(Connection connection, List<String> tables)
  throws SQLException {
    Statement statement = null;
    try {
      statement = connection.createStatement();
      statement.execute("SET FOREIGN_KEY_CHECKS = 0;");

      for (String table : tables) {
        // dropping tables at startup
        statement.execute("UNLOCK TABLES;");
        statement.execute("DROP TABLE IF EXISTS `" + table + "`;");
      }
    } catch (Exception ex) {
      LOGGER.error("PROBLEMS EXECUTING 'DROP TABLE'", ex);
    } finally {
      if (statement != null) {
        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");
        statement.close();
      }
    }
  }


  /**
   *
   *
   * @param connection
   * @param grantScript
   *
   * @throws SQLException
   */
  public static void grantRights(Connection connection, String grantScript)
  throws SQLException {
    Statement statement = null;
    try {
      statement = connection.createStatement();
      simpleExecute(connection, grantScript.split(";"));
    } catch (Exception ex) {
      LOGGER.error("PROBLEMS EXECUTING '" + grantScript + "'", ex);
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  /**
   *
   *
   * @param connection
   * @param grantScript
   *
   * @throws SQLException
   */
  public static void createDatabase(Connection connection, String dbName)
  throws SQLException {
    final String query = "CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8;";
    System.err.println("INFO (ueps): Creating database `" + dbName + "`");
    simpleExecute(connection, query);
  }


  /**
   *
   *
   * @param connection
   * @param dbName
   *
   * @throws SQLException
   */
  public static void dropDatabase(Connection connection, String dbName)
  throws SQLException {
    final String query = "DROP DATABASE IF EXISTS `" + dbName + "`;";
    System.err.println("INFO (ueps): Dropping database `" + dbName + "`");
    simpleExecute(connection, query);
  }

  /**
  *
  *
  * @param connection
  * @throws SQLException
  */
  public static void removeRestrictedUsers(Connection connection, String host) throws SQLException {
    final String userQueries = "SELECT CONCAT('DROP DATABASE IF EXISTS `'"
                               + ",schema_name,'`; ') AS stmt FROM "
                               + "information_schema.schemata WHERE schema_name "
                               + "LIKE 'ueps\\_slave\\_%' ESCAPE '\\\\' "
                               + "ORDER BY schema_name";
    final List<String> dropDBQueries = simpleQuery(connection, userQueries);
    for (String dropDBQuery : dropDBQueries) {
      System.err.println("INFO (ueps): " + dropDBQuery.trim() + "");
      simpleExecute(connection, dropDBQuery.trim());
    }

    final String selectAllUsersQuery = "SELECT User FROM mysql.user;";
    final List<String> userList = simpleQuery(connection, selectAllUsersQuery);

    for (String userName : userList) {
      userName = userName.trim();
      if (userName.startsWith("ueps_")) {
        String userID = userName + "@" + host;
        System.err.println("INFO (ueps): Dropping restricted database user: `" + userID + "`");
        simpleExecute(connection, "REVOKE ALL PRIVILEGES, GRANT OPTION FROM "
                      + userID + ";", "DROP USER " + userID + ";");
      }
    }
  }


  /**
   *
   *
   * @param connection
   * @param query
   *
   * @throws SQLException
   */
  private static void simpleExecute(Connection connection, String... queries)
  throws SQLException {
    Statement statement = null;
    String currentQuery = null;
    try {
      statement = connection.createStatement();
      for (String query : queries) {
        currentQuery = query;
        if (query != null && !query.trim().isEmpty() && query.trim().length() > 5) {
          statement.execute(query);
        }
      }
    } catch (Exception ex) {
      LOGGER.error("PROBLEMS EXECUTING '" + currentQuery + "'", ex);
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }


  /**
   *
   *
   * @param connection
   * @param query
   * @return
   *
   * @throws SQLException
   */
  public static List<String> simpleQuery(Connection connection, String query)
  throws SQLException {
    ResultSet result = null;
    Statement statement = null;
    List<String> resultAsList = null;
    try {
      statement = connection.createStatement();
      result = statement.executeQuery(query);
      result.beforeFirst();
      resultAsList = new ArrayList<String>();

      while (result.next()) {
        resultAsList.add(result.getString(1));
      }

    } catch (Exception ex) {
      LOGGER.error("PROBLEMS EXECUTING 'SHOW TABLES'", ex);
    } finally {
      if (result != null) {
        result.close();
        result = null;
      }
      if (statement != null) {
        statement.close();
        statement = null;
      }
    }
    return resultAsList;
  }

}
