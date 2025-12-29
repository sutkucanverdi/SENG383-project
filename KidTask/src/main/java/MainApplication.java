import enums.TaskType;
import enums.UserRole;
import managers.DataManager;
import managers.TaskManager;
import managers.WishManager;
import models.Child;
import models.Task;
import models.Wish;

import java.time.LocalDate;
import java.util.List;

public class MainApplication {
    public static void main(String[] args) {
        DataManager dataManager = new DataManager();
        TaskManager taskManager = new TaskManager(dataManager);
        WishManager wishManager = new WishManager(dataManager);

        // Seed a default child so the application can run without GUI input.
        List<Child> existingChildren = dataManager.loadChildren();
        Child child = existingChildren.isEmpty()
                ? new Child("Default Child")
                : existingChildren.get(0);
        taskManager.addChildIfMissing(child);

        // Seed sample data if needed.
        if (taskManager.listTasks().isEmpty()) {
            Task task = taskManager.addTask(
                    "Daily Reading",
                    "Read 20 pages",
                    LocalDate.now().plusDays(1),
                    50,
                    TaskType.DAILY,
                    child.getId()
            );
            System.out.println("Sample task created: " + task.getId());
        }

        if (wishManager.listWishes().isEmpty()) {
            Wish wish = wishManager.addWish(
                    "New Story Book",
                    "Adventure series volume 1",
                    80,
                    1,
                    child.getId()
            );
            System.out.println("Sample wish created: " + wish.getId());
        }

        // Demonstrate a minimal happy-path flow for Task1 scope (no GUI), idempotent across runs.
        Task firstPending = taskManager.getPendingTasksForChild(child.getId())
                .stream()
                .findFirst()
                .orElse(null);
        if (firstPending != null) {
            taskManager.markCompleted(firstPending.getId(), child.getId());
            taskManager.approveTask(firstPending.getId(), UserRole.PARENT, 4.5);
        } else {
            System.out.println("No pending tasks; skipping completion/approval step.");
        }

        System.out.printf("Child points=%d, level=%d, avgRating=%.2f%n",
                taskManager.getChild(child.getId()).getPoints(),
                taskManager.getChild(child.getId()).getLevel(),
                taskManager.getChild(child.getId()).getAverageRating());

        System.out.println("Available wishes for child level " + child.getLevel() + ": "
                + wishManager.listAvailableForChild(child).size());
    }
}

