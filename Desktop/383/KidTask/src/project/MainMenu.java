package project;

import project.model.User;
import project.model.Task;
import project.model.Wish;
import project.service.UserService;
import project.service.TaskService;
import project.service.WishService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final UserService userService = new UserService();
    private final TaskService taskService = new TaskService(userService);
    private final WishService wishService = new WishService(userService);

    // ----------------------------------------------------
    //                  START MENU
    // ----------------------------------------------------
    public void start() {
        while (true) {
            System.out.println("\n=== KidTask System ===");
            System.out.println("1) Login");
            System.out.println("2) Register");
            System.out.println("0) Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> login();
                case "2" -> registerUser();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ----------------------------------------------------
    //                  LOGIN
    // ----------------------------------------------------
    private void login() {
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Password: ");
        String pw = scanner.nextLine();

        User u = userService.login(name, pw);

        if (u == null) {
            System.out.println("Wrong login.");
            return;
        }

        switch (u.getRole()) {
            case "kid" -> kidMenu(u);
            case "parent" -> parentMenu(u);
            case "teacher" -> teacherMenu(u);
        }
    }

    // ----------------------------------------------------
    //                REGISTER USER
    // ----------------------------------------------------
    private void registerUser() {
        System.out.println("\nRegister as: ");
        System.out.println("1) Kid");
        System.out.println("2) Parent");
        System.out.println("3) Teacher");
        System.out.print("Choice: ");

        String role = scanner.nextLine();

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Surname: ");
        String surname = scanner.nextLine();

        System.out.print("Password: ");
        String pw = scanner.nextLine();

        User newUser = null;

        if (role.equals("1")) {
            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());

            System.out.print("Class ID: ");
            String classId = scanner.nextLine();

            newUser = User.createKid(name, surname, age, classId, pw);
        }
        else if (role.equals("2")) {
            newUser = User.createParent(name, surname, pw);
        }
        else if (role.equals("3")) {
            newUser = User.createTeacher(name, surname, pw);

            System.out.print("How many classes does this teacher have? ");
            int count = Integer.parseInt(scanner.nextLine());

            for (int i = 0; i < count; i++) {
                System.out.print("Class ID " + (i + 1) + ": ");
                newUser.addClass(scanner.nextLine());
            }
        }

        if (newUser != null) {
            userService.addUser(newUser);
            System.out.println("User registered!");
        }
    }


    // =====================================================================
    //                       KID MENU
    // =====================================================================
    private void kidMenu(User kid) {
        while (true) {
            System.out.println("\n=== Kid Menu ===");
            System.out.println("Welcome " + kid.getName());
            System.out.println("Points: " + kid.getPoints() + " | Level: " + kid.getLevel());
            System.out.println("1) View Tasks");
            System.out.println("2) Complete Task");
            System.out.println("3) Add Wish");
            System.out.println("4) View My Wishes");
            System.out.println("5) Buy a Wish");
            System.out.println("0) Logout");

            System.out.print("Choice: ");
            String ch = scanner.nextLine();

            switch (ch) {
                case "1" -> {
                    List<Task> tasks = taskService.getTasksOfKid(kid.getId());
                    if (tasks.isEmpty()) System.out.println("No tasks.");
                    tasks.forEach(System.out::println);
                }
                case "2" -> completeTaskKid(kid);
                case "3" -> addWishKid(kid);
                case "4" -> {
                    List<Wish> wishes = wishService.getWishesOfKid(kid.getId());
                    wishes.forEach(System.out::println);
                }
                case "5" -> purchaseWishKid(kid);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Kid → complete a task
    private void completeTaskKid(User kid) {
        List<Task> tasks = taskService.getTasksOfKid(kid.getId());

        if (tasks.isEmpty()) {
            System.out.println("No tasks.");
            return;
        }

        tasks.forEach(System.out::println);

        System.out.print("Task ID to complete: ");
        int id = Integer.parseInt(scanner.nextLine());

        taskService.completeTask(id);
        System.out.println("Task completed!");
    }

    // Kid → add wish
    private void addWishKid(User kid) {
    System.out.print("Wish name: ");
    String name = scanner.nextLine();

    System.out.print("Description: ");
    String desc = scanner.nextLine();

    wishService.addWishFromKid(kid.getId(), name, desc);
    System.out.println("Wish added! Waiting for parent/teacher approval.");
}


    // Kid → purchase wish
    private void purchaseWishKid(User kid) {
        List<Wish> list = wishService.getWishesOfKid(kid.getId());

        System.out.println("\n--- Purchasable Wishes ---");
        for (Wish w : list) {
            if (w.isApproved() && !w.isPurchased()) {
                System.out.println(w);
            }
        }

        System.out.print("Wish ID to purchase: ");
        int wid = Integer.parseInt(scanner.nextLine());

        boolean ok = wishService.purchaseWish(kid.getId(), wid);
        System.out.println(ok ? "Purchased!" : "Cannot purchase.");
    }

    // =====================================================================
    //                       PARENT MENU
    // =====================================================================
    private void parentMenu(User parent) {
        while (true) {
            System.out.println("\n=== Parent Menu ===");
            System.out.println("1) View My Kids");
            System.out.println("2) Assign Task to a Kid");
            System.out.println("3) Approve Wishes");
            System.out.println("0) Logout");

            System.out.print("Choice: ");
            String ch = scanner.nextLine();

            switch (ch) {
                case "1" -> showParentKids(parent);
                case "2" -> assignTaskParent(parent);
                case "3" -> approveWishes(parent);
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private void showParentKids(User parent) {
        var kids = userService.getChildrenOfParent(parent.getId());
        kids.forEach(System.out::println);
    }

    private void assignTaskParent(User parent) {
        var kids = userService.getChildrenOfParent(parent.getId());
        if (kids.isEmpty()) {
            System.out.println("You have no kids registered.");
            return;
        }

        kids.forEach(System.out::println);

        System.out.print("Kid ID: ");
        String kid = scanner.nextLine();

        System.out.print("Task name: ");
        String name = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        System.out.print("Points: ");
        int p = Integer.parseInt(scanner.nextLine());

        System.out.print("Frequency (daily/weekly/none): ");
        String freq = scanner.nextLine();

        System.out.print("Due date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        taskService.addTask(kid, name, desc, p, date, freq, parent.getName());
        System.out.println("Task added!");
    }

private void approveWishes(User parent) {

    var kids = userService.getChildrenOfParent(parent.getId());

    System.out.println("\n--- Pending Wishes ---");

    for (var kid : kids) {
        var wishes = wishService.getWishesOfKid(kid.getId());
        for (var w : wishes) {
            if (!w.isApproved()) {
                System.out.println(w);
            }
        }
    }

    System.out.print("Wish ID to approve: ");
    int id = Integer.parseInt(scanner.nextLine());

    System.out.print("Required points: ");
    int pts = Integer.parseInt(scanner.nextLine());

    System.out.print("Required level: ");
    int lv = Integer.parseInt(scanner.nextLine());

    wishService.approveWish(id, pts, lv);

    System.out.println("Wish approved!");
}


    // =====================================================================
    //                       TEACHER MENU
    // =====================================================================
    private void teacherMenu(User teacher) {
        while (true) {
            System.out.println("\n=== Teacher Menu ===");
            System.out.println("1) View My Students");
            System.out.println("2) Assign Task");
            System.out.println("3) Approve Wishes");
            System.out.println("0) Logout");

            System.out.print("Choice: ");
            String ch = scanner.nextLine();

            switch (ch) {
                case "1" -> teacherViewStudents(teacher);
                case "2" -> teacherAssignTask(teacher);
                case "3" -> teacherApproveWishes(teacher);
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private void teacherViewStudents(User teacher) {
        var kids = userService.getKidsOfTeacher(teacher.getId());
        kids.forEach(System.out::println);
    }

    private void teacherAssignTask(User teacher) {
        var kids = userService.getKidsOfTeacher(teacher.getId());
        kids.forEach(System.out::println);

        System.out.print("Kid ID: ");
        String kid = scanner.nextLine();

        System.out.print("Task name: ");
        String name = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        System.out.print("Points: ");
        int p = Integer.parseInt(scanner.nextLine());

        System.out.print("Frequency: ");
        String freq = scanner.nextLine();

        System.out.print("Due date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        taskService.addTask(kid, name, desc, p, date, freq, teacher.getName());
        System.out.println("Task assigned!");
    }

    private void teacherApproveWishes(User teacher) {
        var kids = userService.getKidsOfTeacher(teacher.getId());

        for (var kid : kids) {
            var wishes = wishService.getWishesOfKid(kid.getId());
            for (var w : wishes) {
                if (!w.isApproved()) {
                    System.out.println(w);
                }
            }
        }

        System.out.print("Wish ID to approve: ");
int id = Integer.parseInt(scanner.nextLine());

System.out.print("Required points: ");
int pts = Integer.parseInt(scanner.nextLine());

System.out.print("Required level: ");
int lvl = Integer.parseInt(scanner.nextLine());

wishService.approveWish(id, pts, lvl);

System.out.println("Wish approved!");

    }
}
