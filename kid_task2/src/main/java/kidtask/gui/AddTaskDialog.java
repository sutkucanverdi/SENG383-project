package kidtask.gui;

import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.TaskType;
import kidtask.model.User;
import kidtask.persistence.StorageException;
import kidtask.persistence.UserRepository;
import kidtask.service.TaskService;
import kidtask.service.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog for adding a new task (for Parent/Teacher).
 */
public class AddTaskDialog extends JDialog {
    private final User currentUser;
    private final TaskService taskService;
    private final UserRepository userRepository;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField dueDateField;
    private JSpinner pointsSpinner;
    private JComboBox<TaskType> typeComboBox;
    private JComboBox<User> childComboBox;
    private JButton addButton;
    private JButton cancelButton;

    public AddTaskDialog(JFrame parent, User currentUser, TaskService taskService, UserRepository userRepository) {
        super(parent, "Add New Task", true);
        this.currentUser = currentUser;
        this.taskService = taskService;
        this.userRepository = userRepository;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        dueDateField = new JTextField(20);
        dueDateField.setToolTipText("Format: YYYY-MM-DD");

        pointsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        typeComboBox = new JComboBox<>(TaskType.values());
        childComboBox = new JComboBox<>();

        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");

        // Load children
        loadChildren();
    }

    private void loadChildren() {
        try {
            List<User> allUsers = userRepository.loadAll();
            List<User> children = allUsers.stream()
                    .filter(user -> user.getRole() == Role.CHILD)
                    .collect(Collectors.toList());

            childComboBox.removeAllItems();
            for (User child : children) {
                childComboBox.addItem(child);
            }

            if (children.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No children found. Please add a child user first.",
                        "No Children",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (StorageException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading children: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Title
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(titleField, gbc);

        // Description
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        add(new JScrollPane(descriptionArea), gbc);

        // Due Date
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        add(dueDateField, gbc);

        // Points
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Points:"), gbc);
        gbc.gridx = 1;
        add(pointsSpinner, gbc);

        // Type
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        add(typeComboBox, gbc);

        // Child (simplified - would need actual child list)
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Child ID:"), gbc);
        gbc.gridx = 1;
        add(childComboBox, gbc);

        // Buttons
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> handleAdd());
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleAdd() {
        try {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String dueDateStr = dueDateField.getText().trim();
            int points = (Integer) pointsSpinner.getValue();
            TaskType type = (TaskType) typeComboBox.getSelectedItem();
            User selectedChild = (User) childComboBox.getSelectedItem();

            // Validation
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedChild == null) {
                JOptionPane.showMessageDialog(this, "Please select a child", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String childId = selectedChild.getId();

            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(dueDateStr);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            taskService.addTask(title, description, dueDate, points, type, childId);
            JOptionPane.showMessageDialog(this, "Task added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, "Validation error: " + e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding task: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

