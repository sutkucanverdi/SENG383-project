package models;

import enums.UserRole;

import java.util.Objects;
import java.util.UUID;

public abstract class User {
    private final String id;
    private String name;
    private final UserRole role;

    protected User(String name, UserRole role) {
        this(UUID.randomUUID().toString(), name, role);
    }

    protected User(String id, String name, UserRole role) {
        this.id = id == null ? UUID.randomUUID().toString() : Objects.requireNonNull(id, "id");
        setName(name);
        this.role = Objects.requireNonNull(role, "role");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }
}

