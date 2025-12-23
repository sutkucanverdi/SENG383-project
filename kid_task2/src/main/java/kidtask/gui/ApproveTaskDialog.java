package kidtask.gui;

import kidtask.model.Role;
import kidtask.service.TaskService;
import kidtask.service.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for approving and rating a task (for Parent/Teacher).
 */
public class ApproveTaskDialog extends JDialog {
    private final String taskId;
    private final Role approverRole;
    private final TaskService taskService;

    private JSpinner ratingSpinner;
    private JButton approveButton;
    private JButton cancelButton;

    public ApproveTaskDialog(JFrame parent, String taskId, Role approverRole, TaskService taskService) {
        super(parent, "Approve Task", true);
        this.taskId = taskId;
        this.approverRole = approverRole;
        this.taskService = taskService;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setSize(300, 150);
        setLocationRelativeTo(getParent());

        // Rating spinner: 1.0 to 5.0 with 0.1 step
        SpinnerNumberModel ratingModel = new SpinnerNumberModel(5.0, 1.0, 5.0, 0.1);
        ratingSpinner = new JSpinner(ratingModel);

        approveButton = new JButton("Approve");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Rating label and spinner
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Rating (1.0 - 5.0):"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(ratingSpinner, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(approveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);
    }

    private void setupEventHandlers() {
        approveButton.addActionListener(e -> handleApprove());
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleApprove() {
        try {
            double rating = ((Double) ratingSpinner.getValue());

            taskService.approveAndRate(taskId, rating, approverRole);
            JOptionPane.showMessageDialog(this,
                    "Task approved and rated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this,
                    "Validation error: " + e.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error approving task: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

