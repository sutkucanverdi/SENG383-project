package kidtask.persistence;

import kidtask.model.Child;
import kidtask.model.Role;
import kidtask.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for User entities, handles loading and saving users from/to file storage.
 */
public class UserRepository {
    private static final String FILE_NAME = "Users.txt";
    private static final String HEADER = "id|name|role|points|level|ratingSum|ratingCount";

    private final FileStorage fileStorage;

    public UserRepository() {
        this.fileStorage = new FileStorage(FILE_NAME);
    }

    /**
     * Loads all users from the storage file.
     *
     * @return List of all users
     * @throws StorageException if file operations fail
     */
    public List<User> loadAll() throws StorageException {
        List<List<String>> rows = fileStorage.readAll(HEADER);
        List<User> users = new ArrayList<>();

        for (List<String> row : rows) {
            try {
                User user = parseUser(row);
                users.add(user);
            } catch (Exception e) {
                // Skip invalid rows, log error
                System.err.println("Skipping invalid user row: " + row + " - " + e.getMessage());
            }
        }

        return users;
    }

    /**
     * Saves all users to the storage file.
     *
     * @param users List of users to save
     * @throws StorageException if file operations fail
     */
    public void saveAll(List<User> users) throws StorageException {
        List<List<String>> rows = new ArrayList<>();

        for (User user : users) {
            List<String> row = formatUser(user);
            rows.add(row);
        }

        fileStorage.writeAll(HEADER, rows);
    }

    /**
     * Generates a new unique ID for a user.
     * Note: In a real application, this might check for duplicates.
     *
     * @return New UUID string
     */
    public String nextId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Parses a row of data into a User object.
     * Creates Child instance if role is CHILD.
     *
     * @param row List of field values
     * @return User object (or Child if role is CHILD)
     */
    private User parseUser(List<String> row) {
        if (row.size() < 7) {
            throw new IllegalArgumentException("Invalid user row: insufficient fields");
        }

        String id = row.get(0);
        String name = row.get(1);
        Role role = Role.valueOf(row.get(2).toUpperCase());

        if (role == Role.CHILD) {
            int points = Integer.parseInt(row.get(3));
            int level = Integer.parseInt(row.get(4));
            double ratingSum = Double.parseDouble(row.get(5));
            int ratingCount = Integer.parseInt(row.get(6));
            return new Child(id, name, points, level, ratingSum, ratingCount);
        } else {
            return new User(id, name, role);
        }
    }

    /**
     * Formats a User object into a row of data.
     *
     * @param user User object
     * @return List of field values
     */
    private List<String> formatUser(User user) {
        List<String> row = new ArrayList<>();
        row.add(user.getId());
        row.add(user.getName());
        row.add(user.getRole().name());

        if (user instanceof Child child) {
            row.add(String.valueOf(child.getPoints()));
            row.add(String.valueOf(child.getLevel()));
            row.add(String.valueOf(child.getRatingSum()));
            row.add(String.valueOf(child.getRatingCount()));
        } else {
            row.add("0");  // points
            row.add("0");  // level
            row.add("0.0"); // ratingSum
            row.add("0");  // ratingCount
        }
        return row;
    }
}

