package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

/**
 * ProjectDao class of the database test application.
 *
 * @author Ari
 * @since 2023-10-30
 */
public class ProjectDao extends DaoBase {

  private static final String CATEGORY_TABLE = "category";
  private static final String MATERIAL_TABLE = "material";
  private static final String PROJECT_TABLE = "project";
  private static final String PROJECT_CATEGORY_TABLE = "project_category";
  private static final String STEP_TABLE = "step";

  /**
   * Insert a project into the database.
   *
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
      } catch (Exception e) {
        // Rollback transaction on failure.
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException sqle) {
      throw new DbException(sqle);
    }
  }

  /**
   * Fetch all projects from the database.
   */
  public List<Project> fetchAllProjects() {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        try (ResultSet rs = stmt.executeQuery()) {
          List<Project> projects = new LinkedList<>();

          while (rs.next()) {
            projects.add(extract(rs, Project.class));
          }

          return projects;
        }
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException sqle) {
      throw new DbException(sqle);
    }

  }

  /**
   * Fetch a project by project ID.
   *
   * @param projectId The project ID.
   * @return The project.
   */
  public Optional<Project> fetchProjectById(Integer projectId) {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try {
        Project project = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          setParameter(stmt, 1, projectId, Integer.class);

          try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
              project = extract(rs, Project.class);
            }
          }
        }

        if (Objects.nonNull(project)) {
          project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
          project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
          project.getSteps().addAll(fetchStepsForProject(conn, projectId));
        }

        commitTransaction(conn);
        return Optional.ofNullable(project);

      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException sqle) {
      throw new DbException(sqle);
    }
  }

  /**
   * Fetch all categories for a project.
   *
   * @param conn The connection to the database.
   * @param projectId The project ID.
   * @return The list of categories.
   * @throws SQLException If an error occurs.
   */
  private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId)
      throws SQLException {
    // @formatter:off
    String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c "
        + "JOIN " + PROJECT_CATEGORY_TABLE + " pc "
        + "USING (category_id) "
        + "WHERE pc.project_id = ?";
    // @formatter:on

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);

      try (ResultSet resultSet = stmt.executeQuery()) {
        List<Category> categories = new LinkedList<>();

        while (resultSet.next()) {
          categories.add(extract(resultSet, Category.class));
        }

        return categories;
      }
    }
  }

  /**
   * Fetch all steps for a project.
   *
   * @param conn The connection to the database.
   * @param projectId The project ID.
   * @return The list of steps.
   * @throws SQLException If an error occurs.
   */
  private List<Step> fetchStepsForProject(Connection conn, Integer projectId)
      throws SQLException {
    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);

      try (ResultSet resultSet = stmt.executeQuery()) {
        List<Step> steps = new LinkedList<>();

        while (resultSet.next()) {
          steps.add(extract(resultSet, Step.class));
        }

        return steps;
      }
    }
  }

  /**
   * Fetch all materials from the database.
   *
   * @param conn The connection to the database.
   * @param projectId The project ID.
   * @return The list of materials.
   * @throws SQLException If an error occurs.
   */
  private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId)
      throws SQLException {
    String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);

      try (ResultSet resultSet = stmt.executeQuery()) {
        List<Material> materials = new LinkedList<>();

        while (resultSet.next()) {
          materials.add(extract(resultSet, Material.class));
        }

        return materials;
      }
    }
  }
}
