package models;

import enums.UserRole;

import java.util.Objects;

public class Child extends User {
    private int points;
    private int level;
    private double ratingSum;
    private int ratingCount;

    public Child(String name) {
        this(null, name, 0, 1, 0, 0);
    }

    public Child(String id, String name, int points, int level, double ratingSum, int ratingCount) {
        super(id, name, UserRole.CHILD);
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

    public double getAverageRating() {
        return ratingCount == 0 ? 0 : ratingSum / ratingCount;
    }

    public double getRatingSum() {
        return ratingSum;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void recordRating(double rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        ratingSum += rating;
        ratingCount++;
    }

    public void addPoints(int additionalPoints) {
        if (additionalPoints < 0) {
            throw new IllegalArgumentException("Cannot add negative points");
        }
        points += additionalPoints;
        updateLevelFromPoints();
    }

    public boolean spendPoints(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        if (points < cost) {
            return false;
        }
        points -= cost;
        updateLevelFromPoints();
        return true;
    }

    private void updateLevelFromPoints() {
        // Simple progression: 100 points per level after level 1.
        level = Math.max(1, 1 + points / 100);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Child child)) return false;
        return Objects.equals(getId(), child.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

