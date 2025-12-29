package kidtask.persistence;

import kidtask.model.Task;
import kidtask.model.TaskStatus;
import kidtask.model.TaskType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Task entities, handles loading and saving tasks from/to file storage.
 */
public class TaskRepository {
    private static final String FILE_NAME = "Tasks.txt";
    private static final String HEADER = "id|title|description|dueDate|points|status|type|childId|rating";

    private final FileStorage fileStorage;

    public TaskRepository() {
        this.fileStorage = new FileStorage(FILE_NAME);
    }

    /**
     * Loads all tasks from the storage file.
     *
     * @return List of all tasks
     * @throws StorageException if file operations fail
     */
    public List<Task> loadAll() throws StorageException {
        List<List<String>> rows = fileStorage.readAll(HEADER);
        List<Task> tasks = new ArrayList<>();

        for (List<String> row : rows) {
            try {
                Task task = parseTask(row);
                tasks.add(task);
            } catch (Exception e) {
                // Skip invalid rows, log error
                System.err.println("Skipping invalid task row: " + row + " - " + e.getMessage());
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks to the storage file.
     *
     * @param tasks List of tasks to save
     * @throws StorageException if file operations fail
     */
    public void saveAll(List<Task> tasks) throws StorageException {
        List<List<String>> rows = new ArrayList<>();

        for (Task task : tasks) {
            List<String> row = formatTask(task);
            rows.add(row);
        }

        fileStorage.writeAll(HEADER, rows);
    }

    /**
     * Generates a new unique ID for a task.
     *
     * @return New UUID string
     */
    public String nextId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Parses a row of data into a Task object.
     *
     * @param row List of field values
     * @return Task object
     */
    private Task parseTask(List<String> row) {
        if (row.size() < 9) {
            throw new IllegalArgumentException("Invalid task row: insufficient fields");
        }

        String id = row.get(0);
        String title = row.get(1);
        String description = row.get(2);
        LocalDate dueDate = LocalDate.parse(row.get(3));
        int points = Integer.parseInt(row.get(4));
        TaskStatus status = TaskStatus.valueOf(row.get(5).toUpperCase());
        TaskType type = TaskType.valueOf(row.get(6).toUpperCase());
        String childId = row.get(7);
        Double rating = parseDouble(row.get(8));

        return new Task(id, title, description, dueDate, points, status, type, childId, rating);
    }

    /**
     * Formats a Task object into a row of data.
     *
     * @param task Task object
     * @return List of field values
     */
    private List<String> formatTask(Task task) {
        List<String> row = new ArrayList<>();
        row.add(task.getId());
        row.add(task.getTitle());
        row.add(task.getDescription());
        row.add(task.getDueDate().toString());
        row.add(String.valueOf(task.getPoints()));
        row.add(task.getStatus().name());
        row.add(task.getType().name());
        row.add(task.getChildId());
        row.add(task.getRating() != null ? String.valueOf(task.getRating()) : "");
        return row;
    }

    /**
     * Parses a string to Double, returns null if empty or invalid.
     *
     * @param value String value to parse
     * @return Double value or null
     */
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

