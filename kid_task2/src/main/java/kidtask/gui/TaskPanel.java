package kidtask.gui;

import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.Task;
import kidtask.model.TaskStatus;
import kidtask.model.TaskType;
import kidtask.model.User;
import kidtask.service.TaskService;
import kidtask.service.ValidationException;

import kidtask.persistence.TaskRepository;
import kidtask.persistence.UserRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for task management with table, filters, and role-based actions.
 */
public class TaskPanel extends JPanel {
    private final User currentUser;
    private final TaskService taskService;
    private final Runnable refreshCallback;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JComboBox<TaskType> filterComboBox;
    private JButton addButton;
    private JButton completeButton;
    private JButton approveButton;
    private JButton refreshButton;

    private static final String[] COLUMN_NAMES = {
            "ID", "Title", "Description", "Due Date", "Points", "Status", "Type", "Rating"
    };

    public TaskPanel(User currentUser, TaskService taskService, TaskRepository taskRepository, UserRepository userRepository, Runnable refreshCallback) {
        this.currentUser = currentUser;
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.refreshCallback = refreshCallback;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refresh();
    }

    private void initializeComponents() {
        // Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Filter
        filterComboBox = new JComboBox<>(new TaskType[]{null, TaskType.DAILY, TaskType.WEEKLY});
        filterComboBox.setSelectedIndex(0);

        // Buttons
        addButton = new JButton("Add Task");
        completeButton = new JButton("Complete Task");
        approveButton = new JButton("Approve & Rate");
        refreshButton = new JButton("Refresh");

        // Show/hide buttons based on role
        if (currentUser.getRole() == Role.CHILD) {
            addButton.setVisible(false);
            approveButton.setVisible(false);
        } else {
            completeButton.setVisible(false);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with filter and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterComboBox);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(addButton);
        topPanel.add(completeButton);
        topPanel.add(approveButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        filterComboBox.addActionListener(e -> refresh());

        addButton.addActionListener(e -> handleAddTask());
        completeButton.addActionListener(e -> handleCompleteTask());
        approveButton.addActionListener(e -> handleApproveTask());
        refreshButton.addActionListener(e -> refresh());
    }

    private void handleAddTask() {
        AddTaskDialog dialog = new AddTaskDialog((JFrame) SwingUtilities.getWindowAncestor(this), currentUser, taskService, userRepository);
        dialog.setVisible(true);
        refresh();
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }

    private void handleCompleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to complete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String taskId = (String) tableModel.getValueAt(selectedRow, 0);
        TaskStatus status = TaskStatus.valueOf((String) tableModel.getValueAt(selectedRow, 5));

        if (status != TaskStatus.NEW) {
            JOptionPane.showMessageDialog(this,
                    "Only NEW tasks can be completed",
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            taskService.completeTask(taskId, currentUser.getId());
            JOptionPane.showMessageDialog(this,
                    "Task marked as completed",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refresh();
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error completing task: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleApproveTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to approve",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String taskId = (String) tableModel.getValueAt(selectedRow, 0);
        TaskStatus status = TaskStatus.valueOf((String) tableModel.getValueAt(selectedRow, 5));

        if (status != TaskStatus.PENDING_APPROVAL) {
            JOptionPane.showMessageDialog(this,
                    "Only PENDING_APPROVAL tasks can be approved",
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ApproveTaskDialog dialog = new ApproveTaskDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                taskId, currentUser.getRole(), taskService);
        dialog.setVisible(true);
        refresh();
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }

    public void refresh() {
        try {
            TaskType filter = (TaskType) filterComboBox.getSelectedItem();
            List<Task> tasks = taskService.listTasks(filter);

            // Clear table
            tableModel.setRowCount(0);

            // Add tasks
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Task task : tasks) {
                Object[] row = {
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getDueDate().format(formatter),
                        task.getPoints(),
                        task.getStatus().name(),
                        task.getType().name(),
                        task.getRating() != null ? String.format("%.1f", task.getRating()) : ""
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading tasks: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

