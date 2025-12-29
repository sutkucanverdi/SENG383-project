package kidtask.gui;

import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.User;
import kidtask.persistence.StorageException;
import kidtask.persistence.TaskRepository;
import kidtask.persistence.UserRepository;
import kidtask.persistence.WishRepository;
import kidtask.service.LevelService;
import kidtask.service.TaskService;
import kidtask.service.WishService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main dashboard frame with role-based buttons and panels.
 */
public class DashboardFrame extends JFrame {
    private final User currentUser;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final WishRepository wishRepository;
    private final TaskService taskService;
    private final WishService wishService;
    private final LevelService levelService;

    private JTabbedPane tabbedPane;
    private TaskPanel taskPanel;
    private WishPanel wishPanel;
    private ProgressPanel progressPanel;

    public DashboardFrame(User user, UserRepository userRepository) {
        this.currentUser = user;
        this.userRepository = userRepository;
        this.taskRepository = new TaskRepository();
        this.wishRepository = new WishRepository();
        this.taskService = new TaskService(taskRepository, userRepository);
        this.wishService = new WishService(wishRepository, userRepository);
        this.levelService = new LevelService(userRepository);

        initializeComponents();
        setupLayout();
        setupMenuBar();
    }

    private void initializeComponents() {
        setTitle("KidTask - Dashboard (" + currentUser.getName() + " - " + currentUser.getRole() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        taskPanel = new TaskPanel(currentUser, taskService, taskRepository, userRepository, this::refreshAllPanels);
        wishPanel = new WishPanel(currentUser, wishService, this::refreshAllPanels);
        progressPanel = new ProgressPanel(currentUser, userRepository, levelService);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Add tabs based on role
        tabbedPane.addTab("Tasks", taskPanel);
        tabbedPane.addTab("Wishes", wishPanel);

        // Progress panel only for children
        if (currentUser.getRole() == Role.CHILD) {
            tabbedPane.addTab("Progress", progressPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.add(new JLabel("Logged in as: " + currentUser.getName() + " (" + currentUser.getRole() + ")"));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Do you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame(userRepository);
            loginFrame.setVisible(true);
            this.dispose();
        }
    }

    private void refreshAllPanels() {
        taskPanel.refresh();
        wishPanel.refresh();
        if (currentUser.getRole() == Role.CHILD) {
            progressPanel.refresh();
        }
    }
}

