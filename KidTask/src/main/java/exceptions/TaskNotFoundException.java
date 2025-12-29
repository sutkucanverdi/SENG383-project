package exceptions;

/**
 * Thrown when an operation references an unknown task id.
 */
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String taskId) {
        super("Task not found: " + taskId);
    }
}

