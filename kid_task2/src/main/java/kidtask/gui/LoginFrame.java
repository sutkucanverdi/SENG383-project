package kidtask.gui;

import kidtask.model.Role;
import kidtask.model.User;
import kidtask.persistence.StorageException;
import kidtask.persistence.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Login frame for role selection and user selection.
 */
public class LoginFrame extends JFrame {
    private final UserRepository userRepository;
    private User selectedUser;
    private Role selectedRole;

    private JComboBox<Role> roleComboBox;
    private JComboBox<User> userComboBox;
    private JButton loginButton;

    public LoginFrame(UserRepository userRepository) {
        this.userRepository = userRepository;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("KidTask - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        roleComboBox = new JComboBox<>(Role.values());
        userComboBox = new JComboBox<>();
        loginButton = new JButton("Login");

        // Load users for initial role
        updateUserComboBox((Role) roleComboBox.getSelectedItem());
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(roleComboBox, gbc);

        // User selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("User:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(userComboBox, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(loginButton, gbc);
    }

    private void setupEventHandlers() {
        // Role change updates user list
        roleComboBox.addActionListener(e -> {
            Role role = (Role) roleComboBox.getSelectedItem();
            updateUserComboBox(role);
        });

        // Login button
        loginButton.addActionListener(e -> handleLogin());
    }

    private void updateUserComboBox(Role role) {
        try {
            List<User> allUsers = userRepository.loadAll();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> user.getRole() == role)
                    .collect(Collectors.toList());

            userComboBox.removeAllItems();
            for (User user : filteredUsers) {
                userComboBox.addItem(user);
            }

            if (filteredUsers.isEmpty()) {
                userComboBox.addItem(null);
            }
        } catch (StorageException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogin() {
        selectedRole = (Role) roleComboBox.getSelectedItem();
        selectedUser = (User) userComboBox.getSelectedItem();

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open dashboard
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dashboard = new DashboardFrame(selectedUser, userRepository);
            dashboard.setVisible(true);
            this.dispose();
        });
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public Role getSelectedRole() {
        return selectedRole;
    }
}

