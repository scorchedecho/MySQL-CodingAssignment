import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

/**
 * Main class of the database test application.
 *
 * @author Ari
 * @since 2023-10-04
 */
public class ProjectsApp {
  private Scanner scanner = new Scanner(System.in);
  private ProjectService projectService = new ProjectService();
  private Project currentProject;

  // @formatter:off
  private List<String> operations = List.of(
      "1) Add a project",
      "2) List projects",
      "3) Select a project",
      "4) Update project details",
      "5) Delete a project"
  );
  // @formatter:on

  public static void main(String[] args) {
    new ProjectsApp().processUserSelections();
  }

  /**
   * Process the user's selections.
   */
  private void processUserSelections() {
    boolean done = false;
    while (!done) {
      try {
        int selection = getUserSelection();

        switch (selection) {
          case -1:
            done = exitMenu();
            break;
          case 1:
            createProject();
            break;
          case 2:
            listProjects();
            break;
          case 3:
            selectProjects();
            break;
          case 4:
            updateProjectDetails();
            break;
          case 5:
            deleteProject();
            break;
          default:
            System.out.println("\n" + selection + " is not a valid selection. Try again.");
            break;
        }
      } catch (Exception e) {
        System.out.println("An error occurred: " + e + " Please try again.");
      }
    }
  }

  /**
   * Select projects.
   */
  private void selectProjects() {
    // List projects and prompt for the ID to select.
    listProjects();
    Integer projectId = getIntInput("Enter the project ID to select");

    // Set the current project.
    currentProject = null;
    currentProject = projectService.fetchProjectById(projectId);
  }

  /**
   * List projects.
   */
  private void listProjects() {
    // Fetch all projects from the database.
    List<Project> projects = projectService.fetchAllProjects();

    System.out.println("\nProjects:");

    // Print each project.
    projects.forEach(project ->
        System.out.println(" " + project.getProjectId() + ") " + project.getProjectName()
    ));
  }

  /**
   * Update project details.
   */
  private void updateProjectDetails() {
    if (Objects.isNull(currentProject)) {
      // Quit out if no project is selected.
      System.out.println("You must select a project first.");
      return;
    }

    // Gather required information from the user.
    String projectName =
        getStringInput("Enter the project name [" + currentProject.getProjectName() + "]");
    BigDecimal estimatedHours =
        getDecimalInput("Enter the estimated hours [" + currentProject.getEstimatedHours() + "]");
    BigDecimal actualHours =
        getDecimalInput("Enter the actual hours [" + currentProject.getActualHours() + "]");
    Integer difficulty =
        getIntInput("Enter the difficulty (1 - 5) [" + currentProject.getDifficulty() + "]");
    String notes =
        getStringInput("Enter any project notes [" + currentProject.getNotes() + "]");

    // Create the project object.
    Project project = new Project();

    // Set the project's properties using inputs above; default to existing values if null.
    project.setProjectId(
        currentProject.getProjectId()
    );
    project.setProjectName(
        Objects.isNull(projectName) ? currentProject.getProjectName() : projectName
    );
    project.setEstimatedHours(
        Objects.isNull(estimatedHours) ? currentProject.getEstimatedHours() : estimatedHours
    );
    project.setActualHours(
        Objects.isNull(actualHours) ? currentProject.getActualHours() : actualHours
    );
    project.setDifficulty(
        Objects.isNull(difficulty) ? currentProject.getDifficulty() : difficulty
    );
    project.setNotes(
        Objects.isNull(notes) ? currentProject.getNotes() : notes
    );

    // Execute the statement.
    projectService.modifyProjectDetails(project);

    // Update current project.
    currentProject = projectService.fetchProjectById(currentProject.getProjectId());
  }

  /**
   * Delete a project.
   */
  private void deleteProject() {
    // List projects and prompt for the ID to delete.
    listProjects();
    Integer projectId = getIntInput("Enter the project ID to delete");

    // Delete project and print success message.
    projectService.deleteProject(projectId);
    System.out.println("Successfully deleted project with ID=" + projectId);

    // Reset current project to null as it was deleted.
    if (Objects.nonNull(currentProject) && currentProject.getProjectId().equals(projectId)) {
      currentProject = null;
    }
  }

  /**
   * Exit the menu.
   *
   * @return True if the menu should exit, false otherwise.
   */
  private boolean exitMenu() {
    System.out.println("Exiting...");
    scanner.close();
    return true;
  }

  /**
   * Get the user's selection.
   *
   * @return The user's selection, -1 if nothing is selected.
   */
  private int getUserSelection() {
    printOperations();

    Integer input = getIntInput("Please select an operation");

    return Objects.isNull(input) ? -1 : input;
  }

  /**
   * Print available operations for the user to choose from.
   */
  private void printOperations() {
    // Print available operations.
    System.out.println("\nThese are the available selections. Press the enter key to quit:");
    operations.forEach(line -> System.out.println(" " + line));

    // Print current project if one is selected.
    if (Objects.isNull(currentProject)) {
      System.out.println("\nYou are not working with a project.");
    } else {
      System.out.println("\nYou are working with project: " + currentProject);
    }
  }

  /**
   * Get an int input from the user's String input.
   *
   * @param prompt The prompt for the user
   * @return The int input from the user.
   */
  private Integer getIntInput(String prompt) {
    String input = getStringInput(prompt);

    // Return null if the input is null.
    if (Objects.isNull(input)) {
      return null;
    }

    // Parse the input to an int, throwing an exception if it is not an int.
    try {
      return Integer.valueOf(input);
    } catch (NumberFormatException nfe) {
      throw new DbException(input + " is not a valid integer number.");
    }
  }

  /**
   * Get a decimal input from the user's String input.
   *
   * @param prompt The prompt for the user
   * @return The decimal input from the user.
   */
  private BigDecimal getDecimalInput(String prompt) {
    String input = getStringInput(prompt);

    // Return null if the input is null.
    if (Objects.isNull(input)) {
      return null;
    }

    // Parse the input to a BigDecimal, throwing an exception if it is not a BigDecimal.
    try {
      return new BigDecimal(input).setScale(2);
    } catch (NumberFormatException nfe) {
      throw new DbException(input + " is not a valid decimal number.");
    }
  }

  /**
   * Get a String input from the user.
   *
   * @param prompt The prompt for the user.
   * @return The String input from the user.
   */
  private String getStringInput(String prompt) {
    System.out.print(prompt + ": ");
    String input = scanner.nextLine();

    // Return null if the input is blank, otherwise trim the input and return.
    return input.isBlank() ? null : input.trim();
  }

  /**
   * Create a project.
   */
  private void createProject() {
    // Gather required information from the user.
    String projectName = getStringInput("Enter the project name");
    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
    Integer difficulty = getIntInput("Enter the difficulty (1 - 5)");
    String notes = getStringInput("Enter any project notes");

    // Create the project object.
    Project project = new Project();

    // Set the project's properties using inputs above.
    project.setProjectName(projectName);
    project.setEstimatedHours(estimatedHours);
    project.setActualHours(actualHours);
    project.setDifficulty(difficulty);
    project.setNotes(notes);

    Project dbProject = projectService.addProject(project);
    System.out.println("Successfully added project: " + dbProject);
  }
}