package kidtask.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Task entity representing a task assigned to a child.
 */
public class Task {
    private String id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private int points;
    private TaskStatus status;
    private TaskType type;
    private String childId;
    private Double rating;

    /**
     * Constructor for creating a new task with auto-generated ID.
     *
     * @param title     Task title
     * @param description Task description
     * @param dueDate   Due date
     * @param points    Reward points
     * @param type      Task type (DAILY/WEEKLY)
     * @param childId   ID of the child assigned to this task
     */
    public Task(String title, String description, LocalDate dueDate, int points, TaskType type, String childId) {
        this(UUID.randomUUID().toString(), title, description, dueDate, points, TaskStatus.NEW, type, childId, null);
    }

    /**
     * Full constructor for creating a task with all fields.
     *
     * @param id          Task ID (UUID)
     * @param title       Task title
     * @param description Task description
     * @param dueDate     Due date
     * @param points      Reward points
     * @param status      Task status
     * @param type        Task type (DAILY/WEEKLY)
     * @param childId     ID of the child assigned to this task
     * @param rating      Rating (nullable, set when approved)
     */
    public Task(String id, String title, String description, LocalDate dueDate, int points,
                TaskStatus status, TaskType type, String childId, Double rating) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = description != null ? description : "";
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");
        this.points = points;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.childId = Objects.requireNonNull(childId, "Child ID cannot be null");
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = Objects.requireNonNull(childId, "Child ID cannot be null");
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", points=" + points +
                ", status=" + status +
                ", type=" + type +
                ", childId='" + childId + '\'' +
                ", rating=" + rating +
                '}';
    }
}

