package kidtask.gui;

import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.User;
import kidtask.model.Wish;
import kidtask.model.WishStatus;
import kidtask.service.WishService;
import kidtask.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for wish management with list, add (child), and approve/reject (parent).
 */
public class WishPanel extends JPanel {
    private final User currentUser;
    private final WishService wishService;
    private final Runnable refreshCallback;

    private JTable wishTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton refreshButton;

    private static final String[] COLUMN_NAMES = {
            "ID", "Title", "Description", "Cost Points", "Min Level", "Type", "Status", "Requested By"
    };

    public WishPanel(User currentUser, WishService wishService, Runnable refreshCallback) {
        this.currentUser = currentUser;
        this.wishService = wishService;
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
        wishTable = new JTable(tableModel);
        wishTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wishTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Buttons
        addButton = new JButton("Add Wish");
        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");
        refreshButton = new JButton("Refresh");

        // Show/hide buttons based on role
        if (currentUser.getRole() == Role.CHILD) {
            approveButton.setVisible(false);
            rejectButton.setVisible(false);
        } else {
            addButton.setVisible(false);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(addButton);
        topPanel.add(approveButton);
        topPanel.add(rejectButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(wishTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> handleAddWish());
        approveButton.addActionListener(e -> handleApproveWish());
        rejectButton.addActionListener(e -> handleRejectWish());
        refreshButton.addActionListener(e -> refresh());
    }

    private void handleAddWish() {
        AddWishDialog dialog = new AddWishDialog((JFrame) SwingUtilities.getWindowAncestor(this), currentUser, wishService);
        dialog.setVisible(true);
        refresh();
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }

    private void handleApproveWish() {
        int selectedRow = wishTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a wish to approve",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String wishId = (String) tableModel.getValueAt(selectedRow, 0);
        WishStatus status = WishStatus.valueOf((String) tableModel.getValueAt(selectedRow, 6));

        if (status != WishStatus.PENDING) {
            JOptionPane.showMessageDialog(this,
                    "Only PENDING wishes can be approved",
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            wishService.approveWish(wishId, currentUser.getId());
            JOptionPane.showMessageDialog(this,
                    "Wish approved successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refresh();
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error approving wish: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRejectWish() {
        int selectedRow = wishTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a wish to reject",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String wishId = (String) tableModel.getValueAt(selectedRow, 0);
        WishStatus status = WishStatus.valueOf((String) tableModel.getValueAt(selectedRow, 6));

        if (status != WishStatus.PENDING) {
            JOptionPane.showMessageDialog(this,
                    "Only PENDING wishes can be rejected",
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject this wish?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // Note: WishService would need a rejectWish method
            // For now, this is a placeholder
            JOptionPane.showMessageDialog(this,
                    "Reject functionality not yet implemented",
                    "Not Implemented",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refresh() {
        try {
            List<Wish> wishes;

            if (currentUser.getRole() == Role.CHILD && currentUser instanceof Child child) {
                // Show only visible wishes for child
                wishes = wishService.listVisibleWishes(child.getLevel());
            } else {
                // Show all wishes for parent/teacher
                wishes = wishService.listVisibleWishes(100); // High level to show all
            }

            // Clear table
            tableModel.setRowCount(0);

            // Add wishes
            for (Wish wish : wishes) {
                Object[] row = {
                        wish.getId(),
                        wish.getTitle(),
                        wish.getDescription(),
                        wish.getCostPoints(),
                        wish.getMinLevel(),
                        wish.getType().name(),
                        wish.getStatus().name(),
                        wish.getRequestedByChildId()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading wishes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

