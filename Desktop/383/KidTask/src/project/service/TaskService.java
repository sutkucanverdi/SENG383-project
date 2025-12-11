package project.service;

import project.model.Task;
import project.util.JsonUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final String FILE = "data/tasks.json";
    private List<Task> tasks = new ArrayList<>();
    private final UserService userService;

    public TaskService(UserService u) {
        this.userService = u;
        loadTasks();
    }

    private void loadTasks() {
        File f = new File(FILE);

        if (!f.exists()) {
            tasks = new ArrayList<>();
            saveTasks();
            return;
        }

        tasks = JsonUtils.load(FILE, JsonUtils.listOf(Task.class));
        if (tasks == null) tasks = new ArrayList<>();
    }

    private void saveTasks() {
        JsonUtils.save(FILE, tasks);
    }

    public void addTask(String kidId, String name, String desc, int points, LocalDate date, String freq, String assignedBy) {
        int id = tasks.size() + 1;

        Task t = new Task(
                id, name, desc, date, points, false, freq, assignedBy, kidId
        );

        tasks.add(t);
        saveTasks();
    }

    public List<Task> getTasksOfKid(String kidId) {
        List<Task> result = new ArrayList<>();

        for (Task t : tasks) {
            if (t.getKidId().equals(kidId))
                result.add(t);
        }

        return result;
    }

    public void completeTask(int taskId) {
        for (Task t : tasks) {
            if (t.getId() == taskId) {
                t.setCompleted(true);
                userService.getUserById(t.getKidId()).addPoints(t.getPoints());
                userService.saveUsers();
                saveTasks();
                return;
            }
        }
    }

    public List<Task> getAllTasks() {
        return tasks;
    }
}
