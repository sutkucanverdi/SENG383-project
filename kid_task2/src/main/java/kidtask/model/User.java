package kidtask.model;

import java.util.Objects;
import java.util.UUID;

/**
 * User entity representing a user in the KidTask system.
 * Base class for Child, Parent, and Teacher.
 */
public class User {
    private String id;
    private String name;
    private Role role;

    /**
     * Constructor for creating a new user with auto-generated ID.
     *
     * @param name User's name
     * @param role User's role
     */
    public User(String name, Role role) {
        this(UUID.randomUUID().toString(), name, role);
    }

    /**
     * Constructor for creating a user with existing ID.
     *
     * @param id   User ID (UUID)
     * @param name User's name
     * @param role User's role
     */
    public User(String id, String name, Role role) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}

