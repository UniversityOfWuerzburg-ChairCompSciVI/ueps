package de.uniwue.info6.database.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    ResultSet result = null;
    Statement statement = null;
    try {
      statement = connection.createStatement();
      statement.execute("SET FOREIGN_KEY_CHECKS = 0;");

      for (String table : tables) {
        statement = connection.createStatement();
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
      if (result != null) {
        result.close();
      }
    }
  }
}
