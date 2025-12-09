package managers;

import enums.TaskStatus;
import enums.TaskType;
import enums.UserRole;
import exceptions.TaskNotFoundException;
import models.Child;
import models.Task;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskManager {
    private final DataManager dataManager;
    private final Map<String, Task> tasksById = new HashMap<>();
    private final Map<String, Child> childrenById = new HashMap<>();

    public TaskManager(DataManager dataManager) {
        this.dataManager = Objects.requireNonNull(dataManager, "dataManager");
        load();
    }

    private void load() {
        tasksById.clear();
        childrenById.clear();
        dataManager.loadTasks().forEach(task -> tasksById.put(task.getId(), task));
        dataManager.loadChildren().forEach(child -> childrenById.put(child.getId(), child));
    }

    public void addChildIfMissing(Child child) {
        childrenById.putIfAbsent(child.getId(), child);
        dataManager.saveChildren(childrenById.values());
    }

    public Task addTask(String title, String description, LocalDate dueDate, int points, TaskType type, String childId) {
        if (!childrenById.containsKey(childId)) {
            throw new IllegalArgumentException("Unknown child: " + childId);
        }
        Task task = new Task(title, description, dueDate, points, type, childId);
        tasksById.put(task.getId(), task);
        persist();
        return task;
    }

    public List<Task> listTasks() {
        return tasksById.values().stream().toList();
    }

    public List<Task> listTasks(TaskType type) {
        return tasksById.values()
                .stream()
                .filter(t -> t.getType() == type)
                .toList();
    }

    public List<Task> getPendingTasksForChild(String childId) {
        return tasksById.values()
                .stream()
                .filter(t -> t.getChildId().equals(childId))
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .collect(Collectors.toList());
    }

    public void markCompleted(String taskId, String childId) {
        Task task = requireTask(taskId);
        if (!task.getChildId().equals(childId)) {
            throw new IllegalArgumentException("Task does not belong to child " + childId);
        }
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task already completed or approved");
        }
        task.setStatus(TaskStatus.COMPLETED);
        persist();
    }

    public void approveTask(String taskId, UserRole approverRole, double rating) {
        if (approverRole != UserRole.PARENT && approverRole != UserRole.TEACHER) {
            throw new IllegalArgumentException("Only parent or teacher can approve");
        }
        Task task = requireTask(taskId);
        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new IllegalStateException("Task must be completed before approval");
        }
        task.setStatus(TaskStatus.APPROVED);
        task.setRating(rating);

        Child child = getChild(task.getChildId());
        child.recordRating(rating);
        child.addPoints(task.getPoints());

        persist();
    }

    public Child getChild(String childId) {
        Child child = childrenById.get(childId);
        if (child == null) {
            throw new IllegalArgumentException("Unknown child: " + childId);
        }
        return child;
    }

    private Task requireTask(String taskId) {
        Task task = tasksById.get(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId);
        }
        return task;
    }

    private void persist() {
        dataManager.saveTasks(tasksById.values());
        dataManager.saveChildren(childrenById.values());
    }
}

