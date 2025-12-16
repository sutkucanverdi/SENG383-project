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

    // =====================================================================
    //                            START MENU
    // =====================================================================
    public void start() {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("   KIDTASK SYSTEM (v0.1)   ");
            System.out.println("=============================");
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
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // =====================================================================
    //                            AUTH METHODS
    // =====================================================================
    private void login() {
        System.out.print("\nUsername: ");
        String name = scanner.nextLine();

        System.out.print("Password: ");
        String pw = scanner.nextLine();

        User u = userService.login(name, pw);

        if (u == null) {
            System.out.println("Error: Wrong username or password.");
            return;
        }

        System.out.println("\nLogin Successful! Welcome, " + u.getName());

        switch (u.getRole()) {
            case "kid" -> kidMenu(u);
            case "parent" -> parentMenu(u);
            case "teacher" -> teacherMenu(u);
            default -> System.out.println("Unknown role: " + u.getRole());
        }
    }

    private void registerUser() {
        System.out.println("\n--- NEW USER REGISTRATION ---");
        System.out.println("1) Kid");
        System.out.println("2) Parent");
        System.out.println("3) Teacher");
        System.out.print("Select Role: ");
        String role = scanner.nextLine();

        // ARTIK USERNAME SORUYORUZ
        System.out.print("Enter Username (Login Name): ");
        String name = scanner.nextLine();
        
        System.out.print("Surname: ");
        String surname = scanner.nextLine();
        System.out.print("Password: ");
        String pw = scanner.nextLine();

        User newUser = null;

        try {
            if (role.equals("1")) { // Kid
                System.out.print("Age: ");
                int age = Integer.parseInt(scanner.nextLine());
                System.out.print("Class ID: ");
                String classId = scanner.nextLine();
                newUser = User.createKid(name, surname, age, classId, pw);

            } else if (role.equals("2")) { // Parent
                newUser = User.createParent(name, surname, pw);
                // Not: Çocuk eşleştirmeyi menü içinden yaptıracağız

            } else if (role.equals("3")) { // Teacher
                newUser = User.createTeacher(name, surname, pw);
                System.out.print("How many classes do you have? ");
                int count = Integer.parseInt(scanner.nextLine());
                for (int i = 0; i < count; i++) {
                    System.out.print("Class ID #" + (i + 1) + ": ");
                    newUser.addClass(scanner.nextLine());
                }
            } else {
                System.out.println("Invalid role selection.");
                return;
            }

            if (newUser != null) {
                userService.addUser(newUser);
                System.out.println("Registration Successful! You can login now.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers.");
        }
    }

    // =====================================================================
    //                            KID MENU
    // =====================================================================
    private void kidMenu(User kid) {
        while (true) {
            // Puan ve Level bilgisini güncellemek için user'ı tazeleyelim
            kid = userService.getUserById(kid.getId());

            System.out.println("\n>>> KID DASHBOARD: " + kid.getName());
            System.out.println("Points: " + kid.getPoints() + " | Level: " + kid.getLevel());
            System.out.println("--------------------------------");
            System.out.println("1) View My Tasks");
            System.out.println("2) Request Task Completion (I did it!)");
            System.out.println("3) Add a Wish");
            System.out.println("4) View My Wishes");
            System.out.println("5) Buy a Wish");
            System.out.println("0) Logout");
            System.out.print("Choice: ");

            String ch = scanner.nextLine();

            switch (ch) {
                case "1" -> showTasks(kid.getId());
                case "2" -> completeTaskKid(kid);
                case "3" -> addWishKid(kid);
                case "4" -> showWishes(kid.getId());
                case "5" -> purchaseWishKid(kid);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showTasks(String kidId) {
        List<Task> tasks = taskService.getTasksOfKid(kidId);
        if (tasks.isEmpty()) System.out.println("No tasks found.");
        else {
            System.out.println("\n--- MY TASKS ---");
            tasks.forEach(System.out::println);
        }
    }

    private void completeTaskKid(User kid) {
        showTasks(kid.getId());
        System.out.print("Enter Task ID to mark as done: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            taskService.requestCompletion(id); 
        } catch (Exception e) {
            System.out.println("Invalid ID.");
        }
    }

    private void addWishKid(User kid) {
        System.out.print("Wish Name: ");
        String name = scanner.nextLine();
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        wishService.addWishFromKid(kid.getId(), name, desc);
        System.out.println("Wish added! Wait for approval.");
    }

    private void showWishes(String kidId) {
        List<Wish> wishes = wishService.getWishesOfKid(kidId);
        if (wishes.isEmpty()) System.out.println("No wishes found.");
        else wishes.forEach(System.out::println);
    }

    private void purchaseWishKid(User kid) {
        showWishes(kid.getId());
        System.out.print("Enter Wish ID to buy: ");
        try {
            int wid = Integer.parseInt(scanner.nextLine());
            boolean ok = wishService.purchaseWish(kid.getId(), wid);
            if (ok) System.out.println("Purchase Successful! Enjoy!");
            else System.out.println("Failed: Not enough points, level, or not approved.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    // =====================================================================
    //                          PARENT MENU
    // =====================================================================
    private void parentMenu(User parent) {
        while (true) {
            System.out.println("\n>>> PARENT DASHBOARD: " + parent.getName());
            System.out.println("1) View My Kids");
            System.out.println("2) Assign Task (By Username)");
            System.out.println("3) Approve Wishes");
            System.out.println("4) Approve Completed Tasks");
            System.out.println("5) Link Existing Child Account");
            System.out.println("0) Logout");
            System.out.print("Choice: ");

            String ch = scanner.nextLine();

            switch (ch) {
                case "1" -> showChildren(parent);
                case "2" -> assignTask(parent, "Parent");
                case "3" -> approveWishes(parent);
                case "4" -> approveTasksParent(parent);
                case "5" -> linkChildToParent(parent);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showChildren(User parent) {
        List<User> kids = userService.getChildrenOfParent(parent.getId());
        if (kids.isEmpty()) System.out.println("No children linked yet. Use option 5.");
        else kids.forEach(System.out::println);
    }

    // --- YENİLENMİŞ GÖREV ATAMA (İSİMLE) ---
    private void assignTask(User assigner, String roleName) {
        List<User> kids;
        if (roleName.equals("Parent")) kids = userService.getChildrenOfParent(assigner.getId());
        else kids = userService.getKidsOfTeacher(assigner.getId());

        if (kids.isEmpty()) {
            System.out.println("No kids found to assign tasks.");
            return;
        }

        System.out.println("\n--- Available Kids/Students ---");
        for(User k : kids) {
            System.out.println("- " + k.getName() + " (Level: " + k.getLevel() + ")");
        }

        System.out.print("\nEnter Child Username to assign task: ");
        String targetName = scanner.nextLine();

        // İsimden ID bulma (UserService'e eklediğin metod)
        User targetKid = userService.getKidByUsername(targetName);
        
        // Güvenlik: Bu çocuk benim listemde mi?
        boolean isMine = kids.stream().anyMatch(k -> k.getName().equalsIgnoreCase(targetName));

        if (targetKid == null || !isMine) {
            System.out.println("Error: Child not found in your list. Check spelling.");
            return;
        }

        System.out.print("Task Name: ");
        String tName = scanner.nextLine();
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        
        try {
            System.out.print("Points: ");
            int pts = Integer.parseInt(scanner.nextLine());
            System.out.print("Frequency (Daily/Weekly): ");
            String freq = scanner.nextLine();
            System.out.print("Due Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            // Arka planda ID kullanarak kaydediyoruz
            taskService.addTask(targetKid.getId(), tName, desc, pts, date, freq, assigner.getName());
            
        } catch (Exception e) {
            System.out.println("Error: Invalid date or number format.");
        }
    }

    private void approveWishes(User parent) {
        List<User> kids = userService.getChildrenOfParent(parent.getId());
        boolean foundAny = false;

        System.out.println("\n--- Wishes Pending Approval ---");
        for (User kid : kids) {
            for (Wish w : wishService.getWishesOfKid(kid.getId())) {
                if (!w.isApproved()) {
                    System.out.println("Kid: " + kid.getName() + " -> " + w);
                    foundAny = true;
                }
            }
        }

        if (!foundAny) {
            System.out.println("No pending wishes.");
            return;
        }

        try {
            System.out.print("Wish ID to approve: ");
            int wid = Integer.parseInt(scanner.nextLine());
            System.out.print("Required Points: ");
            int reqPts = Integer.parseInt(scanner.nextLine());
            System.out.print("Required Level: ");
            int reqLvl = Integer.parseInt(scanner.nextLine());

            wishService.approveWish(wid, reqPts, reqLvl);
            System.out.println("Wish Approved!");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private void approveTasksParent(User parent) {
        List<User> kids = userService.getChildrenOfParent(parent.getId());
        boolean foundAny = false;

        System.out.println("\n--- Tasks Waiting for Approval ---");
        for (User kid : kids) {
            for (Task t : taskService.getTasksOfKid(kid.getId())) {
                if ("PENDING".equals(t.getStatus())) {
                    System.out.println("Kid: " + kid.getName() + " -> " + t);
                    foundAny = true;
                }
            }
        }

        if (!foundAny) {
            System.out.println("No tasks waiting for approval.");
            return;
        }

        System.out.print("Enter Task ID to Approve: ");
        try {
            int tid = Integer.parseInt(scanner.nextLine());
            taskService.approveTask(tid);
        } catch (Exception e) {
            System.out.println("Invalid ID.");
        }
    }

    private void linkChildToParent(User parent) {
        System.out.println("\n--- Link Child Account ---");
        System.out.print("Enter Child's Username: ");
        String name = scanner.nextLine();

        if (userService.linkChildByUsername(parent, name)) {
            System.out.println("Success! " + name + " is now linked.");
        } else {
            System.out.println("Error: Child not found or already linked.");
        }
    }

    // =====================================================================
    //                          TEACHER MENU
    // =====================================================================
    private void teacherMenu(User teacher) {
        while (true) {
            System.out.println("\n>>> TEACHER DASHBOARD: " + teacher.getName());
            System.out.println("1) View My Students");
            System.out.println("2) Assign Task");
            System.out.println("3) Approve Completed Tasks");
            System.out.println("0) Logout");
            System.out.print("Choice: ");

            String ch = scanner.nextLine();

            switch (ch) {
                case "1" -> showStudents(teacher);
                case "2" -> assignTask(teacher, "Teacher");
                case "3" -> approveTasksTeacher(teacher);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showStudents(User teacher) {
        List<User> students = userService.getKidsOfTeacher(teacher.getId());
        if (students.isEmpty()) System.out.println("No students found.");
        else students.forEach(System.out::println);
    }

    private void approveTasksTeacher(User teacher) {
        List<User> students = userService.getKidsOfTeacher(teacher.getId());
        boolean foundAny = false;

        System.out.println("\n--- Student Tasks Pending Approval ---");
        for (User kid : students) {
            for (Task t : taskService.getTasksOfKid(kid.getId())) {
                if ("PENDING".equals(t.getStatus()) && teacher.getName().equals(t.getAssignedBy())) {
                    System.out.println("Student: " + kid.getName() + " -> " + t);
                    foundAny = true;
                }
            }
        }

        if (!foundAny) {
            System.out.println("No pending tasks assigned by you.");
            return;
        }

        System.out.print("Enter Task ID to Approve: ");
        try {
            int tid = Integer.parseInt(scanner.nextLine());
            taskService.approveTask(tid);
        } catch (Exception e) {
            System.out.println("Invalid ID.");
        }
    }
}