package project.model;

import java.time.LocalDate;

public class Task {

    private int id;
    private String taskName;
    private String description;
    private LocalDate dueDate;
    private int points;
    private boolean completed;

    private String frequency;
    private String assignedBy;

    private String kidId;   // önemli!

    public Task() {}

    public Task(int id, String name, String desc, LocalDate date,
                int points, boolean completed, String freq,
                String assignedBy, String kidId) {

        this.id = id;
        this.taskName = name;
        this.description = desc;
        this.dueDate = date;
        this.points = points;
        this.completed = completed;
        this.frequency = freq;
        this.assignedBy = assignedBy;
        this.kidId = kidId;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getTaskName() { return taskName; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public int getPoints() { return points; }
    public boolean isCompleted() { return completed; }
    public String getFrequency() { return frequency; }
    public String getAssignedBy() { return assignedBy; }
    public String getKidId() { return kidId; }

    // --- SETTERS ---
    public void setCompleted(boolean val) { this.completed = val; }

    @Override
    public String toString() {
        return "Task #" + id + " | " + taskName +
                " | Points: " + points +
                " | Completed: " + completed;
    }
}
