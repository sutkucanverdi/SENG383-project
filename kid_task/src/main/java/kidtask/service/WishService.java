package kidtask.service;

import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.User;
import kidtask.model.Wish;
import kidtask.model.WishStatus;
import kidtask.model.WishType;
import kidtask.persistence.StorageException;
import kidtask.persistence.UserRepository;
import kidtask.persistence.WishRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing wishes with business logic and validation.
 */
public class WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;

    public WishService(WishRepository wishRepository, UserRepository userRepository) {
        this.wishRepository = wishRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a new wish requested by a child.
     *
     * @param title              Wish title
     * @param description        Wish description
     * @param costPoints         Points required for this wish
     * @param minLevel           Minimum level required
     * @param type               Wish type (PRODUCT/ACTIVITY)
     * @param requestedByChildId ID of the child requesting this wish
     * @return Created wish
     * @throws ValidationException if validation fails
     * @throws NotFoundException   if child not found
     * @throws StorageException    if storage operation fails
     */
    public Wish addWish(String title, String description, int costPoints, int minLevel, WishType type, String requestedByChildId) {
        // Validation
        validateTitle(title);
        validateDescription(description);
        validateCostPoints(costPoints);
        validateMinLevel(minLevel);
        validateChildExists(requestedByChildId);

        // Create wish
        Wish wish = new Wish(title, description, costPoints, minLevel, type, requestedByChildId);

        // Save
        List<Wish> wishes = wishRepository.loadAll();
        wishes.add(wish);
        wishRepository.saveAll(wishes);

        return wish;
    }

    /**
     * Lists wishes visible to a child based on their level.
     * Only shows wishes where child's level >= wish's minLevel.
     *
     * @param childLevel Child's current level
     * @return List of visible wishes
     * @throws ValidationException if validation fails
     * @throws StorageException    if storage operation fails
     */
    public List<Wish> listVisibleWishes(int childLevel) {
        // Validation
        if (childLevel < 1) {
            throw new ValidationException("Child level must be at least 1");
        }

        List<Wish> wishes = wishRepository.loadAll();

        return wishes.stream()
                .filter(wish -> childLevel >= wish.getMinLevel())
                .collect(Collectors.toList());
    }

    /**
     * Approves a wish by a parent.
     *
     * @param wishId        Wish ID
     * @param approverId    ID of the parent approving the wish
     * @throws ValidationException if validation fails
     * @throws NotFoundException   if wish, child, or parent not found
     * @throws StorageException    if storage operation fails
     */
    public void approveWish(String wishId, String approverId) {
        // Validation
        validateWishExists(wishId);
        validateParentExists(approverId);

        // Load wish
        List<Wish> wishes = wishRepository.loadAll();
        Wish wish = findWishById(wishes, wishId);

        // Business logic validation
        if (wish.getStatus() != WishStatus.PENDING) {
            throw new ValidationException("Wish is not in PENDING status. Current status: " + wish.getStatus());
        }

        // Load child
        List<User> users = userRepository.loadAll();
        User childUser = findUserById(users, wish.getRequestedByChildId());

        if (!(childUser instanceof Child child)) {
            throw new ValidationException("User is not a child: " + wish.getRequestedByChildId());
        }

        // Check if child meets requirements
        if (child.getLevel() < wish.getMinLevel()) {
            throw new ValidationException(
                    String.format("Child level %d is below required level %d for this wish", child.getLevel(), wish.getMinLevel()));
        }

        if (child.getPoints() < wish.getCostPoints()) {
            throw new ValidationException(
                    String.format("Child has %d points, but wish requires %d points", child.getPoints(), wish.getCostPoints()));
        }

        // Deduct points and approve wish
        boolean pointsSpent = child.spendPoints(wish.getCostPoints());
        if (!pointsSpent) {
            throw new ValidationException("Failed to spend points for wish");
        }

        wish.setStatus(WishStatus.APPROVED);
        wish.setApprovedByUserId(approverId);

        // Save
        wishRepository.saveAll(wishes);
        userRepository.saveAll(users);
    }

    // Validation methods

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Wish title cannot be null or empty");
        }
        if (title.length() > 200) {
            throw new ValidationException("Wish title cannot exceed 200 characters");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new ValidationException("Wish description cannot exceed 1000 characters");
        }
    }

    private void validateCostPoints(int costPoints) {
        if (costPoints <= 0) {
            throw new ValidationException("Cost points must be positive");
        }
        if (costPoints > 10000) {
            throw new ValidationException("Cost points cannot exceed 10000");
        }
    }

    private void validateMinLevel(int minLevel) {
        if (minLevel < 1) {
            throw new ValidationException("Minimum level must be at least 1");
        }
        if (minLevel > 100) {
            throw new ValidationException("Minimum level cannot exceed 100");
        }
    }

    private void validateChildExists(String childId) {
        if (childId == null || childId.trim().isEmpty()) {
            throw new ValidationException("Child ID cannot be null or empty");
        }
        List<User> users = userRepository.loadAll();
        User user = findUserById(users, childId);
        if (user == null) {
            throw new NotFoundException("Child not found: " + childId);
        }
        if (user.getRole() != Role.CHILD) {
            throw new ValidationException("User is not a child: " + childId);
        }
    }

    private void validateParentExists(String parentId) {
        if (parentId == null || parentId.trim().isEmpty()) {
            throw new ValidationException("Parent ID cannot be null or empty");
        }
        List<User> users = userRepository.loadAll();
        User user = findUserById(users, parentId);
        if (user == null) {
            throw new NotFoundException("Parent not found: " + parentId);
        }
        if (user.getRole() != Role.PARENT) {
            throw new ValidationException("User is not a parent: " + parentId);
        }
    }

    private void validateWishExists(String wishId) {
        if (wishId == null || wishId.trim().isEmpty()) {
            throw new ValidationException("Wish ID cannot be null or empty");
        }
        List<Wish> wishes = wishRepository.loadAll();
        if (findWishById(wishes, wishId) == null) {
            throw new NotFoundException("Wish not found: " + wishId);
        }
    }

    // Helper methods

    private Wish findWishById(List<Wish> wishes, String wishId) {
        return wishes.stream()
                .filter(w -> w.getId().equals(wishId))
                .findFirst()
                .orElse(null);
    }

    private User findUserById(List<User> users, String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}

