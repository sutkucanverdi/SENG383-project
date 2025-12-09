package managers;

import enums.TaskStatus;
import enums.TaskType;
import enums.UserRole;
import exceptions.DataPersistenceException;
import models.Child;
import models.Task;
import models.Wish;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DataManager {
    private final Path baseDir = Paths.get("src", "main", "resources");
    private final Path tasksFile = baseDir.resolve("Tasks.csv");
    private final Path wishesFile = baseDir.resolve("Wishes.csv");
    private final Path usersFile = baseDir.resolve("Users.csv");

    public List<Task> loadTasks() {
        ensureFile(tasksFile, "id,title,description,dueDate,points,status,type,childId,rating");
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(tasksFile)) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] tokens = line.split(",", -1);
                if (tokens.length < 9) continue;
                Task task = new Task(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        LocalDate.parse(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        TaskStatus.valueOf(tokens[5]),
                        TaskType.valueOf(tokens[6]),
                        tokens[7],
                        parseDouble(tokens[8])
                );
                tasks.add(task);
            }
            return tasks;
        } catch (Exception e) {
            throw new DataPersistenceException("Failed to load tasks", e);
        }
    }

    public void saveTasks(Collection<Task> tasks) {
        ensureFile(tasksFile, "id,title,description,dueDate,points,status,type,childId,rating");
        try (BufferedWriter writer = Files.newBufferedWriter(tasksFile)) {
            writer.write("id,title,description,dueDate,points,status,type,childId,rating");
            writer.newLine();
            for (Task task : tasks) {
                writer.write(String.join(",",
                        task.getId(),
                        escape(task.getTitle()),
                        escape(task.getDescription()),
                        task.getDueDate().toString(),
                        Integer.toString(task.getPoints()),
                        task.getStatus().name(),
                        task.getType().name(),
                        task.getChildId(),
                        task.getRating() == null ? "" : Double.toString(task.getRating())
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to save tasks", e);
        }
    }

    public List<Wish> loadWishes() {
        ensureFile(wishesFile, "id,title,description,costPoints,minLevel,approved,requestedByChildId,approvedByUserId");
        List<Wish> wishes = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(wishesFile)) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] tokens = line.split(",", -1);
                if (tokens.length < 8) continue;
                Wish wish = new Wish(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        Boolean.parseBoolean(tokens[5]),
                        tokens[6],
                        tokens[7].isBlank() ? null : tokens[7]
                );
                wishes.add(wish);
            }
            return wishes;
        } catch (Exception e) {
            throw new DataPersistenceException("Failed to load wishes", e);
        }
    }

    public void saveWishes(Collection<Wish> wishes) {
        ensureFile(wishesFile, "id,title,description,costPoints,minLevel,approved,requestedByChildId,approvedByUserId");
        try (BufferedWriter writer = Files.newBufferedWriter(wishesFile)) {
            writer.write("id,title,description,costPoints,minLevel,approved,requestedByChildId,approvedByUserId");
            writer.newLine();
            for (Wish wish : wishes) {
                writer.write(String.join(",",
                        wish.getId(),
                        escape(wish.getTitle()),
                        escape(wish.getDescription()),
                        Integer.toString(wish.getCostPoints()),
                        Integer.toString(wish.getMinLevel()),
                        Boolean.toString(wish.isApproved()),
                        wish.getRequestedByChildId(),
                        Optional.ofNullable(wish.getApprovedByUserId()).orElse("")
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to save wishes", e);
        }
    }

    public List<Child> loadChildren() {
        ensureFile(usersFile, "id,name,role,points,level,ratingSum,ratingCount");
        List<Child> children = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(usersFile)) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] tokens = line.split(",", -1);
                if (tokens.length < 7) continue;
                UserRole role = UserRole.valueOf(tokens[2]);
                if (role != UserRole.CHILD) {
                    continue; // store only children for this manager
                }
                Child child = new Child(
                        tokens[0],
                        tokens[1],
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        Double.parseDouble(tokens[5]),
                        Integer.parseInt(tokens[6])
                );
                children.add(child);
            }
            return children;
        } catch (Exception e) {
            throw new DataPersistenceException("Failed to load users", e);
        }
    }

    public void saveChildren(Collection<Child> children) {
        ensureFile(usersFile, "id,name,role,points,level,ratingSum,ratingCount");
        try (BufferedWriter writer = Files.newBufferedWriter(usersFile)) {
            writer.write("id,name,role,points,level,ratingSum,ratingCount");
            writer.newLine();
            for (Child child : children) {
                writer.write(String.join(",",
                        child.getId(),
                        escape(child.getName()),
                        UserRole.CHILD.name(),
                        Integer.toString(child.getPoints()),
                        Integer.toString(child.getLevel()),
                        Double.toString(child.getRatingSum()),
                        Integer.toString(child.getRatingCount())
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to save users", e);
        }
    }

    private static String escape(String value) {
        return value.replace(",", " "); // naive escape for CSV
    }

    private static Double parseDouble(String raw) {
        return raw == null || raw.isBlank() ? null : Double.parseDouble(raw);
    }

    private void ensureFile(Path path, String header) {
        try {
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(header);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Unable to initialize file: " + path, e);
        }
    }
}

