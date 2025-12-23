package kidtask.service;

import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.Task;
import kidtask.model.TaskStatus;
import kidtask.model.TaskType;
import kidtask.model.User;
import kidtask.persistence.StorageException;
import kidtask.persistence.TaskRepository;
import kidtask.persistence.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing tasks with business logic and validation.
 */
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a new task for a child.
     *
     * @param title       Task title
     * @param description Task description
     * @param dueDate     Due date
     * @param points      Reward points
     * @param type        Task type (DAILY/WEEKLY)
     * @param childId     ID of the child assigned to this task
     * @return Created task
     * @throws ValidationException if validation fails
     * @throws NotFoundException    if child not found
     * @throws StorageException     if storage operation fails
     */
    public Task addTask(String title, String description, LocalDate dueDate, int points, TaskType type, String childId) {
        // Validation
        validateTitle(title);
        validateDescription(description);
        validateDueDate(dueDate);
        validatePoints(points);
        validateChildExists(childId);

        // Create task
        Task task = new Task(title, description, dueDate, points, type, childId);

        // Save
        List<Task> tasks = taskRepository.loadAll();
        tasks.add(task);
        taskRepository.saveAll(tasks);

        return task;
    }

    /**
     * Lists all tasks, optionally filtered by type.
     *
     * @param type Task type filter (null for all tasks)
     * @return List of tasks
     * @throws StorageException if storage operation fails
     */
    public List<Task> listTasks(TaskType type) {
        List<Task> tasks = taskRepository.loadAll();

        if (type == null) {
            return new ArrayList<>(tasks);
        }

        return tasks.stream()
                .filter(task -> task.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Marks a task as completed by a child.
     *
     * @param taskId  Task ID
     * @param childId Child ID who is completing the task
     * @throws ValidationException if validation fails
     * @throws NotFoundException   if task or child not found
     * @throws StorageException    if storage operation fails
     */
    public void completeTask(String taskId, String childId) {
        // Validation
        validateTaskExists(taskId);
        validateChildExists(childId);

        // Load task
        List<Task> tasks = taskRepository.loadAll();
        Task task = findTaskById(tasks, taskId);

        // Business logic validation
        if (!task.getChildId().equals(childId)) {
            throw new ValidationException("Task does not belong to child: " + childId);
        }

        if (task.getStatus() != TaskStatus.NEW) {
            throw new ValidationException("Task is not in NEW status. Current status: " + task.getStatus());
        }

        // Update status
        task.setStatus(TaskStatus.PENDING_APPROVAL);

        // Save
        taskRepository.saveAll(tasks);
    }

    /**
     * Approves a task and assigns a rating.
     *
     * @param taskId       Task ID
     * @param rating       Rating value (1.0 to 5.0)
     * @param approverRole Role of the approver (PARENT or TEACHER)
     * @throws ValidationException if validation fails
     * @throws NotFoundException   if task or child not found
     * @throws StorageException    if storage operation fails
     */
    public void approveAndRate(String taskId, double rating, Role approverRole) {
        // Validation
        validateRating(rating);
        validateApproverRole(approverRole);
        validateTaskExists(taskId);

        // Load task
        List<Task> tasks = taskRepository.loadAll();
        Task task = findTaskById(tasks, taskId);

        // Business logic validation
        if (task.getStatus() != TaskStatus.PENDING_APPROVAL) {
            throw new ValidationException("Task is not in PENDING_APPROVAL status. Current status: " + task.getStatus());
        }

        // Update task
        task.setStatus(TaskStatus.APPROVED);
        task.setRating(rating);

        // Update child's points and rating
        List<User> users = userRepository.loadAll();
        User user = findUserById(users, task.getChildId());

        if (!(user instanceof Child child)) {
            throw new ValidationException("User is not a child: " + task.getChildId());
        }

        child.addPoints(task.getPoints());
        child.recordRating(rating);

        // Save
        taskRepository.saveAll(tasks);
        userRepository.saveAll(users);
    }

    // Validation methods

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Task title cannot be null or empty");
        }
        if (title.length() > 200) {
            throw new ValidationException("Task title cannot exceed 200 characters");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new ValidationException("Task description cannot exceed 1000 characters");
        }
    }

    private void validateDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            throw new ValidationException("Due date cannot be null");
        }
        // Allow past dates for flexibility (tasks might be added retroactively)
    }

    private void validatePoints(int points) {
        if (points <= 0) {
            throw new ValidationException("Points must be positive");
        }
        if (points > 1000) {
            throw new ValidationException("Points cannot exceed 1000");
        }
    }

    private void validateRating(double rating) {
        if (rating < 1.0 || rating > 5.0) {
            throw new ValidationException("Rating must be between 1.0 and 5.0");
        }
    }

    private void validateApproverRole(Role role) {
        if (role != Role.PARENT && role != Role.TEACHER) {
            throw new ValidationException("Only PARENT or TEACHER can approve tasks");
        }
    }

    private void validateTaskExists(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new ValidationException("Task ID cannot be null or empty");
        }
        List<Task> tasks = taskRepository.loadAll();
        if (findTaskById(tasks, taskId) == null) {
            throw new NotFoundException("Task not found: " + taskId);
        }
    }

    private void validateChildExists(String childId) {
        if (childId == null || childId.trim().isEmpty()) {
            throw new ValidationException("Child ID cannot be null or empty");
        }
        List<User> users = userRepository.loadAll();
        User user = findUserById(users, childId);
        if (user == null) {
            throw new NotFoundException("Child not found: " + childId);
        }
        if (user.getRole() != Role.CHILD) {
            throw new ValidationException("User is not a child: " + childId);
        }
    }

    // Helper methods

    private Task findTaskById(List<Task> tasks, String taskId) {
        return tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);
    }

    private User findUserById(List<User> users, String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}

