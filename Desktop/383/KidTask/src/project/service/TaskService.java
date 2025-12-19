package project.service;

import project.model.Task;
import project.model.User;
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

    // ---------------- DOSYA İŞLEMLERİ ----------------
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

    // ---------------- GÖREV EKLEME ----------------
    public void addTask(String kidId, String name, String desc, int points, LocalDate date, String freq, String assignedBy) {
        // Basit ID oluşturma (Daha güvenli yöntem: Max ID + 1)
        int id = (tasks.isEmpty()) ? 1 : tasks.get(tasks.size() - 1).getId() + 1;


        Task t = new Task(
                id, name, desc, date, points, "TODO", freq, assignedBy, kidId
        );

        tasks.add(t);
        saveTasks();
        System.out.println("Task created successfully!");
    }

    // ---------------- LİSTELEME ----------------
    public List<Task> getTasksOfKid(String kidId) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getKidId().equals(kidId))
                result.add(t);
        }
        return result;
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    // ---------------- İŞ MANTIĞI (ÖNEMLİ KISIM) ----------------

    /**
     * ÇOCUK İÇİN:
     * Görevi tamamladığını bildirir. Puan VERMEZ.
     * 
     */
    public void requestCompletion(int taskId) {
        for (Task t : tasks) {
            if (t.getId() == taskId) {
                if ("TODO".equals(t.getStatus())) {
                    t.setStatus("PENDING");
                    saveTasks();
                    System.out.println("Task marked as done. Waiting for approval.");
                } else if ("PENDING".equals(t.getStatus())) {
                    System.out.println("This task is already waiting for approval.");
                } else {
                    System.out.println("Task is already completed.");
                }
                return;
            }
        }
        System.out.println("Task not found with ID: " + taskId);
    }

    /**
     * EBEVEYN/ÖĞRETMEN İÇİN:
     * Bekleyen görevi onaylar ve çocuğa puanı ŞİMDİ verir.
     * Statüyü "PENDING" -> "COMPLETED" yapar.
     */
    public void approveTask(int taskId) {
        for (Task t : tasks) {
            if (t.getId() == taskId) {
                if ("PENDING".equals(t.getStatus())) {
                    
                    // 1. Durumu güncelle
                    t.setStatus("COMPLETED");
                    
                    // 2. Çocuğu bul ve puan ekle
                    User kid = userService.getUserById(t.getKidId());
                    if (kid != null) {
                        kid.addPoints(t.getPoints());
                        userService.saveUsers(); // Puanı dosyaya kaydet
                        System.out.println("Approved! " + t.getPoints() + " points added to " + kid.getName());
                    } else {
                        System.out.println("Error: Kid user not found.");
                    }
                    
                    // 3. Görevi kaydet
                    saveTasks();
                    
                } else {
                    System.out.println("Cannot approve. Task status is: " + t.getStatus());
                    System.out.println("(Task must be 'PENDING' to be approved)");
                }
                return;
            }
        }
        System.out.println("Task not found with ID: " + taskId);
    }
}