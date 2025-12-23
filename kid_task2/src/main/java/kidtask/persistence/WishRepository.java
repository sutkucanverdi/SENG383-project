package kidtask.persistence;

import kidtask.model.Wish;
import kidtask.model.WishStatus;
import kidtask.model.WishType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Wish entities, handles loading and saving wishes from/to file storage.
 */
public class WishRepository {
    private static final String FILE_NAME = "Wishes.txt";
    private static final String HEADER = "id|title|description|costPoints|minLevel|type|status|requestedByChildId|approvedByUserId";

    private final FileStorage fileStorage;

    public WishRepository() {
        this.fileStorage = new FileStorage(FILE_NAME);
    }

    /**
     * Loads all wishes from the storage file.
     *
     * @return List of all wishes
     * @throws StorageException if file operations fail
     */
    public List<Wish> loadAll() throws StorageException {
        List<List<String>> rows = fileStorage.readAll(HEADER);
        List<Wish> wishes = new ArrayList<>();

        for (List<String> row : rows) {
            try {
                Wish wish = parseWish(row);
                wishes.add(wish);
            } catch (Exception e) {
                // Skip invalid rows, log error
                System.err.println("Skipping invalid wish row: " + row + " - " + e.getMessage());
            }
        }

        return wishes;
    }

    /**
     * Saves all wishes to the storage file.
     *
     * @param wishes List of wishes to save
     * @throws StorageException if file operations fail
     */
    public void saveAll(List<Wish> wishes) throws StorageException {
        List<List<String>> rows = new ArrayList<>();

        for (Wish wish : wishes) {
            List<String> row = formatWish(wish);
            rows.add(row);
        }

        fileStorage.writeAll(HEADER, rows);
    }

    /**
     * Generates a new unique ID for a wish.
     *
     * @return New UUID string
     */
    public String nextId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Parses a row of data into a Wish object.
     *
     * @param row List of field values
     * @return Wish object
     */
    private Wish parseWish(List<String> row) {
        if (row.size() < 9) {
            throw new IllegalArgumentException("Invalid wish row: insufficient fields");
        }

        String id = row.get(0);
        String title = row.get(1);
        String description = row.get(2);
        int costPoints = Integer.parseInt(row.get(3));
        int minLevel = Integer.parseInt(row.get(4));
        WishType type = WishType.valueOf(row.get(5).toUpperCase());
        WishStatus status = WishStatus.valueOf(row.get(6).toUpperCase());
        String requestedByChildId = row.get(7);
        String approvedByUserId = parseString(row.get(8));

        return new Wish(id, title, description, costPoints, minLevel, type, status, requestedByChildId, approvedByUserId);
    }

    /**
     * Formats a Wish object into a row of data.
     *
     * @param wish Wish object
     * @return List of field values
     */
    private List<String> formatWish(Wish wish) {
        List<String> row = new ArrayList<>();
        row.add(wish.getId());
        row.add(wish.getTitle());
        row.add(wish.getDescription());
        row.add(String.valueOf(wish.getCostPoints()));
        row.add(String.valueOf(wish.getMinLevel()));
        row.add(wish.getType().name());
        row.add(wish.getStatus().name());
        row.add(wish.getRequestedByChildId());
        row.add(wish.getApprovedByUserId() != null ? wish.getApprovedByUserId() : "");
        return row;
    }

    /**
     * Parses a string, returns null if empty.
     *
     * @param value String value to parse
     * @return String value or null if empty
     */
    private String parseString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}

