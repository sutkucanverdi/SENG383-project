package kidtask.gui;

import kidtask.model.Child;
import kidtask.model.User;
import kidtask.persistence.StorageException;
import kidtask.persistence.UserRepository;
import kidtask.service.LevelService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for displaying child progress: total points, progress bar, and level.
 */
public class ProgressPanel extends JPanel {
    private final User currentUser;
    private final UserRepository userRepository;
    private final LevelService levelService;

    private JLabel totalPointsLabel;
    private JProgressBar progressBar;
    private JLabel levelLabel;
    private JButton refreshButton;
    private JButton updateLevelButton;

    public ProgressPanel(User currentUser, UserRepository userRepository, LevelService levelService) {
        this.currentUser = currentUser;
        this.userRepository = userRepository;
        this.levelService = levelService;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refresh();
    }

    private void initializeComponents() {
        totalPointsLabel = new JLabel("Total Points: 0");
        totalPointsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        // Progress bar: 0-1000 points (or adjust based on max level)
        progressBar = new JProgressBar(0, 1000);
        progressBar.setStringPainted(true);
        progressBar.setString("0 / 1000 points");

        levelLabel = new JLabel("Level: 1");
        levelLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        refreshButton = new JButton("Refresh");
        updateLevelButton = new JButton("Update Level from Ratings");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        // Total Points Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(totalPointsLabel, gbc);

        // Progress Bar
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 20;
        add(progressBar, gbc);

        // Level Label
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.ipady = 0;
        add(levelLabel, gbc);

        // Buttons
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateLevelButton);
        add(buttonPanel, gbc);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> refresh());
        updateLevelButton.addActionListener(e -> handleUpdateLevel());
    }

    private void handleUpdateLevel() {
        try {
            int newLevel = levelService.updateLevelFromRatings(currentUser.getId());
            JOptionPane.showMessageDialog(this,
                    "Level updated to: " + newLevel,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating level: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        try {
            // Reload user to get latest data
            var users = userRepository.loadAll();
            var user = users.stream()
                    .filter(u -> u.getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(null);

            if (!(user instanceof Child child)) {
                totalPointsLabel.setText("Total Points: N/A");
                progressBar.setValue(0);
                progressBar.setString("N/A");
                levelLabel.setText("Level: N/A");
                return;
            }

            // Update display
            int points = child.getPoints();
            int level = child.getLevel();
            double averageRating = child.getAverageRating();

            totalPointsLabel.setText(String.format("Total Points: %d", points));
            progressBar.setValue(Math.min(points, 1000));
            progressBar.setString(String.format("%d / 1000 points", points));
            levelLabel.setText(String.format("Level: %d (Avg Rating: %.2f)", level, averageRating));

        } catch (StorageException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading progress: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

