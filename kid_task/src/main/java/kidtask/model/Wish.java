package kidtask.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Wish entity representing a wish requested by a child.
 */
public class Wish {
    private String id;
    private String title;
    private String description;
    private int costPoints;
    private int minLevel;
    private WishType type;
    private WishStatus status;
    private String requestedByChildId;
    private String approvedByUserId;

    /**
     * Constructor for creating a new wish with auto-generated ID.
     *
     * @param title              Wish title
     * @param description        Wish description
     * @param costPoints         Points required for this wish
     * @param minLevel           Minimum level required
     * @param type               Wish type (PRODUCT/ACTIVITY)
     * @param requestedByChildId ID of the child requesting this wish
     */
    public Wish(String title, String description, int costPoints, int minLevel, WishType type, String requestedByChildId) {
        this(UUID.randomUUID().toString(), title, description, costPoints, minLevel, type, WishStatus.PENDING, requestedByChildId, null);
    }

    /**
     * Full constructor for creating a wish with all fields.
     *
     * @param id                 Wish ID (UUID)
     * @param title              Wish title
     * @param description        Wish description
     * @param costPoints         Points required for this wish
     * @param minLevel           Minimum level required
     * @param type               Wish type (PRODUCT/ACTIVITY)
     * @param status             Wish status
     * @param requestedByChildId ID of the child requesting this wish
     * @param approvedByUserId   ID of the user who approved/rejected (nullable)
     */
    public Wish(String id, String title, String description, int costPoints, int minLevel,
                WishType type, WishStatus status, String requestedByChildId, String approvedByUserId) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = description != null ? description : "";
        this.costPoints = costPoints;
        this.minLevel = minLevel;
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.requestedByChildId = Objects.requireNonNull(requestedByChildId, "Requested by child ID cannot be null");
        this.approvedByUserId = approvedByUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public int getCostPoints() {
        return costPoints;
    }

    public void setCostPoints(int costPoints) {
        this.costPoints = costPoints;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public WishType getType() {
        return type;
    }

    public void setType(WishType type) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
    }

    public WishStatus getStatus() {
        return status;
    }

    public void setStatus(WishStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public String getRequestedByChildId() {
        return requestedByChildId;
    }

    public void setRequestedByChildId(String requestedByChildId) {
        this.requestedByChildId = Objects.requireNonNull(requestedByChildId, "Requested by child ID cannot be null");
    }

    public String getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(String approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wish wish = (Wish) o;
        return Objects.equals(id, wish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Wish{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", costPoints=" + costPoints +
                ", minLevel=" + minLevel +
                ", type=" + type +
                ", status=" + status +
                ", requestedByChildId='" + requestedByChildId + '\'' +
                ", approvedByUserId='" + approvedByUserId + '\'' +
                '}';
    }
}

