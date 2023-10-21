import projects.dao.DbConnection;

/**
 * Main class of the database test application.
 *
 * @author Ari
 * @since 2023-10-04
 */
public class ProjectsApp {

  public static void main(String[] args) {
    // Test a connection to the database.
    DbConnection.getConnection();
  }
}