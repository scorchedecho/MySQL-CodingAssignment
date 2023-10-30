package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

  private static final String CATEGORY_TABLE = "category";
  private static final String MATERIAL_TABLE = "material";
  private static final String PROJECT_TABLE = "project";
  private static final String PROJECT_CATEGORY_TABLE = "project_category";
  private static final String STEP_TABLE = "step";

  /**
   * Insert a project into the database.
   * @param project The project to insert.
   * @return The project inserted.
   * @throws DbException If an error occurs.
   */
  public Project insertProject(Project project) {
    // SQL Statement to insert project values into database.
    // @formatter:off
    String sql = ""
        + "INSERT INTO " + PROJECT_TABLE + " "
        + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
        + "VALUES "
        + "(?, ?, ?, ?, ?)";
    // @formatter:on

    // Attempt connection & transaction.
    try (Connection conn = DbConnection.getConnection()) {
      // Start transaction.
      startTransaction(conn);

      // Attempt to insert project values into database.
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        // Set project details as parameters.
        setParameter(stmt, 1, project.getProjectName(), String.class);
        setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
        setParameter(stmt, 5, project.getNotes(), String.class);

        // Execute the statement.
        stmt.executeUpdate();

        // Grab the ID of the project inserted and commit the transaction.
        Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
        commitTransaction(conn);

        // Set the project's ID and return.
        project.setProjectId(projectId);
        return project;
      }
      catch (Exception e) {
        // Rollback transaction on failure.
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    }
    catch (SQLException sqle) {
      throw new DbException(sqle);
    }
  }
}
