package models;

import java.util.Objects;
import java.util.UUID;

public class Wish {
    private final String id;
    private String title;
    private String description;
    private int costPoints;
    private int minLevel;
    private boolean approved;
    private String requestedByChildId;
    private String approvedByUserId;

    public Wish(String title, String description, int costPoints, int minLevel, String requestedByChildId) {
        this(UUID.randomUUID().toString(), title, description, costPoints, minLevel, false, requestedByChildId, null);
    }

    public Wish(String id, String title, String description, int costPoints, int minLevel,
                boolean approved, String requestedByChildId, String approvedByUserId) {
        this.id = Objects.requireNonNull(id, "id");
        setTitle(title);
        setDescription(description);
        setCostPoints(costPoints);
        setMinLevel(minLevel);
        this.approved = approved;
        this.requestedByChildId = Objects.requireNonNull(requestedByChildId, "requestedByChildId");
        this.approvedByUserId = approvedByUserId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Wish title is required");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public int getCostPoints() {
        return costPoints;
    }

    public void setCostPoints(int costPoints) {
        if (costPoints <= 0) {
            throw new IllegalArgumentException("Cost must be positive");
        }
        this.costPoints = costPoints;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        if (minLevel < 1) {
            throw new IllegalArgumentException("Minimum level must be >= 1");
        }
        this.minLevel = minLevel;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getRequestedByChildId() {
        return requestedByChildId;
    }

    public void setRequestedByChildId(String requestedByChildId) {
        this.requestedByChildId = Objects.requireNonNull(requestedByChildId, "requestedByChildId");
    }

    public String getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(String approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }
}

