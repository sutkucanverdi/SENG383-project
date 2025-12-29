package kidtask.model;

import java.util.UUID;


/**
 * Child entity representing a child user with points, level, and rating tracking.
 */
public class Child extends User {
    private int points;
    private int level;
    private double ratingSum;
    private int ratingCount;

    /**
     * Constructor for creating a new child with default values.
     *
     * @param name Child's name
     */
    public Child(String name) {
        this(UUID.randomUUID().toString(), name, 0, 1, 0.0, 0);
    }


    /**
     * Full constructor for creating a child with all fields.
     *
     * @param id          Child ID (UUID)
     * @param name        Child's name
     * @param points      Current points
     * @param level       Current level
     * @param ratingSum   Sum of all ratings
     * @param ratingCount Number of ratings received
     */
    public Child(String id, String name, int points, int level, double ratingSum, int ratingCount) {
        super(id, name, Role.CHILD);
        setPoints(points);
        setLevel(level);
        this.ratingSum = ratingSum;
        this.ratingCount = ratingCount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.points = points;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }
        this.level = level;
    }

    public double getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(double ratingSum) {
        if (ratingSum < 0) {
            throw new IllegalArgumentException("Rating sum cannot be negative");
        }
        this.ratingSum = ratingSum;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        if (ratingCount < 0) {
            throw new IllegalArgumentException("Rating count cannot be negative");
        }
        this.ratingCount = ratingCount;
    }

    /**
     * Calculates and returns the average rating.
     *
     * @return Average rating, or 0.0 if no ratings exist
     */
    public double getAverageRating() {
        return ratingCount == 0 ? 0.0 : ratingSum / ratingCount;
    }

    /**
     * Adds points to the child and updates level.
     *
     * @param additionalPoints Points to add (must be >= 0)
     */
    public void addPoints(int additionalPoints) {
        if (additionalPoints < 0) {
            throw new IllegalArgumentException("Cannot add negative points");
        }
        this.points += additionalPoints;
        updateLevelFromPoints();
    }

    /**
     * Spends points if sufficient balance exists.
     *
     * @param cost Points to spend
     * @return true if points were spent, false if insufficient balance
     */
    public boolean spendPoints(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        if (points < cost) {
            return false;
        }
        this.points -= cost;
        updateLevelFromPoints();
        return true;
    }

    /**
     * Records a rating and updates sum and count.
     *
     * @param rating Rating value (1.0 to 5.0)
     */
    public void recordRating(double rating) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        this.ratingSum += rating;
        this.ratingCount++;
    }

    /**
     * Updates level based on points.
     * Formula: level = 1 + (points / 100)
     */
    private void updateLevelFromPoints() {
        this.level = Math.max(1, 1 + (points / 100));
    }
}

