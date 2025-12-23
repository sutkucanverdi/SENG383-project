package kidtask.gui;

import kidtask.model.User;
import kidtask.model.WishType;
import kidtask.service.WishService;
import kidtask.service.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for adding a new wish (for Child).
 */
public class AddWishDialog extends JDialog {
    private final User currentUser;
    private final WishService wishService;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JSpinner costPointsSpinner;
    private JSpinner minLevelSpinner;
    private JComboBox<WishType> typeComboBox;
    private JButton addButton;
    private JButton cancelButton;

    public AddWishDialog(JFrame parent, User currentUser, WishService wishService) {
        super(parent, "Add New Wish", true);
        this.currentUser = currentUser;
        this.wishService = wishService;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setSize(500, 350);
        setLocationRelativeTo(getParent());

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        costPointsSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 10000, 1));
        minLevelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        typeComboBox = new JComboBox<>(WishType.values());

        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
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

        // Cost Points
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(new JLabel("Cost Points:"), gbc);
        gbc.gridx = 1;
        add(costPointsSpinner, gbc);

        // Min Level
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Minimum Level:"), gbc);
        gbc.gridx = 1;
        add(minLevelSpinner, gbc);

        // Type
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        add(typeComboBox, gbc);

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
            int costPoints = (Integer) costPointsSpinner.getValue();
            int minLevel = (Integer) minLevelSpinner.getValue();
            WishType type = (WishType) typeComboBox.getSelectedItem();

            // Validation
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            wishService.addWish(title, description, costPoints, minLevel, type, currentUser.getId());
            JOptionPane.showMessageDialog(this, "Wish added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, "Validation error: " + e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding wish: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

