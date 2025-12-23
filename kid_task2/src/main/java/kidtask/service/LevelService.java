package kidtask.service;

import kidtask.model.Child;
import kidtask.model.User;
import kidtask.persistence.StorageException;
import kidtask.persistence.UserRepository;

import java.util.List;

/**
 * Service for managing child levels based on ratings.
 * Provides deterministic level calculation based on average rating.
 */
public class LevelService {
    private final UserRepository userRepository;

    public LevelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Updates a child's level based on their average rating.
     * 
     * Level calculation rules (deterministic):
     * - Level is calculated from average rating only (not from points)
     * - Formula: level = Math.max(1, Math.floor(averageRating))
     * - This means:
     *   - Average 0.0-0.99 → Level 1
     *   - Average 1.0-1.99 → Level 1
     *   - Average 2.0-2.99 → Level 2
     *   - Average 3.0-3.99 → Level 3
     *   - Average 4.0-4.99 → Level 4
     *   - Average 5.0 → Level 5
     * 
     * Note: Points are managed separately and do not affect level calculation.
     * Level is purely based on performance (average rating).
     *
     * @param childId Child ID whose level should be updated
     * @return New level
     * @throws ValidationException if validation fails
     * @throws NotFoundException   if child not found
     * @throws StorageException   if storage operation fails
     */
    public int updateLevelFromRatings(String childId) {
        // Validation
        if (childId == null || childId.trim().isEmpty()) {
            throw new ValidationException("Child ID cannot be null or empty");
        }

        // Load child
        List<User> users = userRepository.loadAll();
        User user = findUserById(users, childId);

        if (user == null) {
            throw new NotFoundException("Child not found: " + childId);
        }

        if (!(user instanceof Child child)) {
            throw new ValidationException("User is not a child: " + childId);
        }

        // Calculate new level based on average rating
        double averageRating = child.getAverageRating();
        int newLevel = calculateLevelFromRating(averageRating);

        // Update level
        child.setLevel(newLevel);

        // Save
        userRepository.saveAll(users);

        return newLevel;
    }

    /**
     * Calculates level from average rating using deterministic formula.
     * Formula: level = Math.max(1, Math.floor(averageRating))
     * 
     * This ensures:
     * - Minimum level is 1
     * - Level increases by 1 for each full point of average rating
     * - Deterministic: same average rating always produces same level
     *
     * @param averageRating Average rating (0.0 to 5.0)
     * @return Calculated level (1 to 5)
     */
    private int calculateLevelFromRating(double averageRating) {
        // Ensure minimum level of 1
        // Floor the average rating to get integer level
        // Example: 3.7 → floor(3.7) = 3 → max(1, 3) = 3
        // Example: 0.5 → floor(0.5) = 0 → max(1, 0) = 1
        return Math.max(1, (int) Math.floor(averageRating));
    }

    // Helper methods

    private User findUserById(List<User> users, String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}

