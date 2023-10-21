package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection {

  /* DEFAULT DATABASE VARIABLES */
  private static String HOST = "localhost";
  private static String PASSWORD = "Wh1t3b04rdBl4ckb04rd!";
  private static int PORT = 3306;
  private static String SCHEMA = "projects";
  private static String USER = "projects";

  /**
   * Establish a connection with the database.
   * @return the {@link java.sql.Connection Connection} object.
   */
  public static Connection getConnection() {
    String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s", HOST, PORT, SCHEMA, USER, PASSWORD);

    // Try to connect to the database.
    try {
      Connection connection = DriverManager.getConnection(uri);
      System.out.println("Connected to database: " + SCHEMA + ".");
      // Success: Return
      return connection;
    }
    // Connection threw an exception:
    catch (SQLException sqle) {
      // Throw DbException with the cause. & a message of failure.
      throw new DbException("Unable to connect to database.", sqle);
    }

  }

}
