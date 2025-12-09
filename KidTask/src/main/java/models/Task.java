package models;

import enums.TaskStatus;
import enums.TaskType;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private final String id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private int points;
    private TaskStatus status;
    private TaskType type;
    private String childId;
    private Double rating; // nullable until approved

    public Task(String title, String description, LocalDate dueDate, int points, TaskType type, String childId) {
        this(UUID.randomUUID().toString(), title, description, dueDate, points, TaskStatus.PENDING, type, childId, null);
    }

    public Task(String id, String title, String description, LocalDate dueDate, int points,
                TaskStatus status, TaskType type, String childId, Double rating) {
        this.id = Objects.requireNonNull(id, "id");
        setTitle(title);
        setDescription(description);
        setDueDate(dueDate);
        setPoints(points);
        this.status = Objects.requireNonNull(status, "status");
        this.type = Objects.requireNonNull(type, "type");
        this.childId = Objects.requireNonNull(childId, "childId");
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate");
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        this.points = points;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = Objects.requireNonNull(childId, "childId");
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

