package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import projects.dao.ProjectDao;
import projects.entity.Project;

/**
 * ProjectService class of the database test application.
 *
 * @author Ari
 * @since 2023-10-30
 */
public class ProjectService {
  private ProjectDao projectDao = new ProjectDao();

  /**
   * Add a project to the database.
   *
   * @param project The project to add.
   * @return The project added.
   */
  public Project addProject(Project project) {
    return projectDao.insertProject(project);
  }

  /**
   * Fetch all projects from the database.
   *
   * @return The list of projects.
   */
  public List<Project> fetchAllProjects() {
    return projectDao.fetchAllProjects();
  }

  /**
   * Fetch a project by project ID.
   *
   * @param projectId The project ID.
   * @return The project.
   */
  public Project fetchProjectById(Integer projectId) {
    return projectDao.fetchProjectById(projectId).orElseThrow(() ->
        new NoSuchElementException("Project with project ID=" + projectId + " not found."));
  }

  /**
   * Modify a project.
   *
   * @param project The project to modify.
   */
  public void modifyProjectDetails(Project project) {
    if (!projectDao.modifyProjectDetails(project)) {
      throw new NoSuchElementException(
          "Project with project ID=" + project.getProjectId() + " not found."
      );
    }
  }

  /**
   * Delete a project.
   *
   * @param projectId The project ID.
   */
  public void deleteProject(Integer projectId) {
    if (!projectDao.deleteProject(projectId)) {
      throw new NoSuchElementException("Project with project ID=" + projectId + " not found.");
    }
  }
}
