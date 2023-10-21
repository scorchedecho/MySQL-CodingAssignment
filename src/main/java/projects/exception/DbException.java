package projects.exception;

/**
 * Exception class for database errors.
 *
 * @author Ari
 * @since 2023-10-04
 */
@SuppressWarnings("unused")
public class DbException extends RuntimeException {

  /**
   * Throw an exception with just a message.
   * @param message The message to display.
   */
  public DbException(String message) {
    super(message);
  }

  /**
   * Throw an exception with just a cause.
   * @param cause The cause of the exception.
   */
  public DbException(Throwable cause) {
    super(cause);
  }

  /**
   * Throw an exception with a message and a cause.
   * @param message The message to display.
   * @param cause The cause of the exception.
   */
  public DbException(String message, Throwable cause) {
    super(message, cause);
  }

}
