package kidtask;

import kidtask.gui.LoginFrame;
import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.User;
import kidtask.persistence.StorageException;
import kidtask.persistence.UserRepository;

import javax.swing.*;
import javax.swing.JOptionPane;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main entry point for KidTask application.
 * Initializes data directory and demo users if needed.
 */
public class Main {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = "Users.txt";

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize data directory and demo users
        try {
            initializeDataDirectory();
            initializeDemoUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error initializing application: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Create repositories
        UserRepository userRepository = new UserRepository();

        // Load data on startup
        try {
            List<User> users = userRepository.loadAll();
            System.out.println("Loaded " + users.size() + " users on startup");
        } catch (StorageException e) {
            System.err.println("Warning: Could not load users on startup: " + e.getMessage());
        }

        // Create and show login frame
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(userRepository);
            loginFrame.setVisible(true);
        });
    }

    /**
     * Creates the data directory if it doesn't exist.
     */
    private static void initializeDataDirectory() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                System.out.println("Created data directory: " + dataDir.toAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create data directory", e);
        }
    }

    /**
     * Creates demo users if Users.txt doesn't exist.
     */
    private static void initializeDemoUsers() {
        try {
            Path usersFile = Paths.get(DATA_DIR, USERS_FILE);
            
            // If file exists, don't overwrite
            if (Files.exists(usersFile)) {
                System.out.println("Users.txt already exists, skipping demo user creation");
                return;
            }

            // Create demo users
            List<User> demoUsers = new ArrayList<>();
            
            // Demo Child (default: 0 points, level 1)
            Child child1 = new Child("child1");
            demoUsers.add(child1);
            
            // Demo Parent
            User parent1 = new User(UUID.randomUUID().toString(), "parent1", Role.PARENT);
            demoUsers.add(parent1);
            
            // Demo Teacher
            User teacher1 = new User(UUID.randomUUID().toString(), "teacher1", Role.TEACHER);
            demoUsers.add(teacher1);

            // Save demo users
            UserRepository userRepository = new UserRepository();
            userRepository.saveAll(demoUsers);
            
            System.out.println("Created " + demoUsers.size() + " demo users:");
            System.out.println("  - child1 (CHILD)");
            System.out.println("  - parent1 (PARENT)");
            System.out.println("  - teacher1 (TEACHER)");
        } catch (Exception e) {
            e.printStackTrace(); // <-- GERÇEK HATA BURADA
            JOptionPane.showMessageDialog(
                null,
                "Failed to initialize demo users:\n" + e.getMessage(),
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

    }
}

